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


    @Query("SELECT course.courseName, lesson.courseId, lesson.id, lesson.lessonTitle," +
            " lesson.lessonSummary, lesson.favoriteLesson From lesson " +
            "INNER JOIN course ON lesson.courseId = course.id " +
            "WHERE lesson.courseId = :courseId "+
            "AND course.id = :courseId ")
    LiveData<List<ListLesson>> getLessons(int courseId);

    @Query("SELECT * From lesson WHERE id = :id ")
    LiveData<Lesson> getLesson(int id);

    @Insert(onConflict = ABORT)
    void insertLesson(Lesson lesson);

    @Query("DELETE FROM lesson WHERE id = :id ")
    void deleteLesson(int id);

    @Query("DELETE FROM lesson WHERE courseId = :courseId ")
    void deleteCourseLessons(int courseId);

}
