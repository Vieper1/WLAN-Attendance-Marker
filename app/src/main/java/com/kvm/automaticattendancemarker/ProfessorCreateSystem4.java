package com.kvm.automaticattendancemarker;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ProfessorCreateSystem4 extends AppCompatActivity
{
    //////////////////////////////////////////////////////////////////////////////// Extras
    private static final String EXTRA_DESIGNATION = "ext_designation";
    private static final String EXTRA_BRANCH = "ext_branch";
    private static final String EXTRA_SUBJECT ="ext_subject";
    //////////////////////////////////////////////////////////////////////////////// Extras

    private static final String EXTRA_SYSTEM_TYPE2 = "system_type";



    private static String forwardExtraDesignation;
    private static String forwardExtraBranch;
    private static String forwardExtraSubject;




    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professor_create_system4);

        toAgencyFB();

        forwardExtraDesignation = getIntent().getStringExtra(EXTRA_DESIGNATION);
        forwardExtraBranch = getIntent().getStringExtra(EXTRA_BRANCH);
        forwardExtraSubject = getIntent().getStringExtra(EXTRA_SUBJECT);

        Button next_button = (Button) findViewById(R.id.professor_create_system4_btn_next_page);
        next_button.setOnClickListener(onNextButtonClickListener);




    }




    private View.OnClickListener onNextButtonClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            TextView thisPageTextView = (TextView) findViewById(R.id.professor_create_system3_edtxt_subject);

            if(thisPageTextView.getText().toString().equals(""))
            {
                quickToaster("INVALID SUBJECT!");
                return;
            }

            String dataDesignation = forwardExtraDesignation;
            String dataBranch = forwardExtraBranch;
            String dataSubject = forwardExtraSubject;
            String dataBatchName = thisPageTextView.getText().toString();


            ////////////////////////////// Database - Check for Instances
            ////////////////////////////// Database - Check for Instances


            ////////////////////////////// Database - New Table

            ////////////////////////////// Database - New Table
        }
    };
















    @Override
    public void onBackPressed()
    {
        Intent backToMenu = new Intent(ProfessorCreateSystem4.this, ProfessorSystemMenu.class);
        backToMenu.putExtra(EXTRA_SYSTEM_TYPE2, "PRACTICAL");
        startActivity(backToMenu);
    }



    private void toAgencyFB()
    {
        TextView lbl0 = (TextView) findViewById(R.id.professor_create_system4_lbl_batch_name);
        TextView lbl1 = (TextView) findViewById(R.id.professor_create_system4_edtxt_batch_name);
        TextView lbl2 = (TextView) findViewById(R.id.professor_create_system4_btn_next_page);

        Typeface agencyfb = Typeface.createFromAsset(getAssets(), "agency_fb.ttf");
        lbl0.setTypeface(agencyfb);
        lbl1.setTypeface(agencyfb);
        lbl2.setTypeface(agencyfb);
    }


    //////////////////////////////////////////////////////////////////////////////// Can Be Used
    private void quickToaster(String message)
    {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    //////////////////////////////////////////////////////////////////////////////// Can Be Used
}
