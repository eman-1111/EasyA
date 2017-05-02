package link.ideas.easya.fragment;


        import android.content.Context;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.database.Cursor;
        import android.net.Uri;
        import android.os.Bundle;
        import android.support.v4.app.Fragment;
        import android.support.v4.app.LoaderManager;
        import android.support.v4.content.CursorLoader;
        import android.support.v4.content.Loader;
        import android.support.v7.app.AlertDialog;
        import android.support.v7.widget.LinearLayoutManager;
        import android.support.v7.widget.RecyclerView;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ListView;
        import android.widget.TextView;


        import link.ideas.easya.CoursesList;
        import link.ideas.easya.adapter.CourseAdapter;
        import link.ideas.easya.R;
        import link.ideas.easya.data.CourseContract;
        import link.ideas.easya.utils.Constants;
        import link.ideas.easya.utils.Helper;

/**
 * Created by eman_ashour on 4/21/2016.
 */
public class CourseListFragment  extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    public static final String LOG_TAG = CourseListFragment.class.getSimpleName();
    TextView emptyView;

    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(Uri idUri, String courseName);
    }
    RecyclerView mRecyclerView;
    private CourseAdapter mCourseAdapter;
    private int mPosition = RecyclerView.NO_POSITION;


    private static final String SELECTED_KEY = "selected_position";
    private static final int COURSE_LOADER = 0;

    private static final String[] COURSES_COLUMNS = {

            CourseContract.CourseEntry.COLUMN_COURSE_ID,
            CourseContract.CourseEntry.COLUMN_COURSE_NAME,
            CourseContract.CourseEntry.COLUMN_TEACHER_NAME,
            CourseContract.CourseEntry.COLUMN_TEACHER_PHOTO_URL,
            CourseContract.CourseEntry.COLUMN_TEACHER_COLOR
    };

    public static final int COL_COURSE_ID = 0;
    public static final int COL_COURSE_NAME = 1;
    public static final int COL_TEACHER_NAME = 2;
    public static final int COL_TEACHER_PHOTO_URL = 3;
    public static final int COL_TEACHER_COLOR = 4;


    public CourseListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_course_list, container, false);
        // Get a reference to the RecyclerView, and attach this adapter to it.
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview);

        // Set the layout manager
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mRecyclerView.setHasFixedSize(true);
        emptyView = (TextView)rootView.findViewById(R.id.empty_tv);
        mCourseAdapter = new CourseAdapter(getActivity(), new CourseAdapter.CourseAdapterOnClickHolder(){
            @Override
            public void onClick(String id , String courseName, CourseAdapter.CourseAdapterViewHolder vh) {
                ((Callback) getActivity())
                        .onItemSelected(CourseContract.SubjectEntry.buildSubjectWithID(id),
                                courseName);
                mPosition = vh.getAdapterPosition();
            }

            @Override
            public boolean onLongClick(final String courseId) {
                AlertDialog.Builder builder2 = new AlertDialog.Builder(getActivity());
                builder2.setMessage("Are you sure you want to delete this Course ");
                builder2.setPositiveButton("Delete",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                getContext().getContentResolver().delete(CourseContract.CourseEntry.CONTENT_URI,
                                        CourseContract.CourseEntry.COLUMN_COURSE_ID + " = ?",
                                        new String[]{courseId});
                                getContext().getContentResolver().delete(CourseContract.SubjectEntry.CONTENT_URI,
                                        CourseContract.SubjectEntry.COLUMN_COURSE_ID + " = ?",
                                        new String[]{courseId});
                                removeItem(courseId);
                                Helper.updateWidgets(getContext());


                            }
                        });
                builder2.setNegativeButton("Cancel", null);
                builder2.show();
                return false;
            }

        });
        mRecyclerView.setAdapter(mCourseAdapter);

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            // The listview probably hasn't even been populated yet.  Actually perform the
            // swapout in onLoadFinished.
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }
        return rootView;
    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(COURSE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                CourseContract.CourseEntry.CONTENT_URI,
                COURSES_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCourseAdapter.swapCursor(data);
        if (data != null && data.moveToFirst()) {
            emptyView.setText("");
        }else{
            emptyView.setText(getResources().getString(R.string.no_course));
        }


        if (mPosition != ListView.INVALID_POSITION) {
            // Ifp we don't need to restart the loader, and there's a desired position to restore
            // to, do so now.
            mRecyclerView.smoothScrollToPosition(mPosition);

        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mCourseAdapter.swapCursor(null);

    }
    private void removeItem(String itemId) {

    }



}
