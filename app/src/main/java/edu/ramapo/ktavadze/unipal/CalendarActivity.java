package edu.ramapo.ktavadze.unipal;

import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.util.Collection;
import java.util.HashSet;

public class CalendarActivity extends AppCompatActivity implements OnDateSelectedListener, OnMonthChangedListener {

    private static final String TAG = "CalendarActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Calendar");

        final MaterialCalendarView events_calendar = findViewById(R.id.events_calendar);
        events_calendar.setOnDateChangedListener(this);
        events_calendar.setOnMonthChangedListener(this);

        HashSet<CalendarDay> days = new HashSet<>();
        CalendarDay today = CalendarDay.today();
        CalendarDay mid = CalendarDay.from(2018, 7, 15);
        days.add(today);
        days.add(mid);
        events_calendar.addDecorator(new EventDecorator(days));
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        Log.d(TAG, "onDateSelected: Selected " + date.toString());
    }

    @Override
    public void onMonthChanged(MaterialCalendarView materialCalendarView, CalendarDay calendarDay) {
        Log.d(TAG, "onMonthChanged: Month changed to " + calendarDay.getMonth());
    }

    public class EventDecorator implements DayViewDecorator {

        private final HashSet<CalendarDay> dates;
        private final int color;
        private final ColorDrawable background;

        public EventDecorator(Collection<CalendarDay> dates) {
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
            view.addSpan(new DotSpan(10, color));
            view.setBackgroundDrawable(background);
        }
    }
}
