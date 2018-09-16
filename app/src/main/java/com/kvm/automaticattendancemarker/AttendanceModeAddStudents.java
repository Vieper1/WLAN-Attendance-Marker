package com.kvm.automaticattendancemarker;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kvm.automaticattendancemarker.utilities.dbrel.AttendanceSystemDBHelper;

import java.util.ArrayList;
import java.util.List;

public class AttendanceModeAddStudents extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener
{
    //////////////////////////////////////////////////////////////////////////////// Categories
    private static final String EXTRA_SYSTEM_TYPE = "system_type";
    private static final String EXTRA_SYSTEM_TYPE1_2 = "ext_type";

    private static final String EXTRA_CLASS_ID = "class_id";
    private static final String EXTRA_SUBJECT_ID = "subject_id";

    private String MenuType;
    private String classId;
    private String subjectId;
    //////////////////////////////////////////////////////////////////////////////// Categories

    private boolean isRegistered = false;


    private static Uri uri;
    ListView scanResultListView;

    //////////////////////////////////////// Temp
    private WifiReceiver wifiReceiver;
    private List<ScanResult> wifiList;
    private ArrayList<String> staticList;










    private ArrayList<CustomAdapterData> staticListMain;
    private CustomAdapter staticAdapter;
    private ArrayList<String> staticListSSIDExisting;
    private Thread getCursorReady;









    private final long scanFrequency = 1000;
    private final String ssidPattern = "....:[0-9]+:[0-9]+:[a-zA-Z0-9]+:[a-zA-Z0-9]+";
    private TextView n_networks_txtView;
    //////////////////////////////////////// Temp


    //////////////////////////////////////// New Alternate
    private WifiManager wifiManager;
    private static int prevWifiState;
    //////////////////////////////////////// New Alternate


















    ////////////////////////////////////////////////// Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_attendance_mode_add_students, menu);
        return true;
    }

    public void onRefresh(MenuItem mi)
    {
        scanForNetworksNow();
    }
    ////////////////////////////////////////////////// Menu



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_mode_add_students);

        toAgencyFB();
        setRefs();
        checkAppPermissions();
        enableWifi();
        getExistingFromDB();


        staticAdapter = new CustomAdapter(AttendanceModeAddStudents.this, staticListMain);
        scanResultListView.setAdapter(staticAdapter);
    }




















    //////////////////////////////////////////////////////////////////////////////// Wifi Receiver
    Handler handler;
    private final Runnable scannerRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            scanForNetworksNow();
        }
    };

    public void scanForNetworksNow()
    {
        handler.removeCallbacks(scannerRunnable);
        if(!isRegistered)
        {
            wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
            wifiReceiver = new WifiReceiver();
            registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
            isRegistered = true;
        }
        wifiManager.startScan();
        handler.postDelayed(scannerRunnable, scanFrequency);
    }

    private class WifiReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            System.out.println("////////////////////////////////////////////////// Scan Receiver Called");

            try
            {
                getCursorReady.join();

            }
            catch(InterruptedException e)
            {

            }

            wifiList = wifiManager.getScanResults();

            System.out.println("////////////////////////////// n(Wifi Networks Found) = "+wifiList.size());
            n_networks_txtView = (TextView) findViewById(R.id.professor_attendance_mode_add_students_n_networks);
            n_networks_txtView.setText(String.valueOf(wifiList.size()));

            for(int i=0 ; i<wifiList.size() ; i++)
            {
                String temp = wifiList.get(i).SSID;
                System.out.println("////////////////////////////// "+temp+" //////////////////////////////");

                if(temp.matches(ssidPattern))
                {
                    if(!staticList.contains(temp))
                    {
                        if(!staticListSSIDExisting.contains(temp))
                        {
                            staticList.add(temp);
                            staticListMain.add(new CustomAdapterData(temp));
                        }

                    }
                }
            }

            System.out.println("////////////////////////////// "+staticList);
            System.out.println("////////////////////////////// "+staticListSSIDExisting);

            staticAdapter.notifyDataSetChanged();
            scanResultListView.setAdapter(staticAdapter);

