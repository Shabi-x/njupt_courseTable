package com.example.njupt_coursetable.ui.viewmodel;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.njupt_coursetable.data.model.Course;
import com.example.njupt_coursetable.data.repository.CourseRepository;
import com.example.njupt_coursetable.data.repository.ReminderRepository;
import com.example.njupt_coursetable.data.model.Reminder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.FutureTask;

/**
 * 课程视图模型类
 * 负责处理与课程相关的业务逻辑和数据操作
 */
public class CourseViewModel extends AndroidViewModel {

    private static final String TAG = "CourseViewModel";
    
    /**
     * 课程仓库实例
     */
    private final CourseRepository courseRepository;
    private final ReminderRepository reminderRepository;
    
    /**
     * 所有课程的LiveData
     */
    private final LiveData<List<Course>> allCourses;
    
    /**
     * 操作结果的LiveData
     */
    private final MutableLiveData<Boolean> operationResult = new MutableLiveData<>();
    
    /**
     * 加载状态的LiveData
     */
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    /**
     * 构造函数
     * @param application 应用实例
     * @param courseRepository 课程仓库
     */
    public CourseViewModel(@NonNull Application application, CourseRepository courseRepository) {
        super(application);
        this.courseRepository = courseRepository;
        this.reminderRepository = ReminderRepository.getInstance(application);
        this.allCourses = courseRepository.getAllCourses();
    }

    /**
     * 获取所有课程
     * @return 所有课程的LiveData
     */
    public LiveData<List<Course>> getAllCourses() {
        return allCourses;
    }
    
    /**
     * 同步获取所有课程（用于需要立即获取课程列表的场景）
     * @return 课程列表
     */
    public List<Course> getAllCoursesSync() {
        try {
            // 使用FutureTask在后台线程中执行数据库查询
            FutureTask<List<Course>> futureTask = new FutureTask<>(() -> {
                try {
                    return courseRepository.getAllCoursesSync();
                } catch (Exception e) {
                    Log.e(TAG, "Error getting all courses synchronously", e);
                    return Collections.emptyList();
                }
            });
            
            AsyncTask.execute(futureTask);
            return futureTask.get();
        } catch (Exception e) {
            Log.e(TAG, "Error executing async task", e);
            return Collections.emptyList();
        }
    }
    
    /**
     * 根据周数获取课程
     * @param weekNumber 周数，如"1"
     * @return 包含该周数的课程列表的LiveData
     */
    public LiveData<List<Course>> getCoursesByWeekNumber(String weekNumber) {
        return courseRepository.getCoursesByWeekNumber(weekNumber);
    }

    /**
     * 从服务器按周同步课程并返回结果（仅内存，不写入本地）
     */
    public LiveData<List<Course>> syncCoursesByWeekFromServer(String weekNumber) {
        return courseRepository.syncCoursesByWeekFromServer(weekNumber);
    }
    
    /**
     * 获取所有需要提醒的课程
     * @return 需要提醒的课程列表的LiveData
     */
    public LiveData<List<Course>> getCoursesWithReminder() {
        return courseRepository.getCoursesWithReminder();
    }

    // reminders
    public LiveData<List<Reminder>> getUpcomingReminders() {
        return reminderRepository.getUpcomingReminders();
    }

    public LiveData<Boolean> createReminder(long courseId, String courseDate, String startTime) {
        return reminderRepository.createReminder(courseId, courseDate, startTime);
    }

    public LiveData<Boolean> deleteReminderByCourseAndDate(long courseId, String courseDate) {
        return reminderRepository.deleteReminderByCourseAndDate(courseId, courseDate);
    }

    /**
     * 插入课程
     * @param course 要插入的课程对象
     * @return LiveData containing the inserted course ID
     */
    public LiveData<Long> insertCourse(Course course) {
        isLoading.setValue(true);
        return courseRepository.insertCourse(course);
    }

    /**
     * 更新课程
     * @param course 要更新的课程对象
     * @return LiveData containing rows affected
     */
    public LiveData<Integer> updateCourse(Course course) {
        isLoading.setValue(true);
        return courseRepository.updateCourse(course);
    }

    /**
     * 删除课程
     * @param courseId 要删除的课程ID
     * @return LiveData containing rows affected
     */
    public LiveData<Integer> deleteCourse(long courseId) {
        isLoading.setValue(true);
        return courseRepository.deleteCourse(courseId);
    }

    /**
     * 添加课程提醒
     * @param courseId 课程ID
     * @return LiveData containing success status
     */
    public LiveData<Boolean> addCourseReminder(long courseId) {
        isLoading.setValue(true);
        return courseRepository.addCourseReminder(courseId);
    }
    
    /**
     * 移除课程提醒
     * @param courseId 课程ID
     * @return LiveData containing success status
     */
    public LiveData<Boolean> removeCourseReminder(long courseId) {
        isLoading.setValue(true);
        return courseRepository.removeCourseReminder(courseId);
    }
    
    /**
     * 从服务器同步所有课程数据
     * @return LiveData containing success status
     */
    public LiveData<Boolean> syncCoursesFromServer() {
        isLoading.setValue(true);
        return courseRepository.syncCoursesFromServer();
    }
    
    /**
     * 从服务器同步所有课程数据（新增）
     */
    public void syncAllCoursesFromServer() {
        isLoading.setValue(true);
        
        courseRepository.syncAllCoursesFromServer().observeForever(success -> {
            isLoading.setValue(false);
            if (success != null && success) {
                Log.d(TAG, "All courses synced from server successfully");
                operationResult.setValue(true);
            } else {
                Log.e(TAG, "Failed to sync all courses from server");
                operationResult.setValue(false);
            }
        });
    }
    
    /**
     * 从服务器同步需要提醒的课程数据
     * @return 需要提醒的课程列表的LiveData
     */
    public LiveData<List<Course>> syncCoursesWithRemindersFromServer() {
        isLoading.setValue(true);
        
        MutableLiveData<List<Course>> result = new MutableLiveData<>();
        
        courseRepository.syncCoursesWithRemindersFromServer().observeForever(courses -> {
            isLoading.setValue(false);
            if (courses != null) {
                Log.d(TAG, "Courses with reminders synced from server successfully");
                result.setValue(courses);
            } else {
                Log.e(TAG, "Failed to sync courses with reminders from server");
                result.setValue(new ArrayList<>());
            }
        });
        
        return result;
    }

    /**
     * 获取操作结果
     * @return 操作结果的LiveData
     */
    public LiveData<Boolean> getOperationResult() {
        return operationResult;
    }

    /**
     * 获取加载状态
     * @return 加载状态的LiveData
     */
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    /**
     * 重置操作结果
     */
    public void resetOperationResult() {
        operationResult.setValue(null);
    }
}