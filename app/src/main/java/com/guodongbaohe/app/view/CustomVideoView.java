package com.guodongbaohe.app.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

public class CustomVideoView extends VideoView {

    /**
     * Created by john on 2017/7/13.
     */


        public CustomVideoView(Context context) {
            super(context);
        }

        public CustomVideoView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public CustomVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        @TargetApi(21)
        public CustomVideoView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//            //int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
//            int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
//            //int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
//            int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
//
//            setMeasuredDimension(widthSpecSize, heightSpecSize);
            int width = getDefaultSize(0, widthMeasureSpec);
            int height = getDefaultSize(0, heightMeasureSpec);

            setMeasuredDimension(width, height);
        }

}
