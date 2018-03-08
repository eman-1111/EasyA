package link.ideas.easya.data.network;

/**
 * Created by Eman on 12/17/2017.
 */

public class ApiUtils {
    public static ApiInterface getAppService() {
        return RetrofitClient.getClient().create(ApiInterface.class);
    }
}
