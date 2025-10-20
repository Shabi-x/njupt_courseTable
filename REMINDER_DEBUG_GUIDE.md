# 课程提醒调试指南

## ✅ 已修复的问题

### 1. 数据库表结构问题
- **问题**：旧的`reminders`表结构与代码不匹配
- **解决**：已删除旧表并重新创建正确的表结构
- **验证**：`mysql -u root -plsj666666 -e "USE coursetable; DESCRIBE reminders;"`

### 2. 后端DTO类型不匹配
- **问题**：`ReminderDTO`使用`LocalDate/LocalTime`导致JSON序列化问题
- **解决**：已改为`String`类型，格式为`yyyy-MM-dd`和`HH:mm:ss`
- **验证**：`curl http://localhost:8080/api/reminders/upcoming`

### 3. 日期计算逻辑
- **问题**：基于"当前周"计算的日期可能已过期
- **解决**：修改`MainActivity.computeCourseDateForCurrentWeek()`，自动跳到下一次上课日期
- **影响**：现在添加提醒时会确保日期是未来的

### 4. 时间格式不一致
- **问题**：`LocalTime.toString()`会省略秒数（如`13:45`而非`13:45:00`）
- **解决**：使用`DateTimeFormatter.ofPattern("HH:mm:ss")`确保格式一致

## 🔍 调试步骤

### 步骤1：检查服务器状态
```bash
# 运行诊断脚本
./diagnose_reminder.sh

# 或手动检查
curl http://localhost:8080/api/courses/week/6
curl http://localhost:8080/api/reminders/upcoming
```

### 步骤2：检查Android日志
在Android Studio的Logcat中过滤以下标签：
- `MainActivity`
- `ReminderRepository`
- `CourseViewModel`
- `OkHttp`（查看网络请求）

查找以下关键信息：
1. **添加提醒请求**：
   ```
   D/OkHttp: --> POST http://10.0.2.2:8080/api/reminders?courseId=xxx&courseDate=2025-10-xx&startTime=xx:xx:xx
   D/OkHttp: <-- 200 OK
   ```

2. **可能的错误**：
   - `ConnectException`：无法连接到服务器
   - `SocketTimeoutException`：请求超时
   - HTTP 404/500：服务器端错误

### 步骤3：验证数据流

#### 3.1 用户点击提醒开关
```
HalfPanel.java (line 101-116)
├─> enableReminderSwitch.setOnCheckedChangeListener()
└─> onCourseReminderListener.onCourseReminderChanged(course, isChecked)
```

#### 3.2 MainActivity处理回调
```
MainActivity.java (line 384-401)
├─> 计算日期: computeCourseDateForCurrentWeek(course.getDayOfWeek())
├─> 计算时间: mapStartTimeByTimeSlot(course.getTimeSlot())
└─> courseViewModel.createReminder(courseId, date, time)
```

#### 3.3 ReminderRepository发送请求
```
ReminderRepository.java (line 39-46)
├─> api.createReminder(courseId, courseDate, startTime)
└─> 返回 LiveData<Boolean>
```

#### 3.4 服务器处理请求
```
ReminderController.java (line 47-56)
├─> 查找课程: courseRepository.findById(courseId)
├─> 创建提醒: new Reminder(course, courseDate, startTime)
└─> 保存到数据库: reminderRepository.save(reminder)
```

## 🐛 常见问题排查

### 问题1：添加提醒后列表不显示

**可能原因**：
1. 添加的日期是过去的日期
2. 服务器端保存失败
3. 客户端获取列表失败

**检查方法**：
```bash
# 检查数据库
mysql -u root -plsj666666 -e "USE coursetable; SELECT * FROM reminders ORDER BY course_date DESC;"

# 检查API
curl "http://localhost:8080/api/reminders/upcoming"
```

**解决方案**：
- 确保添加的课程日期 >= 今天
- 查看服务器日志确认保存成功
- 查看Android日志确认API调用成功

### 问题2：网络请求失败

**症状**：Toast显示"添加提醒失败"或"操作失败"

**可能原因**：
1. 服务器未启动（端口8080）
2. 模拟器无法访问`10.0.2.2`
3. 真机与电脑不在同一网络

