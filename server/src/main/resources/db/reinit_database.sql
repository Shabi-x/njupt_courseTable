-- ==========================================
-- 重新初始化数据库脚本
-- 使用方法: mysql -u root -plsj666666 coursetable < reinit_database.sql
-- ==========================================

USE coursetable;

-- 清空所有数据
DELETE FROM reminders;
DELETE FROM courses;

-- 重置自增ID（可选，如果需要从1开始）
ALTER TABLE reminders AUTO_INCREMENT = 1;
ALTER TABLE courses AUTO_INCREMENT = 1;

-- 验证清空结果
SELECT '数据已清空，请重启服务器以自动重新初始化数据' AS message;
SELECT COUNT(*) AS remaining_courses FROM courses;
SELECT COUNT(*) AS remaining_reminders FROM reminders;

