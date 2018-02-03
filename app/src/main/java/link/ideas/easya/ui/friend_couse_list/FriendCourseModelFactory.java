package link.ideas.easya.ui.friend_couse_list;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

/**
 * Created by Eman on 2/3/2018.
 */

public class FriendCourseModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final String friendAccount;

    public FriendCourseModelFactory(String friendAccount) {
        this.friendAccount = friendAccount;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        //noinspection unchecked
        return (T) new FriendCourseListViewModel(friendAccount);
    }
}