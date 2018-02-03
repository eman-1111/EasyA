package link.ideas.easya.ui.friends_list;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

/**
 * Created by Eman on 2/3/2018.
 */

public class FriendListFactory extends ViewModelProvider.NewInstanceFactory {

    private final String accountName;


    public FriendListFactory(String accountName) {
        this.accountName = accountName;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        //noinspection unchecked
        return (T) new FriendsListViewModel(accountName);
    }
}