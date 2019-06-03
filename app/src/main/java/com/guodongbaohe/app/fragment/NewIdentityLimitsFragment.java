package com.guodongbaohe.app.fragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.guodongbaohe.app.R;
import com.guodongbaohe.app.activity.GVipToFriendActivity;
import com.guodongbaohe.app.activity.ShopRangingClassicActivity;
import com.guodongbaohe.app.activity.YaoQingFriendActivity;
import com.guodongbaohe.app.bean.ExclusiveTutorBean;
import com.guodongbaohe.app.bean.NewUserLevelData;
import com.guodongbaohe.app.common_constant.Constant;
import com.guodongbaohe.app.common_constant.MyApplication;
import com.guodongbaohe.app.dialogfragment.BaseNiceDialog;
import com.guodongbaohe.app.dialogfragment.NiceDialog;
import com.guodongbaohe.app.dialogfragment.ViewConvertListener;
import com.guodongbaohe.app.dialogfragment.ViewHolder;
import com.guodongbaohe.app.myokhttputils.response.JsonResponseHandler;
import com.guodongbaohe.app.util.ClipContentUtil;
import com.guodongbaohe.app.util.DensityUtils;
import com.guodongbaohe.app.util.EncryptUtil;
import com.guodongbaohe.app.util.GsonUtil;
import com.guodongbaohe.app.util.ParamUtil;
import com.guodongbaohe.app.util.PreferUtils;
import com.guodongbaohe.app.util.ToastUtils;
import com.guodongbaohe.app.util.VersionUtil;
import com.guodongbaohe.app.view.CircleImageView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NewIdentityLimitsFragment extends Fragment {
    @BindView(R.id.nestedscrollview)
    NestedScrollView nestedscrollview;
    @BindView(R.id.roundimageview_big)
    ImageView roundimageview_big;
    @BindView(R.id.swiperefreshlayout)
    SmartRefreshLayout swiperefreshlayout;
    /*用户头像*/
    @BindView(R.id.user_iv)
    CircleImageView user_iv;
    /*用户名字*/
    @BindView(R.id.tv_user_name)
    TextView tv_user_name;
    /*用户等级图标*/
    @BindView(R.id.iv_user_level)
    ImageView iv_user_level;
    /*vip用户布局*/
    @BindView(R.id.ll_vip_parent)
    LinearLayout ll_vip_parent;
    /*只有vip才有的客服布局*/
    @BindView(R.id.re_vip_view_service)
    RelativeLayout re_vip_view_service;
    /*vip没有的 合伙人和总裁有的图片*/
    @BindView(R.id.murcielago)
    RoundedImageView murcielago;
    /*只有合伙人才有的晋升任务*/
    @BindView(R.id.ll_partern_promote_task)
    LinearLayout ll_partern_promote_task;
    /*合伙人  或者  总裁 的团队人数*/
    @BindView(R.id.ll_partern_team_person)
    LinearLayout ll_partern_team_person;
    /*合伙人  或者 总裁 的  做总收益*/
    @BindView(R.id.ll_partern_income)
    LinearLayout ll_partern_income;
    /*微信号布局*/
    @BindView(R.id.wchat_img)
    CircleImageView wchat_img;
    @BindView(R.id.tv_wchat_num)
    TextView tv_wchat_num;
    @BindView(R.id.tv_achat_copy)
    TextView tv_achat_copy;
    private View view;
    Context context;
    String direct_son_count_num, member_role, userImg, userName, indirect_son_count_num, total_income, direct_partners, third_son_count_num;
    Intent intent;
    /*vip布局的点击控件*/
    @BindView(R.id.tv_upgrade_explain_one)
    TextView tv_upgrade_explain_one;
    @BindView(R.id.tv_go_invite_one)
    TextView tv_go_invite_one;
    @BindView(R.id.tv_go_make_commission_one)
    TextView tv_go_make_commission_one;
    @BindView(R.id.tv_upgrade_explain_two)
    TextView tv_upgrade_explain_two;
    @BindView(R.id.tv_go_invite_two)
    TextView tv_go_invite_two;
    @BindView(R.id.tv_go_make_commission_two)
    TextView tv_go_make_commission_two;
    @BindView(R.id.tv_upgrade_explain_three)
    TextView tv_upgrade_explain_three;
    @BindView(R.id.tv_go_buy)
    TextView tv_go_buy;
    @BindView(R.id.tv_vip_task)
    TextView tv_vip_task;
    @BindView(R.id.tv_text_one)
    TextView tv_text_one;
    @BindView(R.id.progressBar_mode_one)
    ProgressBar progressBar_mode_one;
    @BindView(R.id.tv_text_two)
    TextView tv_text_two;
    @BindView(R.id.tv_text_three)
    TextView tv_text_three;
    @BindView(R.id.progressBar_mode_two)
    ProgressBar progressBar_mode_two;
    @BindView(R.id.tv_text_four)
    TextView tv_text_four;
    @BindView(R.id.tv_text_five)
    TextView tv_text_five;
    @BindView(R.id.progressBar_mode_three)
    ProgressBar progressBar_mode_three;
    @BindView(R.id.tv_text_six)
    TextView tv_text_six;
    @BindView(R.id.tv_text_seven)
    TextView tv_text_seven;
    @BindView(R.id.progressBar_mode_four)
    ProgressBar progressBar_mode_four;
    @BindView(R.id.tv_text_eight)
    TextView tv_text_eight;
    @BindView(R.id.tv_text_nine)
    TextView tv_text_nine;
    @BindView(R.id.progressBar_mode_five)
    ProgressBar progressBar_mode_five;
    @BindView(R.id.tv_text_ten)
    TextView tv_text_ten;
    @BindView(R.id.progressBar_mode_six)
    ProgressBar progressBar_mode_six;
    @BindView(R.id.tv_text_eleven)
    TextView tv_text_eleven;
    /*合伙人晋升任务布局*/
    @BindView(R.id.tv_promote_one)
    TextView tv_promote_one;
    @BindView(R.id.tv_upgrade_explain_parener)
    TextView tv_upgrade_explain_parener;
    @BindView(R.id.tv_promote_two)
    TextView tv_promote_two;
    @BindView(R.id.progressBar_mode_zhishu)
    ProgressBar progressBar_mode_zhishu;
    @BindView(R.id.tv_promote_threee)
    TextView tv_promote_threee;
    @BindView(R.id.tv_promote_go_invite)
    TextView tv_promote_go_invite;
    @BindView(R.id.tv_upgrade_boss)
    TextView tv_upgrade_boss;
    /*团队人数布局*/
    @BindView(R.id.tv_team_explain)
    TextView tv_team_explain;
    @BindView(R.id.progressBar_mode_tean_person)
    ProgressBar progressBar_mode_tean_person;
    @BindView(R.id.tv_team_person_num)
    TextView tv_team_person_num;
    @BindView(R.id.tv_team_go_invite)
    TextView tv_team_go_invite;
    @BindView(R.id.tv_challenge_one)
    TextView tv_challenge_one;
    /*累计收益布局*/
    @BindView(R.id.tv_income_explain)
    TextView tv_income_explain;
    @BindView(R.id.progressBar_mode_income)
    ProgressBar progressBar_mode_income;
    @BindView(R.id.tv_income_num)
    TextView tv_income_num;
    @BindView(R.id.tv_challenge_two)
    TextView tv_challenge_two;
    String levelOneOfPartner;/*条件合伙人*/
    DisplayMetrics displayMetrics;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister( this );
    }

    // 声明一个订阅方法，用于接收事件
    @Subscribe
    public void onEvent(String msg) {
        switch (msg) {
            case "headimgChange":
                /*用户头像和用户名更改*/
                userImgAnduserNameChange();
                break;
            case Constant.USER_LEVEL_UPGRADE:
                /*用户升级成功调用用户信息接口*/
                getUserLevelData();
                break;
            case Constant.LOGINSUCCESS:
                /*用户登成功之后*/
                getUserLevelData();
                break;
            case Constant.SWIPEREFRESHLAYOUT:
                getUserLevelData();
                break;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate( R.layout.newidentitylimitsfragment, container, false );
            ButterKnife.bind( this, view );
            EventBus.getDefault().register( this );
            context = MyApplication.getInstance();
            displayMetrics = getResources().getDisplayMetrics();
            /*图片显示大小*/
            setBigBgView();
            /*账号信息接口（数据很全）*/
            getUserLevelData();
            /*监听刷新控件*/
            initRefresh();
        }
        return view;
    }

    private void initRefresh() {
        swiperefreshlayout.setOnRefreshListener( new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                getUserLevelData();
                EventBus.getDefault().post( Constant.NEWIDENTITYLIMITSFRAGMENT_REFRESH );
            }
        } );
    }

    /*用户账号信息接口*/
    private void getUserLevelData() {
        long timelineStr = System.currentTimeMillis() / 1000;
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put( Constant.TIMELINE, String.valueOf( timelineStr ) );
        map.put( Constant.PLATFORM, Constant.ANDROID );
        map.put( "member_id", PreferUtils.getString( context, "member_id" ) );
        String qianMingMapParam = ParamUtil.getQianMingMapParam( map );
        String token = EncryptUtil.encrypt( qianMingMapParam + Constant.NETKEY );
        map.put( Constant.TOKEN, token );
        String param = ParamUtil.getMapParam( map );
        MyApplication.getInstance().getMyOkHttp().post()
                .url( Constant.BASE_URL + Constant.USER_LEVEL_NEW_DATA + "?" + param )
                .tag( this )
                .addHeader( "x-userid", PreferUtils.getString( context, "member_id" ) )
                .addHeader( "x-appid", Constant.APPID )
                .addHeader( "x-devid", PreferUtils.getString( context, Constant.PESUDOUNIQUEID ) )
                .addHeader( "x-nettype", PreferUtils.getString( context, Constant.NETWORKTYPE ) )
                .addHeader( "x-agent", VersionUtil.getVersionCode( context ) )
                .addHeader( "x-platform", Constant.ANDROID )
                .addHeader( "x-devtype", Constant.IMEI )
                .addHeader( "x-token", ParamUtil.GroupMap( context, PreferUtils.getString( context, "member_id" ) ) )
                .enqueue( new JsonResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, JSONObject response) {
                        super.onSuccess( statusCode, response );
                        Log.i( "用户级别数据", response.toString() );
                        try {
                            JSONObject jsonObject = new JSONObject( response.toString() );
                            if (jsonObject.getInt( "status" ) >= 0) {
                                NewUserLevelData bean = GsonUtil.GsonToBean( response.toString(), NewUserLevelData.class );
                                if (bean == null) return;
                                NewUserLevelData.NewUserLevel result = bean.getResult();
                                if (result == null) return;
                                member_role = result.getMember_role();/*用户等级*/
                                userImg = result.getAvatar();/*用户头像*/
                                userName = result.getMember_name();/*用户名字*/
                                direct_son_count_num = result.getLevelOne();/*直接下级数量*/
                                indirect_son_count_num = result.getLevelTwo();/*间接下级数量*/
                                third_son_count_num = result.getLevelMore();/*第三级下级数量*/
                                total_income = result.getIncome();/*用户累计收益*/
                                direct_partners = result.getDirect_partners();/*直属合伙人数量*/
                                PreferUtils.putString( context, "member_role", member_role );
                                PreferUtils.putString( context, "son_count", direct_son_count_num );
                                userImgAnduserNameChange();
                                if (Constant.BOSS_USER_LEVEL.contains( member_role )) {
                                    /*总裁级别*/
                                    String target = result.getChallenge_one().getTarget();/*团队人数数量目标*/
                                    NewUserLevelData.Section section = result.getChallenge_two().getSection();
                                    String start = section.getStart();
                                    String end = section.getEnd();
                                    int total_person = Integer.valueOf( direct_son_count_num ) + Integer.valueOf( indirect_son_count_num ) + Integer.valueOf( third_son_count_num );
                                    tv_team_person_num.setText( total_person + "/" + target + "(人)" );
                                    if (Double.valueOf( total_person ) < Double.valueOf( target )) {
                                        Double progress_team = Double.valueOf( total_person ) / Double.valueOf( target ) * 100;
                                        progressBar_mode_tean_person.setProgress( progress_team.intValue() );
                                    } else {
                                        progressBar_mode_tean_person.setProgress( 100 );
                                    }
                                    tv_income_num.setText( total_income + "/" + end + "(元)" );
                                    if (Double.valueOf( total_income ) < Double.valueOf( end )) {
                                        Double progress_income = Double.valueOf( total_income ) / Double.valueOf( end ) * 100;
                                        progressBar_mode_income.setProgress( progress_income.intValue() );
                                    } else {
                                        progressBar_mode_income.setProgress( 100 );
                                    }
                                } else if (Constant.PARTNER_USER_LEVEL.contains( member_role )) {
                                    /*合伙人级别*/
                                    NewUserLevelData.ModeOne mode_one = result.getMode_one();
                                    levelOneOfPartner = mode_one.getLevelOne();
                                    String target = result.getChallenge_one().getTarget();/*团队人数数量目标*/
                                    NewUserLevelData.Section section = result.getChallenge_two().getSection();
                                    String start = section.getStart();
                                    String end = section.getEnd();

                                    tv_promote_one.setText( "晋升任务(满" + levelOneOfPartner + "个直属合伙人即可升级)" );
                                    tv_promote_two.setText( "直属合伙人" + levelOneOfPartner + "人以上" );
                                    tv_promote_threee.setText( direct_partners + "/" + levelOneOfPartner + "(人)" );
                                    if (Double.valueOf( direct_partners ) < Double.valueOf( levelOneOfPartner )) {
                                        Double progress_zhishu = Double.valueOf( direct_partners ) / Double.valueOf( levelOneOfPartner ) * 100;
                                        progressBar_mode_zhishu.setProgress( progress_zhishu.intValue() );
                                    } else {
                                        progressBar_mode_zhishu.setProgress( 100 );
                                    }
                                    int total_person = Integer.valueOf( direct_son_count_num ) + Integer.valueOf( indirect_son_count_num ) + Integer.valueOf( third_son_count_num );
                                    tv_team_person_num.setText( total_person + "/" + target + "(人)" );
                                    if (Double.valueOf( total_person ) < Double.valueOf( target )) {
                                        Double progress_team = Double.valueOf( total_person ) / Double.valueOf( target ) * 100;
                                        progressBar_mode_tean_person.setProgress( progress_team.intValue() );
                                    } else {
                                        progressBar_mode_tean_person.setProgress( 100 );
                                    }
                                    tv_income_num.setText( total_income + "/" + end + "(元)" );
                                    if (Double.valueOf( total_income ) < Double.valueOf( end )) {
                                        Double progress_income = Double.valueOf( total_income ) / Double.valueOf( end ) * 100;
                                        progressBar_mode_income.setProgress( progress_income.intValue() );
                                    } else {
                                        progressBar_mode_income.setProgress( 100 );
                                    }
                                } else {
                                    /*vip级别*/
                                    NewUserLevelData.ModeOne mode_one = result.getMode_one();
                                    NewUserLevelData.ModeTwo mode_two = result.getMode_two();
                                    String levelOne = mode_one.getLevelOne();
                                    String income = mode_one.getIncome();
                                    String income_mode_two = mode_two.getIncome();
                                    String levelOne_mode_two = mode_two.getLevelOne();
                                    String levelTwo_mode_two = mode_two.getLevelTwo();
                                    /*方式一*/
                                    tv_text_one.setText( "邀请" + levelOne + "个直属用户下载注册" );
                                    tv_text_two.setText( direct_son_count_num + "/" + levelOne + "(人)" );
                                    if (Double.valueOf( direct_son_count_num ) < Double.valueOf( levelOne )) {
                                        Double progress_one = Double.valueOf( direct_son_count_num ) / Double.valueOf( levelOne ) * 100;
                                        progressBar_mode_one.setProgress( progress_one.intValue() );
                                    } else {
                                        progressBar_mode_one.setProgress( 100 );
                                    }
                                    tv_text_three.setText( "累计结算收益达到" + income + "元" );
                                    tv_text_four.setText( total_income + "/" + income + "(元)" );
                                    if (Double.valueOf( total_income ) < Double.valueOf( income )) {
                                        Double progress_two = Double.valueOf( total_income ) / Double.valueOf( income ) * 100;
                                        progressBar_mode_two.setProgress( progress_two.intValue() );
                                    } else {
                                        progressBar_mode_two.setProgress( 100 );
                                    }
                                    /*方式二*/
                                    tv_text_five.setText( "直接邀请用户" + levelOne_mode_two + "人以上" );
                                    tv_text_six.setText( direct_son_count_num + "/" + levelOne_mode_two + "(人)" );
                                    if (Double.valueOf( direct_son_count_num ) < Double.valueOf( levelOne_mode_two )) {
                                        Double progress_three = Double.valueOf( direct_son_count_num ) / Double.valueOf( levelOne_mode_two ) * 100;
                                        progressBar_mode_three.setProgress( progress_three.intValue() );
                                    } else {
                                        progressBar_mode_three.setProgress( 100 );
                                    }
                                    tv_text_seven.setText( "间接邀请用户" + levelTwo_mode_two + "人以上" );
                                    tv_text_eight.setText( indirect_son_count_num + "/" + levelTwo_mode_two + "(人)" );
                                    if (Double.valueOf( indirect_son_count_num ) < Double.valueOf( levelTwo_mode_two )) {
                                        Double progress_four = Double.valueOf( indirect_son_count_num ) / Double.valueOf( levelTwo_mode_two ) * 100;
                                        progressBar_mode_four.setProgress( progress_four.intValue() );
                                    } else {
                                        progressBar_mode_four.setProgress( 100 );
                                    }
                                    tv_text_nine.setText( "累计结算收益达到" + income_mode_two + "元" );
                                    tv_text_ten.setText( total_income + "/" + income_mode_two + "(元)" );
                                    if (Double.valueOf( total_income ) < Double.valueOf( income_mode_two )) {
                                        Double progress_five = Double.valueOf( total_income ) / Double.valueOf( income_mode_two ) * 100;
                                        progressBar_mode_five.setProgress( progress_five.intValue() );
                                    } else {
                                        progressBar_mode_five.setProgress( 100 );
                                    }
                                }
                                swiperefreshlayout.finishRefresh();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, String error_msg) {
                        swiperefreshlayout.finishRefresh();
                        ToastUtils.showToast( context, Constant.NONET );
                    }
                } );
    }

    /*用户头像和用户名更改*/
    private void userImgAnduserNameChange() {
        if (TextUtils.isEmpty( userImg )) {
            user_iv.setImageResource( R.mipmap.user_moren_logo );
        } else {
            Glide.with( context ).load( userImg ).into( user_iv );
        }
        tv_user_name.setText( userName );
        if (Constant.BOSS_USER_LEVEL.contains( member_role )) {/*总裁*/
            iv_user_level.setImageResource( R.mipmap.upgrade_zc_icon );
            roundimageview_big.setImageResource( R.mipmap.upgrade_boss );
            ll_vip_parent.setVisibility( View.GONE );
            re_vip_view_service.setVisibility( View.GONE );
            ll_partern_promote_task.setVisibility( View.GONE );
            ll_partern_team_person.setVisibility( View.VISIBLE );
            ll_partern_income.setVisibility( View.VISIBLE );
            tv_challenge_one.setText( "极限挑战" );
            tv_challenge_two.setText( "极限挑战" );
            tv_team_go_invite.setVisibility( View.GONE );
        } else if (Constant.PARTNER_USER_LEVEL.contains( member_role )) {/*合伙人*/
            iv_user_level.setImageResource( R.mipmap.upgrade_hhr_icon );
            roundimageview_big.setImageResource( R.mipmap.upgrade_partner_to_boss );
            ll_vip_parent.setVisibility( View.GONE );
            re_vip_view_service.setVisibility( View.GONE );
            ll_partern_promote_task.setVisibility( View.VISIBLE );
            ll_partern_team_person.setVisibility( View.VISIBLE );
            ll_partern_income.setVisibility( View.VISIBLE );
            tv_challenge_one.setText( "自我挑战" );
            tv_challenge_two.setText( "自我挑战" );
            tv_team_go_invite.setVisibility( View.VISIBLE );
        } else {/*vip*/
            iv_user_level.setImageResource( R.mipmap.new_vip_level );
            roundimageview_big.setImageResource( R.mipmap.upgrade_partern );
            ll_vip_parent.setVisibility( View.VISIBLE );
            re_vip_view_service.setVisibility( View.VISIBLE );
            ll_partern_promote_task.setVisibility( View.GONE );
            ll_partern_team_person.setVisibility( View.GONE );
            ll_partern_income.setVisibility( View.GONE );
            /*获取专属导师微信*/
            getWChatData();
        }
    }

    private void setBigBgView() {
        int width = displayMetrics.widthPixels - DensityUtils.dip2px( getContext(), 20 );
        //会员等级图显示
        float ratio = (float) ((1000 * 1.0) / (1020 * 1.0));
        int height = (int) (width * 1.0 * ratio);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) roundimageview_big.getLayoutParams();
        params.width = width;
        params.height = height;
        roundimageview_big.setLayoutParams( params );
        /*大牛图片显示*/
        float ratio_two = (float) ((1021 * 1.0) / (width * 1.0));
        int height_two = (int) (403 * 1.0 / ratio_two);
        ViewGroup.LayoutParams params_two = murcielago.getLayoutParams();
        params_two.width = width;
        params_two.height = height_two;
        murcielago.setLayoutParams( params_two );
    }

    String wechat;

    private void getWChatData() {
        long timelineStr = System.currentTimeMillis() / 1000;
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put( Constant.TIMELINE, String.valueOf( timelineStr ) );
        map.put( Constant.PLATFORM, Constant.ANDROID );
        map.put( "member_id", PreferUtils.getString( context, "member_id" ) );
        String qianMingMapParam = ParamUtil.getQianMingMapParam( map );
        String token = EncryptUtil.encrypt( qianMingMapParam + Constant.NETKEY );
        map.put( Constant.TOKEN, token );
        String mapParam = ParamUtil.getMapParam( map );
        MyApplication.getInstance().getMyOkHttp().post().url( Constant.BASE_URL + Constant.EXCLUSIVETUTOR_API + "?" + mapParam )
                .tag( this )
                .addHeader( "x-userid", PreferUtils.getString( context, "member_id" ) )
                .addHeader( "x-appid", Constant.APPID )
                .addHeader( "x-devid", PreferUtils.getString( context, Constant.PESUDOUNIQUEID ) )
                .addHeader( "x-nettype", PreferUtils.getString( context, Constant.NETWORKTYPE ) )
                .addHeader( "x-agent", VersionUtil.getVersionCode( context ) )
                .addHeader( "x-platform", Constant.ANDROID )
                .addHeader( "x-devtype", Constant.IMEI )
                .addHeader( "x-token", ParamUtil.GroupMap( context, PreferUtils.getString( context, "member_id" ) ) )
                .enqueue( new JsonResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, JSONObject response) {
                        super.onSuccess( statusCode, response );
                        Log.i( "微信号", response.toString() );
                        try {
                            JSONObject jsonObject = new JSONObject( response.toString() );
                            if (jsonObject.getInt( "status" ) >= 0) {
                                ExclusiveTutorBean bean = GsonUtil.GsonToBean( response.toString(), ExclusiveTutorBean.class );
                                if (bean == null) return;
                                wechat = bean.getResult().getWechat();
                                String avatar = bean.getResult().getAvatar();
                                tv_wchat_num.setText( "微信: " + wechat );
                                Glide.with( context ).load( avatar ).into( wchat_img );
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, String error_msg) {
                    }
                } );
    }

    @OnClick({R.id.tv_achat_copy, R.id.tv_upgrade_explain_one, R.id.tv_go_invite_one, R.id.tv_go_make_commission_one, R.id.tv_upgrade_explain_two,
            R.id.tv_go_invite_two, R.id.tv_go_make_commission_two, R.id.tv_upgrade_explain_three, R.id.tv_go_buy, R.id.tv_vip_task, R.id.tv_income_explain
            , R.id.tv_team_explain, R.id.tv_team_go_invite, R.id.tv_upgrade_explain_parener, R.id.tv_promote_go_invite, R.id.tv_upgrade_boss})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.tv_achat_copy:
                ClipboardManager cm = (ClipboardManager) context.getSystemService( Context.CLIPBOARD_SERVICE );
                ClipData mClipData = ClipData.newPlainText( "Label", wechat );
                cm.setPrimaryClip( mClipData );
                ClipContentUtil.getInstance( context ).putNewSearch( wechat );//保存记录到数据库
                ToastUtils.showBackgroudCenterToast( context, "复制成功" );
                break;
            case R.id.tv_upgrade_explain_one:
                showExplainDialog( "您直接邀请好友注册果冻宝盒达到50个且账户里累计结算收益达到600元，就可以免费升级为合伙人并且享受更高佣金等更多权益。", "vip_one" );
                break;
            case R.id.tv_go_invite_one:
                intent = new Intent( context, YaoQingFriendActivity.class );
                startActivity( intent );
                break;
            case R.id.tv_go_make_commission_one:
                intent = new Intent( context, ShopRangingClassicActivity.class );
                intent.putExtra( "title", "疯抢榜" );
                startActivity( intent );
                break;
            case R.id.tv_upgrade_explain_two:
                showExplainDialog( "一线团队满50人且二线团队达100人，以及账户累计结算收益达到200元，即可免费升级为合伙人！", "vip_two" );
                break;
            case R.id.tv_go_invite_two:
                intent = new Intent( context, YaoQingFriendActivity.class );
                startActivity( intent );
                break;
            case R.id.tv_go_make_commission_two:
                intent = new Intent( context, ShopRangingClassicActivity.class );
                intent.putExtra( "title", "疯抢榜" );
                startActivity( intent );
                break;
            case R.id.tv_upgrade_explain_three:
                showExplainDialog( "你可以直接购买智慧大脑软件，即可获得合伙人权限。", "vip_three" );
                break;
            case R.id.tv_go_buy:
                intent = new Intent( context, GVipToFriendActivity.class );
                startActivity( intent );
                break;
            case R.id.tv_vip_task:/*vip立即升级合伙人按钮*/
                freeUpgradeData();
                break;
            case R.id.tv_income_explain:/*收益说明*/
                if (Constant.PARTNER_USER_LEVEL.contains( member_role )) {
                    showExplainDialog( "累计总收益是你通过推广赚的收益，今日收益+历史收益。", "partner_income" );
                } else if (Constant.BOSS_USER_LEVEL.contains( member_role )) {
                    showExplainDialog( "累计总收益是你通过推广赚的收益，今日收益+历史收益。", "boss_income" );
                }
                break;
            case R.id.tv_team_explain:/*团队说明*/
                if (Constant.PARTNER_USER_LEVEL.contains( member_role )) {
                    showExplainDialog( "“我的团队”里面显示的人数，是“一线团队”+“二线团队”的人数。", "partner_team" );
                } else if (Constant.BOSS_USER_LEVEL.contains( member_role )) {
                    showExplainDialog( "“我的团队”里面显示的人数，是“一线团队”+“二线团队”的人数。", "boss_team" );
                }
                break;
            case R.id.tv_team_go_invite:/*团队去邀请*/
                intent = new Intent( context, YaoQingFriendActivity.class );
                startActivity( intent );
                break;
            case R.id.tv_upgrade_explain_parener:/*晋升任务升级说明按钮*/
                showExplainDialog( "您的一线团队满15个合伙人，就可以升级为总裁并且可以享受最高佣金等更多权益。", "partner_promote" );
                break;
            case R.id.tv_promote_go_invite:/*晋升任务去邀请*/
                intent = new Intent( context, YaoQingFriendActivity.class );
                startActivity( intent );
                break;
            case R.id.tv_upgrade_boss:/*立即升级总裁按钮*/
                freeUpgradeData();
                break;
        }
    }

    private void showExplainDialog(String content, String upgrade_mode) {
        NiceDialog niceDialog = NiceDialog.init();
        niceDialog.setLayoutId( R.layout.upgrade_explain_dialog );
        niceDialog.setConvertListener( new ViewConvertListener() {
            @Override
            protected void convertView(ViewHolder holder, BaseNiceDialog dialog) {
                ImageView close = holder.getView( R.id.close );
                TextView invite = holder.getView( R.id.invite );
                TextView commission = holder.getView( R.id.commission );
                TextView tv_content = holder.getView( R.id.tv_content );
                ImageView iv = holder.getView( R.id.iv );
                TextView tv_method = holder.getView( R.id.tv_method );
                LinearLayout ll_one_parent = holder.getView( R.id.ll_one_parent );
                TextView go_buy = holder.getView( R.id.go_buy );
                int width = displayMetrics.widthPixels - DensityUtils.dip2px( getContext(), 20 ) - 200;
                float ratio = (float) ((400 * 1.0) / (740 * 1.0));
                int height = (int) (width * 1.0 * ratio);
                RelativeLayout.LayoutParams paramsIv = (RelativeLayout.LayoutParams) iv.getLayoutParams();
                paramsIv.width = width;
                paramsIv.height = height;
                iv.setLayoutParams( paramsIv );
                tv_content.setText( content );
                close.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        niceDialog.dismiss();
                    }
                } );
                invite.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        intent = new Intent( context, YaoQingFriendActivity.class );
                        startActivity( intent );
                        niceDialog.dismiss();
                    }
                } );
                commission.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        intent = new Intent( context, ShopRangingClassicActivity.class );
                        intent.putExtra( "title", "疯抢榜" );
                        startActivity( intent );
                        niceDialog.dismiss();
                    }
                } );
                go_buy.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        intent = new Intent( context, GVipToFriendActivity.class );
                        startActivity( intent );
                        niceDialog.dismiss();
                    }
                } );
                switch (upgrade_mode) {
                    case "vip_one":
                        tv_method.setText( "方式一" );
                        setTextViewColor( content, tv_content );
                        go_buy.setVisibility( View.GONE );
                        ll_one_parent.setVisibility( View.VISIBLE );
                        break;
                    case "vip_two":
                        tv_method.setText( "方式二" );
                        setTextViewColorTwo( content, tv_content );
                        go_buy.setVisibility( View.GONE );
                        ll_one_parent.setVisibility( View.VISIBLE );
                        break;
                    case "vip_three":
                        tv_method.setText( "方式三" );
                        setTextViewColorThree( content, tv_content );
                        go_buy.setVisibility( View.VISIBLE );
                        ll_one_parent.setVisibility( View.GONE );
                        break;
                    case "partner_promote":/*合伙人方式*/
                        tv_method.setText( "方式" );
                        setTextViewColorFour( content, tv_content );
                        go_buy.setVisibility( View.GONE );
                        ll_one_parent.setVisibility( View.VISIBLE );
                        break;
                    case "partner_team":/*合伙人人团队*/
                        tv_method.setText( "自我挑战" );
                        setTextViewColorFive( content, tv_content );
                        go_buy.setVisibility( View.GONE );
                        ll_one_parent.setVisibility( View.VISIBLE );
                        break;
                    case "partner_income":/*合伙人收益*/
                        tv_method.setText( "自我挑战" );
                        go_buy.setVisibility( View.GONE );
                        ll_one_parent.setVisibility( View.VISIBLE );
                        break;
                    case "boss_team":/*总裁团队*/
                        tv_method.setText( "极限挑战" );
                        setTextViewColorFive( content, tv_content );
                        go_buy.setVisibility( View.GONE );
                        ll_one_parent.setVisibility( View.VISIBLE );
                        break;
                    case "boss_income":/*总裁收益*/
                        tv_method.setText( "极限挑战" );
                        go_buy.setVisibility( View.GONE );
                        ll_one_parent.setVisibility( View.VISIBLE );
                        break;
                }
            }
        } );
        niceDialog.setMargin( 100 );
        niceDialog.show( getFragmentManager() );
        niceDialog.setCancelable( false );
        niceDialog.setOutCancel( false );
    }

    /*免费升级接口*/
    private void freeUpgradeData() {
        long timelineStr = System.currentTimeMillis() / 1000;
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put( Constant.TIMELINE, String.valueOf( timelineStr ) );
        map.put( Constant.PLATFORM, Constant.ANDROID );
        map.put( "member_id", PreferUtils.getString( context, "member_id" ) );
        String qianMingMapParam = ParamUtil.getQianMingMapParam( map );
        String token = EncryptUtil.encrypt( qianMingMapParam + Constant.NETKEY );
        map.put( Constant.TOKEN, token );
        String mapParam = ParamUtil.getMapParam( map );
        MyApplication.getInstance().getMyOkHttp().post().url( Constant.BASE_URL + Constant.FREE_UPGRADE_API + "?" + mapParam )
                .tag( this )
                .addHeader( "x-userid", PreferUtils.getString( context, "member_id" ) )
                .addHeader( "x-appid", Constant.APPID )
                .addHeader( "x-devid", PreferUtils.getString( context, Constant.PESUDOUNIQUEID ) )
                .addHeader( "x-nettype", PreferUtils.getString( context, Constant.NETWORKTYPE ) )
                .addHeader( "x-agent", VersionUtil.getVersionCode( context ) )
                .addHeader( "x-platform", Constant.ANDROID )
                .addHeader( "x-devtype", Constant.IMEI )
                .addHeader( "x-token", ParamUtil.GroupMap( context, PreferUtils.getString( context, "member_id" ) ) )
                .enqueue( new JsonResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, JSONObject response) {
                        super.onSuccess( statusCode, response );
                        Log.i( "免费升级", response.toString() );
                        try {
                            JSONObject jsonObject = new JSONObject( response.toString() );
                            if (jsonObject.getInt( "status" ) >= 0) {
                                String result = jsonObject.getString( "result" );
                                getUserLevelData();
                                EventBus.getDefault().post( Constant.NEWIDENTITYLIMITSFRAGMENT_REFRESH );
                                ToastUtils.showBackgroudCenterToast( context, result );
                                nestedscrollview.scrollTo( 0, 0 );
                            } else {
                                String result = jsonObject.getString( "result" );
                                ToastUtils.showBackgroudCenterToast( context, result );
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

    private void setTextViewColor(String content, TextView tv_content) {
        SpannableString spannableString = new SpannableString( content );
        spannableString.setSpan( new ForegroundColorSpan( Color.parseColor( "#765522" ) ), 15, 18, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE );
        spannableString.setSpan( new ForegroundColorSpan( Color.parseColor( "#765522" ) ), 22, 34, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE );
        spannableString.setSpan( new ForegroundColorSpan( Color.parseColor( "#765522" ) ), 38, 40, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE );
        tv_content.setText( spannableString );
    }

    private void setTextViewColorTwo(String content, TextView tv_content) {
        SpannableString spannableString = new SpannableString( content );
        spannableString.setSpan( new ForegroundColorSpan( Color.parseColor( "#765522" ) ), 4, 8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE );
        spannableString.setSpan( new ForegroundColorSpan( Color.parseColor( "#765522" ) ), 13, 18, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE );
        spannableString.setSpan( new ForegroundColorSpan( Color.parseColor( "#765522" ) ), 21, 35, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE );
        spannableString.setSpan( new ForegroundColorSpan( Color.parseColor( "#765522" ) ), 38, 40, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE );
        tv_content.setText( spannableString );
    }

    private void setTextViewColorThree(String content, TextView tv_content) {
        SpannableString spannableString = new SpannableString( content );
        spannableString.setSpan( new ForegroundColorSpan( Color.parseColor( "#765522" ) ), 5, 13, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE );
        tv_content.setText( spannableString );
    }

    private void setTextViewColorFour(String content, TextView tv_content) {
        SpannableString spannableString = new SpannableString( content );
        spannableString.setSpan( new ForegroundColorSpan( Color.parseColor( "#765522" ) ), 7, 10, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE );
        spannableString.setSpan( new ForegroundColorSpan( Color.parseColor( "#765522" ) ), 17, 22, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE );
        tv_content.setText( spannableString );
    }

    private void setTextViewColorFive(String content, TextView tv_content) {
        SpannableString spannableString = new SpannableString( content );
        spannableString.setSpan( new ForegroundColorSpan( Color.parseColor( "#765522" ) ), 1, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE );
        spannableString.setSpan( new ForegroundColorSpan( Color.parseColor( "#765522" ) ), 16, 20, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE );
        spannableString.setSpan( new ForegroundColorSpan( Color.parseColor( "#765522" ) ), 22, 27, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE );
        tv_content.setText( spannableString );
    }
}
