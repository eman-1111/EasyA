package eman.app.android.easya;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.List;

import eman.app.android.easya.data.CourseContract;
import eman.app.android.easya.fragment.LinkFragment;
import eman.app.android.easya.fragment.SummaryFragment;
import eman.app.android.easya.fragment.ApplyFragment;
import eman.app.android.easya.interfacee.SaveLesson;
import eman.app.android.easya.utils.Constants;
import eman.app.android.easya.utils.Helper;
import me.relex.circleindicator.CircleIndicator;

import static eman.app.android.easya.fragment.CourseListFragment.LOG_TAG;

public class AddNewLesson extends AppCompatActivity implements SaveLesson, LoaderManager.LoaderCallbacks<Cursor> {


    ViewPager viewPager;
    CircleIndicator indicator;
    String courseId;
    ViewPagerAdapter adapter;
    boolean edit = false;
    private Uri mUri;
    Cursor mCursor = null;
    private static final int DETAIL_LOADER = 0;
    String oldLessonName = "";

    private static final String[] DETAIL_COLUMNS = {
            CourseContract.SubjectEntry.COLUMN_COURSE_ID,
            CourseContract.SubjectEntry.COLUMN_LESSON_TITLE,
            CourseContract.SubjectEntry.COLUMN_LESSON_LINK,
            CourseContract.SubjectEntry.COLUMN_LESSON_PRACTICAL_TITLE,
            CourseContract.SubjectEntry.COLUMN_LESSON_PRACTICAL,
            CourseContract.SubjectEntry.COLUMN_LESSON_OUTLINE,
            CourseContract.SubjectEntry.COLUMN_LESSON_DEBUG,
            CourseContract.CourseEntry.COLUMN_COURSE_NAME,
            CourseContract.CourseEntry.COLUMN_TEACHER_NAME,
            CourseContract.SubjectEntry.COLUMN_FAVORITE,
            CourseContract.SubjectEntry.COLUMN_LESSON_OUTLINE_IMAGE,
            CourseContract.SubjectEntry.COLUMN_LESSON_LINK_IMAGE,
            CourseContract.SubjectEntry.COLUMN_LESSON_PRACTICAL_IMAGE
    };


    public static final int COL_COURSE_ID = 0;
    public static final int COL_LESSON_TITLE = 1;
    public static final int COL_LESSON_LINK = 2;
    public static final int COL_LESSON_PRACTICAL_TITLE = 3;
    public static final int COL_LESSON_PRACTICAL = 4;
    public static final int COL_LESSON_OUTLINE = 5;
    public static final int COL_LESSON_DEBUG = 6;

    public static final int COL_COURSE_NAME = 7;
    public static final int COL_TEACHER_NAME = 8;
    public static final int COL_FAVORITE = 9;

