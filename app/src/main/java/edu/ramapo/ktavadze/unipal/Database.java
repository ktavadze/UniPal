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

import java.util.ArrayList;

/**
 * Database class.
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

    public Database(Context context) {
        this.context = context;
    }

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
                calendarDays.add(event.getCalendarDay());

                selectedEventsAdapter.notifyDataSetChanged();

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
                if (!newEvent.getCalendarDay().equals(oldEvent.getCalendarDay())) {
                    calendarDays.set(calendarDays.indexOf(oldEvent.getCalendarDay()), newEvent.getCalendarDay());
                }

                selectedEventsAdapter.notifyDataSetChanged();

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
                calendarDays.remove(event.getCalendarDay());

                selectedEventsAdapter.notifyDataSetChanged();

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

    public void addListeners() {
        addSchoolsListener();
        addCoursesListener();
        addEventsListener();

        Log.d(TAG, "addListeners: Listeners added");
    }

    public void removeListeners() {
        schoolsData.removeEventListener(schoolsListener);
        coursesData.removeEventListener(coursesListener);
        eventsData.removeEventListener(eventsListener);

        Log.d(TAG, "removeListeners: Listeners removed");
    }

    public void selectAllEvents() {
        selectedDates = null;
        selectedEvents = new ArrayList<>(allEvents);
        selectedEventsAdapter = new EventsRecyclerAdapter(context, selectedEvents);
    }

    public void selectEvents(final ArrayList<String> dates) {
        selectedDates = dates;
        selectedEvents = new ArrayList<>();
        for (Event e : allEvents) {
            if (dates.contains(e.getDate())) {
                selectedEvents.add(e);
            }
        }
        selectedEventsAdapter = new EventsRecyclerAdapter(context, selectedEvents);
    }

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
