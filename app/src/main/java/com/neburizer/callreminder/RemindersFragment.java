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
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * Created by nm3 on 2/4/2016.
 */
public class RemindersFragment extends Fragment{


    //**************************************Default fragment functions*******************************//
    ListView reminderListView;
    String bundleArgNumber  =   "phNumber";
    ArrayList<PendingIntent> registeredAlarms;

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
                            try {
                                Thread.sleep(4000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            Context cxt = view.getContext();
                            AlarmManager alarmMgr;
                            PendingIntent pendingIntent;
                            alarmMgr = (AlarmManager) cxt.getSystemService(Context.ALARM_SERVICE);

                            Calendar calendar = Calendar.getInstance();
                            calendar.setTimeInMillis(System.currentTimeMillis());

                            Intent notificationIntent = new Intent(cxt, NotificationReceiver.class);
                            DatabaseHelper dbh = MainActivity.rdh;
                            Cursor c = dbh.getAllRecords(ReminderTableContract.TABLE_NAME);
                            c.moveToFirst();
                            do {
                                notificationIntent.putExtra
                                        (ReminderTableContract.COLUMN_NAME_ID, c.getInt(c.getColumnIndex(ReminderTableContract.COLUMN_NAME_ID)));
                                pendingIntent = PendingIntent.getBroadcast(cxt, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                                registeredAlarms.add(pendingIntent);
                                for(PendingIntent pI:registeredAlarms){
                                    calendar.setTimeInMillis(c.getInt(c.getColumnIndex(ReminderTableContract.COLUMN_NAME_REM_TIME)));
                                    alarmMgr.setInexactRepeating(AlarmManager.RTC,calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pI);
                                }
                            } while (c.moveToNext());
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
                Context cxt = view.getContext();
                AlarmManager alarmMgr;
                PendingIntent pendingIntent;
                alarmMgr = (AlarmManager)cxt.getSystemService(Context.ALARM_SERVICE);
                for (PendingIntent p:registeredAlarms)
                {
                    alarmMgr.cancel(p);
                    //registeredAlarms.remove(p);
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
                    CommonFunctions.longToTime(rdbCursor.getInt(rdbCursor.getColumnIndex(ReminderTableContract.COLUMN_NAME_REM_TIME)));
            h.reminderTime.setText(callTime);
            String numTxt = rdbCursor.getString(rdbCursor.getColumnIndex(ReminderTableContract.COLUMN_NAME_PH_NO));
            h.contactName.setText(numTxt);
            h.contactIcon.setImageResource(R.drawable.ic_alarm_black_48dp);

            //update with contact names

            if(MainActivity.rdh.getRowCount(ContactsTableContract.TABLE_NAME) < 1) {
                getLoaderManager().initLoader(0, null, this);
            }

            Cursor contactCursor = MainActivity.rdh.getContactsRecord(numTxt);
            try {
                contactCursor.moveToFirst();
                h.contactName.setText(contactCursor.getString(contactCursor.getColumnIndex(ContactsTableContract.COLUMN_CONTACT_NAME)));
                h.contactIcon.setImageURI(Uri.parse(contactCursor.getString(contactCursor.getColumnIndex(ContactsTableContract.COLUMN_CONTACT_IMG_RES))));
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
                    String contactImgResId = "";
                    try {
                        normNum = (data.getString(data.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                        normNum = normNum.replaceAll("[^0-9]","");
                        normNum = normNum.substring(normNum.length()-10,normNum.length());
                        contactName = data.getString(data.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                        contactImgResId = data.getString(data.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI));
                        MainActivity.rdh.createContactsRecord(normNum,contactName,contactImgResId);
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
