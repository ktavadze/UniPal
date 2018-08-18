package edu.ramapo.ktavadze.unipal;

import android.content.Intent;
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

public class EventActivity extends AppCompatActivity {

    private static final String TAG = "EventActivity";

    private String mName;
    private String mDate;
    private String mUid;
    private DatabaseReference mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Event");

        getEventData();

        displayEventData();

        // Delete event from DB
        final Button delete_event_button = findViewById(R.id.delete_event_button);
        delete_event_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mData.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mData.removeValue();

                        Log.d(TAG, "onDataChange: Event deleted");

                        startActivity(new Intent(EventActivity.this, DashboardActivity.class));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                    }
                });
            }
        });
    }

    public void getEventData() {
        final Intent intent = getIntent();
        if (intent.hasExtra("name") && intent.hasExtra("date") && intent.hasExtra("uid")) {
            mName = intent.getStringExtra("name");
            mDate = intent.getStringExtra("date");
            mUid = intent.getStringExtra("uid");

            mData = FirebaseDatabase.getInstance().getReference().child("events").child(User.getUid()).child(mUid);
        }
    }

    public void displayEventData() {
        final TextView event_name_text = findViewById(R.id.event_name_text);
        final TextView event_date_text = findViewById(R.id.event_date_text);
        final TextView event_uid_text = findViewById(R.id.event_uid_text);

        event_name_text.setText(mName);
        event_date_text.setText(mDate);
        event_uid_text.setText(mUid);
    }
}
