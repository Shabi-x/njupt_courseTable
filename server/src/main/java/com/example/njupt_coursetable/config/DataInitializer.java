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

            logger.info("Seeding database with EE courses for 18 weeks...");

            // 真实的大三电子信息专业课程
            String[] courseNames = {
                "信号与系统", "数字信号处理", "通信原理", "微机原理与接口技术", 
                "电磁场与电磁波", "高频电子线路", "数字图像处理", "嵌入式系统设计",
                "EDA技术", "数据通信与计算机网络", "专业英语", "MATLAB课程设计",
                "数字信号处理实验", "通信原理实验", "微机原理实验", "C语言程序设计"
            };
            String[] teachers = {"王建华", "李明", "张伟", "陈静", "刘强", "赵敏", "孙丽", "周杰", "吴洋", "郑浩", "冯涛", "黄磊", "李娜", "田芳"};
            String[] locations = {"教一-203", "教二-105", "教一-301", "实验楼-401", "教二-208", "教一-205", "教二-106", "实验楼-405", "实验楼-501", "教二-302", "教二-201", "机房-A201", "实验楼-302", "实验楼-303"};
            String[] days = {"周一", "周二", "周三", "周四", "周五"};
            String[] timeSlots = {"1-2节", "3-4节", "6-7节", "8-9节"};
            String[] properties = {"必修", "选修", "实验", "实践"};

            Random random = new Random(20250901);
            List<Course> toSave = new ArrayList<>();

            // 为18周生成课程
            for (int week = 1; week <= 18; week++) {
                int coursesThisWeek = 10 + random.nextInt(6); // 10-15门课程
                for (int i = 0; i < coursesThisWeek; i++) {
                    Course course = new Course();
                    course.setCourseName(courseNames[random.nextInt(courseNames.length)]);
                    course.setLocation(locations[random.nextInt(locations.length)]);
                    course.setWeekRange(String.valueOf(week));
                    course.setDayOfWeek(days[random.nextInt(days.length)]);
                    course.setTimeSlot(timeSlots[random.nextInt(timeSlots.length)]);
                    course.setTeacherName(teachers[random.nextInt(teachers.length)]);
                    course.setContactInfo(teachers[random.nextInt(teachers.length)].replace("老师", "") + "@njupt.edu.cn");
                    course.setProperty(properties[random.nextInt(properties.length)]);
                    course.setRemarks("大三电子信息专业课程");
                    course.setWeekType("全周");
                    course.setShouldReminder(false);
                    toSave.add(course);
                }
            }

            courseRepository.saveAll(toSave);
            logger.info("Seeded {} EE courses for 18 weeks.", toSave.size());
        };
    }
}
