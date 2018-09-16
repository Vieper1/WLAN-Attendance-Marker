package com.kvm.automaticattendancemarker;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.kvm.automaticattendancemarker.activities.settings.SettingsActivityPrimary;

public class ProfessorMenu extends AppCompatActivity
{
    //////////////////////////////////////////////////////////////////////////////// Categories
    private static final String[] systemsArray = {
            "CLASS",
            "PRACTICAL"
    };

    public static final String EXTRA_SYSTEM_TYPE = "system_type";

    public static final String EXTRA_CLASS_ID = "class_id";
    public static final String EXTRA_SUBJECT_ID = "subject_id";

    public static final String EXTRA_SERVICE_COMMAND = "service_command";

    public static final String EXTRA_SESSION_ID = "session_id";

    public static final String EXTRA_ROLL_NO = "roll_no";
    //////////////////////////////////////////////////////////////////////////////// Categories




    /*
        ROSTER CONTENTS
        Professor Username
        Number of Subjects
        Number of Classes
        Number of Batches
    */

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_attendance_mode_professor, menu);
        return true;
    }

    public void onOpenSettings(MenuItem mi)
    {
        startActivity(new Intent(ProfessorMenu.this, SettingsActivityPrimary.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professor_menu);

        toAgencyFB();

        setRefs();
    }


    View.OnClickListener onClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            if(view.getId() == R.id.card_take_lecture)
            {
                Intent intent = new Intent(ProfessorMenu.this, ProfessorSystemMenu.class);
                intent.putExtra(EXTRA_SYSTEM_TYPE, "CLASS");
                startActivity(intent);
            }
        }
    };


    private void setRefs()
    {
        CardView cardTakeLecture = (CardView) findViewById(R.id.card_take_lecture);
        cardTakeLecture.setOnClickListener(onClickListener);
    }


    private void toAgencyFB()
    {
        TextView lbl1 = (TextView) findViewById(R.id.professor_menu_page_lbl_professor_details);

        Typeface agencyfb = Typeface.createFromAsset(getAssets(), "agency_fb.ttf");
        lbl1.setTypeface(agencyfb);
    }


    @Override
    public void onBackPressed()
    {
        finish();
    }
}
