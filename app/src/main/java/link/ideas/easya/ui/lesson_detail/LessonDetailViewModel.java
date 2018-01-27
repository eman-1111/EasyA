package link.ideas.easya.ui.lesson_detail;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import link.ideas.easya.data.EasyARepository;
import link.ideas.easya.data.database.Course;
import link.ideas.easya.data.database.Lesson;

/**
 * Created by Eman on 1/15/2018.
 */

public class LessonDetailViewModel extends ViewModel {
    private final EasyARepository mRepository;
    private final LiveData<Lesson> mLesson;
    private  LiveData<Course> mCourse;
    private final int lessonId;



    public LessonDetailViewModel(EasyARepository repository , int lessonId) {
        mRepository = repository;
        mLesson  = mRepository.getUserLesson(lessonId);
        this.lessonId = lessonId;

    }

    public LiveData<Lesson> getUserLesson() {
        return mLesson;
    }

    public LiveData<Course> getCourse(int courseId) {
        mCourse  = mRepository.getUserCourse(courseId);
        return mCourse;
    }

    public void updateFirebaseId(String courseFirebaseId){
        mRepository.updateFirebaseId(lessonId, courseFirebaseId);
    }

    public void updateFirebaseIdL(String lessonFirebaseId){
        mRepository.updateFirebaseIdL(lessonId, lessonFirebaseId);
    }
}

