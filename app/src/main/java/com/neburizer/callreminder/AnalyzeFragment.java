package com.neburizer.callreminder;

import android.app.Fragment;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by nm3 on 2/4/2016.
 */
public class AnalyzeFragment extends Fragment {

    //public constants
    public static final String fragment_pos = "list_position";

    //activity element variables
    public static TextView opText;
    Context cxt;
    NumberPicker skipDaysPicker;
    static int skipDays;
    public static Handler publicHandler;

    //analyzer variables
    static String opKey = "opKey";
    DatabaseHelper reminderDbHelper;
    float skipHours = 2f;
    int minRepeatCount = 2;
    long skipMillis = (long) (skipHours * 60 * 60 * 1000);

    //**********************************Default Fragment functions*********************************//
    @Override
    public View onCreateView (LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.analyze_fragment,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cxt = view.getContext();
        reminderDbHelper = new DatabaseHelper(cxt);
        setupHandler();
        opText = (TextView) view.findViewById(R.id.opText);
        Button btnAnalyze = (Button) view.findViewById(R.id.btn_analyze);
        Button btnTemp = (Button) view.findViewById(R.id.btn_temp);
        btnAnalyze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reminderDbHelper.emptyDb(ReminderTableContract.TABLE_NAME);
                AnalyzeCallLogsThread act = new AnalyzeCallLogsThread(cxt);
                act.setDaemon(true);
                act.start();
                CommonFunctions.showToast(cxt, "Thread started");
                opText.append("Analysis in progress...");
            }
        });
        btnTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AssetManager asm = cxt.getAssets();
                try {
                    InputStream ins = asm.open("testContact.jpg");
                    String out= Environment.getExternalStorageDirectory().getAbsolutePath() + "/tempfolder" ;
                    File outFile = new File(out,"testImage");
                    OutputStream ost = new FileOutputStream(outFile);
                    byte[] buffer = new byte[1024];
                    int read;
                    while((read = ins.read(buffer)) != -1){
                        ost.write(buffer, 0, read);
                    }
                    ins.close();
                    ins=null;
                    ost.flush();
                    ost.close();
                    ost = null;

                }catch (Exception e){}
                //CommonFunctions.insertCallLogsFromXml(cxt);
                /*Date d = new Date();
                reminderDbHelper.insertRecord("9789827780",d.getTime());
                CommonFunctions.showToast(cxt, "inserting dummy");
                Message msg = publicHandler.obtainMessage();
                Bundle b = new Bundle();
                b.putString("typeOfWork","analyzeCallLogs");
                msg.setData(b);
                publicHandler.sendMessage(msg);*/
            }
        });

    }

    //**********************************Default Fragment functions*********************************//


    public void btnDeleteCallLogs(View v) {
        reminderDbHelper.emptyDb(ReminderTableContract.TABLE_NAME);
        //this.getContentResolver().delete(CallLog.Calls.CONTENT_URI, null, null);
        //reminderDbHelper.insertDummyRecord();
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
