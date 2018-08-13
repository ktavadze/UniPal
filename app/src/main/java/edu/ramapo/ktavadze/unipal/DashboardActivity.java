package edu.ramapo.ktavadze.unipal;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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

public class DashboardActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mCurrentUser;
    private User mUser;

    private DatabaseReference mDatabase;
    private DatabaseReference mUserData;
    private DatabaseReference mSchoolData;
    private DatabaseReference mEventData;

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        if (mCurrentUser != null) {
            getUserData();
            writeUserData();
        }

        // Read user data from DB
        final TextView email_text = findViewById(R.id.email_text);
        final TextView name_text = findViewById(R.id.name_text);
        final TextView uid_text = findViewById(R.id.uid_text);
        final Button get_user_button = findViewById(R.id.get_user_button);
        get_user_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUserData.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);

                        name_text.setText(user.getDisplayName());
                        email_text.setText(user.getEmail());
                        uid_text.setText(user.getUid());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
                    }
                });
            }
        });

        // Write school to DB
        final EditText school_name_edit = findViewById(R.id.school_name_edit);
        final Button add_school_button = findViewById(R.id.add_school_button);
        add_school_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSchoolData.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String name = school_name_edit.getText().toString().trim();
                        String uid = mSchoolData.push().getKey();

                        School school = new School(name, uid);

                        mSchoolData.child(uid).setValue(school);
                        System.out.println("School added");

                        school_name_edit.setText("");
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
                    }
                });
            }
        });

        // Read school data from DB
        final ArrayList<String> schoolsArray = new ArrayList<>();
        final ArrayAdapter<String> schoolsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, schoolsArray);
        ListView schools_list = findViewById(R.id.schools_list);
        schools_list.setAdapter(schoolsAdapter);
        mSchoolData.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                School school = dataSnapshot.getValue(School.class);

                schoolsArray.add(school.getName());
                schoolsAdapter.notifyDataSetChanged();
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

            }
        });

        // Write event to DB
        final EditText event_name_edit = findViewById(R.id.event_name_edit);
        final Button add_event_button = findViewById(R.id.add_event_button);
        add_event_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mEventData.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String name = event_name_edit.getText().toString().trim();
                        String uid = mEventData.push().getKey();

                        Event event = new Event(name, uid);

                        mEventData.child(uid).setValue(event);
                        System.out.println("Event added");

                        event_name_edit.setText("");
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
                    }
                });
            }
        });

        // Read event data from DB
        final ArrayList<String> eventsArray = new ArrayList<>();
        final ArrayAdapter<String> eventsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, eventsArray);
        ListView events_list = findViewById(R.id.events_list);
        events_list.setAdapter(eventsAdapter);
        mEventData.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Event event = dataSnapshot.getValue(Event.class);

                eventsArray.add(event.getName());
                eventsAdapter.notifyDataSetChanged();
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

            }
        });

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    startActivity(new Intent(DashboardActivity.this, MainActivity.class));
                }
            }
        };
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
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

        String displayName = profile.getDisplayName();
        String email = profile.getEmail();
        String uid = profile.getUid();

        mUser = new User(displayName, email, uid);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mUserData = mDatabase.child("users").child(mUser.getUid());
        mSchoolData = mDatabase.child("schools").child(mUser.getUid());
        mEventData = mDatabase.child("events").child(mUser.getUid());
    }

    public void writeUserData() {
        mUserData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    System.out.println("User exists");
                }
                else {
                    mUserData.setValue(mUser);
                    System.out.println("User added");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
            }
        });
    }
}
