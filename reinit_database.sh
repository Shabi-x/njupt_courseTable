#!/bin/bash

# 数据库重新初始化脚本
# 使用方法: ./reinit_database.sh

echo "=========================================="
echo "南京邮电大学课程表系统 - 数据库重新初始化"
echo "=========================================="
echo ""

# 数据库配置
DB_NAME="coursetable"
DB_USER="root"
DB_PASS="lsj666666"

echo "正在连接数据库..."
mysql -u $DB_USER -p$DB_PASS $DB_NAME <<EOF
-- 清空所有数据
DELETE FROM reminders;
DELETE FROM courses;

-- 重置自增ID
ALTER TABLE reminders AUTO_INCREMENT = 1;
ALTER TABLE courses AUTO_INCREMENT = 1;

-- 验证清空结果
SELECT '数据已清空' AS status;
SELECT COUNT(*) AS remaining_courses FROM courses;
SELECT COUNT(*) AS remaining_reminders FROM reminders;
EOF

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ 数据库已清空！"
    echo ""
    echo "📝 下一步操作："
    echo "   1. 重启Spring Boot服务器"
    echo "   2. DataInitializer会自动检测到数据库为空并重新初始化数据"
    echo ""
    echo "   或者手动验证数据："
    echo "   mysql -u root -plsj666666 coursetable -e \"SELECT COUNT(*) FROM courses;\""
else
    echo ""
    echo "❌ 数据库清空失败，请检查："
    echo "   1. MySQL服务是否运行"
    echo "   2. 用户名和密码是否正确"
    echo "   3. 数据库是否存在"
fi

