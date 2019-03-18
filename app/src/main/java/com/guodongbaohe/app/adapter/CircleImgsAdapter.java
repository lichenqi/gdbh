package com.guodongbaohe.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.guodongbaohe.app.R;
import com.guodongbaohe.app.activity.PicsLookActivity;
import com.guodongbaohe.app.activity.VideoPlayActivity;
import com.guodongbaohe.app.bean.EverydayHostGoodsBean;
import com.guodongbaohe.app.util.DensityUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CircleImgsAdapter extends RecyclerView.Adapter<CircleImgsAdapter.CircleImgsHolder> {
    private List<String> list_imgs;
    private Context context;
    private DisplayMetrics displayMetrics;
    private int width;
    private FragmentActivity activity;
    private String status,video;

    public CircleImgsAdapter(List<String> list_imgs, Context context, FragmentActivity activity, String status, String video_url) {
        this.list_imgs = list_imgs;
        this.context = context;
        this.activity = activity;
        this.status = status;
        this.video = video_url;
        displayMetrics = context.getResources().getDisplayMetrics();
        int dip2px = DensityUtils.dip2px(this.context, 95);
        width = (displayMetrics.widthPixels - dip2px) / 3;
    }

    @Override
    public CircleImgsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.circleimgsadapter, parent, false);
        return new CircleImgsHolder(view);
    }

    @Override
    public void onBindViewHolder(final CircleImgsHolder holder, final int position) {
        ViewGroup.LayoutParams layoutParams = holder.iv.getLayoutParams();
        ViewGroup.LayoutParams layoutParams1 = holder.v_go.getLayoutParams();
        layoutParams1.width = width;
        layoutParams1.height = width;
        layoutParams.width = width;
        layoutParams.height = width;
        holder.iv.setLayoutParams(layoutParams);
        holder.v_go.setLayoutParams(layoutParams1);
        Glide.with(context).load(list_imgs.get(position)).into(holder.iv);
        if (!TextUtils.isEmpty(video)) {
            holder.video_image.setVisibility(View.VISIBLE);
        } else {
            holder.video_image.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(status)) {
            if (Double.valueOf(status) > 0) {
                holder.v_go.setVisibility(View.GONE);
            } else {
                holder.v_go.setVisibility(View.VISIBLE);
            }
        } else {
            holder.v_go.setVisibility(View.VISIBLE);
        }
        holder.iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                if (!TextUtils.isEmpty(video)) {
                    intent = new Intent(context, VideoPlayActivity.class);
                    intent.putExtra("url", video);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                } else {
                    intent = new Intent(context, PicsLookActivity.class);
                    intent.putStringArrayListExtra("split", (ArrayList<String>) list_imgs);
                    intent.putExtra("position", position);
                    context.startActivity(intent);
                    activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list_imgs == null ? 0 : list_imgs.size();
    }

    public class CircleImgsHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv)
        ImageView iv;
        @BindView(R.id.v_go) //抢光了
        View v_go;
        @BindView(R.id.video_image)
        ImageView video_image;

        public CircleImgsHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
