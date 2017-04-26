package link.ideas.easya.models;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Eman on 4/25/2017.
 */

public class Course {
    private String courseName;
    private String teacherName;
    private String teacherEmail;
    private String teacherPhotoUrl;
    private int colorId;
    private Map<String, Object> timestampLastChanged;
    private Map<String, Object> timestampCreated;

    public Course() {
    }

    public Course(String courseName, String teacherName, String teacherEmail, String teacherPhotoUrl, int colorId,
                  Map<String, Object> timestampLastChanged, Map<String, Object> timestampCreated) {
        this.teacherEmail = teacherEmail;
        this.courseName = courseName;
        this.teacherName = teacherName;
        this.teacherPhotoUrl = teacherPhotoUrl;
        this.colorId = colorId;
        this.timestampLastChanged = timestampLastChanged;
        this.timestampCreated = timestampCreated;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public String getTeacherPhotoUrl() {
        return teacherPhotoUrl;
    }

    public int getColorId() {
        return colorId;
    }

    public Map<String, Object> getTimestampLastChanged() {
        return timestampLastChanged;
    }

    public Map<String, Object> getTimestampCreated() {
        return timestampCreated;
    }

    public String getTeacherEmail() {
        return teacherEmail;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public void setTeacherPhotoUrl(String teacherPhotoUrl) {
        this.teacherPhotoUrl = teacherPhotoUrl;
    }

    public void setColorId(int colorId) {
        this.colorId = colorId;
    }

    public void setTimestampLastChanged(Map<String, Object> timestampLastChanged) {
        this.timestampLastChanged = timestampLastChanged;
    }

    public void setTimestampCreated(Map<String, Object> timestampCreated) {
        this.timestampCreated = timestampCreated;
    }

    public void setTeacherEmail(String teacherEmail) {
        this.teacherEmail = teacherEmail;
    }
}
