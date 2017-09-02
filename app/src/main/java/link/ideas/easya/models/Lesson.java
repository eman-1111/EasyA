package link.ideas.easya.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Map;

/**
 * Created by Eman on 4/25/2017.
 */

public class Lesson implements Parcelable {

    private String lessonName;
    private String lessonLink;
    private String lessonImage;
    private String lastEditName;
    private boolean isAgreed;
    private Map<String, Object> timestampLastChanged;
    private Map<String, Object> timestampCreated;

    private int mData;

    public Lesson() {
    }



    public Lesson(String lessonName, String lessonLink, String lessonImage,String lastEditName, boolean isAgreed,
                  Map<String, Object> timestampLastChanged, Map<String, Object> timestampCreated) {
        this.lessonName = lessonName;
        this.lessonLink = lessonLink;
        this.lessonImage = lessonImage;
        this.lastEditName = lastEditName;
        this.isAgreed = isAgreed;
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

    public String getLastEditName() {
        return lastEditName;
    }

    public boolean isAgreed() {
        return isAgreed;
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

    public void setLastEditName(String lastEditName) {
        this.lastEditName = lastEditName;
    }

    public void setAgreed(boolean agreed) {
        isAgreed = agreed;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(mData);
        out.writeString(lessonImage);
        out.writeString(lessonLink);
        out.writeString(lessonName);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Lesson> CREATOR
            = new Parcelable.Creator<Lesson>() {


        @Override
        public Lesson createFromParcel(Parcel in) {
            return new Lesson(in);
        }

        @Override
        public Lesson[] newArray(int size) {
            return new Lesson[size];
        }
    };

    private Lesson(Parcel in) {
        mData = in.readInt();
        lessonImage = in.readString();
        lessonLink = in.readString();
        lessonName = in.readString();
    }
}
