package eman.app.android.easya.firebase;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.firebase.client.ServerValue;

import java.util.HashMap;

import eman.app.android.easya.utils.Constants;

/**
 * Created by eman_ashour on 7/10/2016.
 */
public class CourseContent {
    private String COURSE_ID;
    private String LESSON_LIST;
    private String COURSE_NAME;
    private String TEACHER_NAME;
    private String TEACHER_EMAIL;
    private String TEACHER_PHOTO_URL;
    private HashMap<String, Object> timestampLastChanged;
    private HashMap<String, Object> timestampCreated;
    public CourseContent() {
    }

    public CourseContent(String COURSE_ID, String COURSE_NAME, String TEACHER_NAME,
                         String TEACHER_PHOTO_URL, String TEACHER_EMAIL,
                         HashMap<String, Object> timestampCreated, String LESSON_LIST) {

        this.COURSE_ID = COURSE_ID;
        this.COURSE_NAME = COURSE_NAME;
        this.TEACHER_NAME = TEACHER_NAME;
        this.TEACHER_PHOTO_URL = TEACHER_PHOTO_URL;
        this.TEACHER_EMAIL = TEACHER_EMAIL;
        this.LESSON_LIST = LESSON_LIST;

        this.timestampCreated = timestampCreated;
        HashMap<String, Object> timestampNowObject = new HashMap<String, Object>();
        timestampNowObject.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);
        this.timestampLastChanged = timestampNowObject;
    }
    public String getLESSON_LIST() {
        return LESSON_LIST;
    }

    public String getCOURSE_NAME() {
        return COURSE_NAME;
    }

    public String getTEACHER_NAME() {
        return TEACHER_NAME;
    }

    public String getTEACHER_EMAIL() {
        return TEACHER_EMAIL;
    }

    public String getTEACHER_PHOTO_URL() {
        return TEACHER_PHOTO_URL;
    }

    public String getCOURSE_ID() {
        return COURSE_ID;
    }

    public HashMap<String, Object> getTimestampLastChanged() {
        return timestampLastChanged;
    }

    public HashMap<String, Object> getTimestampCreated() {
        return timestampCreated;
    }

    @JsonIgnore
    public long getTimestampLastChangedLong() {

        return (long) timestampLastChanged.get(Constants.FIREBASE_PROPERTY_TIMESTAMP);
    }

    @JsonIgnore
    public long getTimestampCreatedLong() {
        return (long) timestampLastChanged.get(Constants.FIREBASE_PROPERTY_TIMESTAMP);
    }
}
