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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CourseActivity extends AppCompatActivity {
    private static final String TAG = "CourseActivity";

    private Course mCourse;

    private DatabaseReference mData;
    private DatabaseReference mSchoolsData;

    private ArrayList<String> mSchoolNames;

    private ArrayAdapter<String> mSchoolNamesAdapter;

    private MenuItem mEditIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        getIntentData();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(mCourse.getName());

        displayCourseData();

        getSchoolNames();

        // Delete course from DB
        final Button delete_course_button = findViewById(R.id.delete_course_button);
        delete_course_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mData.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mData.removeValue();

                        finish();

                        Log.d(TAG, "onDataChange: Course deleted: " + mCourse.getName());
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
        inflater.inflate(R.menu.menu_course, menu);

        mEditIcon = menu.findItem(R.id.action_edit_course);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_edit_course:
                startEditing();
                return true;
            default:
                // Invoke superclass to handle unrecognized action.
                return super.onOptionsItemSelected(item);
        }
    }

    public void getIntentData() {
        final Intent intent = getIntent();
        if (intent.hasExtra("name") && intent.hasExtra("department") && intent.hasExtra("number")
                && intent.hasExtra("section") && intent.hasExtra("schoolName") && intent.hasExtra("uid")) {
            String name = intent.getStringExtra("name");
            String department = intent.getStringExtra("department");
            String number = intent.getStringExtra("number");
            String section = intent.getStringExtra("section");
            String schoolName = intent.getStringExtra("schoolName");
            String uid = intent.getStringExtra("uid");
            mCourse = new Course(name, department, number, section, schoolName, uid);

            mData = FirebaseDatabase.getInstance().getReference().child("courses").child(User.getUid()).child(uid);

            Log.d(TAG, "getIntentData: Intent accepted");
        }
        else {
            finish();

            Log.d(TAG, "getIntentData: Intent rejected");
        }
    }

    public void displayCourseData() {
        final TextView course_name_text = findViewById(R.id.course_name_text);
        final TextView course_department_text = findViewById(R.id.course_department_text);
        final TextView course_number_text = findViewById(R.id.course_number_text);
        final TextView course_section_text = findViewById(R.id.course_section_text);
        final TextView course_school_text = findViewById(R.id.course_school_text);

        course_name_text.setText(mCourse.getName());
        course_department_text.setText(mCourse.getDepartment());
        course_number_text.setText(mCourse.getNumber());
        course_section_text.setText(mCourse.getSection());
        course_school_text.setText(mCourse.getSchoolName());
    }

    public void getSchoolNames() {
        // Init schools
        mSchoolNames = new ArrayList<>();
        mSchoolNamesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, mSchoolNames);

        // Read schools from DB
        mSchoolsData = FirebaseDatabase.getInstance().getReference().child("schools").child(User.getUid());
        mSchoolsData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot schoolSnapshot: dataSnapshot.getChildren()) {
                    String schoolName = schoolSnapshot.child("name").getValue(String.class);
                    mSchoolNames.add(schoolName);

                    mSchoolNamesAdapter.notifyDataSetChanged();

                    Log.d(TAG, "onDataChange: School read: " + schoolName);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });
    }

    public void startEditing() {
        final TextView course_name_text = findViewById(R.id.course_name_text);
        final EditText course_name_edit = findViewById(R.id.course_name_edit);
        final TextView course_department_text = findViewById(R.id.course_department_text);
        final EditText course_department_edit = findViewById(R.id.course_department_edit);
        final TextView course_number_text = findViewById(R.id.course_number_text);
        final EditText course_number_edit = findViewById(R.id.course_number_edit);
        final TextView course_section_text = findViewById(R.id.course_section_text);
        final EditText course_section_edit = findViewById(R.id.course_section_edit);
        final TextView course_school_text = findViewById(R.id.course_school_text);
        final Spinner course_school_spinner = findViewById(R.id.course_school_spinner);
        final Button delete_course_button = findViewById(R.id.delete_course_button);
        final Button cancel_course_button = findViewById(R.id.cancel_course_button);
        final Button update_course_button = findViewById(R.id.update_course_button);

        // Update UI
        mEditIcon.setVisible(false);
        course_name_text.setVisibility(View.GONE);
        course_department_text.setVisibility(View.GONE);
        course_number_text.setVisibility(View.GONE);
        course_section_text.setVisibility(View.GONE);
        course_school_text.setVisibility(View.GONE);
        delete_course_button.setVisibility(View.GONE);
        course_name_edit.setVisibility(View.VISIBLE);
        course_department_edit.setVisibility(View.VISIBLE);
        course_number_edit.setVisibility(View.VISIBLE);
        course_section_edit.setVisibility(View.VISIBLE);
        course_school_spinner.setVisibility(View.VISIBLE);
        cancel_course_button.setVisibility(View.VISIBLE);
        update_course_button.setVisibility(View.VISIBLE);

        // Preview current info
        course_name_edit.setText(mCourse.getName());
        course_department_edit.setText(mCourse.getDepartment());
        course_number_edit.setText(mCourse.getNumber());
        course_section_edit.setText(mCourse.getSection());

        final Course newCourse = new Course();

        // Set school
        if (mSchoolNames.isEmpty()) {
            course_school_spinner.setVisibility(View.GONE);

            newCourse.setSchoolName("Undefined");
        }
        else {
            int index = mSchoolNames.indexOf(mCourse.getSchoolName());
            course_school_spinner.setSelection(index);
            course_school_spinner.setAdapter(mSchoolNamesAdapter);
            course_school_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String schoolName = parent.getItemAtPosition(position).toString();

                    // Set school
                    newCourse.setSchoolName(schoolName);

                    Log.d(TAG, "onItemSelected: School selected: " + schoolName);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }

        // Cancel
        cancel_course_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Update UI
                mEditIcon.setVisible(true);
                course_name_edit.setVisibility(View.GONE);
                course_department_edit.setVisibility(View.GONE);
                course_number_edit.setVisibility(View.GONE);
                course_section_edit.setVisibility(View.GONE);
                course_school_spinner.setVisibility(View.GONE);
                cancel_course_button.setVisibility(View.GONE);
                update_course_button.setVisibility(View.GONE);
                course_name_text.setVisibility(View.VISIBLE);
                course_department_text.setVisibility(View.VISIBLE);
                course_number_text.setVisibility(View.VISIBLE);
                course_section_text.setVisibility(View.VISIBLE);
                course_school_text.setVisibility(View.VISIBLE);
                delete_course_button.setVisibility(View.VISIBLE);

                // Clear listeners
                cancel_course_button.setOnClickListener(null);
                update_course_button.setOnClickListener(null);

                // Hide keyboard
                InputMethodManager imm = (InputMethodManager)getSystemService(CourseActivity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
            }
        });

        // Update
        update_course_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Update UI
                mEditIcon.setVisible(true);
                course_name_edit.setVisibility(View.GONE);
                course_department_edit.setVisibility(View.GONE);
                course_number_edit.setVisibility(View.GONE);
                course_section_edit.setVisibility(View.GONE);
                course_school_spinner.setVisibility(View.GONE);
                cancel_course_button.setVisibility(View.GONE);
                update_course_button.setVisibility(View.GONE);
                course_name_text.setVisibility(View.VISIBLE);
                course_department_text.setVisibility(View.VISIBLE);
                course_number_text.setVisibility(View.VISIBLE);
                course_section_text.setVisibility(View.VISIBLE);
                course_school_text.setVisibility(View.VISIBLE);
                delete_course_button.setVisibility(View.VISIBLE);

                // Clear listeners
                cancel_course_button.setOnClickListener(null);
                update_course_button.setOnClickListener(null);

                // Hide keyboard
                InputMethodManager imm = (InputMethodManager)getSystemService(CourseActivity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);

                // Set name
                String name = course_name_edit.getText().toString().trim();
                if (name.isEmpty()) {
                    newCourse.setName(mCourse.getName());
                }
                else {
                    newCourse.setName(name);
                }

                // Set department
                String department = course_department_edit.getText().toString().trim();
                if (department.isEmpty()) {
                    newCourse.setDepartment(mCourse.getDepartment());
                }
                else {
                    newCourse.setDepartment(department);
                }

                // Set number
                String number = course_number_edit.getText().toString().trim();
                if (number.isEmpty()) {
                    newCourse.setNumber(mCourse.getNumber());
                }
                else {
                    newCourse.setNumber(number);
                }

                // Set section
                String section = course_section_edit.getText().toString().trim();
                if (section.isEmpty()) {
                    newCourse.setSection(mCourse.getSection());
                }
                else {
                    newCourse.setSection(section);
                }

                // Set uid
                newCourse.setUid(mCourse.getUid());

                // Update course
                mCourse = newCourse;

                displayCourseData();

                // Update course in DB
                mData.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mData.setValue(mCourse);

                        Log.d(TAG, "onDataChange: Course updated: " + mCourse.getName());
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
