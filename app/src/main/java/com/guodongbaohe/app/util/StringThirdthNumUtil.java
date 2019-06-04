package com.guodongbaohe.app.util;

public class StringThirdthNumUtil {

    public static String getThirdthNum(String content) {
        String substring;
        if (content.length() > 3) {
            substring = content.substring( 0, 3 );
        } else {
            substring = content;
        }
        return substring;
    }

}
