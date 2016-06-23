package eman.app.android.easya;

/**
 * Created by eman_ashour on 6/9/2016.
 */
public class Image {
    String hashId;
    String image;
    String imageHD;

    public Image( String hashId, String image, String imageHD) {

        this.hashId = hashId;
        this.image = image;
        this.imageHD = imageHD;

    }

    public String getHashId() {
        return hashId;
    }

    public void setHashId(String hashId) {

        this.hashId = hashId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {

        this.image = image;
    }

    public String getImageHD() {
        return imageHD;
    }

    public void setImageHD(String imageHD) {

        this.imageHD = imageHD;
    }
}
