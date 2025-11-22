package com.example.njupt_coursetable.model;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Entity
@Table(name = "reminders")
public class Reminder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "course_date", nullable = false)
    private LocalDate courseDate;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Reminder() {}

    public Reminder(Course course, LocalDate courseDate, LocalTime startTime) {
        this.course = course;
        this.courseDate = courseDate;
        this.startTime = startTime;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }
    public LocalDate getCourseDate() { return courseDate; }
    public void setCourseDate(LocalDate d) { this.courseDate = d; }
    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime t) { this.startTime = t; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}







