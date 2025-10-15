package com.example.njupt_coursetable.ui.viewmodel;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.njupt_coursetable.data.model.Course;
import com.example.njupt_coursetable.data.repository.CourseRepository;

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
     * 获取所有需要提醒的课程
     * @return 需要提醒的课程列表的LiveData
     */
    public LiveData<List<Course>> getCoursesWithReminder() {
        return courseRepository.getCoursesWithReminder();
    }

    /**
     * 插入课程
     * @param course 要插入的课程对象
     */
    public void insertCourse(Course course) {
        isLoading.setValue(true);
        
        courseRepository.insertCourse(course).observeForever(id -> {
            isLoading.setValue(false);
            if (id > 0) {
                Log.d(TAG, "Course inserted with ID: " + id);
                operationResult.setValue(true);
            } else {
                Log.e(TAG, "Failed to insert course");
                operationResult.setValue(false);
            }
        });
    }

    /**
     * 更新课程
     * @param course 要更新的课程对象
     */
    public void updateCourse(Course course) {
        isLoading.setValue(true);
        
        courseRepository.updateCourse(course).observeForever(rowsAffected -> {
            isLoading.setValue(false);
            if (rowsAffected > 0) {
                Log.d(TAG, "Course updated: " + course.getId());
                operationResult.setValue(true);
            } else {
                Log.e(TAG, "Failed to update course: " + course.getId());
                operationResult.setValue(false);
            }
        });
    }

    /**
     * 删除课程
     * @param courseId 要删除的课程ID
     */
    public void deleteCourse(long courseId) {
        isLoading.setValue(true);
        
        courseRepository.deleteCourse(courseId).observeForever(rowsAffected -> {
            isLoading.setValue(false);
            if (rowsAffected > 0) {
                Log.d(TAG, "Course deleted: " + courseId);
                operationResult.setValue(true);
            } else {
                Log.e(TAG, "Failed to delete course: " + courseId);
                operationResult.setValue(false);
            }
        });
    }

    /**
     * 添加课程提醒
     * @param courseId 课程ID
     */
    public void addCourseReminder(long courseId) {
        isLoading.setValue(true);
        
        courseRepository.addCourseReminder(courseId).observeForever(success -> {
            isLoading.setValue(false);
            if (success != null && success) {
                Log.d(TAG, "Course reminder added successfully: " + courseId);
                operationResult.setValue(true);
            } else {
                Log.e(TAG, "Failed to add course reminder: " + courseId);
                operationResult.setValue(false);
            }
        });
    }
    
    /**
     * 移除课程提醒
     * @param courseId 课程ID
     */
    public void removeCourseReminder(long courseId) {
        isLoading.setValue(true);
        
        courseRepository.removeCourseReminder(courseId).observeForever(success -> {
            isLoading.setValue(false);
            if (success != null && success) {
                Log.d(TAG, "Course reminder removed successfully: " + courseId);
                operationResult.setValue(true);
            } else {
                Log.e(TAG, "Failed to remove course reminder: " + courseId);
                operationResult.setValue(false);
            }
        });
    }
    
    /**
     * 从服务器同步所有课程数据
     */
    public void syncCoursesFromServer() {
        isLoading.setValue(true);
        
        courseRepository.syncCoursesFromServer().observeForever(success -> {
            isLoading.setValue(false);
            if (success != null && success) {
                Log.d(TAG, "Courses synced from server successfully");
                operationResult.setValue(true);
            } else {
                Log.e(TAG, "Failed to sync courses from server");
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