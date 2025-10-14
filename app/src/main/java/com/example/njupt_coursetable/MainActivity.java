package com.example.njupt_coursetable;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.FrameLayout;
import com.example.njupt_coursetable.ui.view.CourseTableView;
import com.example.njupt_coursetable.ui.view.HalfPanel;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // 获取CourseTableView和HalfPanel的引用
        CourseTableView courseTableView = findViewById(R.id.course_table_view);
        HalfPanel halfPanel = findViewById(R.id.half_panel);
        
        // 将HalfPanel设置给CourseTableView
        courseTableView.setHalfPanel(halfPanel);
    }
}