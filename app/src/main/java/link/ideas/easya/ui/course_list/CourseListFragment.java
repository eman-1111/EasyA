package link.ideas.easya.ui.course_list;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;
import java.util.Random;

import link.ideas.easya.R;
import link.ideas.easya.data.database.Course;
import link.ideas.easya.utils.Constants;
import link.ideas.easya.utils.Helper;
import link.ideas.easya.utils.InjectorUtils;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by eman_ashour on 4/21/2016.
 */
//todo remove the dialog and firebase references
public class CourseListFragment extends Fragment {

    public static final String LOG_TAG = CourseListFragment.class.getSimpleName();
    TextView emptyView;
    boolean isLoaded;
    CourseListViewModel mViewModel;
    View rootView;

    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(int  courseId, String courseName);
    }

    RecyclerView mRecyclerView;
    private CourseAdapter mCourseAdapter;
    private int mPosition = RecyclerView.NO_POSITION;


    private static final String SELECTED_KEY = "selected_position";


    public CourseListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_add, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.btn_add_menu) {
            startDialog();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        rootView = inflater.inflate(R.layout.fragment_course_list, container, false);
        // Get a reference to the RecyclerView, and attach this adapter to it.
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview);

        // Set the layout manager
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mRecyclerView.setHasFixedSize(true);
        emptyView = (TextView) rootView.findViewById(R.id.empty_tv);

        CourseListModelFactory factory = InjectorUtils.provideMainActivityViewModelFactory(getContext());
        mViewModel = ViewModelProviders.of(this, factory).get(CourseListViewModel.class);


        mCourseAdapter = new CourseAdapter(getActivity(), new CourseAdapter.CourseAdapterOnClickHolder() {
            @Override
            public void onClick(int id, String courseName, CourseAdapter.CourseAdapterViewHolder vh) {
                ((Callback) getActivity())
                        .onItemSelected(id, courseName);
                mPosition = vh.getAdapterPosition();
            }

            @Override
            public boolean onLongClick(final int courseId, final String coursePushId) {
                AlertDialog.Builder builder2 = new AlertDialog.Builder(getActivity());
                builder2.setMessage(getResources().getString(R.string.delete_course));
                builder2.setPositiveButton(getResources().getString(R.string.delete),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (coursePushId != null) {
                                    //todo delete course from firebas

                                }
                                mViewModel.deleteCourse(courseId);
                                Helper.updateWidgets(getContext());


                            }
                        });
                builder2.setNegativeButton(getResources().getString(R.string.cancel), null);
                builder2.show();
                return false;
            }

        });
        mRecyclerView.setAdapter(mCourseAdapter);

        mViewModel.getUserCourses().observe(getActivity(), new Observer<List<Course>>() {
            @Override
            public void onChanged(@Nullable List<Course> courses) {
                mCourseAdapter.swapCursor(courses);
                if (courses.size() != 0 ) {
                    emptyView.setText("");
                } else {
                    emptyView.setText(getResources().getString(R.string.no_course));
                }


                if (mPosition != ListView.INVALID_POSITION) {
                    // Ifp we don't need to restart the loader, and there's a desired position to restore
                    // to, do so now.
                    mRecyclerView.smoothScrollToPosition(mPosition);

                }
                startIntroAnimation();
            }
        });
        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            // The listview probably hasn't even been populated yet.  Actually perform the
            // swapout in onLoadFinished.
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }
        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }



    @Override
    public void onResume() {
        super.onResume();
        if (isLoaded)
            startIntroAnimation();
    }

    private void startIntroAnimation() {
        mRecyclerView.setTranslationY(getResources().getDimensionPixelSize(R.dimen.list_item_lesson));
        mRecyclerView.setAlpha(0f);
        mRecyclerView.animate()
                .translationY(0)
                .setDuration(500)
                .alpha(1f)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();
    }

    public void startDialog() {


        LayoutInflater li = LayoutInflater.from(getContext());
        View promptsView = li.inflate(R.layout.add_course_name, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                getContext(), R.style.DialogTheme);
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
        ColorAdapter mColorAdapter = new ColorAdapter(getContext());
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
                    Snackbar.make(rootView, getResources().getString(R.string.course_name_error),
                            Snackbar.LENGTH_LONG).show();

                } else {
                    addCourseData(courseNameET.getText().toString(), teacherNameET.getText().toString(),
                            courseId,"","", selectedColorId[0]);
                    Helper.updateWidgets(getContext());
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

    private void addCourseData(String courseName, String teacherName, String courseId,
                               String teacherEmail, String teacherPhotoURL,int color) {
        Course course = new Course(courseId, courseName, teacherName, teacherEmail,
                teacherPhotoURL,color, "",  Helper.getNormalizedUtcDateForToday(),
                Helper.getNormalizedUtcDateForToday());
        mViewModel.createNewCourse(course);

    }


}
