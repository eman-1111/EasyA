package link.ideas.easya.ui.friend_lesson_detail;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;

import link.ideas.easya.R;
import link.ideas.easya.data.database.Lesson;
import link.ideas.easya.ui.BaseActivity;
import link.ideas.easya.utils.Constants;
import link.ideas.easya.utils.InjectorUtils;

public class LessonDetailFriend extends BaseActivity {

    TextView mLessonLink, mLessonDebug, mLessonPracticalTitle, mLessonPractical,
            mLessonOutline, mLink, mDebug;

    CollapsingToolbarLayout collapsingToolbar;
    ImageView outlineImage, linkImage, appImage;
    LinearLayout progress;
    FriendLessonDetailViewModel viewModel;

    String coursePushId, lessonPushId;


    Lesson lesson, lessonDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_detail_friend);
        setDrawer(false);

        Intent intent = getIntent();
        coursePushId = intent.getStringExtra(Constants.PREF_COURSE_PUSH_ID);
        lessonPushId = intent.getStringExtra(Constants.PREF_LESSON_PUSH_ID);
        lesson = (Lesson) intent.getParcelableExtra(Constants.PREF_LESSON_OBJECT);
        Log.e("data", coursePushId + " " + lessonPushId + " " + lesson.getLessonTitle());
        initializeScreen();

    }

    private void initializeScreen() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        mLessonLink = (TextView) findViewById(R.id.lesson_linkx_content_tv);
        mLink = (TextView) findViewById(R.id.lesson_linkx_d_tv);
        mLessonPracticalTitle = (TextView) findViewById(R.id.lesson_app_title_d_tv);
        mLessonPractical = (TextView) findViewById(R.id.lesson_app_content_tv);
        mLessonDebug = (TextView) findViewById(R.id.lesson_debug_content_tv);
        mDebug = (TextView) findViewById(R.id.lesson_debugx_title_tv);
        mLessonOutline = (TextView) findViewById(R.id.lesson_overview_content_tv);

        linkImage = (ImageView) findViewById(R.id.link_iv);
        appImage = (ImageView) findViewById(R.id.app_iv);
        outlineImage = (ImageView) findViewById(R.id.outlook_iv);

        linkImage.setContentDescription(getResources().getString(R.string.a11y_link_image));
        appImage.setContentDescription(getResources().getString(R.string.a11y_outline_image));
        outlineImage.setContentDescription(getResources().getString(R.string.a11y_app_image));

        progress = (LinearLayout) findViewById(R.id.lin_Progress);

        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);

        FriendLessonDetailFactory factory = InjectorUtils.provideFreindLessonDetailViewModelFactory(coursePushId, lessonPushId);

        viewModel = ViewModelProviders.of(this,factory).get(FriendLessonDetailViewModel.class);


        if (isDeviceOnline()) {
            attachDatabaseReadListener();
        } else {
            Snackbar.make(coordinatorLayout, getResources().getString(R.string.network),
                    Snackbar.LENGTH_LONG).show();
        }

    }

    private void attachDatabaseReadListener() {

        progress.setVisibility(View.VISIBLE);
        LiveData<DataSnapshot> liveData = viewModel.getDataSnapshotLiveData();

        liveData.observe(this, new Observer<DataSnapshot>() {
            @Override
            public void onChanged(@Nullable DataSnapshot dataSnapshot) {
                lessonDetail = dataSnapshot.getValue(Lesson.class);

                progress.setVisibility(View.GONE);
                setUpView();
            }
        });
    }

    private void setUpView() {
        String lessonName = lesson.getLessonTitle();
        collapsingToolbar.setTitle(lessonName);
        mLessonLink.setText(lesson.getLessonLink());

        mLessonDebug.setText(lessonDetail.getLessonDebug());
        mLessonPracticalTitle.setText(lessonDetail.getLessonPracticalTitle());
        mLessonPractical.setText(lessonDetail.getLessonPractical());
        mLessonOutline.setText(lessonDetail.getLessonSummary());

        String outlineImageUrl = lesson.getLessonImage();

        Glide.with(LessonDetailFriend.this).load(outlineImageUrl)
                .placeholder(R.drawable.summary).dontAnimate()
                .into(outlineImage);

        String linkImageUrl = lessonDetail.getLinkImage();
        if (!linkImageUrl.equals("null")) {
            Glide.with(LessonDetailFriend.this).load(linkImageUrl)
                    .placeholder(R.drawable.placeholder).dontAnimate()
                    .into(linkImage);
        }
        String appImageUrl = lessonDetail.getAppImage();
        if (!appImageUrl.equals("null")) {
            Glide.with(LessonDetailFriend.this).load(appImageUrl)
                    .placeholder(R.drawable.placeholder).dontAnimate()
                    .into(appImage);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.home) {
//            Intent homeIntent = new Intent(LessonDetailFriend.this, LessonListFriends.class);
//            homeIntent.putExtra(Constants.PREF_COURSE_PUSH_ID, coursePushId);
//            startActivity(homeIntent);
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
