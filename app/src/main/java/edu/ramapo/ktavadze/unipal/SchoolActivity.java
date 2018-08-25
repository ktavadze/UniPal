package edu.ramapo.ktavadze.unipal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
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

public class SchoolActivity extends AppCompatActivity {
    private static final String TAG = "SchoolActivity";

    private School mSchool;

    private DatabaseReference mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school);

        getIntentData();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(mSchool.getName());

        displaySchoolData();

        // Delete school from DB
        final Button delete_school_button = findViewById(R.id.delete_school_button);
        delete_school_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mData.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mData.removeValue();

                        finish();

                        Log.d(TAG, "onDataChange: School deleted: " + mSchool.getName());
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
        if (intent.hasExtra("name") && intent.hasExtra("year") && intent.hasExtra("major")
                && intent.hasExtra("minor") && intent.hasExtra("uid")) {
            String name = intent.getStringExtra("name");
            String year = intent.getStringExtra("year");
            String major = intent.getStringExtra("major");
            String minor = intent.getStringExtra("minor");
            String uid = intent.getStringExtra("uid");
            mSchool = new School(name, year, major, minor, uid);

            mData = FirebaseDatabase.getInstance().getReference().child("schools").child(User.getUid()).child(uid);

            Log.d(TAG, "getIntentData: Intent accepted");
        }
        else {
            finish();

            Log.d(TAG, "getIntentData: Intent rejected");
        }
    }

    public void displaySchoolData() {
        final TextView school_name_text = findViewById(R.id.school_name_text);
        final TextView school_year_text = findViewById(R.id.school_year_text);
        final TextView school_major_text = findViewById(R.id.school_major_text);
        final TextView school_minor_text = findViewById(R.id.school_minor_text);

        school_name_text.setText(mSchool.getName());
        school_year_text.setText(mSchool.getYear());
        school_major_text.setText(mSchool.getMajor());
        school_minor_text.setText(mSchool.getMinor());
    }
}
