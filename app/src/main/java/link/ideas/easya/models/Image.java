package link.ideas.easya.models;

/**
 * Created by eman_ashour on 6/9/2016.
 */
public class Image {
    private String id;
    private String secret;
    private String server;
    private String farm;

    public Image(String id, String secret, String server, String farm) {
        this.id = id;
        this.secret = secret;
        this.server = server;
        this.farm = farm;
    }

    public String getId() {
        return id;
    }

    public String getSecret() {
        return secret;
    }

    public String getServer() {
        return server;
    }

    public String getFarm() {
        return farm;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public void setFarm(String farm) {
        this.farm = farm;
    }
}
