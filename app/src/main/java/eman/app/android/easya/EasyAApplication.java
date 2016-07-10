package eman.app.android.easya;

import com.firebase.client.Firebase;

/**
 * Created by eman_ashour on 7/10/2016.
 */
public class EasyAApplication extends android.app.Application  {

    @Override
    public void onCreate() {
        super.onCreate();
        /* Initialize Firebase */
        Firebase.setAndroidContext(this);
    }

}