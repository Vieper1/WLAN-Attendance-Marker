package com.kvm.automaticattendancemarker.activities.result;

import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.kvm.automaticattendancemarker.ProfessorMenu;
import com.kvm.automaticattendancemarker.R;
import com.kvm.automaticattendancemarker.utilities.dbrel.AttendanceSystemDBHelper;
import com.kvm.automaticattendancemarker.utilities.grids.AttendanceLogGridAdapter;
import com.kvm.automaticattendancemarker.utilities.grids.ManageClassGridAdapter;

public class AttendanceResult extends AppCompatActivity
{
    private GridView gridView;
    private TextView txtGridViewAlt;
    private Cursor gridCursor;
    private String gridPresentCSV;
    private String gridAbsentCSV;
    private AttendanceLogGridAdapter gridAdapter;

    private AttendanceSystemDBHelper helper;

    private int totalStudentCount = 0;

    //////////////////////////////////////////////////////////////////////////////// Navigation Control
    private String MenuType;
    private String classbatchId;
    private String subjectId;
    private String logIdDirect;
    //////////////////////////////////////////////////////////////////////////////// Navigation Control

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_result);

        setRefs();
        getDataFromIntent();

        getAttendanceLogFromDB(); // 1

        totalStudentCount = getStudentCountFromDB(); // 2

        setGridProps();
        setGridVisibility();

        // Maintain Order

        gridAdapter = new AttendanceLogGridAdapter(this, totalStudentCount, gridPresentCSV, gridAbsentCSV);
        gridView.setAdapter(gridAdapter);

        final ScrollView tempRef = (ScrollView) findViewById(R.id.activity_attendance_result);
        tempRef.post(new Runnable()
        {
            @Override
            public void run()
            {
                tempRef.scrollTo(0, tempRef.getTop());
            }
        });
    }









    //////////////////////////////////////////////////////////////////////////////// Navigation Control
    private void getAttendanceLogFromDB()
    {
        helper = new AttendanceSystemDBHelper(this);

        //////////////////////////////////////// COLS
        String tableName = AttendanceSystemDBHelper.TABLE_6_NAME;

        String colPresentCSV = AttendanceSystemDBHelper.TABLE_6_COL_4;
        String colAbsentCSV = AttendanceSystemDBHelper.TABLE_6_COL_7;
        String colTimeStart = AttendanceSystemDBHelper.TABLE_6_COL_5;
        String colTimeEnd = AttendanceSystemDBHelper.TABLE_6_COL_6;

        String colSubId = AttendanceSystemDBHelper.TABLE_6_COL_1;
        String colType = AttendanceSystemDBHelper.TABLE_6_COL_3;
        String colClassBatchId = AttendanceSystemDBHelper.TABLE_6_COL_2;

        String colId = AttendanceSystemDBHelper.TABLE_6_COL_0;
        //////////////////////////////////////// COLS



        String[] projection = new String[]{colPresentCSV, colAbsentCSV, colTimeStart, colTimeEnd};
        String selection;
        String[] selectionArgs;


        selection = String.format("%s = ?", colId);
        selectionArgs = new String[]{logIdDirect};


        gridCursor = helper.querySpecific(tableName, projection, selection, selectionArgs, null, null, null);

        try
        {
            gridCursor.moveToFirst();
            gridPresentCSV = gridCursor.getString(gridCursor.getColumnIndex(colPresentCSV));
            gridAbsentCSV = gridCursor.getString(gridCursor.getColumnIndex(colAbsentCSV));
        }
        catch(Exception e)
        {
            //////////////////////////////////////// Failed to fetch attendance log
            gridPresentCSV = "";
            gridAbsentCSV = "";
        }
    }



    private int getStudentCountFromDB()
    {
        //////////////////////////////////////// COLS
        String tableName = AttendanceSystemDBHelper.TABLE_1_NAME;

        String colClassId = AttendanceSystemDBHelper.TABLE_1_COL_0;
        String colLastRollNo = AttendanceSystemDBHelper.TABLE_1_COL_3;
        //////////////////////////////////////// COLS

        String[] projection = new String[]{colLastRollNo};
        String selection;
        String[] selectionArgs;


        selection = String.format("%s = ?", colClassId);
        selectionArgs = new String[]{classbatchId};

        int tempCount = 0;
        Cursor tempCursor = helper.querySpecific(tableName, projection, selection, selectionArgs, null, null, colLastRollNo);

        try
        {
            tempCursor.moveToFirst();
            tempCount = Integer.parseInt(tempCursor.getString(tempCursor.getColumnIndex(colLastRollNo)));
            return tempCount;
        }
        catch (Exception e)
        {
            return tempCount;
        }
    }
    //////////////////////////////////////////////////////////////////////////////// Navigation Control






    //////////////////////////////////////////////////////////////////////////////// Grid
    private void setGridProps()
    {
        final float CORRECTION_FACTOR = 39.45f;

        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        int temp = totalStudentCount;

        int height = (int) (CORRECTION_FACTOR * 1.0f * (totalStudentCount / 5) + CORRECTION_FACTOR);
        if(totalStudentCount % 5 != 0)
        {
            height += CORRECTION_FACTOR;
        }
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, height, getResources().getDisplayMetrics());
        params.height = px;
        gridView.setLayoutParams(params);
    }

    private void setGridVisibility()
    {
        if(totalStudentCount > 0)
        {
            txtGridViewAlt.setVisibility(View.GONE);
            gridView.setVisibility(View.VISIBLE);
        }
    }
    //////////////////////////////////////////////////////////////////////////////// Grid

    //////////////////////////////////////////////////////////////////////////////// Action bar
    private void setTitle()
    {
        try
        {
            new TitleModder().execute();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    private class TitleModder extends AsyncTask<Void, Void, Void>
    {
        private String titleData = "";

        @Override
        protected Void doInBackground(Void... voids)
        {
            //////////////////////////////////////// COLS
            String tableName = AttendanceSystemDBHelper.TABLE_5_NAME;

            String colSubjectId = AttendanceSystemDBHelper.TABLE_5_COL_0;
            String colSubjectName = AttendanceSystemDBHelper.TABLE_5_COL_1;
            //////////////////////////////////////// COLS

            String[] projection = new String[]{colSubjectName};
            String selection;
            String[] selectionArgs;


            selection = String.format("%s = ?", colSubjectId);
            selectionArgs = new String[]{subjectId};

            Cursor tempCursor = helper.querySpecific(tableName, projection, selection, selectionArgs, null, null, null);

            try
            {
                tempCursor.moveToFirst();
                titleData += tempCursor.getString(tempCursor.getColumnIndex(colSubjectName));
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }


            //////////////////////////////////////// COLS
            tableName = AttendanceSystemDBHelper.TABLE_6_NAME;


            //////////////////////////////////////// COLS


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            try
            {
                getSupportActionBar().setTitle(titleData);
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    //////////////////////////////////////////////////////////////////////////////// Action bar


    private void getDataFromIntent()
    {
        Bundle tempBundle = getIntent().getExtras();
        MenuType = tempBundle.getString(ProfessorMenu.EXTRA_SYSTEM_TYPE);
        subjectId = tempBundle.getString(ProfessorMenu.EXTRA_SUBJECT_ID);
        classbatchId = tempBundle.getString(ProfessorMenu.EXTRA_CLASS_ID);
        logIdDirect = tempBundle.getString(ProfessorMenu.EXTRA_SESSION_ID);
    }

    @Override
    protected void onPause() {
        helper.close();
        super.onPause();
    }

    private void setRefs()
    {
        gridView = (GridView) findViewById(R.id.attendance_result_gridview);
        txtGridViewAlt = (TextView) findViewById(R.id.gridview_alternate);
    }
}
