package link.ideas.easya;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

import link.ideas.easya.adapter.LessonFriendsAdapter;
import link.ideas.easya.models.Lesson;
import link.ideas.easya.utils.Constants;
import link.ideas.easya.utils.Helper;
import link.ideas.easya.viewmodel.FriendLessonListViewModel;

public class LessonListFriends extends BaseActivity {

    public static final String LOG_TAG = CourseListFriends.class.getSimpleName();
    TextView emptyView;
    RecyclerView mRecyclerView;
    LinearLayout progress;

    private LessonFriendsAdapter mLessonFriendsAdapter;
    String coursePushId;
    ArrayList<Lesson> friendsLesson;
    ArrayList<String> lessonPushIds;
    FriendLessonListViewModel viewModel;
    boolean isLoaded;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        coursePushId = intent.getStringExtra(Constants.PREF_COURSE_PUSH_ID);
        String friendName = intent.getStringExtra(Constants.PREF_FRIEND_ACCOUNT_NAME);
        setContentView(R.layout.activity_subject_list_friends);
        setDrawer(true);
        setUpAPIs();
        loadNavHeader(Helper.getFristName(friendName) +getResources().getString(R.string.friend_lesson)  );
        setUpNavigationView();

        initializeScreen();

    }

    private void initializeScreen() {
        friendsLesson = new ArrayList<Lesson>();
        lessonPushIds = new ArrayList<String>();

        progress = (LinearLayout) findViewById(R.id.lin_Progress);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_lesson);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mRecyclerView.setHasFixedSize(true);
        emptyView = (TextView) findViewById(R.id.empty_tv);
        mLessonFriendsAdapter = new LessonFriendsAdapter(friendsLesson, lessonPushIds, this, new LessonFriendsAdapter.CourseAdapterFriendsOnClickHolder() {
            @Override
            public void onClick(String lessonPushId, Lesson lesson,LessonFriendsAdapter.CourseAdapterFriendsViewHolder vh) {
                Intent intent = new Intent(LessonListFriends.this, LessonDetailFriend.class);
                intent.putExtra(Constants.PREF_COURSE_PUSH_ID ,coursePushId);
                intent.putExtra(Constants.PREF_LESSON_PUSH_ID ,lessonPushId);
                intent.putExtra(Constants.PREF_LESSON_OBJECT, lesson);
                ActivityOptionsCompat activityOptions =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(LessonListFriends.this,
                                new Pair<View, String>(vh.lessonImage, getString(R.string.shared_element)));
                ActivityCompat.startActivity(LessonListFriends.this, intent, activityOptions.toBundle());
                startActivity(intent);

            }
        });
        mRecyclerView.setAdapter(mLessonFriendsAdapter);


        viewModel = ViewModelProviders.of(this).get(FriendLessonListViewModel.class);
        viewModel.setCoursePushId(coursePushId);

        if (isDeviceOnline()) {
            attachDatabaseReadListener();
        }else {
            deviceOffline();
        }


    }

    private void attachDatabaseReadListener() {

        progress.setVisibility(View.VISIBLE);

        LiveData<DataSnapshot> liveData = viewModel.getDataSnapshotLiveData();

        liveData.observe(this, new Observer<DataSnapshot>() {
                    @Override
                    public void onChanged(@Nullable DataSnapshot dataSnapshot) {
                        for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                            Lesson lesson = childDataSnapshot.getValue(Lesson.class);
                            friendsLesson.add(lesson);
                            lessonPushIds.add(childDataSnapshot.getKey());
                        }
                        mLessonFriendsAdapter.notifyDataSetChanged();
                        if (friendsLesson.size() == 0) {
                            emptyView.setVisibility(View.VISIBLE);
                        } else {
                            emptyView.setVisibility(View.GONE);
                        }
                        progress.setVisibility(View.GONE);
                        startIntroAnimation();
                        isLoaded = true;
                    }
                });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private void startIntroAnimation() {
        mRecyclerView.setTranslationY( getResources().getDimensionPixelSize(R.dimen.list_item_lesson));
        mRecyclerView.setAlpha(0f);
        mRecyclerView.animate()
                .translationY(0)
                .setDuration(500)
                .alpha(1f)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isLoaded)
            startIntroAnimation();
    }
}
