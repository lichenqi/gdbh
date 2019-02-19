package com.guodongbaohe.app.util;

/**
 * Created by Administrator on 2018/7/9.
 */

public class TimeShowUtil {

    public static String getTimeShow(String dataTime, long systemTime) {
        long dateTime = Long.valueOf(dataTime) * 1000;
        long distanceTime = systemTime - dateTime;
        if (distanceTime > 24 * 60 * 60 * 1000) {
            return DateUtils.getTimeHour(dateTime);
        } else if (distanceTime > 12 * 60 * 60 * 1000 && distanceTime < 24 * 60 * 60 * 1000) {
            return "1天前";
        } else if (distanceTime < 12 * 60 * 60 * 1000 && distanceTime > 1 * 60 * 60 * 1000) {
            return (distanceTime / 1000 / 60 / 60 + "小时前");
        } else if (distanceTime < 1 * 60 * 60 * 1000 && distanceTime > 1 * 5 * 60 * 1000) {
            return (distanceTime / 1000 / 60 + "分钟前");
        } else if (distanceTime < 1 * 5 * 60 * 1000) {
            return ("刚刚");
        }
        return "";
    }

}
