package com.example.njupt_coursetable.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ForeignKey;
import androidx.room.Index;

import com.google.gson.annotations.SerializedName;

/**
 * 课程实体类
 * 包含课程的基本信息：课程名、上课地点、上课周数、老师名字、联系方式、属性、备注等
 */
@Entity(
    tableName = "courses",
    foreignKeys = @ForeignKey(
        entity = Reminder.class,
        parentColumns = "id",
        childColumns = "reminderId",
        onDelete = ForeignKey.SET_NULL
    ),
    indices = @Index("reminderId")
)
public class Course {

    @PrimaryKey(autoGenerate = true)
    private long id;

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
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    @Override
    public String toString() {
        return "Course{" +
                "id=" + id +
                ", courseName='" + courseName + '\'' +
                ", location='" + location + '\'' +
                ", weekRange='" + weekRange + '\'' +
                ", dayOfWeek='" + dayOfWeek + '\'' +
                ", timeSlot='" + timeSlot + '\'' +
                ", teacherName='" + teacherName + '\'' +
                ", contactInfo='" + contactInfo + '\'' +
                ", property='" + property + '\'' +
                ", remarks='" + remarks + '\'' +
                ", reminderId=" + reminderId +
                '}';
    }
}