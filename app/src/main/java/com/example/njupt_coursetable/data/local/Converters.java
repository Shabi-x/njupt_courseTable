package com.example.njupt_coursetable.data.local;

import androidx.room.TypeConverter;

/**
 * Room数据库类型转换器
 */
public class Converters {
    
    @TypeConverter
    public static Integer fromDayOfWeek(int dayOfWeek) {
        return dayOfWeek;
    }
    
    @TypeConverter
    public static int fromDayOfWeek(Integer dayOfWeek) {
        return dayOfWeek == null ? 1 : dayOfWeek;
    }
}