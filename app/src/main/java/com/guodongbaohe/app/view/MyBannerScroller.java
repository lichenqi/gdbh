package com.guodongbaohe.app.view;

import android.content.Context;
import android.widget.Scroller;

public class MyBannerScroller extends Scroller {
    private static final int BANNER_DURATION = 2000;
    private int mDuration = BANNER_DURATION;

    public MyBannerScroller(Context context) {
        super(context);
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        super.startScroll(startX, startY, dx, dy, mDuration);
    }
}
