package edu.ramapo.ktavadze.unipal;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

public class DateFragment extends Fragment implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {
    private static final String TAG = "DateFragment";

    private Database mDatabase;

    private String mDate;

    private View mView;

    public DateFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get date
        mDate = getArguments().getString("date", "");

        mDatabase = ((MainActivity)getActivity()).mDatabase;
        mDatabase.selectEvents(mDate);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        mView = inflater.inflate(R.layout.fragment_date, null);

        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();

        setHasOptionsMenu(true);

        initRecycler();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.add(0, 0, 0, R.string.action_new_event)
                .setIcon(R.drawable.ic_add)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                actionNewEvent();
                return true;
            default:
                // Invoke superclass to handle unrecognized action.
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, final int position) {
        // Backup removed event
        final Event event = mDatabase.selectedEvents.get(position);

        // Remove event
        mDatabase.selectedEventsAdapter.removeEvent(position);

        // Show undo snack bar
        Snackbar snackbar = Snackbar.make(mView, "Event removed", Snackbar.LENGTH_SHORT);
        snackbar.setAction("UNDO", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Restore event
                mDatabase.selectedEventsAdapter.restoreEvent(event, position);
            }
        });
        snackbar.show();
    }

    private void initRecycler() {
        // Init recycler
        final RecyclerView date_events_recycler = mView.findViewById(R.id.date_events_recycler);
        date_events_recycler.setAdapter(mDatabase.selectedEventsAdapter);
        date_events_recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        // Attach item touch helper
        ItemTouchHelper.SimpleCallback recyclerTouchHelperCallback =
                new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(recyclerTouchHelperCallback).attachToRecyclerView(date_events_recycler);
    }

    private void actionNewEvent() {
        final Event newEvent = new Event();
        final Calendar cal = Calendar.getInstance();

        // Build new event dialog
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_event_new, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle(R.string.title_new_event);

        // Define fields
        final EditText event_name_edit = dialogView.findViewById(R.id.event_name_edit);
        final Spinner event_type_spinner = dialogView.findViewById(R.id.event_type_spinner);
        final Spinner event_course_spinner = dialogView.findViewById(R.id.event_course_spinner);
        final TextView event_date_pick = dialogView.findViewById(R.id.event_date_pick);
        event_date_pick.setVisibility(View.GONE);
        final TextView event_time_pick = dialogView.findViewById(R.id.event_time_pick);

        // Type
        event_type_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String type = parent.getItemAtPosition(position).toString();

                // Set type
                newEvent.setType(type);

                Log.d(TAG, "onItemSelected: Type selected: " + type);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Course
        if (mDatabase.courseNames.isEmpty()) {
            event_course_spinner.setVisibility(View.GONE);

            newEvent.setCourseName("Undefined");
        }
        else {
            // Set adapter
            event_course_spinner.setAdapter(mDatabase.courseNamesAdapter);

            // Set listener
            event_course_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String courseName = parent.getItemAtPosition(position).toString();

                    // Set course
                    newEvent.setCourseName(courseName);

                    Log.d(TAG, "onItemSelected: Course selected: " + courseName);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }

        // Time
        event_time_pick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Build time picker
                TimePickerDialog time_picker = new TimePickerDialog(getContext(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hour, int minute) {
                                // Format time
                                String time;
                                if (hour < 10 && minute < 10) {
                                    time = "0" + hour + ":0" + minute;
                                }
                                else if (hour < 10) {
                                    time = "0" + hour + ":" + minute;
                                }
                                else if (minute < 10) {
                                    time = "" + hour + ":0" + minute;
                                }
                                else {
                                    time = "" + hour + ":" + minute;
                                }

                                // Set time
                                newEvent.setTime(time);

                                // Preview time
                                event_time_pick.setText(time);

                                Log.d(TAG, "onTimeSet: Time set: " + time);
                            }
                        },
                        cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true);

                // Show time picker
                time_picker.getWindow();
                time_picker.show();
            }
        });

        // Define responses
        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Set name
                String name = event_name_edit.getText().toString().trim();
                if (name.isEmpty()) {
                    newEvent.setName("New event");
                }
                else {
                    newEvent.setName(name);
                }

                // Set date
                newEvent.setDate(mDate);

                // Add event
                mDatabase.addEvent(newEvent);
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // TODO: Cancel
            }
        });

        // Show new event dialog
        AlertDialog eventDialog = dialogBuilder.create();
        eventDialog.show();
    }
}
