-- 初始化课程数据（可重复执行）。
-- 约定：weekRange 存储为具体周数字（例如 "1"、"2" ...），
--      weekType 存储为 "全周" | "单周" | "双周"。

-- 可选：清空表（谨慎使用）
-- TRUNCATE TABLE courses;

-- 示例课程：数学A（全周），每周周一 1-2节
INSERT INTO courses (
  course_name, location, week_range, day_of_week, time_slot,
  teacher_name, contact_info, property, remarks, week_type, should_reminder
) VALUES
('高等数学A', '教一-101', '1', '周一', '1-2节', '张老师', 'zhang@example.com', '必修', '全周课程示例', '全周', 0)
ON DUPLICATE KEY UPDATE course_name=VALUES(course_name);

-- 单周课程：英语口语（单周），周三 3-4节
INSERT INTO courses (
  course_name, location, week_range, day_of_week, time_slot,
  teacher_name, contact_info, property, remarks, week_type, should_reminder
) VALUES
('英语口语', '教二-202', '1', '周三', '3-4节', '李老师', 'li@example.com', '选修', '单周课程示例', '单周', 0)
ON DUPLICATE KEY UPDATE course_name=VALUES(course_name);

-- 双周课程：数据结构（双周），周五 6-7节
INSERT INTO courses (
  course_name, location, week_range, day_of_week, time_slot,
  teacher_name, contact_info, property, remarks, week_type, should_reminder
) VALUES
('数据结构', '教二-201', '1', '周五', '6-7节', '王老师', 'wang@example.com', '必修', '双周课程示例', '双周', 0)
ON DUPLICATE KEY UPDATE course_name=VALUES(course_name);

-- 生成 1-18 周复制（单/双周一致）：
-- 说明：下方使用简单循环示意，实际 MySQL 可用过程或用外部脚本一次性生成。
-- 若不使用存储过程，可手动复制以下 INSERT 块，将 week_range 改为 1..18

-- 示例：将上述三门课复制到第2周
INSERT INTO courses (course_name, location, week_range, day_of_week, time_slot, teacher_name, contact_info, property, remarks, week_type, should_reminder)
SELECT course_name, location, '2', day_of_week, time_slot, teacher_name, contact_info, property, remarks, week_type, 0 FROM courses WHERE week_range='1';

-- 第3周
INSERT INTO courses (course_name, location, week_range, day_of_week, time_slot, teacher_name, contact_info, property, remarks, week_type, should_reminder)
SELECT course_name, location, '3', day_of_week, time_slot, teacher_name, contact_info, property, remarks, week_type, 0 FROM courses WHERE week_range='1';

-- 第4周
INSERT INTO courses (course_name, location, week_range, day_of_week, time_slot, teacher_name, contact_info, property, remarks, week_type, should_reminder)
SELECT course_name, location, '4', day_of_week, time_slot, teacher_name, contact_info, property, remarks, week_type, 0 FROM courses WHERE week_range='1';

-- 请按需继续到第18周（或运行该脚本多次修改周次）。


