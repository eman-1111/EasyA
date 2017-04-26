package link.ideas.easya.models;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Eman on 4/23/2017.
 */

public class User {

    private String name;
    private String photoUrl;
    private Map<String, Object> timestampAdded;

    public User() {
    }

    public User(String name, String photoUrl, Map<String, Object> timestampAdded) {

        this.name = name;
        this.photoUrl = photoUrl;
        this.timestampAdded = timestampAdded;
    }


    public String getName() {
        return name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public Map<String, Object> getTimestampAdded() {
        return timestampAdded;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public void setTimestampAdded(Map<String, Object> timestampAdded) {
        this.timestampAdded = timestampAdded;
    }
}
