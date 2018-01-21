package link.ideas.easya.ui.course_list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;

import link.ideas.easya.R;

/**
 * Created by Eman on 5/2/2017.
 */

public class ColorAdapter extends BaseAdapter {
    private Context context;
    final private int[] colorList = new int[]{
            0, 1, 2, 3};
    public int selectedImage = 0 ;
    private static LayoutInflater inflater=null;

    public ColorAdapter(Context context) {
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
         this.context = context;
    }

    public class Holder
    {
        ImageView img;
        FrameLayout mFrameLayout;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        Holder holder=new Holder();
        View rowView;

        rowView = inflater.inflate(R.layout.color_item, null);

        holder.img =(ImageView) rowView.findViewById(R.id.grid_item_image);
        holder.mFrameLayout =(FrameLayout) rowView.findViewById(R.id.frame_color);


        int color = colorList[position];

        if (color == 0) {
            holder.img.setBackgroundResource(R.drawable.couse1b);
        } else if (color == 1) {
            holder.img.setBackgroundResource(R.drawable.couse2b);
        } else if (color == 2) {
            holder.img.setBackgroundResource(R.drawable.couse3b);
        } else if (color == 3) {
            holder.img.setBackgroundResource(R.drawable.couse4b);
        } else {
            holder.img.setBackgroundResource(R.drawable.couse1b);
        }
        if (position == selectedImage) {
            holder.mFrameLayout.setBackgroundColor(context.getResources().getColor(R.color.ripple_dark));
        }
        return rowView;
    }

    @Override
    public int getCount() {
        return colorList.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

}