package link.ideas.easya.ui.lesson_detail;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import link.ideas.easya.data.EasyARepository;
import link.ideas.easya.ui.lesson_detail.LessonDetailViewModel;
/**
 * Factory method that allows us to create a ViewModel with a constructor that takes a
 * {@link EasyARepository}
 */

public class LessonDetailModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final EasyARepository mRepository;
    private final int lessonId;

    public LessonDetailModelFactory(EasyARepository repository, int lessonId) {
        this.mRepository = repository;
         this.lessonId = lessonId;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        //noinspection unchecked
        return (T) new LessonDetailViewModel(mRepository, lessonId);
    }
}
