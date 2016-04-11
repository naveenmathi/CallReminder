package com.neburizer.callreminder;

import android.app.Fragment;
import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Date;

/**
 * Created by nm3 on 2/4/2016.
 */
public class HelpFragment extends Fragment {

    Context cxt;
    TextView op;

    @Override
    public View onCreateView (LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.help_fragment,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cxt = view.getContext();
        op = (TextView) view.findViewById(R.id.textViewHelp);
        String SELECTION =  ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER + " LIKE ?";
        Cursor c1 = view.getContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,SELECTION,new String[]{"%9789827780"},null);
        c1.moveToFirst();
        op.append("\n" + c1.getString(c1.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER)));
        while(c1.moveToNext())
        {
            op.append("\n"+c1.getString(c1.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER)));
        }
    }
}
