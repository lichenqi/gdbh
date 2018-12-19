package com.guodongbaohe.app.itemdecoration;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by tdc on 2018/1/10.
 */

public class HomeClassifrItemDecoration extends RecyclerView.ItemDecoration {
    private int space;

    public HomeClassifrItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.right = space;
        outRect.bottom = space;
        outRect.top = space;
    }

}
