package com.example.njupt_coursetable.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;

import com.example.njupt_coursetable.data.model.Course;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CourseTableView extends View {
    // 常量定义
    private static final int WEEK_DAYS = 7; // 一周7天
    private static final int MAX_COURSE_SLOTS = 10; // 每天最多10节课
    private static final int TIME_SLOT_HEIGHT_DP = 80; // 每节课的高度（dp）
    private static final int WEEK_DAY_WIDTH_DP = 120; // 每天的宽度（dp）
    private static final int TIME_LABEL_WIDTH_DP = 80; // 时间标签的宽度（dp）
    private static final int HEADER_HEIGHT_DP = 60; // 头部高度（dp）
    private static final int BORDER_WIDTH = 1; // 边框宽度
    private static final int PADDING_DP = 4; // 课程内部 padding

    // 颜色定义
    private static final int BACKGROUND_COLOR = Color.parseColor("#FFFFFF");
    private static final int BORDER_COLOR = Color.parseColor("#E0E0E0");
    private static final int TEXT_COLOR = Color.parseColor("#333333");
    private static final int HEADER_BACKGROUND_COLOR = Color.parseColor("#F5F5F5");
    private static final int CURRENT_WEEK_BG_COLOR = Color.parseColor("#E8F5E8");
    private static final int TIME_LABEL_BACKGROUND_COLOR = Color.parseColor("#F9F9F9");

    // 课程颜色数组
    private static final int[] COURSE_COLORS = {
            Color.parseColor("#FFCDD2"), // 红色系
            Color.parseColor("#C8E6C9"), // 绿色系
            Color.parseColor("#BBDEFB"), // 蓝色系
            Color.parseColor("#E1BEE7"), // 紫色系
            Color.parseColor("#FFF9C4"), // 黄色系
            Color.parseColor("#B2DFDB"), // 青色系
            Color.parseColor("#FFECB3"), // 橙色系
            Color.parseColor("#D1C4E9")  // 深紫色系
    };

    // 时间字符串数组
    private static final String[] TIME_SLOT_STRINGS = {
            "08:00-08:45", "08:50-09:35", "09:50-10:35", "10:40-11:25",
            "11:30-12:15", "13:45-14:30", "14:35-15:20", "15:35-16:20",
            "16:25-17:10", "18:30-19:15"
    };

    // 星期标题数组
    private static final String[] WEEK_DAY_STRINGS = {"一", "二", "三", "四", "五", "六", "日"};

    // 日期数组
    private String[] dateStrings = new String[WEEK_DAYS];

    // 变量
    private int timeSlotHeight; // 每节课的实际高度（像素）
    private int weekDayWidth; // 每天的实际宽度（像素）
    private int timeLabelWidth; // 时间标签的实际宽度（像素）
    private int headerHeight; // 头部的实际高度（像素）
    private int padding; // 实际padding（像素）
    private List<Course> courses = new ArrayList<>();
    private int currentWeek = 6; // 当前周数
    private int selectedDayOfWeek = -1; // 选中的星期几
    private int selectedTimeSlot = -1; // 选中的时间段
    private OnCourseClickListener onCourseClickListener;
    private Map<String, Integer> courseNameToColorIndex = new HashMap<>();
    private Paint borderPaint;
    private TextPaint textPaint;
    private TextPaint smallTextPaint;
    private Paint backgroundPaint;
    private Paint coursePaint;
    private Rect textBounds = new Rect();

    public CourseTableView(Context context) {
        super(context);
        init();
    }

    public CourseTableView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CourseTableView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // 初始化颜色和画笔
        borderPaint = new Paint();
        borderPaint.setColor(BORDER_COLOR);
        borderPaint.setStrokeWidth(BORDER_WIDTH);
        borderPaint.setStyle(Paint.Style.STROKE);

        textPaint = new TextPaint();
        textPaint.setColor(TEXT_COLOR);
        textPaint.setTextSize(spToPx(14));
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setAntiAlias(true);

        smallTextPaint = new TextPaint();
        smallTextPaint.setColor(TEXT_COLOR);
        smallTextPaint.setTextSize(spToPx(12));
        smallTextPaint.setTextAlign(Paint.Align.CENTER);
        smallTextPaint.setAntiAlias(true);

        backgroundPaint = new Paint();
        backgroundPaint.setStyle(Paint.Style.FILL);

        coursePaint = new Paint();
        coursePaint.setStyle(Paint.Style.FILL);

        // 初始化尺寸
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);
        timeSlotHeight = dpToPx(TIME_SLOT_HEIGHT_DP);
        weekDayWidth = dpToPx(WEEK_DAY_WIDTH_DP);
        timeLabelWidth = dpToPx(TIME_LABEL_WIDTH_DP);
        headerHeight = dpToPx(HEADER_HEIGHT_DP);
        padding = dpToPx(PADDING_DP);

        // 设置默认日期
        setDefaultDates();
    }

    private void setDefaultDates() {
        // 这里可以根据实际需求设置日期
        // 为了演示，设置一个固定的日期范围
        dateStrings = new String[]{
                "3/24", "3/25", "3/26", "3/27", "3/28", "3/29", "3/30"
        };
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = timeLabelWidth + weekDayWidth * WEEK_DAYS;
        int height = headerHeight + timeSlotHeight * MAX_COURSE_SLOTS;
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 绘制背景
        canvas.drawColor(BACKGROUND_COLOR);

        // 绘制头部
        drawHeader(canvas);

        // 绘制时间标签列
        drawTimeLabels(canvas);

        // 绘制课程格子
        drawCourseGrid(canvas);

        // 绘制课程
        drawCourses(canvas);
    }

    private void drawHeader(Canvas canvas) {
        // 绘制头部背景
        backgroundPaint.setColor(HEADER_BACKGROUND_COLOR);
        canvas.drawRect(0, 0, getWidth(), headerHeight, backgroundPaint);

        // 绘制当前周数
        backgroundPaint.setColor(CURRENT_WEEK_BG_COLOR);
        int weekTextWidth = 200;
        int weekTextHeight = headerHeight;
        canvas.drawRect(timeLabelWidth, 0, timeLabelWidth + weekTextWidth, weekTextHeight, backgroundPaint);
        drawText(canvas, "第" + currentWeek + "周", timeLabelWidth + weekTextWidth / 2, headerHeight / 2, textPaint);

        // 绘制星期标题
        for (int i = 0; i < WEEK_DAYS; i++) {
            int left = timeLabelWidth + i * weekDayWidth;
            int top = 0;
            int right = left + weekDayWidth;
            int bottom = headerHeight;

            // 绘制边框
            canvas.drawRect(left, top, right, bottom, borderPaint);

            // 绘制星期文字
            drawText(canvas, WEEK_DAY_STRINGS[i], (left + right) / 2, headerHeight / 2 - 10, textPaint);
            drawText(canvas, dateStrings[i], (left + right) / 2, headerHeight / 2 + 15, smallTextPaint);
        }
    }

    private void drawTimeLabels(Canvas canvas) {
        for (int i = 0; i < MAX_COURSE_SLOTS; i++) {
            int top = headerHeight + i * timeSlotHeight;
            int bottom = top + timeSlotHeight;

            // 绘制时间标签背景
            backgroundPaint.setColor(TIME_LABEL_BACKGROUND_COLOR);
            canvas.drawRect(0, top, timeLabelWidth, bottom, backgroundPaint);

            // 绘制时间文字
            String timeSlotString = (i + 1) + "\n" + TIME_SLOT_STRINGS[i];
            int textX = timeLabelWidth / 2;
            int textY = top + timeSlotHeight / 2;
            drawMultilineText(canvas, timeSlotString, textX, textY);

            // 绘制右边框
            canvas.drawLine(timeLabelWidth, top, timeLabelWidth, bottom, borderPaint);
        }

        // 绘制底部横线
        canvas.drawLine(0, getHeight(), getWidth(), getHeight(), borderPaint);
    }

    private void drawCourseGrid(Canvas canvas) {
        for (int i = 0; i < WEEK_DAYS; i++) {
            for (int j = 0; j < MAX_COURSE_SLOTS; j++) {
                int left = timeLabelWidth + i * weekDayWidth;
                int top = headerHeight + j * timeSlotHeight;
                int right = left + weekDayWidth;
                int bottom = top + timeSlotHeight;

                // 绘制边框
                canvas.drawRect(left, top, right, bottom, borderPaint);
            }
        }
    }

    private void drawCourses(Canvas canvas) {
        for (Course course : courses) {
            // 获取课程的位置信息
            int dayOfWeek = getDayOfWeekIndex(course.getDayOfWeek());
            if (dayOfWeek < 0 || dayOfWeek >= WEEK_DAYS) continue;

            int[] timeSlots = getTimeSlotRange(course.getTimeSlot());
            if (timeSlots == null || timeSlots.length != 2) continue;

            int startSlot = timeSlots[0] - 1; // 转换为0-based索引
            int endSlot = timeSlots[1] - 1;

            if (startSlot < 0 || endSlot >= MAX_COURSE_SLOTS) continue;

            // 计算课程的绘制区域
            int left = timeLabelWidth + dayOfWeek * weekDayWidth + padding;
            int top = headerHeight + startSlot * timeSlotHeight + padding;
            int right = left + weekDayWidth - 2 * padding;
            int bottom = headerHeight + (endSlot + 1) * timeSlotHeight - padding;

            // 设置课程颜色
            int colorIndex = getColorIndexForCourse(course.getCourseName());
            coursePaint.setColor(COURSE_COLORS[colorIndex]);

            // 绘制课程背景
            canvas.drawRoundRect(left, top, right, bottom, 8, 8, coursePaint);

            // 绘制课程边框
            borderPaint.setColor(getDarkerColor(COURSE_COLORS[colorIndex]));
            canvas.drawRoundRect(left, top, right, bottom, 8, 8, borderPaint);

            // 绘制课程信息
            float centerX = (left + right) / 2;
            float centerY = (top + bottom) / 2;
            drawCourseInfo(canvas, course, centerX, centerY, bottom - top);
        }
    }

    private void drawCourseInfo(Canvas canvas, Course course, float centerX, float centerY, float height) {
        // 绘制课程名称
        float textSize = spToPx(14);
        if (height < 100) {
            textSize = spToPx(12);
        }
        textPaint.setTextSize(textSize);
        textPaint.setColor(Color.BLACK);

        String[] lines = getCourseInfoLines(course);
        float lineHeight = textSize * 1.5f;
        float totalTextHeight = lines.length * lineHeight;
        float startY = centerY - totalTextHeight / 2 + textSize;

        for (int i = 0; i < lines.length; i++) {
            float y = startY + i * lineHeight;
            drawText(canvas, lines[i], centerX, y, textPaint);
        }
    }

    private String[] getCourseInfoLines(Course course) {
        List<String> lines = new ArrayList<>();
        lines.add(course.getCourseName());
        if (course.getLocation() != null && !course.getLocation().isEmpty()) {
            lines.add("@" + course.getLocation());
        }
        return lines.toArray(new String[0]);
    }

    private int getDayOfWeekIndex(String dayOfWeek) {
        if (dayOfWeek == null) return -1;
        for (int i = 0; i < WEEK_DAY_STRINGS.length; i++) {
            if (dayOfWeek.contains(WEEK_DAY_STRINGS[i])) {
                return i;
            }
        }
        return -1;
    }

    private int[] getTimeSlotRange(String timeSlot) {
        if (timeSlot == null) return null;
        try {
            // 处理格式如"1-2节"的时间
            Pattern pattern = Pattern.compile("(\\d+)-(\\d+)节");
            Matcher matcher = pattern.matcher(timeSlot);
            if (matcher.find()) {
                int start = Integer.parseInt(matcher.group(1));
                int end = Integer.parseInt(matcher.group(2));
                return new int[]{start, end};
            }
            // 处理格式如"第1节"的时间
            pattern = Pattern.compile("第(\\d+)节");
            matcher = pattern.matcher(timeSlot);
            if (matcher.find()) {
                int slot = Integer.parseInt(matcher.group(1));
                return new int[]{slot, slot};
            }
            // 处理格式如"1"的时间
            if (timeSlot.matches("\\d+")) {
                int slot = Integer.parseInt(timeSlot);
                return new int[]{slot, slot};
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private int getColorIndexForCourse(String courseName) {
        if (courseName == null || courseName.isEmpty()) {
            return 0;
        }
        if (!courseNameToColorIndex.containsKey(courseName)) {
            int colorIndex = courseNameToColorIndex.size() % COURSE_COLORS.length;
            courseNameToColorIndex.put(courseName, colorIndex);
        }
        return courseNameToColorIndex.get(courseName);
    }

    private int getDarkerColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.8f; // 降低亮度
        return Color.HSVToColor(hsv);
    }

    private void drawText(Canvas canvas, String text, float x, float y, TextPaint paint) {
        paint.getTextBounds(text, 0, text.length(), textBounds);
        float baseline = y - (textBounds.top + textBounds.bottom) / 2;
        canvas.drawText(text, x, baseline, paint);
    }

    private void drawMultilineText(Canvas canvas, String text, int x, int y) {
        String[] lines = text.split("\\n");
        if (lines.length == 0) return;

        float totalHeight = lines.length * textPaint.getTextSize() * 1.2f;
        float startY = y - totalHeight / 2 + textPaint.getTextSize();

        for (int i = 0; i < lines.length; i++) {
            float currentY = startY + i * textPaint.getTextSize() * 1.2f;
            drawText(canvas, lines[i], x, currentY, textPaint);
        }
    }

    private int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    private int spToPx(int sp) {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        return Math.round(sp * displayMetrics.scaledDensity);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            // 检查是否点击了课程区域
            for (Course course : courses) {
                int dayOfWeek = getDayOfWeekIndex(course.getDayOfWeek());
                if (dayOfWeek < 0 || dayOfWeek >= WEEK_DAYS) continue;

                int[] timeSlots = getTimeSlotRange(course.getTimeSlot());
                if (timeSlots == null || timeSlots.length != 2) continue;

                int startSlot = timeSlots[0] - 1;
                int endSlot = timeSlots[1] - 1;

                if (startSlot < 0 || endSlot >= MAX_COURSE_SLOTS) continue;

                int left = timeLabelWidth + dayOfWeek * weekDayWidth + padding;
                int top = headerHeight + startSlot * timeSlotHeight + padding;
                int right = left + weekDayWidth - 2 * padding;
                int bottom = headerHeight + (endSlot + 1) * timeSlotHeight - padding;

                if (x >= left && x <= right && y >= top && y <= bottom) {
                    // 点击了课程
                    if (onCourseClickListener != null) {
                        onCourseClickListener.onCourseClick(course);
                    }
                    return true;
                }
            }
        }

        return super.onTouchEvent(event);
    }

    // 设置课程列表
    public void setCourses(List<Course> courses) {
        this.courses = courses != null ? courses : new ArrayList<>();
        courseNameToColorIndex.clear();
        invalidate();
    }

    // 设置当前周数
    public void setCurrentWeek(int week) {
        this.currentWeek = week;
        invalidate();
    }

    // 设置日期数组
    public void setDateStrings(String[] dates) {
        if (dates != null && dates.length == WEEK_DAYS) {
            this.dateStrings = dates;
            invalidate();
        }
    }

    // 设置课程点击监听器
    public void setOnCourseClickListener(OnCourseClickListener listener) {
        this.onCourseClickListener = listener;
    }

    // 课程点击监听器接口
    public interface OnCourseClickListener {
        void onCourseClick(Course course);
    }
}