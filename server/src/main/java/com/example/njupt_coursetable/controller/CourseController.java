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
     */
    @GetMapping
    public List<Course> getAllCourses() {
        logger.info("Getting all courses");
        return courseRepository.findAll();
    }

    /**
     * 根据ID获取课程
     */
    @GetMapping("/{id}")
    public ResponseEntity<Course> getCourseById(@PathVariable Long id) {
        logger.info("Getting course with id: {}", id);
        Optional<Course> course = courseRepository.findById(id);
        return course.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 创建新课程
     */
    @PostMapping
    public Course createCourse(@RequestBody Course course) {
        logger.info("Creating new course: {}", course.getCourseName());
        return courseRepository.save(course);
    }

    /**
     * 更新课程
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
                    course.setReminderId(courseDetails.getReminderId());
                    course.setWeekType(courseDetails.getWeekType());
                    course.setShouldReminder(courseDetails.isShouldReminder());
                    return ResponseEntity.ok(courseRepository.save(course));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 删除课程
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

    /**
     * 根据星期几获取课程
     */
    @GetMapping("/day/{dayOfWeek}")
    public List<Course> getCoursesByDayOfWeek(@PathVariable String dayOfWeek) {
        logger.info("Getting courses for day: {}", dayOfWeek);
        return courseRepository.findByDayOfWeek(dayOfWeek);
    }

    /**
     * 根据课程名搜索课程
     */
    @GetMapping("/search/name/{courseName}")
    public List<Course> searchCoursesByName(@PathVariable String courseName) {
        logger.info("Searching courses by name: {}", courseName);
        return courseRepository.findByCourseNameContaining(courseName);
    }

    /**
     * 根据老师名搜索课程
     */
    @GetMapping("/search/teacher/{teacherName}")
    public List<Course> searchCoursesByTeacher(@PathVariable String teacherName) {
        logger.info("Searching courses by teacher: {}", teacherName);
        return courseRepository.findByTeacherNameContaining(teacherName);
    }

    /**
     * 根据地点搜索课程
     */
    @GetMapping("/search/location/{location}")
    public List<Course> searchCoursesByLocation(@PathVariable String location) {
        logger.info("Searching courses by location: {}", location);
        return courseRepository.findByLocationContaining(location);
    }
    
    /**
     * 根据周数查询课程
     */
    @GetMapping("/week/{weekNumber}")
    public List<Course> getCoursesByWeek(@PathVariable String weekNumber) {
        logger.info("Getting courses for week: {}", weekNumber);
        return courseRepository.findByWeekNumber(weekNumber);
    }
    
    /**
     * 根据周类型查询课程
     */
    @GetMapping("/type/{weekType}")
    public List<Course> getCoursesByWeekType(@PathVariable String weekType) {
        logger.info("Getting courses for week type: {}", weekType);
        return courseRepository.findByWeekType(weekType);
    }
    
    /**
     * 获取所有需要提醒的课程
     */
    @GetMapping("/reminders")
    public List<Course> getCoursesWithReminders() {
        logger.info("Getting all courses with reminders");
        return courseRepository.findByShouldReminderTrue();
    }
}