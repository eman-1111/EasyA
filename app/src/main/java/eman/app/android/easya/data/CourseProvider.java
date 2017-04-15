package eman.app.android.easya.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * Created by eman_ashour on 4/19/2016.
 */
public class CourseProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private CourseDBHelper mOpenHelper;

    static final int COURSE = 100;


    static final int SUBJECT = 200;
    static final int SUBJECT_WITH_ID = 210;
    static final int SUBJECT_WITH_TITLE = 220;

    private static final SQLiteQueryBuilder sCouseWithSubjectSettingQueryBuilder;

    static{
        sCouseWithSubjectSettingQueryBuilder = new SQLiteQueryBuilder();


        sCouseWithSubjectSettingQueryBuilder.setTables(
                CourseContract.CourseEntry.TABLE_NAME + " INNER JOIN " +
                        CourseContract.SubjectEntry.TABLE_NAME +
                        " ON " +  CourseContract.CourseEntry.TABLE_NAME +
                        "." +  CourseContract.CourseEntry.COLUMN_COURSE_ID +
                        " = " + CourseContract.SubjectEntry.TABLE_NAME +
                        "." +  CourseContract.SubjectEntry.COLUMN_COURSE_ID);
    }

    private static final String sCourseID =
            CourseContract.SubjectEntry.TABLE_NAME+
                    "." + CourseContract.SubjectEntry.COLUMN_COURSE_ID + " = ? ";

    private static final String sSubjectWithTitle =
            CourseContract.SubjectEntry.COLUMN_COURSE_ID + " = ?  AND " +
                    CourseContract.SubjectEntry.COLUMN_LESSON_TITLE + " = ? ";

    private static final String sSubjectWithFav =
            CourseContract.SubjectEntry.TABLE_NAME +
                    "." + CourseContract.SubjectEntry.COLUMN_COURSE_ID + " = ?  AND " +
                    CourseContract.SubjectEntry.COLUMN_FAVORITE + " = ?";
    private Cursor getCourseWithID(Uri uri, String[] projection, String sortOrder,
                                   String selection, String[] selectionArgs)  {
        String course_Id = CourseContract.SubjectEntry.getSubjectIdFromUri(uri);
        if (selection != null) {
            String fav = "1";
            return sCouseWithSubjectSettingQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                    projection,
                    sSubjectWithFav,
                    new String[]{course_Id, fav},
                    null,
                    null,
                    sortOrder
            );
        } else {
            return sCouseWithSubjectSettingQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                    projection,
                    sCourseID,
                    new String[]{course_Id},
                    null,
                    null,
                    sortOrder
            );

        }

    }


    private Cursor getSubjectWithTitle(Uri uri, String[] projection, String sortOrder) {
        String course_Id = CourseContract.SubjectEntry.getSubjectIdFromUri(uri);
        String title = CourseContract.SubjectEntry.getSubjectTitleFromUri(uri);

        return sCouseWithSubjectSettingQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sSubjectWithTitle,
                new String[]{course_Id, title},
                null,
                null,
                sortOrder
        );
    }


    static UriMatcher buildUriMatcher() {
        // I know what you're thinking.  Why create a UriMatcher when you can use regular
        // expressions instead?  Because you're not crazy, that's why.

        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = CourseContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, CourseContract.PATH_COURSE, COURSE);

        matcher.addURI(authority, CourseContract.PATH_SUBJECT + "/*", SUBJECT_WITH_ID);
        matcher.addURI(authority, CourseContract.PATH_SUBJECT, SUBJECT);
        matcher.addURI(authority, CourseContract.PATH_SUBJECT + "/*/*", SUBJECT_WITH_TITLE);

        return matcher;
    }


    @Override
    public boolean onCreate() {
        mOpenHelper = new CourseDBHelper(getContext());
        return true;
    }


    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {

            case COURSE:
                return CourseContract.CourseEntry.CONTENT_TYPE;
            case SUBJECT_WITH_ID:
                return CourseContract.SubjectEntry.CONTENT_TYPE;
            case SUBJECT:
                return CourseContract.SubjectEntry.CONTENT_TYPE;
            case SUBJECT_WITH_TITLE:
                return CourseContract.SubjectEntry.CONTENT_ITEM_TYPE;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // "course/*
            case SUBJECT_WITH_ID:
            {
                retCursor = getCourseWithID(uri, projection, sortOrder,  selection, selectionArgs);
                break;
            }

            // "course"
            case COURSE: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        CourseContract.CourseEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "subject/*/*
            case SUBJECT_WITH_TITLE:
            {
                retCursor = getSubjectWithTitle(uri, projection, sortOrder);
                break;
            }

            // "subject"
            case SUBJECT: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        CourseContract.SubjectEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }


            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }



    /*
        Student: Add the ability to insert Locations to the implementation of this function.
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case COURSE: {
                long _id = db.insert(CourseContract.CourseEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = CourseContract.CourseEntry.buildCourseUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case SUBJECT: {
                long _id = db.insert(CourseContract.SubjectEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = CourseContract.SubjectEntry.buildSubjectUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch (match) {
            case COURSE:
                rowsDeleted = db.delete(
                        CourseContract.CourseEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case SUBJECT:
                rowsDeleted = db.delete(
                        CourseContract.SubjectEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }



    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case COURSE:
                rowsUpdated = db.update(CourseContract.CourseEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case SUBJECT:
                rowsUpdated = db.update(CourseContract.SubjectEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case COURSE:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(CourseContract.CourseEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    // You do not need to call this method. This is a method specifically to assist the testing
    // framework in running smoothly. You can read more at:
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}