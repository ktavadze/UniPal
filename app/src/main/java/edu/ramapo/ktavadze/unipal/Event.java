package edu.ramapo.ktavadze.unipal;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Event class.
 */

@IgnoreExtraProperties
public class Event {

    private String name;
    private String uid;

    public Event() {
        // Default constructor required for calls to DataSnapshot.getValue(Event.class)
    }

    public Event(String name, String uid) {
        this.name = name;
        this.uid = uid;
    }

    public String getName() {
        return this.name;
    }

    public String getUid() {
        return this.uid;
    }

}
