package com.neburizer.callreminder;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by nm3 on 3/21/2016.
 */
public class ReminderListItemAdapter extends BaseAdapter {

    Context cxt;

    public ReminderListItemAdapter(Context c){
        cxt = c;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Holder h = new Holder();
        View rowView;
        LayoutInflater inflater = (LayoutInflater) cxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rowView = inflater.inflate(R.layout.reminder_list_item,null);
        h.reminderTime = (TextView) rowView.findViewById(R.id.reminderListItemTime);
        h.contactName = (TextView) rowView.findViewById(R.id.reminderListItemContactName);
        h.contactIcon = (ImageView) rowView.findViewById(R.id.reminderListItemContactImage);
        h.reminderTime.setText("test time");
        h.contactName.setText("test name");
        h.contactIcon.setImageResource(R.drawable.ic_alarm_black_48dp);
        return rowView;
    }

    private class Holder {
        TextView contactName;
        ImageView contactIcon;
        TextView reminderTime;
    }
}
