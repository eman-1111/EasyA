package link.ideas.easya.factory;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import link.ideas.easya.data.EasyARepository;
import link.ideas.easya.viewmodel.CourseListViewModel;

/**
 * Factory method that allows us to create a ViewModel with a constructor that takes a
 * {@link EasyARepository}
 */
public class CourseListModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final EasyARepository mRepository;

    public CourseListModelFactory(EasyARepository repository) {
        this.mRepository = repository;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        //noinspection unchecked
        return (T) new CourseListViewModel(mRepository);
    }
}
