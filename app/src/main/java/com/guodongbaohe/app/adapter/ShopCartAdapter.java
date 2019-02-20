package com.guodongbaohe.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.guodongbaohe.app.util.TextViewUtil;

import java.math.BigDecimal;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

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
        String coupon_surplus = list.get(position).getCoupon_surplus();
        holder.tv_sale_num.setText("月销" + NumUtil.getNum(list.get(position).getSales_month()) + "件");
        holder.tv_price.setText(attr_price);
        double d_price = Double.valueOf(attr_prime) - Double.valueOf(attr_price);
        bg3 = new BigDecimal(d_price);
        double d_money = bg3.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        if (Double.valueOf(coupon_surplus) > 0) {
            holder.sjizhuan.setText(StringCleanZeroUtil.DoubleFormat(d_money) + " 元券");
            holder.tv_classic_type.setText("券后");
        } else {
            if (d_price > 0) {
                double disaccount = Double.valueOf(attr_price) / Double.valueOf(attr_prime) * 10;
                bg3 = new BigDecimal(disaccount);
                double d_zhe = bg3.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
                holder.sjizhuan.setText(d_zhe + " 折");
                holder.tv_classic_type.setText("折后");
            } else {
                holder.sjizhuan.setText("立即抢购");
                holder.tv_classic_type.setText("特惠价");
            }
        }

        String member_role = list.get(position).getMember_role();
        if (Constant.BOSS_USER_LEVEL.contains(member_role)) {
            /*总裁用户*/
            holder.ninengzhuan.setVisibility(View.VISIBLE);
            touristData(holder, 90);
        } else if (Constant.PARTNER_USER_LEVEL.contains(member_role)) {
            /*合伙人*/
            holder.ninengzhuan.setVisibility(View.VISIBLE);
            touristData(holder, 70);
        } else if (Constant.VIP_USER_LEVEL.contains(member_role)) {
            /*vip用户*/
            holder.ninengzhuan.setVisibility(View.VISIBLE);
            touristData(holder, 40);
        } else {
            /*普通用户*/
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
        return list == null ? 0 : list.size();
    }

    private void touristData(ShopCartHolder holder, int num) {
        double result = Double.valueOf(attr_price) * Double.valueOf(attr_ratio) * num / 10000 * app_v;
        bg3 = new BigDecimal(result);
        double money = bg3.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        String ninengzhuan_money = "你能赚 ¥" + money;
        TextViewUtil.setTextViewSize(ninengzhuan_money, holder.ninengzhuan);
    }

    public class ShopCartHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv)
        ImageView iv;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.dianpu_name)
        TextView dianpu_name;
        @BindView(R.id.tv_price)
        TextView tv_price;
        @BindView(R.id.tv_sale_num)
        TextView tv_sale_num;
        @BindView(R.id.ninengzhuan)
        TextView ninengzhuan;
        @BindView(R.id.sjizhuan)
        TextView sjizhuan;
        @BindView(R.id.tv_classic_type)
        TextView tv_classic_type;
        @BindView(R.id.re_bottom)
        RelativeLayout re_bottom;

        public ShopCartHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
