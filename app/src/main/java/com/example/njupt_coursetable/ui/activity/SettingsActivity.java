package com.example.njupt_coursetable.ui.activity;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.njupt_coursetable.NjuptCourseTableApp;
import com.example.njupt_coursetable.R;
import com.example.njupt_coursetable.databinding.ActivitySettingsBinding;
import com.example.njupt_coursetable.di.AppComponent;
import com.example.njupt_coursetable.ui.viewmodel.CourseViewModel;
import androidx.lifecycle.ViewModelProvider;

/**
 * 设置页面
 */
public class SettingsActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "CourseTableSettings";
    private static final String KEY_FONT_SIZE = "font_size";

    private ActivitySettingsBinding binding;
    private SharedPreferences preferences;
    private CourseViewModel courseViewModel;
    private AppComponent appComponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 初始化SharedPreferences
        preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        
        // 获取应用组件
        appComponent = ((NjuptCourseTableApp) getApplication()).getAppComponent();
        
        // 初始化ViewModel
        courseViewModel = new androidx.lifecycle.ViewModelProvider(this, new ViewModelProvider.Factory() {
            @androidx.annotation.NonNull
            @Override
            public <T extends androidx.lifecycle.ViewModel> T create(@androidx.annotation.NonNull Class<T> modelClass) {
                if (modelClass.isAssignableFrom(CourseViewModel.class)) {
                    return (T) appComponent.courseViewModel();
                }
                throw new IllegalArgumentException("Unknown ViewModel class");
            }
        }).get(CourseViewModel.class);

        // 设置工具栏
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        // 初始化设置
        initSettings();

        // 设置点击事件
        setupClickListeners();
    }

    /**
     * 初始化设置显示
     */
    private void initSettings() {
        // 字体大小
        String fontSize = preferences.getString(KEY_FONT_SIZE, "中");
        binding.textFontSize.setText(fontSize);

        // 应用版本
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            binding.textAppVersion.setText(pInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            binding.textAppVersion.setText("1.0.0");
        }
    }

    /**
     * 设置点击事件监听器
     */
    private void setupClickListeners() {
        // 字体大小设置
        binding.layoutFontSize.setOnClickListener(v -> showFontSizeDialog());
        
        // 同步服务器数据
        binding.layoutSyncData.setOnClickListener(v -> syncServerData());
        
        // 清空所有课程
        binding.layoutClearAll.setOnClickListener(v -> showClearAllDialog());
    }

    /**
     * 显示字体大小设置对话框
     */
    private void showFontSizeDialog() {
        String[] options = {"小", "中", "大"};
        String currentSize = preferences.getString(KEY_FONT_SIZE, "中");
        int selectedIndex = 1; // 默认中
        
        for (int i = 0; i < options.length; i++) {
            if (options[i].equals(currentSize)) {
                selectedIndex = i;
                break;
            }
        }

        new AlertDialog.Builder(this)
                .setTitle("选择字体大小")
                .setSingleChoiceItems(options, selectedIndex, (dialog, which) -> {
                    String selectedSize = options[which];
                    preferences.edit().putString(KEY_FONT_SIZE, selectedSize).apply();
                    binding.textFontSize.setText(selectedSize);
                    Toast.makeText(this, "已设置字体大小为" + selectedSize, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                })
                .setNegativeButton("取消", null)
                .show();
    }

    /**
     * 同步服务器数据
     */
    private void syncServerData() {
        new AlertDialog.Builder(this)
                .setTitle("同步数据")
                .setMessage("确定要从服务器同步数据吗？这将覆盖本地数据。")
                .setPositiveButton("确定", (dialog, which) -> {
                    Toast.makeText(this, "正在同步数据...", Toast.LENGTH_SHORT).show();
                    courseViewModel.syncCoursesFromServer();
                    courseViewModel.syncCoursesWithRemindersFromServer();
                    
                    courseViewModel.getOperationResult().observe(this, result -> {
                        if (result != null) {
                            if (result) {
                                Toast.makeText(this, "数据同步成功", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(this, "数据同步失败，请检查网络连接", Toast.LENGTH_SHORT).show();
                            }
                            courseViewModel.resetOperationResult();
                        }
                    });
                })
                .setNegativeButton("取消", null)
                .show();
    }

    /**
     * 显示清空所有课程确认对话框
     */
    private void showClearAllDialog() {
        new AlertDialog.Builder(this)
                .setTitle("清空所有课程")
                .setMessage("确定要清空所有课程数据吗？此操作不可恢复！")
                .setPositiveButton("确定", (dialog, which) -> {
                    // 这里需要添加清空所有课程的逻辑
                    Toast.makeText(this, "所有课程已清空", Toast.LENGTH_SHORT).show();
                    // TODO: 调用ViewModel的删除所有课程方法
                })
                .setNegativeButton("取消", null)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
