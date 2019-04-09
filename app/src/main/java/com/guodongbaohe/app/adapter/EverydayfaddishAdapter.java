package com.guodongbaohe.app.adapter;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.guodongbaohe.app.OnItemClick;
import com.guodongbaohe.app.R;
import com.guodongbaohe.app.bean.EverydayHostGoodsBean;
import com.guodongbaohe.app.common_constant.Constant;
import com.guodongbaohe.app.util.PreferUtils;
import com.guodongbaohe.app.util.TimeShowUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EverydayfaddishAdapter extends RecyclerView.Adapter<EverydayfaddishAdapter.EverydayfaddishHolder> {
    private List<EverydayHostGoodsBean.GoodsList> list;
    private Context context;
    private OnItemClick onShareClick, onFuzhiClick, allItemClick;
    private FragmentActivity activity;
    List<String> list_imgs;
    private OnLongClick onLongClick;
    BigDecimal bg3;
    String attr_price, attr_prime, attr_ratio, tax_rate, status, video;
    double app_v;

    public interface OnLongClick {
        void OnLongClickListener(View view, int position);
    }

    public void setAllItemClickListener(OnItemClick allItemClick) {
        this.allItemClick = allItemClick;
    }

    public void onShareClickListener(OnItemClick onShareClick) {
        this.onShareClick = onShareClick;
    }

    public void setonFuZhiListener(OnItemClick onFuzhiClick) {
        this.onFuzhiClick = onFuzhiClick;
    }

    public void setonLongClickListener(OnLongClick onLongClick) {
        this.onLongClick = onLongClick;
    }

    public EverydayfaddishAdapter(List<EverydayHostGoodsBean.GoodsList> list, FragmentActivity activity) {
        this.list = list;
        this.activity = activity;
    }

    @Override
    public EverydayfaddishHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_everydayfaddish, parent, false);
        return new EverydayfaddishHolder(view);
    }

    @Override
    public void onBindViewHolder(final EverydayfaddishHolder holder, int position) {
        tax_rate = PreferUtils.getString(context, "tax_rate");/*配置比例*/
        if (!TextUtils.isEmpty(tax_rate)) {
            app_v = 1 - Double.valueOf(tax_rate);
        }
        status = list.get(position).getStatus();
        video = list.get(position).getVideo();
        attr_price = list.get(position).getAttr_price();
        attr_prime = list.get(position).getAttr_prime();
        attr_ratio = list.get(position).getAttr_ratio();
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
        CircleImgsAdapter circleImgsAdapter = new CircleImgsAdapter(list_imgs, context, activity, status, video);
        holder.recyclerview.setAdapter(circleImgsAdapter);
        String comment = list.get(position).getComment();
        if (TextUtils.isEmpty(comment)) {
            holder.re_taokouling_buju.setVisibility(View.GONE);
        } else {
            holder.re_taokouling_buju.setVisibility(View.VISIBLE);
            holder.tv_kouling_wenben.setText(list.get(position).getComment());
        }
        if (onShareClick != null) {
            holder.re_share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onShareClick.OnItemClickListener(holder.re_share, holder.getAdapterPosition());
                }
            });
        }
        if (onFuzhiClick != null) {
            holder.re_fuzhi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onFuzhiClick.OnItemClickListener(holder.re_fuzhi, holder.getAdapterPosition());
                }
            });
        }
        if (allItemClick != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    allItemClick.OnItemClickListener(holder.itemView, holder.getAdapterPosition());
                }
            });
        }
        if (onLongClick != null) {
            holder.title.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onLongClick.OnLongClickListener(holder.title, holder.getAdapterPosition());
                    return false;
                }
            });
        }

        String member_role = list.get(position).getMember_role();
        if (list.get(position).isLogin()) {
            if (Constant.BOSS_USER_LEVEL.contains(member_role)) {
                /*总裁用户*/
                touristData(holder, Constant.BOSS_RATIO);
            } else if (Constant.PARTNER_USER_LEVEL.contains(member_role)) {
                /*合伙人用户*/
                touristData(holder, Constant.PARTNER_RATIO);
            } else {
                /*vip用户*/
                touristData(holder, Constant.VIP_RATIO);
            }
        } else {
            touristData(holder, Constant.VIP_RATIO);
        }

    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    private void touristData(EverydayfaddishHolder holder, int num) {
        if (!TextUtils.isEmpty(attr_ratio)) {
            double result = Double.valueOf(attr_price) * Double.valueOf(attr_ratio) * num / 10000 * app_v;
            bg3 = new BigDecimal(result);
            double money = bg3.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            holder.ninengzhuan.setText("你能赚¥" + money);
        }
    }

    public class EverydayfaddishHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ll_parent)
        LinearLayout ll_parent;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.re_share)
        RelativeLayout re_share;
        @BindView(R.id.recyclerview)
        RecyclerView recyclerview;
        @BindView(R.id.time)
        TextView time;
        @BindView(R.id.re_fuzhi)
        RelativeLayout re_fuzhi;
        @BindView(R.id.share_nums)
        TextView share_nums;
        @BindView(R.id.tv_kouling_wenben)
        TextView tv_kouling_wenben;
        @BindView(R.id.re_taokouling_buju)
        RelativeLayout re_taokouling_buju;
        @BindView(R.id.ninengzhuan)
        TextView ninengzhuan;

        public EverydayfaddishHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
