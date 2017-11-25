package link.ideas.easya.data.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import java.util.Date;

/**
 * Created by Eman on 11/22/2017.
 */
@Entity(tableName = "lesson", foreignKeys =
        {@ForeignKey(entity = Course.class, parentColumns = "id", childColumns = "courseId")})
public class Lesson {

    @PrimaryKey
    private int id;
    private String courseId;
    private String lessonTitle;
    private String lessonSummary;
    private String lessonLink;
    private String lessonDebug;
    private String lessonPracticalTitle;
    private String lessonPractical;
    private String favoriteLesson;
    private String firebaseId;
    @TypeConverters(DateConverter.class)
    private Date lessonCreate;
    @TypeConverters(DateConverter.class)
    private Date lessonEdit;

    /**
     *  This constructor is used by Room to create LessonEntry
     *  @param id                        column id for Lesson PrimaryKey
     *  @param courseId                  column CourseId for Course foreignKeys
     *  @param lessonTitle               column lesson title
     *  @param lessonSummary             column lesson summary
     *  @param lessonLink                column lesson link, Link the to something Familiar
     *  @param lessonDebug               column lesson debug,the difference between Link and lesson
     *  @param lessonPracticalTitle      column lesson Practical Title, example of the lesson
     *  @param lessonPractical           column lesson Practical summary
     *  @param favoriteLesson            column favorite Lesson
     *  @param firebaseId                column fire base Lesson id
     *  @param lessonCreate              column create lesson date
     *  @param lessonEdit                column edit lesson date
     */
    public Lesson(int id, String courseId, String lessonTitle, String lessonSummary,
                  String lessonLink, String lessonDebug, String lessonPracticalTitle,
                  String lessonPractical, String favoriteLesson, String firebaseId,
                  Date lessonCreate, Date lessonEdit) {
        this.id = id;
        this.courseId = courseId;
        this.lessonTitle = lessonTitle;
        this.lessonSummary = lessonSummary;
        this.lessonLink = lessonLink;
        this.lessonDebug = lessonDebug;
        this.lessonPracticalTitle = lessonPracticalTitle;
        this.lessonPractical = lessonPractical;
        this.favoriteLesson = favoriteLesson;
        this.firebaseId = firebaseId;
        this.lessonCreate = lessonCreate;
        this.lessonEdit = lessonEdit;
    }

    public int getId() {
        return id;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getLessonTitle() {
        return lessonTitle;
    }

    public String getLessonSummary() {
        return lessonSummary;
    }

    public String getLessonLink() {
        return lessonLink;
    }

    public String getLessonDebug() {
        return lessonDebug;
    }

    public String getLessonPracticalTitle() {
        return lessonPracticalTitle;
    }

    public String getLessonPractical() {
        return lessonPractical;
    }

    public String getFavoriteLesson() {
        return favoriteLesson;
    }

    public String getFirebaseId() {
        return firebaseId;
    }

    public Date getLessonCreate() {
        return lessonCreate;
    }

    public Date getLessonEdit() {
        return lessonEdit;
    }
}