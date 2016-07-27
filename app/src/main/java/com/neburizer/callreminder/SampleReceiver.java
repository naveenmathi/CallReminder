package com.neburizer.callreminder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Created by nm3 on 7/27/2016.
 */
public class SampleReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "test", Toast.LENGTH_SHORT).show();
        AlarmManager alarmMgr;
        alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent smpPendIntent = PendingIntent.getBroadcast(context,345,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        Calendar sysCal = Calendar.getInstance();
        sysCal.add(Calendar.SECOND,5);
        alarmMgr.set(AlarmManager.RTC_WAKEUP, sysCal.getTimeInMillis(), smpPendIntent);
    }
}
