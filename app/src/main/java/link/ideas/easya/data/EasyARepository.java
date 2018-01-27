package link.ideas.easya.data;

/**
 * Created by Eman on 11/27/2017.
 */

import android.arch.lifecycle.LiveData;
import android.util.Log;

import java.util.Date;
import java.util.List;

import link.ideas.easya.AppExecutors;
import link.ideas.easya.data.database.Course;
import link.ideas.easya.data.database.CourseDao;
import link.ideas.easya.data.database.Lesson;
import link.ideas.easya.data.database.LessonDao;
import link.ideas.easya.data.database.ListLesson;

/**
 * Handled data operation in EasyA.
 */
public class EasyARepository {
    private static final String LOG_TAG = EasyARepository.class.getSimpleName();

    //for Singleton instantiation
    private static final Object LOCK= new Object();
    private static EasyARepository sInstance;
    private final CourseDao mCourseDao;
    private final LessonDao mLessonDao;
    private final AppExecutors mAppExecutors;

    private EasyARepository(CourseDao mCourseDao,LessonDao mLessonDao,AppExecutors mAppExecutors){
        this.mCourseDao = mCourseDao;
        this.mLessonDao = mLessonDao;
        this.mAppExecutors = mAppExecutors;

        // As long as the repository exists, observe the network LiveData.
        // If that LiveData changes, update the database.
        //todo

    }

    public synchronized static EasyARepository getInstance(
            CourseDao mCourseDao,LessonDao mLessonDao,AppExecutors mAppExecutors) {
        Log.d(LOG_TAG, "Getting the repository");
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new EasyARepository(mCourseDao, mLessonDao,
                        mAppExecutors);
                Log.d(LOG_TAG, "Made new repository");
            }
        }
        return sInstance;
    }



    public LiveData<List<Course>> getUserCourses() {
        return mCourseDao.getCourses();
    }

    public void insertCourse(final Course course) {
        mAppExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mCourseDao.insertCourse(course);
            }
        });
    }
    public void updateCourse(final Course course) {
        mAppExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mCourseDao.updateCourse(course);
            }
        });
    }

    public void updateFirebaseId(final int id, final String pushId) {
        mAppExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mCourseDao.updateFirebaseId(id, pushId);
            }
        });
    }
    public void deleteCourse(final int courseId) {
        mAppExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mCourseDao.deleteCourse(courseId);
            }
        });
    }


    public LiveData<Course> getUserCourse(int courseId) {
        return mCourseDao.getCourse(courseId);
    }


    public LiveData<List<ListLesson>> getUserLessons(int courseId) {
        return mLessonDao.getLessons(courseId);
    }

    public LiveData<Lesson> getUserLesson(int lessonId) {
        return mLessonDao.getLesson(lessonId);
    }

    public LiveData<List<ListLesson>> getUserFavLessons(int courseId) {
        return mLessonDao.getLessons(courseId);
    }


    public void insertLesson(final Lesson lesson) {
        mAppExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mLessonDao.insertLesson(lesson);
            }
        });
    }
    public void updateFirebaseIdL(final int id, final String pushId) {
        mAppExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mLessonDao.updateFirebaseId(id, pushId);
            }
        });
    }
    public void udDateLesson(final Lesson lesson) {
        mAppExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mLessonDao.updateLesson(lesson);
            }
        });
    }

    public void deleteLesson(final int lessonId) {
        mAppExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mLessonDao.deleteLesson(lessonId);
            }
        });
    }

}


