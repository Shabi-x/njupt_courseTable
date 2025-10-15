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
                    oldItem.getTeacherName().equals(newItem.getTeacherName()) &&
                    oldItem.getLocation().equals(newItem.getLocation()) &&
                    oldItem.getDayOfWeek().equals(newItem.getDayOfWeek()) &&
                    oldItem.getTimeSlot().equals(newItem.getTimeSlot()) &&
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
            textViewTeacher.setText(course.getTeacherName());
            textViewLocation.setText(course.getLocation());
            
            // 设置时间信息
            String dayOfWeek = course.getDayOfWeek(); // 直接获取字符串形式的星期几
            String timeRange = course.getTimeSlot(); // 直接获取时间段字符串
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
            // 由于Course类中的dayOfWeek是字符串类型，我们需要将其转换为数字
            String dayOfWeekStr = course.getDayOfWeek();
            int courseDayOfWeek = convertDayOfWeekStringToInt(dayOfWeekStr);
            
            Calendar now = Calendar.getInstance();
            int currentDayOfWeek = now.get(Calendar.DAY_OF_WEEK);
            // 转换为我们的格式（周日=0，周一=1...周六=6）
            int currentDay = currentDayOfWeek == Calendar.SUNDAY ? 0 : currentDayOfWeek - 1;
            
            // 计算距离下次上课还有几天
            int daysUntilNextClass = (courseDayOfWeek - currentDay + 7) % 7;
            
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
        
        /**
         * 将星期几的字符串转换为数字
         * @param dayOfWeekStr 星期几的字符串
         * @return 星期几的数字 (0-6, 0=周日, 1=周一, ..., 6=周六)
         */
        private int convertDayOfWeekStringToInt(String dayOfWeekStr) {
            if (dayOfWeekStr == null) return 1; // 默认周一
            
            switch (dayOfWeekStr) {
                case "周日":
                    return 0;
                case "周一":
                    return 1;
                case "周二":
                    return 2;
                case "周三":
                    return 3;
                case "周四":
                    return 4;
                case "周五":
                    return 5;
                case "周六":
                    return 6;
                default:
                    return 1; // 默认周一
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