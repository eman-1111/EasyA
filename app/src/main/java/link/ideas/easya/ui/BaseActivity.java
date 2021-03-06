package link.ideas.easya.ui;


import android.app.ActivityManager;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.classroom.Classroom;
import com.google.api.services.classroom.ClassroomScopes;
import com.google.api.services.classroom.model.Course;
import com.google.api.services.classroom.model.ListCoursesResponse;
import com.google.api.services.classroom.model.ListStudentsResponse;
import com.google.api.services.classroom.model.Student;
import com.google.api.services.classroom.model.Teacher;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import link.ideas.easya.ui.course_list.CoursesList;
import link.ideas.easya.ui.friends_list.FriendsList;
import link.ideas.easya.ui.help.HelpActivity;
import link.ideas.easya.ui.profile.ProfileActivity;
import link.ideas.easya.R;

import link.ideas.easya.models.User;
import link.ideas.easya.utils.CircleTransform;
import link.ideas.easya.utils.Constants;
import link.ideas.easya.utils.Helper;

import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE;

public class BaseActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener {

    GoogleAccountCredential mCredential;
    private GoogleApiClient mGoogleApiClient;

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;

    public boolean isLogedIn, isDrawerEnable = false;


    private static final String LOG_TAG = BaseActivity.class.getSimpleName();
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUsersDatabaseReference;


    private static final String[] SCOPES2 = {ClassroomScopes.CLASSROOM_COURSES_READONLY,
            ClassroomScopes.CLASSROOM_ROSTERS};
    MakeRequestTask mMakeRequestTask;

