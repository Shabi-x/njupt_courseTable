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
    private OnCourseReminderListener onCourseReminderListener;
    
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
        enableReminderSwitch.setChecked(course.isShouldReminder()); // 根据shouldReminder字段设置状态
        enableReminderSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (currentCourse == null) return;
            
            // 更新课程的shouldReminder字段
            currentCourse.setShouldReminder(isChecked);
            
            if (isChecked) {
                Toast.makeText(getContext(), "已开启课程提醒", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "已关闭课程提醒", Toast.LENGTH_SHORT).show();
            }
            
            // 通过回调通知Activity更新数据库
            if (onCourseReminderListener != null) {
                onCourseReminderListener.onCourseReminderChanged(currentCourse, isChecked);
            }
        });
        
        // 显示面板
        setVisibility(VISIBLE);
    }
    
    /**
     * 设置课程提醒监听器
     * @param listener 监听器
     */
    public void setOnCourseReminderListener(OnCourseReminderListener listener) {
        this.onCourseReminderListener = listener;
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