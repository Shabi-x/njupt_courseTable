package com.example.njupt_coursetable.utils;

import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.temporal.TemporalAdjusters;

import java.util.Locale;

/**
 * 日期工具类
 * 提供日期相关的常用方法
 */
public class DateUtils {

    /**
     * 日期格式化器
     */
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault());
    
    /**
     * 时间格式化器
     */
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault());

    /**
     * 获取当前日期
     * @return 当前日期字符串，格式为yyyy-MM-dd
     */
    public static String getCurrentDate() {
        return LocalDate.now().format(DATE_FORMATTER);
    }

    /**
     * 获取当前时间
     * @return 当前时间字符串，格式为HH:mm
     */
    public static String getCurrentTime() {
        return org.threeten.bp.LocalTime.now().format(TIME_FORMATTER);
    }

    /**
     * 将星期几转换为中文表示
     * @param dayOfWeek 星期几
     * @return 中文星期几，如"周一"
     */
    public static String dayOfWeekToChinese(DayOfWeek dayOfWeek) {
        switch (dayOfWeek) {
            case MONDAY:
                return "周一";
            case TUESDAY:
                return "周二";
            case WEDNESDAY:
                return "周三";
            case THURSDAY:
                return "周四";
            case FRIDAY:
                return "周五";
            case SATURDAY:
                return "周六";
            case SUNDAY:
                return "周日";
            default:
                return "";
        }
    }

    /**
     * 将中文星期几转换为DayOfWeek
     * @param chineseDayOfWeek 中文星期几，如"周一"
     * @return DayOfWeek对象
     */
    public static DayOfWeek chineseToDayOfWeek(String chineseDayOfWeek) {
        switch (chineseDayOfWeek) {
            case "周一":
                return DayOfWeek.MONDAY;
            case "周二":
                return DayOfWeek.TUESDAY;
            case "周三":
                return DayOfWeek.WEDNESDAY;
            case "周四":
                return DayOfWeek.THURSDAY;
            case "周五":
                return DayOfWeek.FRIDAY;
            case "周六":
                return DayOfWeek.SATURDAY;
            case "周日":
                return DayOfWeek.SUNDAY;
            default:
                return DayOfWeek.MONDAY;
        }
    }

    /**
     * 获取本周的某一天
     * @param dayOfWeek 星期几
     * @return 本周的某一天的日期
     */
    public static LocalDate getDateOfCurrentWeek(DayOfWeek dayOfWeek) {
        return LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                .plusDays(dayOfWeek.getValue() - DayOfWeek.MONDAY.getValue());
    }

    /**
     * 获取下周的某一天
     * @param dayOfWeek 星期几
     * @return 下周的某一天的日期
     */
    public static LocalDate getDateOfNextWeek(DayOfWeek dayOfWeek) {
        return LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY))
                .plusDays(dayOfWeek.getValue() - DayOfWeek.MONDAY.getValue());
    }

    /**
     * 检查给定日期是否是本周
     * @param date 要检查的日期
     * @return 如果是本周返回true，否则返回false
     */
    public static boolean isCurrentWeek(LocalDate date) {
        LocalDate mondayOfCurrentWeek = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate sundayOfCurrentWeek = mondayOfCurrentWeek.plusDays(6);
        return !date.isBefore(mondayOfCurrentWeek) && !date.isAfter(sundayOfCurrentWeek);
    }

    /**
     * 检查给定日期是否是下周
     * @param date 要检查的日期
     * @return 如果是下周返回true，否则返回false
     */
    public static boolean isNextWeek(LocalDate date) {
        LocalDate mondayOfNextWeek = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        LocalDate sundayOfNextWeek = mondayOfNextWeek.plusDays(6);
        return !date.isBefore(mondayOfNextWeek) && !date.isAfter(sundayOfNextWeek);
    }

    /**
     * 解析日期字符串
     * @param dateString 日期字符串，格式为yyyy-MM-dd
     * @return LocalDate对象
     */
    public static LocalDate parseDate(String dateString) {
        return LocalDate.parse(dateString, DATE_FORMATTER);
    }

    /**
     * 格式化日期
     * @param date 日期
     * @return 格式化后的日期字符串，格式为yyyy-MM-dd
     */
    public static String formatDate(LocalDate date) {
        return date.format(DATE_FORMATTER);
    }
}