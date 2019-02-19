package com.guodongbaohe.app.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.guodongbaohe.app.OnItemClick;
import com.guodongbaohe.app.R;
import com.guodongbaohe.app.bean.RouteBean;
import com.guodongbaohe.app.common_constant.Constant;
import com.guodongbaohe.app.util.NumUtil;
import com.guodongbaohe.app.util.PreferUtils;
import com.guodongbaohe.app.util.StringCleanZeroUtil;

import java.math.BigDecimal;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class JPAdapter extends RecyclerView.Adapter<JPAdapter.JPHolder> {
    private Context context;
    private List<RouteBean.RouteData> list_related;
    private OnItemClick onItemClick;
    BigDecimal bg3;
    String attr_price, attr_prime, attr_ratio;
    double app_v;

    public void setonclicklistener(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    public JPAdapter(Context context, List<RouteBean.RouteData> list_related) {
        this.context = context;
        this.list_related = list_related;
    }

    @Override
    public JPHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.home_list_item, parent, false);
        return new JPHolder(view);
    }

    @Override
    public void onBindViewHolder(final JPHolder holder, int position) {
        String tax_rate = PreferUtils.getString(context, "tax_rate");
        app_v = 1 - Double.valueOf(tax_rate);
        String goods_thumb = list_related.get(position).getGoods_thumb();
        Glide.with(context).load(goods_thumb).placeholder(R.drawable.loading_img).into(holder.iv);
        holder.title.setText(list_related.get(position).getGoods_name());
        attr_price = list_related.get(position).getAttr_price();
        attr_prime = list_related.get(position).getAttr_prime();
        attr_ratio = list_related.get(position).getAttr_ratio();
        holder.tv_sale_num.setText(NumUtil.getNum(list_related.get(position).getSales_month()) + "人已购");
        StringCleanZeroUtil.StringFormat(attr_price, holder.price);
        StringCleanZeroUtil.StringFormatWithYuan(attr_prime, holder.old_price);
        holder.old_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        /*优惠券和折扣和抢购价显示*/
        String coupon_surplus = list_related.get(position).getCoupon_surplus();
        double d_price = Double.valueOf(attr_prime) - Double.valueOf(attr_price);
        bg3 = new BigDecimal(d_price);
        double d_money = bg3.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        if (Double.valueOf(coupon_surplus) > 0) {
            holder.tv_coupon.setText(StringCleanZeroUtil.DoubleFormat(d_money) + " 元券");
        } else {
            if (d_price > 0) {
                double disaccount = Double.valueOf(attr_price) / Double.valueOf(attr_prime) * 10;
                bg3 = new BigDecimal(disaccount);
                double d_zhe = bg3.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
                holder.tv_coupon.setText(d_zhe + " 折");
            } else {
                holder.tv_coupon.setText("立即抢购");
            }
        }

        if (list_related.get(position).isLogin()) {
            /*登录*/
            String member_role = list_related.get(position).getMember_role();
            if (Constant.BOSS_USER_LEVEL.contains(member_role)) {
                /*总裁用户*/
                holder.ninengzhuan.setVisibility(View.VISIBLE);
                YouMakeMoney(holder, 90);
            } else if (Constant.PARTNER_USER_LEVEL.contains(member_role)) {
                /*和火人用户*/
                holder.ninengzhuan.setVisibility(View.VISIBLE);
                YouMakeMoney(holder, 70);
            } else if (Constant.VIP_USER_LEVEL.contains(member_role)) {
                /*Vip用户*/
                holder.ninengzhuan.setVisibility(View.VISIBLE);
                YouMakeMoney(holder, 40);
            } else {
                /*普通用户*/
                holder.ninengzhuan.setVisibility(View.GONE);
            }
        } else {
            /*游客*/
            holder.ninengzhuan.setVisibility(View.GONE);
        }

        if (onItemClick != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClick.OnItemClickListener(holder.itemView, holder.getAdapterPosition());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list_related == null ? 0 : list_related.size();
    }

    /*你能赚显示*/
    private void YouMakeMoney(JPHolder holder, int num) {
        double result = Double.valueOf(attr_price) * Double.valueOf(attr_ratio) * num / 10000 * app_v;
        bg3 = new BigDecimal(result);
        double money = bg3.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        String ninengzhuan_money = "你能赚 ¥" + money;
        holder.ninengzhuan.setText(ninengzhuan_money);
    }

    public class JPHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv)
        ImageView iv;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.price)
        TextView price;
        @BindView(R.id.old_price)
        TextView old_price;
        @BindView(R.id.ninengzhuan)
        TextView ninengzhuan;
        @BindView(R.id.tv_coupon)
        TextView tv_coupon;
        @BindView(R.id.tv_sale_num)
        TextView tv_sale_num;

        public JPHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
