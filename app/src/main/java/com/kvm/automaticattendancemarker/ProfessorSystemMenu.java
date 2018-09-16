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

public class ProfessorSystemMenu extends AppCompatActivity
{
    //////////////////////////////////////////////////////////////////////////////// Categories
    private static final String[] systemsArray = {
            "CLASS",
            "PRACTICAL"
    };

    private static final String EXTRA_SYSTEM_TYPE = "system_type";
    private static final String EXTRA_SYSTEM_TYPE1_2 = "ext_type";

    private static final String EXTRA_CLASS_ID = "class_id";
    //////////////////////////////////////////////////////////////////////////////// Categories

    private AttendanceSystemDBHelper helper;

    private String MenuType;

    private static Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professor_system_menu);

        MenuType = getIntent().getStringExtra(EXTRA_SYSTEM_TYPE);

        helper = new AttendanceSystemDBHelper(this);

        /*
        getIntent().removeExtra(EXTRA_SYSTEM_TYPE);
        */


        toAgencyFB();

        uri = Uri.parse(getResources().getString(R.string.studbase_content_provider_uri));

        FloatingActionButton createSystemFAB = (FloatingActionButton) findViewById(R.id.professor_system_menu_page_fab_create_delete_subsystem);
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
            if(MenuType.equals("CLASS"))
            {
                ListView systemListView = (ListView) findViewById(R.id.professor_system_menu_page_lv_collective_subsystem);
                Cursor systemCursor;

                systemCursor = helper.rawQueryGetFull(classTableName);




                try
                {
                    int listSize = systemCursor.getCount();
                    int listIterator = 0;

                    String listItemData[] = new String[listSize];

                    systemCursor.moveToFirst();
                    while(listIterator < listSize)
                    {
                        listItemData[listIterator] = String.format(
                                "%s %s %s",
                                systemCursor.getString(systemCursor.getColumnIndex(helper.TABLE_1_COL_2)),
                                systemCursor.getString(systemCursor.getColumnIndex(helper.TABLE_1_COL_1)),
                                systemCursor.getString(systemCursor.getColumnIndex(helper.TABLE_1_COL_3))
                        );


                        ////////////////////////////// To Fix Later
                        try
                        {
                            systemCursor.moveToNext();
                        }
                        catch (CursorIndexOutOfBoundsException e)
                        {
                            e.printStackTrace();
                        }
                        ////////////////////////////// To Fix Later

                        listIterator++;
                    }


                    ListAdapter systemListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listItemData);

                    systemListView.setAdapter(systemListAdapter);
                    systemListView.setOnItemClickListener(onSystemListItemClickListener);

                }
                catch(NullPointerException e)
                {
                    quickToaster("COULDN'T BUILD LIST VIEW!");
                    e.printStackTrace();
                }
            }
            else if(MenuType.equals("PRACTICAL"))
            {

            }
        }
        catch(NullPointerException e)
        {
            e.printStackTrace();
            Intent navigatorIntent = new Intent(ProfessorSystemMenu.this, ProfessorMenu.class);
            startActivity(navigatorIntent);
        }
    }
    //////////////////////////////////////////////////////////////////////////////// List Builder



    //////////////////////////////////////////////////////////////////////////////// List Item
    private AdapterView.OnItemClickListener onSystemListItemClickListener = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            String classTableName = getResources().getString(R.string.studbase_table1_name);
            String subjectTableName = getResources().getString(R.string.studbase_table5_name);

            ListView systemListView = (ListView) findViewById(R.id.professor_system_menu_page_lv_collective_subsystem);
            String listItemText = systemListView.getItemAtPosition(position).toString();

            String listItemTextParts[] = listItemText.split(" ");


            ////////////////////////////////////////////////// Change this when you mod the ListView
            String dataDesignation = listItemTextParts[0];
            String dataBranch = listItemTextParts[1];
            ////////////////////////////////////////////////// Change this when you mod the ListView

            String projection[] = new String[]{helper.TABLE_1_COL_0};
            String selection = String.format("%s = ? AND %s = ?", helper.TABLE_1_COL_2, helper.TABLE_1_COL_1);
            String selectionArgs[] = {dataDesignation, dataBranch};

            Cursor cursor = helper.querySpecific(classTableName, projection, selection, selectionArgs, null, null, null);
            cursor.moveToNext();
            String class_id = cursor.getString(cursor.getColumnIndex(helper.TABLE_1_COL_0));



            Intent goToSubjectMenu = new Intent(ProfessorSystemMenu.this, ProfessorSystemSubjectMenu.class);
            goToSubjectMenu.putExtra(EXTRA_CLASS_ID, class_id);
            goToSubjectMenu.putExtra(EXTRA_SYSTEM_TYPE, MenuType);
            goToSubjectMenu.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(goToSubjectMenu);
        }
    };
    //////////////////////////////////////////////////////////////////////////////// List Item










    //////////////////////////////////////////////////////////////////////////////// Create System
    private View.OnClickListener onAddSystemClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            Intent navigateToAddSystemPage = new Intent(ProfessorSystemMenu.this, ProfessorCreateSystem.class);
            navigateToAddSystemPage.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            navigateToAddSystemPage.putExtra(EXTRA_SYSTEM_TYPE1_2, MenuType);
            startActivity(navigateToAddSystemPage);
        }
    };
    //////////////////////////////////////////////////////////////////////////////// Create System





















    @Override
    protected void onStop()
    {
        helper.close();
        super.onStop();
    }

    @Override
    public void onBackPressed()
    {
        startActivity(new Intent(ProfessorSystemMenu.this, ProfessorMenu.class));
    }

    private void toAgencyFB()
    {
        TextView lbl1 = (TextView) findViewById(R.id.professor_system_menu_page_lbl_choose_subsystem);

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
