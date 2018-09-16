package com.kvm.automaticattendancemarker.vipers.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.kvm.automaticattendancemarker.ProfessorMenu;
import com.kvm.automaticattendancemarker.utilities.dbrel.AttendanceSystemDBHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * Created by VIPER GTS on 09-May-17.
 */

public class MarkerService extends Service
{
    private final IBinder mBinder = new MarkerBinder();

    private Bundle bundle;
    private Notification notification;
    private Context contextForService;

    ////////////////////////////////////////////////// Settings
    private SharedPreferences settings;

    private long pollRate;
    private long sessionLength;
    private long sessionLengthCorrection;
    private int leniencyPercentage;
    ////////////////////////////////////////////////// Settings

    ////////////////////////////////////////////////// Data received from intent
    private String classId;
    private String subjectId;
    private String MenuType;

    private String serviceData;
    ////////////////////////////////////////////////// Data received from intent

    ////////////////////////////////////////////////// Timer and Poller
    private static Handler handler = new Handler();
    private Runnable scannerRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            scanForNetworksNow();
        }
    };




    ////////// Old
    private static Handler timerHandler = new Handler();
    ////////// Old

    ////////// Trial
    private Runnable timerRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            try
            {
                Thread.sleep(sessionLength);
                finishingTouch();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    };
    Thread timerThread = new Thread(timerRunnable);
    ////////// Trial

    ////////// Trial2
    private Long calStartTime;
    ////////// Trial2
    ////////////////////////////////////////////////// Timer and Poller

    ////////////////////////////////////////////////// Wifi and Stuff
    private WifiManager wifiManager;
    private WifiReceiver wifiReceiver;
    private static int prevWifiState;
    private boolean isRegistered = false;
    ////////////////////////////////////////////////// Wifi and Stuff

    ////////////////////////////////////////////////////////////////////// Data
    private int attendancePolls = 0;
    private List<ScanResult> wifiList;
    private ArrayList<String> refreshedList;
    ////////////////////////////////////////////////////////////////////// Data

    ////////////////////////////////////////////////////////////////////// Comms

    ////////////////////////////////////////////////////////////////////// Comms





    ////////////////////////////////////////////////////////////////////// Attendance Data
    private ArrayList<String> staticListExisting;

    private HashMap<String, StudentsInSession> sessionHashMap;
    private Calendar startTime;
    private Calendar endTime;

    private StringBuilder studentsPresentCSV;
    private StringBuilder studentsAbsentCSV;
    ////////////////////////////////////////////////////////////////////// Attendance Data
















    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        setRefs(intent);
        checkWifiState();
        getRegisteredStudentsList();
        formAttendanceDataContainers();
        handler.postDelayed(scannerRunnable, pollRate);
