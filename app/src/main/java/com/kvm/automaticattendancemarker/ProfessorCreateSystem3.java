package com.kvm.automaticattendancemarker;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.kvm.automaticattendancemarker.utilities.dbrel.AttendanceSystemDBHelper;

public class ProfessorCreateSystem3 extends AppCompatActivity
{
    //////////////////////////////////////////////////////////////////////////////// Extras
    private static final String EXTRA_DESIGNATION = "ext_designation";
    private static final String EXTRA_BRANCH = "ext_branch";
    private static final String EXTRA_SUBJECT ="ext_subject";
    private static final String EXTRA_SYSTEM_TYPE = "ext_type";
    //////////////////////////////////////////////////////////////////////////////// Extras


    private static final String EXTRA_SYSTEM_TYPE2 = "system_type";



    private static String forwardExtraDesignation;
    private static String forwardExtraBranch;
    private static String systemType;

    private static Uri uri;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professor_create_system3);

        toAgencyFB();

        uri = Uri.parse(getResources().getString(R.string.studbase_content_provider_uri));

        forwardExtraDesignation = getIntent().getStringExtra(EXTRA_DESIGNATION);
        forwardExtraBranch = getIntent().getStringExtra(EXTRA_BRANCH);
        systemType = getIntent().getStringExtra(EXTRA_SYSTEM_TYPE);

        Button next_button = (Button) findViewById(R.id.professor_create_system3_btn_next_page);
        next_button.setOnClickListener(onNextButtonClickListener);


        if(systemType.equals("PRACTICAL"))
        {
            String buttonText = "NEXT";
            next_button.setText(buttonText);
        }

        /*
        // For Testing Purposes Only
        AttendanceSystemDBHelper helper = new AttendanceSystemDBHelper(getApplicationContext());
        helper.deleteSpecific(uri, helper.TABLE_1_NAME, null, null);
        helper.deleteSpecific(uri, helper.TABLE_5_NAME, null, null);
        */





        quickToaster("DESIGNATION & BRANCH: "+forwardExtraDesignation+" "+forwardExtraBranch);
    }








    private View.OnClickListener onNextButtonClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            TextView thisPageTextView = (TextView) findViewById(R.id.professor_create_system3_edtxt_subject);

            quickToaster("CALLED!");

            if(thisPageTextView.getText().toString().equals(""))
            {
                quickToaster("INVALID SUBJECT!");
                return;
            }

            String dataDesignation = forwardExtraDesignation;
            String dataBranch = forwardExtraBranch;
            String dataSubject = thisPageTextView.getText().toString();

            if(systemType.equals("PRACTICAL"))
            {
                Intent nextPage = new Intent(ProfessorCreateSystem3.this, ProfessorCreateSystem4.class);
                nextPage.putExtra(EXTRA_DESIGNATION, forwardExtraDesignation);
                nextPage.putExtra(EXTRA_BRANCH, forwardExtraBranch);
                nextPage.putExtra(EXTRA_SUBJECT, thisPageTextView.getText().toString());
                startActivity(nextPage);
            }

            ////////////////////////////// Database - Check for Instances
            boolean doesInstanceExist = false;

            ////////////////////////////// Database - Check for Instances


            ////////////////////////////// Database - New Entry
            if(!doesInstanceExist)
            {
                AttendanceSystemDBHelper helper = new AttendanceSystemDBHelper(getApplicationContext());

                String classTableName = AttendanceSystemDBHelper.TABLE_1_NAME;
                String subjectTableName = AttendanceSystemDBHelper.TABLE_5_NAME;

                ////////////////////////////////////////////////// COLS
                String colBranch = AttendanceSystemDBHelper.TABLE_1_COL_1;
                String colDesig = AttendanceSystemDBHelper.TABLE_1_COL_2;
                String colLastRoll = AttendanceSystemDBHelper.TABLE_1_COL_3;

                String colId = AttendanceSystemDBHelper.TABLE_1_COL_0;
                ////////////////////////////////////////////////// COLS




                ContentValues contentValuesClass = new ContentValues();
                contentValuesClass.put(colBranch, dataBranch);
                contentValuesClass.put(colDesig, dataDesignation);
                contentValuesClass.put(colLastRoll, 0);
                helper.insertSpecific(uri, classTableName, contentValuesClass);


                String projection[] = new String[]{colId};
                String selection = String.format("%s = ? AND %s = ?", colDesig, colBranch);
                String selectionArgs[] = {dataDesignation, dataBranch};

                Cursor cursor = helper.querySpecific(classTableName, projection, selection, selectionArgs, null, null, null);
                cursor.moveToNext();
                String class_id = cursor.getString(cursor.getColumnIndex(colId));


                ////////////////////////////////////////////////// COLS
                String colSubject = AttendanceSystemDBHelper.TABLE_5_COL_1;
                String colClassId = AttendanceSystemDBHelper.TABLE_5_COL_2;
                ////////////////////////////////////////////////// COLS


                ContentValues contentValuesSubject = new ContentValues();
                contentValuesSubject.put(colSubject, dataSubject);
                contentValuesSubject.put(colClassId, Integer.parseInt(class_id));
                helper.insertSpecific(uri, subjectTableName, contentValuesSubject);


                Intent navigatorIntent = new Intent(ProfessorCreateSystem3.this, ProfessorSystemMenu.class);
                navigatorIntent.putExtra(EXTRA_SYSTEM_TYPE2, systemType);
                startActivity(navigatorIntent);

                helper.close();
            }
            ////////////////////////////// Database - New Entry
        }
    };












    @Override
    public void onBackPressed()
    {
        Intent backToMenu = new Intent(ProfessorCreateSystem3.this, ProfessorSystemMenu.class);
        backToMenu.putExtra(EXTRA_SYSTEM_TYPE2, systemType);
        startActivity(backToMenu);
    }











    private void toAgencyFB()
    {
        TextView lbl0 = (TextView) findViewById(R.id.professor_create_system3_lbl_subject);
        TextView lbl1 = (TextView) findViewById(R.id.professor_create_system3_edtxt_subject);
        TextView lbl2 = (TextView) findViewById(R.id.professor_create_system3_btn_next_page);

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
