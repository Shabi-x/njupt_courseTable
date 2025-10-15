package com.example.njupt_coursetable.controller;

import com.example.njupt_coursetable.model.Course;
import com.example.njupt_coursetable.repository.CourseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/courses")
@CrossOrigin(origins = "*")
public class CourseController {

    private static final Logger logger = LoggerFactory.getLogger(CourseController.class);

    @Autowired
    private CourseRepository courseRepository;

    /**
     * 获取所有课程
     * @return 所有课程列表
     */
    @GetMapping
    public List<Course> getAllCourses() {
        logger.info("Getting all courses");
        return courseRepository.findAll();
    }

    /**
     * 根据周数查询课程
     * @param weekNumber 周数，如"1"
     * @return 该周的全部课程列表
     */
    @GetMapping("/week/{weekNumber}")
    public List<Course> getCoursesByWeek(@PathVariable String weekNumber) {
        logger.info("Getting courses for week: {}", weekNumber);
        return courseRepository.findByWeekNumber(weekNumber);
    }
    
    /**
     * 获取所有需要提醒的课程
     * @return 需要提醒的课程列表
     */
    @GetMapping("/reminders")
    public List<Course> getCoursesWithReminders() {
        logger.info("Getting all courses with reminders");
        return courseRepository.findByShouldReminderTrue();
    }
    
    /**
     * 更新课程的提醒状态
     * @param id 课程ID
     * @param shouldReminder 是否需要提醒
     * @return 更新后的课程对象
     */
    @PutMapping("/{id}/reminder")
    public ResponseEntity<Course> updateCourseReminderStatus(@PathVariable Long id, @RequestParam boolean shouldReminder) {
        logger.info("Updating course reminder status with id: {} to: {}", id, shouldReminder);
        
        return courseRepository.findById(id)
                .map(course -> {
                    course.setShouldReminder(shouldReminder);
                    return ResponseEntity.ok(courseRepository.save(course));
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 创建新课程
     * @param course 课程对象
     * @return 创建的课程对象
     */
    @PostMapping
    public Course createCourse(@RequestBody Course course) {
        logger.info("Creating new course: {}", course.getCourseName());
        return courseRepository.save(course);
    }
    
    /**
     * 更新课程
     * @param id 课程ID
     * @param courseDetails 课程详情
     * @return 更新后的课程对象
     */
    @PutMapping("/{id}")
    public ResponseEntity<Course> updateCourse(@PathVariable Long id, @RequestBody Course courseDetails) {
        logger.info("Updating course with id: {}", id);
        
        return courseRepository.findById(id)
                .map(course -> {
                    course.setCourseName(courseDetails.getCourseName());
                    course.setLocation(courseDetails.getLocation());
                    course.setWeekRange(courseDetails.getWeekRange());
                    course.setDayOfWeek(courseDetails.getDayOfWeek());
                    course.setTimeSlot(courseDetails.getTimeSlot());
                    course.setTeacherName(courseDetails.getTeacherName());
                    course.setContactInfo(courseDetails.getContactInfo());
                    course.setProperty(courseDetails.getProperty());
                    course.setRemarks(courseDetails.getRemarks());
                    course.setWeekType(courseDetails.getWeekType());
                    course.setShouldReminder(courseDetails.isShouldReminder());
                    return ResponseEntity.ok(courseRepository.save(course));
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 删除课程
     * @param id 课程ID
     * @return 响应结果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        logger.info("Deleting course with id: {}", id);
        
        return courseRepository.findById(id)
                .map(course -> {
                    courseRepository.delete(course);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}