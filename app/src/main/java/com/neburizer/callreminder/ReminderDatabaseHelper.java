package com.neburizer.callreminder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Locale;


/**
 * Created by nm3 on 1/20/2016.
 */
public class ReminderDatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "ReminderDatabase.db";
    public static final int DATABASE_VERSION = 3;
    private static final String TEXT_TYPE = " TEXT";
    private static final String INT_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";
    private static final String TABLE_NAME = ReminderDatabaseContract.ReminderEntry.TABLE_NAME;
    public static final String COLUMN_NAME_ID = ReminderDatabaseContract.ReminderEntry.COLUMN_NAME_ID;
    public static final String COLUMN_NAME_PH_NO = ReminderDatabaseContract.ReminderEntry.COLUMN_NAME_PH_NO;
    public static final String COLUMN_NAME_REM_TIME = ReminderDatabaseContract.ReminderEntry.COLUMN_NAME_REM_TIME;
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_NAME_ID + " INTEGER PRIMARY KEY" + COMMA_SEP +
                    COLUMN_NAME_PH_NO + TEXT_TYPE + COMMA_SEP +
                    COLUMN_NAME_REM_TIME + TEXT_TYPE + " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    SQLiteDatabase db;

    public ReminderDatabaseHelper(Context context) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase argDb) {
        argDb.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public long getRowCount() {
        db = this.getWritableDatabase();
        long cnt  = DatabaseUtils.queryNumEntries(db, TABLE_NAME);
        return cnt;
    }

    public void insertRecord(String phNo, long rTime) {
        db = this.getWritableDatabase();
        ContentValues cValues = new ContentValues();
        long iDb=getRowCount();
        cValues.put(COLUMN_NAME_ID,++iDb);
        cValues.put(COLUMN_NAME_PH_NO,phNo);
        cValues.put(COLUMN_NAME_REM_TIME, String.valueOf(rTime));
        db.insert(TABLE_NAME,null,cValues);
    }

    public String getRecord(int id)
    {
        db = this.getReadableDatabase();
        String opBuilder = "Result: ";
        String sql = "select * from "+
                TABLE_NAME+
                " where "+COLUMN_NAME_ID+"='"+id+"';";
        Cursor cursor = db.rawQuery(sql,null);
        cursor.moveToFirst();
        opBuilder=opBuilder+(cursor.getString(cursor.getColumnIndex(COLUMN_NAME_PH_NO)));
        String remTime = CommonFunctions.longToTime(Long.valueOf(cursor.getString(cursor.getColumnIndex(COLUMN_NAME_REM_TIME))));
        opBuilder=opBuilder+("@"+remTime);
        return opBuilder;
    }

    public boolean emptyDb()
    {
        try {
            db = this.getWritableDatabase();
            db.delete(TABLE_NAME, null, null);
            return true;
        }catch(Exception e) {
            return false;
        }
    }
}
