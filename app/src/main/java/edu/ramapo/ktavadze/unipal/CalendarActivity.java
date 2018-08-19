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

    private DatabaseReference mEventData;

    private ArrayList<Event> mEvents = new ArrayList<>();
    private HashSet<CalendarDay> mDates = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Calendar");

        mEventData = FirebaseDatabase.getInstance().getReference().child("events").child(User.getUid());

        getEventData();
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

        Log.d(TAG, "onDateSelected: Selected " + dateString);
    }

    @Override
    public void onMonthChanged(MaterialCalendarView materialCalendarView, CalendarDay calendarDay) {
        Log.d(TAG, "onMonthChanged: Month changed to " + calendarDay.getMonth());
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

    public void getEventData() {
        final MaterialCalendarView events_calendar = findViewById(R.id.events_calendar);
        events_calendar.setOnDateChangedListener(this);
        events_calendar.setOnMonthChangedListener(this);
        mEventData.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                // Get event
                Event event = dataSnapshot.getValue(Event.class);
                mEvents.add(event);

                // Get date
                String [] dateTokens = event.getDate().split("/");
                Integer month = Integer.parseInt(dateTokens[0]);
                Integer day = Integer.parseInt(dateTokens[1]);
                Integer year = Integer.parseInt(dateTokens[2]);
                CalendarDay date = CalendarDay.from(year, month - 1, day);
                mDates.add(date);

                // Add decorator
                events_calendar.removeDecorators();
                events_calendar.addDecorator(new EventDecorator(mDates));

                Log.d(TAG, "onChildAdded: Event read");
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

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
}
