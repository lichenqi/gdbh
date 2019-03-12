package com.guodongbaohe.app.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.guodongbaohe.app.R;
import com.guodongbaohe.app.activity.SearchActivity;
import com.guodongbaohe.app.adapter.ScrollLeftAdapter;
import com.guodongbaohe.app.adapter.ScrollRightAdapter;
import com.guodongbaohe.app.bean.CommonBean;
import com.guodongbaohe.app.bean.ScrollBean;
import com.guodongbaohe.app.util.SpUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LieBiaoFenLeiFragment extends Fragment {

    private View view;
    /*搜索布局*/
    @BindView(R.id.re_search_title)
    RelativeLayout re_search_title;
    /*搜索点击布局*/
    @BindView(R.id.re_search)
    RelativeLayout re_search;
    private RecyclerView recLeft;
    private RecyclerView recRight;
    private TextView rightTitle;

    private List<String> left;
    private List<ScrollBean> right;
     List<CommonBean.CommonSecond> right_data;
    private ScrollLeftAdapter leftAdapter;
    private ScrollRightAdapter rightAdapter;
    //右侧title在数据中所对应的position集合
    private List<Integer> tPosition = new ArrayList<>();
    private Context mContext;
    //title的高度
    private int tHeight;
    //记录右侧当前可见的第一个item的position
    private int first = 0;
    private GridLayoutManager rightManager;
    @Override
    public void onResume() {
        super.onResume();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.liebiaofenlei, container, false);
            ButterKnife.bind(this, view);
            mContext=view.getContext();
            recLeft = (RecyclerView) view.findViewById(R.id.rec_left);
            recRight = (RecyclerView) view.findViewById(R.id.rec_right);
            rightTitle = (TextView)view. findViewById(R.id.right_title);

            initData();

            initLeft();
            initRight();
            re_search.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getContext(), SearchActivity.class));
                }
            });
        }
        return view;
    }
    private void initRight() {

        rightManager = new GridLayoutManager(mContext, 3);

        if (rightAdapter == null) {
            rightAdapter = new ScrollRightAdapter(R.layout.scroll_right, R.layout.layout_right_title, null);
            recRight.setLayoutManager(rightManager);
            recRight.addItemDecoration(new RecyclerView.ItemDecoration() {
                @Override
                public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                    super.getItemOffsets(outRect, view, parent, state);
                    outRect.set(dpToPx(mContext, getDimens(mContext, R.dimen.dp3))
                            , 0
                            , dpToPx(mContext, getDimens(mContext, R.dimen.dp3))
                            , dpToPx(mContext, getDimens(mContext, R.dimen.dp3)));
                }
            });
            recRight.setAdapter(rightAdapter);
        } else {
            rightAdapter.notifyDataSetChanged();
        }

        rightAdapter.setNewData(right);

        //设置右侧初始title
        if (right.get(first).isHeader) {
            rightTitle.setText(right.get(first).header);
        }

        recRight.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //获取右侧title的高度
                tHeight = rightTitle.getHeight();
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                //判断如果是header
                if (right.get(first).isHeader) {
                    //获取此组名item的view
                    View view = rightManager.findViewByPosition(first);
                    if (view != null) {
                        //如果此组名item顶部和父容器顶部距离大于等于title的高度,则设置偏移量
                        if (view.getTop() >= tHeight) {
                            rightTitle.setY(view.getTop() - tHeight);
                        } else {
                            //否则不设置
                            rightTitle.setY(0);
                        }
                    }
                }

                //因为每次滑动之后,右侧列表中可见的第一个item的position肯定会改变,并且右侧列表中可见的第一个item的position变换了之后,
                //才有可能改变右侧title的值,所以这个方法内的逻辑在右侧可见的第一个item的position改变之后一定会执行
                int firstPosition = rightManager.findFirstVisibleItemPosition();
                if (first != firstPosition && firstPosition >= 0) {
                    //给first赋值
                    first = firstPosition;
                    //不设置Y轴的偏移量
                    rightTitle.setY(0);

                    //判断如果右侧可见的第一个item是否是header,设置相应的值
                    if (right.get(first).isHeader) {
                        rightTitle.setText(right.get(first).header);
                    } else {
//                        rightTitle.setText(right.get(first).t.getType());
                    }
                }

                //遍历左边列表,列表对应的内容等于右边的title,则设置左侧对应item高亮
                for (int i = 0; i < left.size(); i++) {
                    if (left.get(i).equals(rightTitle.getText().toString())) {
                        leftAdapter.selectItem(i);
                    }
                }

                //如果右边最后一个完全显示的item的position,等于bean中最后一条数据的position(也就是右侧列表拉到底了),
                //则设置左侧列表最后一条item高亮
                if (rightManager.findLastCompletelyVisibleItemPosition() == right.size() - 1) {
                    leftAdapter.selectItem(left.size() - 1);
                }
            }
        });
    }

    private void initLeft() {
        if (leftAdapter == null) {
            leftAdapter = new ScrollLeftAdapter(R.layout.scroll_left, null);
            recLeft.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
            recLeft.setAdapter(leftAdapter);
        } else {
            leftAdapter.notifyDataSetChanged();
        }

        leftAdapter.setNewData(left);

        leftAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    //点击左侧列表的相应item,右侧列表相应的title置顶显示
                    //(最后一组内容若不能填充右侧整个可见页面,则显示到右侧列表的最底端)
                    case R.id.item:
                        leftAdapter.selectItem(position);
                        rightManager.scrollToPositionWithOffset(tPosition.get(position), 0);
                        break;
                }
            }
        });
    }
    List<CommonBean.CommonResult> titleList;
    //获取数据(若请求服务端数据,请求到的列表需有序排列)
    private void initData() {
        titleList=SpUtil.getList(getContext(),"head_title_list");
        right_data=new ArrayList<>();
        left = new ArrayList<>();
        for (int i=0;i<titleList.size();i++){
                if (i!=0){
                    left.add(titleList.get(i).getName());
                    right_data.addAll(titleList.get(i).getChild());

                }
        }

        right = new ArrayList<>();
for (int i=0;i<titleList.size();i++){
    if (i!=0){
        right.add(new ScrollBean(true,titleList.get(i).getName()));
        for (int j=0;j<titleList.get(i).getChild().size();j++){
            right.add(new ScrollBean(new ScrollBean.ScrollItemBean(titleList.get(i).getChild().get(j).getName(), titleList.get(i).getChild().get(j).getThumb())));
        }
    }
}
//        for (int i=0;i<left.size();i++){
//                right.add(new ScrollBean(true, left.get(i)));
//                for (int j=0;j<right_data.size();j++){
//                    right.add(new ScrollBean(new ScrollBean.ScrollItemBean(right_data.get(i).getName(), right_data.get(j).getThumb())));
//                }
//        }

//        right.add(new ScrollBean(true, left.get(0)));
//        right.add(new ScrollBean(new ScrollBean.ScrollItemBean("1111111", left.get(0))));
//        right.add(new ScrollBean(new ScrollBean.ScrollItemBean("1111112", left.get(0))));
//        right.add(new ScrollBean(new ScrollBean.ScrollItemBean("1111113", left.get(0))));
//        right.add(new ScrollBean(new ScrollBean.ScrollItemBean("1111114", left.get(0))));
//
//        right.add(new ScrollBean(true, left.get(1)));
//        right.add(new ScrollBean(new ScrollBean.ScrollItemBean("2222222", left.get(1))));
//        right.add(new ScrollBean(new ScrollBean.ScrollItemBean("2222222", left.get(1))));
//        right.add(new ScrollBean(new ScrollBean.ScrollItemBean("2222222", left.get(1))));
//        right.add(new ScrollBean(new ScrollBean.ScrollItemBean("2222222", left.get(1))));
//        right.add(new ScrollBean(new ScrollBean.ScrollItemBean("2222222", left.get(1))));
//        right.add(new ScrollBean(new ScrollBean.ScrollItemBean("2222222", left.get(1))));
//        right.add(new ScrollBean(new ScrollBean.ScrollItemBean("2222222", left.get(1))));
//
//        right.add(new ScrollBean(true, left.get(2)));
//        right.add(new ScrollBean(new ScrollBean.ScrollItemBean("3333333", left.get(2))));
//        right.add(new ScrollBean(new ScrollBean.ScrollItemBean("3333333", left.get(2))));
//        right.add(new ScrollBean(new ScrollBean.ScrollItemBean("3333333", left.get(2))));
//        right.add(new ScrollBean(new ScrollBean.ScrollItemBean("3333333", left.get(2))));
//        right.add(new ScrollBean(new ScrollBean.ScrollItemBean("3333333", left.get(2))));
//        right.add(new ScrollBean(new ScrollBean.ScrollItemBean("3333333", left.get(2))));
//
//        right.add(new ScrollBean(true, left.get(3)));
//        right.add(new ScrollBean(new ScrollBean.ScrollItemBean("4444444", left.get(3))));
//        right.add(new ScrollBean(new ScrollBean.ScrollItemBean("4444444", left.get(3))));
//        right.add(new ScrollBean(new ScrollBean.ScrollItemBean("4444444", left.get(3))));
//        right.add(new ScrollBean(new ScrollBean.ScrollItemBean("4444444", left.get(3))));
//        right.add(new ScrollBean(new ScrollBean.ScrollItemBean("4444444", left.get(3))));
//        right.add(new ScrollBean(new ScrollBean.ScrollItemBean("4444444", left.get(3))));
//        right.add(new ScrollBean(new ScrollBean.ScrollItemBean("4444444", left.get(3))));
//        right.add(new ScrollBean(new ScrollBean.ScrollItemBean("4444444", left.get(3))));
//        right.add(new ScrollBean(new ScrollBean.ScrollItemBean("4444444", left.get(3))));
//
//        right.add(new ScrollBean(true, left.get(4)));
//        right.add(new ScrollBean(new ScrollBean.ScrollItemBean("5555555", left.get(4))));
//        right.add(new ScrollBean(new ScrollBean.ScrollItemBean("5555555", left.get(4))));
//        right.add(new ScrollBean(new ScrollBean.ScrollItemBean("5555555", left.get(4))));
//        right.add(new ScrollBean(new ScrollBean.ScrollItemBean("5555555", left.get(4))));
//        right.add(new ScrollBean(new ScrollBean.ScrollItemBean("5555555", left.get(4))));
//        right.add(new ScrollBean(new ScrollBean.ScrollItemBean("5555555", left.get(4))));
//        right.add(new ScrollBean(new ScrollBean.ScrollItemBean("5555555", left.get(4))));
//        right.add(new ScrollBean(new ScrollBean.ScrollItemBean("5555555", left.get(4))));
//        right.add(new ScrollBean(new ScrollBean.ScrollItemBean("5555555", left.get(4))));
//        right.add(new ScrollBean(new ScrollBean.ScrollItemBean("5555555", left.get(4))));
//        right.add(new ScrollBean(new ScrollBean.ScrollItemBean("5555555", left.get(4))));
//        right.add(new ScrollBean(new ScrollBean.ScrollItemBean("5555555", left.get(4))));
//        right.add(new ScrollBean(new ScrollBean.ScrollItemBean("5555555", left.get(4))));
//        right.add(new ScrollBean(new ScrollBean.ScrollItemBean("5555555", left.get(4))));
//
//        right.add(new ScrollBean(true, left.get(5)));
//        right.add(new ScrollBean(new ScrollBean.ScrollItemBean("6666666", left.get(5))));
//        right.add(new ScrollBean(new ScrollBean.ScrollItemBean("6666666", left.get(5))));
//        right.add(new ScrollBean(new ScrollBean.ScrollItemBean("6666666", left.get(5))));
//        right.add(new ScrollBean(new ScrollBean.ScrollItemBean("6666666", left.get(5))));
//        right.add(new ScrollBean(new ScrollBean.ScrollItemBean("6666666", left.get(5))));
//        right.add(new ScrollBean(new ScrollBean.ScrollItemBean("6666666", left.get(5))));
//        right.add(new ScrollBean(new ScrollBean.ScrollItemBean("6666666", left.get(5))));
//        right.add(new ScrollBean(new ScrollBean.ScrollItemBean("6666666", left.get(5))));
//        right.add(new ScrollBean(new ScrollBean.ScrollItemBean("6666666", left.get(5))));
//        right.add(new ScrollBean(new ScrollBean.ScrollItemBean("6666666", left.get(5))));
//        right.add(new ScrollBean(new ScrollBean.ScrollItemBean("6666666", left.get(5))));
//        right.add(new ScrollBean(new ScrollBean.ScrollItemBean("6666666", left.get(5))));
//        right.add(new ScrollBean(new ScrollBean.ScrollItemBean("6666666", left.get(5))));
//
//        right.add(new ScrollBean(true, left.get(6)));
//        right.add(new ScrollBean(new ScrollBean.ScrollItemBean("7777777", left.get(6))));
//        right.add(new ScrollBean(new ScrollBean.ScrollItemBean("7777777", left.get(6))));
//        right.add(new ScrollBean(new ScrollBean.ScrollItemBean("7777777", left.get(6))));
//        right.add(new ScrollBean(new ScrollBean.ScrollItemBean("7777777", left.get(6))));

        for (int i = 0; i < right.size(); i++) {
            if (right.get(i).isHeader) {
                //遍历右侧列表,判断如果是header,则将此header在右侧列表中所在的position添加到集合中
                tPosition.add(i);
                Log.i("list+++++",i+"");
            }
        }

    }

    /**
     * 获得资源 dimens (dp)
     *
     * @param context
     * @param id      资源id
     * @return
     */
    public float getDimens(Context context, int id) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        float px = context.getResources().getDimension(id);
        return px / dm.density;
    }

    /**
     * dp转px
     *
     * @param context
     * @param dp
     * @return
     */
    public int dpToPx(Context context, float dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return (int) ((dp * displayMetrics.density) + 0.5f);
    }
}
