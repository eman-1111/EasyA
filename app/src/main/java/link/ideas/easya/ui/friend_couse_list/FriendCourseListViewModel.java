package link.ideas.easya.ui.friend_couse_list;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import link.ideas.easya.data.firebase.FirebaseQueryLiveData;
import link.ideas.easya.utils.Constants;
import link.ideas.easya.utils.Helper;
import link.ideas.easya.utils.InjectorUtils;

/**
 * Created by Eman on 1/11/2018.
 */

public class FriendCourseListViewModel extends ViewModel {
    private static DatabaseReference COURSE_LIST_REF;
    private FirebaseQueryLiveData liveData;
    private String friendAccount;


    @NonNull
    public LiveData<DataSnapshot> getDataSnapshotLiveData() {
        return liveData;
    }

    public String getAccountName() {
        return friendAccount;
    }

    public  FriendCourseListViewModel(String friendAccount) {
        this.friendAccount = friendAccount;
        COURSE_LIST_REF =
                FirebaseDatabase.getInstance().getReference().
                        child(Constants.FIREBASE_LOCATION_USERS_COURSES).child(friendAccount).
                        child(Constants.FIREBASE_LOCATION_USER_COURSES);

        liveData = InjectorUtils.getFirebaseRef(COURSE_LIST_REF);
    }
}