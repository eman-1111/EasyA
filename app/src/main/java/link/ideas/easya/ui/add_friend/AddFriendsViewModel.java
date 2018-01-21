package link.ideas.easya.ui.add_friend;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import link.ideas.easya.data.firebase.FirebaseQueryLiveData;
import link.ideas.easya.utils.Constants;
import link.ideas.easya.utils.Helper;


/**
 * Created by Eman on 1/10/2018.
 */

public class AddFriendsViewModel extends ViewModel {

    private static DatabaseReference FRIENDS_ADD_REF = FirebaseDatabase.getInstance().
            getReference().child(Constants.FIREBASE_LOCATION_USERS);
    private FirebaseQueryLiveData liveData = new FirebaseQueryLiveData(FRIENDS_ADD_REF);


    @NonNull
    public LiveData<DataSnapshot> getDataSnapshotLiveData() {
        return liveData;
    }


    public void setQuery(String query) {
        FRIENDS_ADD_REF.orderByKey().startAt(query).endAt(query + "~")
                        .limitToFirst(5);

    }

}