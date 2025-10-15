package com.example.njupt_coursetable.repository;

import com.example.njupt_coursetable.model.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReminderRepository extends JpaRepository<Reminder, Long> {

    // 根据课程ID查询提醒
    Optional<Reminder> findByCourseId(Long courseId);

    // 查询所有启用的提醒
    List<Reminder> findByIsEnabledTrue();

    // 更新提醒启用状态
    @Modifying
    @Query("UPDATE Reminder r SET r.isEnabled = :enabled WHERE r.id = :id")
    int updateReminderEnabledStatus(@Param("id") Long id, @Param("enabled") Boolean enabled);
}