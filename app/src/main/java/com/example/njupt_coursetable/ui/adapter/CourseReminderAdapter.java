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
import com.example.njupt_coursetable.utils.TimeUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 课程提醒列表适配器
 * 用于在RecyclerView中显示需要提醒的课程列表
 */
public class CourseReminderAdapter extends ListAdapter<Course, CourseReminderAdapter.CourseReminderViewHolder> {

    /**
     * 课程点击监听器
     */
    private OnCourseReminderClickListener onCourseReminderClickListener;

    /**
     * 构造函数
     * @param onCourseReminderClickListener 课程点击监听器
     */
    public CourseReminderAdapter(OnCourseReminderClickListener onCourseReminderClickListener) {
        super(DIFF_CALLBACK);
        this.onCourseReminderClickListener = onCourseReminderClickListener;
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
            return oldItem.getCourseName().equals(newItem.getCourseName()) &&
                    oldItem.getTeacher().equals(newItem.getTeacher()) &&
                    oldItem.getLocation().equals(newItem.getLocation()) &&
                    oldItem.getDayOfWeek() == newItem.getDayOfWeek() &&
                    oldItem.getStartSection() == newItem.getStartSection() &&
                    oldItem.isShouldReminder() == newItem.isShouldReminder();
        }
    };

    @NonNull
    @Override
    public CourseReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_course_reminder, parent, false);
        return new CourseReminderViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseReminderViewHolder holder, int position) {
        Course currentCourse = getItem(position);
        holder.bind(currentCourse);
    }

    /**
     * 课程提醒视图持有者
     */
    class CourseReminderViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewCourseName;
        private TextView textViewTeacher;
        private TextView textViewLocation;
        private TextView textViewTimeInfo;
        private TextView textViewNextClass;

        /**
         * 构造函数
         * @param itemView 视图项
         */
        public CourseReminderViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewCourseName = itemView.findViewById(R.id.text_view_course_name);
            textViewTeacher = itemView.findViewById(R.id.text_view_teacher);
            textViewLocation = itemView.findViewById(R.id.text_view_location);
            textViewTimeInfo = itemView.findViewById(R.id.text_view_time_info);
            textViewNextClass = itemView.findViewById(R.id.text_view_next_class);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (onCourseReminderClickListener != null && position != RecyclerView.NO_POSITION) {
                    onCourseReminderClickListener.onCourseReminderClick(getItem(position));
                }
            });
        }

        /**
         * 绑定数据到视图
         * @param course 课程对象
         */
        public void bind(Course course) {
            textViewCourseName.setText(course.getCourseName());
            textViewTeacher.setText(course.getTeacher());
            textViewLocation.setText(course.getLocation());
            
            // 设置时间信息
            String dayOfWeek = TimeUtils.getDayOfWeekString(course.getDayOfWeek());
            String timeRange = TimeUtils.getTimeRangeString(course.getStartSection(), course.getEndSection());
            textViewTimeInfo.setText(dayOfWeek + " " + timeRange);
            
            // 计算下次上课时间
            String nextClassInfo = calculateNextClassTime(course);
            textViewNextClass.setText(nextClassInfo);
        }
        
        /**
         * 计算下次上课时间
         * @param course 课程对象
         * @return 下次上课时间字符串
         */
        private String calculateNextClassTime(Course course) {
            Calendar now = Calendar.getInstance();
            int currentDayOfWeek = now.get(Calendar.DAY_OF_WEEK);
            // 转换为我们的格式（周日=0，周一=1...周六=6）
            int currentDay = currentDayOfWeek == Calendar.SUNDAY ? 0 : currentDayOfWeek - 1;
            
            int courseDayOfWeek = course.getDayOfWeek();
            
            // 计算距离下次上课还有几天
            int daysUntilNextClass = (courseDayOfWeek - currentDay + 7) % 7;
            
            // 如果是今天，计算小时和分钟
            if (daysUntilNextClass == 0) {
                // 获取课程开始时间
                int startSection = course.getStartSection();
                int startHour = TimeUtils.getStartHour(startSection);
                int startMinute = TimeUtils.getStartMinute(startSection);
                
                Calendar courseTime = Calendar.getInstance();
                courseTime.set(Calendar.HOUR_OF_DAY, startHour);
                courseTime.set(Calendar.MINUTE, startMinute);
                courseTime.set(Calendar.SECOND, 0);
                
                // 如果课程时间已过，则计算下周
                if (courseTime.before(now)) {
                    daysUntilNextClass = 7;
                }
            }
            
            // 计算下次上课的具体日期
            Calendar nextClass = Calendar.getInstance();
            nextClass.add(Calendar.DAY_OF_MONTH, daysUntilNextClass);
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM月dd日 E", Locale.getDefault());
            String dateStr = dateFormat.format(nextClass.getTime());
            
            if (daysUntilNextClass == 0) {
                return "今天 " + dateStr;
            } else if (daysUntilNextClass == 1) {
                return "明天 " + dateStr;
            } else {
                return daysUntilNextClass + "天后 " + dateStr;
            }
        }
    }

    /**
     * 课程点击监听器接口
     */
    public interface OnCourseReminderClickListener {
        /**
         * 课程点击回调方法
         * @param course 被点击的课程
         */
        void onCourseReminderClick(Course course);
    }
}