package com.example.njupt_coursetable.data.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.njupt_coursetable.data.local.AppDatabase;
import com.example.njupt_coursetable.data.local.dao.CourseDao;
import com.example.njupt_coursetable.data.model.Course;
import com.example.njupt_coursetable.data.remote.RetrofitClient;
import com.example.njupt_coursetable.data.remote.api.CourseApiService;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 课程仓库类
 * 负责协调本地数据源和远程数据源，提供统一的数据访问接口
 */
public class CourseRepository {
    private static final String TAG = "CourseRepository";
    
    /**
     * 单例实例
     */
    private static CourseRepository instance;
    
    /**
     * 本地数据访问对象
     */
    private final CourseDao courseDao;
    
    /**
     * 远程API服务
     */
    private final CourseApiService courseApiService;
    
    /**
     * 线程池，用于执行数据库操作
     */
    private final ExecutorService executorService;
    
    /**
     * 私有构造函数
     * @param context 应用上下文
     */
    private CourseRepository(Context context) {
        AppDatabase database = AppDatabase.getInstance(context);
        courseDao = database.courseDao();
        courseApiService = RetrofitClient.getCourseApiService(context);
        executorService = Executors.newFixedThreadPool(4);
    }
    
    /**
     * 获取CourseRepository单例实例
     * @param context 应用上下文
     * @return CourseRepository实例
     */
    public static synchronized CourseRepository getInstance(Context context) {
        if (instance == null) {
            instance = new CourseRepository(context.getApplicationContext());
        }
        return instance;
    }
    
    /**
     * 获取所有课程
     * @return 所有课程的LiveData列表
     */
    public LiveData<List<Course>> getAllCourses() {
        // 首先从本地数据库获取数据
        return courseDao.getAllCourses();
    }
    
    /**
     * 根据ID获取课程
     * @param courseId 课程ID
     * @return 课程的LiveData对象
     */
    public LiveData<Course> getCourseById(long courseId) {
        return courseDao.getCourseById(courseId);
    }
    
    /**
     * 根据星期几获取课程
     * @param dayOfWeek 星期几，如"周一"
     * @return 对应星期几的课程列表
     */
    public LiveData<List<Course>> getCoursesByDayOfWeek(String dayOfWeek) {
        return courseDao.getCoursesByDayOfWeek(dayOfWeek);
    }
    
    /**
     * 根据课程名搜索课程
     * @param courseName 课程名
     * @return 匹配的课程列表
     */
    public LiveData<List<Course>> searchCoursesByName(String courseName) {
        return courseDao.searchCoursesByName(courseName);
    }
    
    /**
     * 根据老师名搜索课程
     * @param teacherName 老师名
     * @return 匹配的课程列表
     */
    public LiveData<List<Course>> searchCoursesByTeacher(String teacherName) {
        return courseDao.searchCoursesByTeacher(teacherName);
    }
    
    /**
     * 根据地点搜索课程
     * @param location 地点
     * @return 匹配的课程列表
     */
    public LiveData<List<Course>> searchCoursesByLocation(String location) {
        return courseDao.searchCoursesByLocation(location);
    }
    
    /**
     * 插入课程
     * @param course 要插入的课程对象
     * @return 插入结果的LiveData
     */
    public LiveData<Long> insertCourse(Course course) {
        MutableLiveData<Long> result = new MutableLiveData<>();
        
        // 先在本地数据库插入
        executorService.execute(() -> {
            long id = courseDao.insert(course);
            result.postValue(id);
            
            // 然后同步到服务器
            courseApiService.createCourse(course).enqueue(new Callback<Course>() {
                @Override
                public void onResponse(Call<Course> call, Response<Course> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Log.d(TAG, "Course created on server: " + response.body().getId());
                    } else {
                        Log.e(TAG, "Failed to create course on server: " + response.message());
                    }
                }
                
                @Override
                public void onFailure(Call<Course> call, Throwable t) {
                    Log.e(TAG, "Error creating course on server", t);
                }
            });
        });
        
        return result;
    }
    
    /**
     * 更新课程
     * @param course 要更新的课程对象
     * @return 更新结果的LiveData
     */
    public LiveData<Integer> updateCourse(Course course) {
        MutableLiveData<Integer> result = new MutableLiveData<>();
        
        // 先更新本地数据库
        executorService.execute(() -> {
            int rowsAffected = courseDao.update(course);
            result.postValue(rowsAffected);
            
            // 然后同步到服务器
            courseApiService.updateCourse(course.getId(), course).enqueue(new Callback<Course>() {
                @Override
                public void onResponse(Call<Course> call, Response<Course> response) {
                    if (response.isSuccessful()) {
                        Log.d(TAG, "Course updated on server: " + course.getId());
                    } else {
                        Log.e(TAG, "Failed to update course on server: " + response.message());
                    }
                }
                
                @Override
                public void onFailure(Call<Course> call, Throwable t) {
                    Log.e(TAG, "Error updating course on server", t);
                }
            });
        });
        
        return result;
    }
    
    /**
     * 删除课程
     * @param courseId 要删除的课程ID
     * @return 删除结果的LiveData
     */
    public LiveData<Integer> deleteCourse(long courseId) {
        MutableLiveData<Integer> result = new MutableLiveData<>();
        
        executorService.execute(() -> {
            // 先从本地数据库获取课程信息
            Course course = courseDao.getCourseByIdSync(courseId);
            
            if (course != null) {
                // 删除本地数据库中的课程
                int rowsAffected = courseDao.deleteById(courseId);
                result.postValue(rowsAffected);
                
                // 然后从服务器删除
                courseApiService.deleteCourse(courseId).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Log.d(TAG, "Course deleted from server: " + courseId);
                        } else {
                            Log.e(TAG, "Failed to delete course from server: " + response.message());
                        }
                    }
                    
                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.e(TAG, "Error deleting course from server", t);
                    }
                });
            } else {
                result.postValue(0);
            }
        });
        
        return result;
    }
    
    /**
     * 从服务器同步所有课程数据
     * @return 同步结果的LiveData
     */
    public LiveData<Boolean> syncCoursesFromServer() {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        
        courseApiService.getAllCourses().enqueue(new Callback<List<Course>>() {
            @Override
            public void onResponse(Call<List<Course>> call, Response<List<Course>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Course> courses = response.body();
                    
                    executorService.execute(() -> {
                        // 先清空本地数据库
                        courseDao.deleteAllCourses();
                        
                        // 然后插入从服务器获取的数据
                        for (Course course : courses) {
                            courseDao.insert(course);
                        }
                        
                        result.postValue(true);
                    });
                } else {
                    Log.e(TAG, "Failed to sync courses from server: " + response.message());
                    result.postValue(false);
                }
            }
            
            @Override
            public void onFailure(Call<List<Course>> call, Throwable t) {
                Log.e(TAG, "Error syncing courses from server", t);
                result.postValue(false);
            }
        });
        
        return result;
    }
}