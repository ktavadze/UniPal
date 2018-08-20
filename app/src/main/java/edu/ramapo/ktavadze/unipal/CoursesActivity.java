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

public class CoursesActivity extends AppCompatActivity {

    private static final String TAG = "CoursesActivity";

    private DatabaseReference mCoursesData;

    private ArrayList<Course> mCourses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Courses");

        mCoursesData = FirebaseDatabase.getInstance().getReference().child("courses").child(User.getUid());
    }

    @Override
    protected void onResume() {
        super.onResume();

        getCourses();
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

    public void getCourses() {
        mCourses = new ArrayList<>();
        final CoursesRecyclerAdapter coursesAdapter = new CoursesRecyclerAdapter(this, mCourses);
        final RecyclerView courses_recycler = findViewById(R.id.courses_recycler);
        courses_recycler.setAdapter(coursesAdapter);
        courses_recycler.setLayoutManager(new LinearLayoutManager(this));
        mCoursesData.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Course course = dataSnapshot.getValue(Course.class);
                if (!mCourses.contains(course)) {
                    mCourses.add(course);

                    coursesAdapter.notifyDataSetChanged();

                    Log.d(TAG, "onChildAdded: Course read");
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

    public void actionNewCourse() {
        final Course newCourse = new Course();

        // Build new course dialog
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_course_new, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle(R.string.title_new_course);

        // Name
        final EditText course_name_edit = dialogView.findViewById(R.id.course_name_edit);

        // Define responses
        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Write course to DB
                mCoursesData.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Set name
                        String name = course_name_edit.getText().toString().trim();
                        newCourse.setName(name);

                        // Set uid
                        String uid = mCoursesData.push().getKey();
                        newCourse.setUid(uid);

                        // Add course
                        mCoursesData.child(uid).setValue(newCourse);

                        Log.d(TAG, "onDataChange: Course added");
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
