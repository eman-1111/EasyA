package link.ideas.easya.data;

/**
 * Created by Eman on 11/27/2017.
 */

import android.util.Log;

import link.ideas.easya.AppExecutors;
import link.ideas.easya.data.database.CourseDao;
import link.ideas.easya.data.database.LessonDao;

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



}
