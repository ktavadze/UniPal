package edu.ramapo.ktavadze.unipal;

import android.app.AlertDialog;
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
import android.widget.EditText;

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

public class DashboardActivity extends AppCompatActivity {

    private static final String TAG = "DashboardActivity";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mCurrentUser;

    private DatabaseReference mDatabase;
    private DatabaseReference mUserData;
    private DatabaseReference mSchoolData;
    private DatabaseReference mEventData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Dashboard");

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        if (mCurrentUser != null) {
            getUserData();

            getEventData();
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

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_new_event:
                respondNewEvent();
                return true;
            case R.id.action_user:
                startActivity(new Intent(DashboardActivity.this, UserActivity.class));
                return true;
            case R.id.action_schools:
                startActivity(new Intent(DashboardActivity.this, SchoolsActivity.class));
                return true;
            case R.id.action_sign_out:
                mAuth.signOut();
                return true;
            default:
                // Invoke superclass to handle unrecognized action.
                return super.onOptionsItemSelected(item);
        }
    }

    public void getUserData() {
        // Get Google provider data
        UserInfo profile = mCurrentUser.getProviderData().get(1);

        final String displayName = profile.getDisplayName();
        final String email = profile.getEmail();
        final String uid = profile.getUid();

        User.setUser(displayName, email, uid);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mUserData = mDatabase.child("users").child(uid);
        mSchoolData = mDatabase.child("schools").child(uid);
        mEventData = mDatabase.child("events").child(uid);

        mUserData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Log.d(TAG, "onDataChange: User exists");
                }
                else {
                    mUserData.child("displayName").setValue(displayName);
                    mUserData.child("email").setValue(email);
                    mUserData.child("uid").setValue(uid);

                    Log.d(TAG, "onDataChange: User added");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });
    }

    public void getEventData() {
        final ArrayList<Event> eventsArray = new ArrayList<>();
        final EventsRecyclerAdapter eventsAdapter = new EventsRecyclerAdapter(this, eventsArray);
        final RecyclerView events_recycler = findViewById(R.id.events_recycler);
        events_recycler.setAdapter(eventsAdapter);
        events_recycler.setLayoutManager(new LinearLayoutManager(this));
        mEventData.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Event event = dataSnapshot.getValue(Event.class);

                eventsArray.add(event);
                eventsAdapter.notifyDataSetChanged();

                Log.d(TAG, "onChildAdded: New event read");
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

    public void respondNewEvent() {
        // Build new event dialog
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_event_new, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle(R.string.title_new_event);

        // Define responses
        final EditText event_name_edit = dialogView.findViewById(R.id.event_name_edit);
        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Write event to DB
                mEventData.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String name = event_name_edit.getText().toString().trim();
                        String uid = mEventData.push().getKey();

                        Event event = new Event(name, uid);
                        mEventData.child(uid).setValue(event);

                        Log.d(TAG, "onDataChange: New event added");
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
