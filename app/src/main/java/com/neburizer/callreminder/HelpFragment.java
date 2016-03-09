package com.neburizer.callreminder;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

/**
 * Created by nm3 on 2/4/2016.
 */
public class HelpFragment extends Fragment {

    //public constants
    public static final String fragment_pos = "list_position";

    //activity element variables
    public static TextView opText;
    Context cxt;
    NumberPicker skipDaysPicker;
    static int skipDays;
    public static Handler publicHandler;

    @Override
    public View onCreateView (LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState){
        switch (getArguments().getInt(fragment_pos)){
            case 0:
                return inflater.inflate(R.layout.home_fragment,container,false);
            case 1:
                return inflater.inflate(R.layout.reminders_fragment,container,false);
            case 2:
                return inflater.inflate(R.layout.analyze_fragment,container,false);
            case 3:
                return inflater.inflate(R.layout.help_fragment,container,false);
            default:
                return inflater.inflate(R.layout.home_fragment,container,false);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cxt = view.getContext();
        setupHandler();
        opText = (TextView) view.findViewById(R.id.opText);
        Button btnAnalyze = (Button) view.findViewById(R.id.btn_analyze);
        btnAnalyze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reminderDbHelper.emptyDb();
                AnalyzeCallLogsThread act = new AnalyzeCallLogsThread(cxt,reminderDbHelper);
                act.setDaemon(true);
                act.start();
                CommonFunctions.showToast(cxt, "Thread started");
                opText.append("Analysis in progress...");
            }
        });
    }

    //analyzer variables
    static String opKey = "opKey";
    ReminderDatabaseHelper reminderDbHelper = new ReminderDatabaseHelper(cxt);
    float skipHours = 2f;
    int minRepeatCount = 2;
    long skipMillis = (long) (skipHours * 60 * 60 * 1000);

    /**
     * core function for analysing call logs and generates person data
     */


    public void btnInsertCallLogs(View v) {
        CommonFunctions.insertCallLogsFromXml(cxt);
        CommonFunctions.showToast(cxt, "already inserted");
    }

    public void btnDeleteCallLogs(View v) {
        reminderDbHelper.emptyDb();
        //this.getContentResolver().delete(CallLog.Calls.CONTENT_URI, null, null);
        // reminderDbHelper.insertDummyRecord();
    }


    private void setupHandler() {
        publicHandler = new Handler(){
            public void handleMessage(Message msg)
            {
                Bundle tempB = msg.getData();
                String typeOfWork = tempB.getString("typeOfWork");
                if(typeOfWork=="analyzeCallLogs")
                {
                    opText.append("DONE");
                }
            }
        };
    }

}
