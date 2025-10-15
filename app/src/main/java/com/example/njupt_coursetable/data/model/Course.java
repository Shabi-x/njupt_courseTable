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
    tableName = "courses"
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
    
    private String weekType;      // 周类型，如"单周"、"双周"、"全周"
    
    @SerializedName("shouldReminder")
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


    
    public String getWeekType() {
        return weekType;
    }
    
    public void setWeekType(String weekType) {
        this.weekType = weekType;
    }
    
    public boolean isShouldReminder() {
        return shouldReminder;
    }
    
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
    
    public String getContact() {
        return contactInfo;
    }
    
    public void setContact(String contact) {
        this.contactInfo = contact;
    }
    
    public String getType() {
        return property;
    }
    
    public void setType(String type) {
        this.property = type;
    }
    
    public String getNote() {
        return remarks;
    }
    
    public void setNote(String note) {
        this.remarks = note;
    }
    
    public String getWeeks() {
        return weekRange;
    }
    
    public void setWeeks(String weeks) {
        this.weekRange = weeks;
    }
    
    // 添加颜色属性，用于课程表显示
    private int color = 0xFF2196F3; // 默认蓝色
    
    public int getColor() {
        return color;
    }
    
    public void setColor(int color) {
        this.color = color;
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
                ", weekType='" + weekType + '\'' +
                ", reminderId=" + reminderId +
                '}';
    }
}