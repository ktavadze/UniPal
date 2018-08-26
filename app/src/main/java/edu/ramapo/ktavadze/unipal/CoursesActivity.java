package edu.ramapo.ktavadze.unipal;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CoursesActivity extends AppCompatActivity {
    private static final String TAG = "CoursesActivity";

    private DatabaseReference mCoursesData;
    private DatabaseReference mSchoolsData;

    private ChildEventListener mCoursesListener;
    private ChildEventListener mSchoolsListener;

    private ArrayList<Course> mCourses;
    private ArrayList<String> mSchoolNames;

    private CoursesRecyclerAdapter mCoursesAdapter;
    private ArrayAdapter<String> mSchoolNamesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Courses");

        addCoursesListener();

        addSchoolsListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        removeCoursesListener();

        removeSchoolsListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_courses, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_new_course:
                actionNewCourse();
                return true;
            default:
                // Invoke superclass to handle unrecognized action.
                return super.onOptionsItemSelected(item);
        }
    }

    public void addCoursesListener() {
        // Init courses
        mCourses = new ArrayList<>();
        mCoursesAdapter = new CoursesRecyclerAdapter(this, mCourses);
        final RecyclerView courses_recycler = findViewById(R.id.courses_recycler);
        courses_recycler.setAdapter(mCoursesAdapter);
        courses_recycler.setLayoutManager(new LinearLayoutManager(this));

        // Add courses listener
        mCoursesData = FirebaseDatabase.getInstance().getReference().child("courses").child(User.getUid());
        mCoursesListener = mCoursesData.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Course course = dataSnapshot.getValue(Course.class);
                mCourses.add(course);

                mCoursesAdapter.notifyDataSetChanged();

                Log.d(TAG, "onChildAdded: Course read: " + course.getName());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Course course = dataSnapshot.getValue(Course.class);
                mCourses.remove(course);

                mCoursesAdapter.notifyDataSetChanged();

                Log.d(TAG, "onChildRemoved: Course removed: " + course.getName());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });

        Log.d(TAG, "addCoursesListener: Listener added");
    }

    public void addSchoolsListener() {
        // Init schools
        mSchoolNames = new ArrayList<>();
        mSchoolNamesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, mSchoolNames);

        // Add schools listener
        mSchoolsData = FirebaseDatabase.getInstance().getReference().child("schools").child(User.getUid());
        mSchoolsListener = mSchoolsData.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                School school = dataSnapshot.getValue(School.class);
                mSchoolNames.add(school.getName());

                mSchoolNamesAdapter.notifyDataSetChanged();

                Log.d(TAG, "onChildAdded: School read: " + school.getName());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                School school = dataSnapshot.getValue(School.class);
                mSchoolNames.remove(school.getName());

                mSchoolNamesAdapter.notifyDataSetChanged();

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

    public void removeCoursesListener() {
        // Remove courses listener
        mCoursesData.removeEventListener(mCoursesListener);

        Log.d(TAG, "removeCoursesListener: Listener removed");
    }

    public void removeSchoolsListener() {
        // Remove schools listener
        mSchoolsData.removeEventListener(mSchoolsListener);

        Log.d(TAG, "removeSchoolsListener: Listener removed");
    }

    public void actionNewCourse() {
        final Course newCourse = new Course();

        // Build new course dialog
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_course_new, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle(R.string.title_new_course);

        // Define fields
        final EditText course_name_edit = dialogView.findViewById(R.id.course_name_edit);
        final EditText course_department_edit = dialogView.findViewById(R.id.course_department_edit);
        course_department_edit.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        final EditText course_number_edit = dialogView.findViewById(R.id.course_number_edit);
        final EditText course_section_edit = dialogView.findViewById(R.id.course_section_edit);
        final Spinner course_school_spinner = dialogView.findViewById(R.id.course_school_spinner);

        // School
        if (mSchoolNames.isEmpty()) {
            course_school_spinner.setVisibility(View.GONE);

            newCourse.setSchoolName("Undefined");
        }
        else {
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

        // Define responses
        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Write course to DB
                mCoursesData.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Set name
                        String name = course_name_edit.getText().toString().trim();
                        if (name.isEmpty()) {
                            newCourse.setName("New Course");
                        }
                        else {
                            newCourse.setName(name);
                        }

                        // Set department
                        String department = course_department_edit.getText().toString().trim();
                        if (department.isEmpty()) {
                            newCourse.setDepartment("????");
                        }
                        else {
                            newCourse.setDepartment(department);
                        }

                        // Set number
                        String number = course_number_edit.getText().toString().trim();
                        if (number.isEmpty()) {
                            newCourse.setNumber("???");
                        }
                        else {
                            newCourse.setNumber(number);
                        }

                        // Set section
                        String section = course_section_edit.getText().toString().trim();
                        if (section.isEmpty()) {
                            newCourse.setSection("?");
                        }
                        else {
                            newCourse.setSection(section);
                        }

                        // Set uid
                        String uid = mCoursesData.push().getKey();
                        newCourse.setUid(uid);

                        // Add course
                        mCoursesData.child(uid).setValue(newCourse);

                        Log.d(TAG, "onDataChange: Course added: " + name);
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

        // Show new course dialog
        AlertDialog courseDialog = dialogBuilder.create();
        courseDialog.show();
    }
}
