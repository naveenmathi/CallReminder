package com.neburizer.callreminder;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.provider.CallLog;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {

    //activity element variables
    static TextView opText;
    Context cxt;
    NumberPicker skipDaysPicker;
    static int skipDays;

    //side drawer
    private String[] sTitles;
    TypedArray sImages;
    private DrawerLayout sDrawerLayout;
    private ListView sDrawerList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cxt = getApplicationContext();
        getActionBar().setIcon(R.drawable.ic_alarm_black_48dp);
        //side-drawer initialize
        sTitles = getResources().getStringArray(R.array.titles_array);
        sImages = getResources().obtainTypedArray(R.array.navigation_drawer_images);
        sDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        sDrawerList = (ListView) findViewById(R.id.left_drawer);
        sDrawerList.setAdapter(new CustomAdapter(this,sTitles,sImages));
        sDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        //initialize activity elements to variables
        cxt = getApplicationContext();
        /*opText = (TextView) findViewById(R.id.opText);
        skipDaysPicker = (NumberPicker) findViewById(R.id.skipDaysPicker);
        skipDaysPicker.setMinValue(1);
        skipDaysPicker.setMaxValue(25);
        skipDaysPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                skipDays = newVal;
            }
        });*/
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Fragment newF = new HelpFragment();
            Bundle args = new Bundle();
            args.putInt(HelpFragment.fragment_pos,i);
            newF.setArguments(args);
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.animator.fade_in,0,0,0);
            ft.replace(R.id.content_frame,newF).commit();
            sDrawerLayout.closeDrawer(sDrawerList);
            setTitle(sTitles[i]);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    //*****************  ^  setup  ^  ************************ v User Code v ********************************************************//

    static String opKey = "opKey";
    ReminderDatabaseHelper reminderDbHelper = new ReminderDatabaseHelper(this);
    float skipHours = 2f;
    int minRepeatCount = 2;

    long skipMillis = (long) (skipHours * 60 * 60 * 1000);
    //MAIN THREAD HANDLER
    static Handler btnHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            String string = bundle.getString(opKey);
            opText.append(string);
        }
    };

    /**
     * core function for analysing call logs and generates person data
     */
    public void analyzeCallLogs(View v) {
        reminderDbHelper.emptyDb();
        AnalyzeCallLogsThread act = new AnalyzeCallLogsThread(cxt,reminderDbHelper);
        act.run();
        for(int iR=1; iR< reminderDbHelper.getRowCount(); iR++)
            opText.append(reminderDbHelper.getRecord(iR));

    }

    public void btnInsertCallLogs(View v) {
        CommonFunctions.insertCallLogsFromXml(cxt);
        CommonFunctions.showToast(cxt, "already inserted");
    }

    public void btnDeleteCallLogs(View v) {
        reminderDbHelper.emptyDb();
        //this.getContentResolver().delete(CallLog.Calls.CONTENT_URI, null, null);
       // reminderDbHelper.insertDummyRecord();

    }

}
