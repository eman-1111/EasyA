package link.ideas.easya;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import link.ideas.easya.adapter.AutocompleteFriendAdapter;
import link.ideas.easya.models.User;
import link.ideas.easya.utils.Constants;

/**
 * Created by Eman on 4/23/2017.
 */

public class AddFriendActivity extends BaseActivity {

    ArrayList<User> userList;
    ArrayList<String> userEmail;

    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mUsersDatabaseReference;

    private AutocompleteFriendAdapter mFriendsAutocompleteAdapter;
    private String mInput;
    private ListView mListViewAutocomplete;
    LinearLayout progress;

    EditText mEditTextAddFriendEmail;
    private static final String LOG_TAG = AddFriendActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        setDrawer(false);
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

        mEditTextAddFriendEmail = (EditText) findViewById(R.id.edit_text_add_friend_email);
        mListViewAutocomplete = (ListView) findViewById(R.id.list_view_friends_autocomplete);
        progress = (LinearLayout) findViewById(R.id.lin_Progress);

        userList = new ArrayList<User>();
        userEmail = new ArrayList<String>();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mUsersDatabaseReference = mFirebaseDatabase.getReference().child(Constants.FIREBASE_LOCATION_USERS);

        if (isDeviceOnline()) {
            addAutoComplete();
        }else {
            Snackbar.make(mListViewAutocomplete,getResources().getString(R.string.network) ,
                    Snackbar.LENGTH_LONG).show();
        }


    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    private void addAutoComplete() {

        mEditTextAddFriendEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                /* Get the input after every textChanged event and transform it to lowercase */
                mInput = mEditTextAddFriendEmail.getText().toString().toLowerCase();

            /* Clean up the old adapter */
                if (mFriendsAutocompleteAdapter != null) {
                    userList.clear();
                    userEmail.clear();
                    mFriendsAutocompleteAdapter.notifyDataSetChanged();
                }
            /* Nullify the adapter data if the input length is less than 2 characters */
                if (mInput.equals("") || mInput.length() < 2) {
                    mListViewAutocomplete.setAdapter(null);

            /* Define and set the adapter otherwise. */
                } else {
                    progress.setVisibility(View.VISIBLE);
                    Query query = mUsersDatabaseReference.orderByKey().startAt(mInput).endAt(mInput + "~")
                            .limitToFirst(5);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {

                                User user = childDataSnapshot.getValue(User.class);
                                userList.add(user);
                                userEmail.add(childDataSnapshot.getKey());
                            }
                            if (accountName != null) {
                                mFriendsAutocompleteAdapter = new AutocompleteFriendAdapter
                                        (AddFriendActivity.this, R.layout.single_autocomplete_item,
                                                userEmail,userList, accountName);
                                mListViewAutocomplete.setAdapter(mFriendsAutocompleteAdapter);

                                progress.setVisibility(View.GONE);

                            }
                            if(userList.size() == 0){
                                Snackbar.make(mListViewAutocomplete,getResources().getString(R.string.toast_user_is_not_found) ,
                                        Snackbar.LENGTH_LONG).show();
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.e(LOG_TAG , databaseError +"");

                            progress.setVisibility(View.GONE);
                        }
                    });

                }

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, FriendsList.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
