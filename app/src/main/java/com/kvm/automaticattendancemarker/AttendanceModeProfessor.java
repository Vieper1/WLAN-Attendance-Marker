package com.kvm.automaticattendancemarker;

import android.content.Intent;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kvm.automaticattendancemarker.utilities.dbrel.AttendanceSystemDBHelper;
import com.kvm.automaticattendancemarker.activities.navigator.ClassToNav;
import com.kvm.automaticattendancemarker.vipers.services.MarkerPrimaryIntentService;
import com.kvm.automaticattendancemarker.vipers.services.MarkerService;

public class AttendanceModeProfessor extends AppCompatActivity
{
    //////////////////////////////////////////////////////////////////////////////// Categories
    private static final String EXTRA_SYSTEM_TYPE = "system_type";
    private static final String EXTRA_SYSTEM_TYPE1_2 = "ext_type";

    private static final String EXTRA_CLASS_ID = "class_id";
    private static final String EXTRA_SUBJECT_ID = "subject_id";
    //////////////////////////////////////////////////////////////////////////////// Categories

    private String MenuType;

    private String classId;
    private String subjectId;

    private Cursor subjectCursor;

    private static Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_mode_professor);

        toAgencyFB();

        setRefs();

        classDetailsListBuilder();
    }




    //////////////////////////////////////////////////////////////////////////////// List Builder
    private void classDetailsListBuilder()
    {
        ListView listLegend = (ListView) findViewById(R.id.professor_attendance_view_lv_class_details);
        ListView listValue = (ListView) findViewById(R.id.professor_attendance_view_lv_class_details_values);

        String legend[] = new String[]{"YEAR", "BRANCH", "", "STUDENT COUNT"};

        ////////// Array For List
        String values[] = new String[4];


        Cursor classDetails;
        Cursor subjectDetails;
        AttendanceSystemDBHelper helper = new AttendanceSystemDBHelper(getApplicationContext());

        //////////////////////////////////////// COLS
        String tableName = AttendanceSystemDBHelper.TABLE_1_NAME;
        String tableSubName = AttendanceSystemDBHelper.TABLE_5_NAME;


        String colBranch = AttendanceSystemDBHelper.TABLE_1_COL_1;
        String colYear = AttendanceSystemDBHelper.TABLE_1_COL_2;
        String colLastRollNo = AttendanceSystemDBHelper.TABLE_1_COL_3;

        String colId = AttendanceSystemDBHelper.TABLE_1_COL_0;


        String colSubSubject = AttendanceSystemDBHelper.TABLE_5_COL_1;
        String colSubClassId = AttendanceSystemDBHelper.TABLE_5_COL_2;
        //////////////////////////////////////// COLS


        String projection[] = new String[]{colBranch, colYear, colLastRollNo};
        String selection = String.format("%s = ?", colId);
        String selectionArgs[] = new String[]{classId};
        classDetails = helper.querySpecific(tableName, projection, selection, selectionArgs, null, null, null);



        projection = new String[]{colSubSubject};
        selection  = String.format("%s = ?", colSubClassId);
        selectionArgs = new String[]{classId};
        subjectDetails = helper.querySpecific(tableSubName, projection, selection, selectionArgs, null, null, null);


        try
        {
            classDetails.moveToNext();

            values[0] = classDetails.getString(classDetails.getColumnIndex(colYear));
            values[1] = classDetails.getString(classDetails.getColumnIndex(colBranch));
            values[3] = classDetails.getString(classDetails.getColumnIndex(colLastRollNo));

            subjectDetails.moveToNext();
            values[2] = subjectDetails.getString(subjectDetails.getColumnIndex(colSubSubject));

        }
        catch(CursorIndexOutOfBoundsException e)
        {
            quickToaster("Cursor callDetails invalid index request!");
        }

    }
    //////////////////////////////////////////////////////////////////////////////// List Builder



    private View.OnClickListener onAttendanceRelClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            if(v.getId() == R.id.professor_attendance_view_btn_add_students)
            {
                ////////// To Add Students Page

                Intent toAddStudentsPage = new Intent(AttendanceModeProfessor.this, AttendanceModeAddStudents.class);
                toAddStudentsPage.putExtra(EXTRA_CLASS_ID, classId);
                toAddStudentsPage.putExtra(EXTRA_SUBJECT_ID, subjectId);
                toAddStudentsPage.putExtra(EXTRA_SYSTEM_TYPE, MenuType);
                toAddStudentsPage.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(toAddStudentsPage);

            }

            if(v.getId() == R.id.main_page_student_view_btn_start_attendance)
            {
                ////////// Start Attendance
                Button button_add_student = (Button) findViewById(R.id.professor_attendance_view_btn_add_students);
                Button button_start_attendance = (Button) findViewById(R.id.main_page_student_view_btn_start_attendance);
                Button button_set_exclusions = (Button) findViewById(R.id.main_page_student_view_btn_add_exclusion);



                ////////// Add Animation
                button_add_student.setVisibility(View.GONE);
                button_start_attendance.setVisibility(View.GONE);
                ////////// Add Animation


                startAttendanceMarkerService();



                ////////// Add Animation
                button_add_student.setVisibility(View.VISIBLE);
                button_start_attendance.setVisibility(View.VISIBLE);
                button_start_attendance.setText(getResources().getString(R.string.lbl_attendance_mode_start_attendance_alternate));
                ////////// Add Animation
            }

            if(v.getId() == R.id.main_page_student_view_btn_add_exclusion)
            {
                ////////// If Exceptions In Attendance Arise
            }
        }
    };






    //////////////////////////////////////////////////////////////////////////////// SUPER IMPORTANT
    private void startAttendanceMarkerService()
    {
//        Intent intent = new Intent(AttendanceModeProfessor.this, MarkerPrimaryIntentService.class);
//        intent.putExtra(EXTRA_SYSTEM_TYPE, MenuType);
//        intent.putExtra(EXTRA_CLASS_ID, classId);
//        intent.putExtra(EXTRA_SUBJECT_ID, subjectId);
//        ////////////////////////////////////////////////// Important
//        intent.putExtra(ProfessorMenu.EXTRA_SERVICE_COMMAND, "start_attendance");
//        ////////////////////////////////////////////////// Important
//
//        startService(intent);

        Intent intent = new Intent(AttendanceModeProfessor.this, MarkerService.class);
        intent.putExtra(EXTRA_SYSTEM_TYPE, MenuType);
        intent.putExtra(EXTRA_CLASS_ID, classId);
        intent.putExtra(EXTRA_SUBJECT_ID, subjectId);
        startService(intent);
    }
    //////////////////////////////////////////////////////////////////////////////// SUPER IMPORTANT








    private void setRefs()
    {
        MenuType = getIntent().getStringExtra(EXTRA_SYSTEM_TYPE);
        classId = getIntent().getStringExtra(EXTRA_CLASS_ID);
        subjectId = getIntent().getStringExtra(EXTRA_SUBJECT_ID);

        uri = Uri.parse(getResources().getString(R.string.studbase_content_provider_uri));

        Button button_add_student = (Button) findViewById(R.id.professor_attendance_view_btn_add_students);
        Button button_start_attendance = (Button) findViewById(R.id.main_page_student_view_btn_start_attendance);
        Button button_set_exclusions = (Button) findViewById(R.id.main_page_student_view_btn_add_exclusion);

        button_add_student.setOnClickListener(onAttendanceRelClickListener);
        button_start_attendance.setOnClickListener(onAttendanceRelClickListener);
        button_set_exclusions.setOnClickListener(onAttendanceRelClickListener);
    }


    @Override
    public void onBackPressed()
    {
        Intent backToMenu = new Intent(AttendanceModeProfessor.this, ClassToNav.class);
        backToMenu.putExtras(getIntent().getExtras());
        startActivity(backToMenu);
    }

    private void toAgencyFB()
    {
        TextView lbl1 = (TextView) findViewById(R.id.professor_attendance_view_lbl_attendance);
        TextView lbl2 = (TextView) findViewById(R.id.professor_attendance_view_lbl_class_details);
        TextView lbl3 = (TextView) findViewById(R.id.professor_attendance_view_lbl_todays_list);
        TextView lbl4 = (TextView) findViewById(R.id.professor_attendance_view_btn_add_students);
        TextView lbl5 = (TextView) findViewById(R.id.main_page_student_view_btn_start_attendance);
        TextView lbl6 = (TextView) findViewById(R.id.main_page_student_view_btn_add_exclusion);


        Typeface agencyfb = Typeface.createFromAsset(getAssets(), "agency_fb.ttf");
        lbl1.setTypeface(agencyfb);
        lbl2.setTypeface(agencyfb);
        lbl3.setTypeface(agencyfb);
        lbl4.setTypeface(agencyfb);
        lbl5.setTypeface(agencyfb);
        lbl6.setTypeface(agencyfb);
    }


    //////////////////////////////////////////////////////////////////////////////// Can Be Used
    private void quickToaster(String message)
    {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    //////////////////////////////////////////////////////////////////////////////// Can Be Used
}
