package com.neburizer.callreminder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by nm3 on 5/25/2016.
 */
public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int reminderId = intent.getIntExtra(ReminderTableContract.COLUMN_NAME_ID,0);
        CommonFunctions.pushCallRemindNotification(context,reminderId);
        //CommonFunctions.showToast(context,"toast from BR");
    }
}
