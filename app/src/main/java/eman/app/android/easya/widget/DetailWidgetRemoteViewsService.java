package eman.app.android.easya.widget;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import eman.app.android.easya.R;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;


import eman.app.android.easya.data.CourseContract;

/**
 * Created by Eman on 4/16/2017.
 */

public class DetailWidgetRemoteViewsService extends RemoteViewsService {

    public final String LOG_TAG = DetailWidgetRemoteViewsService.class.getSimpleName();

    private static final String[] COURSES_COLUMNS = {

            CourseContract.CourseEntry.COLUMN_COURSE_ID,
            CourseContract.CourseEntry.COLUMN_COURSE_NAME,
            CourseContract.CourseEntry.COLUMN_TEACHER_NAME,
            CourseContract.CourseEntry.COLUMN_TEACHER_PHOTO_URL
    };

    public static final int COL_COURSE_ID = 0;
    public static final int COL_COURSE_NAME = 1;
    public static final int COL_TEACHER_NAME = 2;
    public static final int COL_TEACHER_PHOTO_URL = 3;;
    static final int INDEX_ID = 5;

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor data = null;

            @Override
            public void onCreate() {
                // Nothing to do
            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }

                final long identityToken = Binder.clearCallingIdentity();

                data = getContentResolver().query( CourseContract.CourseEntry.CONTENT_URI,
                        COURSES_COLUMNS,
                        null,
                        null,
                        null);
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(position)) {
                    return null;
                }
                RemoteViews views = new RemoteViews(getPackageName(),
                        R.layout.widget_list_item);



                String courseName = data.getString(COL_COURSE_NAME);
                String teacherName = data.getString(COL_TEACHER_NAME);
                String photoUrl = data.getString(COL_TEACHER_PHOTO_URL);

                views.setTextViewText(R.id.wedgit_course_name_txt, courseName);
                views.setTextViewText(R.id.wedgit_teacher_name_txt, teacherName);


                final Intent fillInIntent = new Intent();

                int idCulomnIndex = data.getColumnIndex(CourseContract.CourseEntry.COLUMN_COURSE_ID);

                Uri courseUri =CourseContract.SubjectEntry.buildSubjectWithID(data.getString(idCulomnIndex));
                fillInIntent.setData(courseUri);
                views.setOnClickFillInIntent(R.id.widget_list_item, fillInIntent);
                return views;
            }


            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_list_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
//                if (data.moveToPosition(position))
//                    return data.getLong(INDEX_ID);
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}
