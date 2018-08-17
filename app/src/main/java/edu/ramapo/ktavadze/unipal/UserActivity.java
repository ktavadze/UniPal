package edu.ramapo.ktavadze.unipal;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserActivity extends AppCompatActivity {

    private static final String TAG = "UserActivity";

    private DatabaseReference mUserData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("User");

        mUserData = FirebaseDatabase.getInstance().getReference().child("users").child(User.getUid());

        // Show static user data
        final TextView user_name_text = findViewById(R.id.user_name_text);
        final TextView user_email_text = findViewById(R.id.user_email_text);
        final TextView user_uid_text = findViewById(R.id.user_uid_text);
        user_name_text.setText(User.getDisplayName());
        user_email_text.setText(User.getEmail());
        user_uid_text.setText(User.getUid());

        // Read user data from DB
        final Button get_user_button = findViewById(R.id.get_user_button);
        get_user_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUserData.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String displayName = dataSnapshot.child("displayName").getValue(String.class);
                        String email = dataSnapshot.child("email").getValue(String.class);
                        String uid = dataSnapshot.child("uid").getValue(String.class);

                        user_name_text.setText(displayName);
                        user_email_text.setText(email);
                        user_uid_text.setText(uid);

                        Log.d(TAG, "onDataChange: User read");
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                    }
                });
            }
        });
    }
}
