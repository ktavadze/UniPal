package edu.ramapo.ktavadze.unipal;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class EventActivity extends AppCompatActivity {

    private static final String TAG = "EventActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Event");

        Intent intent = getIntent();
        String name = "EVENT_NAME";
        String uid = "EVENT_UID";
        if (intent.hasExtra("name") && intent.hasExtra("uid")) {
            name = intent.getStringExtra("name");
            uid = intent.getStringExtra("uid");
        }

        TextView event_name_text = findViewById(R.id.event_name_text);
        TextView event_uid_text = findViewById(R.id.event_uid_text);

        event_name_text.setText(name);
        event_uid_text.setText(uid);
    }
}
