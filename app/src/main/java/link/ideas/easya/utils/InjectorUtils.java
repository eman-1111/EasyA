package link.ideas.easya.utils;


import android.content.Context;


import com.google.firebase.database.DatabaseReference;

import link.ideas.easya.AppExecutors;
import link.ideas.easya.data.EasyARepository;
import link.ideas.easya.data.database.EasyADatabase;
import link.ideas.easya.data.firebase.FirebaseQueryLiveData;
import link.ideas.easya.ui.add_lesson.AddLessonFactory;
import link.ideas.easya.ui.course_list.CourseListModelFactory;
import link.ideas.easya.ui.friend_couse_list.FriendCourseModelFactory;
import link.ideas.easya.ui.friend_lesson_detail.FriendLessonDetailFactory;
import link.ideas.easya.ui.friend_lesson_list.FriendLessonListFactory;
import link.ideas.easya.ui.friends_list.FriendListFactory;
import link.ideas.easya.ui.lesson_list.FavLessonListModelFactory;
import link.ideas.easya.ui.lesson_detail.LessonDetailModelFactory;
import link.ideas.easya.ui.lesson_list.LessonListModelFactory;

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

    public static AddLessonFactory provideNewLessonViewModelFactory(Context context, int courseId) {
        EasyARepository repository = provideRepository(context.getApplicationContext());
        return new AddLessonFactory(repository, courseId);
    }


    public static LessonListModelFactory provideLessonListViewModelFactory(Context context, int courseId) {
        EasyARepository repository = provideRepository(context.getApplicationContext());
        return new LessonListModelFactory(repository, courseId);
    }

    public static FavLessonListModelFactory provideFavLessonListViewModelFactory(Context context, int courseId) {
        EasyARepository repository = provideRepository(context.getApplicationContext());
        return new FavLessonListModelFactory(repository, courseId);
    }


    public static LessonDetailModelFactory provideLessonDetailViewModelFactory(Context context, int lessonId) {
        EasyARepository repository = provideRepository(context.getApplicationContext());
        return new LessonDetailModelFactory(repository, lessonId);
    }
    public static FriendCourseModelFactory provideFriendCoursViewModelFactory(String friendAccount) {
        return new FriendCourseModelFactory(friendAccount);
    }
    public static FriendLessonDetailFactory provideFreindLessonDetailViewModelFactory(String coursePushId, String lessonPushId) {
        return new FriendLessonDetailFactory(coursePushId,lessonPushId);
    }
    public static FriendLessonListFactory provideFriendLessonListViewModelFactory(String coursePushId) {
        return new FriendLessonListFactory(coursePushId);
    }
    public static FriendListFactory provideFriendListViewModelFactory(String accountName) {
        return new FriendListFactory(accountName);
    }
    public static FirebaseQueryLiveData getFirebaseRef(DatabaseReference ref){
        return new FirebaseQueryLiveData(ref);
    }

}