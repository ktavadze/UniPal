package edu.ramapo.ktavadze.unipal;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Database Class to read/write data from/to the Firebase database.
 * Note: several members are public for ease of access.
 */

public class Database {
    private static final String TAG = "Database";

    private Context context;

    private final DatabaseReference rootData = FirebaseDatabase.getInstance().getReference();
    private final DatabaseReference userData = rootData.child("users").child(User.getUid());
    private final DatabaseReference schoolsData = rootData.child("schools").child(User.getUid());
    private final DatabaseReference coursesData = rootData.child("courses").child(User.getUid());
    private final DatabaseReference eventsData = rootData.child("events").child(User.getUid());

    private ChildEventListener schoolsListener;
    private ChildEventListener coursesListener;
    private ChildEventListener eventsListener;

    public ArrayList<School> schools;
    public ArrayList<Course> courses;
    public ArrayList<Event> allEvents;
    public ArrayList<Event> selectedEvents;

    public SchoolsRecyclerAdapter schoolsAdapter;
    public CoursesRecyclerAdapter coursesAdapter;
    public EventsRecyclerAdapter selectedEventsAdapter;

    public ArrayList<String> schoolNames;
    public ArrayList<String> courseNames;
    public ArrayList<String> selectedDates;
    public ArrayList<CalendarDay> calendarDays;

    public ArrayAdapter<String> schoolNamesAdapter;
    public ArrayAdapter<String> courseNamesAdapter;

    public int filter;

    public Database(Context context) {
        this.context = context;
    }

    /**/
    /*
    NAME

    addSchoolsListener - adds schools listener.

    SYNOPSIS

    private void addSchoolsListener();

    DESCRIPTION

    Will add a child event listener to the schools list associated with the current user in the DB.

    RETURNS

    N/A
    */
    /**/
    private void addSchoolsListener() {
        schools = new ArrayList<>();
        schoolsAdapter = new SchoolsRecyclerAdapter(context, schools);
        schoolNames = new ArrayList<>();
        schoolNamesAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, schoolNames);
        schoolsListener = schoolsData.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                final School school = dataSnapshot.getValue(School.class);
                schools.add(school);
                schoolNames.add(school.getName());

                schoolsAdapter.notifyDataSetChanged();
                schoolNamesAdapter.notifyDataSetChanged();

                Log.d(TAG, "onChildAdded: School read: " + school.getName());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                final School school = dataSnapshot.getValue(School.class);
                final int index = schools.indexOf(school);
                schools.set(index, school);
                schoolNames.set(index, school.getName());

                schoolsAdapter.notifyDataSetChanged();
                schoolNamesAdapter.notifyDataSetChanged();

                Log.d(TAG, "onChildChanged: School updated: " + school.getName());
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                final School school = dataSnapshot.getValue(School.class);
                schools.remove(school);
                schoolNames.remove(school.getName());

                schoolsAdapter.notifyDataSetChanged();
                schoolNamesAdapter.notifyDataSetChanged();

                Log.d(TAG, "onChildRemoved: School removed: " + school.getName());
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

    /**/
    /*
    NAME

    addCoursesListener - adds courses listener.

    SYNOPSIS

    private void addCoursesListener();

    DESCRIPTION

    Will add a child event listener to the courses list associated with the current user in the DB.

    RETURNS

    N/A
    */
    /**/
    private void addCoursesListener() {
        courses = new ArrayList<>();
        coursesAdapter = new CoursesRecyclerAdapter(context, courses);
        courseNames = new ArrayList<>();
        courseNamesAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, courseNames);
        coursesListener = coursesData.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                final Course course = dataSnapshot.getValue(Course.class);
                courses.add(course);
                courseNames.add(course.getName());

                coursesAdapter.notifyDataSetChanged();
                courseNamesAdapter.notifyDataSetChanged();

                Log.d(TAG, "onChildAdded: Course read: " + course.getName());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                final Course course = dataSnapshot.getValue(Course.class);
                final int index = courses.indexOf(course);
                courses.set(index, course);
                courseNames.set(index, course.getName());

