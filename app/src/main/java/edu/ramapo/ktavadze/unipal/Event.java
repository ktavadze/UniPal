package edu.ramapo.ktavadze.unipal;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Event class.
 */

@IgnoreExtraProperties
public class Event {

    private String name;
    private String date;
    private String uid;

    public Event() {
        // Default constructor required for calls to DataSnapshot.getValue(Event.class)
    }

    public Event(String name, String date, String uid) {
        this.name = name;
        this.date = date;
        this.uid = uid;
    }

    public String getName() {
        return this.name;
    }

    public String getDate() {
        return this.date;
    }

    public String getUid() {
        return this.uid;
    }

}
