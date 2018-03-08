package link.ideas.easya.data.network;


import link.ideas.easya.models.Image;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Eman on 3/7/2018.
 */

public interface ApiInterface {

    @GET("rest/?method=flickr.photos.search")
    Call<Image> getImages(@Query("api_key") String key,
                          @Query("tags") String query,
                          @Query("per_page") String pages,
                          @Query("format") String format,
                          @Query("nojsoncallback") String nojsoncallback);
}
