package edu.ramapo.ktavadze.unipal;

import com.google.firebase.database.IgnoreExtraProperties;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Event class.
 */

@IgnoreExtraProperties
public class Event {
    private String name = null;
    private String type = null;
    private String date = new SimpleDateFormat("MM/dd/yyyy", Locale.US).format(new Date());
    private String time = new SimpleDateFormat("HH:mm", Locale.US).format(new Date());
    private String uid = null;
    private boolean complete = false;

    public Event() {
        // Default constructor required for calls to DataSnapshot.getValue(Event.class)
    }

    public Event(String name, String type, String date, String time, String uid, boolean complete) {
        this.name = name;
        this.type = type;
        this.date = date;
        this.time = time;
        this.uid = uid;
        this.complete = complete;
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
}
