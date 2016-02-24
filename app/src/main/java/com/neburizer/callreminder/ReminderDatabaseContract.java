package com.neburizer.callreminder;

import android.provider.BaseColumns;

/**
 * Created by nm3 on 1/20/2016.
 */
public class ReminderDatabaseContract {
    public ReminderDatabaseContract(){}
    public static abstract class ReminderEntry implements BaseColumns {
        public static final String TABLE_NAME = "reminders";
        public static final String COLUMN_NAME_ID = "rowId";
        public static final String COLUMN_NAME_PH_NO = "phNo";
        public static final String COLUMN_NAME_REM_TIME = "remTime";
    }
}
