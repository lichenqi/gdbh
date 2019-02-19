package com.guodongbaohe.app.itemdecoration;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class TimeItemDecoration extends RecyclerView.ItemDecoration {
    private int space;
    private int vertical_space;

    public TimeItemDecoration(int space, int vertical_space) {
        this.space = space;
        this.vertical_space = vertical_space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.right = space;
        outRect.bottom = vertical_space;
        if (parent.getChildLayoutPosition(view) % 4 == 0) {
            outRect.left = space;
        }
    }
}
