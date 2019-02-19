package com.guodongbaohe.app.itemdecoration;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by tdc on 2018/1/29.
 */

public class ColorItemDecoration extends RecyclerView.ItemDecoration {
    private int space;

    public ColorItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (parent.getChildLayoutPosition(view) / 4 != 1) {
            outRect.top = space;
        }
    }
}
