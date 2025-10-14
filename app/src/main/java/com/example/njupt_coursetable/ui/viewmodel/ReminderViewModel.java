package com.example.njupt_coursetable.ui.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.njupt_coursetable.data.model.Reminder;
import com.example.njupt_coursetable.data.repository.ReminderRepository;

import java.util.List;

/**
 * 提醒视图模型类
 * 负责处理与提醒相关的业务逻辑和数据操作
 */
public class ReminderViewModel extends AndroidViewModel {

    private static final String TAG = "ReminderViewModel";
    
    /**
     * 提醒仓库实例
     */
    private final ReminderRepository reminderRepository;
    
    /**
     * 所有提醒的LiveData
     */
    private final LiveData<List<Reminder>> allReminders;
    
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
     * @param reminderRepository 提醒仓库
     */
    public ReminderViewModel(@NonNull Application application, ReminderRepository reminderRepository) {
        super(application);
        this.reminderRepository = reminderRepository;
        this.allReminders = reminderRepository.getAllReminders();
    }

    /**
     * 获取所有提醒
     * @return 所有提醒的LiveData
     */
    public LiveData<List<Reminder>> getAllReminders() {
        return allReminders;
    }

    /**
     * 根据ID获取提醒
     * @param reminderId 提醒ID
     * @return 提醒的LiveData
     */
    public LiveData<Reminder> getReminderById(long reminderId) {
        return reminderRepository.getReminderById(reminderId);
    }

    /**
     * 根据课程ID获取提醒
     * @param courseId 课程ID
     * @return 对应课程的提醒列表的LiveData
     */
    public LiveData<Reminder> getRemindersByCourseId(long courseId) {
        return reminderRepository.getReminderByCourseId(courseId);
    }

    /**
     * 获取所有启用的提醒
     * @return 所有启用的提醒列表的LiveData
     */
    public LiveData<List<Reminder>> getEnabledReminders() {
        return reminderRepository.getEnabledReminders();
    }

    /**
     * 插入提醒
     * @param reminder 要插入的提醒对象
     */
    public void insertReminder(Reminder reminder) {
        isLoading.setValue(true);
        
        reminderRepository.insertReminder(reminder).observeForever(id -> {
            isLoading.setValue(false);
            if (id > 0) {
                Log.d(TAG, "Reminder inserted with ID: " + id);
                operationResult.setValue(true);
            } else {
                Log.e(TAG, "Failed to insert reminder");
                operationResult.setValue(false);
            }
        });
    }

    /**
     * 更新提醒
     * @param reminder 要更新的提醒对象
     */
    public void updateReminder(Reminder reminder) {
        isLoading.setValue(true);
        
        reminderRepository.updateReminder(reminder).observeForever(rowsAffected -> {
            isLoading.setValue(false);
            if (rowsAffected > 0) {
                Log.d(TAG, "Reminder updated: " + reminder.getId());
                operationResult.setValue(true);
            } else {
                Log.e(TAG, "Failed to update reminder: " + reminder.getId());
                operationResult.setValue(false);
            }
        });
    }

    /**
     * 删除提醒
     * @param reminderId 要删除的提醒ID
     */
    public void deleteReminder(long reminderId) {
        isLoading.setValue(true);
        
        reminderRepository.deleteReminder(reminderId).observeForever(rowsAffected -> {
            isLoading.setValue(false);
            if (rowsAffected > 0) {
                Log.d(TAG, "Reminder deleted: " + reminderId);
                operationResult.setValue(true);
            } else {
                Log.e(TAG, "Failed to delete reminder: " + reminderId);
                operationResult.setValue(false);
            }
        });
    }

    /**
     * 更新提醒启用状态
     * @param reminderId 提醒ID
     * @param enabled 是否启用
     */
    public void updateReminderEnabledStatus(long reminderId, boolean enabled) {
        isLoading.setValue(true);
        
        reminderRepository.updateReminderEnabledStatus(reminderId, enabled).observeForever(rowsAffected -> {
            isLoading.setValue(false);
            if (rowsAffected > 0) {
                Log.d(TAG, "Reminder enabled status updated: " + reminderId + ", enabled: " + enabled);
                operationResult.setValue(true);
            } else {
                Log.e(TAG, "Failed to update reminder enabled status: " + reminderId);
                operationResult.setValue(false);
            }
        });
    }

    /**
     * 从服务器同步所有提醒数据
     */
    public void syncRemindersFromServer() {
        isLoading.setValue(true);
        
        reminderRepository.syncRemindersFromServer().observeForever(success -> {
            isLoading.setValue(false);
            if (success != null && success) {
                Log.d(TAG, "Reminders synced from server successfully");
                operationResult.setValue(true);
            } else {
                Log.e(TAG, "Failed to sync reminders from server");
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