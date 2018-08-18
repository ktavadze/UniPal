package edu.ramapo.ktavadze.unipal;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class EventsRecyclerAdapter extends RecyclerView.Adapter<EventsRecyclerAdapter.ViewHolder> {

    private static final String TAG = "EventsRecyclerAdapter";

    private Context mContext;
    private ArrayList<Event> mEvents;

    public EventsRecyclerAdapter(Context mContext, ArrayList<Event> mEvents) {
        this.mContext = mContext;
        this.mEvents = mEvents;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_event, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Event event = mEvents.get(position);
        final String name = event.getName();
        final String date = event.getDate();
        final String time = event.getTime();
        final String uid = event.getUid();

        holder.recycler_event_name.setText(name);
        holder.recycler_event_date.setText(date);
        holder.recycler_event_time.setText(time);
        holder.recycler_event_uid.setText(uid);

        holder.recycler_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Clicked on " + name);

                Intent intent = new Intent(mContext, EventActivity.class);
                intent.putExtra("name", name);
                intent.putExtra("date", date);
                intent.putExtra("time", time);
                intent.putExtra("uid", uid);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mEvents.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout recycler_event;
        TextView recycler_event_name;
        TextView recycler_event_date;
        TextView recycler_event_time;
        TextView recycler_event_uid;

        public ViewHolder(View itemView) {
            super(itemView);

            recycler_event = itemView.findViewById(R.id.recycler_event);
            recycler_event_name = itemView.findViewById(R.id.recycler_event_name);
            recycler_event_date = itemView.findViewById(R.id.recycler_event_date);
            recycler_event_time = itemView.findViewById(R.id.recycler_event_time);
            recycler_event_uid = itemView.findViewById(R.id.recycler_event_uid);
        }
    }

}
