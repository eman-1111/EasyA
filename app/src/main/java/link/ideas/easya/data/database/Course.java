package link.ideas.easya.data.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by Eman on 11/21/2017.
 */

@Entity(tableName = "course")
public class Course {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String courseId;
    private String courseName;
    private String teacherName;
    private String teacherEmail;
    private String teacherPhotoURL;
    private int teacherColor;
    private String firebaseId;

    /**
     * This constructor is used by Room to create CourseEntry.
     *
     * @param id              column id for Course
     * @param courseId        course Id
     * @param courseName      course name
     * @param teacherName     course teacher name
     * @param teacherEmail    course teacher email
     * @param teacherPhotoURL course teacher photo
     * @param teacherColor    course teacher color
     * @param firebaseId      fire base course id
     */
    public Course(int id, String courseId, String courseName, String teacherName,
                  String teacherEmail, String teacherPhotoURL, int teacherColor,
                  String firebaseId) {
        this.id = id;
        this.courseId = courseId;
        this.courseName = courseName;
        this.teacherName = teacherName;
        this.teacherEmail = teacherEmail;
        this.teacherPhotoURL = teacherPhotoURL;
        this.teacherColor = teacherColor;
        this.firebaseId = firebaseId;
    }
    /**
     *
     * @param courseId        course Id
     * @param courseName      course name
     * @param teacherName     course teacher name
     * @param teacherEmail    course teacher email
     * @param teacherPhotoURL course teacher photo
     * @param teacherColor    course teacher color
     * @param firebaseId      fire base course id
     */
    @Ignore
    public Course(String courseId, String courseName, String teacherName,
                  String teacherEmail, String teacherPhotoURL, int teacherColor,
                  String firebaseId) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.teacherName = teacherName;
        this.teacherEmail = teacherEmail;
        this.teacherPhotoURL = teacherPhotoURL;
        this.teacherColor = teacherColor;
        this.firebaseId = firebaseId;
    }

    public int getId() {
        return id;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public String getTeacherEmail() {
        return teacherEmail;
    }

    public String getTeacherPhotoURL() {
        return teacherPhotoURL;
    }

    public int getTeacherColor() {
        return teacherColor;
    }

    public String getFirebaseId() {
        return firebaseId;
    }


    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public void setTeacherEmail(String teacherEmail) {
        this.teacherEmail = teacherEmail;
    }

    public void setTeacherPhotoURL(String teacherPhotoURL) {
        this.teacherPhotoURL = teacherPhotoURL;
    }

    public void setTeacherColor(int teacherColor) {
        this.teacherColor = teacherColor;
    }

    public void setFirebaseId(String firebaseId) {
        this.firebaseId = firebaseId;
    }
}
