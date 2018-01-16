package link.ideas.easya.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import link.ideas.easya.data.EasyARepository;
import link.ideas.easya.data.database.Lesson;

/**
 * Created by Eman on 1/15/2018.
 */

public class LessonDetailViewModel extends ViewModel {
    private final EasyARepository mRepository;
    private final LiveData<Lesson> mLesson;

    public LessonDetailViewModel(EasyARepository repository , int lessonId) {
        mRepository = repository;
        mLesson  = mRepository.getUserLesson(lessonId);
    }

    public LiveData<Lesson> getUserLesson() {
        return mLesson;
    }


}

