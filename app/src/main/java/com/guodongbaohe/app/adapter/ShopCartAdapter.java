package com.guodongbaohe.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.guodongbaohe.app.OnItemClick;
import com.guodongbaohe.app.R;
import com.guodongbaohe.app.bean.AllNetBean;
import com.guodongbaohe.app.common_constant.Constant;
import com.guodongbaohe.app.util.IconAndTextGroupUtil;
import com.guodongbaohe.app.util.NetImageLoadUtil;
import com.guodongbaohe.app.util.NumUtil;
import com.guodongbaohe.app.util.PreferUtils;
import com.guodongbaohe.app.util.StringCleanZeroUtil;
import com.makeramen.roundedimageview.RoundedImageView;

import java.math.BigDecimal;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/*好的*/
public class ShopCartAdapter extends RecyclerView.Adapter<ShopCartAdapter.ShopCartHolder> {
    private Context context;
    private List<AllNetBean.AllNetData> list;
    BigDecimal bg3;
    String attr_price, attr_prime, attr_ratio;
    double app_v;
    private OnItemClick onItemClick;

    public void setonclicklistener(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    public ShopCartAdapter(Context context, List<AllNetBean.AllNetData> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public ShopCartHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.shopping_cart_item, parent, false);
        return new ShopCartHolder(view);
    }

    @Override
    public void onBindViewHolder(final ShopCartHolder holder, int position) {
        String tax_rate = PreferUtils.getString(context, "tax_rate");
        app_v = 1 - Double.valueOf(tax_rate);
        String goods_thumb = list.get(position).getGoods_thumb();
        NetImageLoadUtil.loadImage(goods_thumb, context, holder.iv);
        String goods_short = list.get(position).getGoods_short();/*短标题*/
        String goods_name = list.get(position).getGoods_name();/*长标题*/
        if (!TextUtils.isEmpty(goods_short)) {
            holder.title.setText(goods_short);
            holder.title.setMaxLines(2);
        } else {
            holder.title.setText(goods_name);
            holder.title.setMaxLines(1);
        }
        String attr_site = list.get(position).getAttr_site();
        IconAndTextGroupUtil.setTextView(context, holder.dianpu_name, list.get(position).getSeller_shop(), attr_site);
        attr_price = list.get(position).getAttr_price();
        attr_prime = list.get(position).getAttr_prime();
        attr_ratio = list.get(position).getAttr_ratio();
        String sales_month = list.get(position).getSales_month();
        String coupon_surplus = list.get(position).getCoupon_surplus();
        holder.tv_price.setText(attr_price);
        double d_price = Double.valueOf(attr_prime) - Double.valueOf(attr_price);
        bg3 = new BigDecimal(d_price);
        double d_money = bg3.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        if (Double.valueOf(coupon_surplus) > 0) {
            holder.tv_coupon_money.setText(StringCleanZeroUtil.DoubleFormat(d_money) + " 元券");
            holder.tv_coupon_money_boss.setText(StringCleanZeroUtil.DoubleFormat(d_money) + " 元券");
            holder.tv_classic_type.setText("券后");
        } else {
            if (d_price > 0) {
                double disaccount = Double.valueOf(attr_price) / Double.valueOf(attr_prime) * 10;
                bg3 = new BigDecimal(disaccount);
                double d_zhe = bg3.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
                holder.tv_coupon_money.setText(d_zhe + " 折");
                holder.tv_coupon_money_boss.setText(d_zhe + " 折");
                holder.tv_classic_type.setText("折后");
            } else {
                holder.tv_coupon_money.setText("立即抢购");
                holder.tv_coupon_money_boss.setText("立即抢购");
                holder.tv_classic_type.setText("特惠价");
            }
        }

        holder.tv_sale_num.setText("月销" + NumUtil.getNum(sales_month) + "件");

        String member_role = list.get(position).getMember_role();
        if (Constant.BOSS_USER_LEVEL.contains(member_role)) {
            /*总裁用户*/
            holder.tv_sale_num.setVisibility(View.VISIBLE);
            holder.re_coupon_view.setVisibility(View.GONE);
            holder.tv_upgrade_money.setVisibility(View.GONE);
            holder.re_coupon_view_boss.setVisibility(View.VISIBLE);
            touristData(holder, 90);
        } else {
            holder.tv_sale_num.setVisibility(View.GONE);
            holder.re_coupon_view.setVisibility(View.VISIBLE);
            holder.tv_upgrade_money.setVisibility(View.VISIBLE);
            holder.re_coupon_view_boss.setVisibility(View.GONE);
            if (Constant.PARTNER_USER_LEVEL.contains(member_role)) {
                /*合伙人*/
                touristData(holder, 80);
                setUpgradeViewData(holder, 90);
            } else {
                /*VIP会员*/
                touristData(holder, 55);
                setUpgradeViewData(holder, 80);
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

    /*你能赚显示方法*/
    private void touristData(ShopCartHolder holder, int num) {
        double result = Double.valueOf(attr_price) * Double.valueOf(attr_ratio) * num / 10000 * app_v;
        bg3 = new BigDecimal(result);
        double money = bg3.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        String ninengzhuan_money = "你能赚 ¥" + money;
        holder.ninengzhuan.setText(ninengzhuan_money);
    }

    /*升级赚显示方法*/
    private void setUpgradeViewData(ShopCartHolder holder, int num) {
        double result = Double.valueOf(attr_price) * Double.valueOf(attr_ratio) * num / 10000 * app_v;
        bg3 = new BigDecimal(result);
        double money = bg3.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        holder.tv_upgrade_money.setText("升级赚 ¥" + money);
    }

    public class ShopCartHolder extends RecyclerView.ViewHolder {
        /*图片*/
        @BindView(R.id.iv)
        RoundedImageView iv;
        /*标题*/
        @BindView(R.id.title)
        TextView title;
        /*店铺名字*/
        @BindView(R.id.dianpu_name)
        TextView dianpu_name;
        /*售价*/
        @BindView(R.id.tv_price)
        TextView tv_price;
        /*价格类型*/
        @BindView(R.id.tv_classic_type)
        TextView tv_classic_type;
        /*销量*/
        @BindView(R.id.tv_sale_num)
        TextView tv_sale_num;
        /*优惠券布局*/
        @BindView(R.id.re_coupon_view)
        RelativeLayout re_coupon_view;
        /*优惠券显示金额*/
        @BindView(R.id.tv_coupon_money)
        TextView tv_coupon_money;
        /*你能赚*/
        @BindView(R.id.ninengzhuan)
        TextView ninengzhuan;
        /*升级赚*/
        @BindView(R.id.tv_upgrade_money)
        TextView tv_upgrade_money;
        /*总裁优惠券布局*/
        @BindView(R.id.re_coupon_view_boss)
        RelativeLayout re_coupon_view_boss;
        /*总裁优惠券金额*/
        @BindView(R.id.tv_coupon_money_boss)
        TextView tv_coupon_money_boss;

        public ShopCartHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
