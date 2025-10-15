package com.example.njupt_coursetable.data.local;

import android.content.Context;

import com.example.njupt_coursetable.data.model.Course;
import com.example.njupt_coursetable.data.model.Reminder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 数据初始化类
 * 用于在应用首次启动时添加示例数据
 */
public class DataInitializer {
    
    private final AppDatabase database;
    private final ExecutorService executorService;
    private final Random random = new Random();
    
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
        List<Course> allCourses = new ArrayList<>();
        
        // 添加全周课程（每周都有）
        allCourses.add(new Course("体育", "体育馆", "1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18", "周一", "7-8节", "体育老师", "pe@njupt.edu.cn", "必修", "", "全周"));
        allCourses.add(new Course("思想政治", "教一-201", "1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18", "周三", "7-8节", "思政老师", "sz@njupt.edu.cn", "必修", "", "全周"));
        
        // 添加单周课程（1,3,5,7,9,11,13,15,17周）
        // 周一课程 - 上午和下午分散
        allCourses.add(new Course("高等数学", "教二-101", "1,3,5,7,9,11,13,15,17", "周一", "1-2节", "张老师", "zhang@njupt.edu.cn", "必修", "", "单周"));
        allCourses.add(new Course("线性代数", "教三-201", "1,3,5,7,9,11,13,15,17", "周一", "5-6节", "李老师", "li@njupt.edu.cn", "必修", "", "单周"));
        
        // 周二课程 - 上午和下午分散
        allCourses.add(new Course("大学英语", "教一-301", "1,3,5,7,9,11,13,15,17", "周二", "3-4节", "王老师", "wang@njupt.edu.cn", "必修", "", "单周"));
        
        // 周三课程 - 只有上午
        allCourses.add(new Course("数据结构", "教二-102", "1,3,5,7,9,11,13,15,17", "周三", "1-2节", "刘老师", "liu@njupt.edu.cn", "必修", "", "单周"));
        
        // 周四课程 - 下午和晚上分散
        allCourses.add(new Course("操作系统", "教一-302", "1,3,5,7,9,11,13,15,17", "周四", "3-4节", "杨老师", "yang@njupt.edu.cn", "必修", "", "单周"));
        allCourses.add(new Course("数据库原理", "教四-402", "1,3,5,7,9,11,13,15,17", "周四", "9-10节", "黄老师", "huang@njupt.edu.cn", "必修", "", "单周"));
        
        // 周五课程 - 只有上午
        allCourses.add(new Course("软件工程", "教二-103", "1,3,5,7,9,11,13,15,17", "周五", "1-2节", "周老师", "zhou@njupt.edu.cn", "选修", "", "单周"));
        
        // 添加双周课程（2,4,6,8,10,12,14,16,18周）
        // 周一课程 - 只有下午
        allCourses.add(new Course("离散数学", "教二-104", "2,4,6,8,10,12,14,16,18", "周一", "3-4节", "孙老师", "sun@njupt.edu.cn", "必修", "", "双周"));
        
        // 周二课程 - 上午和晚上分散
        allCourses.add(new Course("日语", "教一-304", "2,4,6,8,10,12,14,16,18", "周二", "1-2节", "冯老师", "feng@njupt.edu.cn", "选修", "", "双周"));
        allCourses.add(new Course("C语言程序设计", "教四-401", "2,4,6,8,10,12,14,16,18", "周二", "9-10节", "赵老师", "zhao@njupt.edu.cn", "必修", "", "双周"));
        
        // 周三课程 - 下午和晚上分散
        allCourses.add(new Course("计算机网络", "教三-202", "2,4,6,8,10,12,14,16,18", "周三", "5-6节", "陈老师", "chen@njupt.edu.cn", "必修", "", "双周"));
        
        // 周四课程 - 只有上午
        allCourses.add(new Course("概率论与数理统计", "教三-204", "2,4,6,8,10,12,14,16,18", "周四", "1-2节", "钱老师", "qian@njupt.edu.cn", "必修", "", "双周"));
        
        // 周五课程 - 下午和晚上分散
        allCourses.add(new Course("人工智能导论", "教三-203", "2,4,6,8,10,12,14,16,18", "周五", "3-4节", "吴老师", "wu@njupt.edu.cn", "选修", "", "双周"));
        allCourses.add(new Course("Web开发技术", "教二-106", "2,4,6,8,10,12,14,16,18", "周五", "7-8节", "朱老师", "zhu@njupt.edu.cn", "选修", "", "双周"));
        
