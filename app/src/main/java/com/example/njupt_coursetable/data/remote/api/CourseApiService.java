package com.example.njupt_coursetable.data.remote.api;

import com.example.njupt_coursetable.data.model.Course;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * 课程网络服务接口
 * 定义与服务器交互的API端点
 */
public interface CourseApiService {

    /**
     * 获取所有课程
     * @return 所有课程列表的Call对象
     */
    @GET("api/courses")
    Call<List<Course>> getAllCourses();

    /**
     * 根据周数查询课程
     * @param weekNumber 周数，如"1"、"2"等
     * @return 对应周数的课程列表的Call对象
     */
    @GET("api/courses/week/{weekNumber}")
    Call<List<Course>> getCoursesByWeek(@Path("weekNumber") String weekNumber);
    
    /**
     * 获取所有需要提醒的课程
     * @return 需要提醒的课程列表的Call对象
     */
    @GET("api/courses/reminders")
    Call<List<Course>> getCoursesWithReminders();
    
    /**
     * 更新课程的提醒状态
     * @param id 课程ID
     * @param shouldReminder 是否需要提醒
     * @return 更新后的课程对象的Call对象
     */
    @PUT("api/courses/{id}/reminder")
    Call<Course> updateCourseReminderStatus(@Path("id") long id, @Query("shouldReminder") boolean shouldReminder);

    /**
     * 创建新课程
     * @param course 课程对象
     * @return 创建的课程对象的Call对象
     */
    @POST("api/courses")
    Call<Course> createCourse(@Body Course course);

    /**
     * 更新课程
     * @param id 课程ID
     * @param course 更新的课程对象
     * @return 更新后的课程对象的Call对象
     */
    @PUT("api/courses/{id}")
    Call<Course> updateCourse(@Path("id") long id, @Body Course course);

    /**
     * 删除课程
     * @param id 课程ID
     * @return 空的Call对象
     */
    @DELETE("api/courses/{id}")
    Call<Void> deleteCourse(@Path("id") long id);
}