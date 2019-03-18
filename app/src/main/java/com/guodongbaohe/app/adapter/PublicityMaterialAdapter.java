package com.guodongbaohe.app.adapter;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.guodongbaohe.app.OnItemClick;
import com.guodongbaohe.app.R;
import com.guodongbaohe.app.bean.EverydayHostGoodsBean;
import com.guodongbaohe.app.util.TimeShowUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PublicityMaterialAdapter extends RecyclerView.Adapter<PublicityMaterialAdapter.PublicMaterialHolder> {
    private FragmentActivity activity;
    private Context context;
    private List<EverydayHostGoodsBean.GoodsList> list;
    private OnItemClick onItemClick, onLongClick, onFuZhiClick;
    private List<String> list_imgs;

    public PublicityMaterialAdapter(FragmentActivity activity, Context context, List<EverydayHostGoodsBean.GoodsList> list) {
        this.activity = activity;
        this.context = context;
        this.list = list;
    }

    public void setonShareListener(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    public void setonlongclicklistener(OnItemClick onLongClick) {
        this.onLongClick = onLongClick;
    }

    public void setonFuZhiClickListener(OnItemClick onFuZhiClick) {
        this.onFuZhiClick = onFuZhiClick;
    }

    @Override
    public PublicMaterialHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.publicitymaterialadapter, parent, false);
        return new PublicMaterialHolder(view);
    }

    @Override
    public void onBindViewHolder(final PublicMaterialHolder holder, int position) {
        String status = list.get(position).getStatus();
        String video_url = list.get(position).getVideo();
        holder.share_nums.setText("分享" + list.get(position).getTimes());
        holder.title.setText(list.get(position).getContent());
        holder.time.setText(TimeShowUtil.getTimeShow(list.get(position).getDateline(), System.currentTimeMillis()));
        String img = list.get(position).getGoods_gallery();
        list_imgs = new ArrayList<>();
        if (img.contains("||")) {
            String[] imgs = img.replace("||", ",").split(",");
            for (int i = 0; i < imgs.length; i++) {
                list_imgs.add(imgs[i]);
            }
        } else {
            list_imgs.add(img);
        }
        holder.recyclerview.setHasFixedSize(true);
        holder.recyclerview.setLayoutManager(new GridLayoutManager(context, 3));
        CircleImgsAdapter circleImgsAdapter = new CircleImgsAdapter(list_imgs, context, activity, status, video_url);
        holder.recyclerview.setAdapter(circleImgsAdapter);
        String comment = list.get(position).getComment();
        if (TextUtils.isEmpty(comment)) {
            holder.re_taokouling_buju.setVisibility(View.GONE);
        } else {
            holder.re_taokouling_buju.setVisibility(View.VISIBLE);
            holder.tv_kouling_wenben.setText(list.get(position).getComment());
        }
        if (onItemClick != null) {
            holder.re_share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClick.OnItemClickListener(holder.re_share, holder.getAdapterPosition());
                }
            });
        }
        if (onItemClick != null) {
            holder.title.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onLongClick.OnItemClickListener(holder.title, holder.getAdapterPosition());
                    return false;
                }
            });
        }
        if (onItemClick != null) {
            holder.re_fuzhi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onFuZhiClick.OnItemClickListener(holder.re_fuzhi, holder.getAdapterPosition());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public class PublicMaterialHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.recyclerview)
        RecyclerView recyclerview;
        @BindView(R.id.time)
        TextView time;
        @BindView(R.id.re_share)
        RelativeLayout re_share;
        @BindView(R.id.share_nums)
        TextView share_nums;
        @BindView(R.id.tv_kouling_wenben)
        TextView tv_kouling_wenben;
        @BindView(R.id.re_taokouling_buju)
        RelativeLayout re_taokouling_buju;
        @BindView(R.id.re_fuzhi)
        RelativeLayout re_fuzhi;

        public PublicMaterialHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
