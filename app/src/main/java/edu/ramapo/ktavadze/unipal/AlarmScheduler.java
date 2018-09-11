package edu.ramapo.ktavadze.unipal;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static android.content.Context.ALARM_SERVICE;

public class AlarmScheduler {
    private static final String TAG = "AlarmScheduler";

    /**/
    /*
    NAME

    scheduleAlarm - schedules alarm.

    SYNOPSIS

    public static void scheduleAlarm(Context context, Event event);
    context--> the context to be used for intent creation and other purposes.
    event--> the event for which the alarm is being scheduled.

    DESCRIPTION

    Will schedule an alarm for the specified event.

    RETURNS

    N/A
    */
    /**/
    public static void scheduleAlarm(Context context, Event event) {
        // Cancel any previous alarms scheduled for the specified event
        cancelAlarm(context, event);

        String [] dateTokens = event.getDate().split("/");
        Integer month = Integer.parseInt(dateTokens[0]) - 1;
        Integer day = Integer.parseInt(dateTokens[1]);

        String [] timeTokens = event.getTime().split(":");
        Integer hour = Integer.parseInt(timeTokens[0]);
        Integer minute = Integer.parseInt(timeTokens[1]);

        // Set alarm time
        Calendar alarmCalendar = Calendar.getInstance();
        alarmCalendar.set(Calendar.MONTH, month);
        alarmCalendar.set(Calendar.DAY_OF_MONTH, day);
        alarmCalendar.set(Calendar.HOUR_OF_DAY, hour);
        alarmCalendar.set(Calendar.MINUTE, minute);
        alarmCalendar.set(Calendar.SECOND, 0);

        switch (event.getAlarm()) {
            case "One hour prior":
                alarmCalendar.add(Calendar.HOUR, -1);
                break;
            case "One day prior":
                alarmCalendar.add(Calendar.DAY_OF_YEAR, -1);
                break;
            case "One week prior":
                alarmCalendar.add(Calendar.WEEK_OF_YEAR, -1);
        }

        // Get current time
        Calendar nowCalendar = Calendar.getInstance();

        if (alarmCalendar.after(nowCalendar)) {
            Intent intent = new Intent(context, AlarmReceiver.class);
            intent.putExtra("title", event.getName());
            intent.putExtra("content", event.getType());
            intent.putExtra("alarmCode", event.getAlarmCode());
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, event.getAlarmCode(),
                    intent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmCalendar.getTimeInMillis(), pendingIntent);

            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm", Locale.US);
            Log.d(TAG, "scheduleAlarm: Alarm scheduled: " + sdf.format(alarmCalendar.getTime()));
        }
    }

    /**/
    /*
    NAME

    cancelAlarm - cancels alarm.

    SYNOPSIS

    public static void cancelAlarm(Context context, Event event);
    context--> the context to be used for intent creation and other purposes.
    event--> the event for which the alarm is being canceled.

    DESCRIPTION

    Will cancel the alarm for the specified event.

    RETURNS

    N/A
    */
    /**/
    public static void cancelAlarm(Context context, Event event) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, event.getAlarmCode(),
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();
    }

    /**/
    /*
    NAME

    showNotification - shows notification.

    SYNOPSIS

    public static void showNotification(Context context, String title, String content, int alarmCode);
    context--> the context to be used for intent creation and other purposes.
    title--> the title of the notification.
    content--> the content of the notification.
    alarmCode--> the unique integer code associated with the specified alarm.

    DESCRIPTION

    Will display the notification in the device notification drawer.

    RETURNS

    N/A
    */
    /**/
    public static void showNotification(Context context, String title, String content, int alarmCode) {
        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(notificationIntent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(alarmCode, PendingIntent.FLAG_UPDATE_CURRENT);

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, User.getUid());
        Notification notification = builder.setContentTitle(title)
                .setContentText(content)
                .setAutoCancel(true)
                .setSound(alarmSound)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentIntent(pendingIntent).build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(alarmCode, notification);
    }
}
