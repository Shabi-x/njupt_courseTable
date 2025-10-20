# 更新日志

## 2025-10-20 - 课程提醒功能修复与课程数据优化

### ✅ 已修复的问题

#### 1. 课程提醒功能问题
- **数据库表结构不匹配**
  - 删除旧的`reminders`表并重新创建
  - 新表结构：id, course_id, course_date, start_time, created_at
  
- **后端DTO类型不匹配**
  - 将`ReminderDTO`的`LocalDate`和`LocalTime`改为`String`类型
  - 统一使用`yyyy-MM-dd`和`HH:mm:ss`格式
  - 使用`DateTimeFormatter`确保格式一致

- **日期计算逻辑问题**
  - 修复`MainActivity.computeCourseDateForCurrentWeek()`方法
  - 现在会自动跳过已过期的日期，计算下一次上课日期
  - 避免添加过去的提醒

- **提醒列表过滤**
  - `/api/reminders/upcoming`只返回 >= 今天的提醒
  - 避免显示已过期的提醒

#### 2. 课程数据优化
- **课程名称真实化**
  - 从"课程A"、"课程B"改为真实的大三电子信息专业课程
  - 包含16种真实课程：信号与系统、数字信号处理、通信原理等
  
- **教师名称**
  - 使用真实的中文姓名：王建华、李明、张伟等14位教师
  - 邮箱格式：`姓名@njupt.edu.cn`

- **教室位置**
  - 真实的教室编号：教一-203、实验楼-401、机房-A201等

### 🗑️ 清理工作

#### 删除的文件
- `server/src/main/resources/db/seed_courses.sql` - 已删除
- `server/src/main/resources/db/seed_generate_18_weeks.sql` - 已删除
- `server/src/main/resources/db/init_courses.sql` - 已删除

#### 统一的数据初始化方式
- 使用Java代码自动初始化：`DataInitializer.java`
- 首次启动且数据库为空时自动执行
- 生成18周、每周10-15门课程
- 总计约200+门课程

### 📁 新增文件
- `server/src/main/resources/db/README.md` - SQL文件说明文档
- `diagnose_reminder.sh` - 课程提醒诊断工具
- `REMINDER_DEBUG_GUIDE.md` - 提醒功能调试指南

### 🎯 数据验证
```bash
# 第1周课程数：133门
# 课程类型分布：
- 必修课：信号与系统、数字信号处理、通信原理、微机原理与接口技术、电磁场与电磁波等
- 选修课：数字图像处理等
- 实验课：数字信号处理实验、通信原理实验、微机原理实验等
- 实践课：MATLAB课程设计、C语言程序设计等
```

### 📊 提醒功能数据流
1. 用户点击HalfPanel中的提醒开关
2. MainActivity计算课程日期和开始时间
3. 调用`courseViewModel.createReminder()`
4. ReminderRepository发送POST请求到服务器
5. ReminderController保存到数据库
6. 自动刷新提醒列表

### 🔧 诊断工具
运行诊断脚本检查系统状态：
```bash
./diagnose_reminder.sh
```

检查项目：
- 服务器状态
- 数据库连接
- 添加提醒测试
- 获取提醒列表
- Android端配置
- 常见问题排查

### 📝 注意事项
1. 服务器端口：8080
2. 学期起始日：2025-09-01（周一）
3. 默认当前周：第6周
4. 模拟器访问地址：http://10.0.2.2:8080/
5. 真机访问地址：http://你的电脑IP:8080/

### 🚀 快速启动
```bash
# 1. 启动MySQL数据库
mysql.server start

# 2. 启动后端服务器
cd server
mvn spring-boot:run

# 3. 运行Android应用
# 在Android Studio中点击Run

# 4. 验证功能
# - 查看课程表
# - 点击课程查看详情
# - 添加课程提醒
# - 切换到"提醒"标签查看列表
```

