package com.example.njupt_coursetable.di;

import com.example.njupt_coursetable.data.repository.CourseRepository;
import com.example.njupt_coursetable.data.repository.ReminderRepository;
import com.example.njupt_coursetable.ui.viewmodel.CourseViewModel;
import com.example.njupt_coursetable.ui.viewmodel.ReminderViewModel;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * ViewModel模块类
 * 提供ViewModel相关的依赖注入
 */
@Module
public class ViewModelModule {

    /**
     * 提供CourseViewModel实例
     * @param courseRepository 课程仓库
     * @return CourseViewModel实例
     */
    @Provides
    @Singleton
    CourseViewModel provideCourseViewModel(CourseRepository courseRepository) {
        return new CourseViewModel(courseRepository);
    }

    /**
     * 提供ReminderViewModel实例
     * @param reminderRepository 提醒仓库
     * @return ReminderViewModel实例
     */
    @Provides
    @Singleton
    ReminderViewModel provideReminderViewModel(ReminderRepository reminderRepository) {
        return new ReminderViewModel(reminderRepository);
    }
}