package link.ideas.easya.ui.profile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import link.ideas.easya.R;
import link.ideas.easya.ui.BaseActivity;
import link.ideas.easya.ui.course_list.CoursesList;
import link.ideas.easya.utils.CircleTransform;
import link.ideas.easya.utils.Constants;

/**
 * Created by Eman on 4/20/2017.
 */

public class ProfileActivity extends BaseActivity {
    ImageView userImage;
    TextView userName, userEmail;
    RelativeLayout logOut;
    Switch showStudying;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setDrawer(false);
        setUpIds();
        setUpAPIs();
        setUserData();
    }

    private void setUpIds() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userImage = (ImageView)  findViewById(R.id.user_image);
        userName = (TextView)  findViewById(R.id.tv_user_name);
        userEmail = (TextView)  findViewById(R.id.tv_user_email);
        showStudying = (Switch)  findViewById(R.id.switch_is_studying);
        logOut = (RelativeLayout)  findViewById(R.id.rlt_logout);

        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences.Editor editor = mSharedPreferences.edit();


        boolean studyingStatus = mSharedPreferences.getBoolean(Constants.PREF_SHOW_STUDYING, true);
        showStudying.setChecked(studyingStatus);

        showStudying.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                editor.putBoolean(Constants.PREF_FIRST_TIME, isChecked);
                editor.apply();
                showUserStudying(isChecked);
            }
        });


    }

    private void setUserData() {

        SharedPreferences prefs = getSharedPreferences(Constants.PREF_USER_DATA, MODE_PRIVATE);

        String account = prefs.getString(Constants.PREF_ACCOUNT_NAME, null);
        if (account != null) {
            String userNameS = prefs.getString(Constants.PREF_ACCOUNT_USER_NAME, null);
            String photoUrl = prefs.getString(Constants.PREF_ACCOUNT_PHOTO_URL, null);

            userName.setText(userNameS);
            userEmail.setText(account);

            Glide.with(this).load(photoUrl)
                    .transform(new CircleTransform(this))
                    .error(R.drawable.ic_account_circle_black_24dp)
                    .into(userImage);

            logOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    signOut();
                    showUserStudying(false);
                    Intent homeIntent = new Intent(ProfileActivity.this, CoursesList.class);
                    startActivity(homeIntent);
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.home) {
            Intent homeIntent = new Intent(ProfileActivity.this, CoursesList.class);
            startActivity(homeIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}