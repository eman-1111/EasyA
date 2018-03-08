package link.ideas.easya.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by eman_ashour on 6/9/2016.
 */
public class Image {

    @SerializedName("photos")
    private Photos photos;

    public Photos getPhotos() {
        return photos;
    }

    public class Photos {
        @SerializedName("page")
        private int page;

        @SerializedName("pages")
        private int pages;

        @SerializedName("perpage")
        private int perpage;

        @SerializedName("total")
        private String total;

        @SerializedName("photo")
        private ArrayList<photo> photo;

        public int getPage() {
            return page;
        }

        public int getPages() {
            return pages;
        }

        public int getPerpage() {
            return perpage;
        }

        public String getTotal() {
            return total;
        }

        public ArrayList<photo> getPhoto() {
            return photo;
        }

        public class photo {
            @SerializedName("id")
            private String id;

            @SerializedName("owner")
            private String owner;

            @SerializedName("secret")
            private String secret;

            @SerializedName("server")
            private String server;

            @SerializedName("farm")
            private int farm;

            @SerializedName("title")
            private String title;

            @SerializedName("ispublic")
            private int ispublic;

            @SerializedName("isfriend")
            private int isfriend;

            @SerializedName("isfamily")
            private int isfamily;

            public String getId() {
                return id;
            }

            public String getOwner() {
                return owner;
            }

            public String getSecret() {
                return secret;
            }

            public String getServer() {
                return server;
            }

            public int getFarm() {
                return farm;
            }

            public String getTitle() {
                return title;
            }

            public int getIspublic() {
                return ispublic;
            }

            public int getIsfriend() {
                return isfriend;
            }

            public int getIsfamily() {
                return isfamily;
            }
        }
    }
}
