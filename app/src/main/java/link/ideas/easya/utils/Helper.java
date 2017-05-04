package link.ideas.easya.utils;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.TextView;

import com.google.firebase.database.ServerValue;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import link.ideas.easya.R;

/**
 * Created by Eman on 4/4/2017.
 */

public class Helper {

    // convert from bitmap to byte array
    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    // convert from byte array to bitmap
    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }


    public static String getFristName(String email) {
        int iend = email.indexOf(" ");
        String subString;
        if (iend != -1) {
            subString = email.substring(0, iend);
        } else {
            subString = email;
        }
        return subString;
    }


    /**
     * Encode user email to use it as a Firebase key (Firebase does not allow "." in the key name)
     * Encoded email is also used as "userEmail", list and item "owner" value
     */
    public static String encodeEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }

    public static HashMap<String, Object> getTimestampLastChanged() {
        /**
         * Set raw version of date to the ServerValue.TIMESTAMP value and save into dateCreatedMap
         */
        HashMap<String, Object> timestampNowHash = new HashMap<>();
        timestampNowHash.put(Constants.FIREBASE_PROPERTY_TIMESTAMP_LAST_CHANGED, ServerValue.TIMESTAMP);

        return timestampNowHash;
    }

    public static Map<String, Object> getTimestampCreated() {
        HashMap<String, Object> timestampNowHash = new HashMap<>();
        timestampNowHash.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);

        return timestampNowHash;
    }

    public static int getCourseColor(int courseItemColor) {
        if (courseItemColor == 0) {
            return R.drawable.couse1b;
        } else if (courseItemColor == 1) {
            return R.drawable.couse2b;
        } else if (courseItemColor == 2) {
            return R.drawable.couse3b;
        } else if (courseItemColor == 3) {
            return R.drawable.couse4b;
        } else {
            return R.drawable.couse1b;
        }

    }

    public static boolean startDialog(final Context mContext, String title, String description) {


        LayoutInflater li = LayoutInflater.from(mContext);
        View promptsView = li.inflate(R.layout.warn_dialog, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext, R.style.DialogTheme );

        alertDialogBuilder.setView(promptsView);

        final TextView txtTitle = (TextView) promptsView
                .findViewById(R.id.txt_title);

        final TextView txtDescription = (TextView) promptsView
                .findViewById(R.id.txt_description);

        final TextView txtOK = (TextView) promptsView
                .findViewById(R.id.txt_ok);

        txtTitle.setText(title);
        txtDescription.setText(description);
        final boolean[] isOkay = {false};
        // set dialog message


        // create alert dialog
        final  AlertDialog alertDialog = alertDialogBuilder.create();
        txtOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isOkay[0] = true;
                alertDialog.cancel();
            }
        });

        alertDialog.show();
        return isOkay[0];
    }


    public static String decodeEmail(String userEmail) {
        return userEmail.replace(",", ".");
    }

    public static Bitmap getImageCompress(Bitmap image) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 0, bytes);
        byte[] bitmapdata = bytes.toByteArray();
        return BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length);
    }


    public static void updateWidgets(Context context) {
        // Setting the package ensures that only components in our app will receive the broadcast
        Intent dataUpdatedIntent = new Intent(Constants.ACTION_DATA_UPDATED)
                .setPackage(context.getPackageName());
        context.sendBroadcast(dataUpdatedIntent);
    }
}
