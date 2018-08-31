package edu.ramapo.ktavadze.unipal;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class SchoolsActivity extends BaseActivity {
    private static final String TAG = "SchoolsActivity";

    private Database mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schools);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Schools");

        mDatabase = new Database(this);
        mDatabase.addSchoolsListener();

        initRecycler();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mDatabase.removeSchoolsListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, R.string.action_new_school)
                .setIcon(R.drawable.ic_add)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        return super.onCreateOptionsMenu(menu);
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

    public void initRecycler() {
        // Init recycler
        final RecyclerView schools_recycler = findViewById(R.id.schools_recycler);
        schools_recycler.setAdapter(mDatabase.schoolsAdapter);
        schools_recycler.setLayoutManager(new LinearLayoutManager(this));
    }

    public void actionNewSchool() {
        final School newSchool = new School();

        // Build new school dialog
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
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
