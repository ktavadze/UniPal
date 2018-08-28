package edu.ramapo.ktavadze.unipal;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SchoolsActivity extends AppCompatActivity {
    private static final String TAG = "SchoolsActivity";

    private DatabaseReference mSchoolsData;
    private ChildEventListener mSchoolsListener;

    private ArrayList<School> mSchools;
    private SchoolsRecyclerAdapter mSchoolsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schools);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Schools");

        addSchoolsListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        removeSchoolsListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_schools, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_new_school:
                actionNewSchool();
                return true;
            default:
                // Invoke superclass to handle unrecognized action.
                return super.onOptionsItemSelected(item);
        }
    }

    public void addSchoolsListener() {
        // Init schools
        mSchools = new ArrayList<>();
        mSchoolsAdapter = new SchoolsRecyclerAdapter(this, mSchools);
        final RecyclerView schools_recycler = findViewById(R.id.schools_recycler);
        schools_recycler.setAdapter(mSchoolsAdapter);
        schools_recycler.setLayoutManager(new LinearLayoutManager(this));

        // Add schools listener
        mSchoolsData = FirebaseDatabase.getInstance().getReference().child("schools").child(User.getUid());
        mSchoolsListener = mSchoolsData.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                School school = dataSnapshot.getValue(School.class);
                mSchools.add(school);

                mSchoolsAdapter.notifyDataSetChanged();

                Log.d(TAG, "onChildAdded: School read: " + school.getName());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                School school = dataSnapshot.getValue(School.class);
                int index = mSchools.indexOf(school);
                mSchools.set(index, school);

                mSchoolsAdapter.notifyDataSetChanged();

                Log.d(TAG, "onChildChanged: School updated: " + school.getName());
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                School school = dataSnapshot.getValue(School.class);
                mSchools.remove(school);

                mSchoolsAdapter.notifyDataSetChanged();

                Log.d(TAG, "onChildRemoved: School removed: " + school.getName());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });

        Log.d(TAG, "addSchoolsListener: Listener added");
    }

    public void removeSchoolsListener() {
        // Remove schools listener
        mSchoolsData.removeEventListener(mSchoolsListener);

        Log.d(TAG, "removeSchoolsListener: Listener removed");
    }

    public void actionNewSchool() {
        final School newSchool = new School();

        // Build new school dialog
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_school_new, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle(R.string.title_new_school);

        // Define fields
        final EditText school_name_edit = dialogView.findViewById(R.id.school_name_edit);
        final EditText school_year_edit = dialogView.findViewById(R.id.school_year_edit);
        final EditText school_major_edit = dialogView.findViewById(R.id.school_major_edit);
        final EditText school_minor_edit = dialogView.findViewById(R.id.school_minor_edit);

        // Define responses
        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Write school to DB
                mSchoolsData.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Set name
                        String name = school_name_edit.getText().toString().trim();
                        if (name.isEmpty()) {
                            newSchool.setName("New School");
                        }
                        else {
                            newSchool.setName(name);
                        }

                        // Set year
                        String year = school_year_edit.getText().toString().trim();
                        if (year.isEmpty()){
                            newSchool.setYear("????");
                        }
                        else {
                            newSchool.setYear(year);
                        }

                        // Set major
                        String major = school_major_edit.getText().toString().trim();
                        if (major.isEmpty()){
                            newSchool.setMajor("Undeclared");
                        }
                        else {
                            newSchool.setMajor(major);
                        }

                        // Set minor
                        String minor = school_minor_edit.getText().toString().trim();
                        if (minor.isEmpty()){
                            newSchool.setMinor("Undeclared");
                        }
                        else {
                            newSchool.setMinor(minor);
                        }

                        // Set uid
                        String uid = mSchoolsData.push().getKey();
                        newSchool.setUid(uid);

                        // Add school
                        mSchoolsData.child(uid).setValue(newSchool);

                        Log.d(TAG, "onDataChange: School added: " + name);
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

        // Show new school dialog
        AlertDialog schoolDialog = dialogBuilder.create();
        schoolDialog.show();
    }
}
