package com.example.njupt_coursetable.data.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.njupt_coursetable.data.local.AppDatabase;
import com.example.njupt_coursetable.data.local.dao.ReminderDao;
import com.example.njupt_coursetable.data.model.Reminder;
import com.example.njupt_coursetable.data.remote.RetrofitClient;
import com.example.njupt_coursetable.data.remote.api.ReminderApiService;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 提醒仓库类
 * 负责协调本地数据源和远程数据源，提供统一的数据访问接口
 */
public class ReminderRepository {
    private static final String TAG = "ReminderRepository";
    
    /**
     * 单例实例
     */
    private static ReminderRepository instance;
    
    /**
     * 本地数据访问对象
     */
    private final ReminderDao reminderDao;
    
    /**
     * 远程API服务
     */
    private final ReminderApiService reminderApiService;
    
    /**
     * 线程池，用于执行数据库操作
     */
    private final ExecutorService executorService;
    
    /**
     * 私有构造函数
     * @param context 应用上下文
     */
    private ReminderRepository(Context context) {
        AppDatabase database = AppDatabase.getInstance(context);
        reminderDao = database.reminderDao();
        reminderApiService = RetrofitClient.getReminderApiService(context);
        executorService = Executors.newFixedThreadPool(4);
    }
    
    /**
     * 获取ReminderRepository单例实例
     * @param context 应用上下文
     * @return ReminderRepository实例
     */
    public static synchronized ReminderRepository getInstance(Context context) {
        if (instance == null) {
            instance = new ReminderRepository(context.getApplicationContext());
        }
        return instance;
    }
    
    /**
     * 获取所有提醒
     * @return 所有提醒的LiveData列表
     */
    public LiveData<List<Reminder>> getAllReminders() {
        // 首先从本地数据库获取数据
        return reminderDao.getAllReminders();
    }
    
    /**
     * 获取所有启用的提醒
     * @return 所有启用的提醒的LiveData列表
     */
    public LiveData<List<Reminder>> getEnabledReminders() {
        return reminderDao.getEnabledReminders();
    }
    
    /**
     * 根据ID获取提醒
     * @param reminderId 提醒ID
     * @return 提醒的LiveData对象
     */
    public LiveData<Reminder> getReminderById(long reminderId) {
        return reminderDao.getReminderById(reminderId);
    }
    
    /**
     * 根据课程ID获取提醒
     * @param courseId 课程ID
     * @return 对应课程的提醒对象
     */
    public LiveData<Reminder> getReminderByCourseId(long courseId) {
        return reminderDao.getReminderByCourseId(courseId);
    }
    
    /**
     * 插入提醒
     * @param reminder 要插入的提醒对象
     * @return 插入结果的LiveData
     */
    public LiveData<Long> insertReminder(Reminder reminder) {
        MutableLiveData<Long> result = new MutableLiveData<>();
        
        // 先在本地数据库插入
        executorService.execute(() -> {
            long id = reminderDao.insert(reminder);
            result.postValue(id);
            
            // 然后同步到服务器
            reminderApiService.createReminder(reminder).enqueue(new Callback<Reminder>() {
                @Override
                public void onResponse(Call<Reminder> call, Response<Reminder> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Log.d(TAG, "Reminder created on server: " + response.body().getId());
                    } else {
                        Log.e(TAG, "Failed to create reminder on server: " + response.message());
                    }
                }
                
                @Override
                public void onFailure(Call<Reminder> call, Throwable t) {
                    Log.e(TAG, "Error creating reminder on server", t);
                }
            });
        });
        
