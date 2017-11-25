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


    @Query("SELECT course.courseName, lesson.courseId, lesson.id, lesson.lessonTitle," +
            " lesson.lessonSummary, lesson.favoriteLesson From lesson " +
            "INNER JOIN course ON lesson.courseId = course.id " +
            "WHERE lesson.courseId = :courseId "+
            "AND course.id = :courseId ")
    List<ListLesson> getLessons(String courseId);

    @Query("SELECT * From lesson WHERE id = :id ")
    Lesson getLesson(int id);

    @Insert(onConflict = ABORT)
    void addLesson(Lesson lesson);

    @Query("DELETE FROM lesson WHERE id = :id ")
    void deleteLesson(int id);

    @Query("DELETE FROM lesson WHERE courseId = :courseId ")
    void deleteCourseLessons(String courseId);

}
