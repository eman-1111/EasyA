package link.ideas.easya;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import link.ideas.easya.adapter.UserFriendsAdapter;
import link.ideas.easya.models.User;
import link.ideas.easya.utils.Constants;

/**
 * Created by Eman on 4/23/2017.
 */

public class AddFriendActivity extends AppCompatActivity {

    RecyclerView mRecyclerViewFriends;
    UserFriendsAdapter mUserFriendsAdapter;
    List<User> userList;

    ValueEventListener mChildEventListener;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mUsersDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        initializeScreen();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    /**
     * Link layout elements from XML and setup the toolbar
     */
    public void initializeScreen() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userList = new ArrayList<User>();
        mRecyclerViewFriends = (RecyclerView) findViewById(R.id.recyclerview_friend);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mUsersDatabaseReference = mFirebaseDatabase.getReference().child(Constants.FIREBASE_LOCATION_USERS);

        mRecyclerViewFriends.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerViewFriends.setHasFixedSize(true);
        mUserFriendsAdapter = new UserFriendsAdapter(userList, this, new UserFriendsAdapter.UserFriendsAdapterOnClickHolder() {
            @Override
            public void onClick(String id, String lessonName, UserFriendsAdapter.UserFriendsAdapterViewHolder vh) {
                Log.e("data", "fffff");
            }
        });
        mRecyclerViewFriends.setAdapter(mUserFriendsAdapter);

    }

    private void attachDatabaseReadListener() {

        // What I will do with this data iget from firebase
        mChildEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("data", dataSnapshot + "vvv");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        //what cild I am listening to
        mUsersDatabaseReference.addValueEventListener(mChildEventListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        attachDatabaseReadListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        detachDatabaseReadListener();
    }

    private void detachDatabaseReadListener() {
        if (mChildEventListener != null) {
            mUsersDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;

        }
    }
}
