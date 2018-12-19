package com.guodongbaohe.app.itemdecoration;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by tdc on 2018/1/19.
 */

public class ChooseBrandItemDecoration extends RecyclerView.ItemDecoration {
    private int space;

    public ChooseBrandItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.bottom = space;
    }

}
