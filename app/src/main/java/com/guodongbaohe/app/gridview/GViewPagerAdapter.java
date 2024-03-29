package com.guodongbaohe.app.gridview;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;

public class GViewPagerAdapter extends PagerAdapter {
    private List<GridView> gridList;


    public GViewPagerAdapter () {
        gridList = new ArrayList<>();
    }

    public void add(List<GridView> datas) {
        if (gridList.size() > 0) {
            gridList.clear();
        }
        this.gridList=datas;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return gridList.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(gridList.get(position));
        return gridList.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
