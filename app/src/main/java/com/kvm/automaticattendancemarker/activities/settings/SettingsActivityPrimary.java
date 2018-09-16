package com.kvm.automaticattendancemarker.activities.settings;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.kvm.automaticattendancemarker.ProfessorMenu;
import com.kvm.automaticattendancemarker.R;
import com.kvm.automaticattendancemarker.fragments.SettingsFragment;

public class SettingsActivityPrimary extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_primary);

//        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment(), "SettingsFragment").commit();
        FragmentManager fragManager = getFragmentManager();
        FragmentTransaction fragTransaction = fragManager.beginTransaction();
        SettingsFragment frag = new SettingsFragment();
        fragTransaction.add(R.id.frag_container, frag, "Settings");
        fragTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(SettingsActivityPrimary.this, ProfessorMenu.class));
    }
}
