package com.neburizer.callreminder;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;

/**
 * Created by nm3 on 5/5/2016.
 */
public class CallReminderService extends IntentService {

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public CallReminderService(String name) {
        super(name);
    }

    public CallReminderService(){
        super("CallReminderService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        DatabaseHelper dbh = MainActivity.rdh;
        Cursor c = dbh.getAllRecords(ReminderTableContract.TABLE_NAME);
        c.moveToFirst();
        
        String temp = c.getString(c.getColumnIndex(ReminderTableContract.COLUMN_NAME_PH_NO));
        CommonFunctions.pushNotification(this,"testTitle2",temp,R.drawable.ic_alarm_black_48dp);
    }
}
