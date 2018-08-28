package edu.ramapo.ktavadze.unipal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
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

    private MenuItem mEditIcon;

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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_school, menu);

        mEditIcon = menu.findItem(R.id.action_edit_school);

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_edit_school:
                startEditing();
                return true;
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

    public void startEditing() {
        final TextView school_name_text = findViewById(R.id.school_name_text);
        final EditText school_name_edit = findViewById(R.id.school_name_edit);
        final TextView school_year_text = findViewById(R.id.school_year_text);
        final EditText school_year_edit = findViewById(R.id.school_year_edit);
        final TextView school_major_text = findViewById(R.id.school_major_text);
        final EditText school_major_edit = findViewById(R.id.school_major_edit);
        final TextView school_minor_text = findViewById(R.id.school_minor_text);
        final EditText school_minor_edit = findViewById(R.id.school_minor_edit);
        final Button delete_school_button = findViewById(R.id.delete_school_button);
        final Button cancel_school_button = findViewById(R.id.cancel_school_button);
        final Button update_school_button = findViewById(R.id.update_school_button);

        // Update UI
        mEditIcon.setVisible(false);
        school_name_text.setVisibility(View.GONE);
        school_year_text.setVisibility(View.GONE);
        school_major_text.setVisibility(View.GONE);
        school_minor_text.setVisibility(View.GONE);
        delete_school_button.setVisibility(View.GONE);
        school_name_edit.setVisibility(View.VISIBLE);
        school_year_edit.setVisibility(View.VISIBLE);
        school_major_edit.setVisibility(View.VISIBLE);
        school_minor_edit.setVisibility(View.VISIBLE);
        cancel_school_button.setVisibility(View.VISIBLE);
        update_school_button.setVisibility(View.VISIBLE);

        // Preview current info
        school_name_edit.setText(mSchool.getName());
        school_year_edit.setText(mSchool.getYear());
        school_major_edit.setText(mSchool.getMajor());
        school_minor_edit.setText(mSchool.getMinor());

        final School newSchool = new School();

        // Cancel
        cancel_school_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Update UI
                mEditIcon.setVisible(true);
                school_name_edit.setVisibility(View.GONE);
                school_year_edit.setVisibility(View.GONE);
                school_major_edit.setVisibility(View.GONE);
                school_minor_edit.setVisibility(View.GONE);
                cancel_school_button.setVisibility(View.GONE);
                update_school_button.setVisibility(View.GONE);
                school_name_text.setVisibility(View.VISIBLE);
                school_year_text.setVisibility(View.VISIBLE);
                school_major_text.setVisibility(View.VISIBLE);
                school_minor_text.setVisibility(View.VISIBLE);
                delete_school_button.setVisibility(View.VISIBLE);

                // Clear listeners
                cancel_school_button.setOnClickListener(null);
                update_school_button.setOnClickListener(null);

                // Hide keyboard
                InputMethodManager imm = (InputMethodManager)getSystemService(SchoolActivity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);

                displaySchoolData();
            }
        });

        // Update
        update_school_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Update UI
                mEditIcon.setVisible(true);
                school_name_edit.setVisibility(View.GONE);
                school_year_edit.setVisibility(View.GONE);
                school_major_edit.setVisibility(View.GONE);
                school_minor_edit.setVisibility(View.GONE);
                cancel_school_button.setVisibility(View.GONE);
                update_school_button.setVisibility(View.GONE);
                school_name_text.setVisibility(View.VISIBLE);
                school_year_text.setVisibility(View.VISIBLE);
                school_major_text.setVisibility(View.VISIBLE);
                school_minor_text.setVisibility(View.VISIBLE);
                delete_school_button.setVisibility(View.VISIBLE);

                // Clear listeners
                cancel_school_button.setOnClickListener(null);
                update_school_button.setOnClickListener(null);

                // Hide keyboard
                InputMethodManager imm = (InputMethodManager)getSystemService(SchoolActivity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);

                // Set name
                String name = school_name_edit.getText().toString().trim();
                if (name.isEmpty()) {
                    newSchool.setName(mSchool.getName());
                }
                else {
                    newSchool.setName(name);
                }

                // Set year
                String year = school_year_edit.getText().toString().trim();
                if (year.isEmpty()) {
                    newSchool.setYear(mSchool.getYear());
                }
                else {
                    newSchool.setYear(year);
                }

                // Set major
                String major = school_major_edit.getText().toString().trim();
                if (major.isEmpty()) {
                    newSchool.setMajor(mSchool.getMajor());
                }
                else {
                    newSchool.setMajor(major);
                }

                // Set minor
                String minor = school_minor_edit.getText().toString().trim();
                if (minor.isEmpty()) {
                    newSchool.setMinor(mSchool.getMinor());
                }
                else {
                    newSchool.setMinor(minor);
                }

                // Set uid
                newSchool.setUid(mSchool.getUid());

                // Update school
                mSchool = newSchool;

                displaySchoolData();

                // Update school in DB
                mData.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mData.setValue(mSchool);

                        Log.d(TAG, "onDataChange: School updated: " + mSchool.getName());
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