    public static final int COL_LESSON_OUTLINE_IMAGE = 10;
    public static final int COL_LESSON_LINK_IMAGE = 11;
    public static final int COL_LESSON_PRACTICAL_IMAGE = 12;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_lesson);

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        indicator = (CircleIndicator) findViewById(R.id.indicator);

        Intent intent = getIntent();
        courseId = intent.getStringExtra("CourseId");
        if (intent.getStringExtra("LessonURL") != null) {
            edit = true;
            Log.e(LOG_TAG, "edit true");
            mUri = Uri.parse(intent.getStringExtra("LessonURL"));
            getSupportLoaderManager().initLoader(DETAIL_LOADER, null, this);

        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setupViewPager(viewPager);
        viewPager.setOffscreenPageLimit(2);
        indicator.setViewPager(viewPager);

    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(this.getSupportFragmentManager());

        adapter.addFragment(new SummaryFragment());
        adapter.addFragment(new LinkFragment());
        adapter.addFragment(new ApplyFragment());

        viewPager.setAdapter(adapter);

    }

    @Override
    public void saveLesson(String id) {

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
            mCursor = data;
            ladEditData();
        }
    }

    private void ladEditData() {
        SummaryFragment.lessonNameET.setText(mCursor.getString(COL_LESSON_TITLE));
        SummaryFragment.lessonOverViewET.setText(mCursor.getString(COL_LESSON_OUTLINE));
        oldLessonName = mCursor.getString(COL_LESSON_TITLE);

        LinkFragment.lessonLink.setText(mCursor.getString(COL_LESSON_LINK));
        LinkFragment.lessonDebug.setText(mCursor.getString(COL_LESSON_DEBUG));

        ApplyFragment.lessonAppTitle = mCursor.getString(COL_LESSON_PRACTICAL_TITLE);
        ApplyFragment.lessonApp = mCursor.getString(COL_LESSON_PRACTICAL);

        byte[] outlineImageB = mCursor.getBlob(COL_LESSON_OUTLINE_IMAGE);
        byte[] linkImageB = mCursor.getBlob(COL_LESSON_LINK_IMAGE);
        byte[] appImageB = mCursor.getBlob(COL_LESSON_PRACTICAL_IMAGE);
        if (outlineImageB != null) {
            SummaryFragment.thumbnail = Helper.getImage(outlineImageB);
            SummaryFragment.outlineImage.setImageBitmap(Helper.getImage(outlineImageB));
        }
        if (linkImageB != null) {
            LinkFragment.imageLink.setImageBitmap(Helper.getImage(linkImageB));
            LinkFragment.thumbnail = Helper.getImage(linkImageB);
        }
        if (appImageB != null) {
            ApplyFragment.thumbnail = Helper.getImage(appImageB);
        }
        //
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment) {
            mFragmentList.add(fragment);
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            SummaryFragment.lessonNameET.addTextChangedListener(new AddNewLesson.MyTextWatcher(SummaryFragment.lessonNameET));
            SummaryFragment.lessonOverViewET.addTextChangedListener(new AddNewLesson.MyTextWatcher(SummaryFragment.lessonOverViewET));

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, SubjectList.class)
                    .setData(CourseContract.SubjectEntry.buildSubjectWithID(courseId));
            startActivity(intent);
            finish();
        } else if (item.getItemId() == R.id.btn_save_menu) {

            if (validateName() && validateOutline()) {
                String title = SummaryFragment.lessonNameET.getText().toString();
                String summary = SummaryFragment.lessonOverViewET.getText().toString();

                String link = LinkFragment.lessonLink.getText().toString();
                String debug = LinkFragment.lessonDebug.getText().toString();

                String appTitle = ApplyFragment.lessonLifeAppTitle.getText().toString();
                String appSummary = ApplyFragment.lessonLifeApp.getText().toString();


                Bitmap outlineImage = SummaryFragment.thumbnail;
                Bitmap imageLink = LinkFragment.thumbnail;
                Bitmap imageApp = ApplyFragment.thumbnail;
                byte[] outlineImage_, imageLink_, imageApp_;
                if (outlineImage != null) {
                    outlineImage_ = Helper.getBytes(outlineImage);
                } else {
                    outlineImage_ = null;
                }
                if (imageLink != null) {
                    imageLink_ = Helper.getBytes(imageLink);
                } else {
                    imageLink_ = null;
                }
                if (imageApp != null) {
                    imageApp_ = Helper.getBytes(imageApp);
                } else {
                    imageApp_ = null;
                }

                addLessonData(courseId, title, summary, outlineImage_,
                        link, imageLink_, appTitle,
                        appSummary, imageApp_, debug);
            }


            Log.e("AddLesson", "Clicked");

        }
        return super.onOptionsItemSelected(item);
    }

    public class MyTextWatcher implements TextWatcher {

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

    /**
     * Helper method to handle insertion of a new location in the weather database.
     *
     * @param courseId           .
     * @param lessonLink         .
     * @param lessonLinkImage    .
     * @param lessonDebugs       .
     * @param lessonLifeAppTitle .
     * @param lessonLifeApp      .
     * @param lessonAppImage
     */
    void addLessonData(String courseId, String lessonName, String lessonOutline, byte[] lessonOutlineImage,
                       String lessonLink, byte[] lessonLinkImage, String lessonLifeAppTitle,
                       String lessonLifeApp, byte[] lessonAppImage, String lessonDebugs) {

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


        if (edit) {

          //  int lessonId =
            Log.e(LOG_TAG, "mURI: "+ mUri);
            this.getContentResolver().update(CourseContract.SubjectEntry.buildSubjectsUri(),
                    courseValues,
                    CourseContract.SubjectEntry.TABLE_NAME +
                            "." + CourseContract.SubjectEntry.COLUMN_LESSON_TITLE + " = ? ",
                    new String[]{oldLessonName});

        } else {
            courseValues.put(CourseContract.SubjectEntry.COLUMN_FAVORITE, "0");
            //  Log.e("Key Subject", userKey);

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
        clearSavedData();
        Intent intent = new Intent(this, SubjectList.class)
                .setData(CourseContract.SubjectEntry.buildSubjectWithID(courseId));
        startActivity(intent);
        finish();

    }

    private void clearSavedData() {
        ApplyFragment.lessonAppTitle = "";
        ApplyFragment.lessonApp = "";


        SummaryFragment.thumbnail = null;
        LinkFragment.thumbnail = null;
        ApplyFragment.thumbnail = null;
    }

    private boolean validateName() {
        if (SummaryFragment.lessonNameET.getText().toString().trim().isEmpty()) {
            SummaryFragment.inputLayoutName.setError("Enter Lesson Name");
            requestFocus(SummaryFragment.lessonNameET);
            return false;
        } else {
            SummaryFragment.inputLayoutName.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateOutline() {
        if (SummaryFragment.lessonOverViewET.getText().toString().trim().isEmpty()) {
            SummaryFragment.inputLayoutOutline.setError("Enter lesson outline");
            requestFocus(SummaryFragment.lessonOverViewET);
            return false;
        } else {
            SummaryFragment.inputLayoutOutline.setErrorEnabled(false);
        }

        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }


}

