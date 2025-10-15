package com.example.njupt_coursetable.di;

import com.example.njupt_coursetable.data.repository.CourseRepository;
import com.example.njupt_coursetable.ui.viewmodel.CourseViewModel;

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
     * @param application 应用实例
     * @param courseRepository 课程仓库
     * @return CourseViewModel实例
     */
    @Provides
    @Singleton
    CourseViewModel provideCourseViewModel(android.app.Application application, CourseRepository courseRepository) {
        return new CourseViewModel(application, courseRepository);
    }


}