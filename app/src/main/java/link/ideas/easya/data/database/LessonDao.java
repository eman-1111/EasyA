package link.ideas.easya.data.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.ABORT;
import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * Created by Eman on 11/22/2017.
 */

@Dao
public interface LessonDao {


    @Query("SELECT lesson.lessonId , lesson.courseId, course.courseName, lesson.lessonTitle," +
            " lesson.lessonSummary, lesson.favoriteLesson, lesson.firebaseId From lesson " +
            "INNER JOIN course ON lesson.courseId = course.id " +
            "WHERE lesson.courseId = :courseId "+
            "AND course.id = :courseId ")
    LiveData<List<ListLesson>> getLessons(int courseId);


    @Query("SELECT lesson.lessonId , lesson.courseId, course.courseName, lesson.lessonTitle," +
            " lesson.lessonSummary, lesson.favoriteLesson, lesson.firebaseId From lesson " +
            "INNER JOIN course ON lesson.courseId = course.id " +
            "WHERE lesson.courseId = :courseId "+
            "AND course.id = :courseId " +
            "AND lesson.favoriteLesson = :fav ")
    LiveData<List<ListLesson>> getFavLessons(int courseId, String fav);

    @Query("SELECT * From lesson WHERE lessonId = :lessonId ")
    LiveData<Lesson> getLesson(int lessonId);

    @Insert(onConflict = REPLACE)
    long insertLesson(Lesson lesson);

    @Update(onConflict = REPLACE)
    void updateLesson(Lesson lesson);

    @Query("UPDATE lesson SET firebaseId = :firebaseId  WHERE lessonId = :lessonId")
    void updateFirebaseId(int lessonId, String firebaseId);

    @Query("DELETE FROM lesson WHERE lessonId = :lessonId ")
    void deleteLesson(int lessonId);

    @Query("DELETE FROM lesson WHERE courseId = :courseId ")
    void deleteCourseLessons(int courseId);

    @Query("UPDATE lesson SET favoriteLesson = :favoriteLesson  WHERE lessonId = :lessonId")
    void updateFavorite(int lessonId, String favoriteLesson);

}
