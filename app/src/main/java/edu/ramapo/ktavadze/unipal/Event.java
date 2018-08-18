package edu.ramapo.ktavadze.unipal;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Event class.
 */

@IgnoreExtraProperties
public class Event {

    private String name;
    private String date;
    private String time;
    private String uid;

    public Event() {
        // Default constructor required for calls to DataSnapshot.getValue(Event.class)
    }

    public Event(String name, String date, String time, String uid) {
        this.name = name;
        this.date = date;
        this.time = time;
        this.uid = uid;
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

    public String getDate() {
        return this.date;
    }

    public String getTime() {
        return this.time;
    }

    public String getUid() {
        return this.uid;
    }

}
