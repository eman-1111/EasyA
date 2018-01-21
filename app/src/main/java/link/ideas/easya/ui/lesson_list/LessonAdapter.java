package link.ideas.easya.ui.lesson_list;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.util.ColorGenerator;

import java.util.List;

import link.ideas.easya.R;
import link.ideas.easya.data.database.ListLesson;
import link.ideas.easya.utils.Constants;
import link.ideas.easya.utils.ImageSaver;

/**
 * Created by eman_ashour on 4/23/2016.
 */
public class LessonAdapter extends RecyclerView.Adapter<LessonAdapter.SubjectAdapterViewHolder> {


    final private Context mContext;
    private List<ListLesson> listLessons;
    final private SubjectAdapterOnClickHolder mClickHolder;
    ColorGenerator generator = ColorGenerator.MATERIAL;

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
            ListLesson lessonList = listLessons.get(adapterPostion);

            int lessonId = lessonList.getLessonId();
            String titleCulomn = lessonList.getLessonTitle();
            mClickHolder.onClick(lessonId, titleCulomn, this);

        }

        @Override
        public boolean onLongClick(View v) {
            int adapterPostion = getAdapterPosition();
            ListLesson lessonList = listLessons.get(adapterPostion);

            int lessonId = lessonList.getLessonId();
            String titleCulomn = lessonList.getLessonTitle();

            String lessonPush = lessonList.getFirebaseId();
            String coursePush = lessonList.getFirebaseId();


            mClickHolder.onLongClick(lessonId, titleCulomn,
                    lessonPush, coursePush);
            return true;
        }

    }

    public LessonAdapter(Context context, SubjectAdapterOnClickHolder dh) {
        mContext = context;
        mClickHolder = dh;

    }

    public static interface SubjectAdapterOnClickHolder {
        void onClick(int id, String lessonName, SubjectAdapterViewHolder vh);

        boolean onLongClick(int id, String lessonName, String coursePushId, String lessonPushId);
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
        ListLesson lessonList = listLessons.get(position);


        String lessonName = lessonList.getLessonTitle();
        holder.lessonName.setText(lessonName);
        holder.lessonName.setContentDescription(mContext.getString(R.string.a11y_lesson_name, lessonName));

        String link = lessonList.getLessonSummary();
        holder.practicalLink.setText(link);
        holder.practicalLink.setContentDescription(mContext.getString(R.string.a11y_link, link));


        Bitmap summaryImage = new ImageSaver(mContext).
                setFileName(lessonName + Constants.LESSON_SUMMARY).
                setDirectoryName(Constants.APP_NAME).
                load();
        holder.lessonImage.setImageBitmap(summaryImage);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            ViewCompat.setTransitionName(holder.lessonImage, "iconViewFav" + position);
    }


    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        if (listLessons == null) {
            return 0;
        }
        return listLessons.size();
    }

    public List<ListLesson> getLessonList() {
        return listLessons;
    }


    public void swapCursor(List<ListLesson> listLessons) {
        this.listLessons = listLessons;
        notifyDataSetChanged();

    }
}