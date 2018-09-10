package edu.ramapo.ktavadze.unipal;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

public class DashboardFragment extends Fragment implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {
    private static final String TAG = "DashboardFragment";

    private Database mDatabase;

    private View mView;

    public DashboardFragment() {
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

        mView = inflater.inflate(R.layout.fragment_dashboard, null);

        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();

        getActivity().setTitle("Dashboard");

        setHasOptionsMenu(true);

        initRecycler();

        addSelectionListeners();
    }

    @Override
    public void onStop() {
        super.onStop();

        removeSelectionListeners();
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
        // Backup event
        final Event eventBackup = new Event(mDatabase.selectedEvents.get(position));

        // Make snack bar
        Snackbar snackbar = Snackbar.make(mView, "Event removed", Snackbar.LENGTH_SHORT);
        snackbar.addCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT) {
                    // Delete event
                    mDatabase.removeEvent(eventBackup);
                }
            }

            @Override
            public void onShown(Snackbar sb) {
                // Remove event locally
                mDatabase.selectedEvents.remove(position);

                mDatabase.selectedEventsAdapter.notifyDataSetChanged();
            }
        });
        snackbar.setAction("UNDO", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Restore event locally
                mDatabase.selectedEvents.add(position, eventBackup);

                mDatabase.selectedEventsAdapter.notifyDataSetChanged();
            }
        });

        // Style snack bar
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) snackbar.getView().getLayoutParams();
        layoutParams.width = FrameLayout.LayoutParams.MATCH_PARENT;
        snackbar.getView().setLayoutParams(layoutParams);
        snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        snackbar.show();
    }

    private void initRecycler() {
        // Init recycler
        final RecyclerView events_recycler = mView.findViewById(R.id.events_recycler);
        events_recycler.setAdapter(mDatabase.selectedEventsAdapter);
        events_recycler.setItemAnimator(new DefaultItemAnimator());
        events_recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        // Attach item touch helper
        ItemTouchHelper.SimpleCallback recyclerTouchHelperCallback =
                new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(recyclerTouchHelperCallback).attachToRecyclerView(events_recycler);
    }

    private void setFilter(int filter) {
        final RecyclerView events_recycler = mView.findViewById(R.id.events_recycler);
        final Button all_button = mView.findViewById(R.id.all_button);
        final Button month_button = mView.findViewById(R.id.month_button);
        final Button week_button = mView.findViewById(R.id.week_button);
        final Button day_button = mView.findViewById(R.id.day_button);

        final int active = getResources().getColor(R.color.colorAccent);
        final int inactive = getResources().getColor(R.color.colorPrimary);

        switch (filter) {
            case 100:
                mDatabase.selectAllEvents();

                all_button.setBackgroundColor(active);
                month_button.setBackgroundColor(inactive);
                week_button.setBackgroundColor(inactive);
                day_button.setBackgroundColor(inactive);
                break;
            case 31:
                mDatabase.selectEvents(31);

                all_button.setBackgroundColor(inactive);
                month_button.setBackgroundColor(active);
                week_button.setBackgroundColor(inactive);
                day_button.setBackgroundColor(inactive);
                break;
            case 7:
                mDatabase.selectEvents(7);

                all_button.setBackgroundColor(inactive);
                month_button.setBackgroundColor(inactive);
                week_button.setBackgroundColor(active);
                day_button.setBackgroundColor(inactive);
                break;
            case 1:
                mDatabase.selectEvents(1);

                all_button.setBackgroundColor(inactive);
                month_button.setBackgroundColor(inactive);
                week_button.setBackgroundColor(inactive);
                day_button.setBackgroundColor(active);
        }

        events_recycler.setAdapter(mDatabase.selectedEventsAdapter);
    }

    private void addSelectionListeners() {
        final Button all_button = mView.findViewById(R.id.all_button);
        final Button month_button = mView.findViewById(R.id.month_button);
        final Button week_button = mView.findViewById(R.id.week_button);
        final Button day_button = mView.findViewById(R.id.day_button);

        setFilter(mDatabase.filter);

        all_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFilter(100);
            }
        });

        month_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFilter(31);
            }
        });

        week_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFilter(7);
            }
        });

        day_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFilter(1);
            }
        });

        Log.d(TAG, "addSelectionListeners: Listeners added");
    }

    private void removeSelectionListeners() {
        final Button all_button = mView.findViewById(R.id.all_button);
        final Button month_button = mView.findViewById(R.id.month_button);
        final Button week_button = mView.findViewById(R.id.week_button);
        final Button day_button = mView.findViewById(R.id.day_button);

        all_button.setOnClickListener(null);
        month_button.setOnClickListener(null);
        week_button.setOnClickListener(null);
        day_button.setOnClickListener(null);

        Log.d(TAG, "removeSelectionListeners: Listeners removed");
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
        final Spinner event_alarm_spinner = dialogView.findViewById(R.id.event_alarm_spinner);
        final TextView event_date_pick = dialogView.findViewById(R.id.event_date_pick);
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

        // Alarm
        event_alarm_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String alarm = parent.getItemAtPosition(position).toString();

                // Set alarm
                newEvent.setAlarm(alarm);

                Log.d(TAG, "onItemSelected: Alarm selected: " + alarm);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Date
        event_date_pick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Build date picker
                DatePickerDialog date_picker = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int day) {
                                // Format date
                                month++;
                                String date;
                                if (month < 10 && day < 10) {
                                    date = "0" + month + "/0" + day + "/" + year;
                                }
                                else if (month < 10) {
                                    date = "0" + month + "/" + day + "/" + year;
                                }
                                else if (day < 10) {
                                    date = "" + month + "/0" + day + "/" + year;
                                }
                                else {
                                    date = "" + month + "/" + day + "/" + year;
                                }

                                // Set date
                                newEvent.setDate(date);

                                // Preview date
                                event_date_pick.setText(date);

                                Log.d(TAG, "onDateSet: Date set: " + date);
                            }
                        },
                        cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));

                // Show date picker
                date_picker.getWindow();
                date_picker.show();
            }
        });

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
