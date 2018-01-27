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

    public LessonDetail() {
    }

    public LessonDetail(String lessonSummery, String linkImage, String lessonAppTitle,
                        String lessonAppDescription, String appImage,String lessonDebug) {

        this.lessonSummery = lessonSummery;
        this.linkImage = linkImage;
        this.lessonAppTitle = lessonAppTitle;
        this.lessonAppDescription = lessonAppDescription;
        this.appImage = appImage;
        this.lessonDebug = lessonDebug;
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

}
