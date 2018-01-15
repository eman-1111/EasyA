package link.ideas.easya.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import link.ideas.easya.data.EasyARepository;
import link.ideas.easya.data.database.ListLesson;

/**
 * Created by Eman on 1/15/2018.
 */

public class FavLessonListViewModel extends ViewModel {
    private final EasyARepository mRepository;
    private final LiveData<List<ListLesson>> mLesson;

    public FavLessonListViewModel(EasyARepository repository , String courseId) {
        mRepository = repository;
        mLesson  = mRepository.getUserFavLessons(courseId);
    }

    public LiveData<List<ListLesson>> getUserLesson() {
        return mLesson;
    }


    public void deleteCourse(int lessonId) {
        mRepository.deleteLesson(lessonId);
    }
}

