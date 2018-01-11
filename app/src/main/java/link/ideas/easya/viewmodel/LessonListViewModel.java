package link.ideas.easya.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import link.ideas.easya.data.firebase.FirebaseQueryLiveData;
import link.ideas.easya.utils.Constants;

/**
 * Created by Eman on 1/11/2018.
 */

public class LessonListViewModel extends ViewModel {
    private static DatabaseReference LESSON_LIST_REF;
    private FirebaseQueryLiveData liveData;
    private String coursePushId;


    @NonNull
    public LiveData<DataSnapshot> getDataSnapshotLiveData() {
        return liveData;
    }

    public String getCoursePushId() {
        return coursePushId;
    }

    public void setCoursePushId(String coursePushId) {
        this.coursePushId = coursePushId;
        LESSON_LIST_REF =
                FirebaseDatabase.getInstance().getReference().
                        child(Constants.FIREBASE_LOCATION_USERS_LESSONS).child(coursePushId);

        liveData = new FirebaseQueryLiveData(LESSON_LIST_REF);
    }
}