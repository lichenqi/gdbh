package com.guodongbaohe.app.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.guodongbaohe.app.OnItemClick;
import com.guodongbaohe.app.R;
import com.guodongbaohe.app.bean.HomeListBean;
import com.guodongbaohe.app.common_constant.Constant;
import com.guodongbaohe.app.util.IconAndTextGroupUtil;
import com.guodongbaohe.app.util.NetImageLoadUtil;
import com.guodongbaohe.app.util.NumUtil;
import com.guodongbaohe.app.util.PreferUtils;
import com.guodongbaohe.app.util.StringCleanZeroUtil;
import com.guodongbaohe.app.util.TextViewUtil;
import com.makeramen.roundedimageview.RoundedImageView;

import java.math.BigDecimal;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeListAdapter extends RecyclerView.Adapter<HomeListAdapter.HomeListHolder> {
    private Context context;
    private List<HomeListBean.ListData> list;
    private OnItemClick onItemClick;
    BigDecimal bg3;
    String attr_prime, attr_ratio, attr_price, tax_rate, sales_month;
    double app_v;

    public void setOnClickListener(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    public HomeListAdapter(Context context, List<HomeListBean.ListData> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public HomeListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.all_list_item, parent, false);
        return new HomeListHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final HomeListHolder holder, int position) {
        tax_rate = PreferUtils.getString(context, "tax_rate");/*配置比例*/
        app_v = 1 - Double.valueOf(tax_rate);
        sales_month = list.get(position).getSales_month();/*月销*/
        attr_prime = list.get(position).getAttr_prime();/*原价*/
        attr_price = list.get(position).getAttr_price();/*券后价*/
        attr_ratio = list.get(position).getAttr_ratio();/*比例*/
        String goods_short = list.get(position).getGoods_short();/*短标题*/
        String goods_name = list.get(position).getGoods_name();/*长标题*/
        NetImageLoadUtil.loadImage(list.get(position).getGoods_thumb(), context, holder.iv);
        IconAndTextGroupUtil.setTextView(context, holder.dianpu_name, list.get(position).getSeller_shop(), list.get(position).getAttr_site());
        if (!TextUtils.isEmpty(goods_short)) {
            holder.title.setText(goods_short);
            holder.title.setMaxLines(2);
        } else {
            holder.title.setText(goods_name);
            holder.title.setMaxLines(1);
        }
        StringCleanZeroUtil.StringFormat(attr_price, holder.tv_price);

        /*黄色优惠券显示文案布局*/
        String coupon_surplus = list.get(position).getCoupon_surplus();
        if (Double.valueOf(coupon_surplus) > 0) {
            /*有券显示*/
            double d_price = Double.valueOf(attr_prime) - Double.valueOf(attr_price);
            bg3 = new BigDecimal(d_price);
            double d_money = bg3.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            holder.sjizhuan.setText(StringCleanZeroUtil.DoubleFormat(d_money) + " 元券");
            TvClassicType(holder, "券后");
        } else {
            /*无券显示 显示折扣或者抢购价*/
            double d_price = Double.valueOf(attr_prime) - Double.valueOf(attr_price);
            if (d_price > 0) {
                double disaccount = Double.valueOf(attr_price) / Double.valueOf(attr_prime) * 10;
                bg3 = new BigDecimal(disaccount);
                double d_zhe = bg3.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
                holder.sjizhuan.setText(d_zhe + " 折");
                TvClassicType(holder, "折后");
            } else {
                holder.sjizhuan.setText("立即抢购");
                TvClassicType(holder, "特惠价");
            }
        }

        /*红色你能赚显示文案布局*/
        if (list.get(position).isLogin()) {
            /*登录*/
            String member_role = list.get(position).getMember_role();
            if (Constant.BOSS_USER_LEVEL.contains(member_role)) {
                /*总裁角色*/
                NiNengZhuanViewData(holder, 90);
                holder.tv_sale_num.setText("月销" + NumUtil.getNum(sales_month));
                holder.tv_sale_num.getPaint().setFlags(1);
            } else if (Constant.PARTNER_USER_LEVEL.contains(member_role)) {
                /*合伙人角色*/
                NiNengZhuanViewData(holder, 80);
                holder.tv_sale_num.setText("月销" + NumUtil.getNum(sales_month));
                holder.tv_sale_num.getPaint().setFlags(1);
            } else {
                /*Vip角色*/
                NiNengZhuanViewData(holder, 55);
                holder.tv_sale_num.setText("月销" + NumUtil.getNum(sales_month));
                holder.tv_sale_num.getPaint().setFlags(1);
            }
        } else {
            /*游客*/
            holder.ninengzhuan.setText("月销" + NumUtil.getNum(sales_month));
            holder.tv_sale_num.setText("¥" + attr_prime);
            holder.tv_sale_num.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
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
    private void NiNengZhuanViewData(HomeListHolder holder, int num) {
        double result = Double.valueOf(attr_price) * Double.valueOf(attr_ratio) * num / 10000 * app_v;
        bg3 = new BigDecimal(result);
        double money = bg3.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        String ninengzhuan_money = "你能赚 ¥" + money;
        TextViewUtil.setTextViewSize(ninengzhuan_money, holder.ninengzhuan);

        TextViewUtil.setTextViewSize(ninengzhuan_money, holder.tv_upgrade_money);
    }

    /*券后  折后  特惠价显示*/
    private void TvClassicType(HomeListHolder holder, String content) {
        holder.tv_classic_type.setText(content);
    }

    public class HomeListHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv)
        RoundedImageView iv;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.tv_price)
        TextView tv_price;
        @BindView(R.id.tv_sale_num)
        TextView tv_sale_num;
        @BindView(R.id.ninengzhuan)
        TextView ninengzhuan;
        @BindView(R.id.sjizhuan)
        TextView sjizhuan;
        @BindView(R.id.dianpu_name)
        TextView dianpu_name;
        @BindView(R.id.tv_classic_type)
        TextView tv_classic_type;
        @BindView(R.id.re_bottom)
        RelativeLayout re_bottom;
        @BindView(R.id.tv_upgrade_money)
        TextView tv_upgrade_money;

        public HomeListHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
