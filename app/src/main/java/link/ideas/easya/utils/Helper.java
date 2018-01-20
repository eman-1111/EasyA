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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.TextView;

import com.google.firebase.database.ServerValue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

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
    public static boolean empty( final String s ) {
        // Null-safe, short-circuit evaluation.
        return s == null || s.trim().isEmpty();
    }
    public static Bitmap getImageCompress(Bitmap original) {
        Log.e("Original   dimensions", original.getByteCount()+" "+original.getHeight()+" "+original.getWidth());

        final int maxSize = 750;
        int outWidth;
        int outHeight;
        int inWidth = original.getWidth();
        int inHeight = original.getHeight();
        if(inWidth > inHeight){
            outWidth = maxSize;
            outHeight = (inHeight * maxSize) / inWidth;
        } else {
            outHeight = maxSize;
            outWidth = (inWidth * maxSize) / inHeight;
        }

        Bitmap decoded = Bitmap.createScaledBitmap(original, outWidth, outHeight, false);

        Log.e("Compressed dimensions", decoded.getByteCount()+" "+decoded.getHeight()+" "+decoded.getWidth());
        return decoded;
    }


    public static void updateWidgets(Context context) {
        // Setting the package ensures that only components in our app will receive the broadcast
        Intent dataUpdatedIntent = new Intent(Constants.ACTION_DATA_UPDATED)
                .setPackage(context.getPackageName());
        context.sendBroadcast(dataUpdatedIntent);
    }

    public static Date getNormalizedUtcDateForToday() {
        long normalizedMilli = getNormalizedUtcMsForToday();
        return new Date(normalizedMilli);
    }
    /**
     * This method returns the number of milliseconds (UTC time) for today's date at midnight in
     * the local time zone. For example, if you live in California and the day is September 20th,
     * 2016 and it is 6:30 PM, it will return 1474329600000. Now, if you plug this number into an
     * Epoch time converter, you may be confused that it tells you this time stamp represents 8:00
     * PM on September 19th local time, rather than September 20th. We're concerned with the GMT
     * date here though, which is correct, stating September 20th, 2016 at midnight.
     * <p>
     * As another example, if you are in Hong Kong and the day is September 20th, 2016 and it is
     * 6:30 PM, this method will return 1474329600000. Again, if you plug this number into an Epoch
     * time converter, you won't get midnight for your local time zone. Just keep in mind that we
     * are just looking at the GMT date here.
     * <p>
     * This method will ALWAYS return the date at midnight (in GMT time) for the time zone you
     * are currently in. In other words, the GMT date will always represent your date.
     * <p>
     * Since UTC / GMT time are the standard for all time zones in the world, we use it to
     * normalize our dates that are stored in the database. When we extract values from the
     * database, we adjust for the current time zone using time zone offsets.
     *
     * @return The number of milliseconds (UTC / GMT) for today's date at midnight in the local
     * time zone
     */
    public static long getNormalizedUtcMsForToday() {

        /*
         * This number represents the number of milliseconds that have elapsed since January
         * 1st, 1970 at midnight in the GMT time zone.
         */
        long utcNowMillis = System.currentTimeMillis();

        /*
         * This TimeZone represents the device's current time zone. It provides us with a means
         * of acquiring the offset for local time from a UTC time stamp.
         */
        TimeZone currentTimeZone = TimeZone.getDefault();

        /*
         * The getOffset method returns the number of milliseconds to add to UTC time to get the
         * elapsed time since the epoch for our current time zone. We pass the current UTC time
         * into this method so it can determine changes to account for daylight savings time.
         */
        long gmtOffsetMillis = currentTimeZone.getOffset(utcNowMillis);

        /*
         * UTC time is measured in milliseconds from January 1, 1970 at midnight from the GMT
         * time zone. Depending on your time zone, the time since January 1, 1970 at midnight (GMT)
         * will be greater or smaller. This variable represents the number of milliseconds since
         * January 1, 1970 (GMT) time.
         */
        long timeSinceEpochLocalTimeMillis = utcNowMillis + gmtOffsetMillis;

        /* This method simply converts milliseconds to days, disregarding any fractional days */
        long daysSinceEpochLocal = TimeUnit.MILLISECONDS.toDays(timeSinceEpochLocalTimeMillis);

        /*
         * Finally, we convert back to milliseconds. This time stamp represents today's date at
         * midnight in GMT time. We will need to account for local time zone offsets when
         * extracting this information from the database.
         */

        return TimeUnit.DAYS.toMillis(daysSinceEpochLocal);
    }
}
