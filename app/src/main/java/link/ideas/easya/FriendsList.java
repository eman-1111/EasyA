package link.ideas.easya;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
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
import link.ideas.easya.viewmodel.FriendsListViewModel;

public class FriendsList extends BaseActivity {

    RecyclerView mRecyclerViewFriends;
    LinearLayout progress;
    UserFriendsAdapter mUserFriendsAdapter;
    TextView empty_list;
    ArrayList<User> userList;
    ArrayList<String> friendsEmails;

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


        mRecyclerViewFriends.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerViewFriends.setHasFixedSize(true);
        mUserFriendsAdapter = new UserFriendsAdapter(userList, friendsEmails, Helper.encodeEmail(accountName),this, new UserFriendsAdapter.UserFriendsAdapterOnClickHolder() {
            @Override
            public void onClick(String friendEmail, String name, UserFriendsAdapter.UserFriendsAdapterViewHolder vh) {
                Intent intent = new Intent(FriendsList.this, CourseListFriends.class);
                intent.putExtra(Constants.PREF_FRIEND_ACCOUNT, friendEmail);
                intent.putExtra(Constants.PREF_FRIEND_ACCOUNT_NAME, name);
                startActivity(intent);
            }
        });
        mRecyclerViewFriends.setAdapter(mUserFriendsAdapter);
        if (isDeviceOnline()) {
            attachDatabaseReadListener();
        } else {
            Snackbar.make(mRecyclerViewFriends, getResources().getString(R.string.network),
                    Snackbar.LENGTH_LONG).show();
        }
    }

    private void attachDatabaseReadListener() {

        // Obtain a new or prior instance of HotStockViewModel from the accountName
        // ViewModelProviders utility class.
        FriendsListViewModel viewModel = ViewModelProviders.of(this).get(FriendsListViewModel.class);
        viewModel.setAccountName(accountName);

        LiveData<DataSnapshot> liveData = viewModel.getDataSnapshotLiveData();

        liveData.observe(this, new Observer<DataSnapshot>() {
            @Override
            public void onChanged(@Nullable DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    userList.clear();
                    friendsEmails.clear();
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
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

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
