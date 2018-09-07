package edu.ramapo.ktavadze.unipal;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

/**
 * Event class.
 */

@IgnoreExtraProperties
public class Event {
    private String name;
    private String type;
    private String courseName;
    private String alarm;
    private int alarmCode = new Random().nextInt();
    private String date = new SimpleDateFormat("MM/dd/yyyy", Locale.US).format(new Date());
    private String time = new SimpleDateFormat("HH:mm", Locale.US).format(new Date());
    private String uid;
    private boolean complete = false;

    public Event() {
        // Default constructor required for calls to DataSnapshot.getValue(Event.class)
    }

    public Event(Event another) {
        this.name = another.name;
        this.type = another.type;
        this.courseName = another.courseName;
        this.alarm = another.alarm;
        this.alarmCode = another.alarmCode;
        this.date = another.date;
        this.time = another.time;
        this.uid = another.uid;
        this.complete = another.complete;
    }

    @Override
    public boolean equals(Object obj) {
        Event event = (Event) obj;
        if (uid.equals(event.getUid())) {
            return true;
        }
        return false;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCourseName() {
        return this.courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getAlarm() {
        return this.alarm;
    }

    public void setAlarm(String alarm) {
        this.alarm = alarm;
    }

    public int getAlarmCode() {
        return this.alarmCode;
    }

    public void setAlarmCode(int alarmCode) {
        this.alarmCode = alarmCode;
    }

    public String getDate() {
        return this.date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return this.time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUid() {
        return this.uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public boolean isComplete() {
        return this.complete;
    }

    public void toggleComplete() {
        this.complete = !this.complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    @Exclude
    public CalendarDay getCalendarDay() {
        String [] dateTokens = this.date.split("/");
        Integer month = Integer.parseInt(dateTokens[0]);
        Integer day = Integer.parseInt(dateTokens[1]);
        Integer year = Integer.parseInt(dateTokens[2]);
        return CalendarDay.from(year, month - 1, day);
    }
}
