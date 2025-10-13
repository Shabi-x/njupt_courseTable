package com.example.njupt_coursetable.ui.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.njupt_coursetable.data.model.Course;
import com.example.njupt_coursetable.data.repository.CourseRepository;

import java.util.List;

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
     * 根据ID获取课程
     * @param courseId 课程ID
     * @return 课程的LiveData
     */
    public LiveData<Course> getCourseById(long courseId) {
        return courseRepository.getCourseById(courseId);
    }

    /**
     * 根据星期几获取课程
     * @param dayOfWeek 星期几，如"周一"
     * @return 对应星期几的课程列表的LiveData
     */
    public LiveData<List<Course>> getCoursesByDayOfWeek(String dayOfWeek) {
        return courseRepository.getCoursesByDayOfWeek(dayOfWeek);
    }

    /**
     * 根据课程名搜索课程
     * @param courseName 课程名
     * @return 匹配的课程列表的LiveData
     */
    public LiveData<List<Course>> searchCoursesByName(String courseName) {
        return courseRepository.searchCoursesByName(courseName);
    }

    /**
     * 根据老师名搜索课程
     * @param teacherName 老师名
     * @return 匹配的课程列表的LiveData
     */
    public LiveData<List<Course>> searchCoursesByTeacher(String teacherName) {
        return courseRepository.searchCoursesByTeacher(teacherName);
    }

    /**
     * 根据地点搜索课程
     * @param location 地点
     * @return 匹配的课程列表的LiveData
     */
    public LiveData<List<Course>> searchCoursesByLocation(String location) {
        return courseRepository.searchCoursesByLocation(location);
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