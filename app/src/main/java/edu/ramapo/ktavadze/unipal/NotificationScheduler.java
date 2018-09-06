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

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;

public class NotificationScheduler {
    public static void scheduleAlarm(Context context, Event event) {
        String [] dateTokens = event.getDate().split("/");
        Integer month = Integer.parseInt(dateTokens[0]) - 1;
        Integer day = Integer.parseInt(dateTokens[1]);

        String [] timeTokens = event.getTime().split(":");
        Integer hour = Integer.parseInt(timeTokens[0]);
        Integer minute = Integer.parseInt(timeTokens[1]);

        Calendar nowCalendar = Calendar.getInstance();
        Calendar alarmCalendar = Calendar.getInstance();
        alarmCalendar.set(Calendar.MONTH, month);
        alarmCalendar.set(Calendar.DAY_OF_MONTH, day);
        alarmCalendar.set(Calendar.HOUR_OF_DAY, hour);
        alarmCalendar.set(Calendar.MINUTE, minute);
        alarmCalendar.set(Calendar.SECOND, 0);

        if (alarmCalendar.before(nowCalendar)) {
            alarmCalendar = nowCalendar;
            alarmCalendar.add(Calendar.MINUTE, 5);
        }

        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("title", event.getName());
        intent.putExtra("content", event.getType());
        intent.putExtra("alarmCode", event.getAlarmCode());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, event.getAlarmCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmCalendar.getTimeInMillis(), pendingIntent);
    }

    public static void cancelAlarm(Context context, Event event) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, event.getAlarmCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();
    }

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
                .setContentText(content + " " + alarmCode)
                .setAutoCancel(true)
                .setSound(alarmSound)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentIntent(pendingIntent).build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(alarmCode, notification);
    }
}
