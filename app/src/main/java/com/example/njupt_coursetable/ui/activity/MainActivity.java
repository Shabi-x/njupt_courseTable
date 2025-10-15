package com.example.njupt_coursetable.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.njupt_coursetable.NjuptCourseTableApp;
import com.example.njupt_coursetable.R;
import com.example.njupt_coursetable.data.local.DataInitializer;
import com.example.njupt_coursetable.data.model.Course;
import com.example.njupt_coursetable.databinding.ActivityMainBinding;
import com.example.njupt_coursetable.di.AppComponent;
import com.example.njupt_coursetable.ui.adapter.CourseReminderAdapter;
import com.example.njupt_coursetable.ui.dialog.CourseDialog;
import com.example.njupt_coursetable.ui.view.CourseTableView;
import com.example.njupt_coursetable.ui.view.HalfPanel;
import com.example.njupt_coursetable.ui.view.OnCourseReminderListener;
import com.example.njupt_coursetable.ui.viewmodel.CourseViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * 主活动类
 * 应用程序的主界面，显示课程表和提醒
 */
public class MainActivity extends AppCompatActivity implements OnCourseReminderListener {

    private static final String TAG = "MainActivity";
    
    /**
     * 视图绑定对象
     */
    private ActivityMainBinding binding;
    
    /**
     * 课程视图模型
     */
    private CourseViewModel courseViewModel;
    
    /**
     * 课程提醒适配器
     */
    private CourseReminderAdapter courseReminderAdapter;
    
    /**
     * 应用组件
     */
    private AppComponent appComponent;

    // 当前周数
    private int currentWeek = 6;
    
    // 课程表视图
    private CourseTableView courseTableView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // 初始化视图绑定
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        // 获取应用组件
        appComponent = ((NjuptCourseTableApp) getApplication()).getAppComponent();
        
        // 初始化依赖注入
        initDependencyInjection();
        
        // 初始化UI
        initUI();
        
        // 初始化观察者
        initObservers();
        
        // 从服务器同步数据
        syncDataFromServer();
        
