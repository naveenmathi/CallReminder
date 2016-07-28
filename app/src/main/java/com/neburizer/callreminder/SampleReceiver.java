package com.neburizer.callreminder;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Created by nm3 on 7/27/2016.
 */
public class SampleReceiver extends BroadcastReceiver {
    int testID = 132;

    @Override
    public void onReceive(Context context, Intent intent) {
        /*//Toast.makeText(context, "test", Toast.LENGTH_SHORT).show();
        //Notification Builder
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        //mBuilder.setSmallIcon(contactCursor.getBlob(contactCursor.getColumnIndex(ContactsTableContract.COLUMN_CONTACT_IMG_RES)));
        mBuilder.setSmallIcon(R.drawable.ic_alarm_black_48dp);
        mBuilder.setContentTitle("title tst");
        mBuilder.setContentText(""+testID);
        //mBuilder.setContentText("msg");
        //GenericLib.showToast(cxt,msg);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(testID++, mBuilder.build());*/
        GenericLib.pushCallRemindNotification(context,1);

        AlarmManager alarmMgr;
        alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent smpPendIntent = PendingIntent.getBroadcast(context,345,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        Calendar sysCal = Calendar.getInstance();
        sysCal.add(Calendar.SECOND,5);
        alarmMgr.set(AlarmManager.RTC_WAKEUP, sysCal.getTimeInMillis(), smpPendIntent);
    }
}
