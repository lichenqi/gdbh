package com.guodongbaohe.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.guodongbaohe.app.OnItemClick;
import com.guodongbaohe.app.R;
import com.guodongbaohe.app.bean.HomeHorizontalListBean;
import com.guodongbaohe.app.util.DensityUtils;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeHorizontalAdapter extends RecyclerView.Adapter<HomeHorizontalAdapter.HomeHorizontalHolder> {

    private Context context;
    private List<HomeHorizontalListBean> horizontalList;
    private OnItemClick onItemClick;
    int height;

    public void setonclicklistener(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    public HomeHorizontalAdapter(Context context, List<HomeHorizontalListBean> horizontalList) {
        this.context = context;
        this.horizontalList = horizontalList;
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        height = (displayMetrics.widthPixels - DensityUtils.dip2px( context, 18 )) * 9 / 40;
    }

    @Override
    public HomeHorizontalHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from( context ).inflate( R.layout.homehorizontalholder, parent, false );
        return new HomeHorizontalHolder( view );
    }

    @Override
    public void onBindViewHolder(final HomeHorizontalHolder holder, int position) {
        ViewGroup.LayoutParams layoutParams = holder.iv.getLayoutParams();
        layoutParams.height = height;
        holder.iv.setLayoutParams( layoutParams );
        Glide.with( context ).load( horizontalList.get( position ).getImage() ).into( holder.iv );
        if (onItemClick != null) {
            holder.itemView.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClick.OnItemClickListener( holder.itemView, holder.getAdapterPosition() );
                }
            } );
        }
    }

    @Override
    public int getItemCount() {
        return horizontalList == null ? 0 : horizontalList.size();
    }

    public class HomeHorizontalHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv)
        RoundedImageView iv;

        public HomeHorizontalHolder(View itemView) {
            super( itemView );
            ButterKnife.bind( this, itemView );
        }
    }
}
