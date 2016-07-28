package com.neburizer.callreminder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.io.FileNotFoundException;
import java.util.Calendar;

/**
 * Created by nm3 on 5/25/2016.
 */
public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int reminderId = intent.getIntExtra(ReminderTableContract.COLUMN_NAME_ID,0);
        GenericLib.pushCallRemindNotification(context,reminderId);
        //GenericLib.showToast(context,"toast from BR = "+reminderId);

        //Repeat this in 24 hours time
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,(200+reminderId) , intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Calendar sysCal = Calendar.getInstance();
        sysCal.add(Calendar.SECOND,10);
        alarmManager.set(AlarmManager.RTC_WAKEUP, sysCal.getTimeInMillis(), pendingIntent);
    }
}
