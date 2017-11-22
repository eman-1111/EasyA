package link.ideas.easya.data.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.IGNORE;

/**
 * Created by Eman on 11/21/2017.
 */

@Dao
public interface CourseDao {

    @Query("SELECT * FROM Course")
    List<Course> getCourses();

    @Insert
    void insertCourse(Course course);

    @Insert(onConflict = IGNORE)
    void insertOrReplaceCourse(Course... courseEntries);

    @Query("DELETE FROM Course WHERE  courseId < :courseId")
    void deleteCourse(String courseId);
}
