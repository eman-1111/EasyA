package eman.app.android.easya;


import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;


import com.squareup.picasso.Picasso;

import eman.app.android.easya.data.CourseContract;


public class AddSubjectTitel extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    EditText lessonNameET, lessonOverViewET;
    ImageView outlineImage;
    TextInputLayout inputLayoutName, inputLayoutOutline;

    String lessonId, lessonOverView, lessonName,lessonNameOld;
    String imageUrl = "l";


    boolean edit = false;
    private Uri mUri;
    private static final int DETAIL_LOADER = 0;

    private static final String[] DETAIL_COLUMNS = {
            CourseContract.SubjectEntry.COLUMN_COURSE_ID,
            CourseContract.SubjectEntry.COLUMN_LESSON_TITLE,
            CourseContract.SubjectEntry.COLUMN_LESSON_OUTLINE,
            CourseContract.CourseEntry.COLUMN_COURSE_NAME,
            CourseContract.SubjectEntry.COLUMN_LESSON_OUTLINE_IMAGE
    };


    public static final int COL_COURSE_ID = 0;
    public static final int COL_LESSON_TITLE = 1;
    public static final int COL_LESSON_OUTLINE = 2;
    public static final int COL_COURSE_NAME = 3;
    public static final int COL_OUTLINE_IMAGE = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_subject_titel);
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

        Intent intent = getIntent();
        setUpID();

        if(intent.getStringExtra("SavedStatue") != null){
            getSavedInstanceState();

        }

        lessonId = intent.getStringExtra("CourseId");
        if (intent.getStringExtra("LessonURL") != null) {
            edit = true;
            mUri = Uri.parse(intent.getStringExtra("LessonURL"));
            getSupportLoaderManager().initLoader(DETAIL_LOADER, null, this);
        }

        if (intent.getStringExtra("outlineImage") != null) {
            imageUrl = intent.getStringExtra("outlineImage");
        }

        Picasso.with(this).load(imageUrl).error(R.drawable.air_plan)
                .into(outlineImage);


        lessonNameET.addTextChangedListener(new MyTextWatcher(lessonNameET));
        lessonOverViewET.addTextChangedListener(new MyTextWatcher(lessonOverViewET));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.next_btn);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lessonName = lessonNameET.getText().toString();
                lessonOverView = lessonOverViewET.getText().toString();
                startDetailIntent();
            }

        });

        outlineImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startImageIntent();
            }

        });


    }

    private void setUpID() {
        inputLayoutName = (TextInputLayout) findViewById(R.id.input_layout_name);
        inputLayoutOutline = (TextInputLayout) findViewById(R.id.input_layout_outline);

        lessonNameET = (EditText) findViewById(R.id.lesson_name_et);
        lessonOverViewET = (EditText) findViewById(R.id.lesson_outline_et);
        outlineImage = (ImageView) findViewById(R.id.outline_image);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, SubjectList.class)
                    .setData(CourseContract.SubjectEntry.buildSubjectWithID(lessonId));
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void startDetailIntent() {

        if (!validateName()) {
            return;
        }

        if (!validateOutline()) {
            return;
        }
        Intent intent = new Intent(this, AddLessonDetail.class);

        if (edit) {
            intent.putExtra("LessonURL", mUri.toString());

        }
        intent.putExtra("CourseId", lessonId);
        addLessonData(lessonId, lessonName, lessonOverView, imageUrl);
        setSavedInstanceState();
        startActivity(intent);
    }

    public void startImageIntent() {
        Intent intent = new Intent(this, ImageSearch.class);
        intent.putExtra("SearchValue", lessonNameET.getText().toString());
        intent.putExtra("CourseId", lessonId);
        intent.putExtra("imageName", "outlineImage");
        setSavedInstanceState();
        startActivity(intent);
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.lesson_name_et:
                    validateName();
                    break;
                case R.id.lesson_outline_et:
                    validateOutline();
                    break;

            }
        }
    }

    private boolean validateName() {
        if (lessonNameET.getText().toString().trim().isEmpty()) {
            inputLayoutName.setError("Enter Lesson Name");
            requestFocus(lessonNameET);
            return false;
        } else {
            inputLayoutName.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateOutline() {
        if (lessonOverViewET.getText().toString().trim().isEmpty()) {
            inputLayoutOutline.setError("Enter lesson outline");
            requestFocus(lessonOverViewET);
            return false;
        } else {
            inputLayoutOutline.setErrorEnabled(false);
        }

        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    /**
     * Helper method to handle insertion of a new location in the weather database.
     *
     * @param lessonId            .
     * @param lessonName          .
     * @param lessonOverview      .
     * @param lessonOverviewImage .
     */
    void addLessonData(String lessonId, String lessonName,
                       String lessonOverview, String lessonOverviewImage) {

        long courseID;

        Cursor courseCursor = this.getContentResolver().query(
                CourseContract.SubjectEntry.CONTENT_URI,
                new String[]{CourseContract.SubjectEntry._ID},
                CourseContract.SubjectEntry.COLUMN_LESSON_TITLE + " = ?",
                new String[]{lessonName},
                null);

        if (edit == true) {
            ContentValues courseValues = new ContentValues();

            courseValues.put(CourseContract.SubjectEntry.COLUMN_COURSE_ID, lessonId);
            courseValues.put(CourseContract.SubjectEntry.COLUMN_LESSON_TITLE, lessonName);
            courseValues.put(CourseContract.SubjectEntry.COLUMN_LESSON_OUTLINE, lessonOverview);
            courseValues.put(CourseContract.SubjectEntry.COLUMN_LESSON_OUTLINE_IMAGE, lessonOverviewImage);


            this.getContentResolver().update(CourseContract.SubjectEntry.buildSubjectsUri(),
                    courseValues,
                    CourseContract.SubjectEntry.TABLE_NAME +
                            "." + CourseContract.SubjectEntry.COLUMN_LESSON_TITLE + " = ? ",
                    new String[]{lessonNameOld});
        } else {

            ContentValues courseValues = new ContentValues();

            courseValues.put(CourseContract.SubjectEntry.COLUMN_COURSE_ID, lessonId);
            courseValues.put(CourseContract.SubjectEntry.COLUMN_LESSON_TITLE, lessonName);
            courseValues.put(CourseContract.SubjectEntry.COLUMN_LESSON_OUTLINE, lessonOverview);
            courseValues.put(CourseContract.SubjectEntry.COLUMN_LESSON_OUTLINE_IMAGE, lessonOverviewImage);

            courseValues.put(CourseContract.SubjectEntry.COLUMN_FAVORITE, 0);

            // Finally, insert Course data into the database.
            Uri insertedUri = this.getContentResolver().insert(
                    CourseContract.SubjectEntry.CONTENT_URI,
                    courseValues);

            // The resulting URI contains the ID for the row.  Extract the locationId from the Uri.
            courseID = ContentUris.parseId(insertedUri);
        }

        courseCursor.close();
        // Wait, that worked?  Yes!
        Intent intent = new Intent(this, SubjectList.class);
        /// intent.putExtra("CourseName", lessonName);
        startActivity(intent);

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

            Picasso.with(this).load(data.getString(COL_OUTLINE_IMAGE)).error(R.drawable.air_plan)
                    .into(outlineImage);
            lessonId =  data.getString(COL_COURSE_ID);
            lessonNameOld = data.getString(COL_LESSON_TITLE);
            lessonNameET.setText(lessonNameOld);
            lessonOverViewET.setText(data.getString(COL_LESSON_OUTLINE));
        }
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


    public void setSavedInstanceState()
    {
        // Store values between instances here
        SharedPreferences.Editor savedInstanceState = getPreferences(MODE_PRIVATE).edit();

        savedInstanceState.putString("LessonId", lessonId);

        savedInstanceState.putString("LessonOverView", lessonOverView);
        savedInstanceState.putString("LessonName", lessonName);
        savedInstanceState.putString("ImageUrl", imageUrl);

        savedInstanceState.commit();
    }


    public void getSavedInstanceState() {
        SharedPreferences savedInstanceState = getPreferences(MODE_PRIVATE);
        lessonId = savedInstanceState.getString("LessonId", "No name defined");

        lessonOverView = savedInstanceState.getString("LessonOverView", "No name defined");
        lessonName = savedInstanceState.getString("LessonName", "No name defined");
        imageUrl = savedInstanceState.getString("ImageUrl", "No name defined");

        lessonNameET.setText(lessonName);
        lessonOverViewET.setText(lessonOverView);
        Picasso.with(this).load(imageUrl).error(R.drawable.air_plan)
                .into(outlineImage);

    }



}
