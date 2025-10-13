package com.example.njupt_coursetable.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.njupt_coursetable.R;
import com.example.njupt_coursetable.ui.activity.MainActivity;

/**
 * 通知工具类
 * 提供创建和显示通知的方法
 */
public class NotificationUtils {

    /**
     * 通知渠道ID
     */
    private static final String CHANNEL_ID = "course_reminder_channel";
    
    /**
     * 通知渠道名称
     */
    private static final String CHANNEL_NAME = "课程提醒";
    
    /**
     * 通知渠道描述
     */
    private static final String CHANNEL_DESCRIPTION = "课程上课提醒通知";

    /**
     * 创建通知渠道
     * @param context 上下文
     */
    public static void createNotificationChannel(Context context) {
        // Android 8.0 (API 26)及以上需要创建通知渠道
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription(CHANNEL_DESCRIPTION);
            channel.enableVibration(true);
            channel.setShowBadge(true);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    /**
     * 显示课程提醒通知
     * @param context 上下文
     * @param courseId 课程ID
     * @param courseName 课程名称
     * @param location 上课地点
     * @param time 上课时间
     */
    public static void showCourseReminderNotification(Context context, long courseId, String courseName, String location, String time) {
        // 创建点击通知后跳转的Intent
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("course_id", courseId);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, (int) courseId, intent, 
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0);

        // 创建通知
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("课程提醒")
                .setContentText(courseName + " 即将开始")
                .setSubText("地点: " + location + " 时间: " + time)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setVibrate(new long[]{0, 300, 200, 300});

        // 显示通知
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify((int) courseId, builder.build());
        }
    }

    /**
     * 取消通知
     * @param context 上下文
     * @param courseId 课程ID
     */
    public static void cancelNotification(Context context, long courseId) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.cancel((int) courseId);
        }
    }
}