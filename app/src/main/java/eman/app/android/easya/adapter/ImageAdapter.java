package eman.app.android.easya.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import eman.app.android.easya.Image;
import eman.app.android.easya.R;


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


        Picasso.with(context).load(imageNor.getImage()).error(R.drawable.blue)
                .into(holder.imageView);

        return convertView;
    }

}

