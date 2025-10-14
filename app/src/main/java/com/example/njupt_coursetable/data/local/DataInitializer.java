package com.example.njupt_coursetable.data.local;

import android.content.Context;

import com.example.njupt_coursetable.data.model.Course;
import com.example.njupt_coursetable.data.model.Reminder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 数据初始化类
 * 用于在应用首次启动时添加示例数据
 */
public class DataInitializer {
    
    private final AppDatabase database;
    private final ExecutorService executorService;
    
    public DataInitializer(Context context) {
        database = AppDatabase.getInstance(context);
        executorService = Executors.newSingleThreadExecutor();
    }
    
    /**
     * 初始化示例数据
     */
    public void initializeSampleData() {
        executorService.execute(() -> {
            // 检查是否已有数据
            if (database.courseDao().getAllCoursesSync().isEmpty()) {
                // 添加示例课程
                addSampleCourses();
            }
            
            // 检查是否已有提醒数据
            if (database.reminderDao().getAllRemindersSync().isEmpty()) {
                // 添加示例提醒
                addSampleReminders();
            }
        });
    }
    
    /**
     * 添加示例课程数据
     */
    private void addSampleCourses() {
        Course[] courses = {
            new Course("高等数学", "教二-101", "1-16周", "周一", "1-2节", "张老师", "zhang@njupt.edu.cn", "必修", ""),
            new Course("线性代数", "教三-201", "1-16周", "周一", "3-4节", "李老师", "li@njupt.edu.cn", "必修", ""),
            new Course("大学英语", "教一-301", "1-16周", "周二", "1-2节", "王老师", "wang@njupt.edu.cn", "必修", ""),
            new Course("C语言程序设计", "教四-401", "1-16周", "周二", "3-4节", "赵老师", "zhao@njupt.edu.cn", "必修", ""),
            new Course("数据结构", "教二-102", "1-16周", "周三", "1-2节", "刘老师", "liu@njupt.edu.cn", "必修", ""),
            new Course("计算机网络", "教三-202", "1-16周", "周三", "3-4节", "陈老师", "chen@njupt.edu.cn", "必修", ""),
            new Course("操作系统", "教一-302", "1-16周", "周四", "1-2节", "杨老师", "yang@njupt.edu.cn", "必修", ""),
            new Course("数据库原理", "教四-402", "1-16周", "周四", "3-4节", "黄老师", "huang@njupt.edu.cn", "必修", ""),
            new Course("软件工程", "教二-103", "1-16周", "周五", "1-2节", "周老师", "zhou@njupt.edu.cn", "选修", ""),
            new Course("人工智能导论", "教三-203", "1-16周", "周五", "3-4节", "吴老师", "wu@njupt.edu.cn", "选修", "")
        };
        
        database.courseDao().insertAll(courses);
    }
    
    /**
     * 添加示例提醒数据
     */
    private void addSampleReminders() {
        // 这里可以添加一些示例提醒数据
        // 由于需要关联课程ID，我们暂时不添加示例提醒
    }
}