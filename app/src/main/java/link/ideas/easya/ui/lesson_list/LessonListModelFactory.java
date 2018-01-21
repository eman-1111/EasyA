package link.ideas.easya.ui.lesson_list;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import link.ideas.easya.data.EasyARepository;



import link.ideas.easya.ui.lesson_list.LessonListViewModel;

/**
 * Factory method that allows us to create a ViewModel with a constructor that takes a
 * {@link EasyARepository}
 */
public class LessonListModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final EasyARepository mRepository;
    private final int courseId;

    public LessonListModelFactory(EasyARepository repository,  int courseId) {
        this.mRepository = repository;
        this.courseId = courseId;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        //noinspection unchecked
        return (T) new LessonListViewModel(mRepository, courseId);
    }
}
