package edu.ramapo.ktavadze.unipal;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class DateActivity extends AppCompatActivity {

    private static final String TAG = "DateActivity";

    private String mDate;

    private DatabaseReference mEventData;

    private ArrayList<Event> mEvents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date);

        getIntentData();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(mDate);
    }

    @Override
    protected void onResume() {
        super.onResume();

        getEventData();
    }

    public void getIntentData() {
        final Intent intent = getIntent();
        if (intent.hasExtra("date")) {
            mDate = intent.getStringExtra("date");

            mEventData = FirebaseDatabase.getInstance().getReference().child("events").child(User.getUid());

            Log.d(TAG, "getIntentData: Intent accepted");
        }
        else {
            finish();

            Log.d(TAG, "getIntentData: Intent rejected");
        }
    }

    public void getEventData() {
        mEvents = new ArrayList<>();
        final EventsRecyclerAdapter eventsAdapter = new EventsRecyclerAdapter(this, mEvents);
        final RecyclerView events_recycler = findViewById(R.id.date_events_recycler);
        events_recycler.setAdapter(eventsAdapter);
        events_recycler.setLayoutManager(new LinearLayoutManager(this));
        mEventData.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Event event = dataSnapshot.getValue(Event.class);
                if (event.getDate().equals(mDate)) {
                    mEvents.add(event);

                    eventsAdapter.notifyDataSetChanged();

                    Log.d(TAG, "onChildAdded: Event read");
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });
    }
}
