package com.neburizer.callreminder;

import android.provider.BaseColumns;

/**
 * Created by naveen on 4/17/2016.
 */
public class ContactsTableContract implements BaseColumns{
    //table name
    public static final String TABLE_NAME = "Contacts_Table";

    //column names
    public static final String COLUMN_CONTACT_NUMBER = "contactNumber";
    public static final String COLUMN_CONTACT_NAME = "contactName";
    public static final String COLUMN_CONTACT_IMG_RES = "contactImgRes";
}
