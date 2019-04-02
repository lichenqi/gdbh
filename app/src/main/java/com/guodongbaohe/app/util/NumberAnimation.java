package com.guodongbaohe.app.util;

import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.TextView;

/*数字渐变显示工具类*/
public class NumberAnimation extends Animation {

    TextView view;
    int from, to;
    int cha;

    public NumberAnimation(TextView v) {
        view = v;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        super.applyTransformation(interpolatedTime, t);
        if (interpolatedTime < 1.0f) {
            if (from != to) {//当没有达到要显示的新值时，持续更新textview
                if (cha > 0) {
                    from++;
                    view.setText(from + "%");
                } else {
                    from--;
                    view.setText(from + "%");
                }
            }
        }
    }

    // 数字从from逐渐变化到to
    public void setNum(int from, int to) {
        this.from = from;
        this.to = to;
        cha = to - from;
        NumberAnimation.this.setDuration(5 * 1000);//持续时间
        view.startAnimation(NumberAnimation.this);
    }
}

