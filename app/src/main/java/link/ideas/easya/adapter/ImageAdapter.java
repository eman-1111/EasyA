package link.ideas.easya.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;


import link.ideas.easya.models.Image;
import link.ideas.easya.R;


/**
 * Created by eman_ashour on 6/14/2016.
 */
public class ImageAdapter extends ArrayAdapter<Image> {

    Context context;

    public ImageAdapter(Context context, int resourceId, ArrayList<Image> image) {
        super(context, resourceId, image);
        this.context = context;
    }



    /*private view holder class*/
    private class ViewHolder {
        ImageView imageView;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        Image imageNor = getItem(position);

        if (convertView == null) {

            holder = new ViewHolder();
            convertView = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.list_item_search, parent, false);
            holder.imageView = (ImageView) convertView.findViewById(R.id.list_item_icon_img);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Glide.with(context).load(imageNor.getImage()).placeholder(R.drawable.blue).dontAnimate()
                .into(holder.imageView);

        return convertView;
    }

}

