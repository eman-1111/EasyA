package link.ideas.easya.fragment;

import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import link.ideas.easya.R;
import link.ideas.easya.data.CourseContract;
import link.ideas.easya.factory.LessonDetailModelFactory;
import link.ideas.easya.models.Course;
import link.ideas.easya.data.database.Lesson;
import link.ideas.easya.models.LessonDetail;
import link.ideas.easya.utils.Constants;
import link.ideas.easya.utils.Helper;
import link.ideas.easya.utils.ImageSaver;
import link.ideas.easya.utils.InjectorUtils;
import link.ideas.easya.viewmodel.LessonDetailViewModel;

import static android.content.Context.MODE_PRIVATE;

/**
 * A placeholder fragment containing a simple view.
 */
public class LessonDetailFragment extends Fragment {


    private static final String LOG_TAG = LessonDetailFragment.class.getSimpleName();

    Menu menu;
    MenuItem shareItem = null;

    boolean isOnLesson = true;
    TextView mLessonLink, mLessonDebug, mLessonPracticalTitle, mLessonPractical,
            mLessonOutline, mLink, mDebug;

    CoordinatorLayout coordinatorLayout;
    CollapsingToolbarLayout collapsingToolbar;
    ProgressDialog mProgressDialog;

    ImageView outlineImage, linkImage, appImage;
    String accountName;
    String teacherEmail, teacherPhoto, courseName, teacherName, lessonName, lessonOutline, lessonLink,
            lessonDebug, lessonPracticalTitle, lessonPractical;
    int courseId;

    Bitmap outlineImageBit = null, linkImageBit = null, appImageBit = null;
    Uri appUrl, linkUrl, summaryUrl;
    int courserColor = 0, favorite = 0;
    String coursePushId, lessonPushId;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mCoursDatabaseReference;
    private DatabaseReference mLessonDatabaseReference;
    private DatabaseReference mLessonDetailDatabaseReference;

    private FirebaseStorage mFirebaseStorage;
    private StorageReference mUserImagesReferenceSummary, mUserImagesReferenceLink, mUserImagesReferenceApp;


    String lessonNames;
    int lessonId;
    LessonDetailViewModel mViewModel;


    public LessonDetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            lessonId = arguments.getInt(Constants.PREF_LESSON_ID);
            lessonNames = arguments.getString(Constants.PREF_LESSON_NAME);

        }

        View rootView = inflater.inflate(R.layout.fragment_subject_detail, container, false);

        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        collapsingToolbar =
                (CollapsingToolbarLayout) rootView.findViewById(R.id.collapsing_toolbar);
        setUpIds(rootView);
        LessonDetailModelFactory factory = InjectorUtils.
                provideLessonDetailViewModelFactory(getActivity(), lessonId);
        mViewModel = ViewModelProviders.of(this, factory)
                .get(LessonDetailViewModel.class);
        mViewModel.getUserLesson().observe(this, new Observer<Lesson>() {
            @Override
            public void onChanged(@Nullable Lesson lesson) {
                setUpValues(lesson);
            }
        });

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
        //todo see if shared on firebase
        shareItem.setIcon(getResources().getDrawable(R.drawable.ic_share_blue_24dp));

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
            startActivity(intent);
        } else if (id == R.id.action_favorite) {

            MenuItem favItem = menu.findItem(R.id.action_favorite);
            if (favorite == 0) {
                favItem.setIcon(getResources().getDrawable(R.drawable.ic_favorite_white_24dp));

            } else {
                favItem.setIcon(getResources().getDrawable(R.drawable.ic_favorite_border_white_24dp));

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
        //todo start share action
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
        super.onActivityCreated(savedInstanceState);
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

    private void setUpValues(Lesson lesson) {
        courseId = lesson.getCourseId();
        lessonName = lesson.getLessonTitle();
        collapsingToolbar.setTitle(lessonName);
        lessonLink = lesson.getLessonLink();
        mLessonLink.setText(lessonLink);
        if (mLessonLink.equals("")) {
            mLink.setText("");
        }

        lessonDebug = lesson.getLessonDebug();
        if (lessonDebug.equals("")) {
            mDebug.setText("");
        }
        mLessonDebug.setText(lessonDebug);

        lessonPracticalTitle = lesson.getLessonPracticalTitle();
        mLessonPracticalTitle.setText(lessonPracticalTitle);

        lessonPractical = lesson.getLessonPractical();
        mLessonPractical.setText(lessonPractical);

        lessonOutline = lesson.getLessonSummary();
        mLessonOutline.setText(lessonOutline);

        teacherEmail = "";//data.getString(COL_TEACHER_EMAIL);
        teacherPhoto = "";// data.getString(COL_TEACHER_PHOTO);
        courseName = "";// data.getString(COL_COURSE_NAME);
        courserColor = 0; //data.getInt(COL_TEACHER_COLOR);
        teacherName = "";// data.getString(COL_TEACHER_NAME);


        outlineImageBit = new ImageSaver(getActivity()).
                setFileName(lessonName + Constants.LESSON_SUMMARY).
                setDirectoryName(Constants.APP_NAME).
                load();
        outlineImage.setImageBitmap(outlineImageBit);


        linkImageBit = new ImageSaver(getActivity()).
                setFileName(lessonName + Constants.LESSON_LINK).
                setDirectoryName(Constants.APP_NAME).
                load();
        linkImage.setImageBitmap(linkImageBit);

        appImageBit = new ImageSaver(getActivity()).
                setFileName(lessonName + Constants.LESSON_APP).
                setDirectoryName(Constants.APP_NAME).
                load();
        appImage.setImageBitmap(appImageBit);


        favorite = Integer.parseInt(lesson.getFavoriteLesson());

        AppCompatActivity activity = (AppCompatActivity) getActivity();

        if (shareItem != null) {
            //todo see the firebase push id
            shareItem.setIcon(getResources().getDrawable(R.drawable.ic_share_blue_24dp));

        }

    }


    //firebase share

    private void createShareUserLesson() {
        //todo see if course is save a firebase key they and start share
        setUpFireBase();
//        if (mCursor.getString(COL_FIREBASE_COURSE_ID) == null) {
//            addCourseToFirebase();
//        } else {
//            if (mCursor.getString(COL_FIREBASE_LESSON_ID) == null) {
//                coursePushId = mCursor.getString(COL_FIREBASE_COURSE_ID);
//                addLessonToFirebase();
//            }
//        }

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

//        Lesson lesson = new Lesson(lessonName, lessonLink, summaryUrl + "", userName, true,
//                Helper.getTimestampCreated(), Helper.getTimestampLastChanged());
        lessonPushId = mLessonDatabaseReference.push().getKey();

        mLessonDatabaseReference.child(lessonPushId).setValue(null);
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
        //todo save courseId to the database
    }

    private void addFirebaseLessonId(String pushId) {
        ContentValues firebasePushId = new ContentValues();
        firebasePushId.put(CourseContract.SubjectEntry.COLUMN_FIREBASE_ID, pushId);
        //todo save lessonId to the database


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
