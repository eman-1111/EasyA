package link.ideas.easya.ui.course_list;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import link.ideas.easya.data.EasyARepository;
import link.ideas.easya.data.database.Course;

/**
 * {@link ViewModel} for {@link CourseListFragment}
 */

public class CourseListViewModel extends ViewModel {
    private final EasyARepository mRepository;
    private final LiveData<List<Course>> mCourses;

    public CourseListViewModel(EasyARepository repository) {
        mRepository = repository;
        mCourses = mRepository.getUserCourses();
    }

    public LiveData<List<Course>> getUserCourses() {
        return mCourses;
    }

    public void createNewCourse(Course course) {
        mRepository.insertCourse(course);
    }

    public void deleteCourse(int courseId) {
        mRepository.deleteCourse(courseId);
    }
}

