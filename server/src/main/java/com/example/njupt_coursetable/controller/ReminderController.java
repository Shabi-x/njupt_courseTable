package com.example.njupt_coursetable.controller;

import com.example.njupt_coursetable.model.Course;
import com.example.njupt_coursetable.model.Reminder;
import com.example.njupt_coursetable.repository.CourseRepository;
import com.example.njupt_coursetable.repository.ReminderRepository;
import com.example.njupt_coursetable.controller.dto.ReminderDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/reminders")
@CrossOrigin(origins = "*")
public class ReminderController {

    @Autowired
    private ReminderRepository reminderRepository;

    @Autowired
    private CourseRepository courseRepository;

    @GetMapping("/upcoming")
    public List<ReminderDTO> getUpcoming() {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        List<Reminder> list = reminderRepository.findByCourseDateGreaterThanEqual(LocalDate.now());
        return list.stream().map(r -> {
            ReminderDTO dto = new ReminderDTO();
            dto.id = r.getId();
            if (r.getCourse() != null) {
                dto.courseId = r.getCourse().getId();
                dto.courseName = r.getCourse().getCourseName();
                dto.location = r.getCourse().getLocation();
                dto.dayOfWeek = r.getCourse().getDayOfWeek();
                dto.timeSlot = r.getCourse().getTimeSlot();
            }
            dto.courseDate = r.getCourseDate().toString();
            dto.startTime = r.getStartTime().format(timeFormatter);
            return dto;
        }).toList();
    }

    @PostMapping
    public ResponseEntity<Reminder> create(
            @RequestParam Long courseId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate courseDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime
    ) {
        return courseRepository.findById(courseId)
                .map(c -> ResponseEntity.ok(reminderRepository.save(new Reminder(c, courseDate, startTime))))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return reminderRepository.findById(id)
                .map(r -> { reminderRepository.delete(r); return ResponseEntity.ok().<Void>build(); })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/byCourseDate")
    public ResponseEntity<Void> deleteByCourseAndDate(
            @RequestParam Long courseId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate courseDate
    ) {
        return reminderRepository.findByCourseIdAndCourseDate(courseId, courseDate)
                .map(r -> { reminderRepository.delete(r); return ResponseEntity.ok().<Void>build(); })
                .orElse(ResponseEntity.notFound().build());
    }
}


