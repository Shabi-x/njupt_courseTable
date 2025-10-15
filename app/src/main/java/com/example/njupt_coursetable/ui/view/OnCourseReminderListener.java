package com.example.njupt_coursetable.ui.view;

import com.example.njupt_coursetable.data.model.Course;

/**
 * 课程提醒监听器接口
 */
public interface OnCourseReminderListener {
    /**
     * 当课程提醒状态改变时调用
     * @param course 课程对象
     * @param shouldReminder 是否需要提醒
     */
    void onCourseReminderChanged(Course course, boolean shouldReminder);
}