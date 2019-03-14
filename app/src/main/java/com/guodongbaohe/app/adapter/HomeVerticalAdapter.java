package com.guodongbaohe.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.guodongbaohe.app.R;
import com.guodongbaohe.app.bean.HomeHorizontalListBean;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeVerticalAdapter extends RecyclerView.Adapter<HomeVerticalAdapter.HomeVerticalHolder> {

    private Context context;
    private List<HomeHorizontalListBean> verticalList;

    public HomeVerticalAdapter(Context context, List<HomeHorizontalListBean> verticalList) {
        this.context = context;
        this.verticalList = verticalList;
    }

    @Override
    public HomeVerticalHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.homeverticaladapter, parent, false);
        return new HomeVerticalHolder(view);
    }

    @Override
    public void onBindViewHolder(HomeVerticalHolder holder, int position) {
        Glide.with(context).load(verticalList.get(position).getImage()).into(holder.iv);
    }

    @Override
    public int getItemCount() {
        return verticalList == null ? 0 : verticalList.size();
    }

    public class HomeVerticalHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv)
        RoundedImageView iv;

        public HomeVerticalHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}