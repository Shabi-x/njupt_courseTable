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
     * 使用精确匹配，避免"6"匹配到"16"的问题
     */
    @Query("SELECT c FROM Course c WHERE c.weekRange = :weekNumber")
    List<Course> findByWeekNumber(@Param("weekNumber") String weekNumber);
    
    /**
     * 查询所有需要提醒的课程
     */
    List<Course> findByShouldReminderTrue();
}