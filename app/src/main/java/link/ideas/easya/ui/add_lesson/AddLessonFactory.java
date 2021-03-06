package link.ideas.easya.ui.add_lesson;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import link.ideas.easya.data.EasyARepository;

/**
 * Factory method that allows us to create a ViewModel with a constructor that takes a
 * {@link EasyARepository}
 */
public class AddLessonFactory extends ViewModelProvider.NewInstanceFactory {

    private final EasyARepository mRepository;
    private final int courseId;
    public AddLessonFactory(EasyARepository repository,int courseId) {
        this.mRepository = repository;
        this.courseId = courseId;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        //noinspection unchecked
        return (T) new AddLessonViewModel(mRepository, courseId);
    }
}