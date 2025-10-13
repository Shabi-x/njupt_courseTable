package com.example.njupt_coursetable.data.remote.api;

import com.example.njupt_coursetable.data.model.Reminder;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * 提醒网络服务接口
 * 定义与服务器交互的API端点
 */
public interface ReminderApiService {

    /**
     * 获取所有提醒
     * @return 提醒列表的Call对象
     */
    @GET("api/reminders")
    Call<List<Reminder>> getAllReminders();

    /**
     * 根据ID获取提醒
     * @param id 提醒ID
     * @return 提醒对象的Call对象
     */
    @GET("api/reminders/{id}")
    Call<Reminder> getReminderById(@Path("id") long id);

    /**
     * 根据课程ID获取提醒
     * @param courseId 课程ID
     * @return 提醒对象的Call对象
     */
    @GET("api/reminders/course/{courseId}")
    Call<Reminder> getReminderByCourseId(@Path("courseId") long courseId);

    /**
     * 创建新提醒
     * @param reminder 提醒对象
     * @return 创建的提醒对象的Call对象
     */
    @POST("api/reminders")
    Call<Reminder> createReminder(@Body Reminder reminder);

    /**
     * 更新提醒
     * @param id 提醒ID
     * @param reminder 更新的提醒对象
     * @return 更新后的提醒对象的Call对象
     */
    @PUT("api/reminders/{id}")
    Call<Reminder> updateReminder(@Path("id") long id, @Body Reminder reminder);

    /**
     * 删除提醒
     * @param id 提醒ID
     * @return 空的Call对象
     */
    @DELETE("api/reminders/{id}")
    Call<Void> deleteReminder(@Path("id") long id);

    /**
     * 获取所有启用的提醒
     * @return 启用的提醒列表的Call对象
     */
    @GET("api/reminders/enabled")
    Call<List<Reminder>> getEnabledReminders();

    /**
     * 更新提醒的启用状态
     * @param id 提醒ID
     * @param enabled 是否启用
     * @return 更新后的提醒对象的Call对象
     */
    @PUT("api/reminders/{id}/enabled/{enabled}")
    Call<Reminder> updateReminderEnabledStatus(@Path("id") long id, @Path("enabled") boolean enabled);
}