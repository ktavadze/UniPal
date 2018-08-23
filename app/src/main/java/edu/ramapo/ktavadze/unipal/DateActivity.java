package edu.ramapo.ktavadze.unipal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
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

    private DatabaseReference mEventsData;
    private ChildEventListener mEventsListener;

    private ArrayList<Event> mEvents;
    private EventsRecyclerAdapter mEventsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date);

        getIntentData();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(mDate);

        addEventsListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        removeEventsListener();
    }

    public void getIntentData() {
        final Intent intent = getIntent();
        if (intent.hasExtra("date")) {
            mDate = intent.getStringExtra("date");

            Log.d(TAG, "getIntentData: Intent accepted");
        }
        else {
            finish();

            Log.d(TAG, "getIntentData: Intent rejected");
        }
    }

    public void addEventsListener() {
        // Init events
        mEvents = new ArrayList<>();
        mEventsAdapter = new EventsRecyclerAdapter(this, mEvents);
        final RecyclerView events_recycler = findViewById(R.id.date_events_recycler);
        events_recycler.setAdapter(mEventsAdapter);
        events_recycler.setLayoutManager(new LinearLayoutManager(this));

        // Add events listener
        mEventsData = FirebaseDatabase.getInstance().getReference().child("events").child(User.getUid());
        mEventsListener = mEventsData.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Event event = dataSnapshot.getValue(Event.class);
                if (event.getDate().equals(mDate)) {
                    mEvents.add(event);

                    mEventsAdapter.notifyDataSetChanged();

                    Log.d(TAG, "onChildAdded: Event read: " + event.getName());
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Event event = dataSnapshot.getValue(Event.class);
                if (!mEvents.contains(event)) {
                    if (event.getDate().equals(mDate)) {
                        mEvents.add(event);
                    }
                }
                else {
                    if (!event.getDate().equals(mDate)) {
                        mEvents.remove(event);
                    }
                }

                mEventsAdapter.notifyDataSetChanged();

                Log.d(TAG, "onChildChanged: Event updated " + event.getName());
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Event event = dataSnapshot.getValue(Event.class);
                if (event.getDate().equals(mDate)) {
                    mEvents.remove(event);

                    mEventsAdapter.notifyDataSetChanged();

                    Log.d(TAG, "onChildRemoved: Event removed: " + event.getName());
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });

        Log.d(TAG, "addEventsListener: Listener added");
    }

    public void removeEventsListener() {
        // Remove events listener
        mEventsData.removeEventListener(mEventsListener);

        Log.d(TAG, "removeEventsListener: Listener removed");
    }
}