        // 设置日期显示
        setWeekDateDisplay();
    }

    /**
     * 初始化依赖注入
     */
    private void initDependencyInjection() {
        // 注入主活动
        appComponent.inject(this);
        
        // 获取视图模型
        courseViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends androidx.lifecycle.ViewModel> T create(@NonNull Class<T> modelClass) {
                if (modelClass.isAssignableFrom(CourseViewModel.class)) {
                    return (T) appComponent.courseViewModel();
                }
                throw new IllegalArgumentException("Unknown ViewModel class");
            }
        }).get(CourseViewModel.class);
    }

    /**
     * 初始化UI
     */
    private void initUI() {
        // 设置工具栏
        setSupportActionBar(binding.toolbar);
        
        // 初始化周数选择器
        initWeekSelector();
        
        // 初始化课程表视图
        courseTableView = binding.courseTableView;
        courseTableView.setCurrentWeek(currentWeek);
        
        // 获取HalfPanel并设置给CourseTableView
        HalfPanel halfPanel = binding.halfPanel;
        courseTableView.setHalfPanel(halfPanel);
        
        // 设置回调接口
        halfPanel.setOnCourseReminderListener(this);
        
        // 设置课程点击监听器
        courseTableView.setOnCourseClickListener(course -> {
            // 点击课程，显示课程详情面板
            if (course != null) {
                halfPanel.showCourseInfo(course);
            }
        });
        
        // 初始化提醒列表
        RecyclerView recyclerViewReminders = binding.recyclerViewReminders;
        recyclerViewReminders.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewReminders.setHasFixedSize(true);
        
        courseReminderAdapter = new CourseReminderAdapter(
                new CourseReminderAdapter.OnCourseReminderClickListener() {
                    @Override
                    public void onCourseReminderClick(Course course) {
                        // 点击课程项，显示课程详情
                        if (course != null) {
                            HalfPanel halfPanel = binding.halfPanel;
                            halfPanel.showCourseInfo(course);
                        }
                    }
                }
        );
        recyclerViewReminders.setAdapter(courseReminderAdapter);
        
        // 设置底部导航
        BottomNavigationView bottomNavigationView = binding.bottomNavigation;
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigation_courses) {
                // 显示课程表视图
                binding.layoutCourses.setVisibility(View.VISIBLE);
                binding.layoutReminders.setVisibility(View.GONE);
                return true;
            } else if (item.getItemId() == R.id.navigation_reminders) {
                // 显示提醒列表
                binding.layoutCourses.setVisibility(View.GONE);
                binding.layoutReminders.setVisibility(View.VISIBLE);
                return true;
            }
            return false;
        });
    }

    /**
     * 初始化观察者
     */
    private void initObservers() {
        // 观察课程列表变化
        courseViewModel.getAllCourses().observe(this, courses -> {
            Log.d(TAG, "Courses updated: " + (courses != null ? courses.size() : 0) + " items");
            
            // 设置课程数据到课程表视图
            if (courses != null && !courses.isEmpty()) {
                courseTableView.setCourses(courses);
                binding.courseTableView.setVisibility(View.VISIBLE);
                binding.textEmptyCourses.setVisibility(View.GONE);
            } else {
                // 如果没有课程数据，显示空状态
                courseTableView.setCourses(new ArrayList<>());
                binding.courseTableView.setVisibility(View.VISIBLE);
                binding.textEmptyCourses.setVisibility(View.VISIBLE);
            }
        });
        
        // 观察课程提醒列表变化
        courseViewModel.getCoursesWithReminder().observe(this, courses -> {
            Log.d(TAG, "Courses with reminders updated: " + (courses != null ? courses.size() : 0) + " items");
            
            if (courses != null && !courses.isEmpty()) {
                courseReminderAdapter.submitList(courses);
                binding.textEmptyReminders.setVisibility(View.GONE);
                binding.recyclerViewReminders.setVisibility(View.VISIBLE);
            } else {
                courseReminderAdapter.submitList(new ArrayList<>());
                binding.textEmptyReminders.setVisibility(View.VISIBLE);
                binding.recyclerViewReminders.setVisibility(View.GONE);
            }
        });
        
        // 观察操作结果
        courseViewModel.getOperationResult().observe(this, result -> {
            if (result != null) {
                if (result) {
                    Toast.makeText(this, "操作成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "操作失败", Toast.LENGTH_SHORT).show();
                }
                courseViewModel.resetOperationResult();
            }
        });
        
        // 观察加载状态
        courseViewModel.getIsLoading().observe(this, isLoading -> {
            if (binding.progressBar != null) {
                binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            }
        });
    }

    /**
     * 从服务器同步数据
     */
    private void syncDataFromServer() {
        // 直接从服务器同步数据，不使用本地示例数据
        courseViewModel.syncAllCoursesFromServer();
        courseViewModel.syncCoursesWithRemindersFromServer();
    }

    /**
     * 显示课程对话框
     * @param course 课程对象，如果为null则表示新建课程
     */
    private void showCourseDialog(Course course) {
        CourseDialog dialog = new CourseDialog(course);
        dialog.setListener(newCourse -> {
            if (course == null) {
                // 添加新课程
                courseViewModel.insertCourse(newCourse);
            } else {
                // 更新现有课程
                courseViewModel.updateCourse(newCourse);
            }
        });
        dialog.show(getSupportFragmentManager(), "CourseDialog");
    }



    /**
     * 设置周日期显示
     */
    private void setWeekDateDisplay() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd", Locale.getDefault());
        
        // 设置日期标签
        String[] dateStrings = new String[7];
        String[] weekdays = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
        
        // 找到本周一
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (dayOfWeek == 0) dayOfWeek = 7; // 周日特殊处理
        calendar.add(Calendar.DAY_OF_MONTH, - (dayOfWeek - 1));
        
        // 设置一周的日期
        for (int i = 0; i < 7; i++) {
            dateStrings[i] = dateFormat.format(calendar.getTime());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        
        // 设置课程表的日期
        courseTableView.setDateStrings(dateStrings);
    }

    /**
     * 初始化周数选择器
     */
    private void initWeekSelector() {
        // 创建周数选项列表（第1周到第18周）
        String[] weekOptions = new String[18];
        for (int i = 0; i < 18; i++) {
            weekOptions[i] = "第" + (i + 1) + "周";
        }
        
        // 创建适配器
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.spinner_week_item,
                weekOptions
        );
        adapter.setDropDownViewResource(R.layout.spinner_week_dropdown_item);
        
        // 设置适配器
        binding.spinnerWeekSelector.setAdapter(adapter);
        
        // 设置当前选中项
        binding.spinnerWeekSelector.setSelection(currentWeek - 1);
        
        // 设置选择监听器
        binding.spinnerWeekSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // 更新当前周数
                currentWeek = position + 1;
                
                // 更新课程表视图
                courseTableView.setCurrentWeek(currentWeek);
                
                // 更新日期显示
                setWeekDateDisplay();
            }
            
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // 不处理
            }
        });
    }

    /**
     * 显示更多选项菜单
     */
    private void showMoreOptionsMenu() {
        // 创建并显示更多选项菜单
        // 这里可以实现设置等功能
        Toast.makeText(this, "更多选项菜单", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        // 移除搜索菜单项
        menu.findItem(R.id.action_search).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_search) {
            // 打开搜索界面
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.action_settings) {
            // 打开设置界面
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    /**
     * 初始化示例数据（已废弃，使用syncDataFromServer中的同步方法）
     */
    @Deprecated
    private void initializeSampleData() {
        DataInitializer dataInitializer = new DataInitializer(this);
        dataInitializer.initializeSampleData();
    }
    
    @Override
    public void onCourseReminderChanged(Course course, boolean shouldReminder) {
        if (course != null) {
            if (shouldReminder) {
                // 添加课程提醒
                courseViewModel.addCourseReminder(course.getId());
            } else {
                // 移除课程提醒
                courseViewModel.removeCourseReminder(course.getId());
            }
        }
    }
}