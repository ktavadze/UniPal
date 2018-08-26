package edu.ramapo.ktavadze.unipal;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Course class.
 */

@IgnoreExtraProperties
public class Course {
    private String name;
    private String department;
    private String number;
    private String section;
    private String schoolName;
    private String uid;

    public Course() {
        // Default constructor required for calls to DataSnapshot.getValue(Course.class)
    }

    public Course(String name, String department, String number, String section, String schoolName, String uid) {
        this.name = name;
        this.department = department;
        this.number = number;
        this.section = section;
        this.schoolName = schoolName;
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

    public String getDepartment() {
        return this.department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getNumber() {
        return this.number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getSection() {
        return this.section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getSchoolName() {
        return this.schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getUid() {
        return this.uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
