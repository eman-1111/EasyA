package link.ideas.easya.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ThemedSpinnerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import android.widget.TextView;

import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bumptech.glide.Glide;


import link.ideas.easya.R;
import link.ideas.easya.data.CourseContract;
import link.ideas.easya.fragment.CourseListFragment;
import link.ideas.easya.utils.Helper;


/**
 * Created by eman_ashour on 4/21/2016.
 */
public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseAdapterViewHolder> {


    final private Context mContext;
    private Cursor mCursor;
    final private CourseAdapterOnClickHolder mClickHolder;


    public class CourseAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public final ImageView teacherImage;
        public final TextView coursName;
        public final TextView teacherName;
        public final CardView listItemCours;

        public CourseAdapterViewHolder(View view) {
            super(view);
            teacherImage = (ImageView) view.findViewById(R.id.teacher_photo_img);
            teacherName = (TextView) view.findViewById(R.id.teacher_name_txt);
            coursName = (TextView) view.findViewById(R.id.course_name_txt);
            listItemCours = (CardView) view.findViewById(R.id.card_view);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPostion = getAdapterPosition();
            mCursor.moveToPosition(adapterPostion);
            int idCulomnIndex = mCursor.getColumnIndex(CourseContract.CourseEntry.COLUMN_COURSE_ID);
            int idCulomnName = mCursor.getColumnIndex(CourseContract.CourseEntry.COLUMN_COURSE_NAME);
            mClickHolder.onClick(mCursor.getString(idCulomnIndex),mCursor.getString(idCulomnName), this);

        }


        @Override
        public boolean onLongClick(View v) {
            int adapterPostion = getAdapterPosition();
            mCursor.moveToPosition(adapterPostion);
            int idCulomnIndex = mCursor.getColumnIndex(CourseContract.CourseEntry.COLUMN_COURSE_ID);
            mClickHolder.onLongClick(mCursor.getString(idCulomnIndex));
            return true;
        }
    }


    public CourseAdapter(Context context, CourseAdapterOnClickHolder dh) {
        mContext = context;
        mClickHolder = dh;

    }


    public static interface CourseAdapterOnClickHolder {
        void onClick(String id, String courseName, CourseAdapterViewHolder vh);
        boolean onLongClick(String id);
    }


    @Override
    public CourseAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (parent instanceof RecyclerView) {


            View view = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.list_item_course, parent, false);

            view.setFocusable(false);
            return new CourseAdapterViewHolder(view);
        } else {
            throw new RuntimeException("Not bind th RecyclerView");
        }

    }

    @Override
    public void onBindViewHolder(final CourseAdapterViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        int courseId = mCursor.getInt(CourseListFragment.COL_COURSE_ID);
        String teacherPhotoURL = mCursor.getString(CourseListFragment.COL_TEACHER_PHOTO_URL);
        int image = 0;

        if (position == 0 || position % 4 == 0) {
            image = R.drawable.panda;
        } else if (position == 1 || position % 4 == 1) {
            image = R.drawable.blue;
        } else if (position == 2 || position % 4 == 2) {
            image = R.drawable.cat;
        } else if (position == 3 || position % 4 == 3) {
            image = R.drawable.monstor;
        }  else {
            image = R.drawable.rana;
        }


        Glide.with(mContext).load(teacherPhotoURL).error(image).
                into(holder.teacherImage);

        String courseName = mCursor.getString(CourseListFragment.COL_COURSE_NAME);
        holder.coursName.setText(courseName);
        holder.coursName.setContentDescription(mContext.getString(R.string.a11y_course_name,courseName));

        String teacherName = mCursor.getString(CourseListFragment.COL_TEACHER_NAME);
        holder.teacherName.setText(teacherName);
        holder.teacherName.setContentDescription(mContext.getString(R.string.a11y_teacher_name,teacherName));


        int courseItemColor = mCursor.getInt(CourseListFragment.COL_TEACHER_COLOR);

        holder.listItemCours.setBackground
                (mContext.getResources().getDrawable(Helper.getCourseColor(courseItemColor)));

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