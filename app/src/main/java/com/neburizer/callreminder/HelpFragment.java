package com.neburizer.callreminder;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by nm3 on 2/4/2016.
 */
public class HelpFragment extends Fragment {

    //public constants
    public static final String fragment_pos = "list_position";

    @Override
    public View onCreateView (LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState){
        switch (getArguments().getInt(fragment_pos)){
            case 0:
                return inflater.inflate(R.layout.home_fragment,container,false);
            case 1:
                return inflater.inflate(R.layout.reminders_fragment,container,false);
            case 2:
                View tempView = inflater.inflate(R.layout.analyze_fragment,container,false);
                MainActivity.opText = (TextView) tempView.findViewById(R.id.opText);
                return tempView;
            case 3:
                return inflater.inflate(R.layout.help_fragment,container,false);
            default:
                return inflater.inflate(R.layout.home_fragment,container,false);
        }
    }
}
