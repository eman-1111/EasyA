package link.ideas.easya.ui.friend_lesson_list;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

/**
 * Created by Eman on 2/3/2018.
 */

public class FriendLessonListFactory extends ViewModelProvider.NewInstanceFactory {

    private final String coursePushId;


    public FriendLessonListFactory(String coursePushId) {
        this.coursePushId = coursePushId;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        //noinspection unchecked
        return (T) new FriendLessonListViewModel(coursePushId );
    }
}