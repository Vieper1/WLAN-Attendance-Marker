package com.kvm.automaticattendancemarker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.kvm.automaticattendancemarker.activities.under_new_management.ManagerMenu;

public class Splasher extends AppCompatActivity
{
    private static final int SPLASH_LENGTH = 2500;



    Thread stay_for_now;
    boolean clearToNavigate = true;

    String appModeValue;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splasher);

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);


        TextView lbl0 = (TextView) findViewById(R.id.splasher_lbl_app_name);
        Typeface agencyfb = Typeface.createFromAsset(getAssets(), "agency_fb.ttf");
        lbl0.setTypeface(agencyfb);

        String sharedPrefFileName = getResources().getString(R.string.shared_pref_app_pref);
        final String appMode = getResources().getString(R.string.shared_pref_app_pref_mode);
        SharedPreferences sharedPref = getSharedPreferences(sharedPrefFileName, Context.MODE_PRIVATE);

        appModeValue = sharedPref.getString(appMode, "NULL");






        stay_for_now = new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    sleep(SPLASH_LENGTH);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                finally
                {
                    if(appModeValue.equals("NULL"))
                        startActivity(new Intent(Splasher.this, LoginPageChooseProfile.class));
                    else if(appModeValue.equals("professor"))
                        startActivity(new Intent(Splasher.this, ProfessorMenu.class));
                    else if(appModeValue.equals("manager"))
                        startActivity(new Intent(Splasher.this, ManagerMenu.class));

                    finish();
                }
            }
        };
        stay_for_now.start();



    }

    @Override
    public void onBackPressed()
    {
        clearToNavigate = false;
        finish();
    }
}
