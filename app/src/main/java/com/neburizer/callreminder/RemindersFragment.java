package com.neburizer.callreminder;

import android.app.AlarmManager;
import android.app.Fragment;
import android.app.LoaderManager;
import android.app.PendingIntent;
import android.content.AsyncTaskLoader;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
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

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

//**************************************Reminders Fragment Class*******************************//

/**
 * Created by nm3 on 2/4/2016.
 */
public class RemindersFragment extends Fragment {
    ListView reminderListView;
    static ArrayList<PendingIntent> registeredAlarms;
    Intent callReminderSerivceIntent;
    public static String startReminder = "startReminder";
    ImageView tempimgview;
    Cursor contactsCursor;
    DatabaseHelper reminderDatabaseHelper = null;
    ReminderListItemAdapter rli = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.reminders_fragment, container, false); //entire reminder fragment view
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tempimgview = (ImageView) view.findViewById(R.id.tempImageView);//TODO delete this
        reminderDatabaseHelper = new DatabaseHelper(view.getContext());
        //set reminder list adapter
        reminderListView = (ListView) view.findViewById(R.id.reminderListView);
        rli = new ReminderListItemAdapter(view.getContext());
        reminderListView.setAdapter(rli);

        //set pendingAlarmIntents array
        registeredAlarms = new ArrayList<PendingIntent>();

        //BUTTONS
        Button btnStartReminder = (Button) view.findViewById(R.id.btnStartReminder);
        Button btnStopReminder = (Button) view.findViewById(R.id.btnStopReminder);

        /**
         * This function starts a set of reminders (repeating alarm managers)
         */
        btnStartReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        callReminderSerivceIntent = new Intent(getActivity(), CallReminderService.class);
                        callReminderSerivceIntent.putExtra(CallReminderService.serviceType, startReminder);
                        //callReminderSerivceIntent.putExtra()
                        getActivity().startService(callReminderSerivceIntent);
                    }
                }).start();
            }
        });


        /**
         * This function starts a set of reminders (repeating alarm managers)
         */
        btnStopReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (registeredAlarms.size() > 0) {
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

        //refresh with contact details
        loadContactsClass lcc1 = new loadContactsClass(view.getContext());
        lcc1.startLoading();

    }

    //**************************ReminderList adapter class*****************************//

    private class ReminderListItemAdapter extends BaseAdapter {

        Context cxt;

        //constructor
        public ReminderListItemAdapter(Context c) {
            cxt = c;
        }

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

        /**
         * returns the view of each list item in adapter
         */
        public View getView(int position, View convertView, ViewGroup parent) {
            //initialize a holder for list view objects
            Holder h = new Holder();
            View rowView;
            LayoutInflater inflater = (LayoutInflater) cxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.reminder_list_item, null);

            //get list objects from xml
            h.reminderTime = (TextView) rowView.findViewById(R.id.reminderListItemTime);
            h.contactName = (TextView) rowView.findViewById(R.id.reminderListItemContactName);
            h.contactIcon = (ImageView) rowView.findViewById(R.id.reminderListItemContactImage);

            //get reminder from DB
            Cursor rdbCursor = reminderDatabaseHelper.getAllRecords(ReminderTableContract.TABLE_NAME);
            rdbCursor.moveToPosition(position);
            String callTime = "Next Call Time: " +
                    GenericLib.longToTime(rdbCursor.getInt(rdbCursor.getColumnIndex(ReminderTableContract.COLUMN_NAME_REM_TIME)));

            //set values for list view objects
            h.reminderTime.setText(callTime);
            String numTxt = rdbCursor.getString(rdbCursor.getColumnIndex(ReminderTableContract.COLUMN_NAME_PH_NO));
            h.contactName.setText(numTxt);
            h.contactIcon.setImageResource(R.drawable.ic_alarm_black_48dp);

            //if contacts name and photo are loaded replace with them
            if (reminderDatabaseHelper.getRowCount(ContactsTableContract.TABLE_NAME) > 0) {
                try {
                    Cursor cTemp = reminderDatabaseHelper.getContactsRecord(numTxt);
                    cTemp.moveToFirst();
                    String cName = cTemp.getString(cTemp.getColumnIndex(ContactsTableContract.COLUMN_CONTACT_NAME));
                    h.contactName.setText(cName);
                    byte[] contImg = cTemp.getBlob(cTemp.getColumnIndex(ContactsTableContract.COLUMN_CONTACT_IMG_RES));
                    Bitmap theImage = BitmapFactory.decodeByteArray(contImg, 0, contImg.length);
                    h.contactIcon.setImageBitmap(theImage);
                } catch (Exception e) {
                }
            }
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
    }

    public static class loadContactsClass extends AsyncTaskLoader<Cursor>{

        public loadContactsClass(Context context) {
            super(context);
        }

        @Override
        public Cursor loadInBackground() {
            String[] projectionCursor = {
                    ContactsContract.CommonDataKinds.Phone.NUMBER,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI
            };
            Cursor data = getContext().getContentResolver().query(ContactsContract.Data.CONTENT_URI,projectionCursor,null,null,null);
            DatabaseHelper reminderDatabaseHelper = new DatabaseHelper(getContext());
            //store in database contactsTable
            reminderDatabaseHelper.emptyDb(ContactsTableContract.TABLE_NAME);
            if (!(data == null)) {
                data.moveToFirst();
                do {
                    String normNum = "";
                    String contactName = "";
                    byte[] contactImg = null;
                    try {
                        //TODO store contact image as a file and generate resource id
                        //TODO This is returning null always
                        normNum = (data.getString(data.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                        normNum = normNum.replaceAll("[^0-9]", "");
                        normNum = normNum.substring(normNum.length() - 10, normNum.length());
                        contactName = data.getString(data.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                        contactImg = data.getBlob(data.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI));
                        reminderDatabaseHelper.createContactsRecord(normNum, contactName, contactImg);
                    } catch (Exception e) {}
                } while (data.moveToNext());
            }

            return null;
        }


    }

}


//***********************************Loader Manager functions*****************************//
/*
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //get all contacts
        //TODO this is wrong, no numbers are generated
        String[] projectionCursor = {
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI
        };

        CursorLoader cldr = new CursorLoader(getActivity(), ContactsContract.Data.CONTENT_URI,projectionCursor,null,null,null);
        return cldr;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, final Cursor data) {
        //store in database contactsTable
        reminderDatabaseHelper.emptyDb(ContactsTableContract.TABLE_NAME);
        if (!(data == null)) {
            data.moveToFirst();
            do {
                String normNum = "";
                String contactName = "";
                byte[] contactImg = null;
                try {
                    //TODO store contact image as a file and generate resource id
                    //TODO This is returning null always
                    normNum = (data.getString(data.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                    normNum = normNum.replaceAll("[^0-9]", "");
                    normNum = normNum.substring(normNum.length() - 10, normNum.length());
                    contactName = data.getString(data.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    contactImg = data.getBlob(data.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI));
                    reminderDatabaseHelper.createContactsRecord(normNum, contactName, contactImg);
                } catch (Exception e) {
                }
            } while (data.moveToNext());
        }

        //refresh the custom adapter list view
        rli.notifyDataSetChanged();

    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }*/