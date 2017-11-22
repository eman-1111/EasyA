package link.ideas.easya.data.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

/**
 * Created by Eman on 11/22/2017.
 */
@Database(entities = {Course.class, Lesson.class}, version = 1)
@TypeConverters(DateConverter.class)
public abstract class EasyADatabase extends RoomDatabase {

    // The associated DAOs for the database
    public abstract CourseDao courseModel();
    public abstract LessonDao lessonModel();

    private static final String LOG_TAG = EasyADatabase.class.getSimpleName();
    private static final String DATABASE_NAME = "easy.a";

    private static final Object LOCK = new Object();
    private static volatile EasyADatabase sInstance;

    public static EasyADatabase getInstance(Context context){
        if(sInstance == null){
            synchronized (LOCK){
                if(sInstance == null){
                    sInstance = Room.databaseBuilder(context.getApplicationContext(),
                            EasyADatabase.class, EasyADatabase.DATABASE_NAME).build();
                }
            }
        }
        return sInstance;
    }
}
