package eman.app.android.easya;


import android.Manifest;
import android.accounts.AccountManager;
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
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AlertDialog;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import eman.app.android.easya.data.CourseContract;

import com.firebase.client.Firebase;
import com.firebase.client.ServerValue;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.classroom.Classroom;
import com.google.api.services.classroom.ClassroomScopes;
import com.google.api.services.classroom.model.Course;
import com.google.api.services.classroom.model.CourseAlias;
import com.google.api.services.classroom.model.ListCoursesResponse;
import com.google.api.services.classroom.model.Teacher;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import eman.app.android.easya.utils.Constants;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;


public class CoursesList extends AppCompatActivity
        implements EasyPermissions.PermissionCallbacks, CourseListFragment.Callback {
    GoogleAccountCredential mCredential;

    ProgressDialog mProgress;
    TextView mErrorText;
    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;


    private static final String PREF_ACCOUNT_NAME = "accountName";

    private static final String[] SCOPES = {ClassroomScopes.CLASSROOM_COURSES_READONLY};

    private GoogleApiClient client;

    /**
     * Create the main activity.
     *
     * @param savedInstanceState previously saved instance data.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* Initialize Firebase */
        Firebase.setAndroidContext(this);

        /**
         * Create Firebase references
         */


        setContentView(R.layout.courses_list);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Slide slide = new Slide();
            slide.setSlideEdge(Gravity.BOTTOM);
            slide.setDuration(1000);
            getWindow().setEnterTransition(slide);
            getWindow().setExitTransition(slide);

        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        mErrorText = (TextView) findViewById(R.id.error_tv);

        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Loading your courses ...");


        // Initialize credentials and service object.
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());

        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        try {
            getResultsFromApi();
        } catch (IOException e) {
            e.printStackTrace();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startDialog(view);
            }
        });
    }

    @Override
    public void onItemSelected(Uri contentUri, String courseName) {

        Intent intent = new Intent(this, SubjectList.class)
                .setData(contentUri);
        intent.putExtra("CourseName", courseName);
        ActivityOptionsCompat activityOptions =
                ActivityOptionsCompat.makeSceneTransitionAnimation(this);
        ActivityCompat.startActivity(this, intent, activityOptions.toBundle());
        this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

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
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (!isDeviceOnline()) {
            // startIntent();
            mErrorText.setText("No network connection available.");
        } else {
            new MakeRequestTask(mCredential).execute();
        }
    }

    /**
     * Attempts to set the account used with the API credentials. If an account
     * name was previously saved it will use that one; otherwise an account
     * picker dialog will be shown to the user. Note that the setting the
     * account to use with the credentials object requires the app to have the
     * GET_ACCOUNTS permission, which is requested here if it is not already
     * present. The AfterPermissionGranted annotation indicates that this
     * function will be rerun automatically whenever the GET_ACCOUNTS permission
     * is granted.
     */
    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() throws IOException {
        if (EasyPermissions.hasPermissions(
                this, Manifest.permission.GET_ACCOUNTS)) {
            String accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi();
            } else {
                // Start a dialog from which the user can choose an account
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
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
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        try {
                            getResultsFromApi();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
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

    /**
     * Respond to requests for permissions at runtime for API 23 and above.
     *
     * @param requestCode  The request code passed in
     *                     requestPermissions(android.app.Activity, String, int, String[])
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }

    /**
     * Callback for when a permission is granted using the EasyPermissions
     * library.
     *
     * @param requestCode The request code associated with the requested
     *                    permission
     * @param list        The requested permission list. Never null.
     */
    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Callback for when a permission is denied using the EasyPermissions
     * library.
     *
     * @param requestCode The request code associated with the requested
     *                    permission
     * @param list        The requested permission list. Never null.
     */
    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Do nothing.
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
                CoursesList.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();

    }

    @Override
    public void onStop() {
        super.onStop();


        client.disconnect();
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
                    .setApplicationName("Classroom API Android Quickstart")
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

                return getDataFromApi();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
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

            List<Course> courses = response.getCourses();

            if (courses != null) {
                for (Course course : courses) {
                    String courseName = course.getName();
                    String courseId = course.getId();
                    //////////////////////////////
                    String alias = "p:bio10p2";
                    CourseAlias courseAlias = new CourseAlias().setAlias(alias);
                    try {
                        courseAlias = mService.courses().aliases().create(courseId, courseAlias).execute();
                        System.out.printf("Alias '%s' created.\n", courseAlias.getAlias());
                    } catch (GoogleJsonResponseException e) {
                        GoogleJsonError error = e.getDetails();
                        if (error.getCode() == 409) {
                            System.out.printf("Alias '%s' is already in use.\n", alias);
                        } else {
                            throw e;
                        }
                    }

                    //////////////////////////////////

                    response2 = mService.courses().teachers().
                            get(courseId, course.getOwnerId()).execute();


                    String teacherEmail = response2.getProfile().getEmailAddress();
                    String teacherName = response2.getProfile().getName().getFullName();
                    String teacherPhotoUrl = response2.getProfile().getPhotoUrl();


                    addCourseData(courseName, teacherName, teacherEmail,
                            teacherPhotoUrl, courseId);

                }
            }


            return null;

        }


        @Override
        protected void onPreExecute() {

            mProgress.show();

        }

        @Override
        protected void onPostExecute(List<String> output) {
            mProgress.hide();
            if (output == null || output.size() == 0) {
                mErrorText.setText("No results returned.");
            }
        }

        @Override
        protected void onCancelled() {
            mProgress.hide();
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            CoursesList.REQUEST_AUTHORIZATION);
                } else {
                    mErrorText.setText("You need to have Google Classroom,so Easy A could Load your courses");
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
                CourseContract.CourseEntry.COLUMN_COURSE_ID + " = ?",
                new String[]{courseId},
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
            /**
             * Create Firebase references
             */
            Firebase userRef = new Firebase(Constants.FIREBASE_URL);
            Firebase newCourseRef = userRef.push();

            /* Save listsRef.push() to maintain same random Id */
            final String courseFireId = newCourseRef.getKey();
            if(courseFireId != null){
                courseId = courseFireId;
            }
            courseValues.put(CourseContract.CourseEntry.COLUMN_COURSE_ID, courseId);
            courseValues.put(CourseContract.CourseEntry.COLUMN_COURSE_NAME, courseName);
            courseValues.put(CourseContract.CourseEntry.COLUMN_TEACHER_EMAIL, teacherEmail);
            courseValues.put(CourseContract.CourseEntry.COLUMN_TEACHER_PHOTO_URL, teacherPhoto);
            courseValues.put(CourseContract.CourseEntry.COLUMN_TEACHER_NAME, teacherName);

            // Finally, insert Course data into the database.
            Uri insertedUri = this.getContentResolver().insert(
                    CourseContract.CourseEntry.CONTENT_URI,
                    courseValues);


            /**
             * Set raw version of date to the ServerValue.TIMESTAMP value and save into
             * timestampCreatedMap
             */
            HashMap<String, Object> timestampCreated = new HashMap<>();
            timestampCreated.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);

            /* Build the shopping list */
            CourseContent newCourse = new CourseContent(courseId, courseName, teacherName, teacherPhoto,
                    teacherEmail, timestampCreated);

            /* Add the shopping list */
            newCourseRef.setValue(newCourse);
            // The resulting URI contains the ID for the row.  Extract the locationId from the Uri.
            courseID = ContentUris.parseId(insertedUri);
        }

        courseCursor.close();
        // Wait, that worked?  Yes!
        return courseID;
    }


    protected void startDialog(final View view) {


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
                                    Snackbar.make(view, "Please fill in the course name",
                                            Snackbar.LENGTH_LONG).show();

                                } else {
                                    addCourseData(courseNameET.getText().toString(), teacherNameET.getText().toString(),
                                            null, null, courseId);
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
}


