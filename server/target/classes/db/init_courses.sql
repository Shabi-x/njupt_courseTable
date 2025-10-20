-- ==========================================
-- 南京邮电大学 大三电子信息工程 课程表数据
-- 学期: 2025-2026学年第一学期
-- 学期起始日: 2025-09-01 (周一)
-- 共18周课程
-- ==========================================

-- 清空旧数据（谨慎使用）
-- DELETE FROM courses;
-- DELETE FROM reminders;

DELIMITER $$

DROP PROCEDURE IF EXISTS init_ee_courses$$

CREATE PROCEDURE init_ee_courses()
BEGIN
  DECLARE week INT DEFAULT 1;
  DECLARE semesterStart DATE DEFAULT '2025-09-01';
  DECLARE courseDate DATE;
  DECLARE weekDay INT;
  
  -- 清空旧数据
  DELETE FROM courses;
  
  -- 定义课程表（大三电子信息工程专业典型课程）
  -- 周一课程
  WHILE week <= 18 DO
    -- 周一
    SET courseDate = DATE_ADD(semesterStart, INTERVAL (week-1)*7 DAY);
    
    INSERT INTO courses (course_name, location, week_range, day_of_week, time_slot, teacher_name, contact_info, property, remarks, week_type, should_reminder)
    VALUES ('信号与系统', '教一-203', CAST(week AS CHAR), '周一', '1-2节', '王建华', 'wangjh@njupt.edu.cn', '必修', '重点课程', '全周', 0);
    
    INSERT INTO courses (course_name, location, week_range, day_of_week, time_slot, teacher_name, contact_info, property, remarks, week_type, should_reminder)
    VALUES ('数字信号处理', '教二-105', CAST(week AS CHAR), '周一', '3-4节', '李明', 'liming@njupt.edu.cn', '必修', '核心课程', '全周', 0);
    
    INSERT INTO courses (course_name, location, week_range, day_of_week, time_slot, teacher_name, contact_info, property, remarks, week_type, should_reminder)
    VALUES ('通信原理', '教一-301', CAST(week AS CHAR), '周一', '6-7节', '张伟', 'zhangwei@njupt.edu.cn', '必修', '专业核心课', '全周', 0);
    
    -- 周二
    SET courseDate = DATE_ADD(semesterStart, INTERVAL (week-1)*7+1 DAY);
    
    INSERT INTO courses (course_name, location, week_range, day_of_week, time_slot, teacher_name, contact_info, property, remarks, week_type, should_reminder)
    VALUES ('微机原理与接口技术', '实验楼-401', CAST(week AS CHAR), '周二', '1-2节', '陈静', 'chenjing@njupt.edu.cn', '必修', '实践性强', '全周', 0);
    
    INSERT INTO courses (course_name, location, week_range, day_of_week, time_slot, teacher_name, contact_info, property, remarks, week_type, should_reminder)
    VALUES ('电磁场与电磁波', '教二-208', CAST(week AS CHAR), '周二', '3-4节', '刘强', 'liuqiang@njupt.edu.cn', '必修', '理论课', '全周', 0);
    
    -- 周二下午实验课（单周）
    IF week % 2 = 1 THEN
      INSERT INTO courses (course_name, location, week_range, day_of_week, time_slot, teacher_name, contact_info, property, remarks, week_type, should_reminder)
      VALUES ('数字信号处理实验', '实验楼-302', CAST(week AS CHAR), '周二', '6-9节', '赵敏', 'zhaomin@njupt.edu.cn', '实验', 'MATLAB实验', '单周', 0);
    END IF;
    
    -- 周二下午实验课（双周）
    IF week % 2 = 0 THEN
      INSERT INTO courses (course_name, location, week_range, day_of_week, time_slot, teacher_name, contact_info, property, remarks, week_type, should_reminder)
      VALUES ('通信原理实验', '实验楼-303', CAST(week AS CHAR), '周二', '6-9节', '孙丽', 'sunli@njupt.edu.cn', '实验', '通信系统仿真', '双周', 0);
    END IF;
    
    -- 周三
    SET courseDate = DATE_ADD(semesterStart, INTERVAL (week-1)*7+2 DAY);
    
    INSERT INTO courses (course_name, location, week_range, day_of_week, time_slot, teacher_name, contact_info, property, remarks, week_type, should_reminder)
    VALUES ('高频电子线路', '教一-205', CAST(week AS CHAR), '周三', '1-2节', '周杰', 'zhoujie@njupt.edu.cn', '必修', '专业课', '全周', 0);
    
    INSERT INTO courses (course_name, location, week_range, day_of_week, time_slot, teacher_name, contact_info, property, remarks, week_type, should_reminder)
    VALUES ('数字图像处理', '教二-106', CAST(week AS CHAR), '周三', '3-4节', '吴洋', 'wuyang@njupt.edu.cn', '选修', '图像处理', '全周', 0);
    
    INSERT INTO courses (course_name, location, week_range, day_of_week, time_slot, teacher_name, contact_info, property, remarks, week_type, should_reminder)
    VALUES ('嵌入式系统设计', '实验楼-405', CAST(week AS CHAR), '周三', '6-7节', '郑浩', 'zhenghao@njupt.edu.cn', '必修', 'ARM开发', '全周', 0);
    
    -- 周四
    SET courseDate = DATE_ADD(semesterStart, INTERVAL (week-1)*7+3 DAY);
    
    INSERT INTO courses (course_name, location, week_range, day_of_week, time_slot, teacher_name, contact_info, property, remarks, week_type, should_reminder)
    VALUES ('信号与系统', '教一-203', CAST(week AS CHAR), '周四', '1-2节', '王建华', 'wangjh@njupt.edu.cn', '必修', '习题课', '全周', 0);
    
    INSERT INTO courses (course_name, location, week_range, day_of_week, time_slot, teacher_name, contact_info, property, remarks, week_type, should_reminder)
    VALUES ('EDA技术', '实验楼-501', CAST(week AS CHAR), '周四', '3-4节', '冯涛', 'fengtao@njupt.edu.cn', '必修', 'FPGA设计', '全周', 0);
    
    INSERT INTO courses (course_name, location, week_range, day_of_week, time_slot, teacher_name, contact_info, property, remarks, week_type, should_reminder)
    VALUES ('数据通信与计算机网络', '教二-302', CAST(week AS CHAR), '周四', '6-7节', '黄磊', 'huanglei@njupt.edu.cn', '必修', '网络原理', '全周', 0);
    
    -- 周五
    SET courseDate = DATE_ADD(semesterStart, INTERVAL (week-1)*7+4 DAY);
    
    INSERT INTO courses (course_name, location, week_range, day_of_week, time_slot, teacher_name, contact_info, property, remarks, week_type, should_reminder)
    VALUES ('通信原理', '教一-301', CAST(week AS CHAR), '周五', '1-2节', '张伟', 'zhangwei@njupt.edu.cn', '必修', '重难点讲解', '全周', 0);
    
    INSERT INTO courses (course_name, location, week_range, day_of_week, time_slot, teacher_name, contact_info, property, remarks, week_type, should_reminder)
    VALUES ('专业英语', '教二-201', CAST(week AS CHAR), '周五', '3-4节', '李娜', 'lina@njupt.edu.cn', '必修', '论文阅读', '全周', 0);
    
    -- 周五下午（前9周：MATLAB课程设计，后9周：无课）
    IF week <= 9 THEN
      INSERT INTO courses (course_name, location, week_range, day_of_week, time_slot, teacher_name, contact_info, property, remarks, week_type, should_reminder)
      VALUES ('MATLAB课程设计', '机房-A201', CAST(week AS CHAR), '周五', '6-9节', '田芳', 'tianfang@njupt.edu.cn', '实践', '综合实践', '全周', 0);
    END IF;
    
    SET week = week + 1;
  END WHILE;
  
END$$

DELIMITER ;

-- 执行存储过程
CALL init_ee_courses();

-- 删除存储过程（可选）
DROP PROCEDURE IF EXISTS init_ee_courses;

-- 验证数据
SELECT COUNT(*) AS total_courses FROM courses;
SELECT DISTINCT course_name FROM courses ORDER BY course_name;

