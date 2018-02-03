package link.ideas.easya.ui.friend_lesson_detail;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

/**
 * Created by Eman on 2/3/2018.
 */

public class FriendLessonDetailFactory extends ViewModelProvider.NewInstanceFactory {

    private final String coursePushId;
    private final String lessonPushId;

    public FriendLessonDetailFactory(String coursePushId, String lessonPushId) {
        this.coursePushId = coursePushId;
        this.lessonPushId = lessonPushId;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        //noinspection unchecked
        return (T) new FriendLessonDetailViewModel(coursePushId,lessonPushId );
    }
}