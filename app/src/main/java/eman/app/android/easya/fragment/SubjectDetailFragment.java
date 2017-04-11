package eman.app.android.easya.fragment;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import eman.app.android.easya.AddNewLesson;
import eman.app.android.easya.R;
import eman.app.android.easya.data.CourseContract;
import eman.app.android.easya.utils.Helper;

/**
 * A placeholder fragment containing a simple view.
 */
public class SubjectDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {


    private static final String LOG_TAG = SubjectDetailFragment.class.getSimpleName();
    public static final String DETAIL_URI = "URI";

    private Menu menu;

    ImageView outlineImage, linkImage, appImage;
    String teacherEmail, lessonName, lessonOutline, lessonLink,
            lessonDebug, lessonPracticalTitle, lessonPractical;


    private ShareActionProvider mShareActionProvider;
    private Uri mUri;
    private static final int DETAIL_LOADER = 0;

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
    int favorite = 0;

    private TextView mLessonLink, mLessonDebug, mLessonPracticalTitle, mLessonPractical,
            mLessonOutline, mLink, mDebug;

    CollapsingToolbarLayout collapsingToolbar;

    public SubjectDetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(SubjectDetailFragment.DETAIL_URI);
        }

        View rootView = inflater.inflate(R.layout.fragment_subject_detail, container, false);
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbars);


        // Set Collapsing Toolbar layout to the screen
        collapsingToolbar =
                (CollapsingToolbarLayout) rootView.findViewById(R.id.collapsing_toolbar);
        setUpIds(rootView);


        return rootView;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.menu = menu;
        inflater.inflate(R.menu.menu_subject_detail, menu);
        if (favorite == 1) {
            MenuItem favItem = menu.findItem(R.id.action_favorite);
            favItem.setIcon(getResources().getDrawable(R.drawable.ic_favorite_white_24dp));
        }
        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);
        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        // If onLoadFinished happens before this, we can go ahead and set the share intent now.
        if (teacherEmail != null) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_edit) {
            Intent intent = new Intent(getActivity(), AddNewLesson.class);
            intent.putExtra("LessonURL", mUri.toString());
            intent.putExtra("CourseId", CourseContract.SubjectEntry.getSubjectIdFromUri(mUri));

            startActivity(intent);
        } else if (id == R.id.action_favorite) {

            MenuItem favItem = menu.findItem(R.id.action_favorite);
            if (favorite == 0) {
                favItem.setIcon(getResources().getDrawable(R.drawable.ic_favorite_white_24dp));
                ContentValues favorite = new ContentValues();
                favorite.put(CourseContract.SubjectEntry.COLUMN_FAVORITE, "1");
                getActivity().getContentResolver().update(CourseContract.SubjectEntry.buildSubjectsUri(),
                        favorite,
                        CourseContract.SubjectEntry.TABLE_NAME +
                                "." + CourseContract.SubjectEntry.COLUMN_LESSON_TITLE + " = ? ",
                        new String[]{CourseContract.SubjectEntry.getSubjectTitleFromUri(mUri)});
            } else {
                favItem.setIcon(getResources().getDrawable(R.drawable.ic_favorite_border_white_24dp));
                ContentValues favorite = new ContentValues();
                favorite.put(CourseContract.SubjectEntry.COLUMN_FAVORITE, "0");

                getActivity().getContentResolver().update(CourseContract.SubjectEntry.buildSubjectsUri(),
                        favorite,
                        CourseContract.SubjectEntry.TABLE_NAME +
                                "." + CourseContract.SubjectEntry.COLUMN_LESSON_TITLE + " = ? ",
                        new String[]{CourseContract.SubjectEntry.getSubjectTitleFromUri(mUri)});
            }


        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        if (null != mUri) {
            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            return new CursorLoader(
                    getActivity(),
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

            lessonName = data.getString(COL_LESSON_TITLE);
            collapsingToolbar.setTitle(lessonName);
            lessonLink = data.getString(COL_LESSON_LINK);
            mLessonLink.setText(lessonLink);
            if (mLessonLink.equals("")) {
                mLink.setText("");
            }

            lessonDebug = data.getString(COL_LESSON_DEBUG);
            if (lessonDebug.equals("")) {
                mDebug.setText("");
            }
            mLessonDebug.setText(lessonDebug);

            lessonPracticalTitle = data.getString(COL_LESSON_PRACTICAL_TITLE);
            mLessonPracticalTitle.setText(lessonPracticalTitle);

            lessonPractical = data.getString(COL_LESSON_PRACTICAL);
            mLessonPractical.setText(lessonPractical);

            lessonOutline = data.getString(COL_LESSON_OUTLINE);
            mLessonOutline.setText(lessonOutline);

            teacherEmail = data.getString(COL_LESSON_OUTLINE);


            byte[] outlineImageB = data.getBlob(COL_LESSON_OUTLINE_IMAGE);
            byte[] linkImageB = data.getBlob(COL_LESSON_LINK_IMAGE);
            byte[] appImageB = data.getBlob(COL_LESSON_PRACTICAL_IMAGE);
            if (outlineImageB != null)
                outlineImage.setImageBitmap(Helper.getImage(outlineImageB));
            if (linkImageB != null)
                linkImage.setImageBitmap(Helper.getImage(linkImageB));
            if (appImageB != null)
                appImage.setImageBitmap(Helper.getImage(appImageB));


            favorite = Integer.parseInt(data.getString(COL_FAVORITE));

        }
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private Intent createShareForecastIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_EMAIL, teacherEmail);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, lessonName);
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Link \n" + "   " + lessonLink + "\n" +
                "\n Debug \n" + "   " + lessonDebug + "\n" +
                "\n" + lessonPracticalTitle + "\n" + "   " + lessonPractical);
        return shareIntent;
    }

    private void setUpIds(View rootView) {
        mLessonLink = (TextView) rootView.findViewById(R.id.lesson_linkx_content_tv);
        mLink = (TextView) rootView.findViewById(R.id.lesson_linkx_d_tv);
        mLessonPracticalTitle = (TextView) rootView.findViewById(R.id.lesson_app_title_d_tv);
        mLessonPractical = (TextView) rootView.findViewById(R.id.lesson_app_content_tv);
        mLessonDebug = (TextView) rootView.findViewById(R.id.lesson_debug_content_tv);
        mDebug = (TextView) rootView.findViewById(R.id.lesson_debugx_title_tv);
        mLessonOutline = (TextView) rootView.findViewById(R.id.lesson_overview_content_tv);

        linkImage = (ImageView) rootView.findViewById(R.id.link_iv);
        appImage = (ImageView) rootView.findViewById(R.id.app_iv);
        outlineImage = (ImageView) rootView.findViewById(R.id.outlook_iv);

    }
}
