package link.ideas.easya;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import link.ideas.easya.adapter.CourseFriendAdapter;
import link.ideas.easya.models.Course;
import link.ideas.easya.utils.Constants;
import link.ideas.easya.utils.Helper;

public class CourseListFriends extends BaseActivity {

    public static final String LOG_TAG = CourseListFriends.class.getSimpleName();
    TextView emptyView;
    RecyclerView mRecyclerView;
    LinearLayout progress;

    private CourseFriendAdapter mCourseFriendAdapter;
    String friendAccount, friendName;
    ArrayList<Course> friendsCourse;
    ArrayList<String> coursePushIds;

    ValueEventListener mValueEventCourseListener;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mCourseDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        friendAccount = intent.getStringExtra(Constants.PREF_FRIEND_ACCOUNT);
        friendName = intent.getStringExtra(Constants.PREF_FRIEND_ACCOUNT_NAME);

        setContentView(R.layout.courses_list_friend);
        setDrawer(true);
        setUpAPIs();
        loadNavHeader(Helper.getFristName(friendName) + getResources().getString(R.string.friend_course)  );
        setUpNavigationView();

        initializeScreen();

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
                intent.putExtra(Constants.PREF_FRIEND_ACCOUNT_NAME ,friendName);
                startActivity(intent);
            }
        });
        mRecyclerView.setAdapter(mCourseFriendAdapter);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mCourseDatabaseReference = mFirebaseDatabase.getReference().
                child(Constants.FIREBASE_LOCATION_USERS_COURSES).child(friendAccount);
        if (isDeviceOnline()) {
            attachDatabaseReadListener();
        }else {
            deviceOffline();
        }


    }

    private void attachDatabaseReadListener() {

        progress.setVisibility(View.VISIBLE);
        mValueEventCourseListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
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
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("DatabaseError", databaseError + "");

            }
        };
        mCourseDatabaseReference.addValueEventListener(mValueEventCourseListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        detachDatabaseReadListener();

    }

    private void detachDatabaseReadListener() {
        if (mValueEventCourseListener != null) {
            mCourseDatabaseReference.removeEventListener(mValueEventCourseListener);
            mValueEventCourseListener = null;

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return true;
    }

}
