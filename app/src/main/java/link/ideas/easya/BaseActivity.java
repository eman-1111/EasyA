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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import link.ideas.easya.data.CourseContract;
import link.ideas.easya.utils.Constants;
import link.ideas.easya.utils.Helper;

public class BaseActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener {

    GoogleAccountCredential mCredential;
    private GoogleApiClient mGoogleApiClient;

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;

    private static final String TAG = "GoogleActivity";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


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

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                try {
                    updateUI(true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private void signIn() {
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
            // startIntent();
            mErrorText.setText(getResources().getString(R.string.network_toast));
        } else {
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
    private void signOut() {
        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        updateUI(null);
                    }
                });
    }
    private void revokeAccess() {
        // Firebase sign out
        mAuth.signOut();

        // Google revoke access
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        updateUI(null);
                    }
                });
    }

    private void updateUI(FirebaseUser user) {

//        if (user != null) {
//            mStatusTextView.setText(getString(R.string.google_status_fmt, user.getEmail()));
//            mDetailTextView.setText(getString(R.string.firebase_status_fmt, user.getUid()));
//
//            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
//            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.VISIBLE);
//        } else {
//            mStatusTextView.setText(R.string.signed_out);
//            mDetailTextView.setText(null);
//
//            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
//            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.GONE);
//        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }
    private void handleSignInResult(GoogleSignInResult result) throws IOException {
        Log.d("CourseList", "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            if (acct != null) {


                String accountName = acct.getEmail();
                String userName = acct.getDisplayName();
                String photoUrl = String.valueOf(acct.getPhotoUrl());
                String accountToken = acct.getIdToken();
                Log.e("accountToken: ",accountToken +"  accountToken");

                SharedPreferences.Editor editor =
                        getSharedPreferences(Constants.PREF_USER_DATA, MODE_PRIVATE).edit();

                editor.putString(Constants.PREF_ACCOUNT_NAME, accountName);
                editor.putString(Constants.PREF_ACCOUNT_USER_NAME, userName);
                editor.putString(Constants.PREF_ACCOUNT_PHOTO_URL, photoUrl);
                editor.putString(Constants.PREF_ACCOUNT_USER_TOKEN, accountToken);
                editor.apply();
                updateUI(true);
                getResultsFromApi();
                firebaseAuthWithGoogle(acct);
            }

        } else {
            // Signed out, show unauthenticated UI.
            //updateUI(false, null, null, null);
        }
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        showProgressDialog();

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(BaseActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // [START_EXCLUDE]
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
    }
// [END auth_with_google]

    private void updateUI(boolean signedIn) throws IOException {
        if (signedIn) {
            SharedPreferences prefs = getSharedPreferences(Constants.PREF_USER_DATA, MODE_PRIVATE);

            String accountName =prefs.getString(Constants.PREF_ACCOUNT_NAME, null);
            if (accountName != null ) {
                String userName =prefs.getString(Constants.PREF_ACCOUNT_USER_NAME, null);
                String photoUrl =prefs.getString(Constants.PREF_ACCOUNT_PHOTO_URL, null);

                // findViewById(R.id.sign_in_button).setVisibility(View.GONE);
                txtName.setText(userName);
                mCredential.setSelectedAccountName(accountName);
                Picasso.with(this).load(photoUrl)
                        .error(R.drawable.ic_account_circle_black_24dp).into(imgProfile);

            }

        } else {
            SharedPreferences.Editor editor =
                    getSharedPreferences(Constants.PREF_USER_DATA, MODE_PRIVATE).edit();

            editor.putString(Constants.PREF_ACCOUNT_NAME, null);
            editor.putString(Constants.PREF_ACCOUNT_USER_NAME, null);
            editor.putString(Constants.PREF_ACCOUNT_PHOTO_URL, null);
            editor.putString(Constants.PREF_ACCOUNT_USER_TOKEN, null);
            editor.apply();
        }
    }


    /**
     * Checks whether the device currently has a network connection.
     *
     * @return true if the device has a network connection, false otherwise.
     */
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
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
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
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

                    Log.e("teacherInfo", "userProfile: " + user + "fff");
                    String teacherEmail = response2.getProfile().getEmailAddress();
                    String teacherName = response2.getProfile().getName().getFullName();
                    String teacherPhotoUrl = response2.getProfile().getPhotoUrl();

                    ListStudentsResponse studentsResponse = mService.courses().students().list(course.getId())
                            .setPageSize(40)
                            .execute();

                    List<Student> students = studentsResponse.getStudents();


                    for (Student student : students) {

                        Log.e("StudentName", student.toString() + "names");
                    }

                    Log.e("teacherInfo", "teacherName: " + teacherName + "teacherEmail: " + teacherEmail +
                            "teacherPhotoUrl: " + teacherPhotoUrl);

                    addCourseData(courseName, teacherName, teacherEmail,
                            teacherPhotoUrl, courseId);
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
                mErrorText.setText("No results returned.");
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
                    mErrorText.setText(getResources().getString(R.string.no_google_classroom));
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
                       String teacherPhoto, String courseId) {

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
                teacherEmail = "Not Found";
            }
            if (teacherPhoto == null) {
                teacherPhoto = "Not Found";
            }

            Log.e("data", courseName + courseId + teacherName);
            courseValues.put(CourseContract.CourseEntry.COLUMN_COURSE_ID, courseId);
            courseValues.put(CourseContract.CourseEntry.COLUMN_COURSE_NAME, courseName);
            courseValues.put(CourseContract.CourseEntry.COLUMN_TEACHER_EMAIL, teacherEmail);
            courseValues.put(CourseContract.CourseEntry.COLUMN_TEACHER_PHOTO_URL, teacherPhoto);
            courseValues.put(CourseContract.CourseEntry.COLUMN_TEACHER_NAME, teacherName);

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
                this);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText courseNameET = (EditText) promptsView
                .findViewById(R.id.course_name_et);

        final EditText teacherNameET = (EditText) promptsView
                .findViewById(R.id.teacher_name_et);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Random randomGenerator = new Random();
                                int random = randomGenerator.nextInt(8964797);
                                String courseId = "user" + random;
                                if (courseNameET.getText().toString().trim().isEmpty()) {
                                    Snackbar.make(drawer, "Please fill in the course name",
                                            Snackbar.LENGTH_LONG).show();

                                } else {
                                    addCourseData(courseNameET.getText().toString(), teacherNameET.getText().toString(),
                                            null, null, courseId);
                                    Helper.updateWidgets(BaseActivity.this);
                                }

                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();


    }

    public void loadNavHeader() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mErrorText = (TextView) findViewById(R.id.error_tv);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        // Navigation view header
        navHeader = navigationView.getHeaderView(0);
        txtName = (TextView) navHeader.findViewById(R.id.name);
        imgProfile = (ImageView) navHeader.findViewById(R.id.img_profile);

        SignInButton signInButton = (SignInButton) navHeader.findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);


        // Initialize credentials and service object.
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES2))
                .setBackOff(new ExponentialBackOff());

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                signIn();
            }
        });


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
                        Toast.makeText(BaseActivity.this, "Time for an upgrade!", Toast.LENGTH_SHORT).show();
                        drawer.closeDrawers();
                        return true;
                    case R.id.nav_friends:
                        Toast.makeText(BaseActivity.this, "Time for an upgrade!", Toast.LENGTH_SHORT).show();
                        drawer.closeDrawers();
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
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }

        super.onBackPressed();
    }


}
