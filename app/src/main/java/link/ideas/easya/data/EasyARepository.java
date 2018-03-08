package link.ideas.easya.data;

/**
 * Created by Eman on 11/27/2017.
 */

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import link.ideas.easya.AppExecutors;
import link.ideas.easya.BuildConfig;
import link.ideas.easya.data.database.Course;
import link.ideas.easya.data.database.CourseDao;
import link.ideas.easya.data.database.Lesson;
import link.ideas.easya.data.database.LessonDao;
import link.ideas.easya.data.database.ListLesson;
import link.ideas.easya.data.network.ApiUtils;
import link.ideas.easya.models.Image;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Handled data operation in EasyA.
 */
public class EasyARepository {
    private static final String LOG_TAG = EasyARepository.class.getSimpleName();

    //for Singleton instantiation
    private static final Object LOCK = new Object();
    private static EasyARepository sInstance;
    private final CourseDao mCourseDao;
    private final LessonDao mLessonDao;
    private final AppExecutors mAppExecutors;

    private EasyARepository(CourseDao mCourseDao, LessonDao mLessonDao, AppExecutors mAppExecutors) {
        this.mCourseDao = mCourseDao;
        this.mLessonDao = mLessonDao;
        this.mAppExecutors = mAppExecutors;

        // As long as the repository exists, observe the network LiveData.
        // If that LiveData changes, update the database.
        //todo

    }

    public synchronized static EasyARepository getInstance(
            CourseDao mCourseDao, LessonDao mLessonDao, AppExecutors mAppExecutors) {
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
                mLessonDao.deleteCourseLessons(courseId);
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

    public LiveData<List<ListLesson>> getUserFavLessons(int courseId, String fav) {
        return mLessonDao.getFavLessons(courseId, fav);
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

    public void updateFavorite(final int id, final String favoriteLesson) {
        mAppExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mLessonDao.updateFavorite(id, favoriteLesson);
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

    public LiveData<Image> searchQuery(String query) {
        final MutableLiveData<Image> data = new MutableLiveData<>();

        String apiKey = BuildConfig.UNIQUE_FLICKR_KEY;
        ApiUtils.getAppService()
                .getImages(apiKey, query, "20", "json", "1").enqueue(new Callback<Image>() {
            @Override
            public void onResponse(@Nullable Call<Image> call,@Nullable Response<Image> response) {

                if(response.isSuccessful()){
                    data.setValue(response.body());
                }
            }

            @Override
            public void onFailure(@Nullable Call<Image> call,@Nullable Throwable t) {

            }
        });
        return data;
    }

}


