package link.ideas.easya;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import link.ideas.easya.data.CourseContract;
import link.ideas.easya.fragment.LinkFragment;
import link.ideas.easya.fragment.SummaryFragment;
import link.ideas.easya.fragment.ApplyFragment;
import link.ideas.easya.interfacee.SaveLesson;
import link.ideas.easya.models.*;
import link.ideas.easya.utils.Constants;
import link.ideas.easya.utils.Helper;
import me.relex.circleindicator.CircleIndicator;

import static link.ideas.easya.fragment.CourseListFragment.LOG_TAG;

public class AddNewLesson extends BaseActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    ViewPager viewPager;
    CircleIndicator indicator;
    CardView mCardView;

    String courseId;
    ViewPagerAdapter adapter;
    boolean edit = false;
    private Uri mUri;
    Cursor mCursor = null;
    private static final int DETAIL_LOADER = 0;
    String oldLessonName = "";

    private FirebaseDatabase mFirebaseDatabase;

    private DatabaseReference mLessonDatabaseReference;
    private DatabaseReference mLessonDetailDatabaseReference;

    private FirebaseStorage mFirebaseStorage;
    private StorageReference mUserImagesReferenceSummary, mUserImagesReferenceLink, mUserImagesReferenceApp;

    Bitmap outlineImage = null, imageLink = null, imageApp = null;
    String title, summary, links, debug, appTitle, appSummary;
    String lessonPushId, coursePushId;
    Uri appUrl, linkUrl, summaryUrl;


    private static final String[] DETAIL_COLUMNS = {
            CourseContract.SubjectEntry.COLUMN_COURSE_ID,
            CourseContract.SubjectEntry.COLUMN_LESSON_TITLE,
            CourseContract.SubjectEntry.COLUMN_LESSON_LINK,
            CourseContract.SubjectEntry.COLUMN_LESSON_PRACTICAL_TITLE,
            CourseContract.SubjectEntry.COLUMN_LESSON_PRACTICAL,
            CourseContract.SubjectEntry.COLUMN_LESSON_OUTLINE,
            CourseContract.SubjectEntry.COLUMN_LESSON_DEBUG,
            CourseContract.CourseEntry.COLUMN_COURSE_NAME,
            CourseContract.CourseEntry.COLUMN_TEACHER_NAME,
            CourseContract.SubjectEntry.COLUMN_FAVORITE,
            CourseContract.SubjectEntry.COLUMN_LESSON_OUTLINE_IMAGE,
            CourseContract.SubjectEntry.COLUMN_LESSON_LINK_IMAGE,
            CourseContract.SubjectEntry.COLUMN_LESSON_PRACTICAL_IMAGE,
            CourseContract.CourseEntry.COLUMN_FIREBASE_ID,
            CourseContract.SubjectEntry.COLUMN_FIREBASE_ID};


    public static final int COL_COURSE_ID = 0;
    public static final int COL_LESSON_TITLE = 1;
    public static final int COL_LESSON_LINK = 2;
    public static final int COL_LESSON_PRACTICAL_TITLE = 3;
    public static final int COL_LESSON_PRACTICAL = 4;
    public static final int COL_LESSON_OUTLINE = 5;
    public static final int COL_LESSON_DEBUG = 6;

    public static final int COL_COURSE_NAME = 7;
    public static final int COL_TEACHER_NAME = 8;
    public static final int COL_FAVORITE = 9;

    public static final int COL_LESSON_OUTLINE_IMAGE = 10;
    public static final int COL_LESSON_LINK_IMAGE = 11;
    public static final int COL_LESSON_PRACTICAL_IMAGE = 12;

    public static final int COL_FIREBASE_COURSE_ID = 13;
    public static final int COL_FIREBASE_LESSON_ID = 14;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_lesson);
        setDrawer(false);


        viewPager = (ViewPager) findViewById(R.id.viewPager);
        indicator = (CircleIndicator) findViewById(R.id.indicator);

        Intent intent = getIntent();
        courseId = intent.getStringExtra(Constants.PREF_COURSE_ID);
        if (intent.getStringExtra(Constants.PREF_LESSON_URL) != null) {
            edit = true;
            mUri = Uri.parse(intent.getStringExtra(Constants.PREF_LESSON_URL));
            getSupportLoaderManager().initLoader(DETAIL_LOADER, null, this);

        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mCardView = (CardView) findViewById(R.id.cv_mainContainer);

        setupViewPager(viewPager);
        viewPager.setOffscreenPageLimit(2);
        indicator.setViewPager(viewPager);

    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(this.getSupportFragmentManager());

        adapter.addFragment(new SummaryFragment());
        adapter.addFragment(new LinkFragment());
        adapter.addFragment(new ApplyFragment());

        viewPager.setAdapter(adapter);

    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (null != mUri) {
            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            return new CursorLoader(
                    this,
                    mUri,
                    DETAIL_COLUMNS,
                    null,
                    null,
                    null
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            mCursor = data;
            ladEditData();
        }
    }

    private void ladEditData() {
        SummaryFragment.lessonNameET.setText(mCursor.getString(COL_LESSON_TITLE));
        SummaryFragment.lessonOverViewET.setText(mCursor.getString(COL_LESSON_OUTLINE));
        oldLessonName = mCursor.getString(COL_LESSON_TITLE);

        LinkFragment.lessonLink.setText(mCursor.getString(COL_LESSON_LINK));
        LinkFragment.lessonDebug.setText(mCursor.getString(COL_LESSON_DEBUG));

        ApplyFragment.lessonAppTitle = mCursor.getString(COL_LESSON_PRACTICAL_TITLE);
        ApplyFragment.lessonApp = mCursor.getString(COL_LESSON_PRACTICAL);

        byte[] outlineImageB = mCursor.getBlob(COL_LESSON_OUTLINE_IMAGE);
        byte[] linkImageB = mCursor.getBlob(COL_LESSON_LINK_IMAGE);
        byte[] appImageB = mCursor.getBlob(COL_LESSON_PRACTICAL_IMAGE);
        if (outlineImageB != null) {
            SummaryFragment.thumbnail = Helper.getImage(outlineImageB);
            SummaryFragment.outlineImage.setImageBitmap(Helper.getImage(outlineImageB));
        }
        if (linkImageB != null) {
            LinkFragment.imageLink.setImageBitmap(Helper.getImage(linkImageB));
            LinkFragment.thumbnail = Helper.getImage(linkImageB);
        }
        if (appImageB != null) {
            ApplyFragment.thumbnail = Helper.getImage(appImageB);
        }
        //
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment) {
            mFragmentList.add(fragment);
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            SummaryFragment.lessonNameET.addTextChangedListener(new AddNewLesson.MyTextWatcher(SummaryFragment.lessonNameET));
            SummaryFragment.lessonOverViewET.addTextChangedListener(new AddNewLesson.MyTextWatcher(SummaryFragment.lessonOverViewET));

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, LessonList.class)
                    .setData(CourseContract.SubjectEntry.buildSubjectWithID(courseId));
            startActivity(intent);
            finish();
        } else if (item.getItemId() == R.id.btn_save_menu) {

            startSaveLesson();


        }
        return super.onOptionsItemSelected(item);
    }

    private void startSaveLesson() {
        if (validateName() && validateOutline()) {
            title = SummaryFragment.lessonNameET.getText().toString();
            summary = SummaryFragment.lessonOverViewET.getText().toString();

            links = LinkFragment.lessonLink.getText().toString();
            debug = LinkFragment.lessonDebug.getText().toString();

            appTitle = ApplyFragment.lessonLifeAppTitle.getText().toString();
            appSummary = ApplyFragment.lessonLifeApp.getText().toString();


            outlineImage = SummaryFragment.thumbnail;
            imageLink = LinkFragment.thumbnail;
            imageApp = ApplyFragment.thumbnail;


            byte[] outlineImage_, imageLink_, imageApp_;
            if (outlineImage != null) {
                outlineImage_ = Helper.getBytes(outlineImage);
            } else {
                outlineImage_ = null;
            }
            if (imageLink != null) {
                imageLink_ = Helper.getBytes(imageLink);
            } else {
                imageLink_ = null;
            }
            if (imageApp != null) {
                imageApp_ = Helper.getBytes(imageApp);
            } else {
                imageApp_ = null;
            }

            addLessonData(courseId, title, summary, outlineImage_,
                    links, imageLink_, appTitle,
                    appSummary, imageApp_, debug);
        }
    }

    public class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.lesson_name_et:
                    validateName();
                    break;
                case R.id.lesson_outline_et:
                    validateOutline();
                    break;

            }
        }
    }

    /**
     * Helper method to handle insertion of a new location in the weather database.
     *
     * @param courseId           .
     * @param lessonLink         .
     * @param lessonLinkImage    .
     * @param lessonDebugs       .
     * @param lessonLifeAppTitle .
     * @param lessonLifeApp      .
     * @param lessonAppImage
     */
    void addLessonData(String courseId, String lessonName, String lessonOutline, byte[] lessonOutlineImage,
                       String lessonLink, byte[] lessonLinkImage, String lessonLifeAppTitle,
                       String lessonLifeApp, byte[] lessonAppImage, String lessonDebugs) {

        ContentValues courseValues = new ContentValues();

        courseValues.put(CourseContract.SubjectEntry.COLUMN_COURSE_ID, courseId);
        courseValues.put(CourseContract.SubjectEntry.COLUMN_LESSON_TITLE, lessonName);
        courseValues.put(CourseContract.SubjectEntry.COLUMN_LESSON_OUTLINE, lessonOutline);
        courseValues.put(CourseContract.SubjectEntry.COLUMN_LESSON_OUTLINE_IMAGE, lessonOutlineImage);
        courseValues.put(CourseContract.SubjectEntry.COLUMN_LESSON_LINK, lessonLink);
        courseValues.put(CourseContract.SubjectEntry.COLUMN_LESSON_LINK_IMAGE, lessonLinkImage);
        courseValues.put(CourseContract.SubjectEntry.COLUMN_LESSON_PRACTICAL_TITLE, lessonLifeAppTitle);
        courseValues.put(CourseContract.SubjectEntry.COLUMN_LESSON_PRACTICAL, lessonLifeApp);
        courseValues.put(CourseContract.SubjectEntry.COLUMN_LESSON_PRACTICAL_IMAGE, lessonAppImage);
        courseValues.put(CourseContract.SubjectEntry.COLUMN_LESSON_DEBUG, lessonDebugs);


        if (edit) {

            //  int lessonId =
            // Log.e(LOG_TAG, "mURI: "+ mUri);
            this.getContentResolver().update(CourseContract.SubjectEntry.buildSubjectsUri(),
                    courseValues,
                    CourseContract.SubjectEntry.TABLE_NAME +
                            "." + CourseContract.SubjectEntry.COLUMN_LESSON_TITLE + " = ? ",
                    new String[]{oldLessonName});
            if (mCursor.getString(COL_FIREBASE_LESSON_ID) != null) {
                if (isDeviceOnline()) {
                    setUpFireBase();
                } else {
                    Snackbar.make(mCardView, getResources().getString(R.string.network_edit),
                            Snackbar.LENGTH_LONG).show();
                }
            }

        } else {
            courseValues.put(CourseContract.SubjectEntry.COLUMN_FAVORITE, "0");
            //  Log.e("Key Subject", userKey);

            this.getContentResolver().insert(
                    CourseContract.SubjectEntry.CONTENT_URI,
                    courseValues);
        }

        clearSavedData();
        Intent intent = new Intent(this, LessonList.class)
                .setData(CourseContract.SubjectEntry.buildSubjectWithID(courseId));
        startActivity(intent);
        finish();

    }


    private void clearSavedData() {
        ApplyFragment.lessonAppTitle = "";
        ApplyFragment.lessonApp = "";


        SummaryFragment.thumbnail = null;
        LinkFragment.thumbnail = null;
        ApplyFragment.thumbnail = null;
    }

    private boolean validateName() {
        if (SummaryFragment.lessonNameET.getText().toString().trim().isEmpty()) {
            SummaryFragment.inputLayoutName.setError(getResources().getString(R.string.lesson_name_error));
            requestFocus(SummaryFragment.lessonNameET);
            viewPager.setCurrentItem(0);
            return false;
        } else {
            SummaryFragment.inputLayoutName.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateOutline() {
        if (SummaryFragment.lessonOverViewET.getText().toString().trim().isEmpty()) {
            SummaryFragment.inputLayoutOutline.setError(getResources().getString(R.string.summary_error));
            requestFocus(SummaryFragment.lessonOverViewET);
            viewPager.setCurrentItem(0);
            return false;
        } else {
            SummaryFragment.inputLayoutOutline.setErrorEnabled(false);
        }

        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }


    private void setUpFireBase() {

        coursePushId = mCursor.getString(COL_FIREBASE_COURSE_ID);
        lessonPushId = mCursor.getString(COL_FIREBASE_LESSON_ID);


        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();

        mLessonDetailDatabaseReference = mFirebaseDatabase.getReference().child(Constants.FIREBASE_LOCATION_USERS_LESSONS_DETAIL);

        mUserImagesReferenceSummary = mFirebaseStorage.getReference()
                .child(coursePushId + "/" + title + "/summary.jpg");
        mUserImagesReferenceLink = mFirebaseStorage.getReference()
                .child(coursePushId + "/" + title + "/link.jpg");
        mUserImagesReferenceApp = mFirebaseStorage.getReference()
                .child(coursePushId + "/" + title + "/app.jpg");

        addLessonToFirebase();
    }

    private void addLessonToFirebase() {
        if (outlineImage != null) {
            addImageToFirebase(outlineImage);
        } else {
            addLessonLinkToFirebase();
        }

    }

    private void addLessonLinkToFirebase() {

        mLessonDatabaseReference = mFirebaseDatabase.getReference().
                child(Constants.FIREBASE_LOCATION_USERS_LESSONS).child(coursePushId);

        SharedPreferences prefs = getSharedPreferences(Constants.PREF_USER_DATA, MODE_PRIVATE);
        String userName = prefs.getString(Constants.PREF_ACCOUNT_USER_NAME, null);

        Lesson lesson = new Lesson(title, links, summaryUrl + "", userName, true,
                Helper.getTimestampCreated(), Helper.getTimestampLastChanged());
        lessonPushId = mLessonDatabaseReference.child(lessonPushId).getKey();

        mLessonDatabaseReference.child(lessonPushId).setValue(lesson);
        mLessonDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                addLessonDetailToFirebase();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void addLessonDetailToFirebase() {

        if (imageLink != null) {
            addImageToFirebaseLink(imageLink);
        } else if (imageApp != null) {
            addImageToFirebaseApp(imageApp);
        } else {
            addLessonDetailsToFirebase();
        }


    }

    private void addLessonDetailsToFirebase() {
        link.ideas.easya.models.LessonDetail lessonDetail = new link.ideas.easya.models.LessonDetail(summary, linkUrl + "",
                appTitle, appSummary, appUrl + "", debug,
                Helper.getTimestampCreated(), Helper.getTimestampLastChanged());
        mLessonDetailDatabaseReference.child(coursePushId).child(lessonPushId).setValue(lessonDetail);

        Snackbar.make(mCardView, getResources().getString(R.string.lesson_edited),
                Snackbar.LENGTH_LONG).show();
    }

    private void addImageToFirebase(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = mUserImagesReferenceSummary.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                summaryUrl = taskSnapshot.getDownloadUrl();
                addLessonLinkToFirebase();

            }
        });
    }


    private void addImageToFirebaseLink(Bitmap bitmap) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = mUserImagesReferenceLink.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                linkUrl = taskSnapshot.getDownloadUrl();
                if (imageApp != null) {
                    addImageToFirebaseApp(imageApp);
                } else {
                    addLessonDetailsToFirebase();
                }
            }
        });
    }

    private void addImageToFirebaseApp(Bitmap bitmap) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = mUserImagesReferenceApp.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                appUrl = taskSnapshot.getDownloadUrl();
                addLessonDetailsToFirebase();
            }
        });
    }
}

