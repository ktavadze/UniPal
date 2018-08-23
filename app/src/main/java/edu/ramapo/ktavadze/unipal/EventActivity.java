package edu.ramapo.ktavadze.unipal;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class EventActivity extends AppCompatActivity {
    private static final String TAG = "EventActivity";

    private Event mEvent;

    private DatabaseReference mData;

    private MenuItem mEditIcon;

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

                        Log.d(TAG, "onDataChange: Event deleted: " + mEvent.getName());
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
        inflater.inflate(R.menu.menu_event, menu);

        mEditIcon = menu.findItem(R.id.action_edit_event);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_edit_event:
                startEditing();
                return true;
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

            Log.d(TAG, "getIntentData: Intent accepted");
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

    public void startEditing() {
        final TextView event_name_text = findViewById(R.id.event_name_text);
        final EditText event_name_edit = findViewById(R.id.event_name_edit);
        final TextView event_date_text = findViewById(R.id.event_date_text);
        final TextView event_time_text = findViewById(R.id.event_time_text);
        final Button delete_event_button = findViewById(R.id.delete_event_button);
        final Button update_event_button = findViewById(R.id.update_event_button);
        final Button cancel_event_button = findViewById(R.id.cancel_event_button);

        // Update UI
        mEditIcon.setVisible(false);
        event_name_text.setVisibility(View.GONE);
        delete_event_button.setVisibility(View.GONE);
        event_name_edit.setVisibility(View.VISIBLE);
        cancel_event_button.setVisibility(View.VISIBLE);
        update_event_button.setVisibility(View.VISIBLE);

        final Event newEvent = new Event();
        final Calendar cal = Calendar.getInstance();

        // Name
        event_name_edit.setText(mEvent.getName());

        // Date
        event_date_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Build date picker
                DatePickerDialog date_picker = new DatePickerDialog(EventActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int day) {
                                // Format date
                                month++;
                                String date;
                                if (month < 10 && day < 10) {
                                    date = "0" + month + "/0" + day + "/" + year;
                                }
                                else if (month < 10) {
                                    date = "0" + month + "/" + day + "/" + year;
                                }
                                else if (day < 10) {
                                    date = "" + month + "/0" + day + "/" + year;
                                }
                                else {
                                    date = "" + month + "/" + day + "/" + year;
                                }

                                // Set date
                                newEvent.setDate(date);

                                // Preview date
                                event_date_text.setText(date);

                                Log.d(TAG, "onDateSet: Date set: " + date);
                            }
                        },
                        cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));

                // Show date picker
                date_picker.getWindow();
                date_picker.show();
            }
        });

        // Time
        event_time_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Build time picker
                TimePickerDialog time_picker = new TimePickerDialog(EventActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hour, int minute) {
                                // Format time
                                String time;
                                if (hour < 10 && minute < 10) {
                                    time = "0" + hour + ":0" + minute;
                                }
                                else if (hour < 10) {
                                    time = "0" + hour + ":" + minute;
                                }
                                else if (minute < 10) {
                                    time = "" + hour + ":0" + minute;
                                }
                                else {
                                    time = "" + hour + ":" + minute;
                                }

                                // Set time
                                newEvent.setTime(time);

                                // Preview time
                                event_time_text.setText(time);

                                Log.d(TAG, "onTimeSet: Time set: " + time);
                            }
                        },
                        cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true);

                // Show time picker
                time_picker.getWindow();
                time_picker.show();
            }
        });

        // Cancel
        cancel_event_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Update UI
                mEditIcon.setVisible(true);
                event_name_edit.setVisibility(View.GONE);
                update_event_button.setVisibility(View.GONE);
                cancel_event_button.setVisibility(View.GONE);
                event_name_text.setVisibility(View.VISIBLE);
                delete_event_button.setVisibility(View.VISIBLE);

                // Clear listeners
                event_date_text.setOnClickListener(null);
                event_time_text.setOnClickListener(null);
                cancel_event_button.setOnClickListener(null);
                update_event_button.setOnClickListener(null);

                // Hide keyboard
                InputMethodManager imm = (InputMethodManager)getSystemService(EventActivity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
            }
        });

        // Update
        update_event_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Update UI
                mEditIcon.setVisible(true);
                event_name_edit.setVisibility(View.GONE);
                update_event_button.setVisibility(View.GONE);
                cancel_event_button.setVisibility(View.GONE);
                event_name_text.setVisibility(View.VISIBLE);
                delete_event_button.setVisibility(View.VISIBLE);

                // Clear listeners
                event_date_text.setOnClickListener(null);
                event_time_text.setOnClickListener(null);
                cancel_event_button.setOnClickListener(null);
                update_event_button.setOnClickListener(null);

                // Hide keyboard
                InputMethodManager imm = (InputMethodManager)getSystemService(EventActivity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);

                // Set name
                String name = event_name_edit.getText().toString().trim();
                if (name.isEmpty()) {
                    newEvent.setName(mEvent.getName());
                }
                else {
                    newEvent.setName(name);
                }

                // Set uid
                String uid = mEvent.getUid();
                newEvent.setUid(uid);

                mEvent = newEvent;

                displayEventData();

                // Update event in DB
                mData.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mData.setValue(newEvent);

                        Log.d(TAG, "onDataChange: Event updated: " + mEvent.getName());
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
