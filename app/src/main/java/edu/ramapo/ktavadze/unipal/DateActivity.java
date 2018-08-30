package edu.ramapo.ktavadze.unipal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

public class DateActivity extends AppCompatActivity {
    private static final String TAG = "DateActivity";

    private String mDate;

    private Database mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date);

        getIntentData();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(mDate);

        mDatabase = new Database(this);
        mDatabase.addEventsListener(mDate);

        initRecycler();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mDatabase.removeEventsListener();
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

    public void initRecycler() {
        // Init recycler
        final RecyclerView events_recycler = findViewById(R.id.date_events_recycler);
        events_recycler.setAdapter(mDatabase.eventsAdapter);
        events_recycler.setLayoutManager(new LinearLayoutManager(this));
    }
}
