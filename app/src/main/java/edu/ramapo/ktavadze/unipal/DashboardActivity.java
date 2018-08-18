package edu.ramapo.ktavadze.unipal;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

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

public class DashboardActivity extends AppCompatActivity {

    private static final String TAG = "DashboardActivity";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mCurrentUser;

    private DatabaseReference mEventsData;
    
    private ArrayList<Event> mEvents;
    private String mNewDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Dashboard");

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        if (mCurrentUser != null) {
            getUser();

            mEventsData = FirebaseDatabase.getInstance().getReference().child("events").child(User.getUid());
        }

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    startActivity(new Intent(DashboardActivity.this, MainActivity.class));
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onResume() {
        super.onResume();

        getEvents();
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

    public void getUser() {
        // Get Google provider data
        UserInfo profile = mCurrentUser.getProviderData().get(1);

        final String displayName = profile.getDisplayName();
        final String email = profile.getEmail();
        final String uid = profile.getUid();

        User.init(displayName, email, uid);
    }

    public void getEvents() {
        mEvents = new ArrayList<>();
        final EventsRecyclerAdapter eventsAdapter = new EventsRecyclerAdapter(this, mEvents);
        final RecyclerView events_recycler = findViewById(R.id.events_recycler);
        events_recycler.setAdapter(eventsAdapter);
        events_recycler.setLayoutManager(new LinearLayoutManager(this));
        mEventsData.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Event event = dataSnapshot.getValue(Event.class);
                if (!mEvents.contains(event)) {
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

    public void actionNewEvent() {
        // Build new event dialog
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_event_new, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle(R.string.title_new_event);

        // Build input fields
        final EditText event_name_edit = dialogView.findViewById(R.id.event_name_edit);
        final TextView event_date_pick = dialogView.findViewById(R.id.event_date_pick);
        event_date_pick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Build date picker
                Calendar cal = Calendar.getInstance();
                DatePickerDialog date_picker = new DatePickerDialog(DashboardActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                mNewDate = "" + year + "/" + month + "/" + dayOfMonth;

                                event_date_pick.setText(mNewDate);

                                Log.d(TAG, "onDateSet: Date set to " + mNewDate);
                            }
                        },
                        cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));

                // Show date picker
                date_picker.getWindow();
                date_picker.show();
            }
        });

        // Define responses
        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Write event to DB
                mEventsData.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String name = event_name_edit.getText().toString().trim();
                        String uid = mEventsData.push().getKey();

                        Event event = new Event(name, mNewDate, uid);
                        mEventsData.child(uid).setValue(event);

                        Log.d(TAG, "onDataChange: Event added");
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
        AlertDialog settingsDialog = dialogBuilder.create();
        settingsDialog.show();
    }
}
