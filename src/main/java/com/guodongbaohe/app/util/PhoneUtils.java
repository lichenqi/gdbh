package com.guodongbaohe.app.util;

import java.util.regex.Pattern;

/**
 * Created by tdc on 2018/1/24.
 */

public class PhoneUtils {
    public static Pattern IS_PHONE = Pattern.compile("^(1(3|4|5|7|8))\\d{9}$");

    public static boolean isPhone(String number) {
        if (!IS_PHONE.matcher(number).matches()) {
            return false;
        } else {
            return true;
        }
    }
}
