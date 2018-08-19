package edu.ramapo.ktavadze.unipal;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
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

    private Event mEvent;

    private DatabaseReference mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        getIntentData();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(mEvent.getName());

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

                        finish();

                        Log.d(TAG, "onDataChange: Event deleted");
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                    }
                });
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
            default:
                // Invoke superclass to handle unrecognized action.
                return super.onOptionsItemSelected(item);
        }
    }

    public void getIntentData() {
        final Intent intent = getIntent();
        if (intent.hasExtra("name") && intent.hasExtra("date") && intent.hasExtra("time") && intent.hasExtra("uid")) {
            String name = intent.getStringExtra("name");
            String date = intent.getStringExtra("date");
            String time = intent.getStringExtra("time");
            String uid = intent.getStringExtra("uid");
            mEvent = new Event(name, date, time, uid);

            mData = FirebaseDatabase.getInstance().getReference().child("events").child(User.getUid()).child(uid);

            Log.d(TAG, "getIntentData: Intent received");
        }
        else {
            finish();

            Log.d(TAG, "getIntentData: Intent rejected");
        }
    }

    public void displayEventData() {
        final TextView event_name_text = findViewById(R.id.event_name_text);
        final TextView event_date_text = findViewById(R.id.event_date_text);
        final TextView event_time_text = findViewById(R.id.event_time_text);
        final TextView event_uid_text = findViewById(R.id.event_uid_text);

        event_name_text.setText(mEvent.getName());
        event_date_text.setText(mEvent.getDate());
        event_time_text.setText(mEvent.getTime());
        event_uid_text.setText(mEvent.getUid());
    }
}
