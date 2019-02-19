package com.guodongbaohe.app.gridview;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.guodongbaohe.app.R;
import com.guodongbaohe.app.activity.BaseH5Activity;
import com.guodongbaohe.app.activity.GVipToFriendActivity;
import com.guodongbaohe.app.activity.KesalanPathActivity;
import com.guodongbaohe.app.activity.LoginAndRegisterActivity;
import com.guodongbaohe.app.activity.NinePinkageActivity;
import com.guodongbaohe.app.activity.PinZheMakeMoneyActivity;
import com.guodongbaohe.app.activity.ShopRangingClassicActivity;
import com.guodongbaohe.app.activity.SuperMakeActivity;
import com.guodongbaohe.app.activity.YaoQingFriendActivity;
import com.guodongbaohe.app.bean.NewBanDataBean;
import com.guodongbaohe.app.fragment.AllFragment;
import com.guodongbaohe.app.util.PreferUtils;

import java.util.ArrayList;
import java.util.List;

public class GridViewAdapter extends BaseAdapter {
    private List<NewBanDataBean> dataList;

    public GridViewAdapter(List<NewBanDataBean> datas, int page) {
        dataList = new ArrayList<>();
        //start end分别代表要显示的数组在总数据List中的开始和结束位置
        int start = page * AllFragment.item_grid_num;
        int end = start + AllFragment.item_grid_num;
        while ((start < datas.size()) && (start < end)) {
            dataList.add(datas.get(start));
            start++;
        }
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int i) {
        return dataList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View itemView, final ViewGroup viewGroup) {
        ViewHolder mHolder;
        if (itemView == null) {
            mHolder = new ViewHolder();
            itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.home_classic_item, viewGroup, false);
            mHolder.iv = (ImageView) itemView.findViewById(R.id.iv);
            mHolder.name = (TextView) itemView.findViewById(R.id.name);
            itemView.setTag(mHolder);
        } else {
            mHolder = (ViewHolder) itemView.getTag();
        }
        final NewBanDataBean bean = dataList.get(i);
        final String title = bean.title;
        final String url = bean.url;
        if (bean != null) {
            Glide.with(viewGroup.getContext()).load(bean.image).into(mHolder.iv);
            mHolder.name.setText(title);
        }
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                Context context = viewGroup.getContext();
                switch (url) {
                    case "jkj":
                        /*9.9包邮*/
                        intent = new Intent(context, NinePinkageActivity.class);
                        intent.putExtra("title", title);
                        context.startActivity(intent);
                        break;
                    case "fqb":
                        /*疯抢榜*/
                        intent = new Intent(context, ShopRangingClassicActivity.class);
                        intent.putExtra("title", title);
                        context.startActivity(intent);
                        break;
                    case "jhs":
                        /*聚划算*/
                        intent = new Intent(context, KesalanPathActivity.class);
                        intent.putExtra("title", title);
                        context.startActivity(intent);
                        break;
                    case "tqg":
                        /*淘抢购*/
                        intent = new Intent(context, SuperMakeActivity.class);
                        intent.putExtra("title", title);
                        context.startActivity(intent);
                        break;
                    case "pzz":
                        /*拼着赚*/
                        if (PreferUtils.getBoolean(context, "isLogin")) {
                            intent = new Intent(context, PinZheMakeMoneyActivity.class);
                            intent.putExtra("url", "https://mo.m.taobao.com/optimus/jhspt2c?pid=");
                            context.startActivity(intent);
                        } else {
                            context.startActivity(new Intent(context, LoginAndRegisterActivity.class));
                        }
                        break;
                    case "yqtz":
                        if (PreferUtils.getBoolean(context, "isLogin")) {
                            intent = new Intent(context, YaoQingFriendActivity.class);
                            context.startActivity(intent);
                        } else {
                            context.startActivity(new Intent(context, LoginAndRegisterActivity.class));
                        }
                        break;
                    case "tgsc":
                        intent = new Intent(context, BaseH5Activity.class);
                        intent.putExtra("url", bean.extend);
                        context.startActivity(intent);
                        break;
                    case "hhr":
                        if (PreferUtils.getBoolean(context, "isLogin")) {
                            intent = new Intent(context, GVipToFriendActivity.class);
                            context.startActivity(intent);
                        } else {
                            context.startActivity(new Intent(context, LoginAndRegisterActivity.class));
                        }
                        break;
                }
            }
        });
        return itemView;
    }

    private class ViewHolder {
        private ImageView iv;
        private TextView name;
    }
}