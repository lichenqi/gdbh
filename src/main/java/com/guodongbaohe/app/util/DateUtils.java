package com.guodongbaohe.app.util;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Administrator on 2017/8/27.
 */

public class DateUtils {
    /**
     * 将时间转换成毫秒值
     */
    public static long strhaomiao(String end) {
        SimpleDateFormat strhaomiao = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long timeend = 0;
        try {
            if (TextUtils.isEmpty(end)) {
                return 0;
            } else {
                timeend = strhaomiao.parse(end).getTime();
                return timeend;
            }

        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /*时间格式转化为时间戳类型*/
    public static long getDateTime(String name) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = format.parse(name);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
    }

    /*将时间戳格式化*/
    public static String getTimeType(long name) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        return format.format(name);
    }


    /*将时间戳格式化(2018-06-20 14:14:15)*/
    public static String getTimeYearType(long name) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
        return format.format(name);
    }

    /*将时间戳格式化*/
    public static String getTimeToString(long name) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(name);
    }

    /*将时间戳格式化 月日时分*/
    public static String getTimeHour(long name) {
        SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
        return format.format(name);
    }

    /*根据时间格式转星期*/
    public static String getWeek(String pTime) {
        String Week = "";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(format.parse(pTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            Week += "天";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
            Week += "一";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY) {
            Week += "二";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY) {
            Week += "三";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY) {
            Week += "四";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) {
            Week += "五";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
            Week += "六";
        }
        return Week;
    }

    /*根据时间格式计算相隔天数*/
    public static long getDistanceDays(String beginTime, String endTime) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date beginDate = null;
        Date endDate = null;
        try {
            beginDate = format.parse(beginTime);
            endDate = format.parse(endTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long betweenDay = (endDate.getTime() - beginDate.getTime()) / (24 * 60 * 60 * 1000);
        return betweenDay;
    }

    //获得当前年月日时分秒星期
    public static String getTime() {
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        String mYear = String.valueOf(c.get(Calendar.YEAR)); // 获取当前年份
        String mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);// 获取当前月份
        String mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));// 获取当前月份的日期号码
        String mWay = String.valueOf(c.get(Calendar.DAY_OF_WEEK));
        String mHour = String.valueOf(c.get(Calendar.HOUR_OF_DAY));//时
        String mMinute = String.valueOf(c.get(Calendar.MINUTE));//分
        String mSecond = String.valueOf(c.get(Calendar.SECOND));//秒

        if ("1".equals(mWay)) {
            mWay = "天";
        } else if ("2".equals(mWay)) {
            mWay = "一";
        } else if ("3".equals(mWay)) {
            mWay = "二";
        } else if ("4".equals(mWay)) {
            mWay = "三";
        } else if ("5".equals(mWay)) {
            mWay = "四";
        } else if ("6".equals(mWay)) {
            mWay = "五";
        } else if ("7".equals(mWay)) {
            mWay = "六";
        }
        return mHour + ":" + mMinute + ":" + mSecond;
    }
}


