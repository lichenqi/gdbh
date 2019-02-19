package com.guodongbaohe.app.itemdecoration;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Administrator on 2018/4/28.
 */

public class PicsBaseItem extends RecyclerView.ItemDecoration {
    private int space;

    public PicsBaseItem(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.right = space;
        outRect.bottom = space;
    }

}
