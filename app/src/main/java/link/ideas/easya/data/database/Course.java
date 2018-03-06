package link.ideas.easya.data.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import java.util.Date;

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
    @TypeConverters(DateConverter.class)
    private Date courseCreate;
    @TypeConverters(DateConverter.class)
    private Date courseEdit;
    private long courseCreateF;
    private long courseEditF;

    //https://stackoverflow.com/questions/44364240/android-room-get-the-id-of-new-inserted-row-with-auto-generate

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
     * @param courseCreate    column create lesson date
     * @param courseEdit      column edit lesson date
     */
    public Course(int id, String courseId, String courseName, String teacherName,
                  String teacherEmail, String teacherPhotoURL, int teacherColor,
                  String firebaseId, Date courseCreate, Date courseEdit) {
        this.id = id;
        this.courseId = courseId;
        this.courseName = courseName;
        this.teacherName = teacherName;
        this.teacherEmail = teacherEmail;
        this.teacherPhotoURL = teacherPhotoURL;
        this.teacherColor = teacherColor;
        this.firebaseId = firebaseId;
        this.courseCreate = courseCreate;
        this.courseEdit = courseEdit;
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
     * @param courseCreate    column create lesson date
     * @param courseEdit      column edit lesson date
     */
    @Ignore
    public Course(String courseId, String courseName, String teacherName,
                  String teacherEmail, String teacherPhotoURL, int teacherColor,
                  String firebaseId, Date courseCreate, Date courseEdit) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.teacherName = teacherName;
        this.teacherEmail = teacherEmail;
        this.teacherPhotoURL = teacherPhotoURL;
        this.teacherColor = teacherColor;
        this.firebaseId = firebaseId;
        this.courseCreate = courseCreate;
        this.courseEdit = courseEdit;
    }


    //firebase dao
    @Ignore
    public Course(int id, String courseName, String teacherName,
                  String teacherEmail, String teacherPhotoURL, int teacherColor,
                  long courseCreate, long courseEdit) {
        this.id = id;
        this.courseName = courseName;
        this.teacherName = teacherName;
        this.teacherEmail = teacherEmail;
        this.teacherPhotoURL = teacherPhotoURL;
        this.teacherColor = teacherColor;
        this.courseCreateF = courseCreate;
        this.courseEditF = courseEdit;
    }

    @Ignore
    public Course() {

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

    public void setId(int id) {
        this.id = id;
    }

    public Date getCourseCreate() {
        return courseCreate;
    }

    public void setCourseCreate(Date courseCreate) {
        this.courseCreate = courseCreate;
    }

    public Date getCourseEdit() {
        return courseEdit;
    }

    public void setCourseEdit(Date courseEdit) {
        this.courseEdit = courseEdit;
    }

    public long getCourseCreateF() {
        return courseCreateF;
    }

    public void setCourseCreateF(long courseCreateF) {
        this.courseCreateF = courseCreateF;
    }

    public long getCourseEditF() {
        return courseEditF;
    }

    public void setCourseEditF(long courseEditF) {
        this.courseEditF = courseEditF;
    }
}
