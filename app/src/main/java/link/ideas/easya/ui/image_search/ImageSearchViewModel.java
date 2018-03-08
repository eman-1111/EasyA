package link.ideas.easya.ui.image_search;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import java.util.ArrayList;

import link.ideas.easya.BuildConfig;
import link.ideas.easya.data.EasyARepository;
import link.ideas.easya.models.Image;

/**
 * Created by Eman on 3/7/2018.
 */

public class ImageSearchViewModel extends ViewModel {

    EasyARepository mRepository;

    public ImageSearchViewModel(EasyARepository mRepository) {
        this.mRepository = mRepository;
    }

    public LiveData<Image> getSearchQuery(String query) {
        return mRepository.searchQuery(query);
    }
}
