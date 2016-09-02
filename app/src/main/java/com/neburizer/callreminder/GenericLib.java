package com.neburizer.callreminder;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.CallLog;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by nm3 on 1/4/2016.
 */
public class GenericLib {


    static int durationToast = Toast.LENGTH_SHORT;
    static int notificationID = 1;
    /**
     * Common Toast function
     * @param context
     * @param message String
     */
    public static void showToast(Context context, String message)
    {
        Toast.makeText(context, message, durationToast).show();
    }

    /**
     * Simple status bar notification
     * @param cxt Context from which notification manager can be called
     * @param reminderId
     * @return returns the notification ID for update purposes
     */
    public static int pushCallRemindNotification(Context cxt,int reminderId){
        //Get data for notification
        DatabaseHelper dbh = DatabaseHelper.getInstance(cxt);
        Cursor c = dbh.getReminderRecord(reminderId);
        c.moveToFirst();
        String msg = c.getString(c.getColumnIndex(ReminderTableContract.COLUMN_NAME_PH_NO));
        Cursor contactCursor = dbh.getContactsRecord(msg);
        contactCursor.moveToFirst();

        //Notification Builder Data
        byte[] b = contactCursor.getBlob(contactCursor.getColumnIndex(ContactsTableContract.COLUMN_CONTACT_IMG_RES));
        if(b!=null){
            Bitmap image = BitmapFactory.decodeByteArray(b, 0, b.length);
        }
        Notification mBuilder = new NotificationCompat.Builder(cxt)
                .setSmallIcon(R.drawable.ic_alarm_black_48dp)
                .setContentTitle("title tst")
                .setContentText(msg).build();


        //call notify using notification manager
        NotificationManager mNotificationManager = (NotificationManager) cxt.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(notificationID, mBuilder);
        notificationID++;
        return (notificationID-1);
    }

    /**
     * Returns dd/MM/yy string format of given long
     * @param l -> Date value in long
     * @return
     */
    public static String longToDate(long l) {
        Date d = new Date(l);
        SimpleDateFormat df2 = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
        return (df2.format(d));
    }

    /**
     * Returns hours string format of given long
     * @param l -> Date value in long
     * @return
     */
    public static String longToTime(long l) {
        Date d = new Date(l);
        SimpleDateFormat df2 = new SimpleDateFormat("hh:mm a");
        return (df2.format(d));
    }


    /**
     * returns only last 10 chars of given string(valid phone number)
     * @param phNo - input string phonen number(any length)
     * @return
     */
    public static String returnValidPhNo(String phNo)
    {
        int phNoLen = phNo.length();
        if(phNoLen>10)
            return phNo.substring(phNoLen-10);
        return phNo;
    }
    /**
     * Insert call logs from xml callLog.csv file to emulator
     */
    public static void insertCallLogsFromXml(Context cxt)
    {
        showToast(cxt,"Calling insertCallLogsFromXml");
        clearCallLogs(cxt);
        BufferedReader reader=null;
        try {
            reader = new BufferedReader(new InputStreamReader(cxt.getAssets().open("callLog2.csv")));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] rowData = line.split(",");
                insertCallLog(rowData[2],rowData[0],rowData[5],rowData[1],cxt);
                Log.v("Reading XML line : ", line);
            }
        }
        catch (IOException ex) {
            Log.v("Reading xml: ","Error opening xml file");
        }
        finally {
            try {
                reader.close();
            }
            catch (IOException e) {
                Log.v("Closing xml: ","Error closing xml file");
            }
        }


    }

    /**
     * inserts individual call logs
     * @param number - phone number
     * @param dt - date
     * @param duration  - duration
     * @param type - incoming,outgoing,missed
     * @param cxt - context of main activity
     */
    public static void insertCallLog(String number, String dt, String duration, String type,Context cxt)
    {
        //convert date string to millis
        Date d = null;
        SimpleDateFormat f = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
        try{
            d = f.parse(dt);
        } catch(Exception e) { e.printStackTrace();}
        //convert duration to seconds
        String[] durArray = duration.split(" ");
        long durInSeconds = new Integer(durArray[0])*60 + new Integer(durArray[2]);
        //convert type string to int
        int typeInt = 0;
        switch(type)
        {
            case "Incoming": typeInt=1;
                break;
            case "Outgoing": typeInt=2;
                break;
            case "Missed": typeInt=3;
                break;
        }
        //insert in call log
        ContentValues values = new ContentValues();
        values.put(CallLog.Calls.NUMBER, number);
        values.put(CallLog.Calls.DATE, d.getTime());
        values.put(CallLog.Calls.DURATION, durInSeconds);
        values.put(CallLog.Calls.TYPE, typeInt);
        values.put(CallLog.Calls.NEW, 1);
        cxt.getContentResolver().insert(CallLog.Calls.CONTENT_URI, values);
    }

    private static void clearCallLogs(Context cxt) {
        cxt.getContentResolver().delete(CallLog.Calls.CONTENT_URI, null, null);
    }

    /**
     * @param timeCalled_1
     * @return only time of the day from the date provided
     */
    public static long roundTime(long timeCalled_1) {
        return timeCalled_1 % 86400000; //1 day in milli seconds
    }

    /**
     * Return a circular bitmap of given image
     * @param inputImg
     * @return
     */
    public static Bitmap getCroppedImage(Bitmap inputImg) {
        int xWidth = inputImg.getWidth();
        int yHeight = inputImg.getHeight();
        Bitmap output = Bitmap.createBitmap(xWidth,yHeight, Bitmap.Config.ARGB_8888);

        //To draw anythign, we need canvas(host draw calls), Paint(store colour informations),
        //Shape(rect,etc),Bitmap(to store pixels)
        Canvas canvas = new Canvas(output);
        Paint paint = new Paint();
        Rect rect = new Rect(0,0,xWidth,yHeight);

        //draw a circle first, then draw the image with SRC_IN mode -> draws only intersecting area
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        canvas.drawCircle(xWidth/2,yHeight/2,xWidth/2,paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(inputImg,rect,rect,paint);
        return output;
    }
}
