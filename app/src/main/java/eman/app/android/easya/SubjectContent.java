package eman.app.android.easya;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.firebase.client.ServerValue;

import java.util.HashMap;

import eman.app.android.easya.utils.Constants;

/**
 * Created by eman_ashour on 7/10/2016.
 */
public class SubjectContent {

    private String COURSE_ID;
    private String LESSON_TITLE;
    private String LESSON_OUTLINE;
    private String LESSON_OUTLINE_IMAGE;
    private String LESSON_LINK;
    private String LESSON_LINK_IMAGE;
    private String LESSON_DEBUG;
    private String LESSON_DEBUG_IMAGE;
    private String LESSON_PRACTICAL_TITLE;
    private String LESSON_PRACTICAL;
    private String LESSON_PRACTICAL_IMAGE;
    private String FAVORITE;
    private HashMap<String, Object> timestampLastChanged;
    private HashMap<String, Object> timestampCreated;

    public SubjectContent() {
    }

    public SubjectContent(String COURSE_ID, String LESSON_TITLE, String LESSON_OUTLINE,
                          String LESSON_OUTLINE_IMAGE, String LESSON_LINK, String LESSON_LINK_IMAGE,
                          String LESSON_DEBUG, String LESSON_DEBUG_IMAGE,
                          String LESSON_PRACTICAL_TITLE, String LESSON_PRACTICAL,
                          String LESSON_PRACTICAL_IMAGE, String FAVORITE,
                          HashMap<String, Object> timestampCreated) {
        this.COURSE_ID = COURSE_ID;
        this.LESSON_TITLE = LESSON_TITLE;
        this.LESSON_OUTLINE = LESSON_OUTLINE;
        this.LESSON_OUTLINE_IMAGE = LESSON_OUTLINE_IMAGE;
        this.LESSON_LINK = LESSON_LINK;
        this.LESSON_LINK_IMAGE = LESSON_LINK_IMAGE;
        this.LESSON_DEBUG = LESSON_DEBUG;
        this.LESSON_DEBUG_IMAGE = LESSON_DEBUG_IMAGE;
        this.LESSON_PRACTICAL_TITLE = LESSON_PRACTICAL_TITLE;
        this.LESSON_PRACTICAL = LESSON_PRACTICAL;
        this.LESSON_PRACTICAL_IMAGE = LESSON_PRACTICAL_IMAGE;
        this.FAVORITE = FAVORITE;

        this.timestampCreated = timestampCreated;
        HashMap<String, Object> timestampNowObject = new HashMap<String, Object>();
        timestampNowObject.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);
        this.timestampLastChanged = timestampNowObject;
    }

    public String getLESSON_TITLE() {
        return LESSON_TITLE;
    }

    public String getCOURSE_ID() {
        return COURSE_ID;
    }

    public String getLESSON_OUTLINE() {
        return LESSON_OUTLINE;
    }

    public String getLESSON_OUTLINE_IMAGE() {
        return LESSON_OUTLINE_IMAGE;
    }

    public String getLESSON_LINK() {
        return LESSON_LINK;
    }

    public String getLESSON_DEBUG() {
        return LESSON_DEBUG;
    }

    public String getLESSON_DEBUG_IMAGE() {
        return LESSON_DEBUG_IMAGE;
    }

    public String getLESSON_LINK_IMAGE() {
        return LESSON_LINK_IMAGE;
    }

    public String getLESSON_PRACTICAL_TITLE() {
        return LESSON_PRACTICAL_TITLE;
    }

    public String getLESSON_PRACTICAL() {
        return LESSON_PRACTICAL;
    }

    public String getLESSON_PRACTICAL_IMAGE() {
        return LESSON_PRACTICAL_IMAGE;
    }

    public String getFAVORITE() {
        return FAVORITE;
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
