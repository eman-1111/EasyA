package link.ideas.easya.data.database;

import android.arch.persistence.room.TypeConverter;

import java.util.Date;

/**
 * Created by Eman on 11/21/2017.
 */
/* {@link TypeConverter} for long to {@link Date}
 * <p>
 *  this store the date as long in the database, but return it as a {@link Date}
 */

public class DateConverter {
    @TypeConverter
    public static Date toDate(Long timestamp){
        return timestamp == null ? null : new Date(timestamp);
    }

    @TypeConverter
    public static Long toTimestamp(Date date){
        return date == null ? null : date.getTime();
    }
}
