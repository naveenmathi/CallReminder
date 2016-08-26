package com.neburizer.callreminder;

import android.content.AsyncTaskLoader;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import java.io.ByteArrayInputStream;

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
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
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
                    int contactId = data.getInt(data.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                    Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);
                    Uri photoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
                    Cursor tmpPhCursor = getContext().getContentResolver().query(photoUri,
                            new String[] {ContactsContract.Contacts.Photo.PHOTO}, null, null, null);
                    if (tmpPhCursor != null) {
                        if (tmpPhCursor.moveToFirst()) {
                            contactImg = tmpPhCursor.getBlob(0);
                        }
                        tmpPhCursor.close();
                    }

                    reminderDatabaseHelper.createContactsRecord(normNum, contactName, contactImg);
                }
                catch (Exception e) {
                    Log.e("CallReminder","InsertContactErr",e);
                }
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