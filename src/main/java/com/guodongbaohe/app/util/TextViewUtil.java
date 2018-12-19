package com.guodongbaohe.app.util;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.widget.TextView;

public class TextViewUtil {
    /*Android 中同一个TextView设置不同大小字体*/
    public static void setTextViewSize(String content, TextView textView) {
        Spannable sp = new SpannableString(content);
        sp.setSpan(new AbsoluteSizeSpan(12, true), 0, 4, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        sp.setSpan(new AbsoluteSizeSpan(14, true), 3, content.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        textView.setText(sp);
    }
}
