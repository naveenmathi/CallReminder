package com.neburizer.callreminder;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.Calendar;

/**
 * Created by nm3 on 7/25/2016.
 */
public class CallReminderService extends Service {

    private static final String LOG_TAG = "CallReminderService";

    public static String serviceType = "serviceType";

    @Override
    public int onStartCommand(Intent intent,int flags, int startId){
        GenericLib.showToast(this,"started");
        Log.i(LOG_TAG,"Call Reminder Starting service");
        String workType = intent.getExtras().getString(serviceType);
        final Context cxt = this; //service is a context
        if(workType.equals(RemindersFragment.startReminder))
        {
            //Notification to prevent service from getting killed
            NotificationCompat.Builder infoNotification = new NotificationCompat.Builder(this);
            infoNotification
                    .setContentTitle("Call Reminder")
                    .setContentText("Service Running")
                    .setSmallIcon(R.drawable.ic_alarm_black_48dp)
                    .setOngoing(true);
            NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(25, infoNotification.build());

            //test sample receiver
            /*AlarmManager alarmMgr;
            alarmMgr = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
            Intent smpIntent = new Intent(this,SampleReceiver.class);
            PendingIntent smpPendIntent = PendingIntent.getBroadcast(this,345,smpIntent,PendingIntent.FLAG_UPDATE_CURRENT);
            alarmMgr.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), smpPendIntent);*/

            AlarmManager alarmMgr;
            alarmMgr = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
            DatabaseHelper dbh = new DatabaseHelper(cxt);
            Cursor c = dbh.getAllRecords(ReminderTableContract.TABLE_NAME);
            while(c.moveToNext()) {
                Intent notificationIntent = new Intent(this, NotificationReceiver.class);
                int uniqueID = c.getInt(c.getColumnIndex(ReminderTableContract.COLUMN_NAME_ID));
                notificationIntent.putExtra(ReminderTableContract.COLUMN_NAME_ID, uniqueID);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, (200 + uniqueID), notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                //RemindersFragment.registeredAlarms.add(pendingIntent);    TODO add alarms to list
                int alarmTime = c.getInt(c.getColumnIndex(ReminderTableContract.COLUMN_NAME_REM_TIME));
                Calendar systemCal = Calendar.getInstance();
                Calendar alarmCal = Calendar.getInstance();   //original code
                alarmCal.setTimeInMillis(alarmTime);
                systemCal.set(Calendar.HOUR_OF_DAY, alarmCal.get(Calendar.HOUR_OF_DAY));
                systemCal.set(Calendar.MINUTE, alarmCal.get(Calendar.MINUTE));
                systemCal.set(Calendar.SECOND, alarmCal.get(Calendar.SECOND));
                //alarmMgr.set(AlarmManager.RTC_WAKEUP, systemCal.getTimeInMillis(), pendingIntent); TODO revert normal alarm time
                alarmMgr.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pendingIntent);
            }
        }
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
