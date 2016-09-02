package com.neburizer.callreminder;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.Calendar;
import java.util.Stack;

/**
 * Created by nm3 on 7/25/2016.
 */
public class CallReminderService extends Service {

    private static final String LOG_TAG = "CallReminderService";
    public static String SERVICE_TYPE = "serviceType";
    public static String STOP_SERVICE = "STOP";
    public static String START_SERVICE = "START";
    Stack<PendingIntent> storedAlarms;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String workType = intent.getExtras().getString(SERVICE_TYPE);
        final Context cxt = this; //service is a context


        if (workType.equals(START_SERVICE)) {
            storedAlarms = new Stack<PendingIntent>();
            GenericLib.showToast(this, "started");
            Log.i(LOG_TAG, "Call Reminder Starting service");

            //Stop intent
            Intent stopIntent = new Intent(this,this.getClass());
            stopIntent.putExtra(SERVICE_TYPE,STOP_SERVICE);
            PendingIntent pStopIntent = PendingIntent.getService(this,25,stopIntent,PendingIntent.FLAG_UPDATE_CURRENT);

            //Notification to prevent service from getting killed
            NotificationCompat.Builder infoNotification = new NotificationCompat.Builder(this);
            infoNotification
                    .setContentTitle("Call Reminder")
                    .setContentText("Service Running")
                    .setSmallIcon(R.drawable.ic_alarm_black_48dp)
                    .setPriority(1)
                    .addAction(R.drawable.ic_alarm_black_48dp,"STOP",pStopIntent)
                    .setOngoing(true);
            NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(25, infoNotification.build());

            AlarmManager alarmMgr;
            alarmMgr = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
            DatabaseHelper dbh = DatabaseHelper.getInstance(cxt);
            Cursor c = dbh.getAllRecords(ReminderTableContract.TABLE_NAME);
            while (c.moveToNext()) {
                Intent notificationIntent = new Intent(this, NotificationReceiver.class);
                int uniqueID = c.getInt(c.getColumnIndex(ReminderTableContract.COLUMN_NAME_ID));
                notificationIntent.putExtra(ReminderTableContract.COLUMN_NAME_ID, uniqueID);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, (200 + uniqueID), notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                storedAlarms.add(pendingIntent);
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
        }else if(workType.equals(STOP_SERVICE)){
            GenericLib.showToast(this,"stopservice");
            this.stopSelf();
        }
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        GenericLib.showToast(this,"service destroying");
        for(PendingIntent pi:storedAlarms)
        {
            AlarmManager alarmMgr;
            alarmMgr = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
            alarmMgr.cancel(pi);
        }
    }
}