package link.ideas.easya.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ThemedSpinnerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import link.ideas.easya.R;
import link.ideas.easya.data.CourseContract;
import link.ideas.easya.models.User;
import link.ideas.easya.utils.CircleTransform;
import link.ideas.easya.utils.Constants;
import link.ideas.easya.utils.Helper;

/**
 * Created by Eman on 4/23/2017.
 */

public class UserFriendsAdapter extends RecyclerView.Adapter<UserFriendsAdapter.UserFriendsAdapterViewHolder> {


    final private Context mContext;
    final private UserFriendsAdapter.UserFriendsAdapterOnClickHolder mClickHolder;
    private List<User> userList;
    private List<String> email;
    private  String userEmail;

    public class UserFriendsAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final TextView friendEmail;
        public final TextView friendName;
        public final ImageView friendImage;
        public final ImageButton canEdit;

        public UserFriendsAdapterViewHolder(View view) {
            super(view);
            friendEmail = (TextView) view.findViewById(R.id.tv_friend_email);
            friendName = (TextView) view.findViewById(R.id.tv_friend_name);
            friendImage = (ImageView) view.findViewById(R.id.iv_friend_image);
            canEdit = (ImageButton) view.findViewById(R.id.ib_can_edit);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPostion = getAdapterPosition();
            String name = userList.get(adapterPostion).getName();
            String friendEmail = email.get(adapterPostion);
            boolean canEdit = userList.get(adapterPostion).isCanEdit();
            mClickHolder.onClick(friendEmail, name, canEdit,this);
        }

    }

    public UserFriendsAdapter(List<User> userList, List<String> email, String userEmail,
                              Context context, UserFriendsAdapter.UserFriendsAdapterOnClickHolder dh) {
        mContext = context;
        mClickHolder = dh;
        this.userList = userList;
        this.email = email;
        this.userEmail = userEmail;

    }

    public static interface UserFriendsAdapterOnClickHolder {
        void onClick(String friendEmail, String friendName, boolean canEdit, UserFriendsAdapter.UserFriendsAdapterViewHolder vh);
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
    public void onBindViewHolder(final UserFriendsAdapter.UserFriendsAdapterViewHolder holder, int position) {
        final User user = userList.get(position);
       final String friendEmail = email.get(position);

        String email = Helper.decodeEmail(friendEmail);
        holder.friendEmail.setText(email);
        holder.friendEmail.setContentDescription(mContext.getString(R.string.a11y_email_button, email));

        String name = user.getName();
        holder.friendName.setText(name);
        holder.friendName.setContentDescription(mContext.getString(R.string.a11y_name_button, name));

        Glide.with(mContext).load(user.getPhotoUrl())
                .transform(new CircleTransform(mContext))
                .error(R.drawable.ic_account_circle_black_24dp)
                .into(holder.friendImage);

        changeEditStatus(user.isCanEdit(),holder);

        holder.canEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();

                final DatabaseReference mUsersFriendsDatabaseReference = mFirebaseDatabase.getReference()
                        .child(Constants.FIREBASE_LOCATION_USER_FRIENDS)
                        .child(userEmail)
                        .child(friendEmail);
                mUsersFriendsDatabaseReference.child(Constants.FIREBASE_LOCATION_USERS_CAN_EDIT).setValue(!user.isCanEdit());
                changeEditStatus(user.isCanEdit(),holder);

            }
        });
    }

    public void changeEditStatus(boolean canEdit, UserFriendsAdapter.UserFriendsAdapterViewHolder holder) {
        if (canEdit) {
            holder.canEdit.setBackground(mContext.getResources().getDrawable(R.drawable.ic_mode_edit_black_24dp));
        } else {
            holder.canEdit.setBackground(mContext.getResources().getDrawable(R.drawable.ic_border_color_black_24dp));

        }
    }

    @Override
    public int getItemCount() {

        return userList.size();
    }

}
