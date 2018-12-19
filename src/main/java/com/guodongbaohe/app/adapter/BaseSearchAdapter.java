package com.guodongbaohe.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.guodongbaohe.app.OnItemClick;
import com.guodongbaohe.app.R;
import com.guodongbaohe.app.bean.AllNetBean;
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

public class BaseSearchAdapter extends RecyclerView.Adapter<BaseSearchAdapter.BaseSearchHolder> {
    private Context context;
    private OnItemClick onItemClick;
    private List<AllNetBean.AllNetData> list;
    BigDecimal bg3;
    String attr_price, attr_prime, attr_ratio;
    double app_v;

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
        holder.title.setText(list.get(position).getGoods_name());
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
            holder.sjizhuan.setText(StringCleanZeroUtil.DoubleFormat(d_money) + "元券");
            holder.tv_classic_type.setText("券后");
        } else {
            if (d_price > 0) {
                double disaccount = Double.valueOf(attr_price) / Double.valueOf(attr_prime) * 10;
                bg3 = new BigDecimal(disaccount);
                double d_zhe = bg3.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
                holder.sjizhuan.setText(d_zhe + "折");
                holder.tv_classic_type.setText("折后");
            } else {
                holder.sjizhuan.setText("立即抢购");
                holder.tv_classic_type.setText("特惠价");
            }
        }
        if (list.get(position).isLogin()) {
            String member_role = list.get(position).getMember_role();
            String son_count = list.get(position).getSon_count();
            if (member_role.equals("2")) {
                double result = Double.valueOf(attr_price) * Double.valueOf(attr_ratio) * 90 / 10000 * app_v;
                bg3 = new BigDecimal(result);
                double money = bg3.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                String ninengzhuan_money = "你能赚¥" + money;
                TextViewUtil.setTextViewSize(ninengzhuan_money, holder.ninengzhuan);
            } else if (member_role.equals("1")) {
                double result = Double.valueOf(attr_price) * Double.valueOf(attr_ratio) * 82 / 10000 * app_v;
                bg3 = new BigDecimal(result);
                double money = bg3.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                String ninengzhuan_money = "你能赚¥" + money;
                TextViewUtil.setTextViewSize(ninengzhuan_money, holder.ninengzhuan);
            } else {
                if (!son_count.equals("0")) {
                    /*存在下级*/
                    double result = Double.valueOf(attr_price) * Double.valueOf(attr_ratio) * 50 / 10000 * app_v;
                    bg3 = new BigDecimal(result);
                    double money = bg3.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                    String ninengzhuan_money = "你能赚¥" + money;
                    TextViewUtil.setTextViewSize(ninengzhuan_money, holder.ninengzhuan);
                } else {
                    /*不存在下级即游客*/
                    touristData(holder);
                }
            }
        } else {
            /*不存在下级即游客*/
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
        return list == null ? 0 : list.size();
    }

    private void touristData(BaseSearchHolder holder) {
        double result = Double.valueOf(attr_price) * Double.valueOf(attr_ratio) * 40 / 10000 * app_v;
        bg3 = new BigDecimal(result);
        double money = bg3.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        String ninengzhuan_money = "你能赚¥" + money;
        TextViewUtil.setTextViewSize(ninengzhuan_money, holder.ninengzhuan);
    }

    public class BaseSearchHolder extends RecyclerView.ViewHolder {
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

        public BaseSearchHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
