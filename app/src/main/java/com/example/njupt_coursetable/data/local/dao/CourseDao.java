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
     * 根据ID获取课程（非LiveData）
     * @param courseId 课程ID
     * @return 对应的课程对象
     */
    @Query("SELECT * FROM courses WHERE id = :courseId")
    Course getCourseByIdSync(long courseId);
    
    /**
     * 根据周数获取课程
     * @param weekNumber 周数，如"1"
     * @return 包含该周数的课程列表
     * 注意：使用精确匹配，避免"6"匹配到"16"的问题
     */
    @Query("SELECT * FROM courses WHERE weekRange = :weekNumber ORDER BY dayOfWeek, timeSlot")
    LiveData<List<Course>> getCoursesByWeekNumber(String weekNumber);
    
    /**
     * 获取所有需要提醒的课程
     * @return 需要提醒的课程列表
     */
    @Query("SELECT * FROM courses WHERE shouldReminder = 1 ORDER BY dayOfWeek, timeSlot")
    LiveData<List<Course>> getCoursesWithReminder();
}