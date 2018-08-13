package edu.ramapo.ktavadze.unipal;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * School class.
 */

@IgnoreExtraProperties
public class School {

    private String name;
    private String uid;

    public School() {
        // Default constructor required for calls to DataSnapshot.getValue(School.class)
    }

    public School(String name, String uid) {
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
