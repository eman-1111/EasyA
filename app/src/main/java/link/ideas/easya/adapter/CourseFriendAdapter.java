package link.ideas.easya.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

import link.ideas.easya.R;
import link.ideas.easya.models.Course;

/**
 * Created by Eman on 4/27/2017.
 */

public class CourseFriendAdapter extends RecyclerView.Adapter<CourseFriendAdapter.CourseAdapterFriendsViewHolder> {


    final private Context mContext;
    final private CourseFriendAdapter.CourseAdapterFriendsOnClickHolder mClickHolder;
    private List<Course> friendsCourse;
    private List<String> coursePushIds;


    public class CourseAdapterFriendsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public final ImageView teacherImage;
        public final TextView coursName;
        public final TextView teacherName;
        public final CardView listItemCours;

        public CourseAdapterFriendsViewHolder(View view) {
            super(view);
            teacherImage = (ImageView) view.findViewById(R.id.teacher_photo_img);
            teacherName = (TextView) view.findViewById(R.id.teacher_name_txt);
            coursName = (TextView) view.findViewById(R.id.course_name_txt);
            listItemCours = (CardView) view.findViewById(R.id.card_view);
            view.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            int adapterPostion = getAdapterPosition();
            mClickHolder.onClick(coursePushIds.get(adapterPostion), this);

        }

    }

    public CourseFriendAdapter(List<Course> friendsCourse, List<String> coursePushIds , Context context,
                               CourseFriendAdapter.CourseAdapterFriendsOnClickHolder dh) {
        mContext = context;
        mClickHolder = dh;
        this.friendsCourse = friendsCourse;
        this.coursePushIds = coursePushIds;

    }

    public static interface CourseAdapterFriendsOnClickHolder {
        void onClick(String coursePushId, CourseFriendAdapter.CourseAdapterFriendsViewHolder vh);
    }

    @Override
    public CourseFriendAdapter.CourseAdapterFriendsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (parent instanceof RecyclerView) {


            View view = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.list_item_course, parent, false);

            view.setFocusable(false);
            return new CourseFriendAdapter.CourseAdapterFriendsViewHolder(view);
        } else {
            throw new RuntimeException("Not bind th RecyclerView");
        }

    }

    @Override
    public void onBindViewHolder(CourseFriendAdapter.CourseAdapterFriendsViewHolder holder, int position) {
        Course course = friendsCourse.get(position);


        String courseName = course.getCourseName();
        holder.coursName.setText(courseName);

        String teacherName = course.getTeacherName();
        holder.teacherName.setText(teacherName);
    }


    @Override
    public int getItemCount() {

        return friendsCourse.size();
    }

}
