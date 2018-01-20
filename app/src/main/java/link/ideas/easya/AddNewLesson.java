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
import link.ideas.easya.utils.ImageSaver;
import me.relex.circleindicator.CircleIndicator;

import static link.ideas.easya.fragment.CourseListFragment.LOG_TAG;

public class AddNewLesson extends BaseActivity implements ApplyFragment.Callback,
        LinkFragment.Callback, SummaryFragment.Callback {
    private static final String LOG_TAG = AddNewLesson.class.getSimpleName();

    ViewPager viewPager;
    CircleIndicator indicator;
    CardView mCardView;

    String courseId;
    ViewPagerAdapter adapter;

    private FirebaseDatabase mFirebaseDatabase;

    private DatabaseReference mLessonDatabaseReference;
    private DatabaseReference mLessonDetailDatabaseReference;

    private FirebaseStorage mFirebaseStorage;
    private StorageReference mUserImagesReferenceSummary, mUserImagesReferenceLink, mUserImagesReferenceApp;

    Bitmap outlineImage = null, imageLink = null, imageApp = null;
    String title, summary, links, debug, appTitle, appSummary;
    String lessonPushId, coursePushId;
    Uri appUrl, linkUrl, summaryUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_lesson);
        setDrawer(false);


        viewPager = (ViewPager) findViewById(R.id.viewPager);
        indicator = (CircleIndicator) findViewById(R.id.indicator);


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
    public void onSavedClicked(String lessonAppTitle, String lessonApp, Bitmap applyImage) {
        imageApp = applyImage;
        appTitle = lessonAppTitle;
        appSummary = lessonApp;

        Log.e(LOG_TAG, appTitle + " " + appSummary);
    }

    @Override
    public void onSavedClickedSummary(String lessonTitle, String lessonSummary, Bitmap summaryImage) {
        outlineImage = summaryImage;
        title = lessonTitle;
        summary = lessonSummary;
        Log.e(LOG_TAG, title + " " + summary);
    }

    @Override
    public void onSavedClickedLink(String lessonLink, String lessonDebug, Bitmap linkImage) {
        imageLink = linkImage;
        links = lessonLink;
        debug = lessonDebug;
        Log.e(LOG_TAG, links + " " + debug);
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

        SummaryFragment summaryFragment = (SummaryFragment)
                adapter.getItem(0);

        if (summaryFragment.validateSummary()) {
            summaryFragment.getSummerData();
            LinkFragment linkFragment = (LinkFragment)
                    adapter.getItem(1);
            linkFragment.getLinkData();
            ApplyFragment applyFragment = (ApplyFragment)
                    adapter.getItem(2);
            applyFragment.getApplyData();
        } else {
            viewPager.setCurrentItem(0);
        }
    }


    /**
     * Helper method to handle insertion of a new location in the weather database.
     *
     * @param courseId           .
     * @param lessonLink         .
     * @param lessonDebugs       .
     * @param lessonLifeAppTitle .
     * @param lessonLifeApp      .
     */
    void addLessonData(String courseId, String lessonName, String lessonOutline,
                       String lessonLink, String lessonLifeAppTitle,
                       String lessonLifeApp, String lessonDebugs) {


        Intent intent = new Intent(this, LessonList.class)
                .setData(CourseContract.SubjectEntry.buildSubjectWithID(courseId));
        startActivity(intent);
        finish();

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

