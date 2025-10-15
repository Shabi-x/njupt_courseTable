package com.example.njupt_coursetable.repository;

import com.example.njupt_coursetable.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    
    /**
     * 根据周数查询课程
     */
    @Query("SELECT c FROM Course c WHERE c.weekRange LIKE %:weekNumber%")
    List<Course> findByWeekNumber(@Param("weekNumber") String weekNumber);
    
    /**
     * 查询所有需要提醒的课程
     */
    List<Course> findByShouldReminderTrue();
}