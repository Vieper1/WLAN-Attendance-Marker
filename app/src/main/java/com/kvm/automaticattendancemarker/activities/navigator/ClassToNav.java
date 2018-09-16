package com.kvm.automaticattendancemarker.activities.navigator;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.kvm.automaticattendancemarker.AttendanceModeAddStudents;
import com.kvm.automaticattendancemarker.AttendanceModeProfessor;
import com.kvm.automaticattendancemarker.ProfessorSystemSubjectMenu;
import com.kvm.automaticattendancemarker.R;
import com.kvm.automaticattendancemarker.activities.ManageClass;
import com.kvm.automaticattendancemarker.activities.result.AttendanceLogList;

public class ClassToNav extends AppCompatActivity
{
    private Button btn_attendance;
    private Button btn_logs;
    private Button btn_manage;

    private Bundle toPassBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_to_nav);

        setPageTitle();
        toAgencyFB();
        setRefs();

        toPassBundle = getIntent().getExtras();
    }

    View.OnClickListener onButtonClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            Intent intent;

            if(view.getId() == R.id.btn_attendance)
            {
                intent = new Intent(ClassToNav.this, AttendanceModeProfessor.class);
                intent.putExtras(toPassBundle);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }

            if(view.getId() == R.id.btn_logs)
            {
                intent = new Intent(ClassToNav.this, AttendanceLogList.class);
                intent.putExtras(toPassBundle);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }

            if(view.getId() == R.id.btn_manage)
            {
                intent = new Intent(ClassToNav.this, ManageClass.class);
                intent.putExtras(toPassBundle);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }
    };
















    public void toAgencyFB()
    {
        try
        {
            TextView lbl1 = (TextView) findViewById(R.id.btn_attendance);
            TextView lbl2 = (TextView) findViewById(R.id.btn_logs);
            TextView lbl3 = (TextView) findViewById(R.id.btn_manage);

            Typeface agencyfb = Typeface.createFromAsset(getAssets(), "agency_fb.ttf");
            lbl1.setTypeface(agencyfb);
            lbl2.setTypeface(agencyfb);
            lbl3.setTypeface(agencyfb);
        }
        catch(NullPointerException e)
        {
            // Nothing
        }
    }


    private void setPageTitle()
    {
        try
        {
            getSupportActionBar().setTitle("Motive");
        }
        catch(NullPointerException e)
        {
            // Nothing
        }
    }

    private void setRefs()
    {
        btn_attendance = (Button) findViewById(R.id.btn_attendance);
        btn_logs = (Button) findViewById(R.id.btn_logs);
        btn_manage = (Button) findViewById(R.id.btn_manage);

        btn_attendance.setOnClickListener(onButtonClickListener);
        btn_logs.setOnClickListener(onButtonClickListener);
        btn_manage.setOnClickListener(onButtonClickListener);
    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(this, ProfessorSystemSubjectMenu.class);
        intent.putExtras(toPassBundle);
        startActivity(intent);
    }
}
