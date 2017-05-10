package link.ideas.easya.fragment;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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

import link.ideas.easya.AddNewLesson;
import link.ideas.easya.LessonList;
import link.ideas.easya.R;
import link.ideas.easya.data.CourseContract;
import link.ideas.easya.models.Course;
import link.ideas.easya.models.Lesson;
import link.ideas.easya.models.LessonDetail;
import link.ideas.easya.utils.Constants;
import link.ideas.easya.utils.Helper;

import static android.content.Context.MODE_PRIVATE;

/**
 * A placeholder fragment containing a simple view.
 */
public class LessonDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {


    private static final String LOG_TAG = LessonDetailFragment.class.getSimpleName();
    public static final String DETAIL_URI = "URI";

    Menu menu;
    MenuItem shareItem = null;

    boolean isOnLesson = true;
    TextView mLessonLink, mLessonDebug, mLessonPracticalTitle, mLessonPractical,
            mLessonOutline, mLink, mDebug;

    CoordinatorLayout coordinatorLayout;

    public ProgressDialog mProgressDialog;

    ImageView outlineImage, linkImage, appImage;
    String accountName;
    String teacherEmail, teacherPhoto, courseName, teacherName, lessonName, lessonOutline, lessonLink,
            lessonDebug, lessonPracticalTitle, lessonPractical, courseId;

    Bitmap outlineImageBit = null, linkImageBit = null, appImageBit = null;
    Uri appUrl, linkUrl, summaryUrl;
    int courserColor = 0, favorite = 0;
    Cursor mCursor;
    String coursePushId, lessonPushId;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mCoursDatabaseReference;
    private DatabaseReference mLessonDatabaseReference;
    private DatabaseReference mLessonDetailDatabaseReference;

    private FirebaseStorage mFirebaseStorage;
    private StorageReference mUserImagesReferenceSummary, mUserImagesReferenceLink, mUserImagesReferenceApp;


    private Uri mUri;
    private static final int DETAIL_LOADER = 0;

    private static final String[] DETAIL_COLUMNS = {
            CourseContract.SubjectEntry.COLUMN_COURSE_ID,
            CourseContract.SubjectEntry.COLUMN_LESSON_TITLE,
            CourseContract.SubjectEntry.COLUMN_LESSON_LINK,
            CourseContract.SubjectEntry.COLUMN_LESSON_PRACTICAL_TITLE,
            CourseContract.SubjectEntry.COLUMN_LESSON_PRACTICAL,
            CourseContract.SubjectEntry.COLUMN_LESSON_OUTLINE,
            CourseContract.SubjectEntry.COLUMN_LESSON_DEBUG,
            CourseContract.SubjectEntry.COLUMN_FAVORITE,
            CourseContract.SubjectEntry.COLUMN_LESSON_OUTLINE_IMAGE,
            CourseContract.SubjectEntry.COLUMN_LESSON_LINK_IMAGE,
            CourseContract.SubjectEntry.COLUMN_LESSON_PRACTICAL_IMAGE,
            CourseContract.CourseEntry.COLUMN_COURSE_NAME,
            CourseContract.CourseEntry.COLUMN_TEACHER_NAME,
            CourseContract.CourseEntry.COLUMN_TEACHER_COLOR,
            CourseContract.CourseEntry.COLUMN_TEACHER_PHOTO_URL,
            CourseContract.CourseEntry.COLUMN_TEACHER_EMAIL,
            CourseContract.CourseEntry.COLUMN_FIREBASE_ID,
            CourseContract.SubjectEntry.COLUMN_FIREBASE_ID};


    public static final int COL_COURSE_ID = 0;
    public static final int COL_LESSON_TITLE = 1;
    public static final int COL_LESSON_LINK = 2;
    public static final int COL_LESSON_PRACTICAL_TITLE = 3;
    public static final int COL_LESSON_PRACTICAL = 4;
    public static final int COL_LESSON_OUTLINE = 5;
    public static final int COL_LESSON_DEBUG = 6;
    public static final int COL_FAVORITE = 7;
    public static final int COL_LESSON_OUTLINE_IMAGE = 8;
    public static final int COL_LESSON_LINK_IMAGE = 9;
    public static final int COL_LESSON_PRACTICAL_IMAGE = 10;

    public static final int COL_COURSE_NAME = 11;
    public static final int COL_TEACHER_NAME = 12;
    public static final int COL_TEACHER_COLOR = 13;
    public static final int COL_TEACHER_PHOTO = 14;
    public static final int COL_TEACHER_EMAIL = 15;

