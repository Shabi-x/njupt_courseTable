# 课程数据SQL文件说明

## 数据初始化方式

本项目使用 **Java代码自动初始化** 课程数据，无需手动执行SQL文件。

### 自动初始化
- 位置：`server/src/main/java/com/example/njupt_coursetable/config/DataInitializer.java`
- 触发时机：服务器首次启动且数据库为空时自动执行
- 数据内容：大三电子信息工程专业18周课程（真实课程名）

###课程名称包括：
- 信号与系统
- 数字信号处理
- 通信原理
- 微机原理与接口技术
- 电磁场与电磁波
- 高频电子线路
- 数字图像处理
- 嵌入式系统设计
- EDA技术
- 数据通信与计算机网络
- 专业英语
- MATLAB课程设计
- 数字信号处理实验
- 通信原理实验
- 微机原理实验
- C语言程序设计

## init_courses.sql（备用）

如果需要手动初始化数据，可以使用此SQL文件：

```bash
mysql -u root -p coursetable < server/src/main/resources/db/init_courses.sql
```

**注意**：此文件为备用方案，正常情况下无需手动执行。

## 数据管理

### 清空数据
```sql
DELETE FROM reminders;
DELETE FROM courses;
```

### 重新初始化
1. 清空数据（上述SQL）
2. 重启服务器，DataInitializer会自动重新生成数据

## 数据统计

- 总周数：18周
- 每周课程：10-15门（随机分布）
- 总课程数：约200+门
- 上课时间：周一至周五
- 时间段：1-2节、3-4节、6-7节、8-9节

