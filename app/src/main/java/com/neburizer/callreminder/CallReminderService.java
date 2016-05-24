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
        String timeOfAlarm = c.getString(c.getColumnIndex(ReminderTableContract.COLUMN_NAME_REM_TIME));

        int temp = c.getInt(c.getColumnIndex(ReminderTableContract.COLUMN_NAME_ID));
        CommonFunctions.pushCallRemindNotification(this,temp);
    }
}
