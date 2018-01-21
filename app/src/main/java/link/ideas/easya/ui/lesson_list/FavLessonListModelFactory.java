package link.ideas.easya.ui.lesson_list;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import link.ideas.easya.data.EasyARepository;
import link.ideas.easya.ui.lesson_list.FavLessonListViewModel;

/**
 * Created by Eman on 1/18/2018.
 */

public class FavLessonListModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final EasyARepository mRepository;
    private final int courseId;

    public FavLessonListModelFactory(EasyARepository repository,  int courseId) {
        this.mRepository = repository;
        this.courseId = courseId;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        //noinspection unchecked
        return (T) new FavLessonListViewModel(mRepository, courseId);
    }
}
