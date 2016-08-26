package com.neburizer.callreminder;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.provider.ContactsContract;
import android.util.Log;

/**
 * Created by nm3 on 8/25/2016.
 */
public class ContactsLoaderAsync extends AsyncTaskLoader<Cursor>
{

    public ContactsLoaderAsync(Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public Cursor loadInBackground() {
        //projection for contacts
        String[] projectionCursor = {
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI
        };

        //Main query for contacts
        Cursor data = getContext().getContentResolver()
                .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,projectionCursor,null,null,null);
        DatabaseHelper reminderDatabaseHelper = DatabaseHelper.getInstance(getContext());

        //store in database contactsTable
        if (data != null && reminderDatabaseHelper.getRowCount(ContactsTableContract.TABLE_NAME) < 1) {
            data.moveToFirst();
            do {
                String normNum = "";
                String contactName = "";
                byte[] contactImg = null;
                try {
                    //TODO store contact image as a file and generate resource id
                    normNum = (data.getString(data.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                    normNum = normNum.replaceAll("[^0-9]", "");
                    normNum = normNum.substring(normNum.length() - 10, normNum.length());
                    contactName = data.getString(data.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    contactImg = data.getBlob(data.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI));
                    reminderDatabaseHelper.createContactsRecord(normNum, contactName, contactImg);
                }
                catch (Exception e) {Log.v("sqlConstraintExcp",normNum);}
            } while (data.moveToNext());
        }
        data.close();
        return  null;
    }

    @Override
    public void deliverResult(Cursor data) {
        super.deliverResult(data);
    }
}