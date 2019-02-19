package com.guodongbaohe.app.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.guodongbaohe.app.OnItemClick;
import com.guodongbaohe.app.R;
import com.guodongbaohe.app.bean.HomeListBean;
import com.guodongbaohe.app.util.IconAndTextGroupUtil;
import com.guodongbaohe.app.util.NetImageLoadUtil;
import com.guodongbaohe.app.util.NumUtil;
import com.guodongbaohe.app.util.PreferUtils;
import com.guodongbaohe.app.util.StringCleanZeroUtil;

import java.math.BigDecimal;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NinePinkageAdapter extends RecyclerView.Adapter<NinePinkageAdapter.NineHolder> {
    private Context context;
    private List<HomeListBean.ListData> list;
    private OnItemClick onItemClick;
    BigDecimal bg3;
    String attr_price, attr_prime, attr_ratio, tax_rate;
    double app_v;

    public void setonclicklistener(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    public NinePinkageAdapter(Context context, List<HomeListBean.ListData> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public NineHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.home_list_item, parent, false);
        return new NineHolder(view);
    }

    @Override
    public void onBindViewHolder(final NineHolder holder, int position) {
        String money_upgrade_switch = PreferUtils.getString(context, "money_upgrade_switch");/*华为上线开关*/
        tax_rate = PreferUtils.getString(context, "tax_rate");/*配置比例*/
        app_v = 1 - Double.valueOf(tax_rate);
        attr_price = list.get(position).getAttr_price();
        attr_prime = list.get(position).getAttr_prime();
        attr_ratio = list.get(position).getAttr_ratio();
        String goods_thumb = list.get(position).getGoods_thumb();
        NetImageLoadUtil.loadImage(goods_thumb, context, holder.iv);
        IconAndTextGroupUtil.setTextView(context, holder.title, list.get(position).getGoods_name(), list.get(position).getAttr_site());
        holder.tv_sale_num.setText(NumUtil.getNum(list.get(position).getSales_month()) + "人已购");
        StringCleanZeroUtil.StringFormat(attr_price, holder.price);
        StringCleanZeroUtil.StringFormatWithYuan(attr_prime, holder.old_price);
        holder.old_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);

        /*优惠券和折扣和抢购价显示*/
        String coupon_surplus = list.get(position).getCoupon_surplus();
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

        if (money_upgrade_switch.equals("yes")) {
            holder.ninengzhuan.setVisibility(View.GONE);
        } else {
            holder.ninengzhuan.setVisibility(View.VISIBLE);
            if (list.get(position).isLogin()) {
                /*登录*/
                String member_role = list.get(position).getMember_role();
                String son_count = list.get(position).getSon_count();
                if (member_role.equals("2")) {
                    YouMakeMoney(holder, 90);
                } else if (member_role.equals("1")) {
                    YouMakeMoney(holder, 82);
                } else {
                    if (!TextUtils.isEmpty(son_count)) {
                        if (!son_count.equals("0")) {
                            /*存在下级*/
                            YouMakeMoney(holder, 50);
                        } else {
                            /*不存在下级即游客*/
                            YouMakeMoney(holder, 40);
                        }
                    }
                }
            } else {
                /*游客*/
                YouMakeMoney(holder, 40);
            }
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
        return list == null ? 0 : list.size();
    }

    /*你能赚显示*/
    private void YouMakeMoney(NineHolder holder, int num) {
        double result = Double.valueOf(attr_price) * Double.valueOf(attr_ratio) * num / 10000 * app_v;
        bg3 = new BigDecimal(result);
        double money = bg3.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        String ninengzhuan_money = "你能赚  ¥" + money;
        holder.ninengzhuan.setText(ninengzhuan_money);
    }

    public class NineHolder extends RecyclerView.ViewHolder {
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

        public NineHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
