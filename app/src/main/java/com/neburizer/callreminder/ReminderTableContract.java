package com.neburizer.callreminder;

import android.provider.BaseColumns;

/**
 * Created by naveen on 4/17/2016.
 */
public class ReminderTableContract implements BaseColumns{
    //table names
    public static final String TABLE_NAME = "Reminders_Table";

    //column names
    public static final String COLUMN_NAME_ID = "rowId";
    public static final String COLUMN_NAME_PH_NO = "phNo";
    public static final String COLUMN_NAME_REM_TIME = "remTime";
;
}
