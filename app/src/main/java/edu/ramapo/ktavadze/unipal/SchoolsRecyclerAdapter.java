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

public class SchoolsRecyclerAdapter extends RecyclerView.Adapter<SchoolsRecyclerAdapter.ViewHolder> {

    private static final String TAG = "SchoolsRecyclerAdapter";

    private Context mContext;
    private ArrayList<School> mSchools;

    public SchoolsRecyclerAdapter(Context mContext, ArrayList<School> mSchools) {
        this.mContext = mContext;
        this.mSchools = mSchools;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_school, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final School school = mSchools.get(position);
        final String name = school.getName();
        final String uid = school.getUid();

        holder.recycler_school_name.setText(name);
        holder.recycler_school_uid.setText(uid);

        holder.recycler_school.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Clicked on " + name);

                Intent intent = new Intent(mContext, SchoolActivity.class);
                intent.putExtra("name", name);
                intent.putExtra("uid", uid);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mSchools.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout recycler_school;
        TextView recycler_school_name;
        TextView recycler_school_uid;

        public ViewHolder(View itemView) {
            super(itemView);

            recycler_school = itemView.findViewById(R.id.recycler_school);
            recycler_school_name = itemView.findViewById(R.id.recycler_school_name);
            recycler_school_uid = itemView.findViewById(R.id.recycler_school_uid);
        }
    }

}