    public static final int COL_FIREBASE_COURSE_ID = 16;
    public static final int COL_FIREBASE_LESSON_ID = 17;

    CollapsingToolbarLayout collapsingToolbar;


    public LessonDetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(LessonDetailFragment.DETAIL_URI);

        }

        View rootView = inflater.inflate(R.layout.fragment_subject_detail, container, false);

        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        collapsingToolbar =
                (CollapsingToolbarLayout) rootView.findViewById(R.id.collapsing_toolbar);
        setUpIds(rootView);
        return rootView;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.menu = menu;
        inflater.inflate(R.menu.menu_subject_detail, menu);
        if (favorite == 1) {
            MenuItem favItem = menu.findItem(R.id.action_favorite);
            favItem.setIcon(getResources().getDrawable(R.drawable.ic_favorite_white_24dp));
        }
        shareItem = menu.findItem(R.id.action_share);
        if (mCursor != null) {
            if (mCursor.getString(COL_FIREBASE_LESSON_ID) != null) {
                shareItem.setIcon(getResources().getDrawable(R.drawable.ic_share_blue_24dp));
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.home) {
            getActivity().onBackPressed();
            return true;
        } else if (id == R.id.action_edit) {
            Intent intent = new Intent(getActivity(), AddNewLesson.class);
            intent.putExtra(Constants.PREF_LESSON_URL, mUri.toString());
            intent.putExtra(Constants.PREF_COURSE_ID, CourseContract.SubjectEntry.getSubjectIdFromUri(mUri));
            startActivity(intent);
        } else if (id == R.id.action_favorite) {

            MenuItem favItem = menu.findItem(R.id.action_favorite);
            if (favorite == 0) {
                favItem.setIcon(getResources().getDrawable(R.drawable.ic_favorite_white_24dp));
                ContentValues favorite = new ContentValues();
                favorite.put(CourseContract.SubjectEntry.COLUMN_FAVORITE, "1");
                getActivity().getContentResolver().update(CourseContract.SubjectEntry.buildSubjectsUri(),
                        favorite,
                        CourseContract.SubjectEntry.TABLE_NAME +
                                "." + CourseContract.SubjectEntry.COLUMN_LESSON_TITLE + " = ? ",
                        new String[]{CourseContract.SubjectEntry.getSubjectTitleFromUri(mUri)});
            } else {
                favItem.setIcon(getResources().getDrawable(R.drawable.ic_favorite_border_white_24dp));
                ContentValues favorite = new ContentValues();
                favorite.put(CourseContract.SubjectEntry.COLUMN_FAVORITE, "0");

                getActivity().getContentResolver().update(CourseContract.SubjectEntry.buildSubjectsUri(),
                        favorite,
                        CourseContract.SubjectEntry.TABLE_NAME +
                                "." + CourseContract.SubjectEntry.COLUMN_LESSON_TITLE + " = ? ",
                        new String[]{CourseContract.SubjectEntry.getSubjectTitleFromUri(mUri)});
            }

        } else if (id == R.id.action_share) {
            if (isDeviceOnline()) {
                startSharing();
            } else {
                Snackbar.make(coordinatorLayout, getResources().getString(R.string.network),
                        Snackbar.LENGTH_LONG).show();
            }

        }
        return super.onOptionsItemSelected(item);
    }

    private void startSharing() {
        String lessonPushId = mCursor.getString(COL_FIREBASE_LESSON_ID);
        SharedPreferences prefs = getActivity().getSharedPreferences(Constants.PREF_USER_DATA, MODE_PRIVATE);
        accountName = prefs.getString(Constants.PREF_ACCOUNT_NAME, null);
        if (accountName != null) {
            if (lessonPushId == null) {
                shareItem.setEnabled(false);
                shareItem.setCheckable(false);
                shareItem.setIcon(getResources().getDrawable(R.drawable.ic_share_yellow_24dp));
                showProgressDialog();
                Snackbar.make(coordinatorLayout, getResources().getString(R.string.lesson_uploading),
                        Snackbar.LENGTH_LONG).show();
                createShareUserLesson();
            } else {
                Helper.startDialog(getActivity(), "",
                        getResources().getString(R.string.shared_lesson_warning));
            }
        } else {
            Helper.startDialog(getActivity(), getResources().getString(R.string.login_title_warning),
                    getResources().getString(R.string.login_share_warning));
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        if (null != mUri) {
            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            return new CursorLoader(
                    getActivity(),
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
            courseId = data.getString(COL_COURSE_ID);
            lessonName = data.getString(COL_LESSON_TITLE);
            collapsingToolbar.setTitle(lessonName);
            lessonLink = data.getString(COL_LESSON_LINK);
            mLessonLink.setText(lessonLink);
            if (mLessonLink.equals("")) {
                mLink.setText("");
            }

            lessonDebug = data.getString(COL_LESSON_DEBUG);
            if (lessonDebug.equals("")) {
                mDebug.setText("");
            }
            mLessonDebug.setText(lessonDebug);

            lessonPracticalTitle = data.getString(COL_LESSON_PRACTICAL_TITLE);
            mLessonPracticalTitle.setText(lessonPracticalTitle);

            lessonPractical = data.getString(COL_LESSON_PRACTICAL);
            mLessonPractical.setText(lessonPractical);

            lessonOutline = data.getString(COL_LESSON_OUTLINE);
            mLessonOutline.setText(lessonOutline);

            teacherEmail = data.getString(COL_TEACHER_EMAIL);
            teacherPhoto = data.getString(COL_TEACHER_PHOTO);
            courseName = data.getString(COL_COURSE_NAME);
            courserColor = data.getInt(COL_TEACHER_COLOR);
            teacherName = data.getString(COL_TEACHER_NAME);

            byte[] outlineImageB = data.getBlob(COL_LESSON_OUTLINE_IMAGE);
            byte[] linkImageB = data.getBlob(COL_LESSON_LINK_IMAGE);
            byte[] appImageB = data.getBlob(COL_LESSON_PRACTICAL_IMAGE);
            if (outlineImageB != null) {
                outlineImageBit = Helper.getImage(outlineImageB);
                outlineImage.setImageBitmap(outlineImageBit);
            }
            if (linkImageB != null) {
                linkImageBit = Helper.getImage(linkImageB);
                linkImage.setImageBitmap(linkImageBit);
            }
            if (appImageB != null) {
                appImageBit = Helper.getImage(appImageB);
                appImage.setImageBitmap(appImageBit);
            }


            favorite = Integer.parseInt(data.getString(COL_FAVORITE));

            AppCompatActivity activity = (AppCompatActivity) getActivity();

            if (shareItem != null) {
                if (mCursor.getString(COL_FIREBASE_LESSON_ID) != null) {
                    shareItem.setIcon(getResources().getDrawable(R.drawable.ic_share_blue_24dp));
                }
            }

        }
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


    private void setUpIds(View rootView) {
        mLessonLink = (TextView) rootView.findViewById(R.id.lesson_linkx_content_tv);
        mLink = (TextView) rootView.findViewById(R.id.lesson_linkx_d_tv);
        mLessonPracticalTitle = (TextView) rootView.findViewById(R.id.lesson_app_title_d_tv);
        mLessonPractical = (TextView) rootView.findViewById(R.id.lesson_app_content_tv);
        mLessonDebug = (TextView) rootView.findViewById(R.id.lesson_debug_content_tv);
        mDebug = (TextView) rootView.findViewById(R.id.lesson_debugx_title_tv);
        mLessonOutline = (TextView) rootView.findViewById(R.id.lesson_overview_content_tv);

        linkImage = (ImageView) rootView.findViewById(R.id.link_iv);
        appImage = (ImageView) rootView.findViewById(R.id.app_iv);
        outlineImage = (ImageView) rootView.findViewById(R.id.outlook_iv);

        coordinatorLayout = (CoordinatorLayout) rootView.findViewById(R.id.coordinator_layout);

    }


    //firebase share

    private void createShareUserLesson() {

        setUpFireBase();
        if (mCursor.getString(COL_FIREBASE_COURSE_ID) == null) {
            addCourseToFirebase();
        } else {
            if (mCursor.getString(COL_FIREBASE_LESSON_ID) == null) {
                coursePushId = mCursor.getString(COL_FIREBASE_COURSE_ID);
                addLessonToFirebase();
            }
        }

    }

    private void setUpFireBase() {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();

        mCoursDatabaseReference = mFirebaseDatabase.getReference().child(Constants.FIREBASE_LOCATION_USERS_COURSES).
                child(Helper.encodeEmail(accountName)).child(Constants.FIREBASE_LOCATION_USER_COURSES);
        mLessonDetailDatabaseReference = mFirebaseDatabase.getReference().child(Constants.FIREBASE_LOCATION_USERS_LESSONS_DETAIL);

    }

    private void addCourseToFirebase() {

        Course course = new Course(courseName, teacherName, teacherEmail, teacherPhoto, courserColor,
                Helper.getTimestampCreated(), Helper.getTimestampLastChanged());

        coursePushId = mCoursDatabaseReference.push().getKey();
        mCoursDatabaseReference.child(coursePushId).setValue(course);
        mCoursDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                addFirebaseCourseId(coursePushId);
                addLessonToFirebase();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void addLessonToFirebase() {

        mUserImagesReferenceSummary = mFirebaseStorage.getReference()
                .child(coursePushId + "/" + lessonName + "/summary.jpg");
        mUserImagesReferenceLink = mFirebaseStorage.getReference()
                .child(coursePushId + "/" + lessonName + "/link.jpg");
        mUserImagesReferenceApp = mFirebaseStorage.getReference()
                .child(coursePushId + "/" + lessonName + "/app.jpg");

        if (outlineImageBit != null) {
            addImageToFirebase(outlineImageBit);
        } else {
            addLessonLinkToFirebase();
        }

    }

    private void addLessonLinkToFirebase() {

        mLessonDatabaseReference = mFirebaseDatabase.getReference().
                child(Constants.FIREBASE_LOCATION_USERS_LESSONS).child(coursePushId);

        SharedPreferences prefs = getActivity().getSharedPreferences(Constants.PREF_USER_DATA, MODE_PRIVATE);
        String userName = prefs.getString(Constants.PREF_ACCOUNT_USER_NAME, null);

        Lesson lesson = new Lesson(lessonName, lessonLink, summaryUrl + "", userName, true,
                Helper.getTimestampCreated(), Helper.getTimestampLastChanged());
        lessonPushId = mLessonDatabaseReference.push().getKey();

        mLessonDatabaseReference.child(lessonPushId).setValue(lesson);
        mLessonDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                addFirebaseLessonId(lessonPushId);
                addLessonDetailToFirebase();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void addLessonDetailToFirebase() {

        if (linkImageBit != null) {
            addImageToFirebaseLink(linkImageBit);
        } else if (appImageBit != null) {
            addImageToFirebaseApp(appImageBit);
        } else {
            addLessonDetailsToFirebase();
        }


    }

    private void addLessonDetailsToFirebase() {
        LessonDetail lessonDetail = new LessonDetail(lessonOutline, linkUrl + "",
                lessonPracticalTitle, lessonPractical, appUrl + "", lessonDebug,
                Helper.getTimestampCreated(), Helper.getTimestampLastChanged());
        mLessonDetailDatabaseReference.child(coursePushId).child(lessonPushId).setValue(lessonDetail);
        if (isOnLesson) {
            shareItem.setIcon(getResources().getDrawable(R.drawable.ic_share_blue_24dp));
            shareItem.setEnabled(true);
            shareItem.setCheckable(true);
            Snackbar.make(coordinatorLayout, getResources().getString(R.string.lesson_added),
                    Snackbar.LENGTH_LONG).show();
            hideProgressDialog();
        }
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
                if (appImageBit != null) {
                    addImageToFirebaseApp(appImageBit);
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

    private void addFirebaseCourseId(String pushId) {
        ContentValues firebasePushId = new ContentValues();
        firebasePushId.put(CourseContract.CourseEntry.COLUMN_FIREBASE_ID, pushId);
        getActivity().getContentResolver().update(CourseContract.CourseEntry.buildCoursesUri(),
                firebasePushId,
                CourseContract.CourseEntry.TABLE_NAME +
                        "." + CourseContract.CourseEntry.COLUMN_COURSE_ID + " = ? ",
                new String[]{CourseContract.CourseEntry.getCourseIdFromUri(mUri)});
        getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
    }

    private void addFirebaseLessonId(String pushId) {
        ContentValues firebasePushId = new ContentValues();
        firebasePushId.put(CourseContract.SubjectEntry.COLUMN_FIREBASE_ID, pushId);
        getActivity().getContentResolver().update(CourseContract.SubjectEntry.buildSubjectsUri(),
                firebasePushId,
                CourseContract.SubjectEntry.TABLE_NAME +
                        "." + CourseContract.SubjectEntry.COLUMN_LESSON_TITLE + " = ? ",
                new String[]{CourseContract.SubjectEntry.getSubjectTitleFromUri(mUri)});
        getLoaderManager().restartLoader(DETAIL_LOADER, null, this);

    }

    public boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    @Override
    public void onPause() {
        super.onPause();
        isOnLesson = false;
    }


    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage(getString(R.string.lesson_uploading));
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setCanceledOnTouchOutside(false);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}
