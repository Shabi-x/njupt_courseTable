package com.example.njupt_coursetable.config;

import com.example.njupt_coursetable.model.Course;
import com.example.njupt_coursetable.repository.CourseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Configuration
public class DataInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    public CommandLineRunner seedCourses(CourseRepository courseRepository) {
        return args -> {
            long existing = courseRepository.count();
            if (existing > 0) {
                logger.info("Courses already exist ({} rows), skip auto seeding.", existing);
                return;
            }

            logger.info("Seeding database with mock courses for 18 weeks starting 2025-09-01...");

            String[] days = {"周一", "周二", "周三", "周四", "周五"};
            String[] teachers = {"张老师", "李老师", "王老师", "赵老师", "陈老师"};
            String[] locations = {"教一-101", "教一-102", "教二-201", "教二-202", "实验楼-301"};
            String[] properties = {"必修", "选修"};
            String[] timeSlots = {"1-2节", "3-4节", "6-7节", "8-9节"}; // 上午/下午常见节次

            Random random = new Random(20250901);
            List<Course> toSave = new ArrayList<>();

            for (int week = 1; week <= 18; week++) {
                // 每周生成 10-15 门随机课程，均匀分布：5天*（上午/下午）
                int coursesThisWeek = 10 + random.nextInt(6); // 10..15

                int basePerDay = coursesThisWeek / 5; // 每天基础节数
                int remainder = coursesThisWeek % 5;   // 前 remainder 天 +1

                for (int dayIdx = 0; dayIdx < 5; dayIdx++) { // 周一..周五
                    String dayOfWeek = days[dayIdx];

                    int countForThisDay = basePerDay + (dayIdx < remainder ? 1 : 0);

                    // 上/下午时间段分组
                    String[] morning = {"1-2节", "3-4节"};
                    String[] afternoon = {"6-7节", "8-9节"};

                    // 尽量一半上午一半下午
                    int morningCount = countForThisDay / 2;
                    int afternoonCount = countForThisDay - morningCount;

                    // 上午课程
                    for (int i = 0; i < morningCount; i++) {
                        String courseName = "课程" + (char)('A' + random.nextInt(26));
                        String timeSlot = morning[random.nextInt(morning.length)];
                        String teacher = teachers[random.nextInt(teachers.length)];
                        String location = locations[random.nextInt(locations.length)];
                        String property = properties[random.nextInt(properties.length)];

                        Course course = new Course();
                        course.setCourseName(courseName);
                        course.setLocation(location);
                        course.setWeekRange(String.valueOf(week));
                        course.setDayOfWeek(dayOfWeek);
                        course.setTimeSlot(timeSlot);
                        course.setTeacherName(teacher);
                        course.setContactInfo("teacher@example.com");
                        course.setProperty(property);
                        course.setRemarks("模拟数据");
                        course.setWeekType("全周");
                        course.setShouldReminder(false);
                        toSave.add(course);
                    }

                    // 下午课程
                    for (int i = 0; i < afternoonCount; i++) {
                        String courseName = "课程" + (char)('A' + random.nextInt(26));
                        String timeSlot = afternoon[random.nextInt(afternoon.length)];
                        String teacher = teachers[random.nextInt(teachers.length)];
                        String location = locations[random.nextInt(locations.length)];
                        String property = properties[random.nextInt(properties.length)];

                        Course course = new Course();
                        course.setCourseName(courseName);
                        course.setLocation(location);
                        course.setWeekRange(String.valueOf(week));
                        course.setDayOfWeek(dayOfWeek);
                        course.setTimeSlot(timeSlot);
                        course.setTeacherName(teacher);
                        course.setContactInfo("teacher@example.com");
                        course.setProperty(property);
                        course.setRemarks("模拟数据");
                        course.setWeekType("全周");
                        course.setShouldReminder(false);
                        toSave.add(course);
                    }
                }
            }

            courseRepository.saveAll(toSave);
            logger.info("Seeded {} courses.", toSave.size());
        };
    }
}



