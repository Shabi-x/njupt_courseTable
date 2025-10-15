# Course Table Server

这是南邮课程表应用的服务端部分，使用Spring Boot框架实现。

## 功能特性

- 课程管理API（增删改查）
- 提醒管理API（增删改查）
- 支持按条件搜索课程
- 支持按星期几获取课程
- 支持按课程名、教师名、地点搜索课程
- 支持管理提醒启用状态

## 技术栈

- Spring Boot 2.7.14
- Spring Data JPA
- H2 内存数据库
- Maven

## 快速开始

### 前置条件

- Java 11 或更高版本
- Maven 3.6 或更高版本

### 运行服务端

1. 在项目根目录下执行以下命令：

```bash
cd server
mvn spring-boot:run
```

2. 服务端将在 http://localhost:8080 启动

### 访问H2数据库控制台

1. 在浏览器中访问：http://localhost:8080/h2-console
2. 使用以下登录信息：
   - JDBC URL: `jdbc:h2:mem:coursetable`
   - 用户名: `sa`
   - 密码: (留空)

## API文档

### 课程API

- `GET /api/courses` - 获取所有课程
- `GET /api/courses/{id}` - 根据ID获取课程
- `POST /api/courses` - 创建新课程
- `PUT /api/courses/{id}` - 更新课程
- `DELETE /api/courses/{id}` - 删除课程
- `GET /api/courses/day/{dayOfWeek}` - 根据星期几获取课程
- `GET /api/courses/search/name/{courseName}` - 根据课程名搜索课程
- `GET /api/courses/search/teacher/{teacherName}` - 根据老师名搜索课程
- `GET /api/courses/search/location/{location}` - 根据地点搜索课程

### 提醒API

- `GET /api/reminders` - 获取所有提醒
- `GET /api/reminders/{id}` - 根据ID获取提醒
- `GET /api/reminders/course/{courseId}` - 根据课程ID获取提醒
- `POST /api/reminders` - 创建新提醒
- `PUT /api/reminders/{id}` - 更新提醒
- `DELETE /api/reminders/{id}` - 删除提醒
- `GET /api/reminders/enabled` - 获取所有启用的提醒
- `PUT /api/reminders/{id}/enabled/{enabled}` - 更新提醒启用状态

## 项目结构

```
server/
├── src/main/java/com/example/njupt_coursetable/
│   ├── config/           # 配置类
│   ├── controller/       # 控制器
│   ├── model/           # 实体类
│   ├── repository/      # 数据访问层
│   └── NjuptCoursetableServerApplication.java  # 主应用类
└── src/main/resources/
    └── application.properties  # 应用配置
```

## 配置说明

服务端配置在 `src/main/resources/application.properties` 文件中，包括：

- 服务器端口：8080
- 数据库配置：H2内存数据库
- 日志级别：DEBUG

## 开发说明

1. 服务端使用H2内存数据库，重启后数据会丢失
2. 如需持久化数据，可以修改配置使用MySQL等数据库
3. 服务端已配置CORS支持，允许跨域请求
4. 所有API都支持JSON格式的请求和响应