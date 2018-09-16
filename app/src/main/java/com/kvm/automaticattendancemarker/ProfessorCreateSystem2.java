package com.kvm.automaticattendancemarker;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ProfessorCreateSystem2 extends AppCompatActivity
{
    //////////////////////////////////////////////////////////////////////////////// Extras
    private static final String EXTRA_DESIGNATION = "ext_designation";
    private static final String EXTRA_BRANCH = "ext_branch";
    private static final String EXTRA_SYSTEM_TYPE = "ext_type";
    //////////////////////////////////////////////////////////////////////////////// Extras

    private static final String EXTRA_SYSTEM_TYPE2 = "system_type";

    private static String forwardExtraDesignation;
    private static String systemType;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professor_create_system2);

        toAgencyFB();

        forwardExtraDesignation = getIntent().getStringExtra(EXTRA_DESIGNATION);
        systemType = getIntent().getStringExtra(EXTRA_SYSTEM_TYPE);

        Button next_button = (Button) findViewById(R.id.professor_create_system2_btn_next_page);
        next_button.setOnClickListener(onNextButtonClickListener);

        quickToaster("SYSTEM TYPE: "+systemType);
    }





    private View.OnClickListener onNextButtonClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            TextView thisPageTextView = (TextView) findViewById(R.id.professor_create_system2_edtxt_branch);

            if(thisPageTextView.getText().toString().equals(""))
            {
                quickToaster("INVALID BRANCH!");
                return;
            }

            Intent nextPage = new Intent(ProfessorCreateSystem2.this, ProfessorCreateSystem3.class);
            nextPage.putExtra(EXTRA_DESIGNATION, forwardExtraDesignation);
            nextPage.putExtra(EXTRA_BRANCH, thisPageTextView.getText().toString());
            nextPage.putExtra(EXTRA_SYSTEM_TYPE, systemType);
            startActivity(nextPage);
        }
    };

















    @Override
    public void onBackPressed()
    {
        Intent backToMenu = new Intent(ProfessorCreateSystem2.this, ProfessorSystemMenu.class);
        backToMenu.putExtra(EXTRA_SYSTEM_TYPE2, systemType);
        startActivity(backToMenu);
    }










    private void toAgencyFB()
    {
        TextView lbl0 = (TextView) findViewById(R.id.professor_create_system2_lbl_branch);
        TextView lbl1 = (TextView) findViewById(R.id.professor_create_system2_edtxt_branch);
        TextView lbl2 = (TextView) findViewById(R.id.professor_create_system2_btn_next_page);

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
