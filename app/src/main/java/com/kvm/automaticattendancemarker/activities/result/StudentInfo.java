package com.kvm.automaticattendancemarker.activities.result;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.kvm.automaticattendancemarker.ProfessorMenu;
import com.kvm.automaticattendancemarker.R;
import com.kvm.automaticattendancemarker.activities.ManageClass;
import com.kvm.automaticattendancemarker.utilities.dbrel.AttendanceSystemDBHelper;

import java.io.InputStream;

public class StudentInfo extends AppCompatActivity
{
    //////////////////////////////////////////////////////////////////////////////// Elements
    private ImageView imageViewStudent;
    private TextView txtStudentName;
    private TextView txtClass;
    private TextView txtEnrolledIn;
    private TextView txtCurrentSubject;
    private TextView txtSubjectAttendancePerc;
    private TextView txtAttended;
    private TextView txtTotal;
    private TextView txtRollNo;
    private TextView txtEmail;
    //////////////////////////////////////////////////////////////////////////////// Elements

    //////////////////////////////////////////////////////////////////////////////// Navigation Control
    private String MenuType;
    private String classbatchId;
    private String subjectId;
    private String logIdDirect;
    private String clickedRollNo;
    //////////////////////////////////////////////////////////////////////////////// Navigation Control

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_info);

        setRefs();

        new DataFromDB().execute();
    }





    //////////////////////////////////////////////////////////////////////////////// Get data from DB
    private class DataFromDB extends AsyncTask<Void, Void, Void>
    {
        private AttendanceSystemDBHelper helper;

        private String dataImageUrl;
        private String dataStudentName;
        private String dataClass;
        private String dataCurrentSubject;
        private String dataEnrolledIn;
        private String dataSubjectAttendancePerc;
        private String dataAttended;
        private String dataTotal;
        private String dataEmail;


        @Override
        protected void onPreExecute()
        {
            helper = new AttendanceSystemDBHelper(StudentInfo.this);
        }

        @Override
        protected Void doInBackground(Void... voids)
        {
            Cursor cursor;


            try
            {
                // TODO: Image extraction from DB
                String tableName;
                String selection;
                String[] selectionArgs;

                //////////////////////////////////////////////////////////// Student_list
                //////////////////////////////////////// COLS
                tableName = AttendanceSystemDBHelper.TABLE_0_NAME;

                String colStuRollNo = AttendanceSystemDBHelper.TABLE_0_COL_3;
                String colStuClassId = AttendanceSystemDBHelper.TABLE_0_COL_4;

                String colStuName = AttendanceSystemDBHelper.TABLE_0_COL_1;
                String colStuEmail = AttendanceSystemDBHelper.TABLE_0_COL_7;
                String colStuImgUri = AttendanceSystemDBHelper.TABLE_0_COL_8;
                //////////////////////////////////////// COLS

                selection = String.format("%s = ? AND %s = ?", colStuRollNo, colStuClassId);
                selectionArgs = new String[]{clickedRollNo, classbatchId};

                cursor = helper.querySpecific(tableName, null, selection, selectionArgs, null, null, null);
                cursor.moveToFirst();

                dataStudentName = cursor.getString(cursor.getColumnIndex(colStuName));
                dataEmail = cursor.getString(cursor.getColumnIndex(colStuEmail));

                try
                {
                    dataImageUrl = cursor.getString(cursor.getColumnIndex(colStuImgUri));
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                    System.out.println("ImageUri Fetch Failed!");
                }
                //////////////////////////////////////////////////////////// Student_list




                //////////////////////////////////////////////////////////// class_main
                //////////////////////////////////////// COLS
                tableName = AttendanceSystemDBHelper.TABLE_1_NAME;

                String colClassId = AttendanceSystemDBHelper.TABLE_1_COL_0;

                String colClassYear = AttendanceSystemDBHelper.TABLE_1_COL_2;
                String colClassBranch = AttendanceSystemDBHelper.TABLE_1_COL_1;
                //////////////////////////////////////// COLS

                selection = String.format("%s = ?", colClassId);
                selectionArgs = new String[]{classbatchId};

                cursor = helper.querySpecific(tableName, null, selection, selectionArgs, null, null, null);
                cursor.moveToFirst();

                dataClass = cursor.getString(cursor.getColumnIndex(colClassYear)) + " " + cursor.getString(cursor.getColumnIndex(colClassBranch));
                //////////////////////////////////////////////////////////// class_main




                //////////////////////////////////////////////////////////// subject_list
                //////////////////////////////////////// COLS
                tableName = AttendanceSystemDBHelper.TABLE_5_NAME;

                String colSubjectId = AttendanceSystemDBHelper.TABLE_5_COL_0;
                String colSubjectClassId = AttendanceSystemDBHelper.TABLE_5_COL_2;

                String colSubjectName = AttendanceSystemDBHelper.TABLE_5_COL_1;
                //////////////////////////////////////// COLS

                selection = String.format("%s = ? AND %s = ?", colSubjectName, colSubjectClassId);
                selectionArgs = new String[]{subjectId, classbatchId};

                cursor = helper.querySpecific(tableName, null, selection, selectionArgs, null, null, null);
                cursor.moveToFirst();

                dataCurrentSubject = cursor.getString(cursor.getColumnIndex(colSubjectName));
                // Get 1

                selection = String.format("%s = ?", colSubjectClassId);
                selectionArgs = new String[]{classbatchId};

                cursor = helper.querySpecific(tableName, null, selection, selectionArgs, null, null, null);
                cursor.moveToFirst();

                dataEnrolledIn = "";
                for(int i=0;i<cursor.getCount();i++)
                {
                    cursor.moveToPosition(i);
                    dataEnrolledIn += cursor.getString(cursor.getColumnIndex(colSubjectName)) + ", ";
                }
                dataEnrolledIn = dataEnrolledIn.substring(0, dataEnrolledIn.length()-2);
                // Get 2
                //////////////////////////////////////////////////////////// subject_list
            }
            catch(Exception e)
            {
                e.printStackTrace();
                System.out.println("DB connection failed!");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            helper.close();
            new UpdateImageOnPage().execute(dataImageUrl);

            txtRollNo.setText(clickedRollNo);
            txtStudentName.setText(dataStudentName);
            txtEmail.setText(dataEmail);
            txtClass.setText(dataClass);
            txtCurrentSubject.setText(dataCurrentSubject);
            txtEnrolledIn.setText(dataEnrolledIn);
        }
    }
    //////////////////////////////////////////////////////////////////////////////// Get data from DB







    //////////////////////////////////////////////////////////////////////////////// Set Image Dynamically
    private class UpdateImageOnPage extends AsyncTask<String, Void, Bitmap>
    {
        @Override
        protected Bitmap doInBackground(String... strings)
        {
            String imageUrl = strings[0];
            Bitmap bitmap = null;
            try
            {
                InputStream input = new java.net.URL(imageUrl).openStream();
                bitmap = BitmapFactory.decodeStream(input);
            }
            catch(Exception e)
            {
                e.printStackTrace();
                System.out.print("Image download failed");
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap)
        {
            if(bitmap != null)
            {
                imageViewStudent.setImageBitmap(bitmap);
            }
        }
    }
    //////////////////////////////////////////////////////////////////////////////// Set Image Dynamically


    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(StudentInfo.this, ManageClass.class);
        intent.putExtras(getIntent().getExtras());
        startActivity(intent);
    }

    private void setRefs()
    {
        Bundle tempBundle = getIntent().getExtras();
        MenuType = tempBundle.getString(ProfessorMenu.EXTRA_SYSTEM_TYPE);
        subjectId = tempBundle.getString(ProfessorMenu.EXTRA_SUBJECT_ID);
        classbatchId = tempBundle.getString(ProfessorMenu.EXTRA_CLASS_ID);
        logIdDirect = tempBundle.getString(ProfessorMenu.EXTRA_SESSION_ID);
        clickedRollNo = tempBundle.getString(ProfessorMenu.EXTRA_ROLL_NO);


        imageViewStudent = (ImageView) findViewById(R.id.imageview);
        txtStudentName = (TextView) findViewById(R.id.txt_student_name);
        txtClass = (TextView) findViewById(R.id.txt_student_class);
        txtEnrolledIn = (TextView) findViewById(R.id.txt_enrolled_in);
        txtCurrentSubject = (TextView) findViewById(R.id.txt_current_subject);
        txtSubjectAttendancePerc = (TextView) findViewById(R.id.txt_current_subject_attendance);
        txtAttended = (TextView) findViewById(R.id.txt_current_subject_attended_number);
        txtTotal = (TextView) findViewById(R.id.txt_current_subject_total_number);
        txtRollNo = (TextView) findViewById(R.id.txt_rollno);
        txtEmail = (TextView) findViewById(R.id.txt_email);
    }
}
