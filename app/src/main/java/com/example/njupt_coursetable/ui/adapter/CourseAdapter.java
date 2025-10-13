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

/**
 * 课程列表适配器
 * 用于在RecyclerView中显示课程列表
 */
public class CourseAdapter extends ListAdapter<Course, CourseAdapter.CourseViewHolder> {

    /**
     * 课程点击监听器
     */
    private OnCourseClickListener onCourseClickListener;

    /**
     * 构造函数
     * @param onCourseClickListener 课程点击监听器
     */
    public CourseAdapter(OnCourseClickListener onCourseClickListener) {
        super(DIFF_CALLBACK);
        this.onCourseClickListener = onCourseClickListener;
    }

    /**
     * DiffUtil回调，用于计算列表差异
     */
    private static final DiffUtil.ItemCallback<Course> DIFF_CALLBACK = new DiffUtil.ItemCallback<Course>() {
        @Override
        public boolean areItemsTheSame(@NonNull Course oldItem, @NonNull Course newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Course oldItem, @NonNull Course newItem) {
            return oldItem.getName().equals(newItem.getName()) &&
                    oldItem.getLocation().equals(newItem.getLocation()) &&
                    oldItem.getWeeks().equals(newItem.getWeeks()) &&
                    oldItem.getDayOfWeek().equals(newItem.getDayOfWeek()) &&
                    oldItem.getStartTime().equals(newItem.getStartTime()) &&
                    oldItem.getEndTime().equals(newItem.getEndTime()) &&
                    oldItem.getTeacher().equals(newItem.getTeacher());
        }
    };

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_course, parent, false);
        return new CourseViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        Course currentCourse = getItem(position);
        holder.bind(currentCourse);
    }

    /**
     * 课程视图持有者
     */
    class CourseViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewCourseName;
        private TextView textViewLocation;
        private TextView textViewTime;
        private TextView textViewTeacher;
        private TextView textViewWeeks;

        /**
         * 构造函数
         * @param itemView 视图项
         */
        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewCourseName = itemView.findViewById(R.id.text_view_course_name);
            textViewLocation = itemView.findViewById(R.id.text_view_location);
            textViewTime = itemView.findViewById(R.id.text_view_time);
            textViewTeacher = itemView.findViewById(R.id.text_view_teacher);
            textViewWeeks = itemView.findViewById(R.id.text_view_weeks);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (onCourseClickListener != null && position != RecyclerView.NO_POSITION) {
                    onCourseClickListener.onCourseClick(getItem(position));
                }
            });
        }

        /**
         * 绑定数据到视图
         * @param course 课程对象
         */
        public void bind(Course course) {
            textViewCourseName.setText(course.getName());
            textViewLocation.setText(course.getLocation());
            textViewTime.setText(course.getStartTime() + " - " + course.getEndTime());
            textViewTeacher.setText(course.getTeacher());
            textViewWeeks.setText(course.getWeeks());
        }
    }

    /**
     * 课程点击监听器接口
     */
    public interface OnCourseClickListener {
        /**
         * 课程点击回调方法
         * @param course 被点击的课程
         */
        void onCourseClick(Course course);
    }
}