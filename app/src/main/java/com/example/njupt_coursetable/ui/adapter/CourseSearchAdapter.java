package com.example.njupt_coursetable.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.njupt_coursetable.R;
import com.example.njupt_coursetable.data.model.Course;

import java.util.List;

public class CourseSearchAdapter extends ListAdapter<Course, CourseSearchAdapter.ViewHolder> {
    
    public CourseSearchAdapter(List<Course> courses) {
        super(new DiffCallback());
        submitList(courses);
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_course_search, parent, false);
        return new ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Course course = getItem(position);
        holder.bind(course);
    }
    
    public void updateCourses(List<Course> courses) {
        submitList(courses);
    }
    
    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textCourseName;
        private TextView textLocation;
        private TextView textTeacher;
        private TextView textTime;
        private TextView textWeekInfo;
        private TextView textWeekType;
        
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textCourseName = itemView.findViewById(R.id.text_course_name);
            textLocation = itemView.findViewById(R.id.text_location);
            textTeacher = itemView.findViewById(R.id.text_teacher);
            textTime = itemView.findViewById(R.id.text_time);
            textWeekInfo = itemView.findViewById(R.id.text_week_info);
            textWeekType = itemView.findViewById(R.id.text_week_type);
        }
        
        public void bind(Course course) {
            textCourseName.setText(course.getCourseName());
            textLocation.setText(course.getLocation());
            textTeacher.setText(course.getTeacher());
            
            // 设置时间
            String timeText = getTimeText(course.getDayOfWeek(), course.getTimeSlot());
            textTime.setText(timeText);
            
            // 设置周数信息
            textWeekInfo.setText(course.getWeekRange());
            
            // 设置周类型
            String weekTypeText = getWeekTypeText(course.getWeekType());
            textWeekType.setText(weekTypeText);
            
            // 根据周类型设置不同的背景色
            int backgroundColor = getWeekTypeBackgroundColor(course.getWeekType());
            textWeekType.setBackgroundColor(backgroundColor);
        }
        
        private String getTimeText(String dayOfWeek, String timeSlot) {
            // dayOfWeek已经是中文星期，如"周一"
            // timeSlot是字符串，如"1-2节"
            
            return dayOfWeek + " " + timeSlot;
        }
        
        private String getWeekTypeText(String weekType) {
            switch (weekType) {
                case "单周": return "单周";
                case "双周": return "双周";
                case "全周": return "全周";
                default: return "全周";
            }
        }
        
        private int getWeekTypeBackgroundColor(String weekType) {
            switch (weekType) {
                case "单周": return itemView.getContext().getResources().getColor(R.color.week_odd);
                case "双周": return itemView.getContext().getResources().getColor(R.color.week_even);
                case "全周": return itemView.getContext().getResources().getColor(R.color.week_all);
                default: return itemView.getContext().getResources().getColor(R.color.week_all);
            }
        }
    }
    
    private static class DiffCallback extends DiffUtil.ItemCallback<Course> {
        @Override
        public boolean areItemsTheSame(@NonNull Course oldItem, @NonNull Course newItem) {
            return oldItem.getId() == newItem.getId();
        }
        
        @Override
        public boolean areContentsTheSame(@NonNull Course oldItem, @NonNull Course newItem) {
            return oldItem.getCourseName().equals(newItem.getCourseName()) &&
                    oldItem.getLocation().equals(newItem.getLocation()) &&
                    oldItem.getTeacher().equals(newItem.getTeacher()) &&
                    oldItem.getDayOfWeek().equals(newItem.getDayOfWeek()) &&
                    oldItem.getTimeSlot().equals(newItem.getTimeSlot()) &&
                    oldItem.getWeekRange().equals(newItem.getWeekRange()) &&
                    oldItem.getWeekType().equals(newItem.getWeekType());
        }
    }
}