package com.example.njupt_coursetable.repository;

import com.example.njupt_coursetable.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    // 根据星期几查询课程
    List<Course> findByDayOfWeek(String dayOfWeek);

    // 根据课程名模糊查询
    @Query("SELECT c FROM Course c WHERE c.courseName LIKE %:courseName%")
    List<Course> findByCourseNameContaining(@Param("courseName") String courseName);

    // 根据老师名模糊查询
    @Query("SELECT c FROM Course c WHERE c.teacherName LIKE %:teacherName%")
    List<Course> findByTeacherNameContaining(@Param("teacherName") String teacherName);

    // 根据地点模糊查询
    @Query("SELECT c FROM Course c WHERE c.location LIKE %:location%")
    List<Course> findByLocationContaining(@Param("location") String location);
    
    // 根据周数查询课程
    @Query("SELECT c FROM Course c WHERE c.weekRange LIKE %:weekNumber%")
    List<Course> findByWeekNumber(@Param("weekNumber") String weekNumber);
    
    // 根据周类型查询课程
    List<Course> findByWeekType(String weekType);
    
    // 查询所有需要提醒的课程
    List<Course> findByShouldReminderTrue();
}