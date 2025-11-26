#!/bin/bash

# 启动Spring Boot服务器脚本
# 使用方法: ./start_server.sh

echo "=========================================="
echo "启动课程表服务器..."
echo "=========================================="
echo ""

cd server

# 检查Maven是否安装
if ! command -v mvn &> /dev/null; then
    echo "❌ 错误: 未找到Maven，请先安装Maven"
    exit 1
fi

# 检查Java版本
echo "检查Java版本..."
java -version

echo ""
echo "正在启动服务器..."
echo "服务器将在 http://localhost:8080 启动"
echo "按 Ctrl+C 停止服务器"
echo ""

# 启动Spring Boot应用
mvn spring-boot:run

