package edu.ramapo.ktavadze.unipal;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

    private ArrayList<School> mSchools;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schools);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Schools");

        mSchoolsData = FirebaseDatabase.getInstance().getReference().child("schools").child(User.getUid());
    }

    @Override
    protected void onResume() {
        super.onResume();

        getSchools();
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

    public void getSchools() {
        mSchools = new ArrayList<>();
        final SchoolsRecyclerAdapter schoolsAdapter = new SchoolsRecyclerAdapter(this, mSchools);
        final RecyclerView schools_recycler = findViewById(R.id.schools_recycler);
        schools_recycler.setAdapter(schoolsAdapter);
        schools_recycler.setLayoutManager(new LinearLayoutManager(this));
        mSchoolsData.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                School school = dataSnapshot.getValue(School.class);
                if (!mSchools.contains(school)) {
                    mSchools.add(school);

                    schoolsAdapter.notifyDataSetChanged();

                    Log.d(TAG, "onChildAdded: School read");
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

    public void actionNewSchool() {
        final School newSchool = new School();

        // Build new school dialog
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_school_new, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle(R.string.title_new_school);

        // Name
        final EditText school_name_edit = dialogView.findViewById(R.id.school_name_edit);

        // Define responses
        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Write school to DB
                mSchoolsData.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Set name
                        String name = school_name_edit.getText().toString().trim();
                        newSchool.setName(name);

                        // Set uid
                        String uid = mSchoolsData.push().getKey();
                        newSchool.setUid(uid);

                        // Add school
                        mSchoolsData.child(uid).setValue(newSchool);

                        Log.d(TAG, "onDataChange: School added");
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
