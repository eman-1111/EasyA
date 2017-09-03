package link.ideas.easya.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by eman_ashour on 4/19/2016.
 */
public class CourseDBHelper  extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 2;

    static final String DATABASE_NAME = "course.db";

    public CourseDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

  
        final String SQL_CREATE_COURSE_TABLE = "CREATE TABLE " + CourseContract.CourseEntry.TABLE_NAME + " (" +

                CourseContract.CourseEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                CourseContract.CourseEntry.COLUMN_COURSE_ID + " TEXT NOT NULl, " +
                CourseContract.CourseEntry.COLUMN_COURSE_NAME + " TEXT NOT NULL, " +
                CourseContract.CourseEntry.COLUMN_TEACHER_NAME + " TEXT NOT NULL, " +
                CourseContract.CourseEntry.COLUMN_TEACHER_PHOTO_URL + " TEXT, " +
                CourseContract.CourseEntry.COLUMN_TEACHER_COLOR + " INTEGER, " +
                CourseContract.CourseEntry.COLUMN_FIREBASE_ID + " TEXT, " +
                CourseContract.CourseEntry.COLUMN_TEACHER_EMAIL + " TEXT, " +

                // it's created a UNIQUE constraint with REPLACE strategy
                " UNIQUE (" + CourseContract.CourseEntry.COLUMN_COURSE_ID +
                ") ON CONFLICT REPLACE);";

        final String SQL_CREATE_SUBJECT_TABLE = "CREATE TABLE " + CourseContract.SubjectEntry.TABLE_NAME + " (" +

                CourseContract.SubjectEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                CourseContract.SubjectEntry.COLUMN_COURSE_ID + " TEXT NOT NULl, " +
                CourseContract.SubjectEntry.COLUMN_LESSON_TITLE + " TEXT NOT NULL, " +
                CourseContract.SubjectEntry.COLUMN_LESSON_OUTLINE + " TEXT NOT NULL, " +
                CourseContract.SubjectEntry.COLUMN_LESSON_LINK + " TEXT , " +
                CourseContract.SubjectEntry.COLUMN_LESSON_DEBUG + " TEXT ," +
                CourseContract.SubjectEntry.COLUMN_LESSON_PRACTICAL_TITLE + " TEXT , " +
                CourseContract.SubjectEntry.COLUMN_FAVORITE + " TEXT , " +
                CourseContract.SubjectEntry.COLUMN_FIREBASE_ID + " TEXT , " +
                CourseContract.SubjectEntry.COLUMN_LESSON_PRACTICAL + " TEXT , " +

                "UNIQUE (" + CourseContract.SubjectEntry.COLUMN_LESSON_TITLE +
                        ") ON CONFLICT REPLACE);";


        sqLiteDatabase.execSQL(SQL_CREATE_COURSE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_SUBJECT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CourseContract.CourseEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CourseContract.SubjectEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}

