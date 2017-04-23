package link.ideas.easya.models;

/**
 * Created by Eman on 4/23/2017.
 */

public class User {

    private String name;
    private String photoUrl;

    public User() {
    }

    public User(String name, String photoUrl) {
        this.name = name;
        this.photoUrl = photoUrl;
    }

    public String getName() {
        return name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
