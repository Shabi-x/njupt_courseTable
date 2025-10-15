package com.example.njupt_coursetable.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Switch;
import android.widget.CompoundButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.njupt_coursetable.R;
import com.example.njupt_coursetable.data.model.Course;
import com.example.njupt_coursetable.data.model.Reminder;
import com.example.njupt_coursetable.reminder.ReminderScheduler;

/**
 * 半屏面板组件，用于展示课程详细信息
 */
public class HalfPanel extends FrameLayout {
    
    private TextView courseNameText;
    private TextView courseLocationText;
    private TextView courseWeeksText;
    private TextView courseTeacherText;
    private TextView courseContactText;
    private TextView courseTypeText;
    private TextView courseNoteText;
    private View backgroundOverlay;
    private Switch enableReminderSwitch;
    
    private OnCloseListener onCloseListener;
    private Course currentCourse;
    private Reminder currentReminder;
    
    public HalfPanel(@NonNull Context context) {
        super(context);
        init();
    }
    
    public HalfPanel(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    
    public HalfPanel(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    
    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.half_panel_layout, this, true);
        
        courseNameText = findViewById(R.id.text_course_name);
        courseLocationText = findViewById(R.id.text_course_location);
        courseWeeksText = findViewById(R.id.text_course_weeks);
        courseTeacherText = findViewById(R.id.text_course_teacher);
        courseContactText = findViewById(R.id.text_course_contact);
        courseTypeText = findViewById(R.id.text_course_type);
        courseNoteText = findViewById(R.id.text_course_note);
        backgroundOverlay = findViewById(R.id.background_overlay);
        enableReminderSwitch = findViewById(R.id.switch_enable_reminder);
        
        // 设置背景遮罩点击事件
        backgroundOverlay.setOnClickListener(v -> {
            if (onCloseListener != null) {
                onCloseListener.onClose();
            }
        });
        
        // 设置提醒选择监听器
        enableReminderSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (currentCourse == null) return;
            
            if (isChecked) {
                // 创建提醒
                createReminderForCourse(currentCourse);
                Toast.makeText(getContext(), "设置提醒成功！会在课程前15分钟发送提醒", Toast.LENGTH_SHORT).show();
            } else {
                // 取消提醒
                cancelReminderForCourse(currentCourse);
            }
        });
        
        // 初始状态为隐藏
        setVisibility(GONE);
    }
    
    /**
     * 设置课程信息并显示面板
     * @param course 课程对象
     */
    public void showCourseInfo(Course course) {
        if (course == null) return;
        
        currentCourse = course;
        
        courseNameText.setText(course.getCourseName());
        courseLocationText.setText(course.getLocation());
        courseWeeksText.setText(course.getWeekRange() + " (" + course.getWeekType() + ")");
        courseTeacherText.setText(course.getTeacherName());
        courseContactText.setText(course.getContactInfo());
        courseTypeText.setText(course.getProperty());
        
        // 显示备注信息，如果没有备注则显示"无"
        String remarks = course.getRemarks();
        if (remarks == null || remarks.isEmpty()) {
            remarks = "无";
        }
        courseNoteText.setText(remarks);
        
        // 重置提醒选择状态
        enableReminderSwitch.setOnCheckedChangeListener(null); // 临时移除监听器
        enableReminderSwitch.setChecked(false); // 默认不开启提醒
        enableReminderSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (currentCourse == null) return;
            
            if (isChecked) {
                // 创建提醒
                createReminderForCourse(currentCourse);
                Toast.makeText(getContext(), "设置提醒成功！会在课程前15分钟发送提醒", Toast.LENGTH_SHORT).show();
            } else {
                // 取消提醒
                cancelReminderForCourse(currentCourse);
            }
        });
        
        // 显示面板
        setVisibility(VISIBLE);
    }
    
    /**
     * 为课程创建提醒
     * @param course 课程对象
     */
    private void createReminderForCourse(Course course) {
        // 创建新的提醒对象
        Reminder reminder = new Reminder();
        reminder.setCourseId(course.getId());
        reminder.setTitle(course.getName() + " 课程提醒");
        reminder.setDescription("地点：" + course.getLocation());
        reminder.setEnabled(true);
        reminder.setAdvanceMinutes(15); // 提前15分钟提醒
        
        // 计算课程时间
        long courseTime = calculateCourseTime(course);
        reminder.setRemindTime(courseTime);
        
        // 设置提醒
        ReminderScheduler.setReminder(getContext(), reminder);
        currentReminder = reminder;
    }
    
    /**
     * 取消课程提醒
     * @param course 课程对象
     */
    private void cancelReminderForCourse(Course course) {
        if (currentReminder != null) {
            ReminderScheduler.cancelReminder(getContext(), currentReminder);
            currentReminder = null;
        }
    }
    
    /**
     * 计算课程时间
     * @param course 课程对象
     * @return 课程时间戳
     */
    private long calculateCourseTime(Course course) {
        // 获取当前时间
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        
        // 设置为本周的对应星期几
        int dayOfWeek = getDayOfWeekValue(course.getDayOfWeek());
        int currentDayOfWeek = calendar.get(java.util.Calendar.DAY_OF_WEEK);
        
        // 计算距离目标星期几的天数差
        int dayDiff = dayOfWeek - currentDayOfWeek;
        if (dayDiff <= 0) {
            dayDiff += 7; // 如果目标日期已过或就是今天，则设置为下周
        }
        
        // 添加天数差
        calendar.add(java.util.Calendar.DAY_OF_MONTH, dayDiff);
        
        // 设置课程开始时间
        int[] startTime = getTimeSlotStart(course.getTimeSlot());
        calendar.set(java.util.Calendar.HOUR_OF_DAY, startTime[0]);
        calendar.set(java.util.Calendar.MINUTE, startTime[1]);
        calendar.set(java.util.Calendar.SECOND, 0);
        calendar.set(java.util.Calendar.MILLISECOND, 0);
        
        return calendar.getTimeInMillis();
    }
    
    /**
     * 将星期几转换为Calendar的常量值
     * @param dayOfWeek 星期几字符串，如"周一"
     * @return Calendar的DAY_OF_WEEK常量值
     */
    private int getDayOfWeekValue(String dayOfWeek) {
        if (dayOfWeek == null) return java.util.Calendar.MONDAY;
        
        switch (dayOfWeek) {
            case "周日":
                return java.util.Calendar.SUNDAY;
            case "周一":
                return java.util.Calendar.MONDAY;
            case "周二":
                return java.util.Calendar.TUESDAY;
            case "周三":
                return java.util.Calendar.WEDNESDAY;
            case "周四":
                return java.util.Calendar.THURSDAY;
            case "周五":
                return java.util.Calendar.FRIDAY;
            case "周六":
                return java.util.Calendar.SATURDAY;
            default:
                return java.util.Calendar.MONDAY;
        }
    }
    
    /**
     * 获取时间段的开始时间
     * @param timeSlot 时间段字符串，如"1-2节"
     * @return 包含小时和分钟的数组
     */
    private int[] getTimeSlotStart(String timeSlot) {
        if (timeSlot == null) return new int[]{8, 0}; // 默认8:00
        
        // 根据时间段返回对应的开始时间
        if (timeSlot.contains("1-2")) {
            return new int[]{8, 0}; // 8:00-9:40
        } else if (timeSlot.contains("3-4")) {
            return new int[]{10, 0}; // 10:00-11:40
        } else if (timeSlot.contains("5-6")) {
            return new int[]{14, 0}; // 14:00-15:40
        } else if (timeSlot.contains("7-8")) {
            return new int[]{16, 0}; // 16:00-17:40
        } else if (timeSlot.contains("9-10")) {
            return new int[]{19, 0}; // 19:00-20:40
        }
        
        // 默认返回8:00
        return new int[]{8, 0};
    }
    
    /**
     * 隐藏面板
     */
    public void hidePanel() {
        setVisibility(GONE);
    }
    
    /**
     * 设置关闭监听器
     * @param listener 监听器
     */
    public void setOnCloseListener(OnCloseListener listener) {
        this.onCloseListener = listener;
    }
    
    /**
     * 关闭监听器接口
     */
    public interface OnCloseListener {
        void onClose();
    }
}