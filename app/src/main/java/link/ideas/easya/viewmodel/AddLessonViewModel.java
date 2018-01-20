package link.ideas.easya.viewmodel;

import android.arch.lifecycle.ViewModel;
import link.ideas.easya.data.EasyARepository;
import link.ideas.easya.data.database.Lesson;

public class AddLessonViewModel extends ViewModel {
    private final EasyARepository mRepository;
    private int courseId ;

    public AddLessonViewModel(EasyARepository repository, int courseId ) {
        mRepository = repository;
        this.courseId = courseId;
    }

    public void addNewLesson(Lesson lesson){
        mRepository.insertLesson(lesson);
    }

}

