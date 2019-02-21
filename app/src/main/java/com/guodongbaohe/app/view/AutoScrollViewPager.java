package com.guodongbaohe.app.view;

import android.content.Context;
import android.support.v4.view.ViewPager;

import java.lang.reflect.Field;

public class AutoScrollViewPager extends ViewPager {

    public AutoScrollViewPager(Context context) {
        super(context);
        initViewPagerScroll();
    }

    private void initViewPagerScroll() {
        try {
            Field mField = ViewPager.class.getDeclaredField("mScroller");
            mField.setAccessible(true);
            MyBannerScroller scroller = new MyBannerScroller(getContext());
            mField.set(this, scroller);
        } catch (Exception e) {
        }
    }

}