**检查方法**：
```bash
# 检查服务器是否运行
lsof -i:8080

# 从模拟器/真机测试连接
adb shell
curl http://10.0.2.2:8080/api/courses/week/1
```

**解决方案**：
- 启动服务器：`cd server && mvn spring-boot:run`
- 模拟器使用：`http://10.0.2.2:8080/`
- 真机使用：`http://你的电脑IP:8080/`（修改`RetrofitClient.java`）

### 问题3：日期计算错误

**症状**：提醒日期与预期不符

**当前逻辑**：
```java
// 学期起始日：2025-09-01（周一）
// 第N周的某天 = 2025-09-01 + (N-1)*7 + weekday_offset
// 如果计算出的日期 < 今天，则每次加7天直到 >= 今天
```

**示例**：
- 当前周：第6周
- 今天：2025-10-20（周日）
- 添加周一课程：
  - 初始日期：2025-09-01 + 5*7 + 0 = 2025-10-06
  - 2025-10-06 < 2025-10-20，加7天 → 2025-10-13
  - 2025-10-13 < 2025-10-20，加7天 → 2025-10-20
  - 2025-10-20 = 2025-10-20，停止
  - 但今天已经过了，所以应该再加7天 → **2025-10-27**

**注意**：`Calendar.before()`在日期相等时返回`false`，所以如果计算出的日期正好是今天，不会再跳到下周。

### 问题4：提醒列表刷新不及时

**症状**：添加提醒后需要手动刷新才能看到

**原因**：`LiveData`观察者可能没有正确触发

**解决方案**：
在`MainActivity.onCourseReminderChanged()`中，添加提醒成功后手动调用`refreshUpcomingReminders()`

```java
// MainActivity.java line 393
courseViewModel.createReminder(course.getId(), courseDate, startTime).observe(this, ok -> {
    Toast.makeText(this, ok != null && ok ? "提醒已添加" : "添加提醒失败", Toast.LENGTH_SHORT).show();
    refreshUpcomingReminders(); // 刷新列表
});
```

## 📝 测试清单

在报告问题前，请完成以下测试：

- [ ] 服务器正常运行（`./diagnose_reminder.sh`显示✓）
- [ ] 数据库表结构正确（包含`reminders`表）
- [ ] 网络配置正确（`AndroidManifest.xml`有`INTERNET`权限和`usesCleartextTraffic="true"`）
- [ ] 添加提醒时查看Logcat日志（搜索"OkHttp"或"ReminderRepository"）
- [ ] 手动调用API测试（使用curl或Postman）
- [ ] 检查提醒日期是否是未来的日期

## 🛠️ 快速测试

```bash
# 1. 启动服务器
cd /Users/Shabix/AndroidStudioProjects/njupt_courseTable/server
mvn spring-boot:run

# 2. 在另一个终端运行诊断
cd /Users/Shabix/AndroidStudioProjects/njupt_courseTable
./diagnose_reminder.sh

# 3. 手动添加一个测试提醒
curl -X POST "http://localhost:8080/api/reminders?courseId=646&courseDate=2025-10-28&startTime=08:00:00"

# 4. 查看提醒列表
curl -X GET "http://localhost:8080/api/reminders/upcoming" | python3 -m json.tool

# 5. 删除测试提醒
curl -X DELETE "http://localhost:8080/api/reminders/byCourseDate?courseId=646&courseDate=2025-10-28"
```

## 📞 需要提供的信息

如果问题仍然存在，请提供：

1. **Android日志**：
   - 过滤标签：`MainActivity`、`ReminderRepository`、`OkHttp`
   - 从点击提醒开关到显示Toast的完整日志

2. **服务器日志**：
   - Spring Boot控制台输出
   - 特别是SQL语句和HTTP请求日志

3. **数据库状态**：
   ```bash
   mysql -u root -plsj666666 -e "USE coursetable; SELECT * FROM reminders;"
   ```

4. **测试步骤**：
   - 当前周数
   - 添加的是哪天的课程（周几）
   - 期望的提醒日期
   - 实际的结果（Toast消息、列表是否更新等）

