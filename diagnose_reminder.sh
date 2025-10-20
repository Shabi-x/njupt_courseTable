#!/bin/bash
# 课程提醒诊断工具
# 用于检查添加提醒失败的原因

echo "======================================"
echo "课程提醒系统诊断工具"
echo "======================================"
echo ""

# 1. 检查服务器状态
echo "1. 检查服务器状态..."
if curl -s http://localhost:8080/api/courses/week/1 > /dev/null 2>&1; then
    echo "   ✓ 服务器运行正常 (http://localhost:8080)"
else
    echo "   ✗ 服务器无法访问！"
    echo "   请先启动服务器: cd server && mvn spring-boot:run"
    exit 1
fi
echo ""

# 2. 检查数据库连接
echo "2. 检查数据库..."
COURSE_COUNT=$(mysql -u root -plsj666666 -e "USE coursetable; SELECT COUNT(*) FROM courses;" 2>/dev/null | tail -1)
REMINDER_COUNT=$(mysql -u root -plsj666666 -e "USE coursetable; SELECT COUNT(*) FROM reminders;" 2>/dev/null | tail -1)
echo "   ✓ courses表: $COURSE_COUNT 条记录"
echo "   ✓ reminders表: $REMINDER_COUNT 条记录"
echo ""

# 3. 测试添加提醒API
echo "3. 测试添加提醒..."
COURSE_ID=646
COURSE_DATE="2025-10-28"  # 下周一
START_TIME="08:00:00"

echo "   课程ID: $COURSE_ID"
echo "   日期: $COURSE_DATE"
echo "   时间: $START_TIME"
echo ""

RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "http://localhost:8080/api/reminders?courseId=$COURSE_ID&courseDate=$COURSE_DATE&startTime=$START_TIME")
HTTP_CODE=$(echo "$RESPONSE" | tail -1)
BODY=$(echo "$RESPONSE" | head -n -1)

if [ "$HTTP_CODE" = "200" ]; then
    echo "   ✓ 添加成功！HTTP 200"
    echo "   响应: $(echo $BODY | python3 -m json.tool 2>/dev/null || echo $BODY)"
else
    echo "   ✗ 添加失败！HTTP $HTTP_CODE"
    echo "   响应: $BODY"
fi
echo ""

# 4. 测试获取提醒列表
echo "4. 测试获取提醒列表..."
UPCOMING=$(curl -s "http://localhost:8080/api/reminders/upcoming")
COUNT=$(echo "$UPCOMING" | python3 -c "import sys, json; print(len(json.load(sys.stdin)))" 2>/dev/null || echo "0")
echo "   ✓ 获取到 $COUNT 条即将到来的提醒"
if [ "$COUNT" -gt "0" ]; then
    echo ""
    echo "   提醒列表:"
    echo "$UPCOMING" | python3 -m json.tool 2>/dev/null || echo "$UPCOMING"
fi
echo ""

# 5. 检查Android端配置
echo "5. Android端配置检查..."
echo "   请确认以下配置正确:"
echo "   - RetrofitClient中的BASE_URL指向: http://10.0.2.2:8080/ (模拟器) 或 http://你的IP:8080/ (真机)"
echo "   - 网络权限已添加到AndroidManifest.xml"
echo "   - 如果使用真机，确保手机和电脑在同一WiFi网络"
echo ""

# 6. 常见问题检查
echo "6. 常见问题检查..."
echo "   [问题1] 日期计算是否正确？"
echo "   - 当前周: 第6周"
echo "   - 学期起始日: 2025-09-01"
echo "   - 今天: $(date +%Y-%m-%d)"
echo "   - 建议: 添加未来一周的课程提醒"
echo ""
echo "   [问题2] 时间格式是否正确？"
echo "   - courseDate格式: yyyy-MM-dd (如: 2025-10-28)"
echo "   - startTime格式: HH:mm:ss (如: 08:00:00)"
echo ""
echo "   [问题3] 提醒列表为空？"
echo "   - API只返回 >= 今天的提醒"
echo "   - 检查添加的提醒日期是否是未来的日期"
echo ""

echo "======================================"
echo "诊断完成！"
echo "======================================"

