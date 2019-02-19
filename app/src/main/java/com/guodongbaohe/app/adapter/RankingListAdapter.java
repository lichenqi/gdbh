package com.guodongbaohe.app.adapter;

import android.content.Context;
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
import com.guodongbaohe.app.util.NetImageLoadUtil;
import com.guodongbaohe.app.util.NumUtil;
import com.guodongbaohe.app.util.PreferUtils;
import com.makeramen.roundedimageview.RoundedImageView;

import java.math.BigDecimal;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RankingListAdapter extends RecyclerView.Adapter<RankingListAdapter.RankingListHolder> {
    private Context context;
    private List<HomeListBean.ListData> list;
    BigDecimal bg3;
    private OnItemClick onItemClick;
    double app_v;
    String attr_price, attr_prime, attr_ratio, member_role, son_count;

    public void setonclicklistener(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    public RankingListAdapter(Context context, List<HomeListBean.ListData> list) {
        this.list = list;
        this.context = context;
    }

    @Override
    public RankingListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rankinglistadapter, parent, false);
        return new RankingListHolder(view);
    }

    @Override
    public void onBindViewHolder(final RankingListHolder holder, int position) {
        String tax_rate = PreferUtils.getString(context, "tax_rate");/*配置比例*/
        app_v = 1 - Double.valueOf(tax_rate);
        attr_price = list.get(position).getAttr_price();
        attr_prime = list.get(position).getAttr_prime();
        attr_ratio = list.get(position).getAttr_ratio();
        NetImageLoadUtil.loadImage(list.get(position).getGoods_thumb(), context, holder.iv);
        holder.tv_today_sale.setText(list.get(position).getSales_today());
        holder.title.setText(list.get(position).getGoods_name());
        holder.tv_price.setText(attr_price);
        holder.tv_sale_num.setText("月销" + NumUtil.getNum(list.get(position).getSales_month()) + "件");

        double d_price = Double.valueOf(attr_prime) - Double.valueOf(attr_price);
        bg3 = new BigDecimal(d_price);
        double d_money = bg3.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        holder.tv_coupon.setText(d_money + "元券");

        if (list.get(position).isLogin()) {
            member_role = list.get(position).getMember_role();
            son_count = list.get(position).getSon_count();
            if (member_role.equals("2")) {
                double result = Double.valueOf(attr_price) * Double.valueOf(attr_ratio) * 90 / 10000 * app_v;
                bg3 = new BigDecimal(result);
                double money = bg3.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                holder.tv_income_price.setText("赚¥" + money);
            } else if (member_role.equals("1")) {
                double result = Double.valueOf(attr_price) * Double.valueOf(attr_ratio) * 82 / 10000 * app_v;
                bg3 = new BigDecimal(result);
                double money = bg3.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                holder.tv_income_price.setText("赚¥" + money);
            } else {
                if (!TextUtils.isEmpty(son_count)) {
                    if (!son_count.equals("0")) {
                        /*存在下级*/
                        double result = Double.valueOf(attr_price) * Double.valueOf(attr_ratio) * 50 / 10000 * app_v;
                        bg3 = new BigDecimal(result);
                        double money = bg3.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                        holder.tv_income_price.setText("赚¥" + money);
                    } else {
                        /*不存在下级即游客*/
                        double result = Double.valueOf(attr_price) * Double.valueOf(attr_ratio) * 40 / 10000 * app_v;
                        bg3 = new BigDecimal(result);
                        double money = bg3.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                        holder.tv_income_price.setText("赚¥" + money);
                    }
                }
            }
        } else {
            /*不存在下级即游客*/
            double result = Double.valueOf(attr_price) * Double.valueOf(attr_ratio) * 40 / 10000 * app_v;
            bg3 = new BigDecimal(result);
            double money = bg3.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            holder.tv_income_price.setText("赚¥" + money);
        }

        switch (position) {
            case 0:
                holder.medal.setVisibility(View.VISIBLE);
                break;
            case 1:
                holder.medal.setVisibility(View.VISIBLE);
                holder.medal.setImageResource(R.mipmap.silver_medal);
                break;
            case 2:
                holder.medal.setVisibility(View.VISIBLE);
                holder.medal.setImageResource(R.mipmap.copper_medal);
                break;
            default:
                holder.medal.setVisibility(View.GONE);
                break;
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

    public class RankingListHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv)
        RoundedImageView iv;
        @BindView(R.id.medal)
        ImageView medal;
        @BindView(R.id.tv_today_sale)
        TextView tv_today_sale;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.tv_income_price)
        TextView tv_income_price;
        @BindView(R.id.tv_sale_num)
        TextView tv_sale_num;
        @BindView(R.id.tv_price)
        TextView tv_price;
        @BindView(R.id.tv_coupon)
        TextView tv_coupon;

        public RankingListHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