    TextView mErrorText;
    public ProgressDialog mProgressDialog;
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View navHeader;
    private ImageView imgProfile;
    private TextView txtName;
    private Toolbar toolbar;
    private SignInButton signInButton;
    public String accountName;
     //todo remove Classroom API from BaseActivity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (!mSharedPreferences.getBoolean(Constants.PREF_FIRST_TIME, false)) {
            Intent helpIntent = new Intent(BaseActivity.this, HelpActivity.class);
            startActivity(helpIntent);
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putBoolean(Constants.PREF_FIRST_TIME, true);
            editor.commit();
        }
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    isLogedIn = true;

                } else {
                    isLogedIn = false;
                }
                if (isDrawerEnable) {
                    try {
                        updateUI(isLogedIn);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        SharedPreferences prefs = getSharedPreferences(Constants.PREF_USER_DATA, MODE_PRIVATE);
        accountName = prefs.getString(Constants.PREF_ACCOUNT_NAME, null);


    }


    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public void setUpAPIs() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this )
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mUsersDatabaseReference = mFirebaseDatabase.getReference().child(Constants.FIREBASE_LOCATION_USERS);

    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    private void signIn() {
        showProgressDialog();
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, REQUEST_ACCOUNT_PICKER);
    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);


        if (showUserStudying()) {
            SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = mSharedPreferences.edit();

            boolean setStudyingOnce = mSharedPreferences.getBoolean(Constants.PREF_SET_STUDYING, false);
            if (!setStudyingOnce) {
                editor.putBoolean(Constants.PREF_SET_STUDYING, true);
                editor.apply();
                showUserStudying(true);

            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (showUserStudying()) {
            if (!isApplicationSentToBackground(this)) {
                SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = mSharedPreferences.edit();
                editor.apply();
                editor.putBoolean(Constants.PREF_SET_STUDYING, false);
                showUserStudying(false);
            }

        }

    }



    /**
     * Attempt to call the API, after verifying that all the preconditions are
     * satisfied. The preconditions are: Google Play Services installed, an
     * account was selected and the device currently has online access. If any
     * of the preconditions are not satisfied, the app will prompt the user as
     * appropriate.
     */
    private void getResultsFromApi() throws IOException {
        if (!isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (!isDeviceOnline()) {
            deviceOffline();
        }else {
            mMakeRequestTask = (MakeRequestTask) new MakeRequestTask(mCredential).execute();
        }
    }


    /**
     * Called when an activity launched here (specifically, AccountPicker
     * and authorization) exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     *
     * @param requestCode code indicating which activity result is incoming.
     * @param resultCode  code indicating the result of the incoming
     *                    activity result.
     * @param data        Intent (containing result data) returned by incoming
     *                    activity result.
     */
    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {

                } else {
                    try {
                        getResultsFromApi();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK) {
                    GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                    try {
                        handleSignInResult(result);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else {
                    hideProgressDialog();
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    try {
                        getResultsFromApi();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;

        }
    }

    public void signOut() {
        // Firebase sign out
        mAuth.signOut();
        removeUserData();
        // Google sign out
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        isLogedIn = false;

                    }
                });
    }

    private void removeUserData() {
        SharedPreferences.Editor editor =
                getSharedPreferences(Constants.PREF_USER_DATA, MODE_PRIVATE).edit();

        editor.putString(Constants.PREF_ACCOUNT_NAME, null);
        editor.putString(Constants.PREF_ACCOUNT_USER_NAME, null);
        editor.putString(Constants.PREF_ACCOUNT_PHOTO_URL, null);
        editor.putString(Constants.PREF_ACCOUNT_USER_TOKEN, null);
        accountName = null;
        editor.apply();
        isLogedIn = false;
    }

    private void revokeAccess() {
        // Firebase sign out
        mAuth.signOut();
        removeUserData();
        // Google revoke access
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        isLogedIn = false;
                    }
                });
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(LOG_TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    private void handleSignInResult(GoogleSignInResult result) throws IOException {
        Log.d(LOG_TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            if (acct != null) {


                String accountName = acct.getEmail();
                String userName = acct.getDisplayName();
                String photoUrl = String.valueOf(acct.getPhotoUrl());
                String accountToken = acct.getIdToken();

                SharedPreferences.Editor editor =
                        getSharedPreferences(Constants.PREF_USER_DATA, MODE_PRIVATE).edit();

                editor.putString(Constants.PREF_ACCOUNT_NAME, accountName);
                editor.putString(Constants.PREF_ACCOUNT_USER_NAME, userName);
                editor.putString(Constants.PREF_ACCOUNT_PHOTO_URL, photoUrl);
                editor.putString(Constants.PREF_ACCOUNT_USER_TOKEN, accountToken);

                editor.apply();
                firebaseAuthWithGoogle(acct);
            }

        } else {
            isLogedIn = false;
            updateUI(isLogedIn);
        }
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) throws IOException {


        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        isLogedIn = task.isSuccessful();
                        User mUser = new User(acct.getDisplayName(), acct.getPhotoUrl() + "", Helper.getTimestampCreated());

                        mUsersDatabaseReference.child(Helper.encodeEmail(acct.getEmail())).setValue(mUser);
                        showUserStudying(true);
                        showTestFriend(acct.getEmail());
                        if (!task.isSuccessful()) {
                            Toast.makeText(BaseActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            isLogedIn = false;
                        }
                        try {
                            updateUI(isLogedIn);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        try {
                            getResultsFromApi();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        hideProgressDialog();
                    }
                });

    }
// [END auth_with_google]

    private void updateUI(boolean signedIn) throws IOException {
        if (signedIn) {
            SharedPreferences prefs = getSharedPreferences(Constants.PREF_USER_DATA, MODE_PRIVATE);

            accountName = prefs.getString(Constants.PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                isLogedIn = true;
                String userName = prefs.getString(Constants.PREF_ACCOUNT_USER_NAME, null);
                String photoUrl = prefs.getString(Constants.PREF_ACCOUNT_PHOTO_URL, null);

                signInButton.setVisibility(View.GONE);
                txtName.setText(userName);
                mCredential.setSelectedAccountName(accountName);
                Glide.with(this).load(photoUrl)
                        .transform(new CircleTransform(this))
                        .error(R.drawable.ic_account_circle_black_24dp)
                        .into(imgProfile);

            }
        } else {
            if (signInButton.getVisibility() == View.GONE) {
                signInButton.setVisibility(View.VISIBLE);
            }
            txtName.setText(null);
            Glide.with(this).load("")
                    .transform(new CircleTransform(this))
                    .error(R.drawable.ic_account_circle_black_24dp)
                    .into(imgProfile);

        }
    }


    /**
     * Checks whether the device currently has a network connection.
     *
     * @return true if the device has a network connection, false otherwise.
     */
    public boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    public void deviceOffline() {
        Snackbar.make(drawer, getResources().getString(R.string.network),
                Snackbar.LENGTH_LONG).show();
    }

    /**
     * Check that Google Play services APK is installed and up to date.
     *
     * @return true if Google Play Services is available and up to
     * date on this device; false otherwise.
     */
    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    /**
     * Attempt to resolve a missing, out-of-date, invalid or disabled Google
     * Play Services installation via a user dialog, if possible.
     */
    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            // showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }


    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     *
     * @param connectionStatusCode code describing the presence (or lack of)
     *                             Google Play Services on this device.
     */
    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                BaseActivity.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }


    /**
     * An asynchronous task that handles the Classroom API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
    private class MakeRequestTask extends AsyncTask<Void, Void, List<String>> {
        private Classroom mService = null;
        private Exception mLastError = null;

        public MakeRequestTask(GoogleAccountCredential credential) throws IOException {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

            mService = new Classroom.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName(getResources().getString(R.string.app_name))
                    .build();


        }

        /**
         * Background task to call Classroom API.
         *
         * @param params no parameters needed for this task.
         */
        @Override
        protected List<String> doInBackground(Void... params) {
            try {
                if (!this.isCancelled()) {
                    return getDataFromApi();
                }
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
            return null;
        }

        /**
         * Fetch a list of the names of the first 10 courses the user has access to.
         *
         * @return List course names, or a simple error message if no courses are
         * found.
         * @throws IOException
         */
        private List<String> getDataFromApi() throws IOException {


            Teacher response2;
            ListCoursesResponse response = mService.courses().list()
                    .setPageSize(10)
                    .execute();
            //UserProfile user = response.
            List<Course> courses = response.getCourses();
            int color = 0;
            if (courses != null) {
                for (Course course : courses) {
                    String courseName = course.getName();
                    String courseId = course.getId();


                    response2 = mService.courses().teachers().
                            get(courseId, course.getOwnerId()).execute();

                    Classroom.UserProfiles user = mService.userProfiles();

                    String teacherEmail = response2.getProfile().getEmailAddress();
                    String teacherName = response2.getProfile().getName().getFullName();
                    String teacherPhotoUrl = response2.getProfile().getPhotoUrl();

                    ListStudentsResponse studentsResponse = mService.courses().students().list(course.getId())
                            .setPageSize(40)
                            .execute();

                    List<Student> students = studentsResponse.getStudents();


                    for (Student student : students) {

                        Log.e(LOG_TAG, "StudentName: " + student.toString() + "names");
                    }

                    Log.d(LOG_TAG, "teacherInfo: " + "teacherName: " + teacherName + "teacherEmail: " + teacherEmail +
                            "teacherPhotoUrl: " + teacherPhotoUrl);

                    addCourseData(courseName, teacherName, teacherEmail,
                            teacherPhotoUrl, courseId, color);
                    color++;
                    Helper.updateWidgets(BaseActivity.this);
                }
            }


            return null;

        }


        @Override
        protected void onPreExecute() {

            showProgressDialog();

        }

        @Override
        protected void onPostExecute(List<String> output) {
            hideProgressDialog();
            if (output == null || output.size() == 0) {
            }
        }

        @Override
        protected void onCancelled() {
            hideProgressDialog();
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            BaseActivity.REQUEST_AUTHORIZATION);
                } else {
                    Log.e(LOG_TAG, getResources().getString(R.string.no_google_classroom));
                }
            }
        }
    }

    /**
     * Helper method to handle insertion of a new location in the weather database.
     *
     * @param courseName   .
     * @param teacherName  .
     * @param teacherEmail .
     * @param teacherPhoto .
     * @param courseId     .
     */
    long addCourseData(String courseName, String teacherName, String teacherEmail,
                       String teacherPhoto, String courseId, int selectedColorId) {
        //todo add course from class room
        return 0;
    }




    public void loadNavHeader(String activityTitle) {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(activityTitle);
        mErrorText = (TextView) findViewById(R.id.error_tv);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        // Navigation view header
        navHeader = navigationView.getHeaderView(0);
        txtName = (TextView) navHeader.findViewById(R.id.name);
        imgProfile = (ImageView) navHeader.findViewById(R.id.img_profile);

        signInButton = (SignInButton) navHeader.findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);


        // Initialize credentials and service object.
        mCredential = GoogleAccountCredential.usingOAuth2(

                getApplicationContext(), Arrays.asList(SCOPES2))
                .setBackOff(new ExponentialBackOff());

        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isLogedIn) {
                    Intent profileIntent = new Intent(BaseActivity.this, ProfileActivity.class);
                    startActivity(profileIntent);
                }
            }
        });

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isDeviceOnline()) {
                    signIn();
                } else {
                    deviceOffline();
                }
            }
        });

        try {
            updateUI(isLogedIn);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    case R.id.nav_home:
                        Intent homeIntent = new Intent(BaseActivity.this, CoursesList.class);
                        startActivity(homeIntent);
                        drawer.closeDrawers();
                        return true;
                    case R.id.nav_about:
                        Intent helpIntent = new Intent(BaseActivity.this, HelpActivity.class);
                        startActivity(helpIntent);
                        drawer.closeDrawers();
                        return true;
                    case R.id.nav_friends:
                        if (isLogedIn) {
                            Intent friendIntent = new Intent(BaseActivity.this, FriendsList.class);
                            startActivity(friendIntent);
                            drawer.closeDrawers();
                        } else {
                            drawer.closeDrawers();
                            Helper.startDialog(BaseActivity.this, getResources().getString(R.string.login_title_warning)
                                    , getResources().getString(R.string.login_des_warning));
                        }
                        return true;
                    default:
                }

                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);


                return true;
            }
        });


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.drawer_open, R.string.drawer_open) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawer.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (isDrawerEnable) {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawers();
                return;
            }
        }

        super.onBackPressed();
    }

    public void setDrawer(boolean isDrawerEnable) {
        this.isDrawerEnable = isDrawerEnable;
    }

    public void showTestFriend(String accountName) {
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();

        DatabaseReference mUsersFriendsDatabaseReference = mFirebaseDatabase.getReference()
                .child(Constants.FIREBASE_LOCATION_USER_FRIENDS)
                .child(Helper.encodeEmail(accountName)).child(Constants.FIREBASE_TEST_USER_EMAIL);;
        User user1 = new User(Constants.FIREBASE_TEST_USER_NAME, Constants.FIREBASE_TEST_USER_URL);
        mUsersFriendsDatabaseReference.setValue(user1);
    }


    public void showUserStudying(boolean isStudying) {
        SharedPreferences prefs = getSharedPreferences(Constants.PREF_USER_DATA, MODE_PRIVATE);
        String accountName = prefs.getString(Constants.PREF_ACCOUNT_NAME, null);
        if (accountName != null) {

            FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference mStudyingDatabaseReference = mFirebaseDatabase.getReference().
                    child(Constants.FIREBASE_LOCATION_USERS_COURSES).child(Helper.encodeEmail(accountName)).child(Constants.FIREBASE_LOCATION_USERS_IS_STUDYING);
            mStudyingDatabaseReference.setValue(isStudying);
        }
    }

    public boolean showUserStudying() {
        if (accountName != null) {
            SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            return mSharedPreferences.getBoolean(Constants.PREF_SHOW_STUDYING, true);
        }
        return false;
    }

    /**
     * Checks if the application is being sent in the background (i.e behind
     * another application's Activity).
     *
     * @param context the context
     * @return <code>true</code> if another application will be above this one.
     */
    public static boolean isApplicationSentToBackground(final Context context) {


        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
            ActivityManager.RunningTaskInfo foregroundTaskInfo = am.getRunningTasks(1).get(0);
            String foregroundTaskPackageName = foregroundTaskInfo.topActivity.getPackageName();

            return foregroundTaskPackageName.toLowerCase().equals(context.getPackageName().toLowerCase());
        } else {
            ActivityManager.RunningAppProcessInfo appProcessInfo = new ActivityManager.RunningAppProcessInfo();
            ActivityManager.getMyMemoryState(appProcessInfo);
            if (appProcessInfo.importance == IMPORTANCE_FOREGROUND || appProcessInfo.importance == IMPORTANCE_VISIBLE) {
                return true;
            }

            KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
            // App is foreground, but screen is locked, so show notification
            return km.inKeyguardRestrictedInputMode();
        }
    }

}
