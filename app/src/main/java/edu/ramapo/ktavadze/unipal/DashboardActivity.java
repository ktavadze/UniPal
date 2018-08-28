package edu.ramapo.ktavadze.unipal;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class DashboardActivity extends AppCompatActivity implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {
    private static final String TAG = "DashboardActivity";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mCurrentUser;

    private DatabaseReference mEventsData;
    private DatabaseReference mCoursesData;

    private ChildEventListener mEventsListener;

    private ArrayList<Event> mEvents;
    private ArrayList<String> mCourseNames;

    private EventsRecyclerAdapter mEventsAdapter;
    private ArrayAdapter<String> mCourseNamesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Dashboard");

        addAuthListener();

        // Initialize
        mCurrentUser = mAuth.getCurrentUser();
        if (mCurrentUser != null) {
            initUser();

            addEventsListener();

            getCourseNames();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        removeAuthListener();

        removeEventsListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_new_event:
                actionNewEvent();
                return true;
            case R.id.action_user:
                startActivity(new Intent(DashboardActivity.this, UserActivity.class));
                return true;
            case R.id.action_schools:
                startActivity(new Intent(DashboardActivity.this, SchoolsActivity.class));
                return true;
            case R.id.action_courses:
                startActivity(new Intent(DashboardActivity.this, CoursesActivity.class));
                return true;
            case R.id.action_calendar:
                startActivity(new Intent(DashboardActivity.this, CalendarActivity.class));
                return true;
            case R.id.action_sign_out:
                mAuth.signOut();
                return true;
            default:
                // Invoke superclass to handle unrecognized action.
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, final int position) {
        // Backup removed event
        final Event event = mEvents.get(position);

        // Remove event
        mEventsAdapter.removeEvent(position);

        // Show undo snack bar
        ConstraintLayout dashboard = findViewById(R.id.dashboard);
        Snackbar snackbar = Snackbar.make(dashboard, "Event removed", Snackbar.LENGTH_SHORT);
        snackbar.setAction("UNDO", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Restore event
                mEventsAdapter.restoreEvent(event, position);
            }
        });
        snackbar.show();
    }

    public void addAuthListener() {
        // Add auth state listener
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    startActivity(new Intent(DashboardActivity.this, MainActivity.class));
                }
            }
        };
        mAuth.addAuthStateListener(mAuthListener);

        Log.d(TAG, "addAuthListener: Listener added");
    }

    public void removeAuthListener() {
        // remove auth listener
        mAuth.removeAuthStateListener(mAuthListener);

        Log.d(TAG, "removeAuthListener: Listener removed");
    }

    public void initUser() {
        // Get Google provider data
        UserInfo profile = mCurrentUser.getProviderData().get(1);

        final String displayName = profile.getDisplayName();
        final String email = profile.getEmail();
        final String uid = profile.getUid();

        User.init(displayName, email, uid);
    }

    public void addEventsListener() {
        // Init events
        mEvents = new ArrayList<>();
        mEventsAdapter = new EventsRecyclerAdapter(this, mEvents);
        final RecyclerView events_recycler = findViewById(R.id.events_recycler);
        events_recycler.setAdapter(mEventsAdapter);
        events_recycler.setItemAnimator(new DefaultItemAnimator());
        events_recycler.setLayoutManager(new LinearLayoutManager(this));

        // Add events listener
        mEventsData = FirebaseDatabase.getInstance().getReference().child("events").child(User.getUid());
        mEventsListener = mEventsData.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Event event = dataSnapshot.getValue(Event.class);
                mEvents.add(event);

                mEventsAdapter.notifyDataSetChanged();

                Log.d(TAG, "onChildAdded: Event read: " + event.getName());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Event event = dataSnapshot.getValue(Event.class);
                int index = mEvents.indexOf(event);
                mEvents.set(index, event);

                mEventsAdapter.notifyDataSetChanged();

                Log.d(TAG, "onChildChanged: Event updated: " + event.getName());
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Event event = dataSnapshot.getValue(Event.class);
                mEvents.remove(event);

                mEventsAdapter.notifyDataSetChanged();

                Log.d(TAG, "onChildRemoved: Event removed: " + event.getName());
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

        // Attach item touch helper
        ItemTouchHelper.SimpleCallback recyclerTouchHelperCallback =
                new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(recyclerTouchHelperCallback).attachToRecyclerView(events_recycler);
    }

    public void removeEventsListener() {
        // Remove events listener
        mEventsData.removeEventListener(mEventsListener);

        Log.d(TAG, "removeEventsListener: Listener removed");
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

    public void actionNewEvent() {
        final Event newEvent = new Event();
        final Calendar cal = Calendar.getInstance();

        // Build new event dialog
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_event_new, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle(R.string.title_new_event);

        // Define fields
        final EditText event_name_edit = dialogView.findViewById(R.id.event_name_edit);
        final Spinner event_type_spinner = dialogView.findViewById(R.id.event_type_spinner);
        final Spinner event_course_spinner = dialogView.findViewById(R.id.event_course_spinner);
        final TextView event_date_pick = dialogView.findViewById(R.id.event_date_pick);
        final TextView event_time_pick = dialogView.findViewById(R.id.event_time_pick);

        // Type
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

            newEvent.setCourseName("Undefined");
        }
        else {
            // Set adapter
            event_course_spinner.setAdapter(mCourseNamesAdapter);

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
        event_date_pick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Build date picker
                DatePickerDialog date_picker = new DatePickerDialog(DashboardActivity.this,
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
                                event_date_pick.setText(date);

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
        event_time_pick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Build time picker
                TimePickerDialog time_picker = new TimePickerDialog(DashboardActivity.this,
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
                                event_time_pick.setText(time);

                                Log.d(TAG, "onTimeSet: Time set: " + time);
                            }
                        },
                        cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true);

                // Show time picker
                time_picker.getWindow();
                time_picker.show();
            }
        });

        // Define responses
        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Write event to DB
                mEventsData.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Set name
                        String name = event_name_edit.getText().toString().trim();
                        if (name.isEmpty()) {
                            newEvent.setName("New event");
                        }
                        else {
                            newEvent.setName(name);
                        }

                        // Set uid
                        String uid = mEventsData.push().getKey();
                        newEvent.setUid(uid);

                        // Add event
                        mEventsData.child(uid).setValue(newEvent);

                        Log.d(TAG, "onDataChange: Event added: " + name);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                    }
                });
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // TODO: Cancel
            }
        });

        // Show new event dialog
        AlertDialog eventDialog = dialogBuilder.create();
        eventDialog.show();
    }
}
