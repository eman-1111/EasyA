package link.ideas.easya.models;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Eman on 4/25/2017.
 */

public class LessonDetail {

    private String lessonSummery;
    private String linkImage;
    private String lessonDebug;
    private String lessonAppTitle;
    private String lessonAppDescription;
    private String appImage;
    private Map<String, Object> timestampLastChanged;
    private Map<String, Object> timestampCreated;

    public LessonDetail() {
    }

    public LessonDetail(String lessonSummery, String linkImage, String lessonAppTitle,
                        String lessonAppDescription, String appImage,String lessonDebug,
                        Map<String, Object> timestampLastChanged, Map<String, Object> timestampCreated) {

        this.lessonSummery = lessonSummery;
        this.linkImage = linkImage;
        this.lessonAppTitle = lessonAppTitle;
        this.lessonAppDescription = lessonAppDescription;
        this.appImage = appImage;
        this.lessonDebug = lessonDebug;
        this.timestampLastChanged = timestampLastChanged;
        this.timestampCreated = timestampCreated;
    }

    public String getLessonSummery() {
        return lessonSummery;
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

    public String getLinkImage() {
        return linkImage;
    }

    public String getLessonDebug() {
        return lessonDebug;
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

    public void setLessonAppTitle(String lessonAppTitle) {
        this.lessonAppTitle = lessonAppTitle;
    }

    public void setLessonAppDescription(String lessonAppDescription) {
        this.lessonAppDescription = lessonAppDescription;
    }

    public void setAppImage(String appImage) {
        this.appImage = appImage;
    }

    public void setLinkImage(String linkImage) {
        this.linkImage = linkImage;
    }

    public void setLessonDebug(String lessonDebug) {
        this.lessonDebug = lessonDebug;
    }

    public void setTimestampLastChanged(Map<String, Object> timestampLastChanged) {
        this.timestampLastChanged = timestampLastChanged;
    }

    public void setTimestampCreated(Map<String, Object> timestampCreated) {
        this.timestampCreated = timestampCreated;
    }
}
