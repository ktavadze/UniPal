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

    private final DatabaseReference rootData = FirebaseDatabase.getInstance().getReference();
    private final DatabaseReference userData = rootData.child("users").child(User.getUid());
    private final DatabaseReference schoolsData = rootData.child("schools").child(User.getUid());
    private final DatabaseReference coursesData = rootData.child("courses").child(User.getUid());
    private final DatabaseReference eventsData = rootData.child("events").child(User.getUid());

    private Context context;

    public ArrayList<School> schools;
    public ArrayList<Course> courses;
    public ArrayList<Event> events;
    public ArrayList<CalendarDay> calendarDays;

    public SchoolsRecyclerAdapter schoolsAdapter;
    public CoursesRecyclerAdapter coursesAdapter;
    public EventsRecyclerAdapter eventsAdapter;

    public ArrayList<String> schoolNames;
    public ArrayList<String> courseNames;

    public ArrayAdapter<String> schoolNamesAdapter;
    public ArrayAdapter<String> courseNamesAdapter;

    private ChildEventListener schoolsListener;
    private ChildEventListener coursesListener;
    private ChildEventListener eventsListener;

    public Database(Context context) {
        this.context = context;
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

    public void addSchoolsListener() {
        this.schools = new ArrayList<>();
        this.schoolsAdapter = new SchoolsRecyclerAdapter(context, schools);
        this.schoolNames = new ArrayList<>();
        this.schoolNamesAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, schoolNames);
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

        Log.d(TAG, "addSchoolsListener: Listener added");
    }

    public void addCoursesListener() {
        this.courses = new ArrayList<>();
        this.coursesAdapter = new CoursesRecyclerAdapter(context, courses);
        this.courseNames = new ArrayList<>();
        this.courseNamesAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, courseNames);
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

        Log.d(TAG, "addCoursesListener: Listener added");
    }

    public void addEventsListener() {
        this.events = new ArrayList<>();
        this.calendarDays = new ArrayList<>();
        this.eventsAdapter = new EventsRecyclerAdapter(context, events);
        eventsListener = eventsData.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                final Event event = dataSnapshot.getValue(Event.class);
                calendarDays.add(event.getCalendarDay());
                events.add(event);

                eventsAdapter.notifyDataSetChanged();

                Log.d(TAG, "onChildAdded: Event read: " + event.getName());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                final Event newEvent = dataSnapshot.getValue(Event.class);
                final int index = events.indexOf(newEvent);
                final Event oldEvent = events.get(index);
                if (!newEvent.getCalendarDay().equals(oldEvent.getCalendarDay())) {
                    calendarDays.remove(oldEvent.getCalendarDay());
                    calendarDays.add(newEvent.getCalendarDay());
                }
                events.set(index, newEvent);

                eventsAdapter.notifyDataSetChanged();

                Log.d(TAG, "onChildChanged: Event updated: " + newEvent.getName());
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                final Event event = dataSnapshot.getValue(Event.class);
                calendarDays.remove(event.getCalendarDay());
                events.remove(event);

                eventsAdapter.notifyDataSetChanged();

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

        Log.d(TAG, "addEventsListener: Listener added");
    }

    public void addEventsListener(final String date) {
        this.events = new ArrayList<>();
        this.eventsAdapter = new EventsRecyclerAdapter(context, events);
        eventsListener = eventsData.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                final Event event = dataSnapshot.getValue(Event.class);
                if (date.equals(event.getDate())) {
                    events.add(event);

                    eventsAdapter.notifyDataSetChanged();

                    Log.d(TAG, "onChildAdded: Event read: " + event.getName());
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                final Event event = dataSnapshot.getValue(Event.class);
                if (events.contains(event)) {
                    if (date.equals(event.getDate())) {
                        final int index = events.indexOf(event);
                        events.set(index, event);
                    }
                    else {
                        events.remove(event);
                    }
                }
                else {
                    if (date.equals(event.getDate())) {
                        events.add(event);
                    }
                }

                eventsAdapter.notifyDataSetChanged();

                Log.d(TAG, "onChildChanged: Event updated " + event.getName());
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                final Event event = dataSnapshot.getValue(Event.class);
                if (date.equals(event.getDate())) {
                    events.remove(event);

                    eventsAdapter.notifyDataSetChanged();

                    Log.d(TAG, "onChildRemoved: Event removed: " + event.getName());
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });

        Log.d(TAG, "addEventsListener: Listener added");
    }

    public void removeSchoolsListener() {
        schoolsData.removeEventListener(schoolsListener);

        Log.d(TAG, "removeSchoolsListener: Listener removed");
    }

    public void removeCoursesListener() {
        coursesData.removeEventListener(coursesListener);

        Log.d(TAG, "removeCoursesListener: Listener removed");
    }

    public void removeEventsListener() {
        eventsData.removeEventListener(eventsListener);

        Log.d(TAG, "removeEventsListener: Listener removed");
    }
}
