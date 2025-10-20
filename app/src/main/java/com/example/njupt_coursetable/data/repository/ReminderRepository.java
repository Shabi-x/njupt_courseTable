package com.example.njupt_coursetable.data.repository;

import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.njupt_coursetable.data.model.Reminder;
import com.example.njupt_coursetable.data.remote.RetrofitClient;
import com.example.njupt_coursetable.data.remote.api.ReminderApiService;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReminderRepository {
    private static ReminderRepository instance;
    private final ReminderApiService api;

    private ReminderRepository(Context context) {
        this.api = RetrofitClient.getClient(context).create(ReminderApiService.class);
    }

    public static synchronized ReminderRepository getInstance(Context context) {
        if (instance == null) instance = new ReminderRepository(context.getApplicationContext());
        return instance;
    }

    public LiveData<List<Reminder>> getUpcomingReminders() {
        MutableLiveData<List<Reminder>> data = new MutableLiveData<>();
        api.getUpcomingReminders().enqueue(new Callback<List<Reminder>>() {
            @Override public void onResponse(Call<List<Reminder>> call, Response<List<Reminder>> resp) {
                data.postValue(resp.isSuccessful() && resp.body()!=null ? resp.body() : new ArrayList<>());
            }
            @Override public void onFailure(Call<List<Reminder>> call, Throwable t) { data.postValue(new ArrayList<>()); }
        });
        return data;
    }

    public LiveData<Boolean> createReminder(long courseId, String courseDate, String startTime) {
        MutableLiveData<Boolean> ok = new MutableLiveData<>(false);
        api.createReminder(courseId, courseDate, startTime).enqueue(new Callback<Reminder>() {
            @Override public void onResponse(Call<Reminder> call, Response<Reminder> resp) { ok.postValue(resp.isSuccessful()); }
            @Override public void onFailure(Call<Reminder> call, Throwable t) { ok.postValue(false); }
        });
        return ok;
    }

    public LiveData<Boolean> deleteReminderByCourseAndDate(long courseId, String courseDate) {
        MutableLiveData<Boolean> ok = new MutableLiveData<>(false);
        api.deleteReminderByCourseAndDate(courseId, courseDate).enqueue(new Callback<Void>() {
            @Override public void onResponse(Call<Void> call, Response<Void> resp) { ok.postValue(resp.isSuccessful()); }
            @Override public void onFailure(Call<Void> call, Throwable t) { ok.postValue(false); }
        });
        return ok;
    }
}


