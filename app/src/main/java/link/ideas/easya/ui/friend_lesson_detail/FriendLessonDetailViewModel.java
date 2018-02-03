package link.ideas.easya.ui.friend_lesson_detail;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import link.ideas.easya.data.firebase.FirebaseQueryLiveData;
import link.ideas.easya.utils.Constants;
import link.ideas.easya.utils.InjectorUtils;

/**
 * Created by Eman on 1/11/2018.
 */

public class FriendLessonDetailViewModel extends ViewModel {

    private static DatabaseReference FRIENDS_LESSON_DETAIL_REF;
    private FirebaseQueryLiveData liveData;

    @NonNull
    public LiveData<DataSnapshot> getDataSnapshotLiveData() {
        return liveData;
    }


    public  FriendLessonDetailViewModel(String coursePushId, String lessonPushId) {
        FRIENDS_LESSON_DETAIL_REF =
                FirebaseDatabase.getInstance().getReference().
                        child(Constants.FIREBASE_LOCATION_USERS_LESSONS_DETAIL).
                        child(coursePushId).child(lessonPushId);
        liveData = InjectorUtils.getFirebaseRef(FRIENDS_LESSON_DETAIL_REF);
    }
}