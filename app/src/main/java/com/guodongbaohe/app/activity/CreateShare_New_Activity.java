package com.guodongbaohe.app.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.guodongbaohe.app.OnItemClick;
import com.guodongbaohe.app.R;
import com.guodongbaohe.app.adapter.CreateShareForChoosePicsAdapter;
import com.guodongbaohe.app.base_activity.BaseActivity;
import com.guodongbaohe.app.bean.ChooseImagsNum;
import com.guodongbaohe.app.bean.TemplateBean;
import com.guodongbaohe.app.common_constant.Constant;
import com.guodongbaohe.app.common_constant.MyApplication;
import com.guodongbaohe.app.itemdecoration.PinLeiItemDecoration;
import com.guodongbaohe.app.myokhttputils.response.JsonResponseHandler;
import com.guodongbaohe.app.myutil.MyBitmapUtil;
import com.guodongbaohe.app.util.ClipContentUtil;
import com.guodongbaohe.app.util.CommonUtil;
import com.guodongbaohe.app.util.DensityUtils;
import com.guodongbaohe.app.util.DialogUtil;
import com.guodongbaohe.app.util.GsonUtil;
import com.guodongbaohe.app.util.IconAndTextGroupUtil;
import com.guodongbaohe.app.util.NetPicsToBitmap;
import com.guodongbaohe.app.util.NetUtil;
import com.guodongbaohe.app.util.NumUtil;
import com.guodongbaohe.app.util.ParamUtil;
import com.guodongbaohe.app.util.PreferUtils;
import com.guodongbaohe.app.util.QRCodeUtil;
import com.guodongbaohe.app.util.ShareManager;
import com.guodongbaohe.app.util.StringCleanZeroUtil;
import com.guodongbaohe.app.util.ToastUtils;
import com.guodongbaohe.app.util.Tools;
import com.guodongbaohe.app.util.VersionUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.wechat.moments.WechatMoments;

public class CreateShare_New_Activity extends BaseActivity {
    /*规则说明*/
    @BindView(R.id.re_guize)
    RelativeLayout re_guize;
    /*奖金金额*/
    @BindView(R.id.tv_bonus)
    TextView tv_bonus;
    /*图片列表*/
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    /*生成海报按钮*/
    @BindView(R.id.tv_buide_poster)
    TextView tv_buide_poster;
    /*文案内容显示*/
    @BindView(R.id.tv_official_content)
    TextView tv_official_content;
    /*恢复默认按钮*/
    @BindView(R.id.tv_huifu_moren)
    TextView tv_huifu_moren;
    /*编辑文案模板*/
    @BindView(R.id.edit_comment_template)
    TextView edit_comment_template;
    /*购买地址显示*/
    @BindView(R.id.re_buy_address_show)
    RelativeLayout re_buy_address_show;
    /*购买地址图片*/
    @BindView(R.id.iv_buy_show)
    ImageView iv_buy_show;
    /*淘口令显示*/
    @BindView(R.id.re_taokou_ling_show)
    RelativeLayout re_taokou_ling_show;
    /*淘口令图标*/
    @BindView(R.id.iv_taokou_ling_show)
    ImageView iv_taokou_ling_show;
    /*复制文案分享按钮*/
    @BindView(R.id.tv_copy_comment_shre)
    TextView tv_copy_comment_shre;
    /*淘口令纯评论框*/
    @BindView(R.id.re_plun)
    RelativeLayout re_plun;
    /*单独的淘口令信息*/
    @BindView(R.id.tv_tkl_content)
    TextView tv_tkl_content;
    /*淘口令模板编辑*/
    @BindView(R.id.tv_edit_taokling_muban)
    TextView tv_edit_taokling_muban;
    /*淘口令评论按钮*/
    @BindView(R.id.tkl_copy_comment)
    TextView tkl_copy_comment;
    /*微信好友*/
    @BindView(R.id.re_wchat_friend)
    RelativeLayout re_wchat_friend;
    /*微信朋友圈*/
    @BindView(R.id.re_wchat_circle)
    RelativeLayout re_wchat_circle;
    /*qq好友*/
    @BindView(R.id.re_qq_friend)
    RelativeLayout re_qq_friend;
    /*保存图片*/
    @BindView(R.id.re_qq_space)
    RelativeLayout re_qq_space;
    /*你能返布局*/
    @BindView(R.id.re_ninengfan_show)
    RelativeLayout re_ninengfan_show;
    @BindView(R.id.iv_fan_show)
    ImageView iv_fan_show;
    /*商品资源*/
    String goods_thumb, goods_gallery, goods_name, promo_slogan, attr_price, attr_prime, sale_num,
            attr_site, good_short, attr_ratio, goods_id, share_taokouling, share_qrcode, coupon_surplus;
    Intent intent;
    private List<String> pics_list;
    /*组装图片数据集合*/
    List<ChooseImagsNum> chooseImagsNumList;
    BigDecimal bg3;
    String member_role, member_id;
    double app_v;
    double v;
    Dialog loadingDialog;
    CreateShareForChoosePicsAdapter adapter;
    /*判断是否生成过推广海报  0 没有 ； 1生成过*/
    private int buidePoster = 0;
    /*记住选中的位置*/
    LinkedHashMap<Integer, Integer> choose_poition;
    List<Integer> allPosition;
    String title_sign = "{标题}";
    String shop_old_price = "{商品原价}";
    String shop_coupon_price = "{券后价}";
    String order_address_sign = "{下单链接}";
    String taokouling_sign = "{淘口令}";
    String tuijian_sign = "{推荐理由}";
    String ninengfan_sign = "{返佣}";
    /*微信朋友圈单张图片*/
    private List<String> wchatCirclePicsList;
    private int ainteger;
    String circle_pic_url;
    /*购买地址默认为true*/
    private boolean isBuyAddress = true;
    /*淘口令默认为true*/
    private boolean isTaoKouling = true;
    /*你能返默认为true*/
    private boolean isNinengfan = true;
    /*定义一个默认变量 表示没有读模板*/
    private int readMuBan = -1;
    String official_content, taokouling_content;
    /*购买地址选中是 1   没有选中是-1*/
    int buy_address_select = 1;
    /*淘口令选中是 1   没有选中是-1*/
    int taokouling_select = 1;
    /*你能返选中是 1 没有选中是-1*/
    int ninengfan_select = 1;
    double ninnegfan_money;

    @Override
    public int getContainerView() {
        return R.layout.createshare_new_activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        ButterKnife.bind( this );
        setMiddleTitle( "创建分享" );
        member_role = PreferUtils.getString( getApplicationContext(), "member_role" );
        member_id = PreferUtils.getString( getApplicationContext(), "member_id" );
        String tax_rate = PreferUtils.getString( getApplicationContext(), "tax_rate" );
        app_v = 1 - Double.valueOf( tax_rate );
        intent = getIntent();
        goods_thumb = intent.getStringExtra( "goods_thumb" );
        goods_gallery = intent.getStringExtra( "goods_gallery" );
        goods_name = intent.getStringExtra( "goods_name" );
        promo_slogan = intent.getStringExtra( "promo_slogan" );
        attr_price = intent.getStringExtra( "attr_price" );
        attr_prime = intent.getStringExtra( "attr_prime" );
        attr_site = intent.getStringExtra( "attr_site" );
        good_short = intent.getStringExtra( "good_short" );
        attr_ratio = intent.getStringExtra( "attr_ratio" );
        goods_id = intent.getStringExtra( "goods_id" );
        share_taokouling = intent.getStringExtra( "share_taokouling" );
        share_qrcode = intent.getStringExtra( "share_qrcode" );
        coupon_surplus = intent.getStringExtra( "coupon_surplus" );
        sale_num = intent.getStringExtra( "sale_num" );
        v = Double.valueOf( attr_prime ) - Double.valueOf( attr_price );
        double ninengfan = Double.valueOf( attr_price ) * Double.valueOf( attr_ratio ) * Constant.VIP_RATIO / 10000 * app_v;
        bg3 = new BigDecimal( ninengfan );
        ninnegfan_money = bg3.setScale( 2, BigDecimal.ROUND_HALF_UP ).doubleValue();
        initRecyclerView();/*图片初始化*/
        initPosterLayoutView();/*二维码海报布局*/
        initTemplateDataView();/*初始化文案数据*/
        initMoneyData();/*初始化金额数据*/
        initTaoKouLingDataView();/*初始化淘口令数据*/
    }

    private void initMoneyData() {
        if (Constant.BOSS_USER_LEVEL.contains( member_role )) {
            /*总裁用户*/
            rebateData( Constant.BOSS_RATIO );
        } else if (Constant.PARTNER_USER_LEVEL.contains( member_role )) {
            /*合伙人用户*/
            rebateData( Constant.PARTNER_RATIO );
        } else {
            /*vip用户*/
            rebateData( Constant.VIP_RATIO );
        }
    }

    /*用户级别显示你能返佣金数*/
    private void rebateData(int num) {
        double ninengfan = Double.valueOf( attr_price ) * Double.valueOf( attr_ratio ) * num / 10000 * app_v;
        bg3 = new BigDecimal( ninengfan );
        double money_ninneng = bg3.setScale( 2, BigDecimal.ROUND_HALF_UP ).doubleValue();
        tv_bonus.setText( "你的奖励预计为: ¥" + money_ninneng );
    }

    /*初始化文案数据*/
    private void initTemplateDataView() {
        official_content = PreferUtils.getString( getApplicationContext(), "official_content" );
        int template_is_save = PreferUtils.getInt( getApplicationContext(), "template_is_save" );
        if (TextUtils.isEmpty( official_content )) {
            /*调用接口数据*/
            getTemplateData( 0 );
        } else {
            if (template_is_save == 1) {
                readMuBan = 1;
            } else {
                readMuBan = -1;
            }
            if (official_content.contains( order_address_sign )) {
                iv_buy_show.setImageResource( R.mipmap.xainshiyjin );
                isBuyAddress = true;
                buy_address_select = 1;
            } else {
                iv_buy_show.setImageResource( R.mipmap.buxianshiyjin );
                isBuyAddress = false;
                buy_address_select = -1;
            }
            if (official_content.contains( taokouling_sign )) {
                iv_taokou_ling_show.setImageResource( R.mipmap.xainshiyjin );
                isTaoKouling = true;
                taokouling_select = 1;
            } else {
                iv_taokou_ling_show.setImageResource( R.mipmap.buxianshiyjin );
                isTaoKouling = false;
                taokouling_select = -1;
            }
            if (official_content.contains( ninengfan_sign )) {
                iv_fan_show.setImageResource( R.mipmap.xainshiyjin );
                isNinengfan = true;
                ninengfan_select = 1;
            } else {
                iv_fan_show.setImageResource( R.mipmap.buxianshiyjin );
                isNinengfan = false;
                ninengfan_select = -1;
            }
            if (TextUtils.isEmpty( good_short )) {
                official_content = official_content.replace( title_sign, goods_name );
            } else {
                official_content = official_content.replace( title_sign, good_short );
            }
            official_content = official_content.replace( shop_old_price, StringCleanZeroUtil.DoubleFormat( Double.valueOf( attr_prime ) ) );
            official_content = official_content.replace( shop_coupon_price, StringCleanZeroUtil.DoubleFormat( Double.valueOf( attr_price ) ) );
            official_content = official_content.replace( ninengfan_sign, String.valueOf( ninnegfan_money ) );
            official_content = official_content.replace( order_address_sign, share_qrcode );
            if (!TextUtils.isEmpty( promo_slogan )) {
                official_content = official_content.replace( tuijian_sign, promo_slogan );
            } else {
                official_content = official_content.replace( "\n" + tuijian_sign, "" );
            }
            official_content = official_content.replace( taokouling_sign, share_taokouling );
            tv_official_content.setText( official_content );
        }
    }

    /*初始化淘口令数据*/
    private void initTaoKouLingDataView() {
        taokouling_content = PreferUtils.getString( getApplicationContext(), "taokouling_content" );
        if (TextUtils.isEmpty( taokouling_content )) {
            getOnlyTaoKouLinData();
        } else {
            taokouling_content = taokouling_content.replace( taokouling_sign, share_taokouling );
            taokouling_content = taokouling_content.replace( order_address_sign, share_qrcode );
            tv_tkl_content.setText( taokouling_content );
        }
    }

    /*仅仅获取文案模板数据*/
    private void getTemplateData(final int mode) {
        if (mode > 0) {
            loadingDialog = DialogUtil.createLoadingDialog( CreateShare_New_Activity.this, "恢复中..." );
        }
        MyApplication.getInstance().getMyOkHttp().post().url( Constant.BASE_URL + Constant.NEW_TAMPLATE_DATA )
                .tag( this )
                .addHeader( "x-appid", Constant.APPID )
                .addHeader( "x-devid", PreferUtils.getString( getApplicationContext(), Constant.PESUDOUNIQUEID ) )
                .addHeader( "x-nettype", PreferUtils.getString( getApplicationContext(), Constant.NETWORKTYPE ) )
                .addHeader( "x-agent", VersionUtil.getVersionCode( getApplicationContext() ) )
                .addHeader( "x-platform", Constant.ANDROID )
                .addHeader( "x-devtype", Constant.IMEI )
                .addHeader( "x-token", ParamUtil.GroupMap( getApplicationContext(), "" ) )
                .enqueue( new JsonResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, JSONObject response) {
                        super.onSuccess( statusCode, response );
                        DialogUtil.closeDialog( loadingDialog, CreateShare_New_Activity.this );
                        Log.i( "打印模板看看", response.toString() );
                        try {
                            JSONObject jsonObject = new JSONObject( response.toString() );
                            if (jsonObject.getInt( "status" ) >= 0) {
                                TemplateBean bean = GsonUtil.GsonToBean( response.toString(), TemplateBean.class );
                                if (bean == null) return;
                                official_content = bean.getResult().getContent();
                                if (official_content == null) return;
                                PreferUtils.putString( getApplicationContext(), "official_content", official_content );
                                if (TextUtils.isEmpty( good_short )) {
                                    official_content = official_content.replace( title_sign, goods_name );
                                } else {
                                    official_content = official_content.replace( title_sign, good_short );
                                }
                                official_content = official_content.replace( shop_old_price, StringCleanZeroUtil.DoubleFormat( Double.valueOf( attr_prime ) ) );
                                official_content = official_content.replace( shop_coupon_price, StringCleanZeroUtil.DoubleFormat( Double.valueOf( attr_price ) ) );
                                official_content = official_content.replace( ninengfan_sign, String.valueOf( ninnegfan_money ) );
                                official_content = official_content.replace( order_address_sign, share_qrcode );
                                if (!TextUtils.isEmpty( promo_slogan )) {
                                    official_content = official_content.replace( tuijian_sign, promo_slogan );
                                } else {
                                    official_content = official_content.replace( "\n" + tuijian_sign, "" );
                                }
                                official_content = official_content.replace( taokouling_sign, share_taokouling );
                                tv_official_content.setText( official_content );
                                if (mode == 0 || mode == 1) {
                                    iv_buy_show.setImageResource( R.mipmap.xainshiyjin );
                                    iv_taokou_ling_show.setImageResource( R.mipmap.xainshiyjin );
                                    iv_fan_show.setImageResource( R.mipmap.xainshiyjin );
                                }
                                if (mode == 1) {
                                    ToastUtils.showToast( getApplicationContext(), "已恢复" );
                                }
                                isBuyAddress = true;
                                isTaoKouling = true;
                                isNinengfan = true;
                                buy_address_select = 1;
                                taokouling_select = 1;
                                ninengfan_select = 1;
                                PreferUtils.putInt( getApplicationContext(), "template_is_save", -1 );
                            } else {
                                String result = jsonObject.getString( "result" );
                                ToastUtils.showToast( getApplicationContext(), result );
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, String error_msg) {
                        DialogUtil.closeDialog( loadingDialog, CreateShare_New_Activity.this );
                        ToastUtils.showToast( getApplicationContext(), Constant.NONET );
                    }
                } );
    }

    /*仅仅只获取淘口令模板数据*/
    private void getOnlyTaoKouLinData() {
        MyApplication.getInstance().getMyOkHttp().post().url( Constant.BASE_URL + Constant.NEW_TAMPLATE_DATA )
                .tag( this )
                .addHeader( "x-appid", Constant.APPID )
                .addHeader( "x-devid", PreferUtils.getString( getApplicationContext(), Constant.PESUDOUNIQUEID ) )
                .addHeader( "x-nettype", PreferUtils.getString( getApplicationContext(), Constant.NETWORKTYPE ) )
                .addHeader( "x-agent", VersionUtil.getVersionCode( getApplicationContext() ) )
                .addHeader( "x-platform", Constant.ANDROID )
                .addHeader( "x-devtype", Constant.IMEI )
                .addHeader( "x-token", ParamUtil.GroupMap( getApplicationContext(), "" ) )
                .enqueue( new JsonResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, JSONObject response) {
                        super.onSuccess( statusCode, response );
                        DialogUtil.closeDialog( loadingDialog, CreateShare_New_Activity.this );
                        Log.i( "打印模板看看", response.toString() );
                        try {
                            JSONObject jsonObject = new JSONObject( response.toString() );
                            if (jsonObject.getInt( "status" ) >= 0) {
                                TemplateBean bean = GsonUtil.GsonToBean( response.toString(), TemplateBean.class );
                                if (bean == null) return;
                                taokouling_content = bean.getResult().getComment();
                                if (TextUtils.isEmpty( taokouling_content )) return;
                                PreferUtils.putString( getApplicationContext(), "taokouling_content", taokouling_content );
                                taokouling_content = taokouling_content.replace( taokouling_sign, share_taokouling );
                                taokouling_content = taokouling_content.replace( order_address_sign, share_qrcode );
                                tv_tkl_content.setText( taokouling_content );
                            } else {
                                String result = jsonObject.getString( "result" );
                                ToastUtils.showToast( getApplicationContext(), result );
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, String error_msg) {
                        DialogUtil.closeDialog( loadingDialog, CreateShare_New_Activity.this );
                        ToastUtils.showToast( getApplicationContext(), Constant.NONET );
                    }
                } );
    }

    /*图片布局显示*/
    private void initRecyclerView() {
        recyclerview.setHasFixedSize( true );
        recyclerview.setNestedScrollingEnabled( false );
        LinearLayoutManager manager = new LinearLayoutManager( getApplicationContext() );
        manager.setOrientation( LinearLayoutManager.HORIZONTAL );
        recyclerview.setLayoutManager( manager );
        pics_list = new ArrayList<>();
        chooseImagsNumList = new ArrayList<>();
        pics_list.add( goods_thumb );
        if (!TextUtils.isEmpty( goods_gallery )) {
            String[] split = goods_gallery.split( "," );
            for (int i = 0; i < split.length; i++) {
                pics_list.add( split[i] );
            }
        }
        for (int i = 0; i < pics_list.size(); i++) {
            ChooseImagsNum bean = new ChooseImagsNum();
            bean.setUrl( pics_list.get( i ) );
            chooseImagsNumList.add( bean );
        }
        recyclerview.addItemDecoration( new PinLeiItemDecoration( DensityUtils.dip2px( getApplicationContext(), 10 ), pics_list ) );
        adapter = new CreateShareForChoosePicsAdapter( getApplicationContext(), chooseImagsNumList );
        recyclerview.setAdapter( adapter );
        adapter.setonclicklistener( new OnItemClick() {
            @Override
            public void OnItemClickListener(View view, int position) {
                intent = new Intent( getApplicationContext(), PosterPhotoViewPagerActivity.class );
                intent.putExtra( "current", position );
                intent.putExtra( "mode", buidePoster );
                intent.putStringArrayListExtra( "pics_list", (ArrayList<String>) pics_list );
                startActivity( intent );
                overridePendingTransition( R.anim.ap2, R.anim.ap1 );
            }
        } );
        choose_poition = new LinkedHashMap<>();
        chooseImagsNumList.get( 0 ).setChecked( true );
        choose_poition.put( 0, 0 );
        adapter.notifyDataSetChanged();
        adapter.setOnCheckedListener( new OnItemClick() {
            @Override
            public void OnItemClickListener(View view, int position) {
                chooseImagsNumList.get( position ).setChecked( !chooseImagsNumList.get( position ).isChecked() );
                if (chooseImagsNumList.get( position ).isChecked()) {
                    choose_poition.put( position, position );
                } else {
                    choose_poition.remove( position );
                }
                adapter.notifyItemChanged( position );
            }
        } );
    }

    View qrcode_poster_view;
    /*中间的大图*/
    ImageView p_iv;
    /*标题*/
    TextView p_title;
    /*销量*/
    TextView p_sale_num;
    /*原价*/
    TextView p_old_price;
    /*券类型 1*/
    TextView tv_coupon_type;
    /*多少元券*/
    TextView tv_coupon_money;
    /*优惠券类型*/
    TextView tv_jia_type;
    /*券后价*/
    TextView p_coupon_price;
    /*二维码图片*/
    ImageView iv_qr_code;

