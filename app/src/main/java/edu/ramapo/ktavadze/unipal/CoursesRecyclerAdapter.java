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

    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout recycler_course;
        TextView recycler_course_name;
        TextView recycler_course_department;
        TextView recycler_course_number;
        TextView recycler_course_section;

        public ViewHolder(View itemView) {
            super(itemView);

            recycler_course = itemView.findViewById(R.id.recycler_course);
            recycler_course_name = itemView.findViewById(R.id.recycler_course_name);
            recycler_course_department = itemView.findViewById(R.id.recycler_course_department);
            recycler_course_number = itemView.findViewById(R.id.recycler_course_number);
            recycler_course_section = itemView.findViewById(R.id.recycler_course_section);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_course, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get course info
        final Course course = mCourses.get(position);
        final String name = course.getName();
        final String department = course.getDepartment();
        final String number = course.getNumber();
        final String section = course.getSection();
        final String uid = course.getUid();

        // Display course info
        holder.recycler_course_name.setText(name);
        holder.recycler_course_department.setText(department);
        holder.recycler_course_number.setText(number);
        holder.recycler_course_section.setText(section);

        // Set course click listener
        holder.recycler_course.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CourseActivity.class);
                intent.putExtra("name", name);
                intent.putExtra("department", department);
                intent.putExtra("number", number);
                intent.putExtra("section", section);
                intent.putExtra("uid", uid);
                mContext.startActivity(intent);

                Log.d(TAG, "onClick: Clicked on: " + name);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCourses.size();
    }
}
