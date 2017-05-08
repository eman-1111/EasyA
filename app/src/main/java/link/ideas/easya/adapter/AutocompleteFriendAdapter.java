package link.ideas.easya.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import link.ideas.easya.AddFriendActivity;
import link.ideas.easya.FriendsList;
import link.ideas.easya.R;
import link.ideas.easya.models.User;
import link.ideas.easya.utils.Constants;
import link.ideas.easya.utils.Helper;

/**
 * Created by Eman on 4/24/2017.
 */

public class AutocompleteFriendAdapter extends ArrayAdapter<User> {

    Context context;
    private String mEncodedEmail;
    ArrayList<User> user;
    ArrayList<String> email;

    public AutocompleteFriendAdapter(Context context, int resourceId, ArrayList<String> email,
                                     ArrayList<User> user, String mEncodedEmail) {
        super(context, resourceId, user);
        this.context = context;
        this.user = user;
        this.email = email;
        this.mEncodedEmail = mEncodedEmail;
    }



    /*private view holder class*/
    private class ViewHolder {
        TextView textViewFriendEmail;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        AutocompleteFriendAdapter.ViewHolder holder = null;
        final User user = getItem(position);
        final String userEmail = email.get(position);

        if (convertView == null) {

            holder = new AutocompleteFriendAdapter.ViewHolder();
            convertView = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.single_autocomplete_item, parent, false);
            holder.textViewFriendEmail = (TextView) convertView.findViewById(R.id.text_view_autocomplete_item);
            convertView.setTag(holder);
        } else {
            holder = (AutocompleteFriendAdapter.ViewHolder) convertView.getTag();
        }


        holder.textViewFriendEmail.setText(Helper.decodeEmail(userEmail));

        holder.textViewFriendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /**
                 * If selected user is not current user proceed
                 */
                if (isNotCurrentUser(Helper.decodeEmail(userEmail))) {
                    FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();

                    final DatabaseReference mUsersFriendsDatabaseReference = mFirebaseDatabase.getReference()
                            .child(Constants.FIREBASE_LOCATION_USER_FRIENDS)
                            .child(Helper.encodeEmail(mEncodedEmail))
                            .child(userEmail);
                    mUsersFriendsDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            /**
                             * Add selected user to current user's friends if not in friends yet
                             */
                            if (isNotAlreadyAdded(dataSnapshot, user)) {
                                User user1 = new User(user.getName(), user.getPhotoUrl());
                                mUsersFriendsDatabaseReference.setValue(user1);
//                                ((Activity)context).finish();
                                Intent intent = new Intent(context, FriendsList.class);
                                context.startActivity(intent);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }

                    });


                }
            }
        });

        return convertView;
    }


    /**
     * Protected method that populates the view attached to the adapter (list_view_friends_autocomplete)
     * with items inflated from single_autocomplete_item.xml
     * populateView also handles data changes and updates the listView accordingly
     */
    private boolean isNotCurrentUser(String userEmail) {
        if (userEmail.equals(mEncodedEmail)) {
            /* Toast appropriate error message if the user is trying to add themselves  */
            Toast.makeText(context,
                    context.getResources().getString(R.string.toast_you_cant_add_yourself),
                    Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private boolean isNotAlreadyAdded(DataSnapshot dataSnapshot, User user) {
        if (dataSnapshot.getValue(User.class) != null) {
            /* Toast appropriate error message if the user is already a friend of the user */
            String friendError = String.format(context.getResources().
                            getString(R.string.toast_is_already_your_friend),
                    user.getName());

            Toast.makeText(context,
                    friendError,
                    Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }


}