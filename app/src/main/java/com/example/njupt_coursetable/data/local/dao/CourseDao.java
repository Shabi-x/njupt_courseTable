package com.example.njupt_coursetable.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import com.example.njupt_coursetable.data.model.Course;

import java.util.List;

/**
 * 课程数据访问对象接口
 * 定义对课程表的所有数据库操作
 */
@Dao
public interface CourseDao {

    /**
     * 插入一个新课程
     * @param course 要插入的课程对象
     * @return 新插入课程的行ID
     */
    @Insert
    long insert(Course course);

    /**
     * 插入多个课程
     * @param courses 要插入的课程列表
     * @return 新插入课程的行ID数组
     */
    @Insert
    long[] insertAll(Course... courses);

    /**
     * 更新课程信息
     * @param course 要更新的课程对象
     * @return 受影响的行数
     */
    @Update
    int update(Course course);

    /**
     * 删除课程
     * @param course 要删除的课程对象
     * @return 受影响的行数
     */
    @Delete
    int delete(Course course);

    /**
     * 根据ID删除课程
     * @param courseId 要删除的课程ID
     * @return 受影响的行数
     */
    @Query("DELETE FROM courses WHERE id = :courseId")
    int deleteById(long courseId);

    /**
     * 删除所有课程
     * @return 受影响的行数
     */
    @Query("DELETE FROM courses")
    int deleteAllCourses();

    /**
     * 获取所有课程
     * @return 所有课程的LiveData列表，用于观察数据变化
     */
    @Query("SELECT * FROM courses ORDER BY dayOfWeek, timeSlot")
    LiveData<List<Course>> getAllCourses();

    /**
     * 获取所有课程（非LiveData）
     * @return 所有课程的列表
     */
    @Query("SELECT * FROM courses ORDER BY dayOfWeek, timeSlot")
    List<Course> getAllCoursesSync();

    /**
     * 根据ID获取课程
     * @param courseId 课程ID
     * @return 对应的课程对象
     */
    @Query("SELECT * FROM courses WHERE id = :courseId")
    LiveData<Course> getCourseById(long courseId);

    /**
     * 根据ID获取课程（非LiveData）
     * @param courseId 课程ID
     * @return 对应的课程对象
     */
    @Query("SELECT * FROM courses WHERE id = :courseId")
    Course getCourseByIdSync(long courseId);

    /**
     * 根据星期几获取课程
     * @param dayOfWeek 星期几，如"周一"
     * @return 对应星期几的课程列表
     */
    @Query("SELECT * FROM courses WHERE dayOfWeek = :dayOfWeek ORDER BY timeSlot")
    LiveData<List<Course>> getCoursesByDayOfWeek(String dayOfWeek);

    /**
     * 根据课程名搜索课程
     * @param courseName 课程名（支持模糊查询）
     * @return 匹配的课程列表
     */
    @Query("SELECT * FROM courses WHERE courseName LIKE '%' || :courseName || '%' ORDER BY dayOfWeek, timeSlot")
    LiveData<List<Course>> searchCoursesByName(String courseName);

    /**
     * 根据老师名搜索课程
     * @param teacherName 老师名（支持模糊查询）
     * @return 匹配的课程列表
     */
    @Query("SELECT * FROM courses WHERE teacherName LIKE '%' || :teacherName || '%' ORDER BY dayOfWeek, timeSlot")
    LiveData<List<Course>> searchCoursesByTeacher(String teacherName);

    /**
     * 根据地点搜索课程
     * @param location 地点（支持模糊查询）
     * @return 匹配的课程列表
     */
    @Query("SELECT * FROM courses WHERE location LIKE '%' || :location || '%' ORDER BY dayOfWeek, timeSlot")
    LiveData<List<Course>> searchCoursesByLocation(String location);
    
    /**
     * 根据周类型获取课程
     * @param weekType 周类型，如"单周"、"双周"、"全周"
     * @return 对应周类型的课程列表
     */
    @Query("SELECT * FROM courses WHERE weekType = :weekType ORDER BY dayOfWeek, timeSlot")
    LiveData<List<Course>> getCoursesByWeekType(String weekType);
    
    /**
     * 根据星期几和周类型获取课程
     * @param dayOfWeek 星期几，如"周一"
     * @param weekType 周类型，如"单周"、"双周"、"全周"
     * @return 对应星期几和周类型的课程列表
     */
    @Query("SELECT * FROM courses WHERE dayOfWeek = :dayOfWeek AND weekType = :weekType ORDER BY timeSlot")
    LiveData<List<Course>> getCoursesByDayAndWeekType(String dayOfWeek, String weekType);
}