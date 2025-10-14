package com.example.njupt_coursetable.ui.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.njupt_coursetable.R;
import com.example.njupt_coursetable.data.model.Course;
import com.example.njupt_coursetable.ui.adapter.CourseSearchAdapter;
import com.example.njupt_coursetable.ui.viewmodel.CourseViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    private static final String TAG = "SearchActivity";
    
    private EditText editSearch;
    private ImageView btnBack;
    private ImageView btnClear;
    private Spinner spinnerWeekday;
    private Spinner spinnerWeekType;
    private RecyclerView recyclerViewSearchResults;
    private View textEmptyResults;
    private View progressBar;
    
    private CourseViewModel courseViewModel;
    private CourseSearchAdapter searchAdapter;
    
    private String[] weekdays = {"全部", "周一", "周二", "周三", "周四", "周五", "周六", "周日"};
    private String[] weekTypes = {"全部", "单周", "双周", "全周"};
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        
        // 初始化ViewModel
        courseViewModel = new ViewModelProvider(this).get(CourseViewModel.class);
        
        // 初始化视图
        initViews();
        
        // 设置监听器
        setupListeners();
        
        // 初始化适配器
        initAdapter();
        
        // 加载初始数据
        loadInitialData();
    }
    
    /**
     * 初始化视图
     */
    private void initViews() {
        editSearch = findViewById(R.id.edit_search);
        btnBack = findViewById(R.id.btn_back);
        btnClear = findViewById(R.id.btn_clear);
        spinnerWeekday = findViewById(R.id.spinner_weekday);
        spinnerWeekType = findViewById(R.id.spinner_week_type);
        recyclerViewSearchResults = findViewById(R.id.recycler_view_search_results);
        textEmptyResults = findViewById(R.id.text_empty_results);
        progressBar = findViewById(R.id.progress_bar);
        
        // 设置星期选择器
        ArrayAdapter<String> weekdayAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, weekdays);
        weekdayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerWeekday.setAdapter(weekdayAdapter);
        
        // 设置周类型选择器
        ArrayAdapter<String> weekTypeAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, weekTypes);
        weekTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerWeekType.setAdapter(weekTypeAdapter);
    }
    
    /**
     * 设置监听器
     */
    private void setupListeners() {
        // 返回按钮
        btnBack.setOnClickListener(v -> finish());
        
        // 清除按钮
        btnClear.setOnClickListener(v -> {
            editSearch.setText("");
            btnClear.setVisibility(View.GONE);
        });
        
        // 搜索框文本变化监听
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnClear.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
                performSearch();
            }
            
            @Override
            public void afterTextChanged(Editable s) {}
        });
        
        // 星期选择器监听
        spinnerWeekday.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                performSearch();
            }
            
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        
        // 周类型选择器监听
        spinnerWeekType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                performSearch();
            }
            
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }
    
    /**
     * 初始化适配器
     */
    private void initAdapter() {
        searchAdapter = new CourseSearchAdapter(new ArrayList<>());
        recyclerViewSearchResults.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewSearchResults.setAdapter(searchAdapter);
    }
    
    /**
     * 加载初始数据
     */
    private void loadInitialData() {
        // 显示加载状态
        progressBar.setVisibility(View.VISIBLE);
        
        // 获取所有课程
        courseViewModel.getAllCourses().observe(this, courses -> {
            progressBar.setVisibility(View.GONE);
            
            if (courses != null && !courses.isEmpty()) {
                // 更新搜索结果
                performSearch();
            } else {
                // 显示空状态
                textEmptyResults.setVisibility(View.VISIBLE);
                recyclerViewSearchResults.setVisibility(View.GONE);
            }
        });
    }
    
    /**
     * 执行搜索
     */
    private void performSearch() {
        String searchText = editSearch.getText().toString().trim();
        int weekdayPosition = spinnerWeekday.getSelectedItemPosition();
        int weekTypePosition = spinnerWeekType.getSelectedItemPosition();
        
        Log.d(TAG, "Search: text=" + searchText + ", weekday=" + weekdayPosition + ", weekType=" + weekTypePosition);
        
        // 获取所有课程
        List<Course> allCourses = courseViewModel.getAllCoursesSync();
        if (allCourses == null || allCourses.isEmpty()) {
            textEmptyResults.setVisibility(View.VISIBLE);
            recyclerViewSearchResults.setVisibility(View.GONE);
            return;
        }
        
        // 根据条件筛选课程
        List<Course> filteredCourses = filterCourses(allCourses, searchText, weekdayPosition, weekTypePosition);
        
        // 更新UI
        if (filteredCourses.isEmpty()) {
            textEmptyResults.setVisibility(View.VISIBLE);
            recyclerViewSearchResults.setVisibility(View.GONE);
        } else {
            textEmptyResults.setVisibility(View.GONE);
            recyclerViewSearchResults.setVisibility(View.VISIBLE);
            searchAdapter.updateCourses(filteredCourses);
        }
    }
    
    /**
     * 根据条件筛选课程
     */
    private List<Course> filterCourses(List<Course> courses, String searchText, int weekdayPosition, int weekTypePosition) {
        List<Course> result = new ArrayList<>();
        
        for (Course course : courses) {
            // 检查星期
            if (weekdayPosition > 0) { // 0表示"全部"
                String weekday = weekdays[weekdayPosition];
                if (!weekday.equals(course.getDayOfWeek())) {
                    continue;
                }
            }
            
            // 检查周类型
            if (weekTypePosition > 0) { // 0表示"全部"
                String weekType = weekTypes[weekTypePosition];
                if (!weekType.equals(course.getWeekType())) {
                    continue;
                }
            }
            
            // 检查搜索文本
            if (!searchText.isEmpty()) {
                String courseName = course.getCourseName().toLowerCase();
                String location = course.getLocation().toLowerCase();
                String teacher = course.getTeacher().toLowerCase();
                String searchLower = searchText.toLowerCase();
                
                if (!courseName.contains(searchLower) && 
                    !location.contains(searchLower) && 
                    !teacher.contains(searchLower)) {
                    continue;
                }
            }
            
            // 所有条件都满足，添加到结果
            result.add(course);
        }
        
        return result;
    }
    
    /**
     * 从中文星期获取数字表示
     */
    private int getDayOfWeekFromChinese(String chineseWeekday) {
        switch (chineseWeekday) {
            case "周一": return 1;
            case "周二": return 2;
            case "周三": return 3;
            case "周四": return 4;
            case "周五": return 5;
            case "周六": return 6;
            case "周日": return 7;
            default: return 1;
        }
    }
}