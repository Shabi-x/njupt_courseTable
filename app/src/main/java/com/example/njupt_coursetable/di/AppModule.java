package com.example.njupt_coursetable.di;

import android.content.Context;

import com.example.njupt_coursetable.data.local.AppDatabase;
import com.example.njupt_coursetable.data.local.dao.CourseDao;
import com.example.njupt_coursetable.data.local.dao.ReminderDao;
import com.example.njupt_coursetable.data.remote.RetrofitClient;
import com.example.njupt_coursetable.data.remote.api.CourseApiService;
import com.example.njupt_coursetable.data.remote.api.ReminderApiService;
import com.example.njupt_coursetable.data.repository.CourseRepository;
import com.example.njupt_coursetable.data.repository.ReminderRepository;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * 应用模块类
 * 提供应用级别的依赖注入
 */
@Module
public class AppModule {

    private final Context applicationContext;

    /**
     * 构造函数
     * @param context 应用上下文
     */
    public AppModule(Context context) {
        this.applicationContext = context.getApplicationContext();
    }

    /**
     * 提供应用上下文
     * @return 应用上下文
     */
    @Provides
    @Singleton
    Context provideApplicationContext() {
        return applicationContext;
    }

    /**
     * 提供AppDatabase实例
     * @param context 应用上下文
     * @return AppDatabase实例
     */
    @Provides
    @Singleton
    AppDatabase provideAppDatabase(Context context) {
        return AppDatabase.getInstance(context);
    }

    /**
     * 提供CourseDao实例
     * @param database 数据库实例
     * @return CourseDao实例
     */
    @Provides
    @Singleton
    CourseDao provideCourseDao(AppDatabase database) {
        return database.courseDao();
    }

    /**
     * 提供ReminderDao实例
     * @param database 数据库实例
     * @return ReminderDao实例
     */
    @Provides
    @Singleton
    ReminderDao provideReminderDao(AppDatabase database) {
        return database.reminderDao();
    }

    /**
     * 提供Retrofit实例
     * @param context 应用上下文
     * @return Retrofit实例
     */
    @Provides
    @Singleton
    retrofit2.Retrofit provideRetrofit(Context context) {
        return RetrofitClient.getClient(context);
    }

    /**
     * 提供CourseApiService实例
     * @param context 应用上下文
     * @return CourseApiService实例
     */
    @Provides
    @Singleton
    CourseApiService provideCourseApiService(Context context) {
        return RetrofitClient.getCourseApiService(context);
    }

    /**
     * 提供ReminderApiService实例
     * @param context 应用上下文
     * @return ReminderApiService实例
     */
    @Provides
    @Singleton
    ReminderApiService provideReminderApiService(Context context) {
        return RetrofitClient.getReminderApiService(context);
    }

    /**
     * 提供CourseRepository实例
     * @param context 应用上下文
     * @return CourseRepository实例
     */
    @Provides
    @Singleton
    CourseRepository provideCourseRepository(Context context) {
        return CourseRepository.getInstance(context);
    }

    /**
     * 提供ReminderRepository实例
     * @param context 应用上下文
     * @return ReminderRepository实例
     */
    @Provides
    @Singleton
    ReminderRepository provideReminderRepository(Context context) {
        return ReminderRepository.getInstance(context);
    }
}