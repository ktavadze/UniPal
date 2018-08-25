package edu.ramapo.ktavadze.unipal;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * School class.
 */

@IgnoreExtraProperties
public class School {
    private String name;
    private String year;
    private String major;
    private String minor;
    private String uid;

    public School() {
        // Default constructor required for calls to DataSnapshot.getValue(School.class)
    }

    public School(String name, String year, String major, String minor, String uid) {
        this.name = name;
        this.year = year;
        this.major = major;
        this.minor = minor;
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

    public String getYear() {
        return this.year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMajor() {
        return this.major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getMinor() {
        return this.minor;
    }

    public void setMinor(String minor) {
        this.minor = minor;
    }

    public String getUid() {
        return this.uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
