package com.example.courseonline.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.courseonline.Adapter.Learner.TimeSwitchAdapter;

import java.util.Map;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            SharedPreferences sharedPreferences = context.getSharedPreferences("AlarmPrefs", Context.MODE_PRIVATE);
            Map<String, ?> allEntries = sharedPreferences.getAll();
            for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                String time = entry.getKey();
                boolean isActive = (Boolean) entry.getValue();
                if (isActive) {
                    TimeSwitchAdapter.setAlarm(context, time);
                }
            }
        }
    }
}
