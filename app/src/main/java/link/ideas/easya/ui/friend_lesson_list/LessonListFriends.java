package link.ideas.easya.ui.friend_lesson_list;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.List;

import link.ideas.easya.data.database.Lesson;
import link.ideas.easya.models.User;
import link.ideas.easya.ui.friend_couse_list.CourseListFriends;
import link.ideas.easya.ui.friend_lesson_detail.LessonDetailFriend;
import link.ideas.easya.R;
import link.ideas.easya.ui.BaseActivity;
import link.ideas.easya.utils.CircleTransform;
import link.ideas.easya.utils.Constants;
import link.ideas.easya.utils.Helper;

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

    /**
     * Created by Eman on 4/23/2017.
     */

    public static class UserFriendsAdapter extends RecyclerView.Adapter<UserFriendsAdapter.UserFriendsAdapterViewHolder> {


        final private Context mContext;
        final private UserFriendsAdapterOnClickHolder mClickHolder;
        private List<User> userList;
        private List<String> email;
        private  String userEmail;

        public class UserFriendsAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            public final TextView friendEmail;
            public final TextView friendName;
            public final ImageView friendImage;


            public UserFriendsAdapterViewHolder(View view) {
                super(view);
                friendEmail = (TextView) view.findViewById(R.id.tv_friend_email);
                friendName = (TextView) view.findViewById(R.id.tv_friend_name);
                friendImage = (ImageView) view.findViewById(R.id.iv_friend_image);
                view.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                int adapterPostion = getAdapterPosition();
                String name = userList.get(adapterPostion).getName();
                String friendEmail = email.get(adapterPostion);

                mClickHolder.onClick(friendEmail, name,this);
            }

        }

        public UserFriendsAdapter(List<User> userList, List<String> email, String userEmail,
                                  Context context, UserFriendsAdapterOnClickHolder dh) {
            mContext = context;
            mClickHolder = dh;
            this.userList = userList;
            this.email = email;
            this.userEmail = userEmail;

        }

        public static interface UserFriendsAdapterOnClickHolder {
            void onClick(String friendEmail, String friendName, UserFriendsAdapterViewHolder vh);
        }

        @Override
        public UserFriendsAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            if (parent instanceof RecyclerView) {


                View view = LayoutInflater.from(parent.getContext()).
                        inflate(R.layout.list_item_friend, parent, false);

                view.setFocusable(false);
                return new UserFriendsAdapterViewHolder(view);
            } else {
                throw new RuntimeException("Not bind th RecyclerView");
            }

        }

        @Override
        public void onBindViewHolder(final UserFriendsAdapterViewHolder holder, int position) {
            final User user = userList.get(position);
           final String friendEmail = email.get(position);

            String email = Helper.decodeEmail(friendEmail);
            holder.friendEmail.setText(email);
            holder.friendEmail.setContentDescription(mContext.getString(R.string.a11y_email_button, email));

            String name = user.getName();
            holder.friendName.setText(name);
            holder.friendName.setContentDescription(mContext.getString(R.string.a11y_name_button, name));

            Glide.with(mContext).load(user.getPhotoUrl())
                    .transform(new CircleTransform(mContext))
                    .error(R.drawable.ic_account_circle_black_24dp)
                    .into(holder.friendImage);


        }

        @Override
        public int getItemCount() {

            return userList.size();
        }

    }
}
