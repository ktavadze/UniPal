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

    @Override
    public boolean equals(Object obj) {
        School school = (School) obj;
        if (uid.equals(school.getUid())) {
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

    public String getUid() {
        return this.uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
