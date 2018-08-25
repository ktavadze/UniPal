package edu.ramapo.ktavadze.unipal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CourseActivity extends AppCompatActivity {
    private static final String TAG = "CourseActivity";

    private Course mCourse;

    private DatabaseReference mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        getIntentData();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(mCourse.getName());

        displayCourseData();

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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
            default:
                // Invoke superclass to handle unrecognized action.
                return super.onOptionsItemSelected(item);
        }
    }

    public void getIntentData() {
        final Intent intent = getIntent();
        if (intent.hasExtra("name") && intent.hasExtra("department") && intent.hasExtra("number")
                && intent.hasExtra("section") && intent.hasExtra("uid")) {
            String name = intent.getStringExtra("name");
            String department = intent.getStringExtra("department");
            String number = intent.getStringExtra("number");
            String section = intent.getStringExtra("section");
            String uid = intent.getStringExtra("uid");
            mCourse = new Course(name, department, number, section, uid);

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

        course_name_text.setText(mCourse.getName());
        course_department_text.setText(mCourse.getDepartment());
        course_number_text.setText(mCourse.getNumber());
        course_section_text.setText(mCourse.getSection());
    }
}
