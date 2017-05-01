package link.ideas.easya.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import java.util.List;
import link.ideas.easya.R;
import link.ideas.easya.models.Lesson;


/**
 * Created by Eman on 4/27/2017.
 */

public class LessonFriendsAdapter extends RecyclerView.Adapter<LessonFriendsAdapter.CourseAdapterFriendsViewHolder> {


    final private Context mContext;
    final private LessonFriendsAdapter.CourseAdapterFriendsOnClickHolder mClickHolder;
    List<Lesson> friendsLesson;
    List<String> lessonPushIds;


    public class CourseAdapterFriendsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public final ImageView lessonImage;
        public final TextView lessonName;
        public final TextView practicalLink;


        public CourseAdapterFriendsViewHolder(View view) {
            super(view);
            lessonImage = (ImageView) view.findViewById(R.id.list_item_lesson_image);
            lessonName = (TextView) view.findViewById(R.id.list_item_lesson_title);
            practicalLink = (TextView) view.findViewById(R.id.list_item_real_Application_title);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPostion = getAdapterPosition();
            String lessonPushId = lessonPushIds.get(adapterPostion);
            mClickHolder.onClick(lessonPushId, friendsLesson.get(adapterPostion) ,this);

        }

    }

    public LessonFriendsAdapter(List<Lesson> friendsLesson, List<String> lessonPushIds , Context context,
                               LessonFriendsAdapter.CourseAdapterFriendsOnClickHolder dh) {
        mContext = context;
        mClickHolder = dh;
        this.friendsLesson = friendsLesson;
        this.lessonPushIds = lessonPushIds;
    }

    public static interface CourseAdapterFriendsOnClickHolder {
        void onClick(String lessonPushId, Lesson lesson ,LessonFriendsAdapter.CourseAdapterFriendsViewHolder vh);
    }

    @Override
    public LessonFriendsAdapter.CourseAdapterFriendsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (parent instanceof RecyclerView) {


            View view = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.list_item_subject, parent, false);

            view.setFocusable(false);
            return new LessonFriendsAdapter.CourseAdapterFriendsViewHolder(view);
        } else {
            throw new RuntimeException("Not bind th RecyclerView");
        }

    }

    @Override
    public void onBindViewHolder(LessonFriendsAdapter.CourseAdapterFriendsViewHolder holder, int position) {
        Lesson lesson = friendsLesson.get(position);


        String lessonName = lesson.getLessonName();
        holder.lessonName.setText(lessonName);


        String lessonLink = lesson.getLessonLink();
        holder.practicalLink.setText(lessonLink);

        String linkImageUrl = lesson.getLessonImage();
        if (linkImageUrl.equals("")) {
            Glide.with(mContext).load(linkImageUrl)
                    .error(R.drawable.placeholder)
                    .into(holder.lessonImage);
        }
    }


    @Override
    public int getItemCount() {
        return friendsLesson.size();
    }

}
