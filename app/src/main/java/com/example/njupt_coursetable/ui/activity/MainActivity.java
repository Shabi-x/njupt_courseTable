package com.example.njupt_coursetable.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.example.njupt_coursetable.data.model.Reminder;
import com.example.njupt_coursetable.databinding.ActivityMainBinding;
import com.example.njupt_coursetable.di.AppComponent;
import com.example.njupt_coursetable.ui.adapter.ReminderAdapter;
import com.example.njupt_coursetable.ui.dialog.CourseDialog;
import com.example.njupt_coursetable.ui.dialog.ReminderDialog;
import com.example.njupt_coursetable.ui.view.CourseTableView;
import com.example.njupt_coursetable.ui.view.HalfPanel;
import com.example.njupt_coursetable.ui.viewmodel.CourseViewModel;
import com.example.njupt_coursetable.ui.viewmodel.ReminderViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * 主活动类
 * 应用程序的主界面，显示课程表和提醒
 */
public class MainActivity extends AppCompatActivity {

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
     * 提醒视图模型
     */
    private ReminderViewModel reminderViewModel;
    
    /**
     * 提醒适配器
     */
    private ReminderAdapter reminderAdapter;
    
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
        
        // 初始化示例数据
        initializeSampleData();
        
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
                } else if (modelClass.isAssignableFrom(ReminderViewModel.class)) {
                    return (T) appComponent.reminderViewModel();
                }
                throw new IllegalArgumentException("Unknown ViewModel class");
            }
        }).get(CourseViewModel.class);
        
        reminderViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends androidx.lifecycle.ViewModel> T create(@NonNull Class<T> modelClass) {
                if (modelClass.isAssignableFrom(CourseViewModel.class)) {
                    return (T) appComponent.courseViewModel();
                } else if (modelClass.isAssignableFrom(ReminderViewModel.class)) {
                    return (T) appComponent.reminderViewModel();
                }
                throw new IllegalArgumentException("Unknown ViewModel class");
            }
        }).get(ReminderViewModel.class);
    }

    /**
     * 初始化UI
     */
    private void initUI() {
        // 设置工具栏
        setSupportActionBar(binding.toolbar);
        
        // 设置周数显示
        binding.textCurrentWeek.setText("第" + currentWeek + "周");
        
        // 初始化课程表视图
        courseTableView = binding.courseTableView;
        courseTableView.setCurrentWeek(currentWeek);
        
        // 获取HalfPanel并设置给CourseTableView
        HalfPanel halfPanel = binding.halfPanel;
        courseTableView.setHalfPanel(halfPanel);
        
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
        
        reminderAdapter = new ReminderAdapter(
                new ReminderAdapter.OnReminderClickListener() {
                    @Override
                    public void onReminderClick(Reminder reminder) {
                        // 点击提醒项，编辑提醒
                        showReminderDialog(reminder);
                    }
                },
                new ReminderAdapter.OnReminderSwitchChangeListener() {
                    @Override
                    public void onReminderSwitchChange(Reminder reminder, boolean isEnabled) {
                        // 更新提醒启用状态
                        reminderViewModel.updateReminderEnabledStatus(reminder.getId(), isEnabled);
                    }
                }
        );
        recyclerViewReminders.setAdapter(reminderAdapter);
        
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
        
        // 设置更多选项按钮
        binding.btnMore.setOnClickListener(view -> {
            // 显示更多选项菜单
            showMoreOptionsMenu();
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
            if (courses != null) {
                courseTableView.setCourses(courses);
            }
            
            // 更新空状态视图
            if (courses == null || courses.isEmpty()) {
                binding.courseTableView.setVisibility(View.GONE);
                binding.textEmptyCourses.setVisibility(View.VISIBLE);
                binding.layoutCourses.setVisibility(View.GONE);
            } else {
                binding.courseTableView.setVisibility(View.VISIBLE);
                binding.textEmptyCourses.setVisibility(View.GONE);
                binding.layoutCourses.setVisibility(View.VISIBLE);
            }
        });
        
        // 观察提醒列表变化
        reminderViewModel.getAllReminders().observe(this, reminders -> {
            Log.d(TAG, "Reminders updated: " + (reminders != null ? reminders.size() : 0) + " items");
            reminderAdapter.submitList(reminders);
            
            // 更新空状态视图
            if (reminders == null || reminders.isEmpty()) {
                binding.textEmptyReminders.setVisibility(View.VISIBLE);
                binding.recyclerViewReminders.setVisibility(View.GONE);
            } else {
                binding.textEmptyReminders.setVisibility(View.GONE);
                binding.recyclerViewReminders.setVisibility(View.VISIBLE);
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
        
        reminderViewModel.getOperationResult().observe(this, result -> {
            if (result != null) {
                if (result) {
                    Toast.makeText(this, "操作成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "操作失败", Toast.LENGTH_SHORT).show();
                }
                reminderViewModel.resetOperationResult();
            }
        });
        
        // 观察加载状态
        courseViewModel.getIsLoading().observe(this, isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });
        
        reminderViewModel.getIsLoading().observe(this, isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });
    }

    /**
     * 从服务器同步数据
     */
    private void syncDataFromServer() {
        // 同步课程数据
        courseViewModel.syncCoursesFromServer();
        
        // 同步提醒数据
        reminderViewModel.syncRemindersFromServer();
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
     * 显示提醒对话框
     * @param reminder 提醒对象，如果为null则表示新建提醒
     */
    private void showReminderDialog(Reminder reminder) {
        // 获取所有课程
        List<Course> courses = courseViewModel.getAllCoursesSync();
        
        ReminderDialog dialog = new ReminderDialog(reminder, courses);
        dialog.setListener(newReminder -> {
            if (reminder == null) {
                // 添加新提醒
                reminderViewModel.insertReminder(newReminder);
            } else {
                // 更新现有提醒
                reminderViewModel.updateReminder(newReminder);
            }
        });
        dialog.show(getSupportFragmentManager(), "ReminderDialog");
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
     * 显示更多选项菜单
     */
    private void showMoreOptionsMenu() {
        // 创建并显示更多选项菜单
        // 这里可以实现周数切换、设置等功能
        Toast.makeText(this, "更多选项菜单", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_sync) {
            // 同步数据
            syncDataFromServer();
            return true;
        } else if (item.getItemId() == R.id.action_settings) {
            // 打开设置界面
            Toast.makeText(this, "设置功能开发中", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    /**
     * 初始化示例数据
     */
    private void initializeSampleData() {
        DataInitializer dataInitializer = new DataInitializer(this);
        dataInitializer.initializeSampleData();
    }
}