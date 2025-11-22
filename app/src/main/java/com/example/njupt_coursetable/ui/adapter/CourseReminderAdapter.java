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
import com.example.njupt_coursetable.data.model.Reminder;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 课程提醒列表适配器
 * 用于在RecyclerView中显示需要提醒的课程列表
 */
public class CourseReminderAdapter extends ListAdapter<Reminder, CourseReminderAdapter.CourseReminderViewHolder> {

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
    private static final DiffUtil.ItemCallback<Reminder> DIFF_CALLBACK = new DiffUtil.ItemCallback<Reminder>() {
        @Override
        public boolean areItemsTheSame(@NonNull Reminder oldItem, @NonNull Reminder newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Reminder oldItem, @NonNull Reminder newItem) {
            return oldItem.getCourseDate().equals(newItem.getCourseDate()) &&
                    oldItem.getStartTime().equals(newItem.getStartTime()) &&
                    oldItem.getCourseId() == newItem.getCourseId() &&
                    safeEquals(oldItem.getCourseName(), newItem.getCourseName()) &&
                    safeEquals(oldItem.getLocation(), newItem.getLocation()) &&
                    safeEquals(oldItem.getDayOfWeek(), newItem.getDayOfWeek()) &&
                    safeEquals(oldItem.getTimeSlot(), newItem.getTimeSlot());
        }
    };

    private static boolean safeEquals(String a, String b) {
        if (a == null) return b == null;
        return a.equals(b);
    }

    @NonNull
    @Override
    public CourseReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_course_reminder, parent, false);
        return new CourseReminderViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseReminderViewHolder holder, int position) {
        Reminder reminder = getItem(position);
        holder.bind(reminder);
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
                    Reminder r = getItem(position);
                    if (r != null) {
                        Course c = new Course();
                        c.setId(r.getCourseId());
                        c.setCourseName(r.getCourseName());
                        c.setLocation(r.getLocation());
                        c.setDayOfWeek(r.getDayOfWeek());
                        c.setTimeSlot(r.getTimeSlot());
                        c.setShouldReminder(true); // 从提醒列表点击的课程，肯定已开启提醒
                        
                        // 设置默认值，避免显示null
                        c.setTeacherName("未知");
                        c.setWeekType("全周");
                        c.setContactInfo("暂无");
                        c.setProperty("必修");
                        
                        // ⭐ 关键：将Reminder的courseDate保存到remarks字段，用于删除提醒
                        // 格式：REMINDER_DATE:yyyy-MM-dd
                        c.setRemarks("REMINDER_DATE:" + r.getCourseDate());
                        
                        // 同时保存weekRange（从courseDate推算）
                        c.setWeekRange(extractWeekFromDate(r.getCourseDate()));
                        
                        onCourseReminderClickListener.onCourseReminderClick(c);
                    }
                }
            });
        }

        /**
         * 绑定数据到视图
         * @param course 课程对象
         */
        public void bind(Reminder reminder) {
            textViewCourseName.setText(reminder.getCourseName());
            textViewTeacher.setText("");
            textViewLocation.setText(reminder.getLocation());
            textViewTimeInfo.setText(reminder.getDayOfWeek() + " " + reminder.getTimeSlot());
            textViewNextClass.setText(formatCountdown(reminder.getCourseDate(), reminder.getStartTime()));
        }
        
        /**
         * 计算下次上课时间
         * @param course 课程对象
         * @return 下次上课时间字符串
         */
        private String formatCountdown(String date, String startTime) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                Date target = sdf.parse(date + " " + startTime);
                long diff = target.getTime() - System.currentTimeMillis();
                if (diff <= 0) return "即将开始";
                long minutes = diff / 60000;
                long hours = minutes / 60; minutes %= 60;
                long days = hours / 24; hours %= 24;
                if (days > 0) return days + "天 " + hours + "小时";
                if (hours > 0) return hours + "小时 " + minutes + "分钟";
                return minutes + "分钟";
            } catch (Exception e) {
                return "--";
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
        
        /**
         * 从日期推算周数（简单返回"未知"）
         * @param dateStr 日期字符串 yyyy-MM-dd
         * @return 周数字符串
         */
        private String extractWeekFromDate(String dateStr) {
            // 简单返回日期，供显示
            return dateStr != null ? dateStr : "未知";
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