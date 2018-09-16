package com.kvm.automaticattendancemarker.vipers.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.kvm.automaticattendancemarker.ProfessorMenu;
import com.kvm.automaticattendancemarker.vipers.services.MarkerPrimaryIntentService;

/**
 * Created by Viper GTS on 11-Jan-17.
 */

public class OnTimerFinishReceiver extends BroadcastReceiver
{
    public final static int REQUEST_CODE = 1911;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        Intent intentFinishService = new Intent(context, MarkerPrimaryIntentService.class);
        intentFinishService.putExtra(ProfessorMenu.EXTRA_SYSTEM_TYPE, intent.getStringExtra(ProfessorMenu.EXTRA_SYSTEM_TYPE));
        intentFinishService.putExtra(ProfessorMenu.EXTRA_CLASS_ID, intent.getStringExtra(ProfessorMenu.EXTRA_CLASS_ID));
        intentFinishService.putExtra(ProfessorMenu.EXTRA_SUBJECT_ID, intent.getStringExtra(ProfessorMenu.EXTRA_SUBJECT_ID));
        ////////////////////////////////////////////////// Important
        intentFinishService.putExtra(ProfessorMenu.EXTRA_SERVICE_COMMAND, "end_attendance");
        ////////////////////////////////////////////////// Important

        context.startService(intentFinishService);
    }
}
