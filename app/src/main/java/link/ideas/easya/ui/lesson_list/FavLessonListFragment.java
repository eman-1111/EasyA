package link.ideas.easya.ui.lesson_list;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import link.ideas.easya.R;
import link.ideas.easya.data.database.ListLesson;
import link.ideas.easya.utils.Constants;
import link.ideas.easya.utils.InjectorUtils;

/**
 * Created by Eman on 4/11/2017.
 */

public class FavLessonListFragment extends Fragment  {

    private static final String LOG_TAG = LessonListFragment.class.getSimpleName();
    public static final String COURSE_ID = "course_id";
    private int courseId;
    TextView emptyView;
    FavLessonListViewModel mViewModel;
    public FavLessonListFragment() {
    }


    public interface Callback {
        public void onItemSelected(int id,  String lessonTitle, LessonFavAdapter.SubjectFavAdapterViewHolder vh);
    }

    // RecyclerView mRecyclerView;
    RecyclerView mRecyclerView;
    private LessonFavAdapter mSubjectFavAdapter;
    private int mPosition = RecyclerView.NO_POSITION;


    private static final String SELECTED_KEY = "selected_position";


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
            courseId = arguments.getInt(LessonListFragment.COURSE_ID);
            Log.e("COURSE_ID",courseId+"g" );
        }
        View rootView = inflater.inflate(R.layout.fragment_subject_list, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview_lesson);
        emptyView = (TextView) rootView.findViewById(R.id.empty_tv);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mRecyclerView.setHasFixedSize(true);

        mSubjectFavAdapter = new LessonFavAdapter(getActivity(), new LessonFavAdapter.SubjectFavAdapterOnClickHolder() {
            @Override
            public void onClick(int id, String lessonTitle, LessonFavAdapter.SubjectFavAdapterViewHolder vh) {
                ((Callback) getActivity())
                        .onItemSelected(id,lessonTitle, vh);
                mPosition = vh.getAdapterPosition();
            }

            @Override
            public boolean onLongClick(final int lessonId, final String lessonName,
                                       final String coursePushId, final String lessonPushId) {
                AlertDialog.Builder builder2 = new AlertDialog.Builder(getActivity());
                builder2.setMessage(getResources().getString(R.string.delete_course));
                builder2.setPositiveButton(getResources().getString(R.string.delete),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if(lessonPushId != null){
                                    deleteLessonFromFirebase( lessonName, coursePushId, lessonPushId);
                                }
                                mViewModel.deleteLesson(lessonId);


                            }
                        });
                builder2.setNegativeButton(getResources().getString(R.string.cancel), null);
                builder2.show();
                return false;
            }
        });

        FavLessonListModelFactory factory = InjectorUtils.provideFavLessonListViewModelFactory(getContext(), courseId);
        mViewModel = ViewModelProviders.of(this, factory).get(FavLessonListViewModel.class);

        mViewModel.getUserLessons().observe(getActivity(), new Observer<List<ListLesson>>() {
            @Override
            public void onChanged(@Nullable List<ListLesson> listLessons) {
                mSubjectFavAdapter.swapCursor(listLessons);
                if (listLessons.size() != 0 ) {
                    emptyView.setText("");
                } else {
                    emptyView.setText(getResources().getString(R.string.no_course));
                }


                if (mPosition != ListView.INVALID_POSITION) {
                    // Ifp we don't need to restart the loader, and there's a desired position to restore
                    // to, do so now.
                    mRecyclerView.smoothScrollToPosition(mPosition);

                }
            }
        });


        mRecyclerView.setAdapter(mSubjectFavAdapter);
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


    private void deleteLessonFromFirebase(String title, String coursePushId, String lessonPushId) {


        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseStorage mFirebaseStorage = FirebaseStorage.getInstance();

        DatabaseReference mLessonDatabaseReference = mFirebaseDatabase.getReference().
                child(Constants.FIREBASE_LOCATION_USERS_LESSONS)
                .child(coursePushId).child(lessonPushId);
        mLessonDatabaseReference.removeValue();

        DatabaseReference mLessonDetailDatabaseReference = mFirebaseDatabase.getReference().
                child(Constants.FIREBASE_LOCATION_USERS_LESSONS_DETAIL)
                .child(coursePushId).child(lessonPushId);
        mLessonDetailDatabaseReference.removeValue();

        StorageReference mUserImagesReferenceSummary = mFirebaseStorage.getReference()
                .child(coursePushId + "/" + title + "/summary.jpg");
        StorageReference mUserImagesReferenceLink = mFirebaseStorage.getReference()
                .child(coursePushId + "/" + title + "/link.jpg");
        StorageReference mUserImagesReferenceApp = mFirebaseStorage.getReference()
                .child(coursePushId + "/" + title + "/app.jpg");

        mUserImagesReferenceSummary.delete();
        mUserImagesReferenceLink.delete();
        mUserImagesReferenceApp.delete();

    }


}

