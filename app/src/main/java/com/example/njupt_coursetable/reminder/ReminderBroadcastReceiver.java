package com.example.njupt_coursetable.reminder;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.njupt_coursetable.R;
import com.example.njupt_coursetable.data.model.Reminder;
import com.example.njupt_coursetable.utils.NotificationUtils;

/**
 * 提醒广播接收器
 * 接收系统闹钟广播，显示课程提醒通知
 */
public class ReminderBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // 获取提醒信息
        String reminderId = intent.getStringExtra("reminder_id");
        String reminderTitle = intent.getStringExtra("reminder_title");
        String reminderDescription = intent.getStringExtra("reminder_description");
        String courseId = intent.getStringExtra("course_id");
        
        // 创建通知渠道
        NotificationUtils.createNotificationChannel(context);
        
        // 构建通知内容
        String contentText = "课程提醒";
        if (courseId != null && !courseId.isEmpty()) {
            contentText = "课程ID: " + courseId;
        }
        if (reminderDescription != null && !reminderDescription.isEmpty()) {
            contentText += "\n" + reminderDescription;
        }
        
        // 显示通知
        NotificationUtils.showCourseReminderNotification(
                context,
                courseId != null ? Long.parseLong(courseId) : 0,
                reminderTitle != null ? reminderTitle : "课程提醒",
                reminderDescription != null ? reminderDescription : "",
                ""
        );
    }
}