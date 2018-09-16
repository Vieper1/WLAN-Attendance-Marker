package com.kvm.automaticattendancemarker.activities.settings;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.kvm.automaticattendancemarker.AttendanceModeProfessor;
import com.kvm.automaticattendancemarker.R;
import com.kvm.automaticattendancemarker.fragments.SettingsFragment;

public class SettingsActivitySecond extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_second);

        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment(), "SettingsFragment").commit();
    }


    @Override
    public void onBackPressed() {
        startActivity(new Intent(SettingsActivitySecond.this, AttendanceModeProfessor.class));
    }
}
