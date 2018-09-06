package edu.ramapo.ktavadze.unipal;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "AlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && context != null) {
            if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
                Log.d(TAG, "onReceive: ACTION_BOOT_COMPLETED");
                return;
            }
        }

        // Show notification
        String title = intent.getStringExtra("title");
        String content = intent.getStringExtra("content");
        int alarmCode = intent.getIntExtra("alarmCode", 13);
        NotificationScheduler.showNotification(context, title, content, alarmCode);
    }
}
