package com.example.njupt_coursetable.controller;

import com.example.njupt_coursetable.model.Reminder;
import com.example.njupt_coursetable.repository.ReminderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reminders")
@CrossOrigin(origins = "*")
public class ReminderController {

    private static final Logger logger = LoggerFactory.getLogger(ReminderController.class);

    @Autowired
    private ReminderRepository reminderRepository;

    /**
     * 获取所有提醒
     */
    @GetMapping
    public List<Reminder> getAllReminders() {
        logger.info("Getting all reminders");
        return reminderRepository.findAll();
    }

    /**
     * 根据ID获取提醒
     */
    @GetMapping("/{id}")
    public ResponseEntity<Reminder> getReminderById(@PathVariable Long id) {
        logger.info("Getting reminder with id: {}", id);
        Optional<Reminder> reminder = reminderRepository.findById(id);
        return reminder.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 根据课程ID获取提醒
     */
    @GetMapping("/course/{courseId}")
    public ResponseEntity<Reminder> getReminderByCourseId(@PathVariable Long courseId) {
        logger.info("Getting reminder for course with id: {}", courseId);
        Optional<Reminder> reminder = reminderRepository.findByCourseId(courseId);
        return reminder.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 创建新提醒
     */
    @PostMapping
    public Reminder createReminder(@RequestBody Reminder reminder) {
        logger.info("Creating new reminder: {}", reminder.getTitle());
        return reminderRepository.save(reminder);
    }

    /**
     * 更新提醒
     */
    @PutMapping("/{id}")
    public ResponseEntity<Reminder> updateReminder(@PathVariable Long id, @RequestBody Reminder reminderDetails) {
        logger.info("Updating reminder with id: {}", id);
        
        return reminderRepository.findById(id)
                .map(reminder -> {
                    reminder.setCourseId(reminderDetails.getCourseId());
                    reminder.setTitle(reminderDetails.getTitle());
                    reminder.setDescription(reminderDetails.getDescription());
                    reminder.setRemindTime(reminderDetails.getRemindTime());
                    reminder.setIsEnabled(reminderDetails.getIsEnabled());
                    reminder.setAdvanceMinutes(reminderDetails.getAdvanceMinutes());
                    return ResponseEntity.ok(reminderRepository.save(reminder));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 删除提醒
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReminder(@PathVariable Long id) {
        logger.info("Deleting reminder with id: {}", id);
        
        return reminderRepository.findById(id)
                .map(reminder -> {
                    reminderRepository.delete(reminder);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 获取所有启用的提醒
     */
    @GetMapping("/enabled")
    public List<Reminder> getEnabledReminders() {
        logger.info("Getting all enabled reminders");
        return reminderRepository.findByIsEnabledTrue();
    }

    /**
     * 更新提醒启用状态
     */
    @PutMapping("/{id}/enabled/{enabled}")
    public ResponseEntity<Reminder> updateReminderEnabledStatus(
            @PathVariable Long id, @PathVariable Boolean enabled) {
        logger.info("Updating reminder enabled status for id: {} to: {}", id, enabled);
        
        int updatedRows = reminderRepository.updateReminderEnabledStatus(id, enabled);
        
        if (updatedRows > 0) {
            return reminderRepository.findById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}