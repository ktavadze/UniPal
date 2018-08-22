package edu.ramapo.ktavadze.unipal;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ValueEventListener;

/**
 * User class.
 */

@IgnoreExtraProperties
public class User {
    private static final String TAG = "User";

    private static String displayName;
    private static String email;
    private static String uid;

    private static DatabaseReference userData;

    public static void init(String a_displayName, String a_email, String a_uid) {
        displayName = a_displayName;
        email = a_email;
        uid = a_uid;

        userData = FirebaseDatabase.getInstance().getReference().child("users").child(uid);

        registerUser();
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

    private static void registerUser() {
        userData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Log.d(TAG, "onDataChange: User exists");
                }
                else {
                    userData.child("displayName").setValue(displayName);
                    userData.child("email").setValue(email);
                    userData.child("uid").setValue(uid);

                    Log.d(TAG, "onDataChange: User registered");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });
    }
}
