package com.guodongbaohe.app.itemdecoration;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

/**
 * Created by Administrator on 2018/5/4.
 */

public class PinLeiItemDecoration extends RecyclerView.ItemDecoration {
    private int space;
    private List<String> list;

    public PinLeiItemDecoration(int space, List<String> list) {
        this.space = space;
        this.list = list;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (parent.getChildLayoutPosition(view) == (list.size() - 1)) {/*代表最后一个item*/
            outRect.right = space;
        }
    }

}
