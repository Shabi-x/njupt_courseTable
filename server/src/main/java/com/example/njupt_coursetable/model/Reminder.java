package com.example.njupt_coursetable.model;

import javax.persistence.*;

@Entity
@Table(name = "reminders")
public class Reminder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long courseId;        // 关联的课程ID

    @Column(nullable = false)
    private String title;         // 提醒标题

    private String description;   // 提醒描述

    @Column(nullable = false)
    private Long remindTime;      // 提醒时间

    @Column(nullable = false)
    private Boolean isEnabled;    // 是否启用

    @Column(nullable = false)
    private Integer advanceMinutes;   // 提前提醒分钟数

    // 默认构造函数
    public Reminder() {
    }

    // 带参数的构造函数
    public Reminder(Long courseId, String title, String description, 
                   Long remindTime, Boolean isEnabled, Integer advanceMinutes) {
        this.courseId = courseId;
        this.title = title;
        this.description = description;
        this.remindTime = remindTime;
        this.isEnabled = isEnabled;
        this.advanceMinutes = advanceMinutes;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
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

    public Long getRemindTime() {
        return remindTime;
    }

    public void setRemindTime(Long remindTime) {
        this.remindTime = remindTime;
    }

    public Boolean getIsEnabled() {
        return isEnabled;
    }

    public void setIsEnabled(Boolean enabled) {
        isEnabled = enabled;
    }

    public Integer getAdvanceMinutes() {
        return advanceMinutes;
    }

    public void setAdvanceMinutes(Integer advanceMinutes) {
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