package eman.app.android.easya.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.util.ColorGenerator;

import eman.app.android.easya.R;
import eman.app.android.easya.data.CourseContract;
import eman.app.android.easya.fragment.FavSubjectListFragment;
import eman.app.android.easya.utils.Helper;

/**
 * Created by Eman on 4/11/2017.
 */

public class SubjectFavAdapter extends RecyclerView.Adapter<SubjectFavAdapter.SubjectFavAdapterViewHolder> {


    final private Context mContext;
    private Cursor mCursor ;
    final private SubjectFavAdapter.SubjectFavAdapterOnClickHolder mClickHolder;
    ColorGenerator generator = ColorGenerator.MATERIAL;

    public static String getSortBy(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_sort_key),
                context.getString(R.string.pref_sort_default));
    }

    public class SubjectFavAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public final ImageView lessonImage;
        public final TextView lessonName;
        public final TextView practicalLink;


        public SubjectFavAdapterViewHolder(View view) {
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
            mClickHolder.onLongClick(mCursor.getString(idCulomnIndex),mCursor.getString(titleCulomnIndex));
            return true;
        }

    }

    public SubjectFavAdapter(Context context, SubjectFavAdapter.SubjectFavAdapterOnClickHolder dh) {
        mContext = context;
        mClickHolder = dh;

    }

    public static interface SubjectFavAdapterOnClickHolder {
        void onClick(String id, String lessonName, SubjectFavAdapter.SubjectFavAdapterViewHolder vh);
        boolean onLongClick(String id,String lessonName);
    }


    @Override
    public SubjectFavAdapter.SubjectFavAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (parent instanceof RecyclerView) {


            View view = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.list_item_subject, parent, false);

            view.setFocusable(false);
            return new SubjectFavAdapter.SubjectFavAdapterViewHolder(view);
        } else {
            throw new RuntimeException("Not bind th RecyclerView");
        }

    }

    @Override
    public void onBindViewHolder(SubjectFavAdapter.SubjectFavAdapterViewHolder SubjectFavAdapterViewHolder, int position) {
        mCursor.moveToPosition(position);


        String lessonName = mCursor.getString(FavSubjectListFragment.CO_LESSON_TITLE);
        SubjectFavAdapterViewHolder.lessonName.setText(lessonName);


        String practical_title_Name = mCursor.getString(FavSubjectListFragment.COL_LESSON_LINK);
        SubjectFavAdapterViewHolder.practicalLink.setText(practical_title_Name);

        byte[] image = mCursor.getBlob(FavSubjectListFragment.COL_LESSON_OUTLINE_IMAGE);
        if(image != null){
            SubjectFavAdapterViewHolder.lessonImage.setImageBitmap(Helper.getImage(image));}

        int fav = Integer.parseInt(mCursor.getString(FavSubjectListFragment.COL_LESSON_FAV));
        Log.e("SubjectFavAdapter", "ff is: "+ fav);


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
