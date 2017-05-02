package link.ideas.easya;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import link.ideas.easya.adapter.LessonFriendsAdapter;
import link.ideas.easya.models.Lesson;
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

    ValueEventListener mValueEventLessonListener;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mLessonDatabaseReference;

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
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
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
                startActivity(intent);
            }
        });
        mRecyclerView.setAdapter(mLessonFriendsAdapter);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mLessonDatabaseReference = mFirebaseDatabase.getReference().
                child(Constants.FIREBASE_LOCATION_USERS_LESSONS).child(coursePushId);
        if (isDeviceOnline()) {
            attachDatabaseReadListener();
        }else {
            deviceOffline();
        }


    }

    private void attachDatabaseReadListener() {

        progress.setVisibility(View.VISIBLE);
        mValueEventLessonListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
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
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(LOG_TAG, databaseError + "");

            }
        };
        mLessonDatabaseReference.addValueEventListener(mValueEventLessonListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        detachDatabaseReadListener();

    }

    private void detachDatabaseReadListener() {
        if (mValueEventLessonListener != null) {
            mLessonDatabaseReference.removeEventListener(mValueEventLessonListener);
            mValueEventLessonListener = null;

        }
    }

}
