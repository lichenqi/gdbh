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
        holder.tv_sale_num.setText("已售" + NumUtil.getNum(list_related.get(position).getSales_month()) + "件");
        holder.price.setText("¥" + attr_price);
        holder.old_price.setText("¥" + attr_prime);
        holder.old_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        double d_price = Double.valueOf(attr_prime) - Double.valueOf(attr_price);
        bg3 = new BigDecimal(d_price);
        double d_money = bg3.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        holder.tv_coupon.setText(StringCleanZeroUtil.DoubleFormat(d_money) + "元券");

        if (list_related.get(position).isLogin()) {
            /*登录*/
            String member_role = list_related.get(position).getMember_role();
            String son_count = list_related.get(position).getSon_count();
            if (member_role.equals("2")) {
                holder.sjizhuan.setVisibility(View.GONE);
                double result = Double.valueOf(attr_price) * Double.valueOf(attr_ratio) * 90 / 10000 * app_v;
                bg3 = new BigDecimal(result);
                double money = bg3.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                holder.ninengzhuan.setText("你能赚¥" + money);
            } else if (member_role.equals("1")) {
                holder.sjizhuan.setVisibility(View.VISIBLE);
                double result = Double.valueOf(attr_price) * Double.valueOf(attr_ratio) * 82 / 10000 * app_v;
                bg3 = new BigDecimal(result);
                double money = bg3.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                holder.ninengzhuan.setText("你能赚¥" + money);

                double sj_result = Double.valueOf(attr_price) * Double.valueOf(attr_ratio) * 90 / 10000 * app_v;
                bg3 = new BigDecimal(sj_result);
                double sj_money = bg3.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                holder.sjizhuan.setText("总裁赚¥" + sj_money);
            } else {
                holder.sjizhuan.setVisibility(View.VISIBLE);
                if (!son_count.equals("0")) {
                    /*存在下级*/
                    double result = Double.valueOf(attr_price) * Double.valueOf(attr_ratio) * 50 / 10000 * app_v;
                    bg3 = new BigDecimal(result);
                    double money = bg3.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                    holder.ninengzhuan.setText("你能赚¥" + money);

                    double sj_result = Double.valueOf(attr_price) * Double.valueOf(attr_ratio) * 82 / 10000 * app_v;
                    bg3 = new BigDecimal(sj_result);
                    double sj_money = bg3.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                    holder.sjizhuan.setText("升级赚¥" + sj_money);
                } else {
                    /*不存在下级即游客*/
                    touristData(holder);
                }
            }
        } else {
            /*游客*/
            holder.sjizhuan.setVisibility(View.VISIBLE);
            touristData(holder);
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

    private void touristData(JPHolder holder) {
        double result = Double.valueOf(attr_price) * Double.valueOf(attr_ratio) * 40 / 10000 * app_v;
        bg3 = new BigDecimal(result);
        double money = bg3.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        holder.ninengzhuan.setText("你能赚¥" + money);

        double sj_result = Double.valueOf(attr_price) * Double.valueOf(attr_ratio) * 50 / 10000 * app_v;
        bg3 = new BigDecimal(sj_result);
        double sj_money = bg3.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        holder.sjizhuan.setText("升级赚¥" + sj_money);
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
        @BindView(R.id.sjizhuan)
        TextView sjizhuan;
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
