package edu.ramapo.ktavadze.unipal;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class EventsRecyclerAdapter extends RecyclerView.Adapter<EventsRecyclerAdapter.ViewHolder> {
    private static final String TAG = "EventsRecyclerAdapter";

    private Context mContext;
    private ArrayList<Event> mEvents;

    public EventsRecyclerAdapter(Context mContext, ArrayList<Event> mEvents) {
        this.mContext = mContext;
        this.mEvents = mEvents;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView recycler_event;
        RelativeLayout recycler_event_background;
        RelativeLayout recycler_event_foreground;
        TextView recycler_event_name;
        TextView recycler_event_type;
        TextView recycler_event_course;
        TextView recycler_event_date;
        TextView recycler_event_time;
        CheckBox recycler_event_check;

        public ViewHolder(View itemView) {
            super(itemView);

            recycler_event = itemView.findViewById(R.id.recycler_event);
            recycler_event_background = itemView.findViewById(R.id.recycler_event_background);
            recycler_event_foreground = itemView.findViewById(R.id.recycler_event_foreground);
            recycler_event_name = itemView.findViewById(R.id.recycler_event_name);
            recycler_event_type = itemView.findViewById(R.id.recycler_event_type);
            recycler_event_course = itemView.findViewById(R.id.recycler_event_course);
            recycler_event_date = itemView.findViewById(R.id.recycler_event_date);
            recycler_event_time = itemView.findViewById(R.id.recycler_event_time);
            recycler_event_check = itemView.findViewById(R.id.recycler_event_check);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_event, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get event info
        final Event event = mEvents.get(position);
        final String name = event.getName();
        final String type = event.getType();
        final String courseName = event.getCourseName();
        final String date = event.getDate();
        final String time = event.getTime();
        final String uid = event.getUid();

        // Display event info
        holder.recycler_event_name.setText(name);
        holder.recycler_event_type.setText(type);
        holder.recycler_event_course.setText(courseName);
        holder.recycler_event_date.setText(date);
        holder.recycler_event_time.setText(time);

        // Set event background
        if (event.isComplete()) {
            holder.recycler_event_foreground.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorTertiary));
        }
        else {
            holder.recycler_event_foreground.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorSecondary));
        }

        // Clear check box listener and set state (ORDER MATTERS)
        holder.recycler_event_check.setOnCheckedChangeListener(null);
        holder.recycler_event_check.setChecked(event.isComplete());

        // Set check box listener
        holder.recycler_event_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                event.setComplete(isChecked);

                // Update status in DB
                final DatabaseReference data = FirebaseDatabase.getInstance().getReference()
                        .child("events").child(User.getUid()).child(uid).child("complete");
                data.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        data.setValue(isChecked);

                        Log.d(TAG, "onDataChange: Status updated: " + isChecked);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                    }
                });
            }
        });

        // Set event click listener
        holder.recycler_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventFragment fragment = new EventFragment();
                Bundle bundle = new Bundle();
                bundle.putString("uid", uid);
                ((MainActivity)mContext).addFragment(fragment, bundle);

                Log.d(TAG, "onClick: Clicked on: " + name);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mEvents.size();
    }
}
