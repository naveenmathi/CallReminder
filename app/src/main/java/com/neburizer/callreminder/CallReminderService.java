package com.neburizer.callreminder;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by nm3 on 7/25/2016.
 */
public class CallReminderService extends IntentService {

    /**
     * Default constructor with no arguments
     */
    public CallReminderService(){
        super("CallReminderService");
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public CallReminderService(String name) {
        super(name);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        
    }
}
