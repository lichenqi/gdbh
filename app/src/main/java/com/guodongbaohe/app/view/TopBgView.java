package com.guodongbaohe.app.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.guodongbaohe.app.R;

public class TopBgView extends View {

    private Paint mPaint;
    private int centerX, centerY;
    private PointF start, end, control;
    private int color = R.color.red;

    public TopBgView(Context context) {
        super(context);
    }

    public TopBgView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        mPaint = new Paint();
        mPaint.setColor(context.getResources().getColor(color));
        mPaint.setStrokeWidth(1);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);

        start = new PointF(0,0);
        end = new PointF(0,0);
        control = new PointF(0,0);
    }

    public TopBgView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        centerX = w;
        start.x = 0;
        start.y = h;
        end.x = w;
        end.y = h; //
        control.x = w/2;
        control.y = h*(4f/5f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBG(canvas);
    }
    // 绘制头部的背景图片 贝塞尔曲线的下边
    private void drawBG(Canvas canvas){
        Path path = new Path();
        path.moveTo(start.x,start.y);
        path.quadTo(control.x,control.y,end.x,end.y);
        path.lineTo(centerX,0);
        path.lineTo(0,0);
        canvas.drawPath(path, mPaint);
    }

    public void setColor(int color){
        this.color = color;
        mPaint.setColor(getResources().getColor(color));
        invalidate();
    }

    public void setColor(String color){
        mPaint.setColor(Color.parseColor(color));
        invalidate();
    }
}
