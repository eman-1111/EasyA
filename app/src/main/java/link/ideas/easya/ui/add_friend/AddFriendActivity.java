package link.ideas.easya.ui.add_friend;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

import link.ideas.easya.ui.friends_list.FriendsList;
import link.ideas.easya.R;
import link.ideas.easya.models.User;
import link.ideas.easya.ui.BaseActivity;

/**
 * Created by Eman on 4/23/2017.
 */

public class AddFriendActivity extends BaseActivity {

    ArrayList<User> userList;
    ArrayList<String> userEmail;


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

        if (isDeviceOnline()) {
            addAutoComplete();
        } else {
            Snackbar.make(mListViewAutocomplete, getResources().getString(R.string.network),
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
        final AddFriendsViewModel viewModel =
                ViewModelProviders.of(this).get(AddFriendsViewModel.class);
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
                viewModel.setQuery(mInput);
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
                    LiveData<DataSnapshot> liveData = viewModel.getDataSnapshotLiveData();
                    liveData.observe(AddFriendActivity.this, new Observer<DataSnapshot>() {
                        @Override
                        public void onChanged(@Nullable DataSnapshot dataSnapshot) {
                            for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {

                                User user = childDataSnapshot.getValue(User.class);
                                userList.add(user);
                                userEmail.add(childDataSnapshot.getKey());
                            }
                            if (accountName != null) {
                                mFriendsAutocompleteAdapter = new AutocompleteFriendAdapter
                                        (AddFriendActivity.this, R.layout.single_autocomplete_item,
                                                userEmail, userList, accountName);
                                mListViewAutocomplete.setAdapter(mFriendsAutocompleteAdapter);

                                progress.setVisibility(View.GONE);

                            }
                            if (userList.size() == 0) {
                                Snackbar.make(mListViewAutocomplete, getResources().getString(R.string.toast_user_is_not_found),
                                        Snackbar.LENGTH_LONG).show();
                            }
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
