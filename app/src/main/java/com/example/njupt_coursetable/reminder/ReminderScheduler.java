package com.example.njupt_coursetable.reminder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.example.njupt_coursetable.data.model.Reminder;

import java.util.Calendar;
import java.util.List;

/**
 * 提醒调度器
 * 负责设置和取消系统闹钟，用于课程提醒
 */
public class ReminderScheduler {

    /**
     * 设置提醒
     * @param context 上下文
     * @param reminder 提醒对象
     */
    public static void setReminder(Context context, Reminder reminder) {
        if (reminder == null || !reminder.isEnabled()) {
            return;
        }

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, ReminderBroadcastReceiver.class);
        
        // 传递提醒信息
        intent.putExtra("reminder_id", String.valueOf(reminder.getId()));
        intent.putExtra("reminder_title", reminder.getTitle());
        intent.putExtra("reminder_description", reminder.getDescription());
        intent.putExtra("course_id", String.valueOf(reminder.getCourseId()));
        
        // 创建PendingIntent
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                (int) reminder.getId(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // 计算提醒时间
        long reminderTime = calculateReminderTime(reminder);
        
        // 设置闹钟
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    reminderTime,
                    pendingIntent
            );
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    reminderTime,
                    pendingIntent
            );
        } else {
            alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    reminderTime,
                    pendingIntent
            );
        }
    }

    /**
     * 取消提醒
     * @param context 上下文
     * @param reminder 提醒对象
     */
    public static void cancelReminder(Context context, Reminder reminder) {
        if (reminder == null) {
            return;
        }

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, ReminderBroadcastReceiver.class);
        
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                (int) reminder.getId(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        alarmManager.cancel(pendingIntent);
    }

    /**
     * 批量设置提醒
     * @param context 上下文
     * @param reminders 提醒列表
     */
    public static void setReminders(Context context, List<Reminder> reminders) {
        for (Reminder reminder : reminders) {
            setReminder(context, reminder);
        }
    }

    /**
     * 批量取消提醒
     * @param context 上下文
     * @param reminders 提醒列表
     */
    public static void cancelReminders(Context context, List<Reminder> reminders) {
        for (Reminder reminder : reminders) {
            cancelReminder(context, reminder);
        }
    }

    /**
     * 计算提醒时间
     * @param reminder 提醒对象
     * @return 提醒时间戳
     */
    private static long calculateReminderTime(Reminder reminder) {
        // 获取提醒时间
        long reminderTime = reminder.getRemindTime();
        
        // 减去提前时间
        int advanceMinutes = reminder.getAdvanceMinutes();
        reminderTime -= advanceMinutes * 60 * 1000; // 转换为毫秒
        
        // 如果时间已经过去，设置为下周同一时间
        if (reminderTime < System.currentTimeMillis()) {
            reminderTime += 7 * 24 * 60 * 60 * 1000; // 加一周的毫秒数
        }
        
        return reminderTime;
    }
}