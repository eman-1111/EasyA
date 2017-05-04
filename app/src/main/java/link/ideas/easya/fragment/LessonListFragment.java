package link.ideas.easya.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ListView;
import android.widget.TextView;


import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter;
import link.ideas.easya.R;
import link.ideas.easya.adapter.LessonAdapter;
import link.ideas.easya.data.CourseContract;


/**
 * A placeholder fragment containing a simple view.
 */
public class LessonListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = LessonListFragment.class.getSimpleName();
    public static final String SUBJECT_URI = "URIS";
    private Uri mUri;
    TextView emptyView;

    public LessonListFragment() {
    }


    public interface Callback {
        public void onItemSelected(Uri idUri, LessonAdapter.SubjectAdapterViewHolder vh);
    }

    // RecyclerView mRecyclerView;
    RecyclerView mRecyclerView;
    private LessonAdapter mSubjectAdapter;
    private int mPosition = RecyclerView.NO_POSITION;


    private static final String SELECTED_KEY = "selected_position";
    private static final int COURSE_LOADER = 1;

    private static final String[] SUBJECT_COLUMNS = {

            CourseContract.SubjectEntry.COLUMN_COURSE_ID,
            CourseContract.SubjectEntry.COLUMN_LESSON_TITLE,
            CourseContract.SubjectEntry.COLUMN_LESSON_LINK,
            CourseContract.CourseEntry.COLUMN_COURSE_NAME,
            CourseContract.SubjectEntry.COLUMN_LESSON_OUTLINE_IMAGE
    };

    public static final int COL_COURSE_ID = 0;
    public static final int CO_LESSON_TITLE = 1;
    public static final int COL_LESSON_LINK = 2;
    public static final int COL_COURSE_NAME = 3;
    public static final int COL_LESSON_OUTLINE_IMAGE = 4;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(LessonListFragment.SUBJECT_URI);
        }

        View rootView = inflater.inflate(R.layout.fragment_subject_list, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview_lesson);
        emptyView = (TextView) rootView.findViewById(R.id.empty_tv);

        // Set the layout manager
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mRecyclerView.setHasFixedSize(true);

        mSubjectAdapter = new LessonAdapter(getActivity(), new LessonAdapter.SubjectAdapterOnClickHolder() {
            @Override
            public void onClick(String id, String lessonTitle, LessonAdapter.SubjectAdapterViewHolder vh) {
                Uri uri = CourseContract.SubjectEntry.buildCourseWithIDAndTitle(id, lessonTitle);
                ((Callback) getActivity())
                        .onItemSelected(uri, vh);

                mPosition = vh.getAdapterPosition();
            }

            @Override
            public boolean onLongClick(final String lessonId, final String lessonName) {
                AlertDialog.Builder builder2 = new AlertDialog.Builder(getActivity());
                builder2.setMessage("Are you sure you want to delete this Lesson ");
                builder2.setPositiveButton("Delete",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                getContext().getContentResolver().delete(CourseContract.SubjectEntry.CONTENT_URI,
                                        CourseContract.SubjectEntry.COLUMN_COURSE_ID + " = ?",
                                        new String[]{lessonId});
                                removeItem(lessonId);
                            }
                        });
                builder2.setNegativeButton("Cancel", null);
                builder2.show();
                return false;
            }
        });
        SlideInBottomAnimationAdapter alphaAdapter = new SlideInBottomAnimationAdapter(mSubjectAdapter);
        alphaAdapter.setFirstOnly(false);
        mRecyclerView.setAdapter(mSubjectAdapter);
        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            // The listview probably hasn't even been populated yet.  Actually perform the
            // swapout in onLoadFinished.
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }
        return rootView;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        // When tablets rotate, the currently selected list item needs to be saved.
        // When no item is selected, mPosition will be set to Listview.INVALID_POSITION,
        // so check for that before storing.
        if (mPosition != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(COURSE_LOADER, null, this);

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {


        if (null != mUri) {

            String sortOrder;
            if (LessonAdapter.getSortBy(getActivity()).equals("date")) {
                sortOrder = null;
            } else {
                sortOrder = CourseContract.SubjectEntry.COLUMN_FAVORITE + " DESC";
            }

            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    SUBJECT_COLUMNS,
                    null,
                    null,
                    sortOrder
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mSubjectAdapter.swapCursor(data);

        if (data != null && data.moveToFirst()) {
            getActivity().setTitle(data.getString(LessonListFragment.COL_COURSE_NAME));
            emptyView.setText("");

        } else {
            emptyView.setText("The lesson you will add, will show up here, so add some");
        }
        if (mPosition != ListView.INVALID_POSITION) {
            // Ifp we don't need to restart the loader, and there's a desired position to restore
            // to, do so now.
            mRecyclerView.smoothScrollToPosition(mPosition);

        }
        startIntroAnimation();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mSubjectAdapter.swapCursor(null);

    }

    private void removeItem(String itemId) {

    }



    private void startIntroAnimation() {
        mRecyclerView.setTranslationY( getResources().getDimensionPixelSize(R.dimen.list_item_lesson));
        mRecyclerView.setAlpha(0f);
        mRecyclerView.animate()
                .translationY(0)
                .setDuration(500)
                .alpha(1f)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();
    }

    private void scheduleStartPostponedTransition(final View sharedElement) {
        sharedElement.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        sharedElement.getViewTreeObserver().removeOnPreDrawListener(this);
                        startPostponedEnterTransition();
                        return true;
                    }
                });
    }
}

