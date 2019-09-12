package com.guodongbaohe.app.util;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

public class TextViewUtil {
    /*Android 中同一个TextView设置不同大小字体*/
    public static void setTextViewSize(String content, TextView textView) {
        Spannable sp = new SpannableString( content );
        sp.setSpan( new AbsoluteSizeSpan( 12, true ), 0, 4, Spannable.SPAN_INCLUSIVE_INCLUSIVE );
        sp.setSpan( new AbsoluteSizeSpan( 13, true ), 3, content.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE );
        textView.setText( sp );
    }

    /*同一个textview设置不同字体大小颜色*/
    public static void setTextViewColorAndSize(String content, TextView tv_content) {
        SpannableString sp = new SpannableString( content );
        sp.setSpan( new ForegroundColorSpan( Color.parseColor( "#ff3831" ) ), 6, content.length() - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE );
        sp.setSpan( new AbsoluteSizeSpan( 18, true ), 6, content.length() - 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE );
        tv_content.setText( sp );
    }
}
