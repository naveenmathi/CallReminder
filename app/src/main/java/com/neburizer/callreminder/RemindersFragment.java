package com.neburizer.callreminder;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


/**
 * Created by nm3 on 2/4/2016.
 */
public class RemindersFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    int DETAILS_QUERY_ID = 0;

    //**************************************Default fragment functions*******************************//
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView (LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.reminders_fragment,container,false); //entire reminder fragment view
        ListView lv = (ListView) v.findViewById(R.id.reminderListView); //holds the reminder list
        lv.setAdapter(new ReminderListItemAdapter(v.getContext())); //adapter for listing reminder items
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button btTemp = (Button)view.findViewById(R.id.temp2);
        btTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateView((ListView) v.findViewById(R.id.reminderListView),2);
            }
        });
    }

    //***********************************Loader Manager functions*****************************//
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    //************************************Other functions********************************
    private void updateView(ListView reminderListView,int index){
        View v = reminderListView.getChildAt(index -
                reminderListView.getFirstVisiblePosition());

        if(v == null)
            return;

        TextView someText = (TextView) v.findViewById(R.id.reminderListItemContactName);
        someText.setText("Hi! I updated you manually!");
    }



    //**************************************ReminderList adapter class*******************************//
    /**
     * Loader manager is used for background loading of contacts
     */
    private class ReminderListItemAdapter extends BaseAdapter{

        Context cxt;
        ReminderDatabaseHelper reminderDatabaseHelper=null;


        //constructor
        public ReminderListItemAdapter(Context c){
            cxt = c;
            reminderDatabaseHelper = new ReminderDatabaseHelper(cxt);
        }

        //***********************************Base Adapter functions*****************************//
        @Override
        public int getCount() {
            return (int) reminderDatabaseHelper.getRowCount();
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
            rowView = inflater.inflate(R.layout.reminder_list_item, null);
            h.reminderTime = (TextView) rowView.findViewById(R.id.reminderListItemTime);
            h.contactName = (TextView) rowView.findViewById(R.id.reminderListItemContactName);
            h.contactIcon = (ImageView) rowView.findViewById(R.id.reminderListItemContactImage);
            Cursor rdbCursor = reminderDatabaseHelper.getAllRecords();
            rdbCursor.moveToPosition(position);
            String callTime = "Next Call Time: " +
                    CommonFunctions.longToTime(rdbCursor.getInt(rdbCursor.getColumnIndex(ReminderDatabaseHelper.COLUMN_NAME_REM_TIME)));
            h.reminderTime.setText(callTime);
            h.contactName.setText(rdbCursor.getString(rdbCursor.getColumnIndex(ReminderDatabaseHelper.COLUMN_NAME_PH_NO)));
            h.contactIcon.setImageResource(R.drawable.ic_alarm_black_48dp);
            return rowView;
        }

        //***********************************Other functions*****************************//
        public class Holder {
            TextView contactName;
            ImageView contactIcon;
            TextView reminderTime;
        }
        /*public static Bitmap retrieveContactPhoto(Context context, String number) {

        }*/
    }
    //**************************************ReminderList adapter class*******************************//
}
