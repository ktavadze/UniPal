package edu.ramapo.ktavadze.unipal;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class EventActivity extends AppCompatActivity {
    private static final String TAG = "EventActivity";

    private Event mEvent;

    private DatabaseReference mData;
    private DatabaseReference mCoursesData;

    private ArrayList<String> mCourseNames;

    private ArrayAdapter<String> mCourseNamesAdapter;

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

        getCourseNames();

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

        // Toggle event in DB
        final Button toggle_event_button = findViewById(R.id.toggle_event_button);
        toggle_event_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mData.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mEvent.toggleComplete();

                        mData.child("complete").setValue(mEvent.isComplete());

                        displayEventData();

                        Log.d(TAG, "onDataChange: Event toggled: " + mEvent.getName());
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
        if (intent.hasExtra("name") && intent.hasExtra("type") && intent.hasExtra("courseName")
                && intent.hasExtra("date") && intent.hasExtra("time") && intent.hasExtra("uid")
                && intent.hasExtra("complete")) {
            String name = intent.getStringExtra("name");
            String type = intent.getStringExtra("type");
            String courseName = intent.getStringExtra("courseName");
            String date = intent.getStringExtra("date");
            String time = intent.getStringExtra("time");
            String uid = intent.getStringExtra("uid");
            boolean complete = intent.getBooleanExtra("complete", false);
            mEvent = new Event(name, type, courseName, date, time, uid, complete);

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
        final TextView event_type_text = findViewById(R.id.event_type_text);
        final TextView event_course_text = findViewById(R.id.event_course_text);
        final TextView event_date_text = findViewById(R.id.event_date_text);
        final TextView event_time_text = findViewById(R.id.event_time_text);
        final TextView event_status_text = findViewById(R.id.event_status_text);
        final ScrollView event_scroll = findViewById(R.id.event_scroll);
        final Button toggle_event_button = findViewById(R.id.toggle_event_button);

        event_name_text.setText(mEvent.getName());
        event_type_text.setText(mEvent.getType());
        event_course_text.setText(mEvent.getCourseName());
        event_date_text.setText(mEvent.getDate());
        event_time_text.setText(mEvent.getTime());

        // Display status
        String status = "Incomplete";
        if (mEvent.isComplete()) {
            status = "Complete";
        }
        event_status_text.setText(status);

        final int backgroundYellow = ContextCompat.getColor(EventActivity.this, R.color.colorSecondary);
        final int backgroundGreen = ContextCompat.getColor(EventActivity.this, R.color.colorTertiary);
        if (mEvent.isComplete()) {
            event_scroll.setBackgroundColor(backgroundGreen);
            toggle_event_button.setBackgroundColor(backgroundYellow);
        }
        else {
            event_scroll.setBackgroundColor(backgroundYellow);
            toggle_event_button.setBackgroundColor(backgroundGreen);
        }
    }

    public void getCourseNames() {
        // Init courses
        mCourseNames = new ArrayList<>();
        mCourseNamesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, mCourseNames);

        // Read courses from DB
        mCoursesData = FirebaseDatabase.getInstance().getReference().child("courses").child(User.getUid());
        mCoursesData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot courseSnapshot: dataSnapshot.getChildren()) {
                    String courseName = courseSnapshot.child("name").getValue(String.class);
                    mCourseNames.add(courseName);

                    mCourseNamesAdapter.notifyDataSetChanged();

                    Log.d(TAG, "onDataChange: Course read: " + courseName);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });
    }

    public void startEditing() {
        final TextView event_name_text = findViewById(R.id.event_name_text);
        final EditText event_name_edit = findViewById(R.id.event_name_edit);
        final TextView event_type_text = findViewById(R.id.event_type_text);
        final Spinner event_type_spinner = findViewById(R.id.event_type_spinner);
        final TextView event_course_text = findViewById(R.id.event_course_text);
        final Spinner event_course_spinner = findViewById(R.id.event_course_spinner);
        final TextView event_date_text = findViewById(R.id.event_date_text);
        final TextView event_time_text = findViewById(R.id.event_time_text);
        final TextView event_status_text = findViewById(R.id.event_status_text);
        final Button delete_event_button = findViewById(R.id.delete_event_button);
        final Button toggle_event_button = findViewById(R.id.toggle_event_button);
        final Button cancel_event_button = findViewById(R.id.cancel_event_button);
        final Button update_event_button = findViewById(R.id.update_event_button);

        // Update UI
        mEditIcon.setVisible(false);
        event_name_text.setVisibility(View.GONE);
        event_type_text.setVisibility(View.GONE);
        event_course_text.setVisibility(View.GONE);
        event_status_text.setVisibility(View.GONE);
        delete_event_button.setVisibility(View.GONE);
        toggle_event_button.setVisibility(View.GONE);
        event_name_edit.setVisibility(View.VISIBLE);
        event_type_spinner.setVisibility(View.VISIBLE);
        event_course_spinner.setVisibility(View.VISIBLE);
        cancel_event_button.setVisibility(View.VISIBLE);
        update_event_button.setVisibility(View.VISIBLE);

        // Preview current name
        event_name_edit.setText(mEvent.getName());

        final Event newEvent = new Event();
        final Calendar cal = Calendar.getInstance();

        // Type
        newEvent.setType(mEvent.getType());
        int index = Arrays.asList("Assignment", "Exam", "Other", "Presentation", "Project", "Quiz",
                "Report", "Test").indexOf(mEvent.getType());
        event_type_spinner.setSelection(index);
        event_type_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String type = parent.getItemAtPosition(position).toString();

                // Set type
                newEvent.setType(type);

                Log.d(TAG, "onItemSelected: Type selected: " + type);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Course
        if (mCourseNames.isEmpty()) {
            event_course_spinner.setVisibility(View.GONE);

            newEvent.setCourseName(mEvent.getCourseName());
        }
        else {
            // Set adapter
            event_course_spinner.setAdapter(mCourseNamesAdapter);

            // Set current selection
            index = mCourseNames.indexOf(mEvent.getCourseName());
            event_course_spinner.setSelection(index);

            // Set listener
            event_course_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String courseName = parent.getItemAtPosition(position).toString();

                    // Set course
                    newEvent.setCourseName(courseName);

                    Log.d(TAG, "onItemSelected: Course selected: " + courseName);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }

        // Date
        newEvent.setDate(mEvent.getDate());
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
        newEvent.setTime(mEvent.getTime());
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
                event_type_spinner.setVisibility(View.GONE);
                event_course_spinner.setVisibility(View.GONE);
                cancel_event_button.setVisibility(View.GONE);
                update_event_button.setVisibility(View.GONE);
                event_name_text.setVisibility(View.VISIBLE);
                event_type_text.setVisibility(View.VISIBLE);
                event_course_text.setVisibility(View.VISIBLE);
                event_status_text.setVisibility(View.VISIBLE);
                delete_event_button.setVisibility(View.VISIBLE);
                toggle_event_button.setVisibility(View.VISIBLE);

                // Clear listeners
                event_date_text.setOnClickListener(null);
                event_time_text.setOnClickListener(null);
                cancel_event_button.setOnClickListener(null);
                update_event_button.setOnClickListener(null);

                // Hide keyboard
                InputMethodManager imm = (InputMethodManager)getSystemService(EventActivity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);

                displayEventData();
            }
        });

        // Update
        update_event_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Update UI
                mEditIcon.setVisible(true);
                event_name_edit.setVisibility(View.GONE);
                event_type_spinner.setVisibility(View.GONE);
                event_course_spinner.setVisibility(View.GONE);
                cancel_event_button.setVisibility(View.GONE);
                update_event_button.setVisibility(View.GONE);
                event_name_text.setVisibility(View.VISIBLE);
                event_type_text.setVisibility(View.VISIBLE);
                event_course_text.setVisibility(View.VISIBLE);
                event_status_text.setVisibility(View.VISIBLE);
                delete_event_button.setVisibility(View.VISIBLE);
                toggle_event_button.setVisibility(View.VISIBLE);

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

                // Set status
                newEvent.setComplete(mEvent.isComplete());

                // Set uid
                newEvent.setUid(mEvent.getUid());

                // Update event
                mEvent = newEvent;

                displayEventData();

                // Update event in DB
                mData.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mData.setValue(mEvent);

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
