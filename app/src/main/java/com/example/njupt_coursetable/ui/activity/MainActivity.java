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
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
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
public class MainActivity extends AppCompatActivity implements OnCourseReminderListener, HalfPanel.OnCourseDeleteListener {

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
    
    // 添加课程按钮
    private FloatingActionButton fabAddCourse;

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
        halfPanel.setOnCourseDeleteListener(this);
        
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
        
        // 设置添加课程按钮
        fabAddCourse = binding.fabAddCourse;
        fabAddCourse.setOnClickListener(v -> showAddCourseDialog());
        
        // 设置底部导航
        BottomNavigationView bottomNavigationView = binding.bottomNavigation;
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigation_courses) {
                // 显示课程表视图
                binding.layoutCourses.setVisibility(View.VISIBLE);
                binding.layoutReminders.setVisibility(View.GONE);
                fabAddCourse.show(); // 显示FAB
                return true;
            } else if (item.getItemId() == R.id.navigation_reminders) {
                // 显示提醒列表
                binding.layoutCourses.setVisibility(View.GONE);
                binding.layoutReminders.setVisibility(View.VISIBLE);
                fabAddCourse.hide(); // 隐藏FAB
                // 刷新提醒列表
                loadUpcomingReminders();
                return true;
            }
            return false;
        });
    }

    /**
     * 初始化观察者
     */
    private void initObservers() {
        // 移除对本地所有课程的覆盖性观察，避免本地空数据清空服务端按周结果
        
        // 观察远程提醒列表变化
        courseViewModel.getUpcomingReminders().observe(this, reminders -> {
            Log.d(TAG, "Upcoming reminders: " + (reminders != null ? reminders.size() : 0));
            if (reminders != null && !reminders.isEmpty()) {
                courseReminderAdapter.submitList(reminders);
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
        // 首次进入默认拉取当前周与提醒
        courseViewModel.syncCoursesByWeekFromServer(String.valueOf(currentWeek)).observe(this, weekCourses -> {
            if (weekCourses != null) {
                courseTableView.setCourses(weekCourses);
            }
        });
        refreshUpcomingReminders();
    }

    /**
     * 显示课程对话框
     * @param course 课程对象，如果为null则表示新建课程
     */
    private void showCourseDialog(Course course) {
        // 获取当前周的课程列表用于冲突检测
        List<Course> currentWeekCourses = courseTableView.getCourses();
        
        CourseDialog dialog = new CourseDialog(course, currentWeekCourses);
        dialog.setListener(newCourse -> {
            if (course == null) {
                // 添加新课程
                courseViewModel.insertCourse(newCourse).observe(MainActivity.this, id -> {
                    if (id != null && id > 0) {
                        Toast.makeText(MainActivity.this, "添加课程成功", Toast.LENGTH_SHORT).show();
                        refreshCoursesForCurrentWeek();
                    } else {
                        Toast.makeText(MainActivity.this, "添加课程失败", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                // 更新现有课程
                courseViewModel.updateCourse(newCourse).observe(MainActivity.this, rowsAffected -> {
                    if (rowsAffected != null && rowsAffected > 0) {
                        Toast.makeText(MainActivity.this, "更新课程成功", Toast.LENGTH_SHORT).show();
                        refreshCoursesForCurrentWeek();
                    } else {
                        Toast.makeText(MainActivity.this, "更新课程失败", Toast.LENGTH_SHORT).show();
                    }
                });
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
        
        // 设置选择监听器（切换周时，从服务端拉取该周课程并只显示这周）
        binding.spinnerWeekSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // 更新当前周数
                currentWeek = position + 1;
                
                // 更新课程表视图
                courseTableView.setCurrentWeek(currentWeek);
                
                // 更新日期显示
                setWeekDateDisplay();

                // 拉取该周课程并展示
                courseViewModel.syncCoursesByWeekFromServer(String.valueOf(currentWeek)).observe(MainActivity.this, weekCourses -> {
                    if (weekCourses != null) {
                        courseTableView.setCourses(weekCourses);
                    } else {
                        courseTableView.setCourses(new ArrayList<>());
                    }
                });
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
        if (course == null) return;

        String courseDate = computeCourseDateForCurrentWeek(course.getDayOfWeek()); // yyyy-MM-dd
        String startTime = mapStartTimeByTimeSlot(course.getTimeSlot()); // HH:mm:ss

        if (shouldReminder) {
            courseViewModel.createReminder(course.getId(), courseDate, startTime).observe(this, ok -> {
                Toast.makeText(this, ok != null && ok ? "提醒已添加" : "添加提醒失败", Toast.LENGTH_SHORT).show();
                refreshUpcomingReminders();
            });
        } else {
            courseViewModel.deleteReminderByCourseAndDate(course.getId(), courseDate).observe(this, ok -> {
                Toast.makeText(this, ok != null && ok ? "提醒已移除" : "移除提醒失败", Toast.LENGTH_SHORT).show();
                refreshUpcomingReminders();
            });
        }
    }

    // 重新获取即将到来的提醒并渲染
    private void refreshUpcomingReminders() {
        courseViewModel.getUpcomingReminders().observe(this, reminders -> {
            if (reminders != null && !reminders.isEmpty()) {
                courseReminderAdapter.submitList(reminders);
                binding.textEmptyReminders.setVisibility(View.GONE);
                binding.recyclerViewReminders.setVisibility(View.VISIBLE);
            } else {
                courseReminderAdapter.submitList(new ArrayList<>());
                binding.textEmptyReminders.setVisibility(View.VISIBLE);
                binding.recyclerViewReminders.setVisibility(View.GONE);
            }
        });
    }

    // 计算下一次该星期几对应的具体日期（yyyy-MM-dd）
    // 如果计算出的日期已经过去，则返回下一周的同一天
    private String computeCourseDateForCurrentWeek(String dayOfWeekStr) {
        int dayOffset = mapWeekdayToOffset(dayOfWeekStr); // 周一=0 .. 周日=6
        Calendar cal = Calendar.getInstance();
        cal.set(2025, Calendar.SEPTEMBER, 1, 0, 0, 0); // 2025-09-01
        cal.set(Calendar.MILLISECOND, 0);
        int days = (currentWeek - 1) * 7 + dayOffset;
        cal.add(Calendar.DAY_OF_MONTH, days);
        
        // 如果计算出的日期已经过去，则找到下一次上课的日期
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        
        while (cal.before(today)) {
            cal.add(Calendar.WEEK_OF_YEAR, 1); // 加一周
        }
        
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return fmt.format(cal.getTime());
    }

    private int mapWeekdayToOffset(String s) {
        if (s == null) return 0;
        switch (s) {
            case "周一": return 0;
            case "周二": return 1;
            case "周三": return 2;
            case "周四": return 3;
            case "周五": return 4;
            case "周六": return 5;
            case "周日": return 6;
            default: return 0;
        }
    }

    private String mapStartTimeByTimeSlot(String timeSlot) {
        if (timeSlot == null) return "08:00:00";
        if (timeSlot.contains("1-2")) return "08:00:00";
        if (timeSlot.contains("3-4")) return "09:50:00";
        if (timeSlot.contains("6-7")) return "13:45:00";
        if (timeSlot.contains("8-9")) return "15:35:00";
        return "08:00:00";
    }
    
    /**
     * 加载即将到来的提醒
     */
    private void loadUpcomingReminders() {
        courseViewModel.getUpcomingReminders().observe(this, reminders -> {
            if (reminders != null && !reminders.isEmpty()) {
                courseReminderAdapter.submitList(reminders);
                binding.recyclerViewReminders.setVisibility(View.VISIBLE);
                binding.textEmptyReminders.setVisibility(View.GONE);
            } else {
                courseReminderAdapter.submitList(new ArrayList<>());
                binding.recyclerViewReminders.setVisibility(View.GONE);
                binding.textEmptyReminders.setVisibility(View.VISIBLE);
            }
        });
    }
    
    /**
     * 刷新当前周的课程数据
     */
    private void refreshCoursesForCurrentWeek() {
        courseViewModel.syncCoursesByWeekFromServer(String.valueOf(currentWeek)).observe(this, weekCourses -> {
            if (weekCourses != null) {
                courseTableView.setCourses(weekCourses);
            }
        });
    }
    
    /**
     * 显示添加课程对话框
     */
    private void showAddCourseDialog() {
        // 获取当前周的课程列表用于冲突检测
        List<Course> currentWeekCourses = courseTableView.getCourses();
        
        CourseDialog dialog = new CourseDialog(null, currentWeekCourses); // null表示新建模式
        dialog.setCurrentWeek(currentWeek); // 设置当前周数
        dialog.setListener(course -> {
            // 调用创建方法并观察结果
            courseViewModel.insertCourse(course).observe(MainActivity.this, id -> {
                if (id != null && id > 0) {
                    Toast.makeText(MainActivity.this, "添加课程成功", Toast.LENGTH_SHORT).show();
                    // 延迟刷新，等待服务器同步完成
                    courseTableView.postDelayed(() -> {
                        refreshCoursesForCurrentWeek();
                    }, 500); // 延迟500毫秒
                } else {
                    Toast.makeText(MainActivity.this, "添加课程失败", Toast.LENGTH_SHORT).show();
                }
            });
        });
        dialog.show(getSupportFragmentManager(), "add_course");
    }
    
    /**
     * 实现HalfPanel.OnCourseDeleteListener接口
     * 删除课程
     */
    @Override
    public void onCourseDelete(Course course) {
        if (course == null || course.getId() <= 0) {
            Toast.makeText(this, "无效的课程", Toast.LENGTH_SHORT).show();
            return;
        }
        
        long courseId = course.getId();
        
        // 调用删除方法并观察结果
        courseViewModel.deleteCourse(courseId).observe(MainActivity.this, rowsAffected -> {
            if (rowsAffected != null && rowsAffected > 0) {
                Toast.makeText(MainActivity.this, "删除课程成功", Toast.LENGTH_SHORT).show();
                // 刷新当前周的课程数据
                refreshCoursesForCurrentWeek();
            } else {
                Toast.makeText(MainActivity.this, "删除课程失败", Toast.LENGTH_SHORT).show();
            }
        });
    }
}