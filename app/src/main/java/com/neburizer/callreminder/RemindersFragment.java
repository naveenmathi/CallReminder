package com.neburizer.callreminder;

import android.app.AlarmManager;
import android.app.Fragment;
import android.app.LoaderManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


/**
 * Created by nm3 on 2/4/2016.
 */
public class RemindersFragment extends Fragment{


    //**************************************Default fragment functions*******************************//
    ListView reminderListView;
    String bundleArgNumber  =   "phNumber";
    static ArrayList<PendingIntent> registeredAlarms;
    Intent callReminderSerivceIntent;
    public static String startReminder = "startReminder";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView (LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.reminders_fragment,container,false); //entire reminder fragment view
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //set reminder list adapter
        reminderListView = (ListView) view.findViewById(R.id.reminderListView);
        reminderListView.setAdapter(new ReminderListItemAdapter(view.getContext()));

        //set pendingAlarmIntents array
        registeredAlarms = new ArrayList<PendingIntent>();

        //**************************************Start Reminder - button function*******************************//
        Button btnStartReminder = (Button)view.findViewById(R.id.btnStartReminder);
        /**
         * This function starts a set of reminders (repeating alarm managers)
         */
        btnStartReminder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            callReminderSerivceIntent = new Intent(getActivity(),CallReminderService.class);
                            callReminderSerivceIntent.putExtra(CallReminderService.serviceType,startReminder);
                            //callReminderSerivceIntent.putExtra()
                            getActivity().startService(callReminderSerivceIntent);
                        }
                    }).start();
                }
        });

        //**************************************Stop Reminder - button function*******************************//
        Button btnStopReminder = (Button)view.findViewById(R.id.btnStopReminder);
        /**
         * This function starts a set of reminders (repeating alarm managers)
         */
        btnStopReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(registeredAlarms.size()>0) {
                    Context cxt = view.getContext();
                    AlarmManager alarmMgr;
                    PendingIntent pendingIntent;
                    alarmMgr = (AlarmManager) cxt.getSystemService(Context.ALARM_SERVICE);
                    for (PendingIntent p : registeredAlarms) {
                        alarmMgr.cancel(p);
                    }
                    registeredAlarms = new ArrayList<PendingIntent>();
                }
            }
        });

    }


    //**************************************ReminderList adapter class*******************************//
    /**
     * Loader manager is used for background loading of contacts
     */
    private class ReminderListItemAdapter extends BaseAdapter implements LoaderManager.LoaderCallbacks<Cursor>{

        Context cxt;
        DatabaseHelper reminderDatabaseHelper=null;


        //constructor
        public ReminderListItemAdapter(Context c){
            cxt = c;
            reminderDatabaseHelper = new DatabaseHelper(cxt);
        }

        //***********************************Base Adapter functions*****************************//
        @Override
        public int getCount() {
            return (int) reminderDatabaseHelper.getRowCount(ReminderTableContract.TABLE_NAME);
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
            Cursor rdbCursor = reminderDatabaseHelper.getAllRecords(ReminderTableContract.TABLE_NAME);
            rdbCursor.moveToPosition(position);
            String callTime = "Next Call Time: " +
                    GenericLib.longToTime(rdbCursor.getInt(rdbCursor.getColumnIndex(ReminderTableContract.COLUMN_NAME_REM_TIME)));
            h.reminderTime.setText(callTime);
            String numTxt = rdbCursor.getString(rdbCursor.getColumnIndex(ReminderTableContract.COLUMN_NAME_PH_NO));
            h.contactName.setText(numTxt);
            //h.contactIcon.setImageResource(R.drawable.ic_alarm_black_48dp);

            //update with contact names

            if(MainActivity.rdh.getRowCount(ContactsTableContract.TABLE_NAME) < 1) {
                getLoaderManager().initLoader(0, null, this);
            }

            Cursor contactCursor = MainActivity.rdh.getContactsRecord(numTxt);
            try {
                contactCursor.moveToFirst();
                h.contactName.setText(contactCursor.getString(contactCursor.getColumnIndex(ContactsTableContract.COLUMN_CONTACT_NAME)));
                byte temp[] = contactCursor.getBlob(contactCursor.getColumnIndex(ContactsTableContract.COLUMN_CONTACT_IMG_RES));
                Bitmap conImg = BitmapFactory.decodeByteArray(temp, 0, temp.length);
                h.contactIcon.setImageBitmap(conImg);
            }catch (Exception c){}

            return rowView;
        }

        /**
         * Holder class for reminder item
         */
        public class Holder {
            TextView contactName;
            ImageView contactIcon;
            TextView reminderTime;
        }

        //***********************************Loader Manager functions*****************************//

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            //get all contacts
            return new CursorLoader(getActivity(), ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            //store in database contactsTable

            MainActivity.rdh.emptyDb(ContactsTableContract.TABLE_NAME);

            if(!(data == null)){
                data.moveToFirst();
                do {
                    String normNum = "";
                    String contactName = "";
                    byte[] contactImg = null;
                    try {//TODO store contact image as a file and generate resource id
                        normNum = (data.getString(data.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                        normNum = normNum.replaceAll("[^0-9]","");
                        normNum = normNum.substring(normNum.length()-10,normNum.length());
                        contactName = data.getString(data.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                        contactImg  = data.getBlob(data.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
                        MainActivity.rdh.createContactsRecord(normNum,contactName,contactImg);
                    }catch (Exception e){}
                }while(data.moveToNext());
            }
        }


        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }


    }
    //**************************************ReminderList adapter class*******************************//
}
