package link.ideas.easya.ui.add_lesson;

import android.arch.lifecycle.ViewModel;

import link.ideas.easya.data.EasyARepository;
import link.ideas.easya.data.database.Lesson;

public class AddLessonViewModel extends ViewModel {
    private final EasyARepository mRepository;
    private int courseId;

    public AddLessonViewModel(EasyARepository repository, int courseId) {
        mRepository = repository;
        this.courseId = courseId;
    }

    public void addNewLesson(Lesson lesson, boolean isUpdated) {
        if (isUpdated) {
            mRepository.udDateLesson(lesson);
        } else {
            mRepository.insertLesson(lesson);
        }
    }

}

