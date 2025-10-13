package com.example.njupt_coursetable.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import com.example.njupt_coursetable.data.model.Reminder;

import java.util.List;

/**
 * 提醒数据访问对象接口
 * 定义对提醒表的所有数据库操作
 */
@Dao
public interface ReminderDao {

    /**
     * 插入一个新提醒
     * @param reminder 要插入的提醒对象
     * @return 新插入提醒的行ID
     */
    @Insert
    long insert(Reminder reminder);

    /**
     * 插入多个提醒
     * @param reminders 要插入的提醒列表
     * @return 新插入提醒的行ID数组
     */
    @Insert
    long[] insertAll(Reminder... reminders);

    /**
     * 更新提醒信息
     * @param reminder 要更新的提醒对象
     * @return 受影响的行数
     */
    @Update
    int update(Reminder reminder);

    /**
     * 删除提醒
     * @param reminder 要删除的提醒对象
     * @return 受影响的行数
     */
    @Delete
    int delete(Reminder reminder);

    /**
     * 根据ID删除提醒
     * @param reminderId 要删除的提醒ID
     * @return 受影响的行数
     */
    @Query("DELETE FROM reminders WHERE id = :reminderId")
    int deleteById(long reminderId);

    /**
     * 删除所有提醒
     * @return 受影响的行数
     */
    @Query("DELETE FROM reminders")
    int deleteAllReminders();

    /**
     * 获取所有提醒
     * @return 所有提醒的LiveData列表，用于观察数据变化
     */
    @Query("SELECT * FROM reminders ORDER BY remindTime")
    LiveData<List<Reminder>> getAllReminders();

    /**
     * 获取所有提醒（非LiveData）
     * @return 所有提醒的列表
     */
    @Query("SELECT * FROM reminders ORDER BY remindTime")
    List<Reminder> getAllRemindersSync();

    /**
     * 根据ID获取提醒
     * @param reminderId 提醒ID
     * @return 对应的提醒对象
     */
    @Query("SELECT * FROM reminders WHERE id = :reminderId")
    LiveData<Reminder> getReminderById(long reminderId);

    /**
     * 根据ID获取提醒（非LiveData）
     * @param reminderId 提醒ID
     * @return 对应的提醒对象
     */
    @Query("SELECT * FROM reminders WHERE id = :reminderId")
    Reminder getReminderByIdSync(long reminderId);

    /**
     * 根据课程ID获取提醒
     * @param courseId 课程ID
     * @return 对应课程的提醒对象
     */
    @Query("SELECT * FROM reminders WHERE courseId = :courseId")
    LiveData<Reminder> getReminderByCourseId(long courseId);

    /**
     * 根据课程ID获取提醒（非LiveData）
     * @param courseId 课程ID
     * @return 对应课程的提醒对象
     */
    @Query("SELECT * FROM reminders WHERE courseId = :courseId")
    Reminder getReminderByCourseIdSync(long courseId);

    /**
     * 获取所有启用的提醒
     * @return 所有启用的提醒列表
     */
    @Query("SELECT * FROM reminders WHERE isEnabled = 1 ORDER BY remindTime")
    LiveData<List<Reminder>> getEnabledReminders();

    /**
     * 获取所有启用的提醒（非LiveData）
     * @return 所有启用的提醒列表
     */
    @Query("SELECT * FROM reminders WHERE isEnabled = 1 ORDER BY remindTime")
    List<Reminder> getEnabledRemindersSync();

    /**
     * 更新提醒的启用状态
     * @param reminderId 提醒ID
     * @param enabled 是否启用
     * @return 受影响的行数
     */
    @Query("UPDATE reminders SET isEnabled = :enabled WHERE id = :reminderId")
    int updateReminderEnabledStatus(long reminderId, boolean enabled);

    /**
     * 获取指定时间之前的提醒
     * @param time 时间戳
     * @return 指定时间之前的提醒列表
     */
    @Query("SELECT * FROM reminders WHERE remindTime <= :time ORDER BY remindTime")
    List<Reminder> getRemindersBeforeTime(long time);
}