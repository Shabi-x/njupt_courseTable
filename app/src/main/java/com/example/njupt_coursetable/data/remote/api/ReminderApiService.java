package com.example.njupt_coursetable.data.remote.api;

import com.example.njupt_coursetable.data.model.Reminder;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ReminderApiService {
    @GET("api/reminders/upcoming")
    Call<List<Reminder>> getUpcomingReminders();

    @POST("api/reminders")
    Call<Reminder> createReminder(
            @Query("courseId") long courseId,
            @Query("courseDate") String courseDate,
            @Query("startTime") String startTime
    );

    @DELETE("api/reminders/{id}")
    Call<Void> deleteReminder(@Path("id") long id);

    @DELETE("api/reminders/byCourseDate")
    Call<Void> deleteReminderByCourseAndDate(
            @Query("courseId") long courseId,
            @Query("courseDate") String courseDate
    );
}


