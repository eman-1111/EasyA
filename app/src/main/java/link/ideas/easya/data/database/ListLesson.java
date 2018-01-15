package link.ideas.easya.data.database;

/**
 * Created by Eman on 11/22/2017.
 */

/**
 * Simplified {@link Lesson} which only contains the details needed for the lesson list in
 * the {@link link.ideas.easya.adapter.LessonAdapter}
 */
public class ListLesson {
    private String courseName;
    private int id;
    private int courseId;
    private String lessonTitle;
    private String lessonSummary;
    private String favoriteLesson;
    private String firebaseId;


    /**
     *
     *  @param courseName                course name
     *  @param id                        column id for Lesson PrimaryKey
     *  @param courseId                  column CourseId for Course foreignKeys
     *  @param lessonTitle               column lesson title
     *  @param lessonSummary             column lesson summary
     *  @param favoriteLesson            column favorite Lesson
     */
    public ListLesson(int id, int courseId, String lessonTitle, String lessonSummary,
                  String courseName, String favoriteLesson, String firebaseId) {
        this.id = id;
        this.courseId = courseId;
        this.lessonTitle = lessonTitle;
        this.lessonSummary = lessonSummary;
        this.courseName = courseName;
        this.favoriteLesson = favoriteLesson;
        this.firebaseId = firebaseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public int getId() {
        return id;
    }

    public int getCourseId() {
        return courseId;
    }

    public String getLessonTitle() {
        return lessonTitle;
    }

    public String getLessonSummary() {
        return lessonSummary;
    }

    public String getFavoriteLesson() {
        return favoriteLesson;
    }

    public String getFirebaseId() {
        return firebaseId;
    }
}
