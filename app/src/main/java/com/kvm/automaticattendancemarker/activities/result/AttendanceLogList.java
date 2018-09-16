package com.kvm.automaticattendancemarker.activities.result;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.kvm.automaticattendancemarker.ProfessorMenu;
import com.kvm.automaticattendancemarker.R;
import com.kvm.automaticattendancemarker.utilities.dbrel.AttendanceSystemDBHelper;
import com.kvm.automaticattendancemarker.utilities.lists.listrel.AttendanceLogListAdapter;

public class AttendanceLogList extends AppCompatActivity
{
    private ListView listView;
    private Cursor listCursor;
    private AttendanceLogListAdapter listAdapter;

    private AttendanceSystemDBHelper helper;

    //////////////////////////////////////////////////////////////////////////////// Navigation Control
    private String MenuType;
    private String classbatchId;
    private String subjectId;
    //////////////////////////////////////////////////////////////////////////////// Navigation Control

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_log_list);

        getRefs();
        getDataFromIntent();

        getAttendanceListFromDB();

        listAdapter = new AttendanceLogListAdapter(this, listCursor);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(onListItemClickListener);
    }




    //////////////////////////////////////////////////////////////////////////////// DB
    private void getAttendanceListFromDB()
    {
        helper = new AttendanceSystemDBHelper(this);

        //////////////////////////////////////// COLS
        String colId = AttendanceSystemDBHelper.TABLE_6_COL_0;

        String colCSV = AttendanceSystemDBHelper.TABLE_6_COL_4;
        String colTimeStart = AttendanceSystemDBHelper.TABLE_6_COL_5;

        String colSubId = AttendanceSystemDBHelper.TABLE_6_COL_1;
        String colType = AttendanceSystemDBHelper.TABLE_6_COL_3;
        String colClassBatchId = AttendanceSystemDBHelper.TABLE_6_COL_2;
        //////////////////////////////////////// COLS

        String[] projection = new String[]{colId, colTimeStart, colCSV};
        String selection;
        String[] selectionArgs = new String[]{};


        selection = String.format("%s = ? AND %s = ? AND %s = ?", colSubId, colType, colClassBatchId);
        selectionArgs = new String[]{subjectId, MenuType, classbatchId};


        listCursor = helper.querySpecific(AttendanceSystemDBHelper.TABLE_6_NAME, projection, selection, selectionArgs, null, null, colTimeStart+" DESC");
    }
    //////////////////////////////////////////////////////////////////////////////// DB



    AdapterView.OnItemClickListener onListItemClickListener = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
        {
            Intent intent = new Intent(AttendanceLogList.this, AttendanceResult.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(ProfessorMenu.EXTRA_CLASS_ID, classbatchId);
            intent.putExtra(ProfessorMenu.EXTRA_SUBJECT_ID, subjectId);
            intent.putExtra(ProfessorMenu.EXTRA_SYSTEM_TYPE, MenuType);
            intent.putExtra(ProfessorMenu.EXTRA_SESSION_ID, String.valueOf(listAdapter.getItemId(i)));
            startActivity(intent);
        }
    };














    private void getDataFromIntent()
    {
        Bundle tempBundle = getIntent().getExtras();
        MenuType = tempBundle.getString(ProfessorMenu.EXTRA_SYSTEM_TYPE);
        subjectId = tempBundle.getString(ProfessorMenu.EXTRA_SUBJECT_ID);
        classbatchId = tempBundle.getString(ProfessorMenu.EXTRA_CLASS_ID);
    }

    private void getRefs()
    {
        listView = (ListView) findViewById(R.id.attendance_log_list_listview);
    }

    @Override
    protected void onPause()
    {
        helper.close();
        super.onPause();
    }
}
