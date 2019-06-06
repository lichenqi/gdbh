package com.guodongbaohe.app.gridview;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.guodongbaohe.app.R;
import com.guodongbaohe.app.activity.BaseH5Activity;
import com.guodongbaohe.app.activity.GShenJiActivity;
import com.guodongbaohe.app.activity.KesalanPathActivity;
import com.guodongbaohe.app.activity.LoginAndRegisterActivity;
import com.guodongbaohe.app.activity.NinePinkageActivity;
import com.guodongbaohe.app.activity.ShopRangingClassicActivity;
import com.guodongbaohe.app.activity.SuperMakeActivity;
import com.guodongbaohe.app.activity.TaoBaoAndTianMaoUrlActivity;
import com.guodongbaohe.app.activity.TaobaoShoppingCartActivity;
import com.guodongbaohe.app.activity.TaobaoTianMaoHolidayOfActivity;
import com.guodongbaohe.app.activity.XinShouJiaoChengActivity;
import com.guodongbaohe.app.activity.YaoQingFriendActivity;
import com.guodongbaohe.app.bean.NewBanDataBean;
import com.guodongbaohe.app.bean.ShopBasicBean;
import com.guodongbaohe.app.common_constant.Constant;
import com.guodongbaohe.app.common_constant.MyApplication;
import com.guodongbaohe.app.dagger2_test.DaggerActvity;
import com.guodongbaohe.app.fragment.AllFragment;
import com.guodongbaohe.app.myokhttputils.response.JsonResponseHandler;
import com.guodongbaohe.app.util.GsonUtil;
import com.guodongbaohe.app.util.JumpToShopDetailUtil;
import com.guodongbaohe.app.util.ParamUtil;
import com.guodongbaohe.app.util.PreferUtils;
import com.guodongbaohe.app.util.ToastUtils;
import com.guodongbaohe.app.util.VersionUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class GridViewAdapter extends BaseAdapter {
    private List<NewBanDataBean> dataList;
    Intent intent;
    Context context;

    public GridViewAdapter(List<NewBanDataBean> datas, int page) {
        dataList = new ArrayList<>();
        //start end分别代表要显示的数组在总数据List中的开始和结束位置
        int start = page * AllFragment.item_grid_num;
        int end = start + AllFragment.item_grid_num;
        while ((start < datas.size()) && (start < end)) {
            dataList.add( datas.get( start ) );
            start++;
        }
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int i) {
        return dataList.get( i );
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
            itemView = LayoutInflater.from( viewGroup.getContext() ).inflate( R.layout.home_classic_item, viewGroup, false );
            mHolder.iv = (ImageView) itemView.findViewById( R.id.iv );
            mHolder.name = (TextView) itemView.findViewById( R.id.name );
            itemView.setTag( mHolder );
        } else {
            mHolder = (ViewHolder) itemView.getTag();
        }
        final NewBanDataBean bean = dataList.get( i );
        final String title = bean.title;
        final String url = bean.url;
        final String type = bean.type;
        if (bean != null) {
            Glide.with( viewGroup.getContext() ).load( bean.image ).into( mHolder.iv );
            mHolder.name.setText( title );
        }
        itemView.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context = viewGroup.getContext();
                if (PreferUtils.getBoolean( context, "isLogin" )) {
                    if (!TextUtils.isEmpty( type )) {
                        switch (type) {
                            case "app_theme":
                                intent = new Intent( context, BaseH5Activity.class );
                                intent.putExtra( "url", bean.extend );
                                intent.putExtra( "title", title );
                                intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
                                context.startActivity( intent );
                                break;
                            case "tmall":
                                intent = new Intent( context, TaoBaoAndTianMaoUrlActivity.class );
                                intent.putExtra( "url", bean.extend );
                                intent.putExtra( "title", title );
                                intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
                                context.startActivity( intent );
                                break;
                            case "normal":
                                intent = new Intent( context, BaseH5Activity.class );
                                intent.putExtra( "url", bean.extend );
                                intent.putExtra( "title", title );
                                intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
                                context.startActivity( intent );
                                break;
                            case "xinshou":
                                /*新手教程界面*/
                                intent = new Intent( context, XinShouJiaoChengActivity.class );
                                intent.putExtra( "url", bean.extend );
                                intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
                                context.startActivity( intent );
                                break;
                            case "local_goods":
                                /*实例商品到商品详情*/
                                getShopBasicData( bean.extend );
                                break;
                            case "taobao_no_coupon":/*淘宝天猫不需要一键查询*/
                                intent = new Intent( context, TaobaoTianMaoHolidayOfActivity.class );
                                intent.putExtra( "url", bean.extend );
                                intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
                                context.startActivity( intent );
                                break;
                            case "ldms":/*0点秒杀*/
                                intent = new Intent( context, ZeroPointsGoodsActivity.class );
                                intent.putExtra( "goods_type", "ldms" );
                                intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
                                context.startActivity( intent );
                                break;
                            case "gysp":/*高佣金商品*/
                                intent = new Intent( context, ZeroPointsGoodsActivity.class );
                                intent.putExtra( "goods_type", "gysp" );
                                intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
                                context.startActivity( intent );
                                break;
                        }
                    } else {
                        switch (url) {
                            case "jkj":
                                /*9.9包邮*/
//                                intent = new Intent( context, NinePinkageActivity.class );
                                intent = new Intent( context, DaggerActvity.class );
                                intent.putExtra( "title", title );
                                intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
                                context.startActivity( intent );
                                break;
                            case "fqb":
                                /*疯抢榜*/
                                intent = new Intent( context, ShopRangingClassicActivity.class );
                                intent.putExtra( "title", title );
                                intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
                                context.startActivity( intent );
                                break;
                            case "jhs":
                                /*聚划算*/
                                intent = new Intent( context, KesalanPathActivity.class );
                                intent.putExtra( "title", title );
                                intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
                                context.startActivity( intent );
                                break;
                            case "tqg":
                                /*淘抢购*/
                                intent = new Intent( context, SuperMakeActivity.class );
                                intent.putExtra( "title", title );
                                intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
                                context.startActivity( intent );
                                break;
                            case "gwc":
                                /*淘宝购物车*/
                                intent = new Intent( context, TaobaoShoppingCartActivity.class );
                                intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
                                context.startActivity( intent );
                                break;
                            case "yqtz":
                                intent = new Intent( context, YaoQingFriendActivity.class );
                                intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
                                context.startActivity( intent );
                                break;
                            case "tgsc":
                                intent = new Intent( context, BaseH5Activity.class );
                                intent.putExtra( "url", bean.extend );
                                intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
                                context.startActivity( intent );
                                break;
                            case "upgrade":/*用户升级*/
                                intent = new Intent( context, GShenJiActivity.class );
                                intent.putExtra( "url", bean.extend );
                                intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
                                context.startActivity( intent );
                                break;
                        }
                    }
                } else {
                    intent = new Intent( context, LoginAndRegisterActivity.class );
                    intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
                    context.startActivity( intent );
                }
            }
        } );
        return itemView;
    }

    private class ViewHolder {
        private ImageView iv;
        private TextView name;
    }

    /*商品详情头部信息*/
    private void getShopBasicData(String shopId) {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put( "goods_id", shopId );
        String param = ParamUtil.getMapParam( map );
        MyApplication.getInstance().getMyOkHttp().post().url( Constant.BASE_URL + Constant.SHOP_HEAD_BASIC + "?" + param )
                .tag( this )
                .addHeader( "x-appid", Constant.APPID )
                .addHeader( "x-devid", PreferUtils.getString( context, Constant.PESUDOUNIQUEID ) )
                .addHeader( "x-nettype", PreferUtils.getString( context, Constant.NETWORKTYPE ) )
                .addHeader( "x-agent", VersionUtil.getVersionCode( context ) )
                .addHeader( "x-platform", Constant.ANDROID )
                .addHeader( "x-devtype", Constant.IMEI )
                .addHeader( "x-token", ParamUtil.GroupMap( context, "" ) )
                .enqueue( new JsonResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, JSONObject response) {
                        super.onSuccess( statusCode, response );
                        try {
                            JSONObject jsonObject = new JSONObject( response.toString() );
                            if (jsonObject.getInt( "status" ) >= 0) {
                                ShopBasicBean bean = GsonUtil.GsonToBean( response.toString(), ShopBasicBean.class );
                                if (bean == null) return;
                                ShopBasicBean.ShopBasicData result = bean.getResult();
                                JumpToShopDetailUtil.start2ActivityOfHeadBean( context, result );
                            } else {
                                String result = jsonObject.getString( "result" );
                                ToastUtils.showToast( context, result );
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, String error_msg) {
                        ToastUtils.showToast( context, Constant.NONET );
                    }
                } );
    }
}
