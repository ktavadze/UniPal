package edu.ramapo.ktavadze.unipal;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class CourseFragment extends Fragment {
    private static final String TAG = "CourseFragment";

    private Database mDatabase;

    private Course mCourse;

    private View mView;

    private MenuItem mEditIcon;

    public CourseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDatabase = ((MainActivity)getActivity()).mDatabase;

        // Get course
        mCourse = new Course();
        String uid = getArguments().getString("uid", "");
        mCourse.setUid(uid);
        int index = mDatabase.courses.indexOf(mCourse);
        mCourse = mDatabase.courses.get(index);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        mView = inflater.inflate(R.layout.fragment_course, null);

        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();

        setHasOptionsMenu(true);

        addDeleteListener();

        displayCourseData();
    }

    @Override
    public void onStop() {
        super.onStop();

        removeDeleteListener();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.add(0, 0, 0, R.string.action_edit_course)
                .setIcon(R.drawable.ic_edit)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        mEditIcon = menu.getItem(0);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                startEditing();
                return true;
            default:
                // Invoke superclass to handle unrecognized action.
                return super.onOptionsItemSelected(item);
        }
    }

    private void displayCourseData() {
        final TextView course_name_text = mView.findViewById(R.id.course_name_text);
        final TextView course_department_text = mView.findViewById(R.id.course_department_text);
        final TextView course_number_text = mView.findViewById(R.id.course_number_text);
        final TextView course_section_text = mView.findViewById(R.id.course_section_text);
        final TextView course_school_text = mView.findViewById(R.id.course_school_text);

        course_name_text.setText(mCourse.getName());
        course_department_text.setText(mCourse.getDepartment());
        course_number_text.setText(mCourse.getNumber());
        course_section_text.setText(mCourse.getSection());
        course_school_text.setText(mCourse.getSchoolName());
    }

    private void addDeleteListener() {
        // Add delete listener
        final Button delete_course_button = mView.findViewById(R.id.delete_course_button);
        delete_course_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Remove course
                mDatabase.removeCourse(mCourse);

                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        Log.d(TAG, "addDeleteListener: Listener added");
    }

    private void removeDeleteListener() {
        // Remove delete listener
        final Button delete_course_button = mView.findViewById(R.id.delete_course_button);
        delete_course_button.setOnClickListener(null);

        Log.d(TAG, "removeDeleteListener: Listener removed");
    }

    private void startEditing() {
        final TextView course_name_text = mView.findViewById(R.id.course_name_text);
        final EditText course_name_edit = mView.findViewById(R.id.course_name_edit);
        final TextView course_department_text = mView.findViewById(R.id.course_department_text);
        final EditText course_department_edit = mView.findViewById(R.id.course_department_edit);
        final TextView course_number_text = mView.findViewById(R.id.course_number_text);
        final EditText course_number_edit = mView.findViewById(R.id.course_number_edit);
        final TextView course_section_text = mView.findViewById(R.id.course_section_text);
        final EditText course_section_edit = mView.findViewById(R.id.course_section_edit);
        final TextView course_school_text = mView.findViewById(R.id.course_school_text);
        final Spinner course_school_spinner = mView.findViewById(R.id.course_school_spinner);
        final Button delete_course_button = mView.findViewById(R.id.delete_course_button);
        final Button cancel_course_button = mView.findViewById(R.id.cancel_course_button);
        final Button update_course_button = mView.findViewById(R.id.update_course_button);

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

        // School
        if (mDatabase.schools.isEmpty()) {
            course_school_spinner.setVisibility(View.GONE);

            newCourse.setSchoolName(mCourse.getSchoolName());
        }
        else {
            // Set adapter
            course_school_spinner.setAdapter(mDatabase.schoolNamesAdapter);

            // Set current selection
            int index = mDatabase.schoolNames.indexOf(mCourse.getSchoolName());
            course_school_spinner.setSelection(index);

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
                course_school_spinner.setOnItemSelectedListener(null);
                cancel_course_button.setOnClickListener(null);
                update_course_button.setOnClickListener(null);

                // Hide keyboard
                ((MainActivity)getActivity()).hideKeyboard();

                displayCourseData();
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
                course_school_spinner.setOnItemSelectedListener(null);
                cancel_course_button.setOnClickListener(null);
                update_course_button.setOnClickListener(null);

                // Hide keyboard
                ((MainActivity)getActivity()).hideKeyboard();

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
                mDatabase.updateCourse(mCourse);

                displayCourseData();
            }
        });
    }
}
