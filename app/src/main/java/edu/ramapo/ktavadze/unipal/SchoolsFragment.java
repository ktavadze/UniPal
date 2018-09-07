package edu.ramapo.ktavadze.unipal;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class SchoolsFragment extends Fragment {
    private static final String TAG = "SchoolsFragment";

    private Database mDatabase;

    private View mView;

    public SchoolsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDatabase = ((MainActivity)getActivity()).mDatabase;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        mView = inflater.inflate(R.layout.fragment_schools, null);

        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();

        getActivity().setTitle("Schools");

        setHasOptionsMenu(true);

        initRecycler();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.add(0, 0, 0, R.string.action_new_school)
                .setIcon(R.drawable.ic_add)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                actionNewSchool();
                return true;
            default:
                // Invoke superclass to handle unrecognized action.
                return super.onOptionsItemSelected(item);
        }
    }

    private void initRecycler() {
        // Init recycler
        final RecyclerView schools_recycler = mView.findViewById(R.id.schools_recycler);
        schools_recycler.setAdapter(mDatabase.schoolsAdapter);
        schools_recycler.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void actionNewSchool() {
        final School newSchool = new School();

        // Build new school dialog
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
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

                // Add school
                mDatabase.addSchool(newSchool);
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
