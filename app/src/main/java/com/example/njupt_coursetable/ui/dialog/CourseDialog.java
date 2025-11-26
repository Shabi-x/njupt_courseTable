package com.example.njupt_coursetable.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.njupt_coursetable.R;
import com.example.njupt_coursetable.data.model.Course;

import java.util.List;

/**
 * 课程编辑对话框
 * 用于添加或编辑课程信息
 */
public class CourseDialog extends DialogFragment {

    /**
     * 课程数据回调接口
     */
    public interface CourseDialogListener {
        /**
         * 保存课程回调方法
         * @param course 课程对象
         */
        void onCourseSave(Course course);
    }

    /**
     * 课程对象
     */
    private Course course;
    
    /**
     * 当前周的课程列表（用于冲突检测）
     */
    private List<Course> existingCourses;
    
    /**
     * 当前周数
     */
    private int currentWeek = 6;
    
    /**
     * 回调监听器
     */
    private CourseDialogListener listener;

    /**
     * 构造函数
     * @param course 课程对象，如果为null则表示新建课程
     * @param existingCourses 当前周的课程列表，用于检测时间冲突
     */
    public CourseDialog(Course course, List<Course> existingCourses) {
        this.course = course;
        this.existingCourses = existingCourses;
    }

    /**
     * 设置当前周数
     * @param week 当前周数
     */
    public void setCurrentWeek(int week) {
        this.currentWeek = week;
    }

    /**
     * 设置回调监听器
     * @param listener 回调监听器
     */
    public void setListener(CourseDialogListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.dialog_course, null);

        EditText editTextCourseName = view.findViewById(R.id.edit_text_course_name);
        EditText editTextLocation = view.findViewById(R.id.edit_text_location);
        EditText editTextTeacher = view.findViewById(R.id.edit_text_teacher);
        EditText editTextWeeks = view.findViewById(R.id.edit_text_weeks);
        Spinner spinnerDayOfWeek = view.findViewById(R.id.spinner_day_of_week);
        Spinner spinnerStartTime = view.findViewById(R.id.spinner_start_time);
        Spinner spinnerEndTime = view.findViewById(R.id.spinner_end_time);
        Button buttonSave = view.findViewById(R.id.button_save);
        Button buttonCancel = view.findViewById(R.id.button_cancel);

        // 设置星期几选项
        ArrayAdapter<CharSequence> dayOfWeekAdapter = ArrayAdapter.createFromResource(
                requireContext(), R.array.days_of_week, android.R.layout.simple_spinner_item);
        dayOfWeekAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDayOfWeek.setAdapter(dayOfWeekAdapter);

        // 设置时间段选项
        ArrayAdapter<CharSequence> timeSlotAdapter = ArrayAdapter.createFromResource(
                requireContext(), R.array.time_slots, android.R.layout.simple_spinner_item);
        timeSlotAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStartTime.setAdapter(timeSlotAdapter);
        spinnerEndTime.setAdapter(timeSlotAdapter);

        // 如果是编辑模式，填充现有数据
        if (course != null) {
            editTextCourseName.setText(course.getCourseName());
            editTextLocation.setText(course.getLocation());
            editTextTeacher.setText(course.getTeacherName());
            editTextWeeks.setText(course.getWeekRange());
            
            // 设置星期几
            String[] daysOfWeek = getResources().getStringArray(R.array.days_of_week);
            for (int i = 0; i < daysOfWeek.length; i++) {
                if (daysOfWeek[i].equals(course.getDayOfWeek())) {
                    spinnerDayOfWeek.setSelection(i);
                    break;
                }
            }
            
            // 设置时间段
            String timeSlot = course.getTimeSlot();
            if (timeSlot != null && !timeSlot.isEmpty()) {
                // 如果timeSlot是"1-2节"这样的格式，转换为对应的时间段
                String[] parts = timeSlot.replace("节", "").split("-");
                if (parts.length > 0) {
                    try {
                        int startSlot = Integer.parseInt(parts[0].trim());
                        int endSlot = parts.length > 1 ? Integer.parseInt(parts[1].trim()) : startSlot;
                        
                        // Spinner索引比节次少1（第1节对应索引0）
                        if (startSlot >= 1 && startSlot <= 9) {
                            spinnerStartTime.setSelection(startSlot - 1);
                        }
                        if (endSlot >= 1 && endSlot <= 9) {
                            spinnerEndTime.setSelection(endSlot - 1);
                        }
                    } catch (NumberFormatException e) {
                        // 解析失败，使用默认值
                        spinnerStartTime.setSelection(0);
                        spinnerEndTime.setSelection(1);
                    }
                }
            }
        } else {
            // 新建模式，设置默认周数为当前周
            editTextWeeks.setText(String.valueOf(currentWeek));
        }

