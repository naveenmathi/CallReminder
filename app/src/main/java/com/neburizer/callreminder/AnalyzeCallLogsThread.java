package com.neburizer.callreminder;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Message;
import android.provider.CallLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
//git check
/**
 * Created by nm3 on 12/24/2015.
 */
public class AnalyzeCallLogsThread extends Thread{
    Context cxt;
    int skipDays = 2;
    float skipHours = 4f;
    int minRepeatCount = 2;
    ArrayList<String> reminderNames;
    ArrayList<Long> reminderTimes;
    String opKey = "opKey";
    long skipMillis = (long)(skipHours * 60 * 60 * 1000);
    DatabaseHelper reminderDbHelper;

    //contructor
    public AnalyzeCallLogsThread(Context c)
    {
        cxt = c;
        reminderDbHelper = MainActivity.rdh;
    }
    @Override
    public void run() {
        dowork();
        Message msg = AnalyzeFragment.publicHandler.obtainMessage();
        Bundle b = new Bundle();
        b.putString("typeOfWork","analyzeCallLogs");
        msg.setData(b);
        AnalyzeFragment.publicHandler.sendMessage(msg);
    }//run method

    private void dowork() {
        //create empty reminder list
        reminderNames = new ArrayList<String>();
        reminderTimes = new ArrayList<Long>();
        int reminderIndex = 0;
        String opMessage="Call Logs: \n";
        //pointer for call logs URI
        Cursor callLogPointer  = cxt.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, CallLog.Calls.DATE + " DESC");
        int number = callLogPointer.getColumnIndex(CallLog.Calls.NUMBER);
        int timeOfCall = callLogPointer.getColumnIndex(CallLog.Calls.DATE);
        if(callLogPointer.getCount()>0) {
            //generate hash map of persons
            HashMap<String, person> hmPersons = new HashMap<String, person>();
            while (callLogPointer.moveToNext()) {
                String c_number = GenericLib.returnValidPhNo(callLogPointer.getString(number));
                String ttt = callLogPointer.getString(timeOfCall);
                long c_time = Long.valueOf(ttt);
                if (!hmPersons.containsKey(c_number)) {
                    person p = new person(c_number);
                    p.addCall(c_time);
                    p.setName(c_number);
                    hmPersons.put(c_number, p);
                    p = null;
                } else {
                    hmPersons.get(c_number).addCall(c_time);
                }
                //opMessage+=(c_number + " - " + c_time + "\n");
            }
            callLogPointer.close();

            /*@
            {
            print output of hashmap
            Iterator it = hmPersons.entrySet().iterator();
            while (it.hasNext())
            {
                Map.Entry entry = (Map.Entry) it.next();
                opMessage+=("Person: "+ entry.getKey() + ":\n");
                person p = (person) entry.getValue();
                Stack<Long> st = new Stack<Long>();
                st.addAll(p.getCalledList());
                while(!st.isEmpty())
                {
                    opMessage+=("\n->"+GenericLib.longToDate(st.pop()));
                }
            }
            sendMsg(opMessage);}
            */


            //generate reminder list
            Iterator iPersons = hmPersons.entrySet().iterator();
            //[A] - loop through each phone number
            while (iPersons.hasNext()) {
                Map.Entry entry = (Map.Entry) iPersons.next();
                person p = (person) entry.getValue();

                List<Long> calledList = p.getCalledList();
                Iterator<Long> iCalledList = calledList.iterator();

                long[] calledArray = new long[calledList.size()];
                int i = 0;
                while (iCalledList.hasNext()) {
                    calledArray[i] = iCalledList.next();
                    i++;
                }
                //[B] - loop through all called list timings
                for (i = 0; i < calledArray.length; i++) {
                    long timeCalled_1 = calledArray[i];
                    int repeatCount = 0;
                    long prevDiff = 0;

                    //[C] - loop through subarray of called list timings starting from i+1
                    for (int j = i + 1; j < calledArray.length; j++) {
                        long timeCalled_2 = calledArray[j];
                        long timeDiff = timeCalled_1 - timeCalled_2;
                        int found = 0;

                        //[D] - loop for validating each skip day
                        for (int iSkipDays = 1; iSkipDays <= skipDays; iSkipDays++) {
                            if (timeDiff < ((long) 86400000 * iSkipDays + skipMillis)) {   //condition if timeDiff lies between (skipdays*24 +/- skipHours)
                                if (timeDiff > ((long) 86400000 * iSkipDays - skipMillis) && (timeDiff - prevDiff) > ((long) (86400000 - skipMillis))) {
                                    repeatCount++;
                                    prevDiff = timeDiff;        //for skipping same day logs
                                    if (repeatCount >= minRepeatCount) {
                                        reminderDbHelper.createReminderRecord(p.getName(), GenericLib.roundTime(timeCalled_1));
                                        found = 1;
                                        prevDiff = 0;
                                        break;
                                    }
                                }
                            } else {
                                if (timeDiff > ((long) 86400000 * skipDays + skipMillis))
                                    break;  //maximum skip time-interval exceeded
                            }
                        }
                        if (found == 1)
                            break;        //break iteration if repeatCount>minRepeatCount
                    }//[C] - loop through subarray of called list timings starting from i+1
                }//[B] - loop through all called list timings
            }//[A] - loop through each phone numberreminderDbHelper.insertRecord(reminderNames.get(i), reminderTimes.get(i));
        }
    }


}
