package link.ideas.easya.ui.lesson_detail;

import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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


import butterknife.BindView;
import butterknife.ButterKnife;
import link.ideas.easya.data.database.Course;
import link.ideas.easya.ui.add_lesson.AddNewLesson;
import link.ideas.easya.R;
import link.ideas.easya.data.database.Lesson;
import link.ideas.easya.utils.Constants;
import link.ideas.easya.utils.Helper;
import link.ideas.easya.utils.ImageSaver;
import link.ideas.easya.utils.InjectorUtils;

import static android.content.Context.MODE_PRIVATE;

/**
 * A placeholder fragment containing a simple view.
 */
public class LessonDetailFragment extends Fragment {


    private static final String LOG_TAG = LessonDetailFragment.class.getSimpleName();

    Menu menu;
    MenuItem shareItem = null;

    boolean isOnLesson = true;

    @BindView(R.id.lesson_linkx_content_tv)
    TextView mLessonLink;
    @BindView(R.id.lesson_debug_content_tv)
    TextView mLessonDebug;
    @BindView(R.id.lesson_app_title_d_tv)
    TextView mLessonPracticalTitle;
    @BindView(R.id.lesson_app_content_tv)
    TextView mLessonPractical;
    @BindView(R.id.lesson_overview_content_tv)
    TextView mLessonOutline;
    @BindView(R.id.lesson_linkx_d_tv)
    TextView mLink;
    @BindView(R.id.lesson_debugx_title_tv)
    TextView mDebug;

    @BindView(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbar;

    @BindView(R.id.outlook_iv)
    ImageView outlineImage;
    @BindView(R.id.link_iv)
    ImageView linkImage;
    @BindView(R.id.app_iv)
    ImageView appImage;

    @BindView(R.id.toolbar)
    Toolbar toolbar;


    String teacherEmail, teacherPhoto, courseName, teacherName, lessonName, lessonOutline, lessonLink,
            lessonDebug, lessonPracticalTitle, lessonPractical;
    int courseId;

    Bitmap outlineImageBit = null, linkImageBit = null, appImageBit = null;
    int courserColor = 0, favorite = 0;
    String coursePushId, lessonPushId;


    String lessonNames;
    int lessonId;
    LessonDetailViewModel mViewModel;
    LessonDetailPresenterContract mActionsListener;

    Lesson shareLesson;
    Course shareCourse;

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
        ButterKnife.bind(this, rootView);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        LessonDetailModelFactory factory = InjectorUtils.
                provideLessonDetailViewModelFactory(getActivity(), lessonId);
        mViewModel = ViewModelProviders.of(this, factory)
                .get(LessonDetailViewModel.class);

        mActionsListener = new LessonDetailPresenter(mViewModel);
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
            intent.putExtra(Constants.PREF_COURSE_PUSH_ID,coursePushId);
            intent.putExtra(Constants.PREF_LESSON,shareLesson );
            intent.putExtra(Constants.PREF_COURSE_ID, courseId);
            startActivity(intent);
        } else if (id == R.id.action_favorite) {

            MenuItem favItem = menu.findItem(R.id.action_favorite);

            if (favorite == 0) {
                mViewModel.updateFavorite("1");
                favItem.setIcon(getResources().getDrawable(R.drawable.ic_favorite_white_24dp));

            } else {
                mViewModel.updateFavorite("0");
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
        String accountName = prefs.getString(Constants.PREF_ACCOUNT_NAME, null);
        String userName = prefs.getString(Constants.PREF_ACCOUNT_USER_NAME, null);
        if (accountName != null) {
            if (lessonPushId.equals("")) {
                shareItem.setEnabled(false);
                shareItem.setCheckable(false);
                shareItem.setIcon(getResources().getDrawable(R.drawable.ic_share_yellow_24dp));
                //    showProgressDialog();
                Snackbar.make(coordinatorLayout, getResources().getString(R.string.lesson_uploading),
                        Snackbar.LENGTH_LONG).show();

                mActionsListener.shareUserLesson(shareCourse, shareLesson, accountName, userName
                        , outlineImageBit, linkImageBit, appImageBit);
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



    private void setUpValues(Lesson lesson) {
        courseId = lesson.getCourseId();
        shareLesson = lesson;
        mViewModel.getCourse(courseId).observe(this, new Observer<Course>() {
            @Override
            public void onChanged(@Nullable Course course) {
                shareCourse = course;
                coursePushId = course.getFirebaseId();
                teacherEmail = course.getTeacherEmail();
                teacherPhoto = course.getTeacherPhotoURL();
                courseName = course.getCourseName();
                courserColor = course.getTeacherColor();
                teacherName = course.getTeacherName();
            }
        });

        lessonPushId = lesson.getFirebaseId();
        lessonName = lesson.getLessonTitle();
        collapsingToolbar.setTitle(lessonName);
        lessonLink = lesson.getLessonLink();

        mLessonLink.setText(lessonLink);
        lessonDebug = lesson.getLessonDebug();
        if (lessonDebug.equals("")) {
            mDebug.setText("");
        }
        if (mLessonLink.equals("")) {
            mLink.setText("");
        }
        mLessonDebug.setText(lessonDebug);

        lessonPracticalTitle = lesson.getLessonPracticalTitle();
        mLessonPracticalTitle.setText(lessonPracticalTitle);

        lessonPractical = lesson.getLessonPractical();
        mLessonPractical.setText(lessonPractical);

        lessonOutline = lesson.getLessonSummary();
        mLessonOutline.setText(lessonOutline);


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


        if (shareItem != null) {
            if (!lessonPushId.equals(""))
                shareItem.setIcon(getResources().getDrawable(R.drawable.ic_share_blue_24dp));

        }

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


//    public void showProgressDialog() {
//        if (mProgressDialog == null) {
//            mProgressDialog = new ProgressDialog(getActivity());
//            mProgressDialog.setMessage(getString(R.string.lesson_uploading));
//            mProgressDialog.setIndeterminate(true);
//            mProgressDialog.setCancelable(false);
//            mProgressDialog.setCanceledOnTouchOutside(false);
//        }
//
//        mProgressDialog.show();
//    }
//
//    public void hideProgressDialog() {
//        if (mProgressDialog != null && mProgressDialog.isShowing()) {
//            mProgressDialog.dismiss();
//        }
//    }
}
