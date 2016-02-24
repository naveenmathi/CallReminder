package com.neburizer.callreminder;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by nm3 on 2/11/2016.
 */
public class CustomAdapter extends BaseAdapter {
    String [] txt_list;
    TypedArray imageId;
    Context context;
    MainActivity ma;
    private static LayoutInflater inflater=null;

    public CustomAdapter(MainActivity mainActivity, String[] txt_list, TypedArray img_list) {
        // TODO Auto-generated constructor stub
        this.txt_list=txt_list;
        context=ma=mainActivity;
        imageId=img_list;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return txt_list.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class Holder
    {
        TextView tv;
        ImageView img;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder=new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.drawer_list_item, null);
        holder.tv=(TextView) rowView.findViewById(R.id.textView1);
        holder.img=(ImageView) rowView.findViewById(R.id.imageView1);
        holder.tv.setText(txt_list[position]);
        holder.img.setImageResource(imageId.getResourceId(position,-1));
        return rowView;
    }

}
