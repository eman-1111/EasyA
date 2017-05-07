package link.ideas.easya.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.util.ColorGenerator;

import link.ideas.easya.R;
import link.ideas.easya.data.CourseContract;
import link.ideas.easya.fragment.LessonListFragment;
import link.ideas.easya.utils.Helper;

/**
 * Created by eman_ashour on 4/23/2016.
 */
public class LessonAdapter extends RecyclerView.Adapter<LessonAdapter.SubjectAdapterViewHolder> {


    final private Context mContext;
    private Cursor mCursor;
    final private SubjectAdapterOnClickHolder mClickHolder;
    ColorGenerator generator = ColorGenerator.MATERIAL;

    public static String getSortBy(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_sort_key),
                context.getString(R.string.pref_sort_default));
    }

    public class SubjectAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public final ImageView lessonImage;
        public final TextView lessonName;
        public final TextView practicalLink;


        public SubjectAdapterViewHolder(View view) {
            super(view);
            lessonImage = (ImageView) view.findViewById(R.id.list_item_lesson_image);
            lessonName = (TextView) view.findViewById(R.id.list_item_lesson_title);
            practicalLink = (TextView) view.findViewById(R.id.list_item_real_Application_title);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPostion = getAdapterPosition();
            mCursor.moveToPosition(adapterPostion);
            int idCulomnIndex = mCursor.getColumnIndex(CourseContract.SubjectEntry.COLUMN_COURSE_ID);
            int titleCulomnIndex = mCursor.getColumnIndex(CourseContract.SubjectEntry.COLUMN_LESSON_TITLE);
            mClickHolder.onClick(mCursor.getString(idCulomnIndex),
                    mCursor.getString(titleCulomnIndex), this);

        }

        @Override
        public boolean onLongClick(View v) {
            int adapterPostion = getAdapterPosition();
            mCursor.moveToPosition(adapterPostion);
            int idCulomnIndex = mCursor.getColumnIndex(CourseContract.SubjectEntry.COLUMN_COURSE_ID);
            int titleCulomnIndex = mCursor.getColumnIndex(CourseContract.SubjectEntry.COLUMN_LESSON_TITLE);

            int lessonPushIndex = mCursor.getColumnIndex(CourseContract.SubjectEntry.COLUMN_FIREBASE_ID);
            int coursePushIndex = mCursor.getColumnIndex(CourseContract.CourseEntry.COLUMN_FIREBASE_ID);

            mClickHolder.onLongClick(mCursor.getString(idCulomnIndex), mCursor.getString(titleCulomnIndex),
                    mCursor.getString(coursePushIndex), mCursor.getString(lessonPushIndex) );
            return true;
        }

    }

    public LessonAdapter(Context context, SubjectAdapterOnClickHolder dh) {
        mContext = context;
        mClickHolder = dh;

    }

    public static interface SubjectAdapterOnClickHolder {
        void onClick(String id, String lessonName, SubjectAdapterViewHolder vh);

        boolean onLongClick(String id, String lessonName, String coursePushId, String lessonPushId);
    }


    @Override
    public SubjectAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (parent instanceof RecyclerView) {


            View view = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.list_item_subject, parent, false);

            view.setFocusable(false);
            return new SubjectAdapterViewHolder(view);
        } else {
            throw new RuntimeException("Not bind th RecyclerView");
        }

    }

    @Override
    public void onBindViewHolder(SubjectAdapterViewHolder holder, int position) {
        mCursor.moveToPosition(position);


        String lessonName = mCursor.getString(LessonListFragment.CO_LESSON_TITLE);
        holder.lessonName.setText(lessonName);
        holder.lessonName.setContentDescription(mContext.getString(R.string.a11y_lesson_name, lessonName));

        String link = mCursor.getString(LessonListFragment.COL_LESSON_LINK);
        holder.practicalLink.setText(link);
        holder.practicalLink.setContentDescription(mContext.getString(R.string.a11y_link, link));


        byte[] image = mCursor.getBlob(LessonListFragment.COL_LESSON_OUTLINE_IMAGE);
        if (image != null) {
            holder.lessonImage.setImageBitmap(Helper.getImage(image));
        }
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ViewCompat.setTransitionName(holder.lessonImage, "iconView" + position);
        }

    }


    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        if (mCursor == null) {
            return 0;
        }
        return mCursor.getCount();
    }

    public Cursor getCursor() {
        return mCursor;
    }


    public void swapCursor(Cursor cursor) {
        mCursor = cursor;
        notifyDataSetChanged();

    }
}
