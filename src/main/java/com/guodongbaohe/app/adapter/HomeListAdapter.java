package com.guodongbaohe.app.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.guodongbaohe.app.OnItemClick;
import com.guodongbaohe.app.R;
import com.guodongbaohe.app.bean.HomeListBean;
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
    String attr_prime, attr_ratio, attr_price, tax_rate;

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
        String attr_site = list.get(position).getAttr_site();
        if (attr_site.equals("tmall")) {
            holder.tv_sale_num.setText("月销" + NumUtil.getNum(list.get(position).getSales_month()) + "|" + "天猫");
        } else {
            holder.tv_sale_num.setText("月销" + NumUtil.getNum(list.get(position).getSales_month()) + "|" + "淘宝");
        }

        if (list.get(position).isLogin()) {
            /*登录*/
            String member_role = list.get(position).getMember_role();
            String son_count = list.get(position).getSon_count();
            if (member_role.equals("2")) {
                holder.sjizhuan.setVisibility(View.GONE);
                double result = Double.valueOf(attr_price) * Double.valueOf(attr_ratio) * 90 / 10000 * app_v;
                bg3 = new BigDecimal(result);
                double money = bg3.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                String ninengzhuan_money = "你能赚¥" + money;
                TextViewUtil.setTextViewSize(ninengzhuan_money, holder.ninengzhuan);
            } else if (member_role.equals("1")) {
                holder.sjizhuan.setVisibility(View.VISIBLE);
                double result = Double.valueOf(attr_price) * Double.valueOf(attr_ratio) * 82 / 10000 * app_v;
                bg3 = new BigDecimal(result);
                double money = bg3.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                String ninengzhuan_money = "你能赚¥" + money;
                TextViewUtil.setTextViewSize(ninengzhuan_money, holder.ninengzhuan);

                double sj_result = Double.valueOf(attr_price) * Double.valueOf(attr_ratio) * 90 / 10000 * app_v;
                bg3 = new BigDecimal(sj_result);
                double sj_money = bg3.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                holder.sjizhuan.setText("总裁赚¥" + sj_money);
            } else {
                holder.sjizhuan.setVisibility(View.VISIBLE);
                if (!TextUtils.isEmpty(son_count)) {
                    if (!son_count.equals("0")) {
                        /*存在下级*/
                        double result = Double.valueOf(attr_price) * Double.valueOf(attr_ratio) * 50 / 10000 * app_v;
                        bg3 = new BigDecimal(result);
                        double money = bg3.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                        String ninengzhuan_money = "你能赚¥" + money;
                        TextViewUtil.setTextViewSize(ninengzhuan_money, holder.ninengzhuan);

                        double sj_result = Double.valueOf(attr_price) * Double.valueOf(attr_ratio) * 82 / 10000 * app_v;
                        bg3 = new BigDecimal(sj_result);
                        double sj_money = bg3.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                        holder.sjizhuan.setText("升级赚¥" + sj_money);
                    } else {
                        /*不存在下级即游客*/
                        touristData(holder);
                    }
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
        return list == null ? 0 : list.size();
    }

    double app_v;

    private void touristData(HomeListHolder holder) {
        app_v = 1 - Double.valueOf(tax_rate);
        double result = Double.valueOf(attr_price) * Double.valueOf(attr_ratio) * 40 / 10000 * app_v;
        bg3 = new BigDecimal(result);
        double money = bg3.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        String ninengzhuan_money = "你能赚¥" + money;
        TextViewUtil.setTextViewSize(ninengzhuan_money, holder.ninengzhuan);

        double sj_result = Double.valueOf(attr_price) * Double.valueOf(attr_ratio) * 50 / 10000 * app_v;
        bg3 = new BigDecimal(sj_result);
        double sj_money = bg3.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        holder.sjizhuan.setText("升级赚¥" + sj_money);

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

        public HomeListHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
