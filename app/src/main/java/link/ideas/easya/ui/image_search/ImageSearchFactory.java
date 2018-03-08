package link.ideas.easya.ui.image_search;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import link.ideas.easya.data.EasyARepository;

/**
 * Created by Eman on 3/7/2018.
 */

public class ImageSearchFactory extends ViewModelProvider.NewInstanceFactory {
    private final EasyARepository mRepository;

    public ImageSearchFactory(EasyARepository mRepository){
        this.mRepository = mRepository;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        //noinspection unchecked
        return (T) new ImageSearchViewModel(mRepository);
    }
}
