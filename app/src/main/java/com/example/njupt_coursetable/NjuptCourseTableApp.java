package com.example.njupt_coursetable;

import android.app.Application;

import com.example.njupt_coursetable.di.AppComponent;
import com.example.njupt_coursetable.di.AppModule;
import com.example.njupt_coursetable.di.ViewModelModule;

/**
 * 应用类
 * 负责初始化应用级别的组件和依赖注入
 */
public class NjuptCourseTableApp extends Application {

    /**
     * 应用组件实例
     */
    private AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        
        // 初始化依赖注入组件
        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .viewModelModule(new ViewModelModule())
                .build();
    }

    /**
     * 获取应用组件实例
     * @return AppComponent实例
     */
    public AppComponent getAppComponent() {
        return appComponent;
    }
}