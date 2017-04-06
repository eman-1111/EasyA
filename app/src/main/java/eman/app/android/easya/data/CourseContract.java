package eman.app.android.easya.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.format.Time;

/**
 * Created by eman_ashour on 4/19/2016.
 */
public class CourseContract {
    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "eman.app.android.easya";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);


    public static final String PATH_COURSE = "course";

    public static final String PATH_SUBJECT = "subject";



    public static final class CourseEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_COURSE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_COURSE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_COURSE;

        public static final String TABLE_NAME = "course";


        public static final String COLUMN_COURSE_ID = "course_id";
        public static final String COLUMN_COURSE_NAME = "course_name";
        public static final String COLUMN_TEACHER_NAME = "teacher_name";


        public static final String COLUMN_TEACHER_EMAIL= "teacher_email";

        public static final String COLUMN_TEACHER_PHOTO_URL = "teacher_photo";



        public static Uri buildCourseUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
        public static Uri buildCourseWithID(String courseID) {
            return CONTENT_URI.buildUpon().appendPath(courseID).build();
        }


        public static String getCourseIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }


    }

    public static final class SubjectEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_SUBJECT).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SUBJECT;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SUBJECT;

        public static final String TABLE_NAME = "subject";


        // Weather id as returned by API, to identify the icon to be used
        public static final String COLUMN_COURSE_ID = "course_s_id";

        public static final String COLUMN_LESSON_TITLE = "lesson_title";
        public static final String COLUMN_LESSON_OUTLINE= "lesson_outline";
        public static final String COLUMN_LESSON_OUTLINE_IMAGE= "lesson_outline_image";
        public static final String COLUMN_LESSON_LINK = "lesson_link";
        public static final String COLUMN_LESSON_LINK_IMAGE = "lesson_link_image";
        public static final String COLUMN_LESSON_DEBUG = "lesson_debug";
        public static final String COLUMN_LESSON_PRACTICAL_TITLE= "lesson_practical_title";
        public static final String COLUMN_LESSON_PRACTICAL= "lesson_practical";
        public static final String COLUMN_LESSON_PRACTICAL_IMAGE= "lesson_practical_image";
        public static final String COLUMN_FAVORITE = "favorite";


        public static Uri buildSubjectUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
        public static Uri buildSubjectsUri() {
            return CONTENT_URI.buildUpon().build();
        }
        public static Uri buildSubjectWithID(String courseID) {
            return CONTENT_URI.buildUpon().appendPath(courseID).build();
        }
        public static Uri buildCourseWithIDAndTitle(String courseID, String lessonTitle) {
            return CONTENT_URI.buildUpon().appendPath(courseID).appendPath(lessonTitle).build();
        }


        public static String getSubjectIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static String getSubjectTitleFromUri(Uri uri) {
            return uri.getPathSegments().get(2);
        }


    }
}

