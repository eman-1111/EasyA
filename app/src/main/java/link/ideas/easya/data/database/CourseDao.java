package link.ideas.easya.data.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

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
    long insertCourse(Course course);

    @Update(onConflict = REPLACE)
    void updateCourse(Course course);

    @Query("UPDATE course SET firebaseId = :firebaseId  WHERE id = :id")
     int updateFirebaseId(int id, String firebaseId);

    @Query("DELETE FROM course WHERE  id = :id")
    void deleteCourse(int id);

}
