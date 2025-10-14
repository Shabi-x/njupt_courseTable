package com.example.njupt_coursetable.data.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.njupt_coursetable.data.local.dao.CourseDao;
import com.example.njupt_coursetable.data.local.dao.ReminderDao;
import com.example.njupt_coursetable.data.model.Course;
import com.example.njupt_coursetable.data.model.Reminder;

/**
 * 应用数据库类
 * 使用Room持久化库创建和管理数据库
 */
@Database(
    entities = {Course.class, Reminder.class},
    version = 2,
    exportSchema = false
)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    /**
     * 数据库名称
     */
    private static final String DATABASE_NAME = "njupt_course_table_db";

    /**
     * 数据库单例实例
     */
    private static volatile AppDatabase INSTANCE;

    /**
     * 获取课程数据访问对象
     * @return CourseDao实例
     */
    public abstract CourseDao courseDao();

    /**
     * 获取提醒数据访问对象
     * @return ReminderDao实例
     */
    public abstract ReminderDao reminderDao();

    /**
     * 获取数据库实例（单例模式）
     * @param context 应用上下文
     * @return AppDatabase实例
     */
    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = buildDatabase(context);
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 数据库迁移策略
     * 从版本1到版本2的迁移
     */
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // 由于我们只是添加了新的方法，没有改变表结构，所以不需要执行任何SQL语句
            // 如果将来需要修改表结构，可以在这里添加相应的SQL语句
        }
    };

    /**
     * 构建数据库实例
     * @param context 应用上下文
     * @return AppDatabase实例
     */
    private static AppDatabase buildDatabase(Context context) {
        return Room.databaseBuilder(
                context.getApplicationContext(),
                AppDatabase.class,
                DATABASE_NAME
        )
        .addCallback(new RoomDatabase.Callback() {
            @Override
            public void onCreate(SupportSQLiteDatabase db) {
                super.onCreate(db);
                // 数据库创建时的回调，可以在这里添加初始数据
            }
        })
        .addMigrations(MIGRATION_1_2)
        .fallbackToDestructiveMigration() // 如果迁移失败，则重建数据库
        .build();
    }

    /**
     * 类型转换器类，用于处理Room不支持的数据类型
     */
    public static class Converters {
        // 如果需要添加自定义类型转换器，可以在这里添加
        // 例如：Date类型、List类型等
    }
}