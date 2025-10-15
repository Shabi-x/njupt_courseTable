package com.example.njupt_coursetable.utils;

/**
 * 时间工具类
 * 用于处理课程时间相关的计算和转换
 */
public class TimeUtils {
    
    /**
     * 根据星期几的数字获取对应的字符串
     * @param dayOfWeek 星期几 (0-6, 0=周日, 1=周一, ..., 6=周六)
     * @return 星期几的字符串
     */
    public static String getDayOfWeekString(int dayOfWeek) {
        switch (dayOfWeek) {
            case 0:
                return "周日";
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
            default:
                return "未知";
        }
    }
    
    /**
     * 根据节次获取开始时间的小时
     * @param section 节次 (1-12)
     * @return 开始时间的小时
     */
    public static int getStartHour(int section) {
        switch (section) {
            case 1:
            case 2:
                return 8; // 第1-2节 8:00-9:40
            case 3:
            case 4:
                return 10; // 第3-4节 10:00-11:40
            case 5:
            case 6:
                return 14; // 第5-6节 14:00-15:40
            case 7:
            case 8:
                return 16; // 第7-8节 16:00-17:40
            case 9:
            case 10:
                return 19; // 第9-10节 19:00-20:40
            case 11:
            case 12:
                return 21; // 第11-12节 21:00-22:40
            default:
                return 8;
        }
    }
    
    /**
     * 根据节次获取开始时间的分钟
     * @param section 节次 (1-12)
     * @return 开始时间的分钟
     */
    public static int getStartMinute(int section) {
        switch (section) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 9:
            case 11:
                return 0; // 奇数节次从整点开始
            case 2:
            case 4:
            case 6:
            case 8:
            case 10:
            case 12:
                return 0; // 偶数节次从整点开始
            default:
                return 0;
        }
    }
    
    /**
     * 根据节次获取结束时间的小时
     * @param section 节次 (1-12)
     * @return 结束时间的小时
     */
    public static int getEndHour(int section) {
        switch (section) {
            case 1:
                return 9; // 第1节 8:00-9:00
            case 2:
                return 10; // 第2节 9:00-10:00
            case 3:
                return 11; // 第3节 10:00-11:00
            case 4:
                return 12; // 第4节 11:00-12:00
            case 5:
                return 15; // 第5节 14:00-15:00
            case 6:
                return 16; // 第6节 15:00-16:00
            case 7:
                return 17; // 第7节 16:00-17:00
            case 8:
                return 18; // 第8节 17:00-18:00
            case 9:
                return 20; // 第9节 19:00-20:00
            case 10:
                return 21; // 第10节 20:00-21:00
            case 11:
                return 22; // 第11节 21:00-22:00
            case 12:
                return 23; // 第12节 22:00-23:00
            default:
                return 9;
        }
    }
    
    /**
     * 根据节次获取结束时间的分钟
     * @param section 节次 (1-12)
     * @return 结束时间的分钟
     */
    public static int getEndMinute(int section) {
        return 0; // 所有节次都在整点结束
    }
    
    /**
     * 根据开始和结束节次获取时间范围字符串
     * @param startSection 开始节次
     * @param endSection 结束节次
     * @return 时间范围字符串
     */
    public static String getTimeRangeString(int startSection, int endSection) {
        int startHour = getStartHour(startSection);
        int startMinute = getStartMinute(startSection);
        int endHour = getEndHour(endSection);
        int endMinute = getEndMinute(endSection);
        
        return String.format("%02d:%02d-%02d:%02d", startHour, startMinute, endHour, endMinute);
    }
    
    /**
     * 根据节次获取节次字符串
     * @param section 节次 (1-12)
     * @return 节次字符串
     */
    public static String getSectionString(int section) {
        return "第" + section + "节";
    }
    
    /**
     * 根据开始和结束节次获取节次范围字符串
     * @param startSection 开始节次
     * @param endSection 结束节次
     * @return 节次范围字符串
     */
    public static String getSectionRangeString(int startSection, int endSection) {
        if (startSection == endSection) {
            return getSectionString(startSection);
        } else {
            return "第" + startSection + "-" + endSection + "节";
        }
    }
}