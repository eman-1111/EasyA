package link.ideas.easya.dp;

import android.arch.persistence.room.Room;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import link.ideas.easya.data.database.ListLesson;
import link.ideas.easya.data.database.Course;
import link.ideas.easya.data.database.EasyADatabase;
import link.ideas.easya.data.database.Lesson;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Created by Eman on 11/25/2017.
 */
@RunWith(AndroidJUnit4.class)
public class LessonDaoTest {
    private static final Course COURSE = new Course("sas", "Math", "Eman",
            "eman.ashour1111@gmail.com", null, 2, null);

    private static final Calendar calendar = Calendar.getInstance();
    private static final Date time = calendar.getTime();
    private static Lesson LESSON = new Lesson(0,"Mass", "the amout of matter that the object contains",
            "we ask for mass when we buy fruits", null, null,
            null, "0", null, time, time);
    private EasyADatabase mDatabase;

    @Before
    public void initDb() {
        mDatabase = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(),
                EasyADatabase.class)
                // allowing main thread queries, just for testing
                .allowMainThreadQueries()
                .build();
    }

    @Test
    public void insertAndGetLessons(){
        //insert the course and get the its id to insert the lesson
        mDatabase.courseModel().insertCourse(COURSE);
        Course course = mDatabase.courseModel().getCourse(COURSE.getCourseId());
        LESSON.setCourseId(course.getId());
        mDatabase.lessonModel().insertLesson(LESSON);

        //get lessons list for that course
        List<ListLesson> lessons = mDatabase.lessonModel().getLessons(course.getId());
        assertThat(lessons.size(), is(1));
        ListLesson lesson = lessons.get(0);

        //make sure its the same lesson I inserted
        assertEquals(LESSON.getLessonTitle(), lesson.getLessonTitle());
        //check to see it the foreignKey is correct
        assertEquals(course.getId(), lesson.getCourseId());
    }
    @Test
    public void insertAndGetLesson(){
        //insert the course and get the its id to insert the lesson
        mDatabase.courseModel().insertCourse(COURSE);
        Course course = mDatabase.courseModel().getCourse(COURSE.getCourseId());
        LESSON.setCourseId(course.getId());
        mDatabase.lessonModel().insertLesson(LESSON);

        //get lessons list for that course
        List<ListLesson> lessons = mDatabase.lessonModel().getLessons(course.getId());
        assertThat(lessons.size(), is(1));
        ListLesson lesson = lessons.get(0);

        //get lesson`s detail
        Lesson dpLesson = mDatabase.lessonModel().getLesson(lesson.getId());

        //make sure its the same lesson I inserted
        assertEquals(dpLesson.getLessonCreate(), LESSON.getLessonCreate());
        //check to see it the foreignKey is correct
        assertEquals(course.getId(), dpLesson.getCourseId());
    }

    @Test
    public void deleteOneLesson(){
        //insert the course and get the its id to insert the lesson
        mDatabase.courseModel().insertCourse(COURSE);
        Course course = mDatabase.courseModel().getCourse(COURSE.getCourseId());
        LESSON.setCourseId(course.getId());
        mDatabase.lessonModel().insertLesson(LESSON);

        //get lessons list for that course
        List<ListLesson> lessons = mDatabase.lessonModel().getLessons(course.getId());
        assertThat(lessons.size(), is(1));
        ListLesson lesson = lessons.get(0);

        //delete lesson
        mDatabase.lessonModel().deleteLesson(lesson.getId());

        //the Lesson is no longer in the data source
        List<ListLesson> lessonsA = mDatabase.lessonModel().getLessons(course.getId());
        assertThat(lessonsA.size(), is(0));
    }

    @Test
    public void deleteCourseLessons(){
        mDatabase.courseModel().insertCourse(COURSE);
        Course course = mDatabase.courseModel().getCourse(COURSE.getCourseId());
        LESSON.setCourseId(course.getId());
        mDatabase.lessonModel().insertLesson(LESSON);

        //get lessons list for that course
        List<ListLesson> lessons = mDatabase.lessonModel().getLessons(course.getId());
        assertThat(lessons.size(), is(1));
        ListLesson lesson = lessons.get(0);

        //delete course`s lesson
        mDatabase.lessonModel().deleteCourseLessons(lesson.getCourseId());

        //the Lesson is no longer in the data source
        List<ListLesson> lessonsA = mDatabase.lessonModel().getLessons(course.getId());
        assertThat(lessonsA.size(), is(0));
    }

    @After
    public void closeDb() {
        mDatabase.close();
    }
}
