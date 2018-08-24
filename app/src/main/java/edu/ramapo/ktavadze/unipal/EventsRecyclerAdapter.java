package edu.ramapo.ktavadze.unipal;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
        LinearLayout recycler_event_foreground;
        TextView recycler_event_name;
        TextView recycler_event_date;
        TextView recycler_event_time;
        TextView recycler_event_uid;

        public ViewHolder(View itemView) {
            super(itemView);

            recycler_event = itemView.findViewById(R.id.recycler_event);
            recycler_event_background = itemView.findViewById(R.id.recycler_event_background);
            recycler_event_foreground = itemView.findViewById(R.id.recycler_event_foreground);
            recycler_event_name = itemView.findViewById(R.id.recycler_event_name);
            recycler_event_date = itemView.findViewById(R.id.recycler_event_date);
            recycler_event_time = itemView.findViewById(R.id.recycler_event_time);
            recycler_event_uid = itemView.findViewById(R.id.recycler_event_uid);
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
        final String date = event.getDate();
        final String time = event.getTime();
        final String uid = event.getUid();
        final boolean complete = event.isComplete();

        // Display event info
        holder.recycler_event_name.setText(name);
        holder.recycler_event_date.setText(date);
        holder.recycler_event_time.setText(time);
        holder.recycler_event_uid.setText(uid);

        // Set event background
        if (complete) {
            holder.recycler_event_foreground.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorTertiary));
        }
        else {
            holder.recycler_event_foreground.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorSecondary));
        }

        // Set event click listener
        holder.recycler_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, EventActivity.class);
                intent.putExtra("name", name);
                intent.putExtra("date", date);
                intent.putExtra("time", time);
                intent.putExtra("uid", uid);
                intent.putExtra("complete", complete);
                mContext.startActivity(intent);

                Log.d(TAG, "onClick: Clicked on: " + name);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mEvents.size();
    }

    public void removeEvent(int position) {
        // Delete event from DB
        final String uid = mEvents.get(position).getUid();
        final DatabaseReference data = FirebaseDatabase.getInstance().getReference()
                .child("events").child(User.getUid()).child(uid);
        data.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                data.removeValue();

                Log.d(TAG, "onDataChange: Event removed");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });
    }

    public void restoreEvent(final Event event, int position) {
        // Write event to DB
        final DatabaseReference eventsData = FirebaseDatabase.getInstance().getReference()
                .child("events").child(User.getUid());
        eventsData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                eventsData.child(event.getUid()).setValue(event);

                Log.d(TAG, "onDataChange: Event restored");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });
    }
}
