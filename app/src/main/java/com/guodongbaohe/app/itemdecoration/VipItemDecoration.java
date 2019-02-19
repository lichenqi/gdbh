package com.guodongbaohe.app.itemdecoration;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;


public class VipItemDecoration extends RecyclerView.ItemDecoration {
    private int space;
    private int size;

    public VipItemDecoration(int space, int size) {
        this.space = space;
        this.size = size;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (parent.getChildLayoutPosition(view) == size - 1) {
            outRect.right = space;
        }
    }

}
