package com.example.njupt_coursetable.di;

import com.example.njupt_coursetable.NjuptCourseTableApp;
import com.example.njupt_coursetable.ui.activity.MainActivity;
import com.example.njupt_coursetable.ui.viewmodel.CourseViewModel;

import javax.inject.Singleton;

import dagger.Component;

/**
 * 应用组件接口
 * 定义依赖注入的组件
 */
@Singleton
@Component(modules = {AppModule.class, ViewModelModule.class})
public interface AppComponent {

    /**
     * 注入到应用类
     * @param app 应用实例
     */
    void inject(NjuptCourseTableApp app);

    /**
     * 注入到主活动
     * @param mainActivity 主活动实例
     */
    void inject(MainActivity mainActivity);

    /**
     * 提供CourseViewModel实例
     * @return CourseViewModel实例
     */
    CourseViewModel courseViewModel();
}