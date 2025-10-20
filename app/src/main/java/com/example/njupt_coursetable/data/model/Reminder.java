package com.example.njupt_coursetable.data.model;

/**
 * 提醒实体（与后端 ReminderDTO 对应的扁平结构）
 */
public class Reminder {
    private long id;
    private long courseId;
    private String courseName;
    private String location;
    private String dayOfWeek;   // 如 "周一"
    private String timeSlot;    // 如 "1-2节"
    private String courseDate;  // yyyy-MM-dd
    private String startTime;   // HH:mm:ss

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getCourseId() { return courseId; }
    public void setCourseId(long courseId) { this.courseId = courseId; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(String dayOfWeek) { this.dayOfWeek = dayOfWeek; }

    public String getTimeSlot() { return timeSlot; }
    public void setTimeSlot(String timeSlot) { this.timeSlot = timeSlot; }

    public String getCourseDate() { return courseDate; }
    public void setCourseDate(String courseDate) { this.courseDate = courseDate; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }
}


