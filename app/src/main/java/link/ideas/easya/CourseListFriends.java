package link.ideas.easya;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

import link.ideas.easya.adapter.CourseFriendAdapter;
import link.ideas.easya.models.Course;
import link.ideas.easya.utils.Constants;
import link.ideas.easya.utils.Helper;
import link.ideas.easya.viewmodel.FriendCourseListViewModel;
import link.ideas.easya.viewmodel.StudyingViewModel;

public class CourseListFriends extends BaseActivity {

    public static final String LOG_TAG = CourseListFriends.class.getSimpleName();
    TextView emptyView;
    RecyclerView mRecyclerView;
    LinearLayout progress;

    private CourseFriendAdapter mCourseFriendAdapter;
    StudyingViewModel studyingViewModel;
    String friendAccount, friendName;
    ArrayList<Course> friendsCourse;
    ArrayList<String> coursePushIds;
    FriendCourseListViewModel courseViewModel;


    boolean isLoaded, canEdit;
    MenuItem changeStatus = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        friendAccount = intent.getStringExtra(Constants.PREF_FRIEND_ACCOUNT);
        friendName = intent.getStringExtra(Constants.PREF_FRIEND_ACCOUNT_NAME);
        canEdit = intent.getBooleanExtra(Constants.PREF_FRIEND_CAN_EDIT, false);

        setContentView(R.layout.courses_list_friend);
        setDrawer(true);
        setUpAPIs();
        loadNavHeader(Helper.getFristName(friendName) + getResources().getString(R.string.friend_course));
        setUpNavigationView();

        initializeScreen();
        FirebaseCrash.report(new Exception("My first Android non-fatal error"));
    }

    private void initializeScreen() {
        friendsCourse = new ArrayList<Course>();
        coursePushIds = new ArrayList<String>();

        progress = (LinearLayout) findViewById(R.id.lin_Progress);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mRecyclerView.setHasFixedSize(true);
        emptyView = (TextView) findViewById(R.id.empty_tv);
        mCourseFriendAdapter = new CourseFriendAdapter(friendsCourse, coursePushIds, this, new CourseFriendAdapter.CourseAdapterFriendsOnClickHolder() {
            @Override
            public void onClick(String coursePushId, CourseFriendAdapter.CourseAdapterFriendsViewHolder vh) {
                Intent intent = new Intent(CourseListFriends.this, LessonListFriends.class);
                intent.putExtra(Constants.PREF_COURSE_PUSH_ID, coursePushId);
                intent.putExtra(Constants.PREF_FRIEND_ACCOUNT_NAME, friendName);
                startActivity(intent);
            }
        });
        mRecyclerView.setAdapter(mCourseFriendAdapter);

        courseViewModel = ViewModelProviders.of(this).get(FriendCourseListViewModel.class);
        courseViewModel.setAccountName(friendAccount);

        studyingViewModel = ViewModelProviders.of(this).get(StudyingViewModel.class);
        studyingViewModel.setAccountName(friendAccount);

        if (isDeviceOnline()) {
            attachDatabaseReadListener();
        } else {
            deviceOffline();
        }


    }

    private void attachDatabaseReadListener() {

        progress.setVisibility(View.VISIBLE);

        LiveData<DataSnapshot> liveDataCourse = courseViewModel.getDataSnapshotLiveData();

        liveDataCourse.observe(this, new Observer<DataSnapshot>() {
                    @Override
                    public void onChanged(@Nullable DataSnapshot dataSnapshot) {
                        for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                            Course course = childDataSnapshot.getValue(Course.class);
                            friendsCourse.add(course);
                            coursePushIds.add(childDataSnapshot.getKey());
                        }
                        mCourseFriendAdapter.notifyDataSetChanged();
                        if (friendsCourse.size() == 0) {
                            emptyView.setVisibility(View.VISIBLE);
                        } else {
                            emptyView.setVisibility(View.GONE);
                        }
                        progress.setVisibility(View.GONE);
                        isLoaded = true;
                        startIntroAnimation();
                    }
                });


        LiveData<DataSnapshot> liveDataStudying = courseViewModel.getDataSnapshotLiveData();

        liveDataStudying.observe(this, new Observer<DataSnapshot>() {
            @Override
            public void onChanged(@Nullable DataSnapshot dataSnapshot) {
                Log.e("data00" ,dataSnapshot.getKey());
//                boolean isStudying = (boolean) dataSnapshot.getValue();
//                changeStudyingStatus(isStudying);
            }
        });


    }

    private void changeStudyingStatus(boolean isStudying) {
        if (changeStatus != null) {
            if (isStudying) {
                changeStatus.setIcon(getResources().getDrawable(R.drawable.studying));
            } else {
                changeStatus.setIcon(getResources().getDrawable(R.drawable.not_studying));
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.studying_status, menu);
        changeStatus = menu.findItem(R.id.btn_studying_menu);
        return true;
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (isLoaded)
            startIntroAnimation();
    }

    private void startIntroAnimation() {
        mRecyclerView.setTranslationY(getResources().getDimensionPixelSize(R.dimen.list_item_lesson));
        mRecyclerView.setAlpha(0f);
        mRecyclerView.animate()
                .translationY(0)
                .setDuration(500)
                .alpha(1f)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();
    }
}
