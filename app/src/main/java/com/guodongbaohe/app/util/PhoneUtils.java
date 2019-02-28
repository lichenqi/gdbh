package com.guodongbaohe.app.util;

import java.util.regex.Pattern;

/**
 * Created by tdc on 2018/1/24.
 */

public class PhoneUtils {
    public static Pattern IS_PHONE = Pattern.compile("^(1(3|4|5|7|8))\\d{9}$");
    public static Pattern IS_COLOR = Pattern.compile("^#[A-Za-z0-9]\\d{7}$");

    public static boolean isPhone(String number) {
        if (!IS_PHONE.matcher(number).matches()) {
            return false;
        } else {
            return true;
        }
    }

    public static boolean isColor(String number) {
        if (IS_COLOR.matcher(number).matches()) {
            return true;
        } else {
            return false;
        }
    }

}
