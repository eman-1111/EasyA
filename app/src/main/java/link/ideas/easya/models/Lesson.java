package link.ideas.easya.models;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Eman on 4/25/2017.
 */

public class Lesson {

    private String lessonName;
    private String lessonLink;
    private String lessonImage;
    private Map<String, Object> timestampLastChanged;
    private Map<String, Object> timestampCreated;

    public Lesson() {
    }

    public Lesson(String lessonName, String lessonLink, String lessonImage,
                  Map<String, Object> timestampLastChanged, Map<String, Object> timestampCreated) {
        this.lessonName = lessonName;
        this.lessonLink = lessonLink;
        this.lessonImage = lessonImage;
        this.timestampLastChanged = timestampLastChanged;
        this.timestampCreated = timestampCreated;
    }

    public String getLessonName() {
        return lessonName;
    }

    public String getLessonLink() {
        return lessonLink;
    }

    public String getLessonImage() {
        return lessonImage;
    }

    public Map<String, Object> getTimestampLastChanged() {
        return timestampLastChanged;
    }

    public Map<String, Object> getTimestampCreated() {
        return timestampCreated;
    }

    public void setLessonName(String lessonName) {
        this.lessonName = lessonName;
    }

    public void setLessonLink(String lessonLink) {
        this.lessonLink = lessonLink;
    }

    public void setLessonImage(String lessonImage) {
        this.lessonImage = lessonImage;
    }

    public void setTimestampLastChanged(Map<String, Object> timestampLastChanged) {
        this.timestampLastChanged = timestampLastChanged;
    }

    public void setTimestampCreated(Map<String, Object> timestampCreated) {
        this.timestampCreated = timestampCreated;
    }
}