//            unregisterReceiver(this);
        }
    }
    //////////////////////////////////////////////////////////////////////////////// Wifi Receiver




























    //////////////////////////////////////////////////////////////////////////////// Custom Check List
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
    {
        int position = scanResultListView.getPositionForView(buttonView);
        if(position != ListView.INVALID_POSITION)
        {
            CustomAdapterData listContent = staticListMain.get(position);
            listContent.setChecked(isChecked);

            Toast.makeText(this, "Clicked on "+listContent.displayName, Toast.LENGTH_SHORT).show();

            StringBuilder sBuilder = new StringBuilder();
            for(int i=0;i<staticListMain.size();i++)
            {
                sBuilder.append(staticListMain.get(i).checked + " " + staticListMain.get(i).displayName + "\n");
            }
            quickAlertMessage("Show List", ""+sBuilder.toString());
        }
    }

    private class CustomAdapterData
    {
        public String displayName;
        public String SSID;
        public boolean checked;

        public CustomAdapterData(String SSID)
        {
            this.SSID = SSID;
            this.checked = false;

            String[] splitter = SSID.split(":");
            this.displayName = splitter[2] + " " + splitter[3] + " " + splitter[4];
        }

        public void setChecked(boolean checked)
        {
            this.checked = checked;
        }
    }

    private class CustomAdapter extends ArrayAdapter<CustomAdapterData>
    {
        private ArrayList<CustomAdapterData> dataList;

        CustomAdapter(Context context, ArrayList<CustomAdapterData> list)
        {
            super(context, R.layout.add_students_lv_layout, list);
            dataList = list;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            View v = convertView;
            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.add_students_lv_layout, parent, false);


            TextView displayName = (TextView) v.findViewById(R.id.add_students_lv_main_text);
            TextView SSID = (TextView) v.findViewById(R.id.add_students_lv_ssid_text);
            CheckBox checkbox = (CheckBox) v.findViewById(R.id.add_students_lv_checkbox);
            checkbox.setOnCheckedChangeListener(AttendanceModeAddStudents.this);

            displayName.setText(dataList.get(position).displayName);
            SSID.setText(dataList.get(position).SSID);
            checkbox.setChecked(dataList.get(position).checked);

            return v;

        }
    }
    //////////////////////////////////////////////////////////////////////////////// Custom Check List








    //////////////////////////////////////////////////////////////////////////////// Button Listeners
    private View.OnClickListener onFABClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            if(v.getId() == R.id.professor_attendance_mode_add_students_fab_add)
            {
                AttendanceSystemDBHelper helper = new AttendanceSystemDBHelper(AttendanceModeAddStudents.this);
                String tableName = helper.TABLE_0_NAME;

                ArrayList<String> submitter = new ArrayList<>();
                for(int i=0;i<staticListMain.size();i++)
                {
                    CustomAdapterData temp = staticListMain.get(i);
                    if(temp.checked == true)
                        submitter.add(temp.SSID);
                }

                for(int i=0;i<submitter.size();i++)
                {
                    ContentValues vals = new ContentValues();
                    String[] splitter = submitter.get(i).split(":");
                    // 1911:2014140033:37:Vishal:Naidu
                    // 0    1          2  3      4

                    vals.put(helper.TABLE_0_COL_1, splitter[3]+" "+splitter[4]);
                    vals.put(helper.TABLE_0_COL_2, splitter[1]);
                    vals.put(helper.TABLE_0_COL_3, splitter[2]);
                    vals.put(helper.TABLE_0_COL_6, submitter.get(i));

                    if(MenuType.equals("CLASS"))
                    {
                        vals.put(helper.TABLE_0_COL_4, classId);
                    }
                    else if(MenuType.equals("PRACTICAL"))
                    {
                        //vals.put(helper.TABLE_0_COL_5, practicalId);  Mod for Practical Mode
                    }

                    helper.insertSpecific(uri, tableName, vals);
                }

                Toast.makeText(AttendanceModeAddStudents.this, "Selected students added to the group", Toast.LENGTH_SHORT).show();
                helper.close();
                backToAttendanceMenu();
            }
        }
    };
    //////////////////////////////////////////////////////////////////////////////// Button Listeners





    //////////////////////////////////////////////////////////////////////////////// Utils
    private void enableWifi()
    {
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        prevWifiState = wifiManager.getWifiState();
        if(!wifiManager.isWifiEnabled())
        {
            Toast.makeText(AttendanceModeAddStudents.this, "Wifi not enabled! Enabling...", Toast.LENGTH_SHORT).show();
            wifiManager.setWifiEnabled(true);
        }
    }

    private void returnToPrevWifi()
    {
        if(prevWifiState != WifiManager.WIFI_STATE_ENABLED && prevWifiState != WifiManager.WIFI_STATE_ENABLING)
        {
            wifiManager.setWifiEnabled(false);
        }
    }

    private void checkAppPermissions()
    {
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            quickAlertMessage("Update Permissions", "The app needs Coarse Location Update Permission\nGrant it to avail this feature");
            ActivityCompat.requestPermissions(this, new String[] {android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1080);
        }
    }

    private void setRefs()
    {
        MenuType = getIntent().getStringExtra(EXTRA_SYSTEM_TYPE);
        classId = getIntent().getStringExtra(EXTRA_CLASS_ID);
        subjectId = getIntent().getStringExtra(EXTRA_SUBJECT_ID);

        scanResultListView = (ListView) findViewById(R.id.professor_attendance_mode_add_students_available_ids);

        uri = Uri.parse(getResources().getString(R.string.studbase_content_provider_uri));


        FloatingActionButton fab_add = (FloatingActionButton) findViewById(R.id.professor_attendance_mode_add_students_fab_add);
        fab_add.setOnClickListener(onFABClickListener);

        staticList = new ArrayList<>();
        staticListMain = new ArrayList<>();
    }

    private void getExistingFromDB()
    {
        getCursorReady = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                System.out.println("//////////");
                Cursor cursorStudentList;
                staticListSSIDExisting = new ArrayList<>();
                AttendanceSystemDBHelper helper = new AttendanceSystemDBHelper(getApplicationContext());
                String[] projection = new String[]{helper.TABLE_0_COL_6};
                String selection = new String("");
                String[] selectionArgs = new String[]{};
                if(MenuType.equals("CLASS"))
                {
                    selection = String.format("%s = ?", helper.TABLE_0_COL_4);
                    selectionArgs = new String[]{classId};
                }
                else
                {
                    // selection = String.format("%s = ?", helper.)
                    // selectionArgs = new String[]{classId};
                }
                cursorStudentList = helper.querySpecific(helper.TABLE_0_NAME, projection, selection, selectionArgs, null, null, null);

                for(int i=0;i<cursorStudentList.getCount();i++)
                {
                    cursorStudentList.moveToNext();
                    staticListSSIDExisting.add(cursorStudentList.getString(cursorStudentList.getColumnIndex(helper.TABLE_0_COL_6)));
                }
                System.out.println(staticListSSIDExisting);
                System.out.println("//////////");
                helper.close();
            }
        });
        getCursorReady.start();
    }
    //////////////////////////////////////////////////////////////////////////////// Utils















    @Override
    protected void onPause()
    {
        handler.removeCallbacks(scannerRunnable);
        try
        {
            unregisterReceiver(wifiReceiver);
        }
        catch(Exception e)
        {
            // Viper
        }
        super.onPause();
    }



    @Override
    protected void onResume()
    {
        handler = new Handler();
        handler.postDelayed(scannerRunnable, scanFrequency);
        super.onResume();
    }

    @Override
    protected void onStop()
    {
        handler.removeCallbacks(scannerRunnable);
        returnToPrevWifi();
        try
        {
            unregisterReceiver(wifiReceiver);
        }
        catch(Exception e)
        {
            // Already Unregistered
        }
        super.onStop();
    }














    private void backToAttendanceMenu()
    {
        Intent backToMenu = new Intent(AttendanceModeAddStudents.this, AttendanceModeProfessor.class);
        backToMenu.putExtra(EXTRA_SYSTEM_TYPE, MenuType);
        backToMenu.putExtra(EXTRA_SUBJECT_ID, subjectId);
        backToMenu.putExtra(EXTRA_CLASS_ID, classId);
        startActivity(backToMenu);
    }

    @Override
    public void onBackPressed()
    {
//        backToAttendanceMenu();
        super.onBackPressed();
    }

    private void toAgencyFB()
    {
        TextView lbl1 = (TextView) findViewById(R.id.professor_attendance_mode_add_students_header);
        TextView lbl2 = (TextView) findViewById(R.id.professor_attendance_mode_add_students_alert);



        Typeface agencyfb = Typeface.createFromAsset(getAssets(), "agency_fb.ttf");
        lbl1.setTypeface(agencyfb);
        lbl2.setTypeface(agencyfb);
    }

    //////////////////////////////////////////////////////////////////////////////// Can Be Used
    private void quickToaster(String message)
    {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void quickAlertMessage(String title, String message)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(AttendanceModeAddStudents.this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    //////////////////////////////////////////////////////////////////////////////// Can Be Used
}