        return result;
    }
    
    /**
     * 更新提醒
     * @param reminder 要更新的提醒对象
     * @return 更新结果的LiveData
     */
    public LiveData<Integer> updateReminder(Reminder reminder) {
        MutableLiveData<Integer> result = new MutableLiveData<>();
        
        // 先更新本地数据库
        executorService.execute(() -> {
            int rowsAffected = reminderDao.update(reminder);
            result.postValue(rowsAffected);
            
            // 然后同步到服务器
            reminderApiService.updateReminder(reminder.getId(), reminder).enqueue(new Callback<Reminder>() {
                @Override
                public void onResponse(Call<Reminder> call, Response<Reminder> response) {
                    if (response.isSuccessful()) {
                        Log.d(TAG, "Reminder updated on server: " + reminder.getId());
                    } else {
                        Log.e(TAG, "Failed to update reminder on server: " + response.message());
                    }
                }
                
                @Override
                public void onFailure(Call<Reminder> call, Throwable t) {
                    Log.e(TAG, "Error updating reminder on server", t);
                }
            });
        });
        
        return result;
    }
    
    /**
     * 删除提醒
     * @param reminderId 要删除的提醒ID
     * @return 删除结果的LiveData
     */
    public LiveData<Integer> deleteReminder(long reminderId) {
        MutableLiveData<Integer> result = new MutableLiveData<>();
        
        executorService.execute(() -> {
            // 先从本地数据库获取提醒信息
            Reminder reminder = reminderDao.getReminderByIdSync(reminderId);
            
            if (reminder != null) {
                // 删除本地数据库中的提醒
                int rowsAffected = reminderDao.deleteById(reminderId);
                result.postValue(rowsAffected);
                
                // 然后从服务器删除
                reminderApiService.deleteReminder(reminderId).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Log.d(TAG, "Reminder deleted from server: " + reminderId);
                        } else {
                            Log.e(TAG, "Failed to delete reminder from server: " + response.message());
                        }
                    }
                    
                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.e(TAG, "Error deleting reminder from server", t);
                    }
                });
            } else {
                result.postValue(0);
            }
        });
        
        return result;
    }
    
    /**
     * 更新提醒的启用状态
     * @param reminderId 提醒ID
     * @param enabled 是否启用
     * @return 更新结果的LiveData
     */
    public LiveData<Integer> updateReminderEnabledStatus(long reminderId, boolean enabled) {
        MutableLiveData<Integer> result = new MutableLiveData<>();
        
        executorService.execute(() -> {
            // 更新本地数据库
            int rowsAffected = reminderDao.updateReminderEnabledStatus(reminderId, enabled);
            result.postValue(rowsAffected);
            
            // 同步到服务器
            reminderApiService.updateReminderEnabledStatus(reminderId, enabled).enqueue(new Callback<Reminder>() {
                @Override
                public void onResponse(Call<Reminder> call, Response<Reminder> response) {
                    if (response.isSuccessful()) {
                        Log.d(TAG, "Reminder enabled status updated on server: " + reminderId);
                    } else {
                        Log.e(TAG, "Failed to update reminder enabled status on server: " + response.message());
                    }
                }
                
                @Override
                public void onFailure(Call<Reminder> call, Throwable t) {
                    Log.e(TAG, "Error updating reminder enabled status on server", t);
                }
            });
        });
        
        return result;
    }
    
    /**
     * 从服务器同步所有提醒数据
     * @return 同步结果的LiveData
     */
    public LiveData<Boolean> syncRemindersFromServer() {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        
        reminderApiService.getAllReminders().enqueue(new Callback<List<Reminder>>() {
            @Override
            public void onResponse(Call<List<Reminder>> call, Response<List<Reminder>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Reminder> reminders = response.body();
                    
                    executorService.execute(() -> {
                        // 先清空本地数据库
                        reminderDao.deleteAllReminders();
                        
                        // 然后插入从服务器获取的数据
                        for (Reminder reminder : reminders) {
                            reminderDao.insert(reminder);
                        }
                        
                        result.postValue(true);
                    });
                } else {
                    Log.e(TAG, "Failed to sync reminders from server: " + response.message());
                    result.postValue(false);
                }
            }
            
            @Override
            public void onFailure(Call<List<Reminder>> call, Throwable t) {
                Log.e(TAG, "Error syncing reminders from server", t);
                result.postValue(false);
            }
        });
        
        return result;
    }
    
    /**
     * 获取指定时间之前的提醒
     * @param time 时间戳
     * @return 指定时间之前的提醒列表
     */
    public List<Reminder> getRemindersBeforeTime(long time) {
        return reminderDao.getRemindersBeforeTime(time);
    }
}