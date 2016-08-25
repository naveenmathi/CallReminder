package com.neburizer.callreminder;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.telephony.PhoneNumberUtils;
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
        getLoaderManager().initLoader(5,null,contactsLoader);
    }

    private LoaderManager.LoaderCallbacks<Cursor> contactsLoader = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new ContactsLoaderAsync(cxt);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            /*data.moveToFirst();
            op.append("\ntest: "+data.getString(data.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
            op.append("\ntest: "+data.getString(data.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));*/
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    };

}



