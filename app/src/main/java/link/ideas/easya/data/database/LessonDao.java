package link.ideas.easya.data.database;

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


    @Query("SELECT Course.courseName, Lesson.courseId, Lesson.id, Lesson.lessonTitle," +
            " Lesson.lessonSummary, Lesson.favoriteLesson From Lesson " +
            "INNER JOIN Course ON Lesson.courseId = Course.courseId " +
            "WHERE Lesson.courseId = :courseId ")
    List<ListLesson> getLessons(String courseId);

    @Query("SELECT * From Lesson WHERE id = :id ")
    Lesson getLesson(int id);

    @Insert(onConflict = ABORT)
    void addLesson(Lesson lesson);

    @Query("DELETE FROM Lesson WHERE id = :id ")
    void deleteLesson(int id);

    @Query("DELETE FROM Lesson WHERE courseId = :courseId ")
    void deleteCourseLessons(String courseId);

}
