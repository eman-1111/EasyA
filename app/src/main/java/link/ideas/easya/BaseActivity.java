package link.ideas.easya;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
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
import java.util.Random;

import link.ideas.easya.adapter.ColorAdapter;
import link.ideas.easya.data.CourseContract;

import link.ideas.easya.models.User;
import link.ideas.easya.utils.CircleTransform;
import link.ideas.easya.utils.Constants;
import link.ideas.easya.utils.Helper;

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
                .enableAutoManage(this, BaseActivity.this)
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
    }

    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
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
            case 11:
                if (resultCode == RESULT_OK) {
                    Uri selectedImageUri = data.getData();
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
                            teacherPhotoUrl, courseId, 0);
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
//                    showGooglePlayServicesAvailabilityErrorDialog(
//                            ((GooglePlayServicesAvailabilityIOException) mLastError)
//                                    .getConnectionStatusCode());
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

        long courseID;

        Cursor courseCursor = this.getContentResolver().query(
                CourseContract.CourseEntry.CONTENT_URI,
                new String[]{CourseContract.CourseEntry._ID},
                CourseContract.CourseEntry.COLUMN_COURSE_NAME + " = ?",
                new String[]{courseName},
                null);

        if (courseCursor.moveToFirst()) {
            int courseIdIndex = courseCursor.getColumnIndex(CourseContract.CourseEntry._ID);
            courseID = courseCursor.getLong(courseIdIndex);
        } else {

            ContentValues courseValues = new ContentValues();

            if (teacherEmail == null) {
                teacherEmail = getResources().getString(R.string.not_found);
            }
            if (teacherPhoto == null) {
                teacherPhoto = getResources().getString(R.string.not_found);
            }

            courseValues.put(CourseContract.CourseEntry.COLUMN_COURSE_ID, courseId);
            courseValues.put(CourseContract.CourseEntry.COLUMN_COURSE_NAME, courseName);
            courseValues.put(CourseContract.CourseEntry.COLUMN_TEACHER_EMAIL, teacherEmail);
            courseValues.put(CourseContract.CourseEntry.COLUMN_TEACHER_PHOTO_URL, teacherPhoto);
            courseValues.put(CourseContract.CourseEntry.COLUMN_TEACHER_NAME, teacherName);
            courseValues.put(CourseContract.CourseEntry.COLUMN_TEACHER_COLOR, selectedColorId);


            // Finally, insert Course data into the database.
            Uri insertedUri = this.getContentResolver().insert(
                    CourseContract.CourseEntry.CONTENT_URI,
                    courseValues);


            // The resulting URI contains the ID for the row.  Extract the locationId from the Uri.
            courseID = ContentUris.parseId(insertedUri);
        }

        courseCursor.close();
        // Wait, that worked?  Yes!
        return courseID;
    }


    public void startDialog() {


        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.add_course_name, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this, R.style.DialogTheme);
        alertDialogBuilder.setView(promptsView);
        final EditText courseNameET = (EditText) promptsView
                .findViewById(R.id.course_name_et);

        final EditText teacherNameET = (EditText) promptsView
                .findViewById(R.id.teacher_name_et);
        final TextView txtOK = (TextView) promptsView
                .findViewById(R.id.txt_ok);

        final TextView txtCancel = (TextView) promptsView
                .findViewById(R.id.txt_cancel);

        final int[] selectedColorId = {0};
        final GridView gridView = (GridView) promptsView.findViewById(R.id.gridview_color);
        ColorAdapter mColorAdapter = new ColorAdapter(this);
        gridView.setAdapter(mColorAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                ColorAdapter mColorAdapter = (ColorAdapter) gridView.getAdapter();
                mColorAdapter.selectedImage = position;
                mColorAdapter.notifyDataSetChanged();
                selectedColorId[0] = position;


            }
        });

        // create alert dialog
        final AlertDialog alertDialog = alertDialogBuilder.create();
        txtOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Random randomGenerator = new Random();
                int random = randomGenerator.nextInt(8964797);
                String courseId = getResources().getString(R.string.user) + random;
                if (courseNameET.getText().toString().trim().isEmpty()) {
                    Snackbar.make(drawer, getResources().getString(R.string.course_name_error),
                            Snackbar.LENGTH_LONG).show();

                } else {
                    addCourseData(courseNameET.getText().toString(), teacherNameET.getText().toString(),
                            null, null, courseId, selectedColorId[0]);
                    Helper.updateWidgets(BaseActivity.this);
                    alertDialog.cancel();
                }

            }
        });
        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.cancel();
            }
        });
        // show it
        alertDialog.show();


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
        imgProfile.setContentDescription(getString(R.string.a11y_profileImage));

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


}