    /*二维码海报布局*/
    private void initPosterLayoutView() {
        qrcode_poster_view = LayoutInflater.from( getApplicationContext() ).inflate( R.layout.creation_second_share_poster, null );
        p_iv = qrcode_poster_view.findViewById( R.id.p_iv );
        p_title = qrcode_poster_view.findViewById( R.id.p_title );
        p_sale_num = qrcode_poster_view.findViewById( R.id.p_sale_num );
        p_old_price = qrcode_poster_view.findViewById( R.id.p_old_price );
        tv_coupon_type = qrcode_poster_view.findViewById( R.id.tv_coupon_type );
        tv_coupon_money = qrcode_poster_view.findViewById( R.id.tv_coupon_money );
        tv_jia_type = qrcode_poster_view.findViewById( R.id.tv_jia_type );
        p_coupon_price = qrcode_poster_view.findViewById( R.id.p_coupon_price );
        iv_qr_code = qrcode_poster_view.findViewById( R.id.iv_qr_code );
        if (Double.valueOf( coupon_surplus ) > 0) {
            tv_coupon_type.setText( "券" );
            tv_jia_type.setText( "券后价 ¥ " );
            double d_price = Double.valueOf( attr_prime ) - Double.valueOf( attr_price );
            bg3 = new BigDecimal( d_price );
            double d_money = bg3.setScale( 2, BigDecimal.ROUND_HALF_UP ).doubleValue();
            tv_coupon_money.setText( StringCleanZeroUtil.DoubleFormat( d_money ) + "元券" );
        } else {
            if (v > 0) {
                tv_coupon_type.setText( "折" );
                tv_jia_type.setText( "折后价 ¥ " );
                double disaccount = Double.valueOf( attr_price ) / Double.valueOf( attr_prime ) * 10;
                bg3 = new BigDecimal( disaccount );
                double d_zhe = bg3.setScale( 1, BigDecimal.ROUND_HALF_UP ).doubleValue();
                tv_coupon_money.setText( d_zhe + "折" );
            } else {
                tv_coupon_type.setText( "特" );
                tv_jia_type.setText( "特惠价 ¥ " );
                tv_coupon_money.setText( "立即抢购" );
            }
        }
        p_sale_num.setText( "销量: " + NumUtil.getNum( sale_num ) );
        IconAndTextGroupUtil.setTextView( getApplicationContext(), p_title, goods_name, attr_site );
        p_coupon_price.setText( StringCleanZeroUtil.DoubleFormat( Double.valueOf( attr_price ) ) );
        p_old_price.setText( " ¥" + StringCleanZeroUtil.DoubleFormat( Double.valueOf( attr_prime ) ) );
        p_old_price.getPaint().setFlags( Paint.STRIKE_THRU_TEXT_FLAG ); //中间横线
        p_old_price.getPaint().setAntiAlias( true );// 抗锯齿
        Bitmap mBitmap = QRCodeUtil.createQRCodeBitmap( share_qrcode, DensityUtils.dip2px( getApplicationContext(), 100 ) );
        iv_qr_code.setImageBitmap( mBitmap );
    }

