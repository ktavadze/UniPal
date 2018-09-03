package edu.ramapo.ktavadze.unipal;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

import java.util.ArrayList;
import java.util.HashSet;

public class CalendarFragment extends Fragment implements OnDateSelectedListener, OnMonthChangedListener {
    private static final String TAG = "CalendarFragment";

    private DatabaseReference mEventsData;
    private ChildEventListener mEventsListener;

    private ArrayList<Event> mEvents;
    private HashSet<CalendarDay> mDates;

    private View mView;

    public CalendarFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        mView = inflater.inflate(R.layout.fragment_calendar, null);

        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();

        addEventsListener();
    }

    @Override
    public void onStop() {
        super.onStop();

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
        DateFragment fragment = new DateFragment();
        Bundle bundle = new Bundle();
        bundle.putString("date", dateString);
        ((MainActivity)getActivity()).addFragment(fragment, bundle);

        Log.d(TAG, "onDateSelected: Date selected: " + dateString);
    }

    @Override
    public void onMonthChanged(MaterialCalendarView materialCalendarView, CalendarDay calendarDay) {
        Log.d(TAG, "onMonthChanged: Month changed: " + (calendarDay.getMonth() + 1));
    }

    public class EventDecorator implements DayViewDecorator {
        private final HashSet<CalendarDay> dates;
        private final int backgroundColor;
        private final int textColor;

        public EventDecorator(HashSet<CalendarDay> dates) {
            this.dates = dates;
            this.backgroundColor = ContextCompat.getColor(getContext(), R.color.colorAccent);
            this.textColor = ContextCompat.getColor(getContext(), R.color.colorPrimary);
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return dates.contains(day);
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.addSpan(new ForegroundColorSpan(textColor));
            view.setBackgroundDrawable(new ColorDrawable(backgroundColor));
        }
    }

    private void addEventsListener() {
        // Set calendar listeners
        final MaterialCalendarView events_calendar = mView.findViewById(R.id.events_calendar);
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
                // Add event
                Event event = dataSnapshot.getValue(Event.class);
                mEvents.add(event);

                // Add date
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
                // Update event
                Event event = dataSnapshot.getValue(Event.class);
                int index = mEvents.indexOf(event);
                String oldDate = mEvents.get(index).getDate();
                mEvents.set(index, event);

                // Add new date
                String [] dateTokens = event.getDate().split("/");
                Integer month = Integer.parseInt(dateTokens[0]);
                Integer day = Integer.parseInt(dateTokens[1]);
                Integer year = Integer.parseInt(dateTokens[2]);
                CalendarDay date = CalendarDay.from(year, month - 1, day);
                if (!mDates.contains(date)) {
                    mDates.add(date);
                }

                // Remove old date
                dateTokens = oldDate.split("/");
                month = Integer.parseInt(dateTokens[0]);
                day = Integer.parseInt(dateTokens[1]);
                year = Integer.parseInt(dateTokens[2]);
                date = CalendarDay.from(year, month - 1, day);
                boolean remove = true;
                for (Event e : mEvents) {
                    if (e.getDate().equals(oldDate)) {
                        remove = false;
                    }
                }
                if (remove) {
                    mDates.remove(date);
                }

                // Update decorator
                events_calendar.removeDecorators();
                events_calendar.addDecorator(new EventDecorator(mDates));

                Log.d(TAG, "onChildChanged: Event updated: " + event.getName());
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

    private void removeEventsListener() {
        // Remove events listener
        mEventsData.removeEventListener(mEventsListener);

        Log.d(TAG, "removeEventsListener: Listener removed");
    }
}
