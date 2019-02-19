package com.guodongbaohe.app.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.RadioButton;

import com.guodongbaohe.app.R;
import com.guodongbaohe.app.util.DensityUtils;


@SuppressLint("AppCompatCustomView")
public class MyRadioButton extends RadioButton {
    private float mImg_width;
    private float mImg_height;

    public MyRadioButton(Context context) {
        super(context);
    }

    public MyRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray t = context.obtainStyledAttributes(attrs, R.styleable.MyRadioButton);
        mImg_width = t.getDimension(R.styleable.MyRadioButton_rb_width, DensityUtils.dip2px(context, 25));
        mImg_height = t.getDimension(R.styleable.MyRadioButton_rb_height, DensityUtils.dip2px(context, 25));
        t.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //让RadioButton的图标可调大小 属性：
        Drawable drawableLeft = this.getCompoundDrawables()[0];//获得文字左侧图片
        Drawable drawableTop = this.getCompoundDrawables()[1];//获得文字顶部图片
        Drawable drawableRight = this.getCompoundDrawables()[2];//获得文字右侧图片
        Drawable drawableBottom = this.getCompoundDrawables()[3];//获得文字底部图片
        if (drawableLeft != null) {
            drawableLeft.setBounds(0, 0, (int) mImg_width, (int) mImg_height);
            this.setCompoundDrawables(drawableLeft, null, null, null);
        }
        if (drawableRight != null) {
            drawableRight.setBounds(0, 0, (int) mImg_width, (int) mImg_height);
            this.setCompoundDrawables(null, null, drawableRight, null);
        }
        if (drawableTop != null) {
            drawableTop.setBounds(0, 0, (int) mImg_width, (int) mImg_height);
            this.setCompoundDrawables(null, drawableTop, null, null);
        }
        if (drawableBottom != null) {
            drawableBottom.setBounds(0, 0, (int) mImg_width, (int) mImg_height);
            this.setCompoundDrawables(null, null, null, drawableBottom);
        }
    }
}