    @OnClick({R.id.tv_buide_poster, R.id.edit_comment_template, R.id.tkl_copy_comment, R.id.tv_copy_comment_shre
            , R.id.re_wchat_friend, R.id.re_wchat_circle, R.id.re_qq_friend, R.id.re_qq_space, R.id.re_guize
            , R.id.tv_huifu_moren, R.id.re_buy_address_show, R.id.re_taokou_ling_show, R.id.tv_edit_taokling_muban, R.id.re_ninengfan_show})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.tv_buide_poster:/*点击生成海报按钮*/
                if (NetUtil.getNetWorkState( CreateShare_New_Activity.this ) < 0) {
                    ToastUtils.showToast( getApplicationContext(), "您的网络异常，请联网重试" );
                    return;
                }
                if (buidePoster == 1) {
                    ToastUtils.showToast( getApplicationContext(), "已经生成过推广海报" );
                } else {
                    allPosition = new ArrayList<>();
                    Collection<Integer> values = choose_poition.values();// 得到全部的value
                    Iterator<Integer> iter = values.iterator();
                    while (iter.hasNext()) {
                        Integer str = iter.next();
                        allPosition.add( str );
                    }
                    Collections.sort( allPosition );
                    loadingDialog = DialogUtil.createLoadingDialog( CreateShare_New_Activity.this, "生成中..." );
                    if (allPosition.size() == 0) {
                        Glide.with( getApplicationContext() ).load( pics_list.get( 0 ) ).asBitmap().into( target );
                    } else {
                        Glide.with( getApplicationContext() ).load( pics_list.get( allPosition.get( 0 ) ) ).asBitmap().into( target );
                    }
                }
                break;
            case R.id.edit_comment_template:/*编辑文案模板按钮*/
                intent = new Intent( getApplicationContext(), EditCommentTemplateActivity.class );
                startActivityForResult( intent, 100 );
                break;
            case R.id.tkl_copy_comment:/*复制评论按钮*/
                String pl_content = tv_tkl_content.getText().toString().trim();
                ClipboardManager cm = (ClipboardManager) getSystemService( Context.CLIPBOARD_SERVICE );
                if (cm.hasPrimaryClip()) {
                    cm.getPrimaryClip().getItemAt( 0 ).getText();
                }
                ClipData mClipData = ClipData.newPlainText( "Label", pl_content );
                cm.setPrimaryClip( mClipData );
                ClipContentUtil.getInstance( getApplicationContext() ).putNewSearch( pl_content );//保存记录到数据库
                ToastUtils.showBackgroudCenterToast( getApplicationContext(), "淘口令复制成功" );
                break;
            case R.id.tv_copy_comment_shre:/*复制文案按钮*/
                copyWenAnFunction();
                break;
            case R.id.re_wchat_friend:/*微信好友*/
                if (NetUtil.getNetWorkState( CreateShare_New_Activity.this ) < 0) {
                    ToastUtils.showToast( getApplicationContext(), "您的网络异常，请联网重试" );
                    return;
                }
                if (choose_poition.size() == 0) {
                    ToastUtils.showToast( getApplicationContext(), "至少勾选一张图片才能分享" );
                    return;
                }
                if (ContextCompat.checkSelfPermission( CreateShare_New_Activity.this, Manifest.permission.READ_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission( CreateShare_New_Activity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED) {
                    //没有存储权限
                    ActivityCompat.requestPermissions( CreateShare_New_Activity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1 );
                } else {
                    copyWenAnFunction();
                    if (buidePoster == 1) {
                        /*生成过海报*/
                        wChatFriendPicShare( 0 );
                    } else {
                        /*没有生成过海报*/
                        wChatFriendPicShare( 1 );
                    }
                }
                break;
            case R.id.re_wchat_circle:/*朋友圈分享*/
                if (NetUtil.getNetWorkState( CreateShare_New_Activity.this ) < 0) {
                    ToastUtils.showToast( getApplicationContext(), "您的网络异常，请联网重试" );
                    return;
                }
                if (choose_poition.size() == 0) {
                    ToastUtils.showToast( getApplicationContext(), "至少勾选一张图片才能分享" );
                    return;
                }
                if (ContextCompat.checkSelfPermission( CreateShare_New_Activity.this, Manifest.permission.READ_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission( CreateShare_New_Activity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED) {
                    //没有存储权限
                    ActivityCompat.requestPermissions( CreateShare_New_Activity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2 );
                } else {
                    copyWenAnFunction();
                    /*朋友圈分享*/
                    circleCommonFunction();
                }
                break;
            case R.id.re_qq_space:/*批量下载*/
                if (NetUtil.getNetWorkState( CreateShare_New_Activity.this ) < 0) {
                    ToastUtils.showToast( getApplicationContext(), "您的网络异常，请联网重试" );
                    return;
                }
                if (choose_poition.size() == 0) {
                    ToastUtils.showToast( getApplicationContext(), "至少勾选一张图片才能保存" );
                    return;
                }
                if (ContextCompat.checkSelfPermission( CreateShare_New_Activity.this, Manifest.permission.READ_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission( CreateShare_New_Activity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED) {
                    //没有存储权限
                    ActivityCompat.requestPermissions( CreateShare_New_Activity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 4 );
                } else {
                    savePicsToLocal();
                }
                break;
            case R.id.re_guize:/*规则*/
//                intent = new Intent(getApplicationContext(), ShareDetailActivity.class);
//                intent.putExtra("url", PreferUtils.getString(getApplicationContext(), "share_goods"));
//                startActivity(intent);
                break;
            case R.id.re_qq_friend:/*qq好友分享*/
                if (NetUtil.getNetWorkState( CreateShare_New_Activity.this ) < 0) {
                    ToastUtils.showToast( getApplicationContext(), "您的网络异常，请联网重试" );
                    return;
                }
                if (choose_poition.size() == 0) {
                    ToastUtils.showToast( getApplicationContext(), "至少勾选一张图片才能分享" );
                    return;
                }
                if (ContextCompat.checkSelfPermission( CreateShare_New_Activity.this, Manifest.permission.READ_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission( CreateShare_New_Activity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED) {
                    //没有存储权限
                    ActivityCompat.requestPermissions( CreateShare_New_Activity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 3 );
                } else {
                    copyWenAnFunction();
                    /*qq好友分享*/
                    qqFriendShareCommon();
                }
                break;
            case R.id.tv_huifu_moren:/*恢复默认*/
                readMuBan = -1;
                getTemplateData( 1 );
                break;
            case R.id.re_buy_address_show:/*购买地址*/
                official_content = PreferUtils.getString( getApplicationContext(), "official_content" );/*获取模板内容*/
                if (isBuyAddress) {

                    /*隐藏购买地址*/
                    iv_buy_show.setImageResource( R.mipmap.buxianshiyjin );
                    buy_address_select = -1;

                    if (readMuBan == -1) {
                        /*模板初始化状态*/

                        if (TextUtils.isEmpty( good_short )) {
                            official_content = official_content.replace( title_sign, goods_name );
                        } else {
                            official_content = official_content.replace( title_sign, good_short );
                        }
                        official_content = official_content.replace( shop_old_price, StringCleanZeroUtil.DoubleFormat( Double.valueOf( attr_prime ) ) );
                        official_content = official_content.replace( shop_coupon_price, StringCleanZeroUtil.DoubleFormat( Double.valueOf( attr_price ) ) );
                        official_content = official_content.replace( "\n【下单链接】" + order_address_sign, "" );
                        if (!TextUtils.isEmpty( promo_slogan )) {
                            official_content = official_content.replace( tuijian_sign, promo_slogan );
                        } else {
                            official_content = official_content.replace( "\n" + tuijian_sign, "" );
                        }
                        if (taokouling_select == 1) {/*淘口令显示在*/
                            official_content = official_content.replace( taokouling_sign, share_taokouling );
                        } else {/*淘口令隐藏在*/
                            official_content = official_content.replace( "\n长按復至" + taokouling_sign + "，➡[掏✔寳]即可抢购", "" );
                        }
                        if (ninengfan_select == 1) {/*你能返显示在*/
                            official_content = official_content.replace( ninengfan_sign, String.valueOf( ninnegfan_money ) );
                        } else {/*你能返隐藏在*/
                            official_content = official_content.replace( "\n【下载果冻宝盒再省】{返佣}元", "" );
                        }

                    } else {/*模板更改过*/
                        if (official_content.contains( order_address_sign )) {

                            /*模板包含{下单链接}*/
                            if (TextUtils.isEmpty( good_short )) {
                                official_content = official_content.replace( title_sign, goods_name );
                            } else {
                                official_content = official_content.replace( title_sign, good_short );
                            }
                            official_content = official_content.replace( shop_old_price, StringCleanZeroUtil.DoubleFormat( Double.valueOf( attr_prime ) ) );
                            official_content = official_content.replace( shop_coupon_price, StringCleanZeroUtil.DoubleFormat( Double.valueOf( attr_price ) ) );
                            official_content = official_content.replace( order_address_sign, "" );
                            if (!TextUtils.isEmpty( promo_slogan )) {
                                official_content = official_content.replace( tuijian_sign, promo_slogan );
                            } else {
                                official_content = official_content.replace( "\n" + tuijian_sign, "" );
                            }

                            if (taokouling_select == 1) {/*淘口令显示在*/
                                if (official_content.contains( taokouling_sign )) {/*编辑模板包含淘口令*/
                                    official_content = official_content.replace( taokouling_sign, share_taokouling );
                                } else {/*编辑模板不包含淘口令*/
                                    official_content = official_content + "\n长按復至" + share_taokouling + "，➡[掏✔寳]即可抢购";
                                }
                            } else {/*淘口令隐藏在*/
                                if (official_content.contains( taokouling_sign )) {/*编辑模板包含淘口令*/
                                    official_content = official_content.replace( taokouling_sign, "" );
                                } else {/*编辑模板不包含淘口令*/
                                    official_content = official_content.replace( "\n长按復至" + share_taokouling + "，➡[掏✔寳]即可抢购", "" );
                                }
                            }

                            if (ninengfan_select == 1) {/*你能返显示在*/
                                if (official_content.contains( ninengfan_sign )) {/*编辑模板包含你能返*/
                                    official_content = official_content.replace( ninengfan_sign, String.valueOf( ninnegfan_money ) );
                                } else {/*编辑模板不包含淘口令*/
                                    official_content = official_content + "\n【下载果冻宝盒再省】" + ninnegfan_money + "元";
                                }
                            } else {/*你能返隐藏在*/
                                if (official_content.contains( ninengfan_sign )) {/*编辑模板包含你能返*/
                                    official_content = official_content.replace( ninengfan_sign, "" );
                                } else {/*编辑模板不包含你能返*/
                                    official_content = official_content.replace( "\n【下载果冻宝盒再省】" + ninnegfan_money + "元", "" );
                                }
                            }

                        } else {

                            /*模板不包含下单链接*/
                            if (TextUtils.isEmpty( good_short )) {
                                official_content = official_content.replace( title_sign, goods_name );
                            } else {
                                official_content = official_content.replace( title_sign, good_short );
                            }
                            official_content = official_content.replace( shop_old_price, StringCleanZeroUtil.DoubleFormat( Double.valueOf( attr_prime ) ) );
                            official_content = official_content.replace( shop_coupon_price, StringCleanZeroUtil.DoubleFormat( Double.valueOf( attr_price ) ) );
                            official_content = official_content.replace( "\n【下单链接】" + share_qrcode, "" );
                            if (!TextUtils.isEmpty( promo_slogan )) {
                                official_content = official_content.replace( tuijian_sign, promo_slogan );
                            } else {
                                official_content = official_content.replace( "\n" + tuijian_sign, "" );
                            }

                            if (taokouling_select == 1) {/*淘口令显示在*/
                                if (official_content.contains( taokouling_sign )) {/*编辑模板包含淘口令*/
                                    official_content = official_content.replace( taokouling_sign, share_taokouling );
                                } else {/*编辑模板不包含淘口令*/
                                    official_content = official_content + "\n长按復至" + share_taokouling + "，➡[掏✔寳]即可抢购";
                                }
                            } else {/*淘口令隐藏在*/
                                if (official_content.contains( taokouling_sign )) {/*编辑模板包含淘口令*/
                                    official_content = official_content.replace( taokouling_sign, "" );
                                } else {/*编辑模板不包含淘口令*/
                                    official_content = official_content.replace( "\n长按復至" + share_taokouling + "，➡[掏✔寳]即可抢购", "" );
                                }
                            }

                            if (ninengfan_select == 1) {/*你能返显示在*/
                                if (official_content.contains( ninengfan_sign )) {/*编辑模板包含你能返*/
                                    official_content = official_content.replace( ninengfan_sign, String.valueOf( ninnegfan_money ) );
                                } else {/*编辑模板不包含淘口令*/
                                    official_content = official_content + "\n【下载果冻宝盒再省】" + ninnegfan_money + "元";
                                }
                            } else {/*你能返隐藏在*/
                                if (official_content.contains( ninengfan_sign )) {/*编辑模板包含你能返*/
                                    official_content = official_content.replace( ninengfan_sign, "" );
                                } else {/*编辑模板不包含你能返*/
                                    official_content = official_content.replace( "\n【下载果冻宝盒再省】" + ninnegfan_money + "元", "" );
                                }
                            }

                        }
                    }

                } else {

                    /*显示购买地址*/
                    iv_buy_show.setImageResource( R.mipmap.xainshiyjin );
                    buy_address_select = 1;

                    if (readMuBan == -1) {/*模板初始化状态*/

                        if (TextUtils.isEmpty( good_short )) {
                            official_content = official_content.replace( title_sign, goods_name );
                        } else {
                            official_content = official_content.replace( title_sign, good_short );
                        }
                        official_content = official_content.replace( shop_old_price, StringCleanZeroUtil.DoubleFormat( Double.valueOf( attr_prime ) ) );
                        official_content = official_content.replace( shop_coupon_price, StringCleanZeroUtil.DoubleFormat( Double.valueOf( attr_price ) ) );
                        official_content = official_content.replace( order_address_sign, share_qrcode );
                        if (!TextUtils.isEmpty( promo_slogan )) {
                            official_content = official_content.replace( tuijian_sign, promo_slogan );
                        } else {
                            official_content = official_content.replace( "\n" + tuijian_sign, "" );
                        }
                        if (taokouling_select == 1) {/*淘口令显示在*/
                            official_content = official_content.replace( taokouling_sign, share_taokouling );
                        } else {/*淘口令隐藏在*/
                            official_content = official_content.replace( "\n长按復至" + taokouling_sign + "，➡[掏✔寳]即可抢购", "" );
                        }
                        if (ninengfan_select == 1) {/*你能返显示在*/
                            official_content = official_content.replace( ninengfan_sign, String.valueOf( ninnegfan_money ) );
                        } else {/*你能返隐藏在*/
                            official_content = official_content.replace( "\n【下载果冻宝盒再省】{返佣}元", "" );
                        }
                        tv_official_content.setText( official_content );

                    } else {/*模板更改过*/
                        if (official_content.contains( order_address_sign )) {/*模板包含{下单链接}*/
                            if (TextUtils.isEmpty( good_short )) {
                                official_content = official_content.replace( title_sign, goods_name );
                            } else {
                                official_content = official_content.replace( title_sign, good_short );
                            }
                            official_content = official_content.replace( shop_old_price, StringCleanZeroUtil.DoubleFormat( Double.valueOf( attr_prime ) ) );
                            official_content = official_content.replace( shop_coupon_price, StringCleanZeroUtil.DoubleFormat( Double.valueOf( attr_price ) ) );
                            official_content = official_content.replace( order_address_sign, share_qrcode );
                            if (!TextUtils.isEmpty( promo_slogan )) {
                                official_content = official_content.replace( tuijian_sign, promo_slogan );
                            } else {
                                official_content = official_content.replace( "\n" + tuijian_sign, "" );
                            }

                            if (taokouling_select == 1) {/*淘口令显示在*/
                                if (official_content.contains( taokouling_sign )) {/*编辑模板包含淘口令*/
                                    official_content = official_content.replace( taokouling_sign, share_taokouling );
                                } else {/*编辑模板不包含淘口令*/
                                    official_content = official_content + "\n长按復至" + share_taokouling + "，➡[掏✔寳]即可抢购";
                                }
                            } else {/*淘口令隐藏在*/
                                if (official_content.contains( taokouling_sign )) {/*编辑模板包含淘口令*/
                                    official_content = official_content.replace( taokouling_sign, "" );
                                } else {/*编辑模板不包含淘口令*/
                                    official_content = official_content.replace( "\n长按復至" + share_taokouling + "，➡[掏✔寳]即可抢购", "" );
                                }
                            }

                            if (ninengfan_select == 1) {/*你能返显示在*/
                                if (official_content.contains( ninengfan_sign )) {/*编辑模板包含你能返*/
                                    official_content = official_content.replace( ninengfan_sign, String.valueOf( ninnegfan_money ) );
                                } else {/*编辑模板不包含淘口令*/
                                    official_content = official_content + "\n【下载果冻宝盒再省】" + ninnegfan_money + "元";
                                }
                            } else {/*你能返隐藏在*/
                                if (official_content.contains( ninengfan_sign )) {/*编辑模板包含你能返*/
                                    official_content = official_content.replace( ninengfan_sign, "" );
                                } else {/*编辑模板不包含你能返*/
                                    official_content = official_content.replace( "\n【下载果冻宝盒再省】" + ninnegfan_money + "元", "" );
                                }
                            }

                        } else {/*模板不包含下单链接*/
                            if (TextUtils.isEmpty( good_short )) {
                                official_content = official_content.replace( title_sign, goods_name );
                            } else {
                                official_content = official_content.replace( title_sign, good_short );
                            }
                            official_content = official_content.replace( shop_old_price, StringCleanZeroUtil.DoubleFormat( Double.valueOf( attr_prime ) ) );
                            official_content = official_content.replace( shop_coupon_price, StringCleanZeroUtil.DoubleFormat( Double.valueOf( attr_price ) ) );
                            if (!TextUtils.isEmpty( promo_slogan )) {
                                official_content = official_content.replace( tuijian_sign, promo_slogan );
                            } else {
                                official_content = official_content.replace( "\n" + tuijian_sign, "" );
                            }
                            if (taokouling_select == 1) {/*淘口令显示在*/
                                if (official_content.contains( taokouling_sign )) {/*编辑模板包含淘口令*/
                                    official_content = official_content.replace( taokouling_sign, share_taokouling );
                                } else {/*编辑模板不包含淘口令*/
                                    official_content = official_content + "\n长按復至" + share_taokouling + "，➡[掏✔寳]即可抢购";
                                }
                            } else {/*淘口令隐藏在*/
                                if (official_content.contains( taokouling_sign )) {/*编辑模板包含淘口令*/
                                    official_content = official_content.replace( taokouling_sign, "" );
                                } else {/*编辑模板不包含淘口令*/
                                    official_content = official_content.replace( "\n长按復至" + share_taokouling + "，➡[掏✔寳]即可抢购", "" );
                                }
                            }

                            if (ninengfan_select == 1) {/*你能返显示在*/
                                if (official_content.contains( ninengfan_sign )) {/*编辑模板包含你能返*/
                                    official_content = official_content.replace( ninengfan_sign, String.valueOf( ninnegfan_money ) );
                                } else {/*编辑模板不包含淘口令*/
                                    official_content = official_content + "\n【下载果冻宝盒再省】" + ninnegfan_money + "元";
                                }
                            } else {/*你能返隐藏在*/
                                if (official_content.contains( ninengfan_sign )) {/*编辑模板包含你能返*/
                                    official_content = official_content.replace( ninengfan_sign, "" );
                                } else {/*编辑模板不包含你能返*/
                                    official_content = official_content.replace( "\n【下载果冻宝盒再省】" + ninnegfan_money + "元", "" );
                                }
                            }

                            official_content = official_content + "\n【下单链接】" + share_qrcode;
                        }
                    }
                }
                tv_official_content.setText( official_content );
                isBuyAddress = !isBuyAddress;
                break;
            case R.id.re_taokou_ling_show:/*淘口令点击显示*/
                official_content = PreferUtils.getString( getApplicationContext(), "official_content" );/*获取模板内容*/
                if (isTaoKouling) {
                    /*隐藏淘口令*/
                    taokouling_select = -1;
                    iv_taokou_ling_show.setImageResource( R.mipmap.buxianshiyjin );

                    if (readMuBan == -1) {/*模板初始化状态*/

                        if (TextUtils.isEmpty( good_short )) {
                            official_content = official_content.replace( title_sign, goods_name );
                        } else {
                            official_content = official_content.replace( title_sign, good_short );
                        }
                        official_content = official_content.replace( shop_old_price, StringCleanZeroUtil.DoubleFormat( Double.valueOf( attr_prime ) ) );
                        official_content = official_content.replace( shop_coupon_price, StringCleanZeroUtil.DoubleFormat( Double.valueOf( attr_price ) ) );
                        if (buy_address_select == 1) {/*购买地址显示在*/
                            official_content = official_content.replace( order_address_sign, share_qrcode );
                        } else {/*购买地址隐藏在*/
                            official_content = official_content.replace( "\n【下单链接】" + order_address_sign, "" );
                        }
                        if (ninengfan_select == 1) {/*你能返显示在*/
                            official_content = official_content.replace( ninengfan_sign, String.valueOf( ninnegfan_money ) );
                        } else {/*你能返隐藏在*/
                            official_content = official_content.replace( "\n【下载果冻宝盒再省】{返佣}元", "" );
                        }
                        if (!TextUtils.isEmpty( promo_slogan )) {
                            official_content = official_content.replace( tuijian_sign, promo_slogan );
                        } else {
                            official_content = official_content.replace( "\n" + tuijian_sign, "" );
                        }
                        official_content = official_content.replace( "\n长按復至" + taokouling_sign + "，➡[掏✔寳]即可抢购", "" );

                    } else {/*模板更改后的状态*/

                        if (official_content.contains( taokouling_sign )) {

                            /*编辑模板包含淘口令标识*/
                            if (TextUtils.isEmpty( good_short )) {
                                official_content = official_content.replace( title_sign, goods_name );
                            } else {
                                official_content = official_content.replace( title_sign, good_short );
                            }
                            official_content = official_content.replace( shop_old_price, StringCleanZeroUtil.DoubleFormat( Double.valueOf( attr_prime ) ) );
                            official_content = official_content.replace( shop_coupon_price, StringCleanZeroUtil.DoubleFormat( Double.valueOf( attr_price ) ) );
                            if (!TextUtils.isEmpty( promo_slogan )) {
                                official_content = official_content.replace( tuijian_sign, promo_slogan );
                            } else {
                                official_content = official_content.replace( "\n" + tuijian_sign, "" );
                            }
                            official_content = official_content.replace( taokouling_sign, "" );
                            if (buy_address_select == 1) {/*购买地址显示在*/
                                if (official_content.contains( order_address_sign )) {/*编辑模板包含下单链接*/
                                    official_content = official_content.replace( order_address_sign, share_qrcode );
                                } else {/*编辑模板不包含下单链接*/
                                    official_content = official_content + "\n【下单链接】" + share_qrcode;
                                }
                            } else {/*购买地址隐藏在*/
                                if (official_content.contains( order_address_sign )) {/*编辑模板包含下单链接*/
                                    official_content = official_content.replace( order_address_sign, "" );
                                } else {/*编辑模板不包含下单链接*/
                                    official_content = official_content.replace( "\n【下单链接】" + share_qrcode, "" );
                                }
                            }

                            if (ninengfan_select == 1) {/*你能返显示在*/
                                if (official_content.contains( ninengfan_sign )) {/*编辑模板包含你能返*/
                                    official_content = official_content.replace( ninengfan_sign, String.valueOf( ninnegfan_money ) );
                                } else {/*编辑模板不包含淘口令*/
                                    official_content = official_content + "\n【下载果冻宝盒再省】" + ninnegfan_money + "元";
                                }
                            } else {/*你能返隐藏在*/
                                if (official_content.contains( ninengfan_sign )) {/*编辑模板包含你能返*/
                                    official_content = official_content.replace( ninengfan_sign, "" );
                                } else {/*编辑模板不包含你能返*/
                                    official_content = official_content.replace( "\n【下载果冻宝盒再省】" + ninnegfan_money + "元", "" );
                                }
                            }

                        } else {

                            /*编辑模板不包含淘口令标识*/
                            if (TextUtils.isEmpty( good_short )) {
                                official_content = official_content.replace( title_sign, goods_name );
                            } else {
                                official_content = official_content.replace( title_sign, good_short );
                            }
                            official_content = official_content.replace( shop_old_price, StringCleanZeroUtil.DoubleFormat( Double.valueOf( attr_prime ) ) );
                            official_content = official_content.replace( shop_coupon_price, StringCleanZeroUtil.DoubleFormat( Double.valueOf( attr_price ) ) );
                            if (!TextUtils.isEmpty( promo_slogan )) {
                                official_content = official_content.replace( tuijian_sign, promo_slogan );
                            } else {
                                official_content = official_content.replace( "\n" + tuijian_sign, "" );
                            }
                            official_content = official_content.replace( "\n长按復至" + share_taokouling + "，➡[掏✔寳]即可抢购", "" );

                            if (buy_address_select == 1) {/*购买地址显示在*/
                                if (official_content.contains( order_address_sign )) {/*编辑模板包含下单链接*/
                                    official_content = official_content.replace( order_address_sign, share_qrcode );
                                } else {/*编辑模板不包含下单链接*/
                                    official_content = official_content + "\n【下单链接】" + share_qrcode;
                                }
                            } else {/*购买地址隐藏在*/
                                if (official_content.contains( order_address_sign )) {/*编辑模板包含下单链接*/
                                    official_content = official_content.replace( order_address_sign, "" );
                                } else {/*编辑模板不包含下单链接*/
                                    official_content = official_content.replace( "\n【下单链接】" + share_qrcode, "" );
                                }
                            }

                            if (ninengfan_select == 1) {/*你能返显示在*/
                                if (official_content.contains( ninengfan_sign )) {/*编辑模板包含你能返*/
                                    official_content = official_content.replace( ninengfan_sign, String.valueOf( ninnegfan_money ) );
                                } else {/*编辑模板不包含淘口令*/
                                    official_content = official_content + "\n【下载果冻宝盒再省】" + ninnegfan_money + "元";
                                }
                            } else {/*你能返隐藏在*/
                                if (official_content.contains( ninengfan_sign )) {/*编辑模板包含你能返*/
                                    official_content = official_content.replace( ninengfan_sign, "" );
                                } else {/*编辑模板不包含你能返*/
                                    official_content = official_content.replace( "\n【下载果冻宝盒再省】" + ninnegfan_money + "元", "" );
                                }
                            }

                        }

                    }

                } else {

                    /*显示淘口令*/
                    taokouling_select = 1;
                    iv_taokou_ling_show.setImageResource( R.mipmap.xainshiyjin );

                    if (readMuBan == -1) {/*模板初始化状态*/

                        if (TextUtils.isEmpty( good_short )) {
                            official_content = official_content.replace( title_sign, goods_name );
                        } else {
                            official_content = official_content.replace( title_sign, good_short );
                        }
                        official_content = official_content.replace( shop_old_price, StringCleanZeroUtil.DoubleFormat( Double.valueOf( attr_prime ) ) );
                        official_content = official_content.replace( shop_coupon_price, StringCleanZeroUtil.DoubleFormat( Double.valueOf( attr_price ) ) );
                        if (buy_address_select == 1) {/*购买地址显示在*/
                            official_content = official_content.replace( order_address_sign, share_qrcode );
                        } else {/*购买地址隐藏在*/
                            official_content = official_content.replace( "\n【下单链接】" + order_address_sign, "" );
                        }
                        if (ninengfan_select == 1) {/*你能返显示在*/
                            official_content = official_content.replace( ninengfan_sign, String.valueOf( ninnegfan_money ) );
                        } else {/*你能返隐藏在*/
                            official_content = official_content.replace( "\n【下载果冻宝盒再省】{返佣}元", "" );
                        }
                        if (!TextUtils.isEmpty( promo_slogan )) {
                            official_content = official_content.replace( tuijian_sign, promo_slogan );
                        } else {
                            official_content = official_content.replace( "\n" + tuijian_sign, "" );
                        }
                        official_content = official_content.replace( taokouling_sign, share_taokouling );

                    } else {
                        /*模板更改后*/
                        if (official_content.contains( taokouling_sign )) {

                            /*编辑模板包含淘口令*/
                            if (TextUtils.isEmpty( good_short )) {
                                official_content = official_content.replace( title_sign, goods_name );
                            } else {
                                official_content = official_content.replace( title_sign, good_short );
                            }
                            official_content = official_content.replace( shop_old_price, StringCleanZeroUtil.DoubleFormat( Double.valueOf( attr_prime ) ) );
                            official_content = official_content.replace( shop_coupon_price, StringCleanZeroUtil.DoubleFormat( Double.valueOf( attr_price ) ) );
                            if (!TextUtils.isEmpty( promo_slogan )) {
                                official_content = official_content.replace( tuijian_sign, promo_slogan );
                            } else {
                                official_content = official_content.replace( "\n" + tuijian_sign, "" );
                            }
                            official_content = official_content.replace( taokouling_sign, share_taokouling );

                            if (buy_address_select == 1) {/*购买地址显示在*/
                                if (official_content.contains( order_address_sign )) {/*编辑模板包含下单链接*/
                                    official_content = official_content.replace( order_address_sign, share_qrcode );
                                } else {/*编辑模板不包含下单链接*/
                                    official_content = official_content + "\n【下单链接】" + share_qrcode;
                                }
                            } else {/*购买地址隐藏在*/
                                if (official_content.contains( order_address_sign )) {/*编辑模板包含下单链接*/
                                    official_content = official_content.replace( order_address_sign, "" );
                                } else {/*编辑模板不包含下单链接*/
                                    official_content = official_content.replace( "\n【下单链接】" + share_qrcode, "" );
                                }
                            }

                            if (ninengfan_select == 1) {/*你能返显示在*/
                                if (official_content.contains( ninengfan_sign )) {/*编辑模板包含你能返*/
                                    official_content = official_content.replace( ninengfan_sign, String.valueOf( ninnegfan_money ) );
                                } else {/*编辑模板不包含淘口令*/
                                    official_content = official_content + "\n【下载果冻宝盒再省】" + ninnegfan_money + "元";
                                }
                            } else {/*你能返隐藏在*/
                                if (official_content.contains( ninengfan_sign )) {/*编辑模板包含你能返*/
                                    official_content = official_content.replace( ninengfan_sign, "" );
                                } else {/*编辑模板不包含你能返*/
                                    official_content = official_content.replace( "\n【下载果冻宝盒再省】" + ninnegfan_money + "元", "" );
                                }
                            }

                        } else {

                            /*编辑模板不包含淘口令标识*/
                            if (TextUtils.isEmpty( good_short )) {
                                official_content = official_content.replace( title_sign, goods_name );
                            } else {
                                official_content = official_content.replace( title_sign, good_short );
                            }
                            official_content = official_content.replace( shop_old_price, StringCleanZeroUtil.DoubleFormat( Double.valueOf( attr_prime ) ) );
                            official_content = official_content.replace( shop_coupon_price, StringCleanZeroUtil.DoubleFormat( Double.valueOf( attr_price ) ) );
                            if (!TextUtils.isEmpty( promo_slogan )) {
                                official_content = official_content.replace( tuijian_sign, promo_slogan );
                            } else {
                                official_content = official_content.replace( "\n" + tuijian_sign, "" );
                            }
                            official_content = official_content + "\n长按復至" + share_taokouling + "，➡[掏✔寳]即可抢购";

                            if (buy_address_select == 1) {/*购买地址显示在*/
                                if (official_content.contains( order_address_sign )) {/*编辑模板包含下单链接*/
                                    official_content = official_content.replace( order_address_sign, share_qrcode );
                                } else {/*编辑模板不包含下单链接*/
                                    official_content = official_content + "\n【下单链接】" + share_qrcode;
                                }
                            } else {/*购买地址隐藏在*/
                                if (official_content.contains( order_address_sign )) {/*编辑模板包含下单链接*/
                                    official_content = official_content.replace( order_address_sign, "" );
                                } else {/*编辑模板不包含下单链接*/
                                    official_content = official_content.replace( "\n【下单链接】" + share_qrcode, "" );
                                }
                            }

                            if (ninengfan_select == 1) {/*你能返显示在*/
                                if (official_content.contains( ninengfan_sign )) {/*编辑模板包含你能返*/
                                    official_content = official_content.replace( ninengfan_sign, String.valueOf( ninnegfan_money ) );
                                } else {/*编辑模板不包含淘口令*/
                                    official_content = official_content + "\n【下载果冻宝盒再省】" + ninnegfan_money + "元";
                                }
                            } else {/*你能返隐藏在*/
                                if (official_content.contains( ninengfan_sign )) {/*编辑模板包含你能返*/
                                    official_content = official_content.replace( ninengfan_sign, "" );
                                } else {/*编辑模板不包含你能返*/
                                    official_content = official_content.replace( "\n【下载果冻宝盒再省】" + ninnegfan_money + "元", "" );
                                }
                            }

                        }
                    }
                }
                tv_official_content.setText( official_content );
                isTaoKouling = !isTaoKouling;
                break;
            case R.id.tv_edit_taokling_muban:/*编辑淘口令模板*/
                intent = new Intent( getApplicationContext(), EditTaoKouLingTemplateActivity.class );
                startActivityForResult( intent, 100 );
                break;
            case R.id.re_ninengfan_show:/*你能返点击*/
                official_content = PreferUtils.getString( getApplicationContext(), "official_content" );/*获取模板内容*/
                if (isNinengfan) {/*现在是隐藏*/
                    iv_fan_show.setImageResource( R.mipmap.buxianshiyjin );
                    ninengfan_select = -1;

                    if (readMuBan == -1) {/*模板初始化状态*/

                        if (TextUtils.isEmpty( good_short )) {
                            official_content = official_content.replace( title_sign, goods_name );
                        } else {
                            official_content = official_content.replace( title_sign, good_short );
                        }
                        official_content = official_content.replace( shop_old_price, StringCleanZeroUtil.DoubleFormat( Double.valueOf( attr_prime ) ) );
                        official_content = official_content.replace( shop_coupon_price, StringCleanZeroUtil.DoubleFormat( Double.valueOf( attr_price ) ) );
                        official_content = official_content.replace( "\n【下载果冻宝盒再省】{返佣}元", "" );
                        if (buy_address_select == 1) {/*购买地址显示在*/
                            official_content = official_content.replace( order_address_sign, share_qrcode );
                        } else {/*购买地址隐藏在*/
                            official_content = official_content.replace( "\n【下单链接】{下单链接}", "" );
                        }
                        if (!TextUtils.isEmpty( promo_slogan )) {
                            official_content = official_content.replace( tuijian_sign, promo_slogan );
                        } else {
                            official_content = official_content.replace( "\n" + tuijian_sign, "" );
                        }
                        if (taokouling_select == 1) {/*淘口令显示在*/
                            official_content = official_content.replace( taokouling_sign, share_taokouling );
                        } else {/*淘口令隐藏在*/
                            official_content = official_content.replace( "\n长按復至{淘口令}，➡[掏✔寳]即可抢购", "" );
                        }

                    } else {/*模板更改后的状态*/

                        /*编辑模板包含你能返标识*/
                        if (official_content.contains( ninengfan_sign )) {

                            if (TextUtils.isEmpty( good_short )) {
                                official_content = official_content.replace( title_sign, goods_name );
                            } else {
                                official_content = official_content.replace( title_sign, good_short );
                            }
                            official_content = official_content.replace( shop_old_price, StringCleanZeroUtil.DoubleFormat( Double.valueOf( attr_prime ) ) );
                            official_content = official_content.replace( shop_coupon_price, StringCleanZeroUtil.DoubleFormat( Double.valueOf( attr_price ) ) );
                            if (!TextUtils.isEmpty( promo_slogan )) {
                                official_content = official_content.replace( tuijian_sign, promo_slogan );
                            } else {
                                official_content = official_content.replace( "\n" + tuijian_sign, "" );
                            }
                            official_content = official_content.replace( ninengfan_sign, "" );
                            if (buy_address_select == 1) {/*购买地址显示在*/
                                if (official_content.contains( order_address_sign )) {/*编辑模板包含下单链接*/
                                    official_content = official_content.replace( order_address_sign, share_qrcode );
                                } else {/*编辑模板不包含下单链接*/
                                    official_content = official_content + "\n【下单链接】" + share_qrcode;
                                }
                            } else {/*购买地址隐藏在*/
                                if (official_content.contains( order_address_sign )) {/*编辑模板包含下单链接*/
                                    official_content = official_content.replace( order_address_sign, "" );
                                } else {/*编辑模板不包含下单链接*/
                                    official_content = official_content.replace( "\n【下单链接】" + share_qrcode, "" );
                                }
                            }

                            if (taokouling_select == 1) {/*淘口令显示在*/
                                if (official_content.contains( taokouling_sign )) {/*编辑模板包含淘口令*/
                                    official_content = official_content.replace( taokouling_sign, share_taokouling );
                                } else {/*编辑模板不包含淘口令*/
                                    official_content = official_content + "\n长按復至" + share_taokouling + "，➡[掏✔寳]即可抢购";
                                }
                            } else {/*淘口令隐藏在*/
                                if (official_content.contains( taokouling_sign )) {/*编辑模板包含淘口令*/
                                    official_content = official_content.replace( taokouling_sign, "" );
                                } else {/*编辑模板不包含淘口令*/
                                    official_content = official_content.replace( "\n长按復至" + share_taokouling + "，➡[掏✔寳]即可抢购", "" );
                                }
                            }

                        } else {
                            /*编辑模板不包含你能返标识*/

                            if (TextUtils.isEmpty( good_short )) {
                                official_content = official_content.replace( title_sign, goods_name );
                            } else {
                                official_content = official_content.replace( title_sign, good_short );
                            }
                            official_content = official_content.replace( shop_old_price, StringCleanZeroUtil.DoubleFormat( Double.valueOf( attr_prime ) ) );
                            official_content = official_content.replace( shop_coupon_price, StringCleanZeroUtil.DoubleFormat( Double.valueOf( attr_price ) ) );
                            if (!TextUtils.isEmpty( promo_slogan )) {
                                official_content = official_content.replace( tuijian_sign, promo_slogan );
                            } else {
                                official_content = official_content.replace( "\n" + tuijian_sign, "" );
                            }
                            official_content = official_content.replace( "\n【下载果冻宝盒再省】" + ninnegfan_money + "元", "" );

                            if (buy_address_select == 1) {/*购买地址显示在*/
                                if (official_content.contains( order_address_sign )) {/*编辑模板包含下单链接*/
                                    official_content = official_content.replace( order_address_sign, share_qrcode );
                                } else {/*编辑模板不包含下单链接*/
                                    official_content = official_content + "\n【下单链接】" + share_qrcode;
                                }
                            } else {/*购买地址隐藏在*/
                                if (official_content.contains( order_address_sign )) {/*编辑模板包含下单链接*/
                                    official_content = official_content.replace( order_address_sign, "" );
                                } else {/*编辑模板不包含下单链接*/
                                    official_content = official_content.replace( "\n【下单链接】" + share_qrcode, "" );
                                }
                            }

                            if (taokouling_select == 1) {/*淘口令显示在*/
                                if (official_content.contains( taokouling_sign )) {/*编辑模板包含淘口令*/
                                    official_content = official_content.replace( taokouling_sign, share_taokouling );
                                } else {/*编辑模板不包含淘口令*/
                                    official_content = official_content + "\n长按復至" + share_taokouling + "，➡[掏✔寳]即可抢购";
                                }
                            } else {/*淘口令隐藏在*/
                                if (official_content.contains( taokouling_sign )) {/*编辑模板包含淘口令*/
                                    official_content = official_content.replace( taokouling_sign, "" );
                                } else {/*编辑模板不包含淘口令*/
                                    official_content = official_content.replace( "\n长按復至" + share_taokouling + "，➡[掏✔寳]即可抢购", "" );
                                }
                            }

                        }

                    }

                } else {/*现在是显示*/

                    iv_fan_show.setImageResource( R.mipmap.xainshiyjin );
                    ninengfan_select = 1;

                    if (readMuBan == -1) {/*模板初始化状态*/

                        if (TextUtils.isEmpty( good_short )) {
                            official_content = official_content.replace( title_sign, goods_name );
                        } else {
                            official_content = official_content.replace( title_sign, good_short );
                        }
                        official_content = official_content.replace( shop_old_price, StringCleanZeroUtil.DoubleFormat( Double.valueOf( attr_prime ) ) );
                        official_content = official_content.replace( shop_coupon_price, StringCleanZeroUtil.DoubleFormat( Double.valueOf( attr_price ) ) );
                        official_content = official_content.replace( ninengfan_sign, String.valueOf( ninnegfan_money ) );
                        if (buy_address_select == 1) {/*购买地址显示在*/
                            official_content = official_content.replace( order_address_sign, share_qrcode );
                        } else {/*购买地址隐藏在*/
                            official_content = official_content.replace( "\n【下单链接】" + order_address_sign, "" );
                        }
                        if (!TextUtils.isEmpty( promo_slogan )) {
                            official_content = official_content.replace( tuijian_sign, promo_slogan );
                        } else {
                            official_content = official_content.replace( "\n" + tuijian_sign, "" );
                        }

                        if (taokouling_select == 1) {/*淘口令显示在*/
                            official_content = official_content.replace( taokouling_sign, share_taokouling );
                        } else {/*淘口令隐藏在*/
                            official_content = official_content.replace( "\n长按復至" + taokouling_sign + "，➡[掏✔寳]即可抢购", "" );
                        }

                    } else {
                        /*模板更改后*/

                        if (official_content.contains( ninengfan_sign )) {
                            /*编辑模板包含你能返*/

                            if (TextUtils.isEmpty( good_short )) {
                                official_content = official_content.replace( title_sign, goods_name );
                            } else {
                                official_content = official_content.replace( title_sign, good_short );
                            }
                            official_content = official_content.replace( shop_old_price, StringCleanZeroUtil.DoubleFormat( Double.valueOf( attr_prime ) ) );
                            official_content = official_content.replace( shop_coupon_price, StringCleanZeroUtil.DoubleFormat( Double.valueOf( attr_price ) ) );
                            if (!TextUtils.isEmpty( promo_slogan )) {
                                official_content = official_content.replace( tuijian_sign, promo_slogan );
                            } else {
                                official_content = official_content.replace( "\n" + tuijian_sign, "" );
                            }
                            official_content = official_content.replace( ninengfan_sign, String.valueOf( ninnegfan_money ) );

                            if (buy_address_select == 1) {/*购买地址显示在*/
                                if (official_content.contains( order_address_sign )) {/*编辑模板包含下单链接*/
                                    official_content = official_content.replace( order_address_sign, share_qrcode );
                                } else {/*编辑模板不包含下单链接*/
                                    official_content = official_content + "\n【下单链接】" + share_qrcode;
                                }
                            } else {/*购买地址隐藏在*/
                                if (official_content.contains( order_address_sign )) {/*编辑模板包含下单链接*/
                                    official_content = official_content.replace( order_address_sign, "" );
                                } else {/*编辑模板不包含下单链接*/
                                    official_content = official_content.replace( "\n【下单链接】" + share_qrcode, "" );
                                }
                            }

                            if (taokouling_select == 1) {/*淘口令显示在*/
                                if (official_content.contains( taokouling_sign )) {/*编辑模板包含淘口令*/
                                    official_content = official_content.replace( taokouling_sign, share_taokouling );
                                } else {/*编辑模板不包含淘口令*/
                                    official_content = official_content + "\n长按復至" + share_taokouling + "，➡[掏✔寳]即可抢购";
                                }
                            } else {/*淘口令隐藏在*/
                                if (official_content.contains( taokouling_sign )) {/*编辑模板包含淘口令*/
                                    official_content = official_content.replace( taokouling_sign, "" );
                                } else {/*编辑模板不包含淘口令*/
                                    official_content = official_content.replace( "\n长按復至" + share_taokouling + "，➡[掏✔寳]即可抢购", "" );
                                }
                            }

                        } else {
                            /*编辑模板不包含你能返标识*/

                            if (TextUtils.isEmpty( good_short )) {
                                official_content = official_content.replace( title_sign, goods_name );
                            } else {
                                official_content = official_content.replace( title_sign, good_short );
                            }
                            official_content = official_content.replace( shop_old_price, StringCleanZeroUtil.DoubleFormat( Double.valueOf( attr_prime ) ) );
                            official_content = official_content.replace( shop_coupon_price, StringCleanZeroUtil.DoubleFormat( Double.valueOf( attr_price ) ) );
                            if (!TextUtils.isEmpty( promo_slogan )) {
                                official_content = official_content.replace( tuijian_sign, promo_slogan );
                            } else {
                                official_content = official_content.replace( "\n" + tuijian_sign, "" );
                            }
                            official_content = official_content + "\n【下载果冻宝盒再省】" + ninnegfan_money + "元";

                            if (buy_address_select == 1) {/*购买地址显示在*/
                                if (official_content.contains( order_address_sign )) {/*编辑模板包含下单链接*/
                                    official_content = official_content.replace( order_address_sign, share_qrcode );
                                } else {/*编辑模板不包含下单链接*/
                                    official_content = official_content + "\n【下单链接】" + share_qrcode;
                                }
                            } else {/*购买地址隐藏在*/
                                if (official_content.contains( order_address_sign )) {/*编辑模板包含下单链接*/
                                    official_content = official_content.replace( order_address_sign, "" );
                                } else {/*编辑模板不包含下单链接*/
                                    official_content = official_content.replace( "\n【下单链接】" + share_qrcode, "" );
                                }
                            }

                            if (taokouling_select == 1) {/*淘口令显示在*/
                                if (official_content.contains( taokouling_sign )) {/*编辑模板包含淘口令*/
                                    official_content = official_content.replace( taokouling_sign, share_taokouling );
                                } else {/*编辑模板不包含淘口令*/
                                    official_content = official_content + "\n长按復至" + share_taokouling + "，➡[掏✔寳]即可抢购";
                                }
                            } else {/*淘口令隐藏在*/
                                if (official_content.contains( taokouling_sign )) {/*编辑模板包含淘口令*/
                                    official_content = official_content.replace( taokouling_sign, "" );
                                } else {/*编辑模板不包含淘口令*/
                                    official_content = official_content.replace( "\n长按復至" + share_taokouling + "，➡[掏✔寳]即可抢购", "" );
                                }
                            }
                        }
                    }
                }
                tv_official_content.setText( official_content );
                isNinengfan = !isNinengfan;
                break;
        }
    }

    /*qq好友分享*/
    private void qqFriendShareCommon() {
        if (buidePoster == 1) {
            /*qq好友生成过海报*/
            qqFriendHavePoster( 0 );
        } else {
            /*qq好友没有生成过海报*/
            qqFriendHavePoster( 1 );
        }
    }

    /*qq好友*/
    private void qqFriendHavePoster(int mode) {
        ShareManager shareManager = new ShareManager( CreateShare_New_Activity.this );
        allPosition = new ArrayList<>();
        Collection<Integer> values = choose_poition.values();// 得到全部的value
        Iterator<Integer> iter = values.iterator();
        while (iter.hasNext()) {
            Integer str = iter.next();
            allPosition.add( str );
        }
        Collections.sort( allPosition );
        share_imgs = new ArrayList<>();
        for (int i = 0; i < allPosition.size(); i++) {
            share_imgs.add( chooseImagsNumList.get( allPosition.get( i ) ).getUrl() );
        }
        shareManager.setShareImage( hebingBitmap, 0, share_imgs, "", "qq", mode );
    }

    /*朋友圈分享公共方法*/
    private void circleCommonFunction() {
        allPosition = new ArrayList<>();
        Collection<Integer> values = choose_poition.values();// 得到全部的value
        Iterator<Integer> iter = values.iterator();
        while (iter.hasNext()) {
            Integer str = iter.next();
            allPosition.add( str );
        }
        Collections.sort( allPosition );
        if (allPosition.size() == 1) {
            /*单张图片分享用sharesdk*/
            wchatCirclePicsList = new ArrayList<>();
            ainteger = 0;
            for (int i = 0; i < allPosition.size(); i++) {
                ainteger = allPosition.get( i );
                circle_pic_url = chooseImagsNumList.get( ainteger ).getUrl();
            }
            wchatFriendCircle( ainteger, circle_pic_url );
        } else {
            /*微信朋友圈多张图片分享*/
            wchatCirlcePicsShare();
        }
    }

    /*直接保存图片*/
    private void savePicsToLocal() {
        if (buidePoster == 1) {
            /*生成过海报图片 去批量下载*/
            havePosterPicsSaveLocal();
        } else {
            /*没有生成过海报图片 去批量下载*/
            netPicsSaveLocal();
        }
    }

    /*微信朋友圈多张图片分享*/
    private void wchatCirlcePicsShare() {
        /*由于微信机制不让分享多图直接到微信朋友圈*/
        WChatCircleDialog();
        if (buidePoster == 1) {
            /*生成过海报图片 去批量下载*/
            havePosterPicsSaveLocal();
        } else {
            /*没有生成过海报图片 去批量下载*/
            netPicsSaveLocal();
        }
    }

    /*生成过海报图片 去批量下载*/
    private void havePosterPicsSaveLocal() {
        allPosition = new ArrayList<>();
        Collection<Integer> values = choose_poition.values();// 得到全部的value
        Iterator<Integer> iter = values.iterator();
        while (iter.hasNext()) {
            Integer str = iter.next();
            allPosition.add( str );
        }
        Collections.sort( allPosition );
        share_imgs = new ArrayList<>();
        for (int i = 0; i < allPosition.size(); i++) {
            if (allPosition.get( i ) == 0) {
                Bitmap bitmap = NetPicsToBitmap.convertStringToIcon( chooseImagsNumList.get( 0 ).getUrl() );
                CommonUtil.saveBitmap2file( bitmap, getApplicationContext() );
            }
            share_imgs.add( chooseImagsNumList.get( allPosition.get( i ) ).getUrl() );
        }
        saveMorePhotoToLocal( 1, share_imgs );
    }

    /*没有生成过海报图片 去批量下载*/
    private void netPicsSaveLocal() {
        allPosition = new ArrayList<>();
        Collection<Integer> values = choose_poition.values();// 得到全部的value
        Iterator<Integer> iter = values.iterator();
        while (iter.hasNext()) {
            Integer str = iter.next();
            allPosition.add( str );
        }
        Collections.sort( allPosition );
        share_imgs = new ArrayList<>();
        for (int i = 0; i < allPosition.size(); i++) {
            share_imgs.add( chooseImagsNumList.get( allPosition.get( i ) ).getUrl() );
        }
        saveMorePhotoToLocal( 0, share_imgs );
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            Bitmap bitmap = (Bitmap) msg.obj;
            CommonUtil.saveBitmap2file( bitmap, getApplicationContext() );
        }
    };

    /*微信朋友圈单图分享*/
    private void wchatFriendCircle(int mode, String url) {
        WechatMoments.ShareParams sp = new WechatMoments.ShareParams();
        if (buidePoster == 1) {
            if (mode == 0) {
                Bitmap bitmap = NetPicsToBitmap.convertStringToIcon( url );
                sp.setImageData( bitmap );
            } else {
                sp.setImageUrl( url );
            }
        } else {
            sp.setImageUrl( url );
        }
        sp.setShareType( Platform.SHARE_IMAGE );
        Platform weChat = ShareSDK.getPlatform( WechatMoments.NAME );
        weChat.setPlatformActionListener( new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
            }

            @Override
            public void onCancel(Platform platform, int i) {
            }
        } );
        weChat.share( sp );
    }


    List<String> share_imgs;

    /*微信好友分享*/
    private void wChatFriendPicShare(int mode) {
        ShareManager shareManager = new ShareManager( CreateShare_New_Activity.this );
        allPosition = new ArrayList<>();
        Collection<Integer> values = choose_poition.values();// 得到全部的value
        Iterator<Integer> iter = values.iterator();
        while (iter.hasNext()) {
            Integer str = iter.next();
            allPosition.add( str );
        }
        Collections.sort( allPosition );
        share_imgs = new ArrayList<>();
        for (int i = 0; i < allPosition.size(); i++) {
            share_imgs.add( chooseImagsNumList.get( allPosition.get( i ) ).getUrl() );
        }
        shareManager.setShareImage( hebingBitmap, 0, share_imgs, "", "wchat", mode );
    }

    /*复制文案方法*/
    private void copyWenAnFunction() {
        String copy_cotent = tv_official_content.getText().toString().trim();
        ClipboardManager cm = (ClipboardManager) getSystemService( Context.CLIPBOARD_SERVICE );
        if (cm.hasPrimaryClip()) {
            cm.getPrimaryClip().getItemAt( 0 ).getText();
        }
        ClipData mClipData = ClipData.newPlainText( "Label", copy_cotent );
        cm.setPrimaryClip( mClipData );
        ClipContentUtil.getInstance( getApplicationContext() ).putNewSearch( copy_cotent );//保存记录到数据库
        ToastUtils.showBackgroudCenterToast( getApplicationContext(), "文案复制成功" );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult( requestCode, resultCode, data );
        if (requestCode == 100) {
            switch (resultCode) {
                case 100:/*文案保存回调*/
                    initTemplateDataView();
                    break;
                case 300:/*淘口令保存回调*/
                    initTaoKouLingDataView();
                    break;
            }
        }
    }

    private SimpleTarget target = new SimpleTarget<Bitmap>() {
        @Override
        public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
            p_iv.setImageBitmap( bitmap );
            viewSaveToImage( qrcode_poster_view );
        }
    };

    Bitmap hebingBitmap;

    private void viewSaveToImage(View view) {
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics( metric );
        int width = metric.widthPixels;
        int height = metric.heightPixels;
        hebingBitmap = MyBitmapUtil.createBitmapOfNew( getApplicationContext(), view, width, height );
//        if (hebingBitmap.getRowBytes() * hebingBitmap.getHeight() > 1024) {
//            hebingBitmap = MyBitmapUtil.compressImage( hebingBitmap );
//        }
        String one_buide_pic = NetPicsToBitmap.convertIconToString( hebingBitmap );
        PreferUtils.putString( getApplicationContext(), "one_buide_pic", one_buide_pic );
        ChooseImagsNum bean = new ChooseImagsNum();
        bean.setUrl( one_buide_pic );
        bean.setChecked( true );
        chooseImagsNumList.add( 0, bean );
        for (ChooseImagsNum list : chooseImagsNumList) {
            list.setChecked( false );
        }
        chooseImagsNumList.get( 0 ).setChecked( true );
        adapter.refreDataChange( 1 );
        buidePoster = 1;
        choose_poition.clear();
        choose_poition.put( 0, 0 );
        DialogUtil.closeDialog( loadingDialog, CreateShare_New_Activity.this );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DialogUtil.closeDialog( loadingDialog, CreateShare_New_Activity.this );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                /*存储权限回调*/
                /*微信好友分享回调*/
                if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    ToastUtils.showToast( getApplicationContext(), "需要打开存储权限，请前往设置-应用-果冻宝盒-权限进行设置" );
                    return;
                } else if (grantResults.length <= 1 || grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                    ToastUtils.showToast( getApplicationContext(), "需要打开存储权限，请前往设置-应用-果冻宝盒-权限进行设置" );
                    return;
                } else if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults.length > 1 && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    if (buidePoster == 1) {
                        /*生成过海报*/
                        wChatFriendPicShare( 0 );
                    } else {
                        /*没有生成过海报*/
                        wChatFriendPicShare( 1 );
                    }
                }
                break;
            case 2:
                if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    ToastUtils.showToast( getApplicationContext(), "需要打开存储权限，请前往设置-应用-果冻宝盒-权限进行设置" );
                    return;
                } else if (grantResults.length <= 1 || grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                    ToastUtils.showToast( getApplicationContext(), "需要打开存储权限，请前往设置-应用-果冻宝盒-权限进行设置" );
                    return;
                } else if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults.length > 1 && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    circleCommonFunction();
                }
                break;
            case 3:
                if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    ToastUtils.showToast( getApplicationContext(), "需要打开存储权限，请前往设置-应用-果冻宝盒-权限进行设置" );
                    return;
                } else if (grantResults.length <= 1 || grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                    ToastUtils.showToast( getApplicationContext(), "需要打开存储权限，请前往设置-应用-果冻宝盒-权限进行设置" );
                    return;
                } else if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults.length > 1 && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    qqFriendShareCommon();
                }
                break;
            case 4:
                if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    ToastUtils.showToast( getApplicationContext(), "需要打开存储权限，请前往设置-应用-果冻宝盒-权限进行设置" );
                    return;
                } else if (grantResults.length <= 1 || grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                    ToastUtils.showToast( getApplicationContext(), "需要打开存储权限，请前往设置-应用-果冻宝盒-权限进行设置" );
                    return;
                } else if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults.length > 1 && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    savePicsToLocal();
                }
                break;
        }
    }

    Dialog dialog;

    /*多图分享到微信时弹框到微信app*/
    private void WChatCircleDialog() {
        dialog = new Dialog( CreateShare_New_Activity.this, R.style.transparentFrameWindowStyle );
        dialog.setContentView( R.layout.wchatcircle_dialog_pics );
        Window window = dialog.getWindow();
        window.setGravity( Gravity.CENTER | Gravity.CENTER );
        TextView dismiss = (TextView) dialog.findViewById( R.id.dismiss );
        dismiss.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        } );
        TextView open_wx = (TextView) dialog.findViewById( R.id.open_wx );
        open_wx.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Tools.isAppAvilible( getApplicationContext(), "com.tencent.mm" )) {
                    ToastUtils.showToast( getApplicationContext(), "您还没有安装微信客户端,请先安转客户端" );
                    return;
                }
                Intent intent = new Intent( Intent.ACTION_MAIN );
                ComponentName cmp = new ComponentName( "com.tencent.mm", "com.tencent.mm.ui.LauncherUI" );
                intent.addCategory( Intent.CATEGORY_LAUNCHER );
                intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
                intent.setComponent( cmp );
                startActivity( intent );
                dialog.dismiss();
            }
        } );
        dialog.show();
    }

    /*创建下载线程方法*/
    /*批量下载图片*/
    private void saveMorePhotoToLocal(final int mode, final List<String> share_imgs) {
        /*网络路劲存储*/
        new Thread( new Runnable() {
            @Override
            public void run() {
                URL imageurl;
                try {
                    for (int i = mode; i < share_imgs.size(); i++) {
                        imageurl = new URL( share_imgs.get( i ) );
                        HttpURLConnection conn = (HttpURLConnection) imageurl.openConnection();
                        conn.setDoInput( true );
                        conn.connect();
                        InputStream is = conn.getInputStream();
                        Bitmap bitmap = BitmapFactory.decodeStream( is );
                        is.close();
                        Message msg = new Message();
                        // 把bm存入消息中,发送到主线程
                        msg.obj = bitmap;
                        handler.sendMessage( msg );
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } ).start();
    }

}
