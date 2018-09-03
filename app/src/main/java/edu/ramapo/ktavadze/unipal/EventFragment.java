package edu.ramapo.ktavadze.unipal;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Arrays;
import java.util.Calendar;

public class EventFragment extends Fragment {
    private static final String TAG = "EventFragment";

    private Database mDatabase;

    private Event mEvent;

    private View mView;

    private MenuItem mEditIcon;

    public EventFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDatabase = ((MainActivity)getActivity()).mDatabase;

        // Get event
        mEvent = new Event();
        String uid = getArguments().getString("uid", "");
        mEvent.setUid(uid);
        int index = mDatabase.events.indexOf(mEvent);
        mEvent = mDatabase.events.get(index);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        mView = inflater.inflate(R.layout.fragment_event, null);

        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();

        setHasOptionsMenu(true);

        addDeleteListener();
        addToggleListener();

        displayEventData();
    }

    @Override
    public void onStop() {
        super.onStop();

        removeDeleteListener();
        removeToggleListener();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.add(0, 0, 0, R.string.action_edit_event)
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

    private void addDeleteListener() {
        // Add delete listener
        final Button delete_event_button = mView.findViewById(R.id.delete_event_button);
        delete_event_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Remove event
                mDatabase.removeEvent(mEvent);

                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        Log.d(TAG, "addDeleteListener: Listener added");
    }

    private void addToggleListener() {
        // Add toggle listener
        final Button toggle_event_button = mView.findViewById(R.id.toggle_event_button);
        toggle_event_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Update event
                mEvent.toggleComplete();
                mDatabase.updateEvent(mEvent);

                displayEventData();
            }
        });

        Log.d(TAG, "addToggleListener: Listener added");
    }

    private void removeDeleteListener() {
        // Remove delete listener
        final Button delete_event_button = mView.findViewById(R.id.delete_event_button);
        delete_event_button.setOnClickListener(null);

        Log.d(TAG, "removeDeleteListener: Listener removed");
    }

    private void removeToggleListener() {
        // Remove toggle listener
        final Button toggle_event_button = mView.findViewById(R.id.toggle_event_button);
        toggle_event_button.setOnClickListener(null);

        Log.d(TAG, "removeToggleListener: Listener removed");
    }

    private void displayEventData() {
        final TextView event_name_text = mView.findViewById(R.id.event_name_text);
        final TextView event_type_text = mView.findViewById(R.id.event_type_text);
        final TextView event_course_text = mView.findViewById(R.id.event_course_text);
        final TextView event_date_text = mView.findViewById(R.id.event_date_text);
        final TextView event_time_text = mView.findViewById(R.id.event_time_text);
        final TextView event_status_text = mView.findViewById(R.id.event_status_text);
        final ScrollView event_scroll = mView.findViewById(R.id.event_scroll);
        final Button toggle_event_button = mView.findViewById(R.id.toggle_event_button);

        event_name_text.setText(mEvent.getName());
        event_type_text.setText(mEvent.getType());
        event_course_text.setText(mEvent.getCourseName());
        event_date_text.setText(mEvent.getDate());
        event_time_text.setText(mEvent.getTime());

        // Display status
        String status = "Incomplete";
        if (mEvent.isComplete()) {
            status = "Complete";
        }
        event_status_text.setText(status);

        final int backgroundYellow = ContextCompat.getColor(getContext(), R.color.colorSecondary);
        final int backgroundGreen = ContextCompat.getColor(getContext(), R.color.colorTertiary);
        if (mEvent.isComplete()) {
            event_scroll.setBackgroundColor(backgroundGreen);
            toggle_event_button.setBackgroundColor(backgroundYellow);
        }
        else {
            event_scroll.setBackgroundColor(backgroundYellow);
            toggle_event_button.setBackgroundColor(backgroundGreen);
        }
    }

    private void startEditing() {
        final TextView event_name_text = mView.findViewById(R.id.event_name_text);
        final EditText event_name_edit = mView.findViewById(R.id.event_name_edit);
        final TextView event_type_text = mView.findViewById(R.id.event_type_text);
        final Spinner event_type_spinner = mView.findViewById(R.id.event_type_spinner);
        final TextView event_course_text = mView.findViewById(R.id.event_course_text);
        final Spinner event_course_spinner = mView.findViewById(R.id.event_course_spinner);
        final TextView event_date_text = mView.findViewById(R.id.event_date_text);
        final TextView event_time_text = mView.findViewById(R.id.event_time_text);
        final LinearLayout event_status_container = mView.findViewById(R.id.event_status_container);
        final Button delete_event_button = mView.findViewById(R.id.delete_event_button);
        final Button toggle_event_button = mView.findViewById(R.id.toggle_event_button);
        final Button cancel_event_button = mView.findViewById(R.id.cancel_event_button);
        final Button update_event_button = mView.findViewById(R.id.update_event_button);

        // Update UI
        mEditIcon.setVisible(false);
        event_name_text.setVisibility(View.GONE);
        event_type_text.setVisibility(View.GONE);
        event_course_text.setVisibility(View.GONE);
        event_status_container.setVisibility(View.GONE);
        delete_event_button.setVisibility(View.GONE);
        toggle_event_button.setVisibility(View.GONE);
        event_name_edit.setVisibility(View.VISIBLE);
        event_type_spinner.setVisibility(View.VISIBLE);
        event_course_spinner.setVisibility(View.VISIBLE);
        cancel_event_button.setVisibility(View.VISIBLE);
        update_event_button.setVisibility(View.VISIBLE);

        // Preview current name
        event_name_edit.setText(mEvent.getName());

        final Event newEvent = new Event();
        final Calendar cal = Calendar.getInstance();

        // Type
        newEvent.setType(mEvent.getType());
        int index = Arrays.asList("Assignment", "Exam", "Other", "Presentation", "Project", "Quiz",
                "Report", "Test").indexOf(mEvent.getType());
        event_type_spinner.setSelection(index);
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
        if (mDatabase.courses.isEmpty()) {
            event_course_spinner.setVisibility(View.GONE);

            newEvent.setCourseName(mEvent.getCourseName());
        }
        else {
            // Set adapter
            event_course_spinner.setAdapter(mDatabase.courseNamesAdapter);

            // Set current selection
            index = mDatabase.courseNames.indexOf(mEvent.getCourseName());
            event_course_spinner.setSelection(index);

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

        // Date
        newEvent.setDate(mEvent.getDate());
        event_date_text.setOnClickListener(new View.OnClickListener() {
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
                                event_date_text.setText(date);

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
        newEvent.setTime(mEvent.getTime());
        event_time_text.setOnClickListener(new View.OnClickListener() {
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
                                event_time_text.setText(time);

                                Log.d(TAG, "onTimeSet: Time set: " + time);
                            }
                        },
                        cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true);

                // Show time picker
                time_picker.getWindow();
                time_picker.show();
            }
        });

        // Cancel
        cancel_event_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Update UI
                mEditIcon.setVisible(true);
                event_name_edit.setVisibility(View.GONE);
                event_type_spinner.setVisibility(View.GONE);
                event_course_spinner.setVisibility(View.GONE);
                cancel_event_button.setVisibility(View.GONE);
                update_event_button.setVisibility(View.GONE);
                event_name_text.setVisibility(View.VISIBLE);
                event_type_text.setVisibility(View.VISIBLE);
                event_course_text.setVisibility(View.VISIBLE);
                event_status_container.setVisibility(View.VISIBLE);
                delete_event_button.setVisibility(View.VISIBLE);
                toggle_event_button.setVisibility(View.VISIBLE);

                // Clear listeners
                event_type_spinner.setOnItemSelectedListener(null);
                event_course_spinner.setOnItemSelectedListener(null);
                event_date_text.setOnClickListener(null);
                event_time_text.setOnClickListener(null);
                cancel_event_button.setOnClickListener(null);
                update_event_button.setOnClickListener(null);

                // Hide keyboard
                ((MainActivity)getActivity()).hideKeyboard();

                displayEventData();
            }
        });

        // Update
        update_event_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Update UI
                mEditIcon.setVisible(true);
                event_name_edit.setVisibility(View.GONE);
                event_type_spinner.setVisibility(View.GONE);
                event_course_spinner.setVisibility(View.GONE);
                cancel_event_button.setVisibility(View.GONE);
                update_event_button.setVisibility(View.GONE);
                event_name_text.setVisibility(View.VISIBLE);
                event_type_text.setVisibility(View.VISIBLE);
                event_course_text.setVisibility(View.VISIBLE);
                event_status_container.setVisibility(View.VISIBLE);
                delete_event_button.setVisibility(View.VISIBLE);
                toggle_event_button.setVisibility(View.VISIBLE);

                // Clear listeners
                event_type_spinner.setOnItemSelectedListener(null);
                event_course_spinner.setOnItemSelectedListener(null);
                event_date_text.setOnClickListener(null);
                event_time_text.setOnClickListener(null);
                cancel_event_button.setOnClickListener(null);
                update_event_button.setOnClickListener(null);

                // Hide keyboard
                ((MainActivity)getActivity()).hideKeyboard();

                // Set name
                String name = event_name_edit.getText().toString().trim();
                if (name.isEmpty()) {
                    newEvent.setName(mEvent.getName());
                }
                else {
                    newEvent.setName(name);
                }

                // Set status
                newEvent.setComplete(mEvent.isComplete());

                // Set uid
                newEvent.setUid(mEvent.getUid());

                // Update event
                mEvent = newEvent;
                mDatabase.updateEvent(mEvent);

                displayEventData();
            }
        });
    }
}
