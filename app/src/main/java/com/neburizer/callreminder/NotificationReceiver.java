package com.neburizer.callreminder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.io.FileNotFoundException;

/**
 * Created by nm3 on 5/25/2016.
 */
public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int reminderId = intent.getIntExtra(ReminderTableContract.COLUMN_NAME_ID,0);
        try {
            GenericLib.pushCallRemindNotification(context,reminderId);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //GenericLib.showToast(context,"toast from BR = "+reminderId);
    }
}
