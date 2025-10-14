package com.example.njupt_coursetable.ui.dialog;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.njupt_coursetable.R;
import com.example.njupt_coursetable.data.model.Course;
import com.example.njupt_coursetable.data.model.Reminder;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 提醒编辑对话框
 * 用于添加或编辑提醒信息
 */
public class ReminderDialog extends DialogFragment {

    /**
     * 提醒数据回调接口
     */
    public interface ReminderDialogListener {
        /**
         * 保存提醒回调方法
         * @param reminder 提醒对象
         */
        void onReminderSave(Reminder reminder);
    }

    /**
     * 提醒对象
     */
    private Reminder reminder;
    
    /**
     * 课程列表
     */
    private List<Course> courseList;
    
    /**
     * 回调监听器
     */
    private ReminderDialogListener listener;
    
    /**
     * 时间格式化器
     */
    private final SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm", Locale.getDefault());
    
    /**
     * 选中的提醒时间
     */
    private Calendar selectedTime = Calendar.getInstance();

    /**
     * 构造函数
     * @param reminder 提醒对象，如果为null则表示新建提醒
     * @param courseList 课程列表
     */
    public ReminderDialog(Reminder reminder, List<Course> courseList) {
        this.reminder = reminder;
        this.courseList = courseList;
    }

    /**
     * 设置回调监听器
     * @param listener 回调监听器
     */
    public void setListener(ReminderDialogListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.dialog_reminder, null);

        EditText editTextTitle = view.findViewById(R.id.edit_text_reminder_title);
        EditText editTextDescription = view.findViewById(R.id.edit_text_reminder_description);
        Spinner spinnerCourse = view.findViewById(R.id.spinner_course);
        TextView textViewTime = view.findViewById(R.id.text_view_reminder_time);
        Spinner spinnerAdvance = view.findViewById(R.id.spinner_advance_minutes);
        Button buttonSave = view.findViewById(R.id.button_save);
        Button buttonCancel = view.findViewById(R.id.button_cancel);

        // 设置课程选项
        ArrayAdapter<Course> courseAdapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_spinner_item, courseList);
        courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCourse.setAdapter(courseAdapter);

        // 设置提前分钟数选项
        ArrayAdapter<CharSequence> advanceAdapter = ArrayAdapter.createFromResource(
                requireContext(), R.array.advance_minutes, android.R.layout.simple_spinner_item);
        advanceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAdvance.setAdapter(advanceAdapter);

        // 时间选择点击事件
        textViewTime.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    requireContext(),
                    new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            selectedTime.set(Calendar.MINUTE, minute);
                            textViewTime.setText(timeFormatter.format(selectedTime.getTime()));
                        }
                    },
                    selectedTime.get(Calendar.HOUR_OF_DAY),
                    selectedTime.get(Calendar.MINUTE),
                    true
            );
            timePickerDialog.show();
        });

        // 如果是编辑模式，填充现有数据
        if (reminder != null) {
            editTextTitle.setText(reminder.getTitle());
            editTextDescription.setText(reminder.getDescription());
            
            // 设置课程
            for (int i = 0; i < courseList.size(); i++) {
                if (courseList.get(i).getId() == reminder.getCourseId()) {
                    spinnerCourse.setSelection(i);
                    break;
                }
            }
            
            // 设置提醒时间
            Calendar reminderTime = Calendar.getInstance();
            reminderTime.set(Calendar.HOUR_OF_DAY, (int)(reminder.getRemindTime() / 3600000));
            reminderTime.set(Calendar.MINUTE, (int)((reminder.getRemindTime() % 3600000) / 60000));
            selectedTime = reminderTime;
            textViewTime.setText(timeFormatter.format(selectedTime.getTime()));
            
            // 设置提前分钟数
            String[] advanceMinutes = getResources().getStringArray(R.array.advance_minutes);
            for (int i = 0; i < advanceMinutes.length; i++) {
                if (advanceMinutes[i].equals(String.valueOf(reminder.getAdvanceMinutes()))) {
                    spinnerAdvance.setSelection(i);
                    break;
                }
            }
        } else {
            // 默认设置为当前时间
            textViewTime.setText(timeFormatter.format(selectedTime.getTime()));
        }

        // 保存按钮点击事件
        buttonSave.setOnClickListener(v -> {
            String title = editTextTitle.getText().toString().trim();
            String description = editTextDescription.getText().toString().trim();
            Course selectedCourse = (Course) spinnerCourse.getSelectedItem();
            String advanceMinutesStr = spinnerAdvance.getSelectedItem().toString();

            // 验证输入
            if (title.isEmpty()) {
                editTextTitle.setError("请输入提醒标题");
                return;
            }

            if (selectedCourse == null) {
                Toast.makeText(requireContext(), "请选择课程", Toast.LENGTH_SHORT).show();
                return;
            }

            int advanceMinutes = Integer.parseInt(advanceMinutesStr);

            // 创建或更新提醒对象
            Reminder newReminder;
            if (reminder != null) {
                // 更新现有提醒
                newReminder = reminder;
            } else {
                // 创建新提醒
                newReminder = new Reminder();
                newReminder.setEnabled(true);
            }

            newReminder.setTitle(title);
            newReminder.setDescription(description);
            newReminder.setCourseId(selectedCourse.getId());
            
            // 将Calendar转换为时间戳（毫秒）
            long remindTime = selectedTime.get(Calendar.HOUR_OF_DAY) * 3600000 + 
                             selectedTime.get(Calendar.MINUTE) * 60000;
            newReminder.setRemindTime(remindTime);
            
            newReminder.setAdvanceMinutes(advanceMinutes);

            // 通知监听器
            if (listener != null) {
                listener.onReminderSave(newReminder);
            }

            dismiss();
        });

        // 取消按钮点击事件
        buttonCancel.setOnClickListener(v -> dismiss());

        // 创建对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(view);
        builder.setTitle(reminder == null ? "添加提醒" : "编辑提醒");

        return builder.create();
    }
}