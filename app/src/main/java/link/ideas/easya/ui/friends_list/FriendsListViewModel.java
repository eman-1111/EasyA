package link.ideas.easya.ui.friends_list;

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
 * Created by Eman on 1/10/2018.
 */

public class FriendsListViewModel extends ViewModel {
    private static DatabaseReference FRIENDS_LIST_REF;
    private FirebaseQueryLiveData liveData;
    private String accountName;


    @NonNull
    public LiveData<DataSnapshot> getDataSnapshotLiveData() {
        return liveData;
    }

    public String getAccountName() {
        return accountName;
    }

    public FriendsListViewModel(String accountName) {
        this.accountName = accountName;
        FRIENDS_LIST_REF =
                FirebaseDatabase.getInstance().getReference().
                        child(Constants.FIREBASE_LOCATION_USER_FRIENDS)
                        .child(Helper.encodeEmail(accountName));
        liveData = InjectorUtils.getFirebaseRef(FRIENDS_LIST_REF);
    }
}