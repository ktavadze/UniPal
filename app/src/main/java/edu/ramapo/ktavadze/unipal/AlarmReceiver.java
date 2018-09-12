package edu.ramapo.ktavadze.unipal;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * AlarmReceiver Class to receive alarm broadcasts.
 */

public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "AlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        // Receive device reboot broadcast
        if (intent.getAction() != null && context != null) {
            if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
                Log.d(TAG, "onReceive: ACTION_BOOT_COMPLETED");
                return;
            }
        }

        // Receive alarm broadcast
        String title = intent.getStringExtra("title");
        String content = intent.getStringExtra("content");
        int alarmCode = intent.getIntExtra("alarmCode", 13);
        AlarmScheduler.showNotification(context, title, content, alarmCode);
    }
}
