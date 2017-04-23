package link.ideas.easya.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import link.ideas.easya.R;
import link.ideas.easya.models.User;

/**
 * Created by Eman on 4/23/2017.
 */

public class UserFriendsAdapter extends RecyclerView.Adapter<UserFriendsAdapter.UserFriendsAdapterViewHolder> {


    final private Context mContext;
    final private UserFriendsAdapter.UserFriendsAdapterOnClickHolder mClickHolder;
    private List<User> userList;

    public class UserFriendsAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public final TextView lessonFriendName;


        public UserFriendsAdapterViewHolder(View view) {
            super(view);
            lessonFriendName = (TextView) view.findViewById(R.id.tv_friend_name);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPostion = getAdapterPosition();

        }

    }

    public UserFriendsAdapter(List<User> userList ,Context context, UserFriendsAdapter.UserFriendsAdapterOnClickHolder dh) {
        mContext = context;
        mClickHolder = dh;
        this.userList = userList;

    }

    public static interface UserFriendsAdapterOnClickHolder {
        void onClick(String id, String lessonName, UserFriendsAdapter.UserFriendsAdapterViewHolder vh);
    }


    @Override
    public UserFriendsAdapter.UserFriendsAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (parent instanceof RecyclerView) {


            View view = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.list_item_friend, parent, false);

            view.setFocusable(false);
            return new UserFriendsAdapter.UserFriendsAdapterViewHolder(view);
        } else {
            throw new RuntimeException("Not bind th RecyclerView");
        }

    }

    @Override
    public void onBindViewHolder(UserFriendsAdapter.UserFriendsAdapterViewHolder holder, int position) {
        User user = userList.get(position);

        holder.lessonFriendName.setText(user.getName());
    }


    @Override
    public int getItemCount() {

        return userList.size();
    }

}
