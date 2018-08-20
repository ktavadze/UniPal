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

public class CoursesRecyclerAdapter extends RecyclerView.Adapter<CoursesRecyclerAdapter.ViewHolder> {

    private static final String TAG = "CoursesRecyclerAdapter";

    private Context mContext;
    private ArrayList<Course> mCourses;

    public CoursesRecyclerAdapter(Context mContext, ArrayList<Course> mCourses) {
        this.mContext = mContext;
        this.mCourses = mCourses;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_course, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Course course = mCourses.get(position);
        final String name = course.getName();
        final String uid = course.getUid();

        holder.recycler_course_name.setText(name);
        holder.recycler_course_uid.setText(uid);

        holder.recycler_course.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Clicked on " + name);

                Intent intent = new Intent(mContext, CourseActivity.class);
                intent.putExtra("name", name);
                intent.putExtra("uid", uid);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCourses.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout recycler_course;
        TextView recycler_course_name;
        TextView recycler_course_uid;

        public ViewHolder(View itemView) {
            super(itemView);

            recycler_course = itemView.findViewById(R.id.recycler_course);
            recycler_course_name = itemView.findViewById(R.id.recycler_course_name);
            recycler_course_uid = itemView.findViewById(R.id.recycler_course_uid);
        }
    }

}
