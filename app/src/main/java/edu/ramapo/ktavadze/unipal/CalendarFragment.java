package edu.ramapo.ktavadze.unipal;

import android.graphics.Color;
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

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

public class CalendarFragment extends Fragment implements OnDateSelectedListener, OnMonthChangedListener {
    private static final String TAG = "CalendarFragment";

    private Database mDatabase;

    private View mView;

    public CalendarFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDatabase = ((MainActivity)getActivity()).mDatabase;
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

        getActivity().setTitle("Calendar");

        updateDecorator();
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

    private class EventDecorator implements DayViewDecorator {
        private final int textColor;
        private final int backgroundColor;

        private EventDecorator() {
            this.textColor = Color.WHITE;
            this.backgroundColor = ContextCompat.getColor(getContext(), R.color.colorAccent);
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return mDatabase.calendarDays.contains(day);
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.addSpan(new ForegroundColorSpan(textColor));
            view.setBackgroundDrawable(new ColorDrawable(backgroundColor));
        }
    }

    private void updateDecorator() {
        // Set listeners
        final MaterialCalendarView events_calendar = mView.findViewById(R.id.events_calendar);
        events_calendar.setOnDateChangedListener(this);
        events_calendar.setOnMonthChangedListener(this);

        // Update decorator
        events_calendar.removeDecorators();
        events_calendar.addDecorator(new EventDecorator());

        Log.d(TAG, "updateDecorator: Decorator updated");
    }
}
