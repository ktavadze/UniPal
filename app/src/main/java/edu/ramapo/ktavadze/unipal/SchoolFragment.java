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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SchoolFragment extends Fragment {
    private static final String TAG = "SchoolFragment";

    private Database mDatabase;

    private School mSchool;

    private View mView;

    private MenuItem mEditIcon;

    public SchoolFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDatabase = ((MainActivity)getActivity()).mDatabase;

        // Get event
        mSchool = new School();
        String uid = getArguments().getString("uid", "");
        mSchool.setUid(uid);
        int index = mDatabase.schools.indexOf(mSchool);
        mSchool = mDatabase.schools.get(index);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        mView = inflater.inflate(R.layout.fragment_school, null);

        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();

        getActivity().setTitle(mSchool.getName());

        setHasOptionsMenu(true);

        addDeleteListener();

        displaySchoolData();
    }

    @Override
    public void onStop() {
        super.onStop();

        removeDeleteListener();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.add(0, 0, 0, R.string.action_edit_school)
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

    /**/
    /*
    NAME

    addDeleteListener - adds delete listener.

    SYNOPSIS

    private void addDeleteListener();

    DESCRIPTION

    Will add the click listener to the delete button.

    RETURNS

    N/A
    */
    /**/
    private void addDeleteListener() {
        // Add delete listener
        final Button delete_school_button = mView.findViewById(R.id.delete_school_button);
        delete_school_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Remove school
                mDatabase.removeSchool(mSchool);

                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        Log.d(TAG, "addDeleteListener: Listener added");
    }

    /**/
    /*
    NAME

    removeDeleteListener - removes delete listener.

    SYNOPSIS

    private boolean removeDeleteListener();

    DESCRIPTION

    Will remove the click listener from the delete button.

    RETURNS

    N/A
    */
    /**/
    private void removeDeleteListener() {
        // Remove delete listener
        final Button delete_school_button = mView.findViewById(R.id.delete_school_button);
        delete_school_button.setOnClickListener(null);

        Log.d(TAG, "removeDeleteListener: Listener removed");
    }

    /**/
    /*
    NAME

    displaySchoolData - displays school info.

    SYNOPSIS

    private void displaySchoolData();

    DESCRIPTION

    Will display the data for the current school.

    RETURNS

    N/A
    */
    /**/
    private void displaySchoolData() {
        final TextView school_name_text = mView.findViewById(R.id.school_name_text);
        final TextView school_year_text = mView.findViewById(R.id.school_year_text);
        final TextView school_major_text = mView.findViewById(R.id.school_major_text);
        final TextView school_minor_text = mView.findViewById(R.id.school_minor_text);

        school_name_text.setText(mSchool.getName());
        school_year_text.setText(mSchool.getYear());
        school_major_text.setText(mSchool.getMajor());
        school_minor_text.setText(mSchool.getMinor());
    }

    /**/
    /*
    NAME

    startEditing - starts editing school.

    SYNOPSIS

    private void startEditing();

    DESCRIPTION

    Will update UI to allow editing of school information and define click listeners for the
    cancel and update buttons.

    RETURNS

    N/A
    */
    /**/
    private void startEditing() {
        final TextView school_name_text = mView.findViewById(R.id.school_name_text);
        final EditText school_name_edit = mView.findViewById(R.id.school_name_edit);
        final TextView school_year_text = mView.findViewById(R.id.school_year_text);
        final EditText school_year_edit = mView.findViewById(R.id.school_year_edit);
        final TextView school_major_text = mView.findViewById(R.id.school_major_text);
        final EditText school_major_edit = mView.findViewById(R.id.school_major_edit);
        final TextView school_minor_text = mView.findViewById(R.id.school_minor_text);
        final EditText school_minor_edit = mView.findViewById(R.id.school_minor_edit);
        final Button delete_school_button = mView.findViewById(R.id.delete_school_button);
        final Button cancel_school_button = mView.findViewById(R.id.cancel_school_button);
        final Button update_school_button = mView.findViewById(R.id.update_school_button);

        // Update UI
        mEditIcon.setVisible(false);
        school_name_text.setVisibility(View.GONE);
        school_year_text.setVisibility(View.GONE);
        school_major_text.setVisibility(View.GONE);
        school_minor_text.setVisibility(View.GONE);
        delete_school_button.setVisibility(View.GONE);
        school_name_edit.setVisibility(View.VISIBLE);
        school_year_edit.setVisibility(View.VISIBLE);
        school_major_edit.setVisibility(View.VISIBLE);
        school_minor_edit.setVisibility(View.VISIBLE);
        cancel_school_button.setVisibility(View.VISIBLE);
        update_school_button.setVisibility(View.VISIBLE);

        // Preview current info
        school_name_edit.setText(mSchool.getName());
        school_year_edit.setText(mSchool.getYear());
        school_major_edit.setText(mSchool.getMajor());
        school_minor_edit.setText(mSchool.getMinor());

        final School newSchool = new School();

        // Cancel
        cancel_school_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Update UI
                mEditIcon.setVisible(true);
                school_name_edit.setVisibility(View.GONE);
                school_year_edit.setVisibility(View.GONE);
                school_major_edit.setVisibility(View.GONE);
                school_minor_edit.setVisibility(View.GONE);
                cancel_school_button.setVisibility(View.GONE);
                update_school_button.setVisibility(View.GONE);
                school_name_text.setVisibility(View.VISIBLE);
                school_year_text.setVisibility(View.VISIBLE);
                school_major_text.setVisibility(View.VISIBLE);
                school_minor_text.setVisibility(View.VISIBLE);
                delete_school_button.setVisibility(View.VISIBLE);

                // Clear listeners
                cancel_school_button.setOnClickListener(null);
                update_school_button.setOnClickListener(null);

                // Hide keyboard
                ((MainActivity)getActivity()).hideKeyboard();

                displaySchoolData();
            }
        });

        // Update
        update_school_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Update UI
                mEditIcon.setVisible(true);
                school_name_edit.setVisibility(View.GONE);
                school_year_edit.setVisibility(View.GONE);
                school_major_edit.setVisibility(View.GONE);
                school_minor_edit.setVisibility(View.GONE);
                cancel_school_button.setVisibility(View.GONE);
                update_school_button.setVisibility(View.GONE);
                school_name_text.setVisibility(View.VISIBLE);
                school_year_text.setVisibility(View.VISIBLE);
                school_major_text.setVisibility(View.VISIBLE);
                school_minor_text.setVisibility(View.VISIBLE);
                delete_school_button.setVisibility(View.VISIBLE);

                // Clear listeners
                cancel_school_button.setOnClickListener(null);
                update_school_button.setOnClickListener(null);

                // Hide keyboard
                ((MainActivity)getActivity()).hideKeyboard();

                // Set name
                String name = school_name_edit.getText().toString().trim();
                if (name.isEmpty()) {
                    newSchool.setName(mSchool.getName());
                }
                else {
                    newSchool.setName(name);
                }

                // Set year
                String year = school_year_edit.getText().toString().trim();
                if (year.isEmpty()) {
                    newSchool.setYear(mSchool.getYear());
                }
                else {
                    newSchool.setYear(year);
                }

                // Set major
                String major = school_major_edit.getText().toString().trim();
                if (major.isEmpty()) {
                    newSchool.setMajor(mSchool.getMajor());
                }
                else {
                    newSchool.setMajor(major);
                }

                // Set minor
                String minor = school_minor_edit.getText().toString().trim();
                if (minor.isEmpty()) {
                    newSchool.setMinor(mSchool.getMinor());
                }
                else {
                    newSchool.setMinor(minor);
                }

                // Set uid
                newSchool.setUid(mSchool.getUid());

                // Update school
                mSchool = newSchool;
                mDatabase.updateSchool(mSchool);

                displaySchoolData();
            }
        });
    }
}
