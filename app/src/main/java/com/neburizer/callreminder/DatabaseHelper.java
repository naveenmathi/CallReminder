package com.neburizer.callreminder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * Created by nm3 on 1/20/2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    //Sql keywords
    public static final String DATABASE_NAME = "ReminderDatabase.db";
    public static final int DATABASE_VERSION = 1;
    private static final String TEXT_TYPE = " TEXT";
    private static final String INT_PRIMARY = " INTEGER PRIMARY KEY";
    private static final String BLOB_TYPE = " BLOB";
    private static final String COMMA_SEP = ",";

    //create table sql queries
    private static final String SQL_CREATE_ENTRIES1 =
            "CREATE TABLE " + ReminderTableContract.TABLE_NAME + " (" +
                    ReminderTableContract.COLUMN_NAME_ID + INT_PRIMARY + COMMA_SEP +
                    ReminderTableContract.COLUMN_NAME_PH_NO + TEXT_TYPE + COMMA_SEP +
                    ReminderTableContract.COLUMN_NAME_REM_TIME + TEXT_TYPE + " )";
    private static final String SQL_CREATE_ENTRIES2 =
            "CREATE TABLE "+ ContactsTableContract.TABLE_NAME + " ("+
                    ContactsTableContract.COLUMN_CONTACT_NUMBER + INT_PRIMARY + COMMA_SEP +
                    ContactsTableContract.COLUMN_CONTACT_NAME + TEXT_TYPE + COMMA_SEP +
                    ContactsTableContract.COLUMN_CONTACT_IMG_RES + BLOB_TYPE + " )";

    //delete tables queries
    private static final String SQL_DELETE_ENTRIES1 = "DROP TABLE IF EXISTS " + ReminderTableContract.TABLE_NAME;
    private static final String SQL_DELETE_ENTRIES2 = "DROP TABLE IF EXISTS " + ContactsTableContract.TABLE_NAME;

    //Thread handling for this database helper --- SINGLETON METHOD
    private static DatabaseHelper sInstance;
    public static synchronized DatabaseHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase argDb) {

        argDb.execSQL(SQL_CREATE_ENTRIES1);
        argDb.execSQL(SQL_CREATE_ENTRIES2);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES1);
        db.execSQL(SQL_DELETE_ENTRIES2);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    /**
     * returns number of rows in given tableName
     * @param tableName
     * @return
     */
    public long getRowCount(String tableName) {
        SQLiteDatabase db = this.getWritableDatabase();
        long cnt  = DatabaseUtils.queryNumEntries(db, tableName);
        //db.close();
        return cnt;
    }

    /**
     * specific function to reminder table to create new record
     * @param phNo
     * @param rTime
     */
    public void createReminderRecord(String phNo, long rTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cValues = new ContentValues();
        long iDb=getRowCount(ReminderTableContract.TABLE_NAME);
        cValues.put(ReminderTableContract.COLUMN_NAME_ID, ++iDb);
        cValues.put(ReminderTableContract.COLUMN_NAME_PH_NO, phNo);
        cValues.put(ReminderTableContract.COLUMN_NAME_REM_TIME, String.valueOf(rTime));
        //cValues.put(ReminderTableContract.COLUMN_NAME_IMG, );
        db.insert(ReminderTableContract.TABLE_NAME, null, cValues);
        //db.close();
    }

    /**
     * specific function to contacts table to create new record
     * @param conNum - phone number of contact
     * @param conName - display name of contact
     * @param conPhoto - display image's photo file id
     */
    public void createContactsRecord(String conNum, String conName, byte[] conPhoto){
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues cValues = new ContentValues();
        cValues.put(ContactsTableContract.COLUMN_CONTACT_NUMBER, conNum);
        cValues.put(ContactsTableContract.COLUMN_CONTACT_NAME, conName);
        cValues.put(ContactsTableContract.COLUMN_CONTACT_IMG_RES, conPhoto);
        db.insertOrThrow(ContactsTableContract.TABLE_NAME, null, cValues);
        //db.close();
    }
    public Cursor getReminderRecord(int id)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "select * from "+
                ReminderTableContract.TABLE_NAME+
                " where "+ReminderTableContract.COLUMN_NAME_ID+"='"+id+"';";
        Cursor resultCursor = db.rawQuery(sql,null);
        //db.close();
        return resultCursor;
    }

    public Cursor getContactsRecord(String conNum)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "select * from "+
                ContactsTableContract.TABLE_NAME+
                " where "+ContactsTableContract.COLUMN_CONTACT_NUMBER+"="+conNum+";";
        Cursor resultCursor = db.rawQuery(sql,null);
        //db.close();
        return resultCursor;
    }

    public Cursor getAllRecords(String tableName)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "select * from " + tableName + ";";
        Cursor resultCursor = db.rawQuery(sql,null);
        //db.close();
        return resultCursor;
    }

    public boolean emptyDb(String tableName)
    {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete(tableName, null, null);
            //db.close();
            return true;
        }catch(Exception e) {
            return false;
        }
    }
}
