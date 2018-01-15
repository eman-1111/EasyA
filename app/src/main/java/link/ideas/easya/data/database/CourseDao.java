package link.ideas.easya.data.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.IGNORE;
import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * Created by Eman on 11/21/2017.
 */

@Dao
public interface CourseDao {

    @Query("SELECT * FROM course")
    LiveData<List<Course>> getCourses();

    @Query("SELECT * From course WHERE id = :id ")
    LiveData<Course> getCourse(int id);

    @Query("SELECT * From course WHERE courseId = :courseId ")
    LiveData<Course> getCourse(String courseId);

    @Insert(onConflict = REPLACE)
    void insertCourse(Course course);


    @Query("DELETE FROM course WHERE  courseId = :courseId")
    void deleteCourse(String courseId);
}
