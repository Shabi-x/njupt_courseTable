package com.example.njupt_coursetable.data.remote;

import android.content.Context;
import android.util.Log;

import com.example.njupt_coursetable.data.remote.api.CourseApiService;
import com.example.njupt_coursetable.data.remote.api.ReminderApiService;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Retrofit客户端类
 * 负责创建和配置Retrofit实例，提供API服务接口
 */
public class RetrofitClient {

    private static final String TAG = "RetrofitClient";
    
    /**
     * 服务器基础URL
     */
    private static final String BASE_URL = "http://10.0.2.2:8080/"; // Android模拟器访问本地服务器的地址
    
    /**
     * Retrofit实例
     */
    private static Retrofit retrofit = null;
    
    /**
     * OkHttpClient实例
     */
    private static OkHttpClient okHttpClient = null;
    
    /**
     * 课程API服务实例
     */
    private static CourseApiService courseApiService = null;
    
    /**
     * 提醒API服务实例
     */
    private static ReminderApiService reminderApiService = null;

    /**
     * 获取Retrofit实例
     * @param context 应用上下文
     * @return Retrofit实例
     */
    public static synchronized Retrofit getClient(Context context) {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(getOkHttpClient(context))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    /**
     * 获取OkHttpClient实例
     * @param context 应用上下文
     * @return OkHttpClient实例
     */
    private static synchronized OkHttpClient getOkHttpClient(Context context) {
        if (okHttpClient == null) {
            // 创建日志拦截器
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            
            // 创建OkHttpClient
            okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor) // 添加日志拦截器
                    .connectTimeout(30, TimeUnit.SECONDS) // 连接超时时间
                    .readTimeout(30, TimeUnit.SECONDS) // 读取超时时间
                    .writeTimeout(30, TimeUnit.SECONDS) // 写入超时时间
                    .retryOnConnectionFailure(true) // 连接失败时重试
                    .build();
        }
        return okHttpClient;
    }

    /**
     * 获取课程API服务实例
     * @param context 应用上下文
     * @return CourseApiService实例
     */
    public static synchronized CourseApiService getCourseApiService(Context context) {
        if (courseApiService == null) {
            courseApiService = getClient(context).create(CourseApiService.class);
        }
        return courseApiService;
    }

    /**
     * 获取提醒API服务实例
     * @param context 应用上下文
     * @return ReminderApiService实例
     */
    public static synchronized ReminderApiService getReminderApiService(Context context) {
        if (reminderApiService == null) {
            reminderApiService = getClient(context).create(ReminderApiService.class);
        }
        return reminderApiService;
    }

    /**
     * 更新服务器基础URL
     * @param newBaseUrl 新的基础URL
     */
    public static synchronized void updateBaseUrl(String newBaseUrl) {
        Log.d(TAG, "Updating base URL to: " + newBaseUrl);
        if (!BASE_URL.equals(newBaseUrl)) {
            // 重置所有实例，以便下次调用时使用新的URL
            retrofit = null;
            okHttpClient = null;
            courseApiService = null;
            reminderApiService = null;
        }
    }
}