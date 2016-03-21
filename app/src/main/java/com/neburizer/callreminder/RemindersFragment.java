package com.neburizer.callreminder;

import android.app.Fragment;
import android.app.ListFragment;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;


/**
 * Created by nm3 on 2/4/2016.
 */
public class RemindersFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView (LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.reminders_fragment,container,false);
        ListView lv = (ListView) v.findViewById(R.id.reminderListView);
        //String[] itemarr = {"one","two"};
        //ArrayAdapter<String> items = new ArrayAdapter<String>(v.getContext(),R.layout.testview,itemarr);
        lv.setAdapter(new ReminderListItemAdapter(v.getContext()));
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