        // 添加一些随机分布的课程，使课程表更真实
        addRandomCourses(allCourses);
        
        // 批量插入所有课程
        database.courseDao().insertAll(allCourses.toArray(new Course[0]));
    }
    
    /**
     * 添加随机分布的课程，使课程表更真实
     */
    private void addRandomCourses(List<Course> courses) {
        String[] courseNames = {
            "Python程序设计", "移动应用开发", "信息安全", "编译原理", 
            "计算机组成原理", "大学物理", "大学化学", "马克思主义基本原理",
            "中国近现代史纲要", "形势与政策", "创新创业基础", "心理健康教育",
            "机器学习", "深度学习", "数据挖掘", "云计算", "物联网技术",
            "嵌入式系统", "数字图像处理", "计算机图形学", "自然语言处理",
            "大数据分析", "Java高级编程", "算法设计", "数据科学基础",
            "统计学", "运筹学", "管理学原理", "经济学原理", "心理学"
        };
        
        String[] teachers = {
            "魏老师", "秦老师", "沈老师", "韩老师", "杨老师", "朱老师", 
            "徐老师", "何老师", "高老师", "林老师", "罗老师", "梁老师",
            "张老师", "李老师", "王老师", "刘老师", "陈老师", "黄老师",
            "赵老师", "周老师", "吴老师", "郑老师", "孙老师", "钱老师"
        };
        
        String[] locations = {
            "教一-101", "教一-102", "教一-103", "教二-201", "教二-202", "教二-203",
            "教三-301", "教三-302", "教三-303", "教四-401", "教四-402", "教四-403",
            "实验楼-201", "实验楼-202", "实验楼-301", "实验楼-302",
            "实-101", "实-102", "实-201", "实-202", "实-301", "实-302"
        };
        
        // 时间段池 - 分为上午、下午和晚上
        String[] morningSlots = {"1-2节", "3-4节"};  // 上午
        String[] afternoonSlots = {"5-6节", "7-8节"};  // 下午
        String[] eveningSlots = {"9-10节"};  // 晚上
        
        String[] weekDays = {"周一", "周二", "周三", "周四", "周五"};
        String[] weekTypes = {"单周", "双周", "全周"};
        String[] properties = {"必修", "选修", "专业选修", "公共选修", "实践课"};
        
        // 减少随机课程数量以降低密度
        int courseCount = 5 + random.nextInt(4); // 5-8门随机课程
        
        for (int i = 0; i < courseCount; i++) {
            String courseName = courseNames[random.nextInt(courseNames.length)];
            String teacherName = teachers[random.nextInt(teachers.length)];
            String location = locations[random.nextInt(locations.length)];
            String dayOfWeek = weekDays[random.nextInt(weekDays.length)];
            String weekType = weekTypes[random.nextInt(weekTypes.length)];
            String property = properties[random.nextInt(properties.length)];
            
            // 随机选择时间段，确保分布均匀
            String timeSlot;
            int timeSlotType = random.nextInt(3); // 0=上午, 1=下午, 2=晚上
            if (timeSlotType == 0) {
                timeSlot = morningSlots[random.nextInt(morningSlots.length)];
            } else if (timeSlotType == 1) {
                timeSlot = afternoonSlots[random.nextInt(afternoonSlots.length)];
            } else {
                timeSlot = eveningSlots[random.nextInt(eveningSlots.length)];
            }
            
            String teacherEmail = teacherName.toLowerCase().replace("老师", "") + "@njupt.edu.cn";
            
            // 根据周类型生成具体的周数
            String weekNumbers;
            if (weekType.equals("单周")) {
                weekNumbers = "1,3,5,7,9,11,13,15,17";
            } else if (weekType.equals("双周")) {
                weekNumbers = "2,4,6,8,10,12,14,16,18";
            } else { // 全周
                weekNumbers = "1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18";
            }
            
            courses.add(new Course(
                courseName, 
                location, 
                weekNumbers, 
                dayOfWeek, 
                timeSlot, 
                teacherName, 
                teacherEmail, 
                property, 
                "", 
                weekType
            ));
        }
    }
    
    /**
     * 添加示例提醒数据
     */
    private void addSampleReminders() {
        // 这里可以添加一些示例提醒数据
        // 由于需要关联课程ID，我们暂时不添加示例提醒
    }
}