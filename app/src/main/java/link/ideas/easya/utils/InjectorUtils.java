package link.ideas.easya.utils;


import android.content.Context;


import link.ideas.easya.AppExecutors;
import link.ideas.easya.data.EasyARepository;
import link.ideas.easya.data.database.EasyADatabase;
import link.ideas.easya.factory.CourseListModelFactory;
import link.ideas.easya.factory.LessonListModelFactory;

/**
 * Provides static methods to inject the various classes needed for EasyA
 */
public class InjectorUtils {

    public static EasyARepository provideRepository(Context context) {
        EasyADatabase database = EasyADatabase.getInstance(context.getApplicationContext());
        AppExecutors executors = AppExecutors.getInstance();

        //todo create NetworkDataSource
        return EasyARepository.getInstance(database.courseModel(), database.lessonModel() , executors);
    }


    public static CourseListModelFactory provideMainActivityViewModelFactory(Context context) {
        EasyARepository repository = provideRepository(context.getApplicationContext());
        return new CourseListModelFactory(repository);
    }

    public static LessonListModelFactory provideLessonListViewModelFactory(Context context, String courseId) {
        EasyARepository repository = provideRepository(context.getApplicationContext());
        return new LessonListModelFactory(repository, courseId);
    }


}