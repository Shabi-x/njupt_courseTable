package com.example.njupt_coursetable.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ForeignKey;
import androidx.room.Index;

import com.google.gson.annotations.SerializedName;

/**
 * 提醒实体类
 * 包含提醒的基本信息：标题、描述、提醒时间、是否启用、提前提醒分钟数等
 */
@Entity(
    tableName = "reminders",
    foreignKeys = @ForeignKey(
        entity = Course.class,
        parentColumns = "id",
        childColumns = "courseId",
        onDelete = ForeignKey.CASCADE
    ),
    indices = @Index("courseId")
)
public class Reminder {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private long courseId;        // 关联的课程ID

    private String title;         // 提醒标题

    private String description;   // 提醒描述

    private long remindTime;      // 提醒时间

    private boolean isEnabled;    // 是否启用

    private int advanceMinutes;   // 提前提醒分钟数

    // 默认构造函数
    public Reminder() {
    }

    // 带参数的构造函数
    public Reminder(long courseId, String title, String description, 
                   long remindTime, boolean isEnabled, int advanceMinutes) {
        this.courseId = courseId;
        this.title = title;
        this.description = description;
        this.remindTime = remindTime;
        this.isEnabled = isEnabled;
        this.advanceMinutes = advanceMinutes;
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCourseId() {
        return courseId;
    }

    public void setCourseId(long courseId) {
        this.courseId = courseId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getRemindTime() {
        return remindTime;
    }

    public void setRemindTime(long remindTime) {
        this.remindTime = remindTime;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public int getAdvanceMinutes() {
        return advanceMinutes;
    }

    public void setAdvanceMinutes(int advanceMinutes) {
        this.advanceMinutes = advanceMinutes;
    }

    @Override
    public String toString() {
        return "Reminder{" +
                "id=" + id +
                ", courseId=" + courseId +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", remindTime=" + remindTime +
                ", isEnabled=" + isEnabled +
                ", advanceMinutes=" + advanceMinutes +
                '}';
    }
}