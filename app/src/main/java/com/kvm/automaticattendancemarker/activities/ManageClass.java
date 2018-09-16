package com.kvm.automaticattendancemarker.activities;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.NumberPicker;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.kvm.automaticattendancemarker.ProfessorMenu;
import com.kvm.automaticattendancemarker.R;
import com.kvm.automaticattendancemarker.activities.navigator.ClassToNav;
import com.kvm.automaticattendancemarker.activities.result.StudentInfo;
import com.kvm.automaticattendancemarker.utilities.crypt.AESCrypt;
import com.kvm.automaticattendancemarker.utilities.dbrel.AttendanceSystemDBHelper;
import com.kvm.automaticattendancemarker.utilities.grids.ManageClassGridAdapter;


import org.json.JSONObject;

// {"displayname":"Vishal Naidu","email":"naiduvishal13@gmail.com","photourl":""}

public class ManageClass extends AppCompatActivity
{
    private GridView gridView;
    private TextView gridViewAlt;
    private Cursor gridCursor;
    private String existsInClassCSV;
    private int maxRollNo;
    private int lastClickedRollNo = 0;
    private ManageClassGridAdapter gridAdapter;

    private AttendanceSystemDBHelper helper;

    //////////////////////////////////////////////////////////////////////////////// Scan
    private IntentIntegrator qrScan;
    //////////////////////////////////////////////////////////////////////////////// Scan

    //////////////////////////////////////////////////////////////////////////////// Navigation Control
    private String MenuType;
    private String classbatchId;
    private String subjectId;
    //////////////////////////////////////////////////////////////////////////////// Navigation Control




    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_class);

        setRefs();
        getDataFromIntent();

        refreshGrid();

        final ScrollView tempRef = (ScrollView) findViewById(R.id.activity_manage_class);
        tempRef.post(new Runnable()
        {
            @Override
            public void run()
            {
                tempRef.scrollTo(0, tempRef.getTop());
            }
        });
    }
















    //////////////////////////////////////////////////////////////////////////////// DB
    private int getStudentCountFromDBAndSetCSV()
    {
        //      CSV
        //////////////////////////////////////// COLS
        String tableName = AttendanceSystemDBHelper.TABLE_0_NAME;

        String colClassId = AttendanceSystemDBHelper.TABLE_0_COL_4;
        String colRollNo = AttendanceSystemDBHelper.TABLE_0_COL_3;
        //////////////////////////////////////// COLS

        String[] projection = new String[]{colRollNo};
        String selection;
        String[] selectionArgs;

        selection = String.format("%s = ?", colClassId);
        selectionArgs = new String[]{classbatchId};

        Cursor csvCursor = helper.querySpecific(tableName, projection, selection, selectionArgs, null, null, null);

        System.out.println("csvCursor.getCount = "+csvCursor.getCount());

        existsInClassCSV = "";
        csvCursor.moveToFirst();
        for(int i=0;i<csvCursor.getCount();i++)
        {
            existsInClassCSV += csvCursor.getString(csvCursor.getColumnIndex(colRollNo)) + ",";
            csvCursor.moveToNext();
        }
        System.out.println("CSV = "+existsInClassCSV);
        if(existsInClassCSV.length() != 0)
        {
            existsInClassCSV = existsInClassCSV.substring(0, existsInClassCSV.length()-1);
        }
        System.out.println("CSV = "+existsInClassCSV);
        csvCursor.close();
        //      CSV


        //      Last Roll Number
        //////////////////////////////////////// COLS
        String tableName2 = AttendanceSystemDBHelper.TABLE_1_NAME;

        String colClassId2 = AttendanceSystemDBHelper.TABLE_1_COL_0;
        String colLastRollNo = AttendanceSystemDBHelper.TABLE_1_COL_3;
        //////////////////////////////////////// COLS
        projection = new String[]{colLastRollNo};
        selection = String.format("%s = ?", colClassId2);
        selectionArgs = new String[]{classbatchId};



        int tempCount = 0;
        Cursor tempCursor = helper.querySpecific(tableName2, projection, selection, selectionArgs, null, null, colLastRollNo+" DESC");
        try
        {
            tempCursor.moveToFirst();
            tempCount = Integer.parseInt(tempCursor.getString(tempCursor.getColumnIndex(colLastRollNo)));
            System.out.println("tempCount = "+tempCount);
            return tempCount;
        }
        catch (Exception e)
        {
            System.out.println("Exceptioned tempCount = "+tempCount);
            e.printStackTrace();
            return tempCount;
        }
        finally
        {
            tempCursor.close();
        }
        //      Last Roll Number
    }

    private class updateDBNewMax extends AsyncTask<String, Void, Void>
    {
        String newMax;
        @Override
        protected Void doInBackground(String... strings)
        {
            newMax = strings[0];
            ContentValues contentValues = new ContentValues();
            contentValues.put(AttendanceSystemDBHelper.TABLE_1_COL_3, newMax);
            helper.updateClassMainUsingID(contentValues, classbatchId);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            Toast.makeText(ManageClass.this, "AttendanceBase Updated!", Toast.LENGTH_SHORT).show();
            refreshGrid();
        }
    }
    //////////////////////////////////////////////////////////////////////////////// DB






























    //////////////////////////////////////////////////////////////////////////////// Grid
    private void refreshGrid()
    {
        maxRollNo = getStudentCountFromDBAndSetCSV();
        setGridVisibility(maxRollNo);
        setGridProps();
    }

    private void setGridProps()
    {
        final float CORRECTION_FACTOR = 39.45f;

        gridAdapter = new ManageClassGridAdapter(this, maxRollNo, existsInClassCSV);
        gridView.setAdapter(gridAdapter);
        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        int temp = maxRollNo;
        int height = (int) (CORRECTION_FACTOR * 1.0f * (temp / 5) + CORRECTION_FACTOR);

        if(temp % 5 != 0)
        {
            height += CORRECTION_FACTOR;
        }
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, height, getResources().getDisplayMetrics());
        params.height = px;
        gridView.setLayoutParams(params);

        gridView.setOnItemClickListener(onGridItemClickListener);
    }

    private void setGridVisibility(int temp)
    {
        if(temp != 0)
        {
            gridView.setVisibility(View.VISIBLE);
            gridViewAlt.setVisibility(View.GONE);
        }
    }

    public static final int MANAGE_CLASS_REQ_CODE_GET_STATS = 1231;

    AdapterView.OnItemClickListener onGridItemClickListener = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
        {
            boolean isExisting = gridAdapter.getItemExisting((int) l);
            lastClickedRollNo = ((int) l);
            SharedPreferences.Editor tempPrefEditor = getSharedPreferences("TempPref", MODE_PRIVATE).edit();
            tempPrefEditor.putInt("lastClickedRollNo", lastClickedRollNo);
            tempPrefEditor.apply();

            if(isExisting)
            {
                // Display Stats Page
                Intent intent = new Intent(ManageClass.this, StudentInfo.class);
                intent.putExtras(ManageClass.this.getIntent().getExtras());
                intent.putExtra(ProfessorMenu.EXTRA_ROLL_NO, String.valueOf(lastClickedRollNo));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ManageClass.this.startActivity(intent);
            }
            else
            {
                Toast.makeText(ManageClass.this, ""+lastClickedRollNo, Toast.LENGTH_SHORT).show();
                qrScan.setBeepEnabled(false);
                qrScan.addExtra("LastClickedRollNumber", lastClickedRollNo);
                qrScan.initiateScan();
            }
        }
    };
    //////////////////////////////////////////////////////////////////////////////// Grid




















    //////////////////////////////////////////////////////////////////////////////// Back from Scan
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if(result != null)
        {
            if(result.getContents() != null)
            {
                try
                {
                    System.out.println("//////////////////////////////////////////////////");
                    String nitroNation = "ViperGTSR2015";
                    System.out.println(result.getContents());
                    String nitroQR = AESCrypt.decrypt(nitroNation, result.getContents());
                    JSONObject obj = new JSONObject(nitroQR);

                    new updateDBusingScannedData().execute(obj.getString("displayname"), obj.getString("email"), obj.getString("photourl"));
                    System.out.println("//////////////////////////////////////////////////");
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                    Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                }
            }
            else
            {
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private class updateDBusingScannedData extends AsyncTask<String, Void, Void>
    {
        private String data_emailid;
        private String data_photourl;
        private String data_name;
        private AttendanceSystemDBHelper tempHelper;

        private String toastMessage;

        @Override
        protected Void doInBackground(String... strings)
        {
            data_name = strings[0];
            data_emailid = strings[1];
            data_photourl = strings[2];

            tempHelper = new AttendanceSystemDBHelper(ManageClass.this);

            if(!dbCheckForInstance())
            {
                dbUpdate();
                toastMessage = "AttendanceBase Updated!";
            }
            else
            {
                // Open Existing student info page
                toastMessage = "Already exists in database";
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            if(!toastMessage.equals("NONE"))
            {
                Toast.makeText(ManageClass.this, toastMessage, Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(ManageClass.this, toastMessage, Toast.LENGTH_SHORT).show();
            }
            refreshGrid();
            tempHelper.close();
        }












        private void dbUpdate()
        {
            //////////////////////////////////////// COLS
            String tableName = AttendanceSystemDBHelper.TABLE_0_NAME;

            String colRollNo = AttendanceSystemDBHelper.TABLE_0_COL_3;
            String colName = AttendanceSystemDBHelper.TABLE_0_COL_1;
            String colClassId = AttendanceSystemDBHelper.TABLE_0_COL_4;
            String colEmailId = AttendanceSystemDBHelper.TABLE_0_COL_7;
            String colPhotoUrl = AttendanceSystemDBHelper.TABLE_0_COL_8;
            //////////////////////////////////////// COLS

            SharedPreferences tempPref = getSharedPreferences("TempPref", MODE_PRIVATE);
            lastClickedRollNo = tempPref.getInt("lastClickedRollNo", -1);

            ContentValues contentValues = new ContentValues();
            contentValues.put(colRollNo, lastClickedRollNo);
            contentValues.put(colName, data_name);
            contentValues.put(colClassId, classbatchId);
            contentValues.put(colEmailId, data_emailid);
            contentValues.put(colPhotoUrl, data_photourl);

            try
            {
                tempHelper.insertSpecific(Uri.parse(""), tableName, contentValues);
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }

        private boolean dbCheckForInstance()
        {
            //////////////////////////////////////// COLS
            String tableName = AttendanceSystemDBHelper.TABLE_0_NAME;

            String colRollNo = AttendanceSystemDBHelper.TABLE_0_COL_3;
            String colName = AttendanceSystemDBHelper.TABLE_0_COL_1;
            String colClassId = AttendanceSystemDBHelper.TABLE_0_COL_4;
            String colEmailId = AttendanceSystemDBHelper.TABLE_0_COL_7;
            String colPhotoUrl = AttendanceSystemDBHelper.TABLE_0_COL_8;
            //////////////////////////////////////// COLS

            String selection = String.format("%s = ? AND %s = ?", colEmailId, colClassId);
            String[] selectionArgs = new String[]{data_emailid, classbatchId};

            Cursor tempCursor;
            try
            {
                tempCursor = tempHelper.querySpecific(tableName, null, selection, selectionArgs, null, null, null);
                if(tempCursor.getCount() != 0)
                {
                    return true;
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
                return false;
            }
            return false;
        }
    }
    //////////////////////////////////////////////////////////////////////////////// Back from Scan






    //////////////////////////////////////////////////////////////////////////////// Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_manage_class, menu);
        return true;
    }

    public void onOpenSetStrength(MenuItem mi)
    {
        showMaxNumberSetter();
    }

    private void showMaxNumberSetter()
    {
        final Dialog dialog = new Dialog(ManageClass.this);
        dialog.setTitle("Set last roll number");
        dialog.setContentView(R.layout.dialog_max_picker);

        Button ok_button = (Button) dialog.findViewById(R.id.ok_button);
        final NumberPicker picker = (NumberPicker) dialog.findViewById(R.id.number_picker);
        picker.setMinValue(maxRollNo);
        picker.setMaxValue(150);
        picker.setWrapSelectorWheel(false);

        ok_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(picker.getValue() != maxRollNo)
                {
                    new updateDBNewMax().execute(String.valueOf(picker.getValue()));
//                    setNewMaxFromPicker(picker.getValue());
                }
                dialog.dismiss();
            }
        });


        dialog.show();
    }
    //////////////////////////////////////////////////////////////////////////////// Menu





















    private void setRefs()
    {
        gridView = (GridView) findViewById(R.id.gridview);
        gridViewAlt = (TextView) findViewById(R.id.gridview_alternate);
        qrScan = new IntentIntegrator(this);

        helper = new AttendanceSystemDBHelper(this);
    }

    private void getDataFromIntent()
    {
        try
        {
            Bundle tempBundle = getIntent().getExtras();
            MenuType = tempBundle.getString(ProfessorMenu.EXTRA_SYSTEM_TYPE);
            subjectId = tempBundle.getString(ProfessorMenu.EXTRA_SUBJECT_ID);
            classbatchId = tempBundle.getString(ProfessorMenu.EXTRA_CLASS_ID);
        }
        catch(NullPointerException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed()
    {
        Intent backToMenu = new Intent(ManageClass.this, ClassToNav.class);
        backToMenu.putExtras(getIntent().getExtras());
        startActivity(backToMenu);
    }

    @Override
    protected void onResume()
    {
        helper = new AttendanceSystemDBHelper(this);
        super.onResume();
    }

    @Override
    protected void onStop()
    {
        helper.close();
        super.onStop();
    }
}