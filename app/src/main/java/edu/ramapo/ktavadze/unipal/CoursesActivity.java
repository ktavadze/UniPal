package edu.ramapo.ktavadze.unipal;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;

public class CoursesActivity extends BaseActivity {
    private static final String TAG = "CoursesActivity";

    private Database mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Courses");

        mDatabase = new Database(this);
        mDatabase.addCoursesListener();
        mDatabase.addSchoolsListener();

        initRecycler();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mDatabase.removeCoursesListener();
        mDatabase.removeSchoolsListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, R.string.action_new_course)
                .setIcon(R.drawable.ic_add)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                actionNewCourse();
                return true;
            default:
                // Invoke superclass to handle unrecognized action.
                return super.onOptionsItemSelected(item);
        }
    }

    public void initRecycler() {
        // Init recycler
        final RecyclerView courses_recycler = findViewById(R.id.courses_recycler);
        courses_recycler.setAdapter(mDatabase.coursesAdapter);
        courses_recycler.setLayoutManager(new LinearLayoutManager(this));
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
        final EditText course_number_edit = dialogView.findViewById(R.id.course_number_edit);
        final EditText course_section_edit = dialogView.findViewById(R.id.course_section_edit);
        final Spinner course_school_spinner = dialogView.findViewById(R.id.course_school_spinner);

        // School
        if (mDatabase.schools.isEmpty()) {
            course_school_spinner.setVisibility(View.GONE);

            newCourse.setSchoolName("Undefined");
        }
        else {
            // Set adapter
            course_school_spinner.setAdapter(mDatabase.schoolNamesAdapter);

            // Set listener
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

                // Add course
                mDatabase.addCourse(newCourse);
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
