package link.ideas.easya.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import link.ideas.easya.data.EasyARepository;
import link.ideas.easya.data.database.Lesson;
import link.ideas.easya.data.database.ListLesson;

/**
 * Created by Eman on 1/14/2018.
 */

public class LessonListViewModel extends ViewModel {
    private final EasyARepository mRepository;
    private final LiveData<List<ListLesson>> mLesson;

    public LessonListViewModel(EasyARepository repository , String courseId) {
        mRepository = repository;
        mLesson  = mRepository.getUserLessons(courseId);
    }

    public LiveData<List<ListLesson>> getUserLesson() {
        return mLesson;
    }


    public void deleteCourse(int lessonId) {
        mRepository.deleteLesson(lessonId);
    }
}

