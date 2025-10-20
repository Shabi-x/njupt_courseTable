package com.example.njupt_coursetable.repository;

import com.example.njupt_coursetable.model.Reminder;
import com.example.njupt_coursetable.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReminderRepository extends JpaRepository<Reminder, Long> {
    List<Reminder> findByCourse(Course course);
    List<Reminder> findByCourseDateGreaterThanEqual(LocalDate date);
    Optional<Reminder> findByCourseIdAndCourseDate(Long courseId, LocalDate date);
}





