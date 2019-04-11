package com.guodongbaohe.app.adapter;

import android.animation.Animator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.guodongbaohe.app.R;
import com.guodongbaohe.app.recyclerview_animation.AlphaInAnimation;
import com.guodongbaohe.app.recyclerview_animation.BaseAnimation;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShopDynamicDiaplayAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private int imageItemType = 1;/*图片布局*/
    private int textItemType = 2;/*文字布局*/
    private String imageUrl = "https://img.alicdn.com/i1/1133873553/O1CN01pr9dYc1c7Jip5N9l7_!!0-item_pic.jpg";
    private boolean mOpenAnimationEnable = true;
    private BaseAnimation mSelectAnimation = new AlphaInAnimation();
    private int mDuration = 2000;
    private Interpolator mInterpolator = new LinearInterpolator();
    private int mLastPosition = -1;
    private int listSize;

    public void addLitSize(int listSize) {
        this.listSize = listSize;
        notifyDataSetChanged();
    }

    public ShopDynamicDiaplayAdapter(Context context, int listSize) {
        this.context = context;
        this.listSize = listSize;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == imageItemType) {
            view = LayoutInflater.from(context).inflate(R.layout.shopdynamicdiaplayadapter, parent, false);
            return new ShopDynamicDiaplayHolder(view);
        } else if (viewType == textItemType) {
            view = LayoutInflater.from(context).inflate(R.layout.shopdynamicdiaplayadapter_text, parent, false);
            return new ShopDynamicDiaplayHolderOfText(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ShopDynamicDiaplayHolder) {
            ((ShopDynamicDiaplayHolder) holder).iv.setImageResource(R.mipmap.green_choose);
            Glide.with(context).load(imageUrl).into(((ShopDynamicDiaplayHolder) holder).iv);
        } else if (holder instanceof ShopDynamicDiaplayHolderOfText) {
            ((ShopDynamicDiaplayHolderOfText) holder).text.setText("iOS阿检查评价是否名称我没测名称吃饭排污费传媒网吃产品摩擦我出门出门 从莫测免费吗吃吃二次");
        }
        if (mOpenAnimationEnable) {
            addAnimation(holder);
        }
    }

    @Override
    public int getItemCount() {
        return listSize == 0 ? 0 : listSize;
    }

    @Override
    public int getItemViewType(int position) {
        if (position % 2 == 0) {
            return imageItemType;
        } else {
            return textItemType;
        }
    }

    /*添加动画*/
    public void addAnimation(RecyclerView.ViewHolder holder) {
        if (mOpenAnimationEnable) {
            if (holder.getLayoutPosition() > mLastPosition) {
                BaseAnimation animation = null;
                if (mSelectAnimation != null) {
                    animation = mSelectAnimation;
                }
                for (Animator anim : animation.getAnimators(holder.itemView)) {
                    startAnim(anim);
                }
                mLastPosition = holder.getLayoutPosition();
            }
        }
    }

    /*设置动画效果*/
    public void setAnimation(BaseAnimation animation) {
        this.mOpenAnimationEnable = true;
        this.mSelectAnimation = animation;
    }

    /*开启动画*/
    private void startAnim(Animator animator) {
        animator.setDuration(mDuration).start();
        animator.setInterpolator(mInterpolator);
    }

    public class ShopDynamicDiaplayHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv)
        ImageView iv;

        public ShopDynamicDiaplayHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class ShopDynamicDiaplayHolderOfText extends RecyclerView.ViewHolder {
        @BindView(R.id.text)
        TextView text;

        public ShopDynamicDiaplayHolderOfText(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
