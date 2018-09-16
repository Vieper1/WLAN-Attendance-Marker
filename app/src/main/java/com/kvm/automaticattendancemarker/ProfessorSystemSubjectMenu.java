package com.kvm.automaticattendancemarker;

import android.content.Intent;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kvm.automaticattendancemarker.utilities.dbrel.AttendanceSystemDBHelper;
import com.kvm.automaticattendancemarker.activities.navigator.ClassToNav;

public class ProfessorSystemSubjectMenu extends AppCompatActivity
{
    //////////////////////////////////////////////////////////////////////////////// Categories
    private static final String[] systemsArray = {
            "CLASS",
            "PRACTICAL"
    };

    private static final String EXTRA_SYSTEM_TYPE = "system_type";
    private static final String EXTRA_SYSTEM_TYPE1_2 = "ext_type";

    private static final String EXTRA_CLASS_ID = "class_id";
    private static final String EXTRA_SUBJECT_ID = "subject_id";
    //////////////////////////////////////////////////////////////////////////////// Categories

    private String MenuType;

    private String classId;

    private Cursor subjectCursor;

    private static Uri uri;

    private AttendanceSystemDBHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professor_system_subject_menu);

        MenuType = getIntent().getStringExtra(EXTRA_SYSTEM_TYPE);
        classId = getIntent().getStringExtra(EXTRA_CLASS_ID);

        helper = new AttendanceSystemDBHelper(this);
        /*
        getIntent().removeExtra(EXTRA_SYSTEM_TYPE);
        */


        toAgencyFB();

        uri = Uri.parse(getResources().getString(R.string.studbase_content_provider_uri));

        FloatingActionButton createSystemFAB = (FloatingActionButton) findViewById(R.id.professor_system_subject_menu_page_fab_create_delete_subject);
        createSystemFAB.setOnClickListener(onAddSystemClickListener);

        menuListBuild(MenuType);







    }





    //////////////////////////////////////////////////////////////////////////////// List Builder
    public void menuListBuild(String MenuType)
    {
        String classTableName = getResources().getString(R.string.studbase_table1_name);
        String subjectTableName = getResources().getString(R.string.studbase_table5_name);

        try
        {
            ListView subjectListView = (ListView) findViewById(R.id.professor_system_subject_menu_page_lv_collective_subsystem);


            String projection[] = new String[]{helper.TABLE_5_COL_1};
            String selection = String.format("%s = ?", helper.TABLE_5_COL_2);
            String selectionArgs[] = new String[]{classId};

            subjectCursor = helper.querySpecific(helper.TABLE_5_NAME, projection, selection, selectionArgs, null, null, null);


            int listSize = subjectCursor.getCount();
            int listIterator = 0;

            String listItemData[] = new String[listSize];

            while(listIterator < listSize)
            {
                subjectCursor.moveToNext();
                listItemData[listIterator] = String.format(
                        "%s",
                        subjectCursor.getString(subjectCursor.getColumnIndex(helper.TABLE_5_COL_1))
                );
                listIterator++;
            }

            ListAdapter subjectListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listItemData);

            subjectListView.setAdapter(subjectListAdapter);
            subjectListView.setOnItemClickListener(onSubjectListItemClickListener);
        }
        catch(NullPointerException e)
        {
            quickToaster("COULDN'T BUILD LIST VIEW!");
            e.printStackTrace();
        }
    }
    //////////////////////////////////////////////////////////////////////////////// List Builder

    //////////////////////////////////////////////////////////////////////////////// List Item
    private AdapterView.OnItemClickListener onSubjectListItemClickListener = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            String classTableName = getResources().getString(R.string.studbase_table1_name);
            String subjectTableName = getResources().getString(R.string.studbase_table5_name);

            String dataSubjectId;
            String dataClassId = classId;

            try
            {
                subjectCursor.moveToPosition(position);
                dataSubjectId = subjectCursor.getString(subjectCursor.getColumnIndex(helper.TABLE_5_COL_1));

                Intent toAttendanceNow = new Intent(ProfessorSystemSubjectMenu.this, ClassToNav.class);
                toAttendanceNow.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                toAttendanceNow.putExtra(EXTRA_CLASS_ID, dataClassId);
                toAttendanceNow.putExtra(EXTRA_SUBJECT_ID, dataSubjectId);
                toAttendanceNow.putExtra(EXTRA_SYSTEM_TYPE, MenuType);
                startActivity(toAttendanceNow);
            }
            catch(NullPointerException e)
            {
                quickToaster("subjectCursor null!");
            }
            catch(CursorIndexOutOfBoundsException e)
            {
                quickToaster("INVALID subjectCursor LOCATION REQUEST!");
            }
        }
    };
    //////////////////////////////////////////////////////////////////////////////// List Item




    //////////////////////////////////////////////////////////////////////////////// Create Subject
    private View.OnClickListener onAddSystemClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            //Intent navigateToAddSystemPage = new Intent(ProfessorSystemMenu.this, ProfessorCreateSystem.class);
            //navigateToAddSystemPage.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //navigateToAddSystemPage.putExtra(EXTRA_SYSTEM_TYPE1_2, MenuType);
            //startActivity(navigateToAddSystemPage);
        }
    };
    //////////////////////////////////////////////////////////////////////////////// Create Subject





















    @Override
    protected void onStop()
    {
        helper.close();
        super.onStop();
    }

    @Override
    public void onBackPressed()
    {
        Intent backToMenu = new Intent(ProfessorSystemSubjectMenu.this, ProfessorSystemMenu.class);
        backToMenu.putExtra(EXTRA_SYSTEM_TYPE, MenuType);
        startActivity(backToMenu);
    }

    private void toAgencyFB()
    {
        TextView lbl1 = (TextView) findViewById(R.id.professor_system_subject_menu_page_lbl_choose_subsystem);

        Typeface agencyfb = Typeface.createFromAsset(getAssets(), "agency_fb.ttf");
        lbl1.setTypeface(agencyfb);
    }

    //////////////////////////////////////////////////////////////////////////////// Can Be Used
    private void quickToaster(String message)
    {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    //////////////////////////////////////////////////////////////////////////////// Can Be Used
}
