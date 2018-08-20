package edu.ramapo.ktavadze.unipal;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Course class.
 */

@IgnoreExtraProperties
public class Course {

    private String name;
    private String uid;

    public Course() {
        // Default constructor required for calls to DataSnapshot.getValue(Course.class)
    }

    public Course(String name, String uid) {
        this.name = name;
        this.uid = uid;
    }

    @Override
    public boolean equals(Object obj) {
        Course course = (Course) obj;
        if (uid.equals(course.getUid())) {
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
