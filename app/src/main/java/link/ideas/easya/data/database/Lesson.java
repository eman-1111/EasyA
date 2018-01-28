package link.ideas.easya.data.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by Eman on 11/22/2017.
 */
@Entity(tableName = "lesson", foreignKeys =
        {@ForeignKey(entity = Course.class, parentColumns = "id", childColumns = "courseId")})
public class Lesson implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private int lessonId;
    private int courseId;
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

    private String lastEditName;
    private boolean isAgreed;
    private String lessonImage;
    private String linkImage;
    private String appImage;
    private long lessonCreateF;
    private long lessonEditF;

    /**
     * This constructor is used by Room to create LessonEntry
     *
     * @param lessonId             column id for Lesson PrimaryKey
     * @param courseId             column CourseId for Course foreignKeys
     * @param lessonTitle          column lesson title
     * @param lessonSummary        column lesson summary
     * @param lessonLink           column lesson link, Link the to something Familiar
     * @param lessonDebug          column lesson debug,the difference between Link and lesson
     * @param lessonPracticalTitle column lesson Practical Title, example of the lesson
     * @param lessonPractical      column lesson Practical summary
     * @param favoriteLesson       column favorite Lesson
     * @param firebaseId           column fire base Lesson id
     * @param lessonCreate         column create lesson date
     * @param lessonEdit           column edit lesson date
     */
    public Lesson(int lessonId, int courseId, String lessonTitle, String lessonSummary,
                  String lessonLink, String lessonDebug, String lessonPracticalTitle,
                  String lessonPractical, String favoriteLesson, String firebaseId,
                  Date lessonCreate, Date lessonEdit) {
        this.lessonId = lessonId;
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

    /**
     * @param courseId             column CourseId for Course foreignKeys
     * @param lessonTitle          column lesson title
     * @param lessonSummary        column lesson summary
     * @param lessonLink           column lesson link, Link the to something Familiar
     * @param lessonDebug          column lesson debug,the difference between Link and lesson
     * @param lessonPracticalTitle column lesson Practical Title, example of the lesson
     * @param lessonPractical      column lesson Practical summary
     * @param favoriteLesson       column favorite Lesson
     * @param firebaseId           column fire base Lesson id
     * @param lessonCreate         column create lesson date
     * @param lessonEdit           column edit lesson date
     */
    @Ignore
    public Lesson(int courseId, String lessonTitle, String lessonSummary,
                  String lessonLink, String lessonDebug, String lessonPracticalTitle,
                  String lessonPractical, String favoriteLesson, String firebaseId,
                  Date lessonCreate, Date lessonEdit) {
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

    @Ignore
    public Lesson() {

    }

    @Ignore
    public Lesson(String lessonTitle, String lessonLink, String lessonImage, String lastEditName,
                  boolean isAgreed, long lessonCreate, long lessonEdit) {
        this.lessonTitle = lessonTitle;
        this.lessonLink = lessonLink;
        this.lessonImage = lessonImage;
        this.lastEditName = lastEditName;
        this.isAgreed = isAgreed;
        this.lessonCreateF = lessonCreate;
        this.lessonEditF = lessonEdit;
    }

    @Ignore
    public Lesson(String lessonSummary, String linkImage, String lessonPracticalTitle,
                  String lessonPractical, String appImage, String lessonDebug) {

        this.lessonSummary = lessonSummary;
        this.linkImage = linkImage;
        this.lessonPracticalTitle = lessonPracticalTitle;
        this.lessonPractical = lessonPractical;
        this.appImage = appImage;
        this.lessonDebug = lessonDebug;
    }

    public int getLessonId() {
        return lessonId;
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

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public void setLessonTitle(String lessonTitle) {
        this.lessonTitle = lessonTitle;
    }

    public void setLessonSummary(String lessonSummary) {
        this.lessonSummary = lessonSummary;
    }

    public void setLessonLink(String lessonLink) {
        this.lessonLink = lessonLink;
    }

    public void setLessonDebug(String lessonDebug) {
        this.lessonDebug = lessonDebug;
    }

    public void setLessonPracticalTitle(String lessonPracticalTitle) {
        this.lessonPracticalTitle = lessonPracticalTitle;
    }

    public void setLessonPractical(String lessonPractical) {
        this.lessonPractical = lessonPractical;
    }

    public void setFavoriteLesson(String favoriteLesson) {
        this.favoriteLesson = favoriteLesson;
    }

    public void setFirebaseId(String firebaseId) {
        this.firebaseId = firebaseId;
    }

    public void setLessonCreate(Date lessonCreate) {
        this.lessonCreate = lessonCreate;
    }

    public void setLessonEdit(Date lessonEdit) {
        this.lessonEdit = lessonEdit;
    }

    public void setLessonId(int lessonId) {
        this.lessonId = lessonId;
    }

    public String getLastEditName() {
        return lastEditName;
    }

    public void setLastEditName(String lastEditName) {
        this.lastEditName = lastEditName;
    }

    public boolean isAgreed() {
        return isAgreed;
    }

    public void setAgreed(boolean agreed) {
        isAgreed = agreed;
    }

    public String getLessonImage() {
        return lessonImage;
    }

    public void setLessonImage(String lessonImage) {
        this.lessonImage = lessonImage;
    }

    public String getLinkImage() {
        return linkImage;
    }

    public void setLinkImage(String linkImage) {
        this.linkImage = linkImage;
    }

    public String getAppImage() {
        return appImage;
    }

    public void setAppImage(String appImage) {
        this.appImage = appImage;
    }

    public long getLessonCreateF() {
        return lessonCreateF;
    }

    public void setLessonCreateF(long lessonCreateF) {
        this.lessonCreateF = lessonCreateF;
    }

    public long getLessonEditF() {
        return lessonEditF;
    }

    public void setLessonEditF(long lessonEditF) {
        this.lessonEditF = lessonEditF;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(lessonId);
        dest.writeInt(courseId);
        dest.writeString(lessonTitle);
        dest.writeString(lessonSummary);
        dest.writeString(lessonLink);
        dest.writeString(lessonDebug);
        dest.writeString(lessonPracticalTitle);
        dest.writeString(lessonPractical);
        dest.writeString(favoriteLesson);
        dest.writeString(firebaseId);
        dest.writeLong(lessonCreate != null ? lessonCreate.getTime() : -1L);
        dest.writeLong(lessonEdit != null ? lessonEdit.getTime() : -1L);
        dest.writeString(lastEditName);
        dest.writeByte((byte) (isAgreed ? 0x01 : 0x00));
        dest.writeString(lessonImage);
        dest.writeString(linkImage);
        dest.writeString(appImage);
        dest.writeLong(lessonCreateF);
        dest.writeLong(lessonEditF);
    }

    protected Lesson(Parcel in) {
        lessonId = in.readInt();
        courseId = in.readInt();
        lessonTitle = in.readString();
        lessonSummary = in.readString();
        lessonLink = in.readString();
        lessonDebug = in.readString();
        lessonPracticalTitle = in.readString();
        lessonPractical = in.readString();
        favoriteLesson = in.readString();
        firebaseId = in.readString();
        long tmpLessonCreate = in.readLong();
        lessonCreate = tmpLessonCreate != -1 ? new Date(tmpLessonCreate) : null;
        long tmpLessonEdit = in.readLong();
        lessonEdit = tmpLessonEdit != -1 ? new Date(tmpLessonEdit) : null;
        lastEditName = in.readString();
        isAgreed = in.readByte() != 0x00;
        lessonImage = in.readString();
        linkImage = in.readString();
        appImage = in.readString();
        lessonCreateF = in.readLong();
        lessonEditF = in.readLong();
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Lesson> CREATOR = new Parcelable.Creator<Lesson>() {
        @Override
        public Lesson createFromParcel(Parcel in) {
            return new Lesson(in);
        }

        @Override
        public Lesson[] newArray(int size) {
            return new Lesson[size];
        }
    };
}