//        timerThread.run();
        calStartTime = Calendar.getInstance().getTimeInMillis();

        return Service.START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return mBinder;
    }

    public class MarkerBinder extends Binder
    {
        MarkerService getService()
        {
            return MarkerService.this;
        }
    }





    ////////////////////////////////////////////////////////////////////////////////////////// Wifi Utility
    private void checkWifiState()
    {
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        prevWifiState = wifiManager.getWifiState();
        if(!wifiManager.isWifiEnabled())
        {
            Toast.makeText(this, "Wifi not enabled! Enabling...", Toast.LENGTH_SHORT).show();
            wifiManager.setWifiEnabled(true);
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////// Wifi Utility






    ////////////////////////////////////////////////////////////////////////////////////////// Database
    private void getRegisteredStudentsList()
    {
        Cursor cursorStudentList;
        staticListExisting = new ArrayList<>();
        AttendanceSystemDBHelper helper = new AttendanceSystemDBHelper(getApplicationContext());

        //////////////////////////////////////// COLS
        String tableName = AttendanceSystemDBHelper.TABLE_0_NAME;

        String colEmail = AttendanceSystemDBHelper.TABLE_0_COL_7;
        String colRollNo = AttendanceSystemDBHelper.TABLE_0_COL_3;
        String colClassId = AttendanceSystemDBHelper.TABLE_0_COL_4;
        //////////////////////////////////////// COLS

        String[] projection = new String[]{colEmail, colRollNo};
        String selection = new String();
        String[] selectionArgs = new String[]{};

        if(MenuType.equals("CLASS"))
        {
            selection = String.format("%s = ?", colClassId);
            selectionArgs = new String[]{classId};
        }
        else
        {
            // selection = String.format("%s = ?", helper.)
            // selectionArgs = new String[]{classId};
        }

        cursorStudentList = helper.querySpecific(tableName, projection, selection, selectionArgs, null, null, null);

        for(int i=0;i<cursorStudentList.getCount();i++)
        {
            cursorStudentList.moveToNext();
            // Testing work going on here
            staticListExisting.add(cursorStudentList.getString(cursorStudentList.getColumnIndex(colRollNo))
                    + ":"
                    + cursorStudentList.getString(cursorStudentList.getColumnIndex(colEmail)));
        }

        cursorStudentList.close();
        helper.close();
    }
    ////////////////////////////////////////////////////////////////////////////////////////// Database














    ////////////////////////////////////////////////////////////////////////////////////////// Scanner (Modulate Later)
    private void scanForNetworksNow()
    {
        Long calCurrentTime = Calendar.getInstance().getTimeInMillis();
        System.out.println(calCurrentTime);
        System.out.println(calStartTime + sessionLength);
        System.out.println(sessionLength);
        System.out.println(calStartTime + sessionLength - calCurrentTime);


        if(calCurrentTime < calStartTime + sessionLength)
        {
            handler.removeCallbacks(scannerRunnable);
            wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            wifiReceiver = new WifiReceiver();
            registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
            isRegistered = true;

//            if(!isRegistered)
//            {
//                wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
//                wifiReceiver = new WifiReceiver();
//                registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
//                isRegistered = true;
//            }
            wifiManager.startScan();
            handler.postDelayed(scannerRunnable, pollRate);
        }
        else
        {
            handler.removeCallbacks(scannerRunnable);
            finishingTouch();
        }
    }

    private class WifiReceiver extends BroadcastReceiver
    {
//        private final String ssidPattern = "[a-zA-Z0-9]+@gmail.com";

        @Override
        public void onReceive(Context context, Intent intent)
        {
            System.out.println("////////////////////////////////////////////////// Scan Receiver Called");

            wifiList = wifiManager.getScanResults();
            ArrayList<String> staticList = new ArrayList<>();
            for(int i=0 ; i<wifiList.size() ; i++)
            {
                String temp = wifiList.get(i).SSID;
                //////////////////////////////////////// Some Serious Decryption Work Here
                String nitroTemp = temp;
                //////////////////////////////////////// Some Serious Decryption Work Here
//                if(nitroTemp.matches(ssidPattern))
//                {
//                    staticList.add(temp);
//                }

                if(sessionHashMap.containsKey(nitroTemp.split("@")[0] + "@gmail.com"))
                {
                    staticList.add(nitroTemp);
                }
            }

            System.out.println("////////////////////////////// wifilist = "+wifiList);
            System.out.println("////////////////////////////// networkCount = "+wifiList.size());
            System.out.println("////////////////////////////// staticList = "+staticList);
            System.out.println("////////////////////////////// staticListSSIDExisting = "+staticListExisting);

            refreshedList = new ArrayList<>(staticList);
            updatePolls();

            try
            {
                unregisterReceiver(this);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////// Scanner (Modulate Later)



















    ////////////////////////////////////////////////////////////////////////////////////////// Notif and Stuff
    private NotificationCompat.Builder builder;
    private NotificationManager manager;
    private int progressMax;
    private int progressNow = 0;


    private void showNotification()
    {
        int iconID = com.kvm.automaticattendancemarker.R.mipmap.ic_content_paste_white_48dp;
        progressMax = (int) sessionLength;

        builder = new NotificationCompat.Builder(getApplicationContext());
        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        builder.setContentTitle("Attendance Session Active")
                .setSmallIcon(iconID)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), iconID));
        notification = builder.build();
        notification.flags = Notification.FLAG_ONGOING_EVENT;
        manager.notify(13, notification);
    }

    private void updateNotification()
    {
        progressNow += pollRate;
        builder.setProgress(progressMax, progressNow, false);
        manager.notify(13, notification);
    }

    private void finishNotification()
    {
        builder = new NotificationCompat.Builder(getApplicationContext());
        builder.setProgress(0, 0, false);
        builder.setContentText("Session complete");
        notification = builder.build();
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        manager.notify(13, notification);
    }
    ////////////////////////////////////////////////////////////////////////////////////////// Notif and Stuff





//    Need better code here to optimize things
//    Things here are coded in testing perspective


    ////////////////////////////////////////////////////////////////////////////////////////// Attendance Data
    public class StudentsInSession
    {
        public String EmailSub;
        public String RollNo;
        public int PresentPolls;

        public StudentsInSession(String rollNo, String email)
        {
            this.RollNo = rollNo;
            this.EmailSub = email.split("@")[0];
            this.PresentPolls = 0;
        }
    }

    ////////////////////////////////////////////////////////////////////// Before session
    private void formAttendanceDataContainers()
    {
        int size = staticListExisting.size();

        System.out.println(staticListExisting.get(0));

        sessionHashMap = new HashMap<>(size);
        for(int i=0;i<size;i++)
        {
            String rollNo = staticListExisting.get(i).split(":")[0];
            String email = staticListExisting.get(i).split(":")[1];

            sessionHashMap.put(email, new StudentsInSession(rollNo, email));
        }
    }
    ////////////////////////////////////////////////////////////////////// Before session





    ////////////////////////////////////////////////////////////////////// During session
    private synchronized void updatePolls()
    {
        int size = refreshedList.size();
        System.out.println("/ / / / / "+refreshedList+" / / / / /");
        attendancePolls++;
        for(int i=0;i<size;i++)
        {
            StudentsInSession temp = sessionHashMap.get(refreshedList.get(i).split("@")[0] + "@gmail.com");
            temp.PresentPolls++;
        }

        System.out.println("//////////////////////////////////////////////////");
        for(int i=0;i<staticListExisting.size();i++)
        {
            StudentsInSession temp = sessionHashMap.get(staticListExisting.get(i).split(":")[1]);
            System.out.println(sessionHashMap);
            System.out.println(staticListExisting);
            System.out.println(temp.RollNo+": "+temp.PresentPolls+"/"+attendancePolls);
        }
        System.out.println("//////////////////////////////////////////////////");
    }
    ////////////////////////////////////////////////////////////////////// During session






    ////////////////////////////////////////////////////////////////////// After session
    private void produceCSV()
    {
        StudentsInSession tempStud;
        Float tempPollsPresent;
        Float tempPollsAllowed;

        for(String key : sessionHashMap.keySet())
        {
            tempStud = sessionHashMap.get(key);
            tempPollsPresent = (float) (tempStud.PresentPolls);
            tempPollsAllowed = (float) attendancePolls*leniencyPercentage/100f;
            if(tempPollsPresent > tempPollsAllowed)
            {
                studentsPresentCSV
                        .append(tempStud.RollNo)
                        .append(",");
            }
            else
            {
                studentsAbsentCSV
                        .append(tempStud.RollNo)
                        .append(",");
            }
        }

        int len1 = studentsPresentCSV.length();
        int len2 = studentsAbsentCSV.length();

        if(len1 != 0)
        {
            studentsPresentCSV.deleteCharAt(len1-1);
        }

        if(len2 != 0)
        {
            studentsAbsentCSV.deleteCharAt(len2-1);
        }
    }



    private void updateAttendanceDataDatabase(String finalPresentCSV, String finalAbsentCSV)
    {
        AttendanceSystemDBHelper helper = new AttendanceSystemDBHelper(contextForService);
        endTime = Calendar.getInstance();

        String attendanceTableName = AttendanceSystemDBHelper.TABLE_6_NAME;

        ContentValues values = new ContentValues();
        values.put(AttendanceSystemDBHelper.TABLE_6_COL_1, subjectId);
        values.put(AttendanceSystemDBHelper.TABLE_6_COL_3, MenuType);
        values.put(AttendanceSystemDBHelper.TABLE_6_COL_4, finalPresentCSV);
        values.put(AttendanceSystemDBHelper.TABLE_6_COL_7, finalAbsentCSV);
        values.put(AttendanceSystemDBHelper.TABLE_6_COL_5, startTime.getTimeInMillis());
        values.put(AttendanceSystemDBHelper.TABLE_6_COL_6, endTime.getTimeInMillis());


        if(MenuType.equals("CLASS"))
        {
            values.put(AttendanceSystemDBHelper.TABLE_6_COL_2, classId);
        }
        else if(MenuType.equals("PRACTICAL"))
        {
//            values.put(AttendanceSystemDBHelper.TABLE_6_COL_2, practicalId);
        }

        helper.insertSpecific(Uri.parse(""), attendanceTableName, values);


        helper.close();
    }
    ////////////////////////////////////////////////////////////////////// After session
    ////////////////////////////////////////////////////////////////////////////////////////// Attendance Data








    ////////////////////////////////////////////////////////////////////////////////////////// Service Complete
    private void finishingTouch()
    {
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ Finishing touch called!");
        handler.removeCallbacks(scannerRunnable);
        timerHandler.removeCallbacks(timerRunnable);
//        try
//        {
//            unregisterReceiver(wifiReceiver);
//        }
//        catch(Exception e)
//        {
//            e.printStackTrace();
//        }
        if(prevWifiState != WifiManager.WIFI_STATE_ENABLED && prevWifiState != WifiManager.WIFI_STATE_ENABLING)
        {
            wifiManager.setWifiEnabled(false);
        }
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ Updating database!");
//        finishNotification();

        produceCSV();

        updateAttendanceDataDatabase(studentsPresentCSV.toString(), studentsAbsentCSV.toString());

        this.stopSelf();
    }
    ////////////////////////////////////////////////////////////////////////////////////////// Service Complete












    private void setRefs(Intent intent)
    {
        contextForService = this;
        bundle = intent.getExtras();
        settings = PreferenceManager.getDefaultSharedPreferences(this);

        classId = bundle.getString(ProfessorMenu.EXTRA_CLASS_ID);
        subjectId = bundle.getString(ProfessorMenu.EXTRA_SUBJECT_ID);
        MenuType = bundle.getString(ProfessorMenu.EXTRA_SYSTEM_TYPE);

        serviceData = bundle.getString(ProfessorMenu.EXTRA_SERVICE_COMMAND, "NONE");

        studentsPresentCSV = new StringBuilder();
        studentsAbsentCSV = new StringBuilder();

//        broadcaster = LocalBroadcastManager.getInstance(this);

        if(MenuType.equals("CLASS")) {sessionLength = Long.valueOf(settings.getString("pref_lecture_length", "30000"));}
        else if(MenuType.equals("PRACTICAL")) {sessionLength = Long.valueOf(settings.getString("pref_practical_length", "30000"));}

        sessionLengthCorrection = 4000L;


        pollRate = Long.valueOf(settings.getString("pref_polling_rate", "5000"));

        leniencyPercentage = Integer.valueOf(settings.getString("pref_leniency_percentage", "50"));

        if(serviceData.equals("end_attendance"))
        {
            finishingTouch();
        }
        startTime = Calendar.getInstance();
    }
}