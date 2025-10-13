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

/**
 * 课程网络服务接口
 * 定义与服务器交互的API端点
 */
public interface CourseApiService {

    /**
     * 获取所有课程
     * @return 课程列表的Call对象
     */
    @GET("api/courses")
    Call<List<Course>> getAllCourses();

    /**
     * 根据ID获取课程
     * @param id 课程ID
     * @return 课程对象的Call对象
     */
    @GET("api/courses/{id}")
    Call<Course> getCourseById(@Path("id") long id);

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

    /**
     * 根据星期几获取课程
     * @param dayOfWeek 星期几，如"周一"
     * @return 对应星期几的课程列表的Call对象
     */
    @GET("api/courses/day/{dayOfWeek}")
    Call<List<Course>> getCoursesByDayOfWeek(@Path("dayOfWeek") String dayOfWeek);

    /**
     * 根据课程名搜索课程
     * @param courseName 课程名
     * @return 匹配的课程列表的Call对象
     */
    @GET("api/courses/search/name/{courseName}")
    Call<List<Course>> searchCoursesByName(@Path("courseName") String courseName);

    /**
     * 根据老师名搜索课程
     * @param teacherName 老师名
     * @return 匹配的课程列表的Call对象
     */
    @GET("api/courses/search/teacher/{teacherName}")
    Call<List<Course>> searchCoursesByTeacher(@Path("teacherName") String teacherName);

    /**
     * 根据地点搜索课程
     * @param location 地点
     * @return 匹配的课程列表的Call对象
     */
    @GET("api/courses/search/location/{location}")
    Call<List<Course>> searchCoursesByLocation(@Path("location") String location);
}