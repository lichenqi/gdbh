package com.guodongbaohe.app.util;

import android.widget.TextView;

import java.text.NumberFormat;

public class StringCleanZeroUtil {
    public static void StringFormat(String content, TextView textView) {
        NumberFormat nf = NumberFormat.getInstance();
        String format = nf.format(Double.valueOf(content));
        textView.setText(format);
    }

    public static void StringFormatWithYuan(String content, TextView textView) {
        NumberFormat nf = NumberFormat.getInstance();
        String format = nf.format(Double.valueOf(content));
        textView.setText("¥" + format);
    }

    public static String DoubleFormat(Double money) {
        NumberFormat nf = NumberFormat.getInstance();
        String format = nf.format(money);
        return format;
    }
}
