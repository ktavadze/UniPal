package edu.ramapo.ktavadze.unipal;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class SchoolsRecyclerAdapter extends RecyclerView.Adapter<SchoolsRecyclerAdapter.ViewHolder> {
    private static final String TAG = "SchoolsRecyclerAdapter";

    private Context mContext;
    private ArrayList<School> mSchools;

    public SchoolsRecyclerAdapter(Context mContext, ArrayList<School> mSchools) {
        this.mContext = mContext;
        this.mSchools = mSchools;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView recycler_school;
        TextView recycler_school_name;
        TextView recycler_school_year;
        TextView recycler_school_major;
        TextView recycler_school_minor;

        public ViewHolder(View itemView) {
            super(itemView);

            recycler_school = itemView.findViewById(R.id.recycler_school);
            recycler_school_name = itemView.findViewById(R.id.recycler_school_name);
            recycler_school_year = itemView.findViewById(R.id.recycler_school_year);
            recycler_school_major = itemView.findViewById(R.id.recycler_school_major);
            recycler_school_minor = itemView.findViewById(R.id.recycler_school_minor);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_school, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get school info
        final School school = mSchools.get(position);
        final String name = school.getName();
        final String year = school.getYear();
        final String major = school.getMajor();
        final String minor = school.getMinor();
        final String uid = school.getUid();

        // Display school info
        holder.recycler_school_name.setText(name);
        holder.recycler_school_year.setText(year);
        holder.recycler_school_major.setText(major);
        holder.recycler_school_minor.setText(minor);
        holder.recycler_school_year.setText(year);

        // Set school click listener
        holder.recycler_school.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SchoolFragment fragment = new SchoolFragment();
                Bundle bundle = new Bundle();
                bundle.putString("uid", uid);
                ((MainActivity)mContext).addFragment(fragment, bundle);

                Log.d(TAG, "onClick: Clicked on: " + name);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mSchools.size();
    }
}
