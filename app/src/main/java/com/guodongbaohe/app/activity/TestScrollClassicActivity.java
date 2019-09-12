package com.guodongbaohe.app.activity;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.bumptech.glide.Glide;
import com.guodongbaohe.app.R;
import com.guodongbaohe.app.base_activity.BaseActivity;
import com.guodongbaohe.app.util.DensityUtils;
import com.guodongbaohe.app.util.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TestScrollClassicActivity extends BaseActivity {
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.seekbar)
    SeekBar seekbar;
    int widthPixels;

    @Override
    public int getContainerView() {
        return R.layout.testscrollclassicactivity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setMiddleTitle( "滑动分类效果" );
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        widthPixels = displayMetrics.widthPixels / 5;
        ButterKnife.bind( this );
        recyclerview.setHasFixedSize( true );
        GridLayoutManager manager = new GridLayoutManager( getApplicationContext(), 2, GridLayoutManager.HORIZONTAL, false );
        recyclerview.setLayoutManager( manager );
        recyclerview.setAdapter( new MyAdapter() );
        seekbar.setPadding( 0, 0, 0, 0 );
        seekbar.setThumbOffset( 0 );
        recyclerview.addOnScrollListener( new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged( recyclerView, newState );
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled( recyclerView, dx, dy );
                //显示区域的高度。
                int extent = recyclerView.computeHorizontalScrollExtent();
                //整体的高度，注意是整体，包括在显示区域之外的。
                int range = recyclerView.computeHorizontalScrollRange();
                //已经向下滚动的距离，为0时表示已处于顶部。
                int offset = recyclerView.computeHorizontalScrollOffset();
                //此处获取seekbar的getThumb，就是可以滑动的小的滚动游标
                GradientDrawable gradientDrawable = (GradientDrawable) seekbar.getThumb();
                //根据列表的个数，动态设置游标的大小，设置游标的时候，progress进度的颜色设置为和seekbar的颜色设置的一样的，所以就不显示进度了。
                gradientDrawable.setSize( extent / (20 / 2), DensityUtils.dip2px( getApplicationContext(), 5 ) );
                //设置可滚动区域
                seekbar.setMax( range - extent );
                if (dx == 0) {
                    seekbar.setProgress( 0 );
                } else if (dx > 0) {
                    seekbar.setProgress( offset );
                } else if (dx < 0) {
                    seekbar.setProgress( offset );
                }
            }
        } );
    }

    public class MyAdapter extends RecyclerView.Adapter<MyHolder> {

        @NonNull
        @Override
        public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from( getApplicationContext() ).inflate( R.layout.scroll_classic_item, viewGroup, false );
            return new MyHolder( view );
        }

        @Override
        public void onBindViewHolder(@NonNull MyHolder myHolder, int i) {
            ViewGroup.LayoutParams layoutParams = myHolder.ll_parent.getLayoutParams();
            layoutParams.width = widthPixels;
            layoutParams.height = widthPixels;
            myHolder.ll_parent.setLayoutParams( layoutParams );
            Glide.with( getApplicationContext() ).load( "https://assets.mopland.com/image/2019/0620/5d0babe5bd812.png" ).into( myHolder.iv );
            myHolder.ll_parent.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ToastUtils.showToast( getApplicationContext(), i + "" );
                }
            } );
        }

        @Override
        public int getItemCount() {
            return 20;
        }
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ll_parent)
        LinearLayout ll_parent;
        @BindView(R.id.iv)
        ImageView iv;

        public MyHolder(@NonNull View itemView) {
            super( itemView );
            ButterKnife.bind( this, itemView );
        }
    }

}
