package edu.ramapo.ktavadze.unipal;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SchoolsActivity extends AppCompatActivity {

    private DatabaseReference mSchoolData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schools);

        mSchoolData = FirebaseDatabase.getInstance().getReference().child("schools").child(User.getUid());

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
    }
}