        // 保存按钮点击事件
        buttonSave.setOnClickListener(v -> {
            String courseName = editTextCourseName.getText().toString().trim();
            String location = editTextLocation.getText().toString().trim();
            String teacher = editTextTeacher.getText().toString().trim();
            String weeks = editTextWeeks.getText().toString().trim();
            String dayOfWeek = spinnerDayOfWeek.getSelectedItem().toString();
            String startTime = spinnerStartTime.getSelectedItem().toString();
            String endTime = spinnerEndTime.getSelectedItem().toString();

            // 验证输入
            if (courseName.isEmpty()) {
                editTextCourseName.setError("请输入课程名称");
                return;
            }

            if (location.isEmpty()) {
                editTextLocation.setError("请输入上课地点");
                return;
            }

            if (teacher.isEmpty()) {
                editTextTeacher.setError("请输入教师姓名");
                return;
            }

            if (weeks.isEmpty()) {
                editTextWeeks.setError("请输入上课周数");
                return;
            }

            // 验证开始时间早于结束时间
            String[] timeSlots = getResources().getStringArray(R.array.time_slots);
            int startIndex = -1, endIndex = -1;
            for (int i = 0; i < timeSlots.length; i++) {
                if (timeSlots[i].equals(startTime)) {
                    startIndex = i;
                }
                if (timeSlots[i].equals(endTime)) {
                    endIndex = i;
                }
            }

            if (startIndex >= endIndex) {
                Toast.makeText(requireContext(), "开始时间必须早于结束时间", Toast.LENGTH_SHORT).show();
                return;
            }

            // 如果是新建课程，检查时间冲突
            if (course == null && existingCourses != null) {
                boolean hasConflict = checkTimeConflict(dayOfWeek, startIndex, endIndex);
                if (hasConflict) {
                    Toast.makeText(requireContext(), "该时间段已有课程，请选择其他时间", Toast.LENGTH_LONG).show();
                    return;
                }
            }

            // 创建或更新课程对象
            Course newCourse;
            if (course != null) {
                // 更新现有课程
                newCourse = course;
            } else {
                // 创建新课程
                newCourse = new Course();
                // 设置默认值
                newCourse.setProperty("必修");
                newCourse.setWeekType("全周");
                newCourse.setShouldReminder(false);
                newCourse.setRemarks("");
            }

            newCourse.setCourseName(courseName);
            newCourse.setLocation(location);
            newCourse.setTeacherName(teacher);
            newCourse.setWeekRange(weeks);
            newCourse.setDayOfWeek(dayOfWeek);
            
            // 设置联系方式（简单生成邮箱）
            if (course == null) {
                String email = teacher.toLowerCase().replace(" ", "") + "@njupt.edu.cn";
                newCourse.setContactInfo(email);
            }
            
            // 将选择的时间段转换为"1-2节"格式
            String startSlot = "";
            String endSlot = "";
            
            // 获取开始时间对应的节次
            for (int i = 0; i < timeSlots.length; i++) {
                if (timeSlots[i].equals(startTime)) {
                    startSlot = String.valueOf(i + 1);
                    break;
                }
            }
            
            // 获取结束时间对应的节次
            for (int i = 0; i < timeSlots.length; i++) {
                if (timeSlots[i].equals(endTime)) {
                    endSlot = String.valueOf(i + 1);
                    break;
                }
            }
            
            // 设置时间段，格式为"1-2节"
            if (!startSlot.isEmpty() && !endSlot.isEmpty()) {
                newCourse.setTimeSlot(startSlot + "-" + endSlot + "节");
            } else if (!startSlot.isEmpty()) {
                newCourse.setTimeSlot(startSlot + "节");
            }

            // 通知监听器
            if (listener != null) {
                listener.onCourseSave(newCourse);
            }

            dismiss();
        });

        // 取消按钮点击事件
        buttonCancel.setOnClickListener(v -> dismiss());

        // 创建对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(view);
        builder.setTitle(course == null ? "添加课程" : "编辑课程");

        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                // 获取屏幕尺寸
                DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
                int screenHeight = displayMetrics.heightPixels;
                
                // 设置对话框宽度为屏幕宽度的90%，高度最大为屏幕高度的80%
                WindowManager.LayoutParams params = window.getAttributes();
                params.width = (int) (displayMetrics.widthPixels * 0.9);
                params.height = Math.min((int) (screenHeight * 0.8), dpToPx(600));
                window.setAttributes(params);
            }
        }
    }

    /**
     * 将dp转换为px
     */
    private int dpToPx(int dp) {
        float density = Resources.getSystem().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    /**
     * 检查时间冲突
     * @param dayOfWeek 星期几
     * @param startIndex 开始时间索引（0-based）
     * @param endIndex 结束时间索引（0-based）
     * @return 是否有冲突
     */
    private boolean checkTimeConflict(String dayOfWeek, int startIndex, int endIndex) {
        if (existingCourses == null || existingCourses.isEmpty()) {
            return false;
        }

        for (Course existingCourse : existingCourses) {
            // 检查星期是否相同
            if (!dayOfWeek.equals(existingCourse.getDayOfWeek())) {
                continue;
            }

            // 解析现有课程的时间段
            String timeSlot = existingCourse.getTimeSlot();
            if (timeSlot == null || timeSlot.isEmpty()) {
                continue;
            }

            // 解析时间段格式："1-2节" 或 "1节"
            String[] parts = timeSlot.replace("节", "").split("-");
            if (parts.length == 0) {
                continue;
            }

            try {
                int existingStart = Integer.parseInt(parts[0].trim()) - 1; // 转换为0-based索引
                int existingEnd = parts.length > 1 ? Integer.parseInt(parts[1].trim()) - 1 : existingStart;

                // 检查时间段是否重叠
                // 两个时间段重叠的条件: startA <= endB && endA >= startB
                if (startIndex <= existingEnd && endIndex >= existingStart) {
                    return true; // 有冲突
                }
            } catch (NumberFormatException e) {
                // 解析失败，跳过这个课程
                continue;
            }
        }

        return false; // 没有冲突
    }
}