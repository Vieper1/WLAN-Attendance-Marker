package com.kvm.automaticattendancemarker;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class ProfessorCreateSystem extends AppCompatActivity
{
    //////////////////////////////////////////////////////////////////////////////// Extras
    private static final String EXTRA_DESIGNATION = "ext_designation";
    private static final String EXTRA_SYSTEM_TYPE = "ext_type";
    //////////////////////////////////////////////////////////////////////////////// Extras


    private static final String EXTRA_SYSTEM_TYPE2 = "system_type";


    private static String systemType;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professor_create_system);

        toAgencyFB();


        Button next_button = (Button) findViewById(R.id.professor_create_system_btn_next_page);
        next_button.setOnClickListener(onNextButtonClickListener);

        Bundle extras = getIntent().getExtras();

        systemType = extras.getString((EXTRA_SYSTEM_TYPE));

        //systemType = getIntent().getStringExtra(EXTRA_SYSTEM_TYPE);
        //getIntent().removeExtra(EXTRA_SYSTEM_TYPE);

        quickToaster("SYSTEM TYPE: "+systemType);
    }









    private View.OnClickListener onNextButtonClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            RadioGroup tempGroupRef = (RadioGroup) findViewById(R.id.professor_create_system_radio_group);
            int checkedId = tempGroupRef.getCheckedRadioButtonId();

            Intent nextPage = new Intent(ProfessorCreateSystem.this, ProfessorCreateSystem2.class);

            switch(checkedId)
            {
                case R.id.professor_create_system_rd_fe:
                    nextPage.putExtra(EXTRA_DESIGNATION, "FE");
                    nextPage.putExtra(EXTRA_SYSTEM_TYPE, systemType);
                    startActivity(nextPage);
                    break;
                case R.id.professor_create_system_rd_se:
                    nextPage.putExtra(EXTRA_DESIGNATION, "SE");
                    nextPage.putExtra(EXTRA_SYSTEM_TYPE, systemType);
                    startActivity(nextPage);
                    break;
                case R.id.professor_create_system_rd_te:
                    nextPage.putExtra(EXTRA_DESIGNATION, "TE");
                    nextPage.putExtra(EXTRA_SYSTEM_TYPE, systemType);
                    startActivity(nextPage);
                    break;
                case R.id.professor_create_system_rd_be:
                    nextPage.putExtra(EXTRA_DESIGNATION, "BE");
                    nextPage.putExtra(EXTRA_SYSTEM_TYPE, systemType);
                    startActivity(nextPage);
                    break;


                default:
                    quickToaster("NOTHING SELECTED!");
            }
        }
    };






    @Override
    public void onBackPressed()
    {
        Intent backToMenu = new Intent(ProfessorCreateSystem.this, ProfessorSystemMenu.class);
        backToMenu.putExtra(EXTRA_SYSTEM_TYPE2, systemType);
        startActivity(backToMenu);
    }













    private void toAgencyFB()
    {
        TextView lbl0 = (TextView) findViewById(R.id.professor_create_system_lbl_batch_year);
        TextView lbl1 = (TextView) findViewById(R.id.professor_create_system_rd_fe);
        TextView lbl2 = (TextView) findViewById(R.id.professor_create_system_rd_se);
        TextView lbl3 = (TextView) findViewById(R.id.professor_create_system_rd_te);
        TextView lbl4 = (TextView) findViewById(R.id.professor_create_system_rd_be);
        //TextView lbl5 = (TextView) findViewById(R.id.professor_create_system_rd_mcafe);
        //TextView lbl6 = (TextView) findViewById(R.id.professor_create_system_rd_mcase);
        TextView lbl7 = (TextView) findViewById(R.id.professor_create_system_btn_next_page);

        Typeface agencyfb = Typeface.createFromAsset(getAssets(), "agency_fb.ttf");
        lbl0.setTypeface(agencyfb);
        lbl1.setTypeface(agencyfb);
        lbl2.setTypeface(agencyfb);
        lbl3.setTypeface(agencyfb);
        lbl4.setTypeface(agencyfb);
        //lbl5.setTypeface(agencyfb);
        //lbl6.setTypeface(agencyfb);
        lbl7.setTypeface(agencyfb);
    }

    //////////////////////////////////////////////////////////////////////////////// Can Be Used
    private void quickToaster(String message)
    {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    //////////////////////////////////////////////////////////////////////////////// Can Be Used
}
