package link.ideas.easya.data.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.ABORT;

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

    @Query("SELECT * From lesson WHERE lessonId = :lessonId ")
    LiveData<Lesson> getLesson(int lessonId);

    @Insert(onConflict = ABORT)
    void insertLesson(Lesson lesson);

    @Query("DELETE FROM lesson WHERE lessonId = :lessonId ")
    void deleteLesson(int lessonId);

    @Query("DELETE FROM lesson WHERE courseId = :courseId ")
    void deleteCourseLessons(int courseId);

}
