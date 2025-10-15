# 南邮课程表应用

这是一个包含客户端和服务端的完整课程表应用，客户端使用Android开发，服务端使用Spring Boot实现。

## 项目结构

```
njupt_courseTable/
├── app/                   # Android客户端
└── server/                # Spring Boot服务端
```

## 功能特性

- 课程管理（增删改查）
- 提醒功能
- 课程搜索
- 数据同步

## 快速开始

### 启动服务端

1. 确保已安装Java 11和Maven
2. 在项目根目录执行：
   ```bash
   cd server
   mvn spring-boot:run
   ```
3. 服务端将在 http://localhost:8080 启动

### 运行客户端

1. 使用Android Studio打开项目
2. 连接Android设备或启动模拟器
3. 运行应用

## 技术栈

### 客户端
- Android
- Java
- Room数据库
- Retrofit网络库
- Dagger依赖注入

### 服务端
- Spring Boot
- Spring Data JPA
- H2数据库
- Maven

## API文档

详见 [server/README.md](server/README.md)