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

import java.math.BigDecimal;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BaseSearchAdapter extends RecyclerView.Adapter<BaseSearchAdapter.BaseSearchHolder> {
    private Context context;
    private OnItemClick onItemClick;
    private List<AllNetBean.AllNetData> list;
    BigDecimal bg3;
    String attr_price, attr_prime, attr_ratio;
    double app_v;
    double d_price;

    public void setonclicklistener(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    public BaseSearchAdapter(Context context, List<AllNetBean.AllNetData> list) {
        this.list = list;
        this.context = context;
    }

    @Override
    public BaseSearchHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_list_item, parent, false);
        return new BaseSearchHolder(view);
    }

    @Override
    public void onBindViewHolder(final BaseSearchHolder holder, int position) {
        String tax_rate = PreferUtils.getString(context, "tax_rate");
        app_v = 1 - Double.valueOf(tax_rate);
        String goods_thumb = list.get(position).getGoods_thumb();
        NetImageLoadUtil.loadImage(goods_thumb, context, holder.iv);
        String goods_short = list.get(position).getGoods_short();/*短标题*/
        String goods_name = list.get(position).getGoods_name();/*长标题*/
        String sales_month = list.get(position).getSales_month();/*月销*/
        if (!TextUtils.isEmpty(goods_short)) {
            holder.title.setText(goods_short);
            holder.title.setMaxLines(2);
        } else {
            holder.title.setText(goods_name);
            holder.title.setMaxLines(1);
        }
        String attr_site = list.get(position).getAttr_site();
        if (!TextUtils.isEmpty(attr_site) && !TextUtils.isEmpty(list.get(position).getSeller_shop())) {
            IconAndTextGroupUtil.setTextView(context, holder.dianpu_name, list.get(position).getSeller_shop(), attr_site);
        }
        attr_price = list.get(position).getAttr_price();
        attr_prime = list.get(position).getAttr_prime();
        attr_ratio = list.get(position).getAttr_ratio();
        String coupon_surplus = list.get(position).getCoupon_surplus();
        holder.tv_price.setText(attr_price);
        d_price = Double.valueOf(attr_prime) - Double.valueOf(attr_price);
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
        holder.tv_base_sale_num.setText("月销" + NumUtil.getNum(sales_month) + "件");

        /*红色你能赚显示文案布局*/
        if (list.get(position).isLogin()) {
            /*登录*/
            String member_role = list.get(position).getMember_role();
            if (Constant.BOSS_USER_LEVEL.contains(member_role)) {
                /*总裁角色*/
                holder.tv_base_sale_num.setVisibility(View.GONE);
                holder.tv_sale_num.setVisibility(View.VISIBLE);
                holder.re_coupon_view.setVisibility(View.GONE);
                holder.tv_upgrade_money.setVisibility(View.GONE);
                holder.re_coupon_view_boss.setVisibility(View.VISIBLE);
                NiNengZhuanViewData(holder, 90);
            } else {
                holder.tv_base_sale_num.setVisibility(View.VISIBLE);
                holder.tv_sale_num.setVisibility(View.GONE);
                holder.re_coupon_view.setVisibility(View.VISIBLE);
                holder.tv_upgrade_money.setVisibility(View.VISIBLE);
                holder.re_coupon_view_boss.setVisibility(View.GONE);
                if (Constant.PARTNER_USER_LEVEL.contains(member_role)) {
                    /*合伙人*/
                    NiNengZhuanViewData(holder, 80);
                    setUpgradeViewData(holder, 90);
                } else {
                    /*VIP*/
                    NiNengZhuanViewData(holder, 55);
                    setUpgradeViewData(holder, 80);
                }
            }
        } else {
            /*游客*/
            holder.tv_base_sale_num.setVisibility(View.VISIBLE);
            holder.tv_sale_num.setVisibility(View.GONE);
            holder.re_coupon_view.setVisibility(View.VISIBLE);
            holder.tv_upgrade_money.setVisibility(View.VISIBLE);
            holder.re_coupon_view_boss.setVisibility(View.GONE);
            NiNengZhuanViewData(holder, 55);
            setUpgradeViewData(holder, 80);
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
    private void NiNengZhuanViewData(BaseSearchHolder holder, int num) {
        double result = Double.valueOf(attr_price) * Double.valueOf(attr_ratio) * num / 10000 * app_v;
        bg3 = new BigDecimal(result);
        double money = bg3.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        holder.ninengzhuan.setText("你能赚 ¥" + money);
    }

    /*升级赚显示方法*/
    private void setUpgradeViewData(BaseSearchHolder holder, int num) {
        double result = Double.valueOf(attr_price) * Double.valueOf(attr_ratio) * num / 10000 * app_v;
        bg3 = new BigDecimal(result);
        double money = bg3.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        holder.tv_upgrade_money.setText("升级赚 ¥" + money);
    }

    public class BaseSearchHolder extends RecyclerView.ViewHolder {
        /*图片*/
        @BindView(R.id.iv)
        ImageView iv;
        /*标题*/
        @BindView(R.id.title)
        TextView title;
        /*店铺名字*/
        @BindView(R.id.dianpu_name)
        TextView dianpu_name;
        /*价格*/
        @BindView(R.id.tv_price)
        TextView tv_price;
        /*价格类型*/
        @BindView(R.id.tv_classic_type)
        TextView tv_classic_type;
        /*销量*/
        @BindView(R.id.tv_sale_num)
        TextView tv_sale_num;
        /*优惠券显示*/
        @BindView(R.id.re_coupon_view)
        RelativeLayout re_coupon_view;
        /*优惠券金额*/
        @BindView(R.id.tv_coupon_money)
        TextView tv_coupon_money;
        /*你能赚*/
        @BindView(R.id.ninengzhuan)
        TextView ninengzhuan;
        /*升级赚*/
        @BindView(R.id.tv_upgrade_money)
        TextView tv_upgrade_money;
        /*总裁优惠券显示*/
        @BindView(R.id.re_coupon_view_boss)
        RelativeLayout re_coupon_view_boss;
        @BindView(R.id.tv_coupon_money_boss)
        TextView tv_coupon_money_boss;
        /*中间价格显示*/
        @BindView(R.id.tv_base_sale_num)
        TextView tv_base_sale_num;

        public BaseSearchHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
