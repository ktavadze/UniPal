package edu.ramapo.ktavadze.unipal;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * User class.
 */

@IgnoreExtraProperties
public class User {

    private static String displayName;
    private static String email;
    private static String uid;

    public static void setUser(String a_displayName, String a_email, String a_uid) {
        displayName = a_displayName;
        email = a_email;
        uid = a_uid;
    }

    public static String getDisplayName() {
        return displayName;
    }

    public static String getEmail() {
        return email;
    }

    public static String getUid() {
        return uid;
    }

}
