package link.ideas.easya.models;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Eman on 4/25/2017.
 */

public class LessonDetail {

    private String lessonSummery;
    private String summeryImage;
    private String lessonAppTitle;
    private String lessonAppDescription;
    private String appImage;
    private Map<String, Object> timestampLastChanged;
    private Map<String, Object> timestampCreated;

    public LessonDetail() {
    }

    public LessonDetail(String lessonSummery, String summeryImage, String lessonAppTitle,
                        String lessonAppDescription, String appImage,
                        Map<String, Object> timestampLastChanged, Map<String, Object> timestampCreated) {

        this.lessonSummery = lessonSummery;
        this.summeryImage = summeryImage;
        this.lessonAppTitle = lessonAppTitle;
        this.lessonAppDescription = lessonAppDescription;
        this.appImage = appImage;
        this.timestampLastChanged = timestampLastChanged;
        this.timestampCreated = timestampCreated;
    }

    public String getLessonSummery() {
        return lessonSummery;
    }

    public String getSummeryImage() {
        return summeryImage;
    }

    public String getLessonAppTitle() {
        return lessonAppTitle;
    }

    public String getLessonAppDescription() {
        return lessonAppDescription;
    }

    public String getAppImage() {
        return appImage;
    }

    public Map<String, Object> getTimestampLastChanged() {
        return timestampLastChanged;
    }

    public Map<String, Object> getTimestampCreated() {
        return timestampCreated;
    }

    public void setLessonSummery(String lessonSummery) {
        this.lessonSummery = lessonSummery;
    }

    public void setSummeryImage(String summeryImage) {
        this.summeryImage = summeryImage;
    }

    public void setLessonAppTitle(String lessonAppTitle) {
        this.lessonAppTitle = lessonAppTitle;
    }

    public void setLessonAppDescription(String lessonAppDescription) {
        this.lessonAppDescription = lessonAppDescription;
    }

    public void setAppImage(String appImage) {
        this.appImage = appImage;
    }

    public void setTimestampLastChanged(Map<String, Object> timestampLastChanged) {
        this.timestampLastChanged = timestampLastChanged;
    }

    public void setTimestampCreated(Map<String, Object> timestampCreated) {
        this.timestampCreated = timestampCreated;
    }
}
