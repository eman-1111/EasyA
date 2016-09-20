package eman.app.android.easya;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;


import com.squareup.picasso.Picasso;


import java.util.HashMap;

import eman.app.android.easya.data.CourseContract;
import eman.app.android.easya.utils.Constants;

public class AddLessonDetail extends AppCompatActivity implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = AddLessonDetail.class.getSimpleName();

    String courseId, lessonName, lessonOverView, imageOutline;
    boolean edit = false;
    TextInputLayout inputLayoutLink, inputLayoutDebug, inputLayoutAppTitle, inputLayoutApp;
    EditText lessonLink, lessonDebug, lessonLifeAppTitle, lessonLifeApp;
    ImageView infoLink, infoDebug, infoTitle, infoApp;
    ImageView imageLink, imageDebug, imageApp;
    String imageLinkS = "l" , imageDebugS = "l", imageAppS = "l";
    String lessonLinks, lessonDebugs, lessonLifeAppTitles, lessonLifeApps;

    Bundle dataB;

    private Uri mUri;
    private static final int DETAIL_LOADER = 0;
    private static final String[] DETAIL_COLUMNS = {
            CourseContract.SubjectEntry.COLUMN_COURSE_ID,
            CourseContract.SubjectEntry.COLUMN_LESSON_LINK,
            CourseContract.SubjectEntry.COLUMN_LESSON_PRACTICAL_TITLE,
            CourseContract.SubjectEntry.COLUMN_LESSON_PRACTICAL,
            CourseContract.SubjectEntry.COLUMN_LESSON_DEBUG,
            CourseContract.SubjectEntry.COLUMN_LESSON_TITLE,
            CourseContract.CourseEntry.COLUMN_COURSE_NAME
    };


    public static final int COL_COURSE_ID = 0;
    public static final int COL_LESSON_LINK = 1;
    public static final int COL_LESSON_PRACTICAL_TITLE = 2;
    public static final int COL_LESSON_PRACTICAL = 3;
    public static final int COL_LESSON_DEBUG = 4;
    public static final int COL_LESSON_NAME = 5;
    public static final int COL_COURSE_NAME = 6;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_lesson_detail);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            Fade fade = new Fade();
            fade.setDuration(1000);
            getWindow().setExitTransition(fade);
            getWindow().setEnterTransition(fade);

        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        setUpIds();

        Intent intent = getIntent();

        courseId = intent.getStringExtra("CourseId");
        if (intent.getStringExtra("LessonName") != null) {
            lessonName = intent.getStringExtra("LessonName");
            lessonOverView = intent.getStringExtra("LessonOverview");
            imageOutline = intent.getStringExtra("ImageUrl");
        }

        if (getIntent().getExtras() != null) {
            getSavedInstanceState(getIntent().getExtras());
        }
        if (intent.getStringExtra("LessonURL") != null) {
            mUri = Uri.parse(intent.getStringExtra("LessonURL"));
            edit = true;
            getSupportLoaderManager().initLoader(DETAIL_LOADER, null, this);
        }

        if (intent.getStringExtra("linkImage") != null) {
            imageLinkS = intent.getStringExtra("linkImage");

        }

        if (intent.getStringExtra("debugImage") != null) {
            imageDebugS = intent.getStringExtra("debugImage");
        }
        if (intent.getStringExtra("appImage") != null) {
            imageAppS = intent.getStringExtra("appImage");

        }

        Picasso.with(this).load(imageLinkS).error(R.drawable.air_plan).resize(80, 50)
                .into(imageLink);


        Picasso.with(this).load(imageDebugS).error(R.drawable.air_plan)
                .into(imageDebug);

        Picasso.with(this).load(imageAppS).error(R.drawable.air_plan)
                .into(imageApp);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lessonLinks = lessonLink.getText().toString();
                lessonDebugs = lessonDebug.getText().toString();
                lessonLifeAppTitles = lessonLifeAppTitle.getText().toString();
                lessonLifeApps = lessonLifeApp.getText().toString();

                Log.e("Insert",courseId +lessonName+ lessonOverView+ " "+imageOutline+ " "+lessonLinks+
                        imageLinkS+ lessonLifeAppTitles+ lessonLifeApps+ imageAppS+
                        lessonDebugs+ imageDebugS);
                addLessonData(courseId, lessonName, lessonOverView, imageOutline, lessonLinks, imageLinkS,
                        lessonLifeAppTitles, lessonLifeApps, imageAppS,
                        lessonDebugs, imageDebugS);
                Intent intent = new Intent(AddLessonDetail.this, SubjectList.class)
                        .setData(CourseContract.SubjectEntry.buildSubjectWithID(courseId));
                startActivity(intent);


            }
        });
    }


    @Override
    public void onClick(View v) {
        String info;
        switch (v.getId()) {
            case R.id.link_info:
                info = this.getString(R.string.info_link);
                showDialog(info);
                break;
            case R.id.debug_info:
                info = this.getString(R.string.info_debug);
                showDialog(info);
                break;

            case R.id.title_info:
                info = this.getString(R.string.info_title);
                showDialog(info);
                break;
            case R.id.app_info:
                info = this.getString(R.string.info_descr);
                showDialog(info);
                break;
            case R.id.link_image:
                startIntent("linkImage");
                break;
            case R.id.debug_image:
                startIntent("debugImage");
                break;
            case R.id.app_image:
                startIntent("appImage");
                break;

        }

    }

    private void showDialog(String info) {
        AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
        builder2.setMessage(info);
        builder2.setPositiveButton("OK", null);
        builder2.show();
    }

    private void startIntent(String extra) {
        Intent intent = new Intent(AddLessonDetail.this, ImagesSearch.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("CourseId", courseId);
        intent.putExtra("imageName", extra);
        intent.putExtras(setSavedInstanceState());
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, AddSubjectTitel.class);
            intent.putExtra("CourseId", courseId);
            intent.putExtra("SavedStatue", "back");
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Helper method to handle insertion of a new location in the weather database.
     *
     * @param courseId           .
     * @param lessonLink         .
     * @param lessonLinkImage    .
     * @param lessonDebugs       .
     * @param lessonDebugsImage  .
     * @param lessonLifeAppTitle .
     * @param lessonLifeApp      .
     * @param lessonAppImage
     */
    void addLessonData(String courseId, String lessonName, String lessonOutline, String lessonOutlineImage,
                       String lessonLink, String lessonLinkImage, String lessonLifeAppTitle,
                       String lessonLifeApp, String lessonAppImage, String lessonDebugs,
                       String lessonDebugsImage) {

        ContentValues courseValues = new ContentValues();

        courseValues.put(CourseContract.SubjectEntry.COLUMN_COURSE_ID, courseId);
        courseValues.put(CourseContract.SubjectEntry.COLUMN_LESSON_TITLE, lessonName);
        courseValues.put(CourseContract.SubjectEntry.COLUMN_LESSON_OUTLINE, lessonOutline);
        courseValues.put(CourseContract.SubjectEntry.COLUMN_LESSON_OUTLINE_IMAGE, lessonOutlineImage);
        courseValues.put(CourseContract.SubjectEntry.COLUMN_LESSON_LINK, lessonLink);
        courseValues.put(CourseContract.SubjectEntry.COLUMN_LESSON_LINK_IMAGE, lessonLinkImage);
        courseValues.put(CourseContract.SubjectEntry.COLUMN_LESSON_PRACTICAL_TITLE, lessonLifeAppTitle);
        courseValues.put(CourseContract.SubjectEntry.COLUMN_LESSON_PRACTICAL, lessonLifeApp);
        courseValues.put(CourseContract.SubjectEntry.COLUMN_LESSON_PRACTICAL_IMAGE, lessonAppImage);
        courseValues.put(CourseContract.SubjectEntry.COLUMN_LESSON_DEBUG, lessonDebugs);
        courseValues.put(CourseContract.SubjectEntry.COLUMN_LESSON_DEBUG_IMAGE, lessonDebugsImage);
//        String COURSE_ID, String LESSON_TITLE, String LESSON_OUTLINE,
//                String LESSON_OUTLINE_IMAGE, String LESSON_LINK, String LESSON_LINK_IMAGE,
//                String LESSON_DEBUG, String LESSON_DEBUG_IMAGE,
//                String LESSON_PRACTICAL_TITLE, String LESSON_PRACTICAL,
//                String LESSON_PRACTICAL_IMAGE, String FAVORITE) {

        if (edit) {


            this.getContentResolver().update(CourseContract.SubjectEntry.buildSubjectsUri(),
                    courseValues,
                    CourseContract.SubjectEntry.TABLE_NAME +
                            "." + CourseContract.SubjectEntry.COLUMN_LESSON_TITLE + " = ? ",
                    new String[]{lessonName});

        } else {
            courseValues.put(CourseContract.SubjectEntry.COLUMN_FAVORITE, 0);
            final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            String userKey = sharedPref.getString(Constants.PREF_USER_ACCOUNT_KEY, null);

            Log.e("Key Subject", userKey);



            this.getContentResolver().insert(
                    CourseContract.SubjectEntry.CONTENT_URI,
                    courseValues);
        }

        // Get the string that the user entered into the EditText
        // Go to the "listName" child node of the root node.
        // This will create the node for you if it doesn't already exist.
        // Then using the setValue menu it will set value the node to userEnteredName.

        // Wait, that worked?  Yes!

        // Wait, that worked?  Yes!
        Intent intent = new Intent(this, SubjectList.class);
        startActivity(intent);

    }
    private void addItem(String itemId) {

    }


    private void setUpIds() {
        inputLayoutLink = (TextInputLayout) findViewById(R.id.input_layout_linkx);
        inputLayoutDebug = (TextInputLayout) findViewById(R.id.input_layout_debug);
        inputLayoutAppTitle = (TextInputLayout) findViewById(R.id.input_layout_app_title);
        inputLayoutApp = (TextInputLayout) findViewById(R.id.input_layout_app);

        lessonLink = (EditText) findViewById(R.id.lesson_linkx_et);
        lessonDebug = (EditText) findViewById(R.id.lesson_debug_et);
        lessonLifeAppTitle = (EditText) findViewById(R.id.lesson_app_title_et);
        lessonLifeApp = (EditText) findViewById(R.id.lesson_app_et);

        infoLink = (ImageView) findViewById(R.id.link_info);
        infoDebug = (ImageView) findViewById(R.id.debug_info);
        infoTitle = (ImageView) findViewById(R.id.title_info);
        infoApp = (ImageView) findViewById(R.id.app_info);
        infoApp.setOnClickListener(this);
        infoDebug.setOnClickListener(this);
        infoLink.setOnClickListener(this);
        infoTitle.setOnClickListener(this);


        imageLink = (ImageView) findViewById(R.id.link_image);
        imageDebug = (ImageView) findViewById(R.id.debug_image);
        imageApp = (ImageView) findViewById(R.id.app_image);
        imageLink.setOnClickListener(this);
        imageDebug.setOnClickListener(this);
        imageApp.setOnClickListener(this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        if (null != mUri) {
            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            return new CursorLoader(
                    this,
                    mUri,
                    DETAIL_COLUMNS,
                    null,
                    null,
                    null
            );
        }
        return null;
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (data != null && data.moveToFirst()) {

            Picasso.with(this).load(imageLinkS).error(R.drawable.air_plan)
                    .into(imageLink);

            Picasso.with(this).load(imageDebugS).error(R.drawable.air_plan)
                    .into(imageDebug);

            Picasso.with(this).load(imageAppS).error(R.drawable.air_plan)
                    .into(imageApp);

            lessonLink.setText(data.getString(COL_LESSON_LINK));
            lessonDebug.setText(data.getString(COL_LESSON_DEBUG));
            lessonLifeAppTitle.setText(data.getString(COL_LESSON_PRACTICAL_TITLE));
            lessonLifeApp.setText(data.getString(COL_LESSON_PRACTICAL));

            courseId = data.getString(COL_COURSE_ID);
            lessonName = data.getString(COL_LESSON_NAME);
        }
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }



    public Bundle setSavedInstanceState() {
        // Store values between instances here
        // SharedPreferences.Editor savedInstanceState = getPreferences(MODE_PRIVATE).edit();
        Bundle data = new Bundle();

        data.putString("ImageLink", imageLinkS);
        data.putString("ImageDebug", imageDebugS);
        data.putString("ImageApp", imageAppS);

        data.putString("lessonName", lessonName);
        data.putString("lessonOverView", lessonOverView);
        data.putString("imageOutline", imageOutline);

        lessonLinks = lessonLink.getText().toString();
        lessonDebugs = lessonDebug.getText().toString();
        lessonLifeAppTitles = lessonLifeAppTitle.getText().toString();
        lessonLifeApps = lessonLifeApp.getText().toString();

        data.putString("LessonLink", lessonLinks);
        data.putString("LessonDebug", lessonDebugs);
        data.putString("LessonLifeAppTitle", lessonLifeAppTitles);
        data.putString("LessonLifeApp", lessonLifeApps);

        return data;
        // Commit to storage
        // savedInstanceState.commit();
    }


    public void getSavedInstanceState(Bundle data) {

        if (data.getString("lessonName") != null) {
            lessonName = data.getString("lessonName");
        }
        if (data.getString("lessonOverView") != null) {
            lessonOverView = data.getString("lessonOverView");
        }
        if (data.getString("imageOutline") != null) {
            imageOutline = data.getString("imageOutline");
        }

        if (data.getString("LessonLink") != null) {
            lessonLinks = data.getString("LessonLink");
            lessonLink.setText(lessonLinks);
        }
        if (data.getString("LessonDebug") != null) {
            lessonDebugs = data.getString("LessonDebug");
            lessonDebug.setText(lessonDebugs);
        }
        if (data.getString("LessonLifeAppTitle") != null) {
            lessonLifeAppTitles = data.getString("LessonLifeAppTitle");
            lessonLifeAppTitle.setText(lessonLifeAppTitles);
        }
        if (data.getString("LessonLifeApp") != null) {
            lessonLifeApps = data.getString("LessonLifeApp");
            lessonLifeApp.setText(lessonLifeApps);
        }


        if (data.getString("ImageLink") != null) {
            imageLinkS = data.getString("ImageLink");
        }
        if (data.getString("ImageDebug") != null) {
            imageDebugS = data.getString("ImageDebug");


        }
        if (data.getString("ImageApp") != null) {
            imageAppS = data.getString("ImageApp");
        }
    }

//    "linking lessons to something else make it easy to remember"
//    "Look for similarity and difference between the lesson and the link you came up with"
//    "If you can not come up with an idea you can put any example where the lesson is used"
//    "Write a brief summary of your application"

}
