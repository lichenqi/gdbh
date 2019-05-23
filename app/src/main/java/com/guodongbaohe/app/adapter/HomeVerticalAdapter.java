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

public class HomeVerticalAdapter extends RecyclerView.Adapter<HomeVerticalAdapter.HomeVerticalHolder> {

    private Context context;
    private List<HomeHorizontalListBean> verticalList;
    private OnItemClick onItemClick;
    private int height;

    public void setonclicklistener(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    public HomeVerticalAdapter(Context context, List<HomeHorizontalListBean> verticalList) {
        this.context = context;
        this.verticalList = verticalList;
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        height = (displayMetrics.widthPixels - DensityUtils.dip2px( context, 24 )) / 3 / 25 * 37;
    }

    @Override
    public HomeVerticalHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from( context ).inflate( R.layout.homeverticaladapter, parent, false );
        return new HomeVerticalHolder( view );
    }

    @Override
    public void onBindViewHolder(final HomeVerticalHolder holder, int position) {
        ViewGroup.LayoutParams layoutParams = holder.iv.getLayoutParams();
        layoutParams.height = height;
        holder.iv.setLayoutParams( layoutParams );
        Glide.with( context ).load( verticalList.get( position ).getImage() ).into( holder.iv );
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
        return verticalList == null ? 0 : verticalList.size();
    }

    public class HomeVerticalHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv)
        RoundedImageView iv;

        public HomeVerticalHolder(View itemView) {
            super( itemView );
            ButterKnife.bind( this, itemView );
        }
    }
}