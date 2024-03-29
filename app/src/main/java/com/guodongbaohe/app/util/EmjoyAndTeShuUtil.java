package com.guodongbaohe.app.util;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmjoyAndTeShuUtil {
    /**
     * 判断字符串中是否包含有表情存在
     *
     * @param source
     * @return
     */
//    public static boolean containsEmoji(String source) {
//        int len = source.length();
//        boolean isEmoji = false;
//        for (int i = 0; i < len; i++) {
//            char hs = source.charAt(i);
//            if (0xd800 <= hs && hs <= 0xdbff) {
//                if (source.length() > 1) {
//                    char ls = source.charAt(i + 1);
//                    int uc = ((hs - 0xd800) * 0x400) + (ls - 0xdc00) + 0x10000;
//                    if (0x1d000 <= uc && uc <= 0x1f77f) {
//                        return true;
//                    }
//                }
//            } else {
//                if (0x2100 <= hs && hs <= 0x27ff && hs != 0x263b) {
//                    return true;
//                } else if (0x2B05 <= hs && hs <= 0x2b07) {
//                    return true;
//                } else if (0x2934 <= hs && hs <= 0x2935) {
//                    return true;
//                } else if (0x3297 <= hs && hs <= 0x3299) {
//                    return true;
//                } else if (hs == 0xa9 || hs == 0xae || hs == 0x303d || hs == 0x3030 || hs == 0x2b55 || hs == 0x2b1c
//                        || hs == 0x2b1b || hs == 0x2b50 || hs == 0x231a) {
//                    return true;
//                }
//                if (!isEmoji && source.length() > 1 && i < source.length() - 1) {
//                    char ls = source.charAt(i + 1);
//                    if (ls == 0x20e3) {
//                        return true;
//                    }
//                }
//            }
//        }
//        return isEmoji;
//    }

    /**
     * 判断是否含有特殊字符
     *
     * @param str
     * @return true为包含，false为不包含
     */
    public static boolean isSpecialChar(String str) {
        String regEx = "[ _`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]|\n|\r|\t";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.find();
    }

    public static String DEFAULT_QUERY_REGEX = "[!$^&*+=|{}';'\",<>/?~！#￥%……&*——|{}【】‘；：”“'。，、？]";

    /*判断字符是否包含数字*/
    public static boolean isDigit(String str) {
        boolean isDigit = false;
        if (!TextUtils.isEmpty(str)) {
            for (int i = 0; i < str.length(); i++) {
                if (Character.isDigit(str.charAt(i))) {
                    isDigit = true;
                }
            }
        }
        return isDigit;
    }

    /*判断字符是否包含字母*/
    public static boolean isLetter(String str) {
        boolean isLetter = false;
        for (int i = 0; i < str.length(); i++) {
            if (Character.isLetter(str.charAt(i))) {
                isLetter = true;
            }
        }
        return isLetter;
    }

    /*是否为汉字*/
    public static boolean isContainChinese(String str) {
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }

    /**
     * check 'emoji' isExit
     *
     * @param source
     * @return
     */
    public static boolean containsEmoji(String source) {
        int len = source.length();
        for (int i = 0; i < len; i++) {
            char codePoint = source.charAt(i);
            if (!isEmojiCharacter(codePoint)) {
                return true;
            }
        }
        return false;
    }

    /**
     * check is or not 'emoji'
     *
     * @param codePoint
     * @return
     */
    private static boolean isEmojiCharacter(char codePoint) {
        return (codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA) ||
                (codePoint == 0xD) || ((codePoint >= 0x20) && (codePoint <= 0xD7FF)) ||
                ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) || ((codePoint >= 0x10000)
                && (codePoint <= 0x10FFFF));
    }

}