                coursesAdapter.notifyDataSetChanged();
                courseNamesAdapter.notifyDataSetChanged();

                Log.d(TAG, "onChildChanged: Course updated: " + course.getName());
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                final Course course = dataSnapshot.getValue(Course.class);
                courses.remove(course);
                courseNames.remove(course.getName());

                coursesAdapter.notifyDataSetChanged();
                courseNamesAdapter.notifyDataSetChanged();

                Log.d(TAG, "onChildRemoved: Course removed: " + course.getName());
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

    /**/
    /*
    NAME

    addEventsListener - adds events listener.

    SYNOPSIS

    private void addEventsListener();

    DESCRIPTION

    Will add a child event listener to the events list associated with the current user in the DB.

    RETURNS

    N/A
    */
    /**/
    private void addEventsListener() {
        allEvents = new ArrayList<>();
        selectedEvents = new ArrayList<>();
        selectedEventsAdapter = new EventsRecyclerAdapter(context, selectedEvents);
        calendarDays = new ArrayList<>();
        eventsListener = eventsData.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                final Event event = dataSnapshot.getValue(Event.class);
                allEvents.add(event);
                if (selectedDates == null) {
                    selectedEvents.add(event);
                }
                else {
                    if (selectedDates.contains(event.getDate())) {
                        selectedEvents.add(event);
                    }
                }

                selectedEventsAdapter.notifyDataSetChanged();

                // Add to calendar days
                calendarDays.add(event.getCalendarDay());

                // Schedule alarm
                if (!event.getAlarm().equals("No alarm") && !event.isComplete()) {
                    AlarmScheduler.scheduleAlarm(context, event);
                }

                Log.d(TAG, "onChildAdded: Event read: " + event.getName());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                final Event newEvent = dataSnapshot.getValue(Event.class);
                final Event oldEvent = allEvents.get(allEvents.indexOf(newEvent));
                allEvents.set(allEvents.indexOf(oldEvent), newEvent);
                if (selectedDates == null) {
                    selectedEvents.set(selectedEvents.indexOf(oldEvent), newEvent);
                }
                else {
                    if (newEvent.getDate().equals(oldEvent.getDate())) {
                        if (selectedEvents.contains(oldEvent)) {
                            selectedEvents.set(selectedEvents.indexOf(oldEvent), newEvent);
                        }
                    }
                    else {
                        if (selectedEvents.contains(oldEvent)) {
                            if (selectedDates.contains(newEvent.getDate())) {
                                selectedEvents.set(selectedEvents.indexOf(oldEvent), newEvent);
                            }
                            else {
                                selectedEvents.remove(oldEvent);
                            }
                        }
                        else {
                            if (selectedDates.contains(newEvent.getDate())) {
                                selectedEvents.add(newEvent);
                            }
                        }
                    }
                }

                selectedEventsAdapter.notifyDataSetChanged();

                // Update calendar days
                if (!newEvent.getCalendarDay().equals(oldEvent.getCalendarDay())) {
                    calendarDays.set(calendarDays.indexOf(oldEvent.getCalendarDay()), newEvent.getCalendarDay());
                }

                // Update alarm
                if (newEvent.getAlarm().equals("No alarm") || newEvent.isComplete()) {
                    AlarmScheduler.cancelAlarm(context, oldEvent);
                }
                else {
                    AlarmScheduler.scheduleAlarm(context, newEvent);
                }

                Log.d(TAG, "onChildChanged: Event updated: " + newEvent.getName());
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                final Event event = dataSnapshot.getValue(Event.class);
                allEvents.remove(event);
                if (selectedDates == null) {
                    selectedEvents.remove(event);
                }
                else {
                    if (selectedDates.contains(event.getDate())) {
                        selectedEvents.remove(event);
                    }
                }

                selectedEventsAdapter.notifyDataSetChanged();

                // Remove from calendar days
                calendarDays.remove(event.getCalendarDay());

                // Cancel alarm
                if (!event.getAlarm().equals("No alarm") && !event.isComplete()) {
                    AlarmScheduler.cancelAlarm(context, event);
                }

                Log.d(TAG, "onChildRemoved: Event removed: " + event.getName());
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

    /**/
    /*
    NAME

    addListeners - adds listeners.

    SYNOPSIS

    public void addListeners();

    DESCRIPTION

    Will add listeners to the lists associated with the current user in the DB.

    RETURNS

    N/A
    */
    /**/
    public void addListeners() {
        addSchoolsListener();
        addCoursesListener();
        addEventsListener();

        Log.d(TAG, "addListeners: Listeners added");
    }

    /**/
    /*
    NAME

    removeListeners - removes listeners.

    SYNOPSIS

    public void removeListeners();

    DESCRIPTION

    Will remove listeners from the lists associated with the current user in the DB.

    RETURNS

    N/A
    */
    /**/
    public void removeListeners() {
        schoolsData.removeEventListener(schoolsListener);
        coursesData.removeEventListener(coursesListener);
        eventsData.removeEventListener(eventsListener);

        Log.d(TAG, "removeListeners: Listeners removed");
    }

    /**/
    /*
    NAME

    selectAllEvents - selects all events.

    SYNOPSIS

    public void selectAllEvents();

    DESCRIPTION

    Will select all events associated with the current user.

    RETURNS

    N/A
    */
    /**/
    public void selectAllEvents() {
        filter = 100;
        selectedDates = null;
        selectedEvents = new ArrayList<>(allEvents);
        selectedEventsAdapter = new EventsRecyclerAdapter(context, selectedEvents);
    }

    /**/
    /*
    NAME

    selectEvents - selects events.

    SYNOPSIS

    public void selectEvents(final int days);
    days--> the number of days ahead of the current day to select events for.

    DESCRIPTION

    Will select events as far ahead as the number of days specified.

    RETURNS

    N/A
    */
    /**/
    public void selectEvents(final int days) {
        filter = days;
        selectedDates = new ArrayList<>();
        selectedEvents = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        for (int i = 0; i < days; i++) {
            Calendar calendar = new GregorianCalendar();
            calendar.add(Calendar.DATE, i);
            String date = sdf.format(calendar.getTime());
            selectedDates.add(date);
        }
        for (Event e : allEvents) {
            if (selectedDates.contains(e.getDate())) {
                selectedEvents.add(e);
            }
        }
        selectedEventsAdapter = new EventsRecyclerAdapter(context, selectedEvents);

        Log.d(TAG, "selectEvents: Selected dates: " + selectedDates.toString());
    }

    /**/
    /*
    NAME

    selectEvents - selects events.

    SYNOPSIS

    public void selectEvents(final String date);
    date--> the date to select events for.

    DESCRIPTION

    Will select events matching the specified date.

    RETURNS

    N/A
    */
    /**/
    public void selectEvents(final String date) {
        selectedDates = new ArrayList<>();
        selectedDates.add(date);
        selectedEvents = new ArrayList<>();
        for (Event e : allEvents) {
            if (date.equals(e.getDate())) {
                selectedEvents.add(e);
            }
        }
        selectedEventsAdapter = new EventsRecyclerAdapter(context, selectedEvents);
    }

    /**/
    /*
    NAME

    addSchool - adds school.

    SYNOPSIS

    public void addSchool(final School school);
    school--> the school to be added.

    DESCRIPTION

    Will add the specified school to the schools list associated with the current user in the DB.

    RETURNS

    N/A
    */
    /**/
    public void addSchool(final School school) {
        schoolsData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String uid = schoolsData.push().getKey();
                school.setUid(uid);

                schoolsData.child(uid).setValue(school);

                Log.d(TAG, "onDataChange: School added: " + school.getName());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });
    }

    /**/
    /*
    NAME

    updateSchool - updates school.

    SYNOPSIS

    public void updateSchool(final School school);
    school--> the school to be updated.

    DESCRIPTION

    Will update the specified school in the schools list associated with the current user in the DB.

    RETURNS

    N/A
    */
    /**/
    public void updateSchool(final School school) {
        schoolsData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                schoolsData.child(school.getUid()).setValue(school);

                Log.d(TAG, "onDataChange: School updated: " + school.getName());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });
    }

    /**/
    /*
    NAME

    removeSchool - removes school.

    SYNOPSIS

    public void removeSchool(final School school);
    school--> the school to be removed.

    DESCRIPTION

    Will remove the specified school from the schools list associated with the current user in the DB.

    RETURNS

    N/A
    */
    /**/
    public void removeSchool(final School school) {
        schoolsData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                schoolsData.child(school.getUid()).removeValue();

                Log.d(TAG, "onDataChange: School deleted: " + school.getName());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });
    }

    /**/
    /*
    NAME

    addCourse - adds course.

    SYNOPSIS

    public void addCourse(final Course course);
    course--> the course to be added.

    DESCRIPTION

    Will add the specified course to the courses list associated with the current user in the DB.

    RETURNS

    N/A
    */
    /**/
    public void addCourse(final Course course) {
        coursesData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String uid = coursesData.push().getKey();
                course.setUid(uid);

                coursesData.child(uid).setValue(course);

                Log.d(TAG, "onDataChange: Course added: " + course.getName());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });
    }

    /**/
    /*
    NAME

    updateCourse - updates course.

    SYNOPSIS

    public void updateCourse(final Course course);
    course--> the course to be updated.

    DESCRIPTION

    Will update the specified course in the courses list associated with the current user in the DB.

    RETURNS

    N/A
    */
    /**/
    public void updateCourse(final Course course) {
        coursesData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                coursesData.child(course.getUid()).setValue(course);

                Log.d(TAG, "onDataChange: Course updated: " + course.getName());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });
    }

    /**/
    /*
    NAME

    removeCourse - removes course.

    SYNOPSIS

    public void removeCourse(final Course course);
    course--> the course to be removed.

    DESCRIPTION

    Will remove the specified course from the courses list associated with the current user in the DB.

    RETURNS

    N/A
    */
    /**/
    public void removeCourse(final Course course) {
        coursesData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                coursesData.child(course.getUid()).removeValue();

                Log.d(TAG, "onDataChange: Course deleted: " + course.getName());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });
    }

    /**/
    /*
    NAME

    addEvent - adds event.

    SYNOPSIS

    public void addEvent(final Event event);
    event--> the event to be added.

    DESCRIPTION

    Will add the specified event to the events list associated with the current user in the DB.

    RETURNS

    N/A
    */
    /**/
    public void addEvent(final Event event) {
        eventsData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String uid = eventsData.push().getKey();
                event.setUid(uid);

                eventsData.child(uid).setValue(event);

                Log.d(TAG, "onDataChange: Event added: " + event.getName());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });
    }

    /**/
    /*
    NAME

    updateEvent - updates event.

    SYNOPSIS

    public void updateEvent(final Event event);
    event--> the event to be updated.

    DESCRIPTION

    Will update the specified event in the events list associated with the current user in the DB.

    RETURNS

    N/A
    */
    /**/
    public void updateEvent(final Event event) {
        eventsData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                eventsData.child(event.getUid()).setValue(event);

                Log.d(TAG, "onDataChange: Event updated: " + event.getName());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });
    }

    /**/
    /*
    NAME

    removeEvent - removes event.

    SYNOPSIS

    public void removeEvent(final Event event);
    event--> the event to be removed.

    DESCRIPTION

    Will remove the specified event from the events list associated with the current user in the DB.

    RETURNS

    N/A
    */
    /**/
    public void removeEvent(final Event event) {
        eventsData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                eventsData.child(event.getUid()).removeValue();

                Log.d(TAG, "onDataChange: Event deleted: " + event.getName());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });
    }
}
