-- ==========================================
-- 南京邮电大学课程表系统 - 数据库初始化脚本
-- ==========================================

-- 1. 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS coursetable
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE coursetable;

-- 2. 说明
-- 注意：表结构由Spring Boot的Hibernate自动创建（spring.jpa.hibernate.ddl-auto=update）
-- 课程数据由DataInitializer.java在服务器首次启动时自动生成

-- 3. 如果需要重置数据库，执行以下命令：
-- DROP TABLE IF EXISTS reminders;
-- DROP TABLE IF EXISTS courses;
-- 然后重启服务器，会自动重新创建表和数据

-- 4. 验证数据
-- SELECT COUNT(*) FROM courses;
-- SELECT DISTINCT course_name FROM courses ORDER BY course_name;

