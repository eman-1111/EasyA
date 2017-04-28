package link.ideas.easya;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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

import link.ideas.easya.adapter.UserFriendsAdapter;
import link.ideas.easya.models.Course;
import link.ideas.easya.models.User;
import link.ideas.easya.utils.Constants;
import link.ideas.easya.utils.Helper;

public class FriendsList extends BaseActivity {

    RecyclerView mRecyclerViewFriends;
    LinearLayout progress;
    UserFriendsAdapter mUserFriendsAdapter;
    TextView empty_list;
    ArrayList<User> userList;
    ArrayList<String> friendsEmails;

    ValueEventListener mChildEventFriendsListener;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mUsersFriendsDatabaseReference;


    private static final String LOG_TAG = FriendsList.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);
        setDrawer(true);
        setUpAPIs();
        loadNavHeader(getResources().getString(R.string.your_friends_list));
        setUpNavigationView();
        initializeScreen();
    }


    /**
     * Link layout elements from XML and setup the toolbar
     */
    public void initializeScreen() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        empty_list = (TextView) findViewById(R.id.error_tv);

        userList = new ArrayList<User>();
        friendsEmails = new ArrayList<String>();

        mRecyclerViewFriends = (RecyclerView) findViewById(R.id.recyclerview_friend);
        progress = (LinearLayout) findViewById(R.id.lin_Progress);
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mUsersFriendsDatabaseReference = mFirebaseDatabase.getReference().
                child(Constants.FIREBASE_LOCATION_USER_FRIENDS).child(Helper.encodeEmail(accountName));

        mRecyclerViewFriends.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerViewFriends.setHasFixedSize(true);
        mUserFriendsAdapter = new UserFriendsAdapter(userList, friendsEmails, this, new UserFriendsAdapter.UserFriendsAdapterOnClickHolder() {
            @Override
            public void onClick(String friendEmail, UserFriendsAdapter.UserFriendsAdapterViewHolder vh) {
                Intent intent = new Intent(FriendsList.this, CourseListFriends.class);
                intent.putExtra(Constants.PREF_FRIEND_ACCOUNT ,friendEmail);
                startActivity(intent);
            }
        });
        mRecyclerViewFriends.setAdapter(mUserFriendsAdapter);
        if (isDeviceOnline()) {
            progress.setVisibility(View.VISIBLE);
            attachDatabaseReadListener();
        }
    }

    private void attachDatabaseReadListener() {

        // What I will do with this data iget from firebase
        mChildEventFriendsListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                    User user = childDataSnapshot.getValue(User.class);
                    userList.add(user);
                    friendsEmails.add(childDataSnapshot.getKey());
                }
                mUserFriendsAdapter.notifyDataSetChanged();
                if (userList.size() == 0) {
                    empty_list.setVisibility(View.VISIBLE);
                } else {
                    empty_list.setVisibility(View.GONE);
                }
                progress.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("DatabaseError", databaseError + "");

            }
        };

        //what cild I am listening to
        mUsersFriendsDatabaseReference.addValueEventListener(mChildEventFriendsListener);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        detachDatabaseReadListener();

    }

    private void detachDatabaseReadListener() {
        if (mChildEventFriendsListener != null) {
            mUsersFriendsDatabaseReference.removeEventListener(mChildEventFriendsListener);
            mChildEventFriendsListener = null;

        }
    }

    /**
     * Launch AddFriendActivity to find and add user to current user's friends list
     * when the button AddFriend is pressed
     */
    public void onAddFriendPressed(View view) {
        Intent intent = new Intent(FriendsList.this, AddFriendActivity.class);
        startActivity(intent);
    }
}
