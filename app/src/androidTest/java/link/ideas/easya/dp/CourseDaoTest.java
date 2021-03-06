package link.ideas.easya.dp;


import android.arch.persistence.room.Room;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import link.ideas.easya.LiveDataTestUtil;
import link.ideas.easya.data.database.Course;
import link.ideas.easya.data.database.EasyADatabase;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Created by Eman on 11/23/2017.
 */

@RunWith(AndroidJUnit4.class)
public class CourseDaoTest {
    private static final Course COURSE = new Course("sas", "Math", "Eman",
            "eman.ashour1111@gmail.com", null, 2, "",
            null,null);
    private EasyADatabase mDatabase;


    @Before
    public void initDb() throws Exception {
        // using an in-memory database because the information stored here disappears when the
        // process is killed
        mDatabase = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(), EasyADatabase.class)
                // allowing main thread queries, just for testing
                .allowMainThreadQueries()
                .build();
    }
    @Test
    public void insertAndGetCourse() throws InterruptedException  {
        //inserting new user into the course table
        mDatabase.courseModel().insertCourse(COURSE);

        //retrieving inserted course
        Course dbCourse = LiveDataTestUtil.getValue(mDatabase.courseModel().getCourse(COURSE.getCourseId()));

        //make sure its the same course I inserted
        assertEquals(dbCourse.getCourseName(), COURSE.getCourseName());
        assertEquals(dbCourse.getCourseId(), COURSE.getCourseId());

    }


    @Test
    public void updateAndGetCourse()throws InterruptedException  {
        //insert new course
        mDatabase.courseModel().insertCourse(COURSE);
        //retrieve the course
        Course course = LiveDataTestUtil.getValue(mDatabase.courseModel().getCourse(COURSE.getCourseId()));

        //update value
        course.setCourseName("Math2");
        mDatabase.courseModel().insertCourse(course);
        Course updaterCourse = LiveDataTestUtil.getValue(mDatabase.courseModel().getCourse(course.getId()));

        //make sure its the same course I inserted
        assertEquals(updaterCourse.getCourseName(), course.getCourseName());

    }

    @Test
    public void getCoursesList() throws InterruptedException {
        //insert new course
        mDatabase.courseModel().insertCourse(COURSE);

        //retrieve courses list it should has one row only
        List<Course> courses = LiveDataTestUtil.getValue(mDatabase.courseModel().getCourses());
        assertThat(courses.size(), is(1));
        Course course = courses.get(0);

        //make sure its the same course I inserted
        assertEquals(course.getTeacherEmail(), COURSE.getTeacherEmail());
    }
    @Test
    public void editCourse() throws InterruptedException{
        long courseId = mDatabase.courseModel().insertCourse(COURSE);
        COURSE.setId((int) courseId);
        COURSE.setCourseName("Math3");

        mDatabase.courseModel().updateCourse(COURSE);

        //get that course
        Course dbCourse = LiveDataTestUtil.getValue(mDatabase.courseModel().getCourse((int) courseId));
        assertEquals(dbCourse.getCourseName(), COURSE.getCourseName());

    }

    @Test
    public void updatePuchId() throws InterruptedException{
        long courseId = mDatabase.courseModel().insertCourse(COURSE);
        COURSE.setCourseName("23456tg");

        mDatabase.courseModel().updateFirebaseId((int)courseId, "23456tg");

        //get that course
        Course dbCourse = LiveDataTestUtil.getValue(mDatabase.courseModel().getCourse((int) courseId));
        assertEquals(dbCourse.getFirebaseId(), "23456tg");

    }
    @Test
    public void deleteAndGetCourse()throws InterruptedException {
        //insert new course
        mDatabase.courseModel().insertCourse(COURSE);

        //get that course
        Course dbCourse = LiveDataTestUtil.getValue(mDatabase.courseModel().getCourse(COURSE.getCourseId()));
        //delete the course
        mDatabase.courseModel().deleteCourse(dbCourse.getId());

        //the course is no longer in the data source
        List<Course> courses = LiveDataTestUtil.getValue(mDatabase.courseModel().getCourses());
        assertThat(courses.size(), is(0));
    }

    @After
    public void closeDb() throws Exception {
        mDatabase.close();
    }
}
