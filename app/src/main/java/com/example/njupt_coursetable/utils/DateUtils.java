package com.example.njupt_coursetable.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 日期工具类
 * 提供日期相关的常用方法
 */
public class DateUtils {

    /**
     * 日期格式化器
     */
    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    
    /**
     * 时间格式化器
     */
    private static final SimpleDateFormat TIME_FORMATTER = new SimpleDateFormat("HH:mm", Locale.getDefault());

    /**
     * 获取当前日期
     * @return 当前日期字符串，格式为yyyy-MM-dd
     */
    public static String getCurrentDate() {
        return DATE_FORMATTER.format(new Date());
    }

    /**
     * 获取当前时间
     * @return 当前时间字符串，格式为HH:mm
     */
    public static String getCurrentTime() {
        return TIME_FORMATTER.format(new Date());
    }

    /**
     * 将星期几转换为中文表示
     * @param dayOfWeek 星期几 (1-7, 1为周日)
     * @return 中文星期几，如"周一"
     */
    public static String dayOfWeekToChinese(int dayOfWeek) {
        // 转换Calendar的星期几到我们的标准 (1=周日 -> 7=周六)
        int standardDayOfWeek = dayOfWeek == Calendar.SUNDAY ? 7 : dayOfWeek - 1;
        
        switch (standardDayOfWeek) {
            case 1:
                return "周一";
            case 2:
                return "周二";
            case 3:
                return "周三";
            case 4:
                return "周四";
            case 5:
                return "周五";
            case 6:
                return "周六";
            case 7:
                return "周日";
            default:
                return "周一";
        }
    }

    /**
     * 将中文星期几转换为Calendar常量
     * @param chineseDayOfWeek 中文星期几，如"周一"
     * @return Calendar常量 (Calendar.MONDAY等)
     */
    public static int chineseToCalendarDayOfWeek(String chineseDayOfWeek) {
        switch (chineseDayOfWeek) {
            case "周一":
                return Calendar.MONDAY;
            case "周二":
                return Calendar.TUESDAY;
            case "周三":
                return Calendar.WEDNESDAY;
            case "周四":
                return Calendar.THURSDAY;
            case "周五":
                return Calendar.FRIDAY;
            case "周六":
                return Calendar.SATURDAY;
            case "周日":
                return Calendar.SUNDAY;
            default:
                return Calendar.MONDAY;
        }
    }

    /**
     * 获取本周的某一天
     * @param dayOfWeek Calendar常量 (Calendar.MONDAY等)
     * @return 本周的某一天的日期字符串
     */
    public static String getDateOfCurrentWeek(int dayOfWeek) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);
        return DATE_FORMATTER.format(calendar.getTime());
    }

    /**
     * 获取下周的某一天
     * @param dayOfWeek Calendar常量 (Calendar.MONDAY等)
     * @return 下周的某一天的日期字符串
     */
    public static String getDateOfNextWeek(int dayOfWeek) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.WEEK_OF_YEAR, 1);
        calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);
        return DATE_FORMATTER.format(calendar.getTime());
    }

    /**
     * 检查给定日期是否是本周
     * @param date 要检查的日期字符串
     * @return 如果是本周返回true，否则返回false
     */
    public static boolean isCurrentWeek(String date) {
        try {
            Date targetDate = DATE_FORMATTER.parse(date);
            if (targetDate == null) return false;
            
            Calendar targetCalendar = Calendar.getInstance();
            targetCalendar.setTime(targetDate);
            
            Calendar currentCalendar = Calendar.getInstance();
            
            // 获取本周一的日期
            Calendar mondayOfCurrentWeek = Calendar.getInstance();
            mondayOfCurrentWeek.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            
            // 获取本周日的日期
            Calendar sundayOfCurrentWeek = Calendar.getInstance();
            sundayOfCurrentWeek.setTime(mondayOfCurrentWeek.getTime());
            sundayOfCurrentWeek.add(Calendar.DAY_OF_MONTH, 6);
            
            return !targetCalendar.before(mondayOfCurrentWeek) && !targetCalendar.after(sundayOfCurrentWeek);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 检查给定日期是否是下周
     * @param date 要检查的日期字符串
     * @return 如果是下周返回true，否则返回false
     */
    public static boolean isNextWeek(String date) {
        try {
            Date targetDate = DATE_FORMATTER.parse(date);
            if (targetDate == null) return false;
            
            Calendar targetCalendar = Calendar.getInstance();
            targetCalendar.setTime(targetDate);
            
            // 获取下周一的日期
            Calendar mondayOfNextWeek = Calendar.getInstance();
            mondayOfNextWeek.add(Calendar.WEEK_OF_YEAR, 1);
            mondayOfNextWeek.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            
            // 获取下周日的日期
            Calendar sundayOfNextWeek = Calendar.getInstance();
            sundayOfNextWeek.setTime(mondayOfNextWeek.getTime());
            sundayOfNextWeek.add(Calendar.DAY_OF_MONTH, 6);
            
            return !targetCalendar.before(mondayOfNextWeek) && !targetCalendar.after(sundayOfNextWeek);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 解析日期字符串
     * @param dateString 日期字符串，格式为yyyy-MM-dd
     * @return Date对象
     */
    public static Date parseDate(String dateString) {
        try {
            return DATE_FORMATTER.parse(dateString);
        } catch (Exception e) {
            return new Date();
        }
    }

    /**
     * 格式化日期
     * @param date 日期
     * @return 格式化后的日期字符串，格式为yyyy-MM-dd
     */
    public static String formatDate(Date date) {
        return DATE_FORMATTER.format(date);
    }
}