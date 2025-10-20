# 🚀 项目设置指南

## 新成员快速上手

### 前置要求
- ✅ Java 11+
- ✅ Maven 3.6+
- ✅ MySQL 8.0+
- ✅ Android Studio (用于Android端开发)

---

## 📦 后端服务器设置

### 步骤1: 克隆项目
```bash
git clone <repository-url>
cd njupt_courseTable
```

### 步骤2: 创建数据库
```bash
# 方式A: 使用SQL文件（推荐）
mysql -u root -p < server/src/main/resources/db/setup.sql

# 方式B: 手动创建
mysql -u root -p
CREATE DATABASE coursetable CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
exit;
```

### 步骤3: 配置数据库连接
编辑 `server/src/main/resources/application.properties`：

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/coursetable?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=你的密码
```

### 步骤4: 启动服务器
```bash
cd server
mvn spring-boot:run
```

**首次启动时会自动：**
- ✅ 创建courses表和reminders表
- ✅ 生成18周、200+门课程模拟数据
- ✅ 使用真实的大三电子信息专业课程

启动成功后访问：http://localhost:8080

---

## 📱 Android客户端设置

### 步骤1: 打开Android Studio
```bash
# 打开项目根目录
open -a "Android Studio" .
```

### 步骤2: 配置服务器地址

根据你的开发环境选择：

**使用模拟器（默认）：**
- 无需修改，默认配置为 `http://10.0.2.2:8080/`

**使用真机：**
1. 找到你电脑的IP地址：
   ```bash
   ifconfig | grep "inet " | grep -v 127.0.0.1
   ```
2. 修改 `app/src/main/java/com/example/njupt_coursetable/data/remote/RetrofitClient.java`：
   ```java
   private static final String BASE_URL = "http://你的IP:8080/";
   ```

### 步骤3: 运行应用
1. 确保后端服务器正在运行
2. 点击 Android Studio 的 Run 按钮
3. 选择模拟器或真机

---

## 🔍 验证安装

### 测试后端API
```bash
# 1. 检查服务器状态
curl http://localhost:8080/api/courses/week/1

# 2. 查看课程数量
mysql -u root -p coursetable -e "SELECT COUNT(*) FROM courses;"

# 3. 查看课程列表
mysql -u root -p coursetable -e "SELECT course_name, day_of_week, time_slot FROM courses WHERE week_range='1' LIMIT 10;"

# 4. 运行诊断工具（可选）
./diagnose_reminder.sh
```

期望结果：
- ✅ API返回JSON格式的课程数据
- ✅ 数据库约有200+条课程记录
- ✅ 课程名称为真实专业课程（如"信号与系统"、"通信原理"等）

---

## 📊 数据说明

### 自动生成的数据
- **周数**: 18周
- **每周课程**: 10-15门（随机）
- **总课程数**: 约200+门
- **课程类型**: 16种真实的电子信息专业课程
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
- **教师**: 14位（王建华、李明、张伟等）
- **教室**: 14个（教一-203、实验楼-401等）

---

## 🔧 常见问题

### Q1: 数据库表没有自动创建？
**A**: 检查 `application.properties` 中的配置：
```properties
spring.jpa.hibernate.ddl-auto=update
```
确保此项设置为 `update` 而不是 `none`。

### Q2: 没有生成模拟数据？
**A**: 
1. 检查日志，确认DataInitializer是否执行
2. 如果已有数据，不会重复生成。清空数据库：
   ```sql
   DELETE FROM reminders;
   DELETE FROM courses;
   ```
3. 重启服务器

### Q3: Android应用无法连接服务器？
**A**:
1. 检查服务器是否运行：`curl http://localhost:8080/api/courses/week/1`
2. 模拟器使用 `10.0.2.2` 而不是 `localhost`
3. 真机需要与电脑在同一WiFi网络，并使用电脑IP
4. 检查 `AndroidManifest.xml` 是否有 `usesCleartextTraffic="true"`

### Q4: 如何重置所有数据？
**A**:
```bash
# 方式1: SQL命令
mysql -u root -p coursetable -e "DELETE FROM reminders; DELETE FROM courses;"
# 然后重启服务器

# 方式2: 删除表（会自动重建）
mysql -u root -p coursetable -e "DROP TABLE IF EXISTS reminders; DROP TABLE IF EXISTS courses;"
# 然后重启服务器
```

---

## 📚 相关文档

- `CHANGELOG.md` - 更新日志
- `REMINDER_DEBUG_GUIDE.md` - 提醒功能调试指南
- `server/src/main/resources/db/README.md` - 数据库文件说明
- `diagnose_reminder.sh` - 系统诊断工具

---

## 🆘 需要帮助？

如果遇到问题：
1. 查看上面的"常见问题"部分
2. 运行诊断工具：`./diagnose_reminder.sh`
3. 查看服务器日志
4. 查看Android Logcat（过滤 "OkHttp"、"MainActivity"）

---

## ✅ 成功标志

当你完成设置后，你应该能够：
- ✅ 启动后端服务器（端口8080）
- ✅ 通过API获取课程数据
- ✅ 启动Android应用
- ✅ 查看18周的课程表
- ✅ 点击课程查看详情
- ✅ 添加课程提醒
- ✅ 在"提醒"页面查看已添加的提醒

祝开发愉快！🎉

