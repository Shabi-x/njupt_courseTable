package com.example.njupt_coursetable.model;

import javax.persistence.*;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "courses")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String courseName;    // 课程名

    private String location;      // 上课地点

    private String weekRange;    // 上课周数，如"1-16周"

    private String dayOfWeek;     // 星期几，如"周一"

    private String timeSlot;      // 时间段，如"1-2节"

    private String teacherName;  // 老师名字

    private String contactInfo;   // 老师联系方式

    private String property;      // 属性，如"必修"、"选修"

    private String remarks;       // 备注

    private Long reminderId;      // 关联的提醒ID
    
    private String weekType;      // 周类型，如"单周"、"双周"、"全周"
    
    private boolean shouldReminder; // 是否需要提醒

    // 默认构造函数
    public Course() {
    }

    // 带参数的构造函数
    public Course(String courseName, String location, String weekRange, 
                 String dayOfWeek, String timeSlot, String teacherName, 
                 String contactInfo, String property, String remarks) {
        this.courseName = courseName;
        this.location = location;
        this.weekRange = weekRange;
        this.dayOfWeek = dayOfWeek;
        this.timeSlot = timeSlot;
        this.teacherName = teacherName;
        this.contactInfo = contactInfo;
        this.property = property;
        this.remarks = remarks;
        this.weekType = "全周"; // 默认为全周
    }
    
    // 带weekType参数的构造函数
    public Course(String courseName, String location, String weekRange, 
                 String dayOfWeek, String timeSlot, String teacherName, 
                 String contactInfo, String property, String remarks, String weekType) {
        this.courseName = courseName;
        this.location = location;
        this.weekRange = weekRange;
        this.dayOfWeek = dayOfWeek;
        this.timeSlot = timeSlot;
        this.teacherName = teacherName;
        this.contactInfo = contactInfo;
        this.property = property;
        this.remarks = remarks;
        this.weekType = weekType;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getWeekRange() {
        return weekRange;
    }

    public void setWeekRange(String weekRange) {
        this.weekRange = weekRange;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Long getReminderId() {
        return reminderId;
    }

    public void setReminderId(Long reminderId) {
        this.reminderId = reminderId;
    }
    
    public String getWeekType() {
        return weekType;
    }
    
    public void setWeekType(String weekType) {
        this.weekType = weekType;
    }
    
    @JsonProperty("shouldReminder")
    public boolean isShouldReminder() {
        return shouldReminder;
    }
    
    @JsonProperty("shouldReminder")
    public void setShouldReminder(boolean shouldReminder) {
        this.shouldReminder = shouldReminder;
    }

    // 添加一些便捷方法
    public String getName() {
        return courseName;
    }
    
    public void setName(String name) {
        this.courseName = name;
    }
    
    public String getTeacher() {
        return teacherName;
    }
    
    public void setTeacher(String teacher) {
        this.teacherName = teacher;
    }
}