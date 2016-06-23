package eman.app.android.easya;


import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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

import eman.app.android.easya.data.CourseContract;

public class AddLessonDetail extends AppCompatActivity implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    String lessonId;
    boolean edit = false;
    TextInputLayout inputLayoutLink, inputLayoutDebug, inputLayoutAppTitle, inputLayoutApp;
    EditText lessonLink, lessonDebug, lessonLifeAppTitle, lessonLifeApp;
    ImageView infoLink, infoDebug, infoTitle, infoApp;
    ImageView imageLink, imageDebug, imageApp;
    String imageLinkS ="l", imageDebugS = "l", imageAppS = "l";
    String lessonLinks, lessonDebugs,lessonLifeAppTitles,lessonLifeApps;


    private Uri mUri;
    private static final int DETAIL_LOADER = 0;
    private static final String[] DETAIL_COLUMNS = {
            CourseContract.SubjectEntry.COLUMN_COURSE_ID,
            CourseContract.SubjectEntry.COLUMN_LESSON_LINK,
            CourseContract.SubjectEntry.COLUMN_LESSON_PRACTICAL_TITLE,
            CourseContract.SubjectEntry.COLUMN_LESSON_PRACTICAL,
            CourseContract.SubjectEntry.COLUMN_LESSON_DEBUG,
            CourseContract.CourseEntry.COLUMN_COURSE_NAME
    };


    public static final int COL_COURSE_ID = 0;
    public static final int COL_LESSON_LINK = 1;
    public static final int COL_LESSON_PRACTICAL_TITLE = 2;
    public static final int COL_LESSON_PRACTICAL = 3;
    public static final int COL_LESSON_DEBUG = 4;
    public static final int COL_COURSE_NAME = 5;


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
        lessonId = intent.getStringExtra("CourseId");
        if(intent.getStringExtra("SavedStatue") != null){
            getSavedInstanceState();
        }
        if (intent.getStringExtra("LessonURL") != null) {
            mUri = Uri.parse(intent.getStringExtra("LessonURL"));
            edit = true;
            getSupportLoaderManager().initLoader(DETAIL_LOADER, null, this);
        }

        if (intent.getStringExtra("linkImage") != null) {
            imageLinkS = intent.getStringExtra("linkImage");
        } else if (intent.getStringExtra("debugImage") != null) {
            imageDebugS = intent.getStringExtra("debugImage");
        } else if (intent.getStringExtra("appImage") != null) {
            imageAppS = intent.getStringExtra("appImage");
        }

        Picasso.with(this).load(imageLinkS).error(R.drawable.air_plan)
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

                addLessonData(lessonId, lessonLinks, imageLinkS,
                        lessonLifeAppTitles, lessonLifeApps, imageAppS,
                        lessonDebugs, imageDebugS);
                Intent intent = new Intent(AddLessonDetail.this, SubjectList.class)
                        .setData(CourseContract.SubjectEntry.buildSubjectWithID(lessonId));
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
        Intent intent = new Intent(this, ImageSearch.class);
        intent.putExtra("imageName", extra);
        intent.putExtra("CourseId", lessonId);
        setSavedInstanceState();
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, AddSubjectTitel.class);
            intent.putExtra("CourseId", lessonId);
            intent.putExtra("SavedStatue", "back");
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Helper method to handle insertion of a new location in the weather database.
     *
     * @param lessonId           .
     * @param lessonLink         .
     * @param lessonLinkImage    .
     * @param lessonDebugs       .
     * @param lessonDebugsImage
     * @param lessonLifeAppTitle .
     * @param lessonLifeApp      .
     * @param lessonAppImage
     */
    void addLessonData(String lessonId, String lessonLink, String lessonLinkImage,
                       String lessonLifeAppTitle, String lessonLifeApp, String lessonAppImage,
                       String lessonDebugs, String lessonDebugsImage) {


        ContentValues courseValues = new ContentValues();

        courseValues.put(CourseContract.SubjectEntry.COLUMN_COURSE_ID, lessonId);
        courseValues.put(CourseContract.SubjectEntry.COLUMN_LESSON_LINK, lessonLink);
        courseValues.put(CourseContract.SubjectEntry.COLUMN_LESSON_LINK_IMAGE, lessonLinkImage);
        courseValues.put(CourseContract.SubjectEntry.COLUMN_LESSON_PRACTICAL_TITLE, lessonLifeAppTitle);
        courseValues.put(CourseContract.SubjectEntry.COLUMN_LESSON_PRACTICAL, lessonLifeApp);
        courseValues.put(CourseContract.SubjectEntry.COLUMN_LESSON_PRACTICAL_IMAGE, lessonAppImage);
        courseValues.put(CourseContract.SubjectEntry.COLUMN_LESSON_DEBUG, lessonDebugs);
        courseValues.put(CourseContract.SubjectEntry.COLUMN_LESSON_DEBUG_IMAGE, lessonDebugsImage);


        this.getContentResolver().update(CourseContract.SubjectEntry.buildSubjectsUri(),
                courseValues,
                CourseContract.SubjectEntry.TABLE_NAME +
                        "." + CourseContract.SubjectEntry.COLUMN_COURSE_ID + " = ? ",
                new String[]{lessonId});
        // Wait, that worked?  Yes!
        Intent intent = new Intent(this, SubjectList.class);
        startActivity(intent);

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
        }
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is

        savedInstanceState.putString("ImageLink", imageLinkS);
        savedInstanceState.putString("ImageDebug", imageDebugS);
        savedInstanceState.putString("ImageApp", imageAppS);

        savedInstanceState.putString("LessonId", lessonId);

        savedInstanceState.putString("LessonLink", lessonLinks);
        savedInstanceState.putString("LessonDebug", lessonDebugs);
        savedInstanceState.putString("LessonLifeAppTitle", lessonLifeAppTitles);
        savedInstanceState.putString("LessonLifeApp", lessonLifeApps);
        // etc.
    }
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore UI state from the savedInstanceState.
        // This bundle has also been passed to onCreate.
        imageLinkS = savedInstanceState.getString("ImageLink");
        imageDebugS = savedInstanceState.getString("ImageDebug");
        imageAppS = savedInstanceState.getString("ImageApp");

        lessonId = savedInstanceState.getString("LessonId");

        lessonLinks = savedInstanceState.getString("LessonLink");
        lessonDebugs = savedInstanceState.getString("LessonDebug");
        lessonLifeAppTitles = savedInstanceState.getString("LessonLifeAppTitle");
        lessonLifeApps = savedInstanceState.getString("LessonLifeApp");
    }

    public void setSavedInstanceState()
    {
        // Store values between instances here
        SharedPreferences.Editor savedInstanceState = getPreferences(MODE_PRIVATE).edit();

        savedInstanceState.putString("ImageLink", imageLinkS);
        savedInstanceState.putString("ImageDebug", imageDebugS);
        savedInstanceState.putString("ImageApp", imageAppS);

        savedInstanceState.putString("LessonId", lessonId);

        savedInstanceState.putString("LessonLink", lessonLinks);
        savedInstanceState.putString("LessonDebug", lessonDebugs);
        savedInstanceState.putString("LessonLifeAppTitle", lessonLifeAppTitles);
        savedInstanceState.putString("LessonLifeApp", lessonLifeApps);
        // Commit to storage
        savedInstanceState.commit();
    }


    public void getSavedInstanceState() {
        SharedPreferences savedInstanceState = getPreferences(MODE_PRIVATE);
        lessonId = savedInstanceState.getString("LessonId", "No name defined");

        imageLinkS = savedInstanceState.getString("ImageLink", "No name defined");
        imageDebugS = savedInstanceState.getString("ImageDebug", "No name defined");
        imageAppS = savedInstanceState.getString("ImageApp", "No name defined");


        lessonLinks = savedInstanceState.getString("LessonLink", "No name defined");
        lessonDebugs = savedInstanceState.getString("LessonDebug", "No name defined");
        lessonLifeAppTitles = savedInstanceState.getString("LessonLifeAppTitle", "No name defined");
        lessonLifeApps = savedInstanceState.getString("LessonLifeApp", "No name defined");

        Picasso.with(this).load(imageLinkS).error(R.drawable.air_plan)
                .into(imageLink);

        Picasso.with(this).load(imageDebugS).error(R.drawable.air_plan)
                .into(imageDebug);

        Picasso.with(this).load(imageAppS).error(R.drawable.air_plan)
                .into(imageApp);

        lessonLink.setText(lessonLinks);
        lessonDebug.setText(lessonDebugs);
        lessonLifeAppTitle.setText(lessonLifeAppTitles);
        lessonLifeApp.setText(lessonLifeApps);
    }

    //    "linking lessons to something else make it easy to remember"
//            "Look for similarity and difference between the lesson and the link you came up with"
//            "If you can not come up with an idea you can put any example where the lesson is used"
//            "Write a brief summary of your application"

}
