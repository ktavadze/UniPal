package edu.ramapo.ktavadze.unipal;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.util.ArrayList;
import java.util.HashSet;

public class CalendarActivity extends AppCompatActivity implements OnDateSelectedListener, OnMonthChangedListener {

    private static final String TAG = "CalendarActivity";

    private DatabaseReference mEventsData;
    private ChildEventListener mEventsListener;

    private ArrayList<Event> mEvents;
    private HashSet<CalendarDay> mDates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Calendar");

        addEventsListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        removeEventsListener();
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        // Format date
        Integer month = date.getMonth() + 1;
        Integer day = date.getDay();
        Integer year = date.getYear();
        String dateString;
        if (month < 10 && day < 10) {
            dateString = "0" + month + "/0" + day + "/" + year;
        }
        else if (month < 10) {
            dateString = "0" + month + "/" + day + "/" + year;
        }
        else if (day < 10) {
            dateString = "" + month + "/0" + day + "/" + year;
        }
        else {
            dateString = "" + month + "/" + day + "/" + year;
        }

        // Pass date
        Intent intent = new Intent(CalendarActivity.this, DateActivity.class);
        intent.putExtra("date", dateString);
        startActivity(intent);

        Log.d(TAG, "onDateSelected: Date selected: " + dateString);
    }

    @Override
    public void onMonthChanged(MaterialCalendarView materialCalendarView, CalendarDay calendarDay) {
        Log.d(TAG, "onMonthChanged: Month changed: " + (calendarDay.getMonth() + 1));
    }

    public class EventDecorator implements DayViewDecorator {

        private final HashSet<CalendarDay> dates;
        private final int color;
        private final ColorDrawable background;

        public EventDecorator(HashSet<CalendarDay> dates) {
            this.dates = new HashSet<>(dates);
            this.color = R.color.colorPrimary;
            this.background = new ColorDrawable(color);
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return dates.contains(day);
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.addSpan(new DotSpan(5, color));
            view.setBackgroundDrawable(background);
        }
    }

    public void addEventsListener() {
        // Set calendar listeners
        final MaterialCalendarView events_calendar = findViewById(R.id.events_calendar);
        events_calendar.setOnDateChangedListener(this);
        events_calendar.setOnMonthChangedListener(this);

        // Init events and dates
        mEvents = new ArrayList<>();
        mDates = new HashSet<>();

        // Add events listener
        mEventsData = FirebaseDatabase.getInstance().getReference().child("events").child(User.getUid());
        mEventsListener = mEventsData.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                // Read event
                Event event = dataSnapshot.getValue(Event.class);
                mEvents.add(event);

                // Read date
                String [] dateTokens = event.getDate().split("/");
                Integer month = Integer.parseInt(dateTokens[0]);
                Integer day = Integer.parseInt(dateTokens[1]);
                Integer year = Integer.parseInt(dateTokens[2]);
                CalendarDay date = CalendarDay.from(year, month - 1, day);
                mDates.add(date);

                // Update decorator
                events_calendar.removeDecorators();
                events_calendar.addDecorator(new EventDecorator(mDates));

                Log.d(TAG, "onChildAdded: Event read: " + event.getName());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                // Remove event
                Event event = dataSnapshot.getValue(Event.class);
                mEvents.remove(event);

                // Remove date
                String [] dateTokens = event.getDate().split("/");
                Integer month = Integer.parseInt(dateTokens[0]);
                Integer day = Integer.parseInt(dateTokens[1]);
                Integer year = Integer.parseInt(dateTokens[2]);
                CalendarDay date = CalendarDay.from(year, month - 1, day);
                mDates.remove(date);

                // Update decorator
                events_calendar.removeDecorators();
                events_calendar.addDecorator(new EventDecorator(mDates));

                Log.d(TAG, "onChildChanged: Event removed: " + event.getName());
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

    public void removeEventsListener() {
        // Remove events listener
        mEventsData.removeEventListener(mEventsListener);

        Log.d(TAG, "removeEventsListener: Listener removed");
    }
}
