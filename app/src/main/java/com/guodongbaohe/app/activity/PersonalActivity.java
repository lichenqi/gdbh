package com.guodongbaohe.app.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.ali.auth.third.core.model.Session;
import com.ali.auth.third.login.callback.LogoutCallback;
import com.alibaba.baichuan.android.trade.adapter.login.AlibcLogin;
import com.alibaba.baichuan.android.trade.callback.AlibcLoginCallback;
import com.bumptech.glide.Glide;
import com.guodongbaohe.app.MainActivity;
import com.guodongbaohe.app.R;
import com.guodongbaohe.app.base_activity.BaseActivity;
import com.guodongbaohe.app.common_constant.Constant;
import com.guodongbaohe.app.common_constant.MyApplication;
import com.guodongbaohe.app.myokhttputils.response.JsonResponseHandler;
import com.guodongbaohe.app.util.EncryptUtil;
import com.guodongbaohe.app.util.ParamUtil;
import com.guodongbaohe.app.util.PictureUtil;
import com.guodongbaohe.app.util.PreferUtils;
import com.guodongbaohe.app.util.ToastUtils;
import com.guodongbaohe.app.util.VersionUtil;
import com.guodongbaohe.app.view.CircleImageView;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PersonalActivity extends BaseActivity {
    @BindView(R.id.loginout)
    TextView loginout;
    Intent intent;
    @BindView(R.id.re_change_img)
    RelativeLayout re_change_img;
    @BindView(R.id.circleimageview)
    CircleImageView circleimageview;
    String member_id;
    String userImg, sex, phoneNum, userName, wchatname;
    @BindView(R.id.re_change_name)
    RelativeLayout re_change_name;
    @BindView(R.id.tv_name)
    TextView tv_name;
    @BindView(R.id.re_change_sex)
    RelativeLayout re_change_sex;
    @BindView(R.id.tv_sex)
    TextView tv_sex;
    @BindView(R.id.re_change_wchat)
    RelativeLayout re_change_wchat;
    @BindView(R.id.tv_what)
    TextView tv_what;
    @BindView(R.id.tv_phone)
    TextView tv_phone;
    @BindView(R.id.re_taobao_login)
    RelativeLayout re_taobao_login;
    @BindView(R.id.switch_one)
    Switch switch_one;
    @BindView(R.id.switch_push)
    Switch switch_push;
    @BindView(R.id.re_change_cdoe)
    RelativeLayout re_change_cdoe;
    @BindView(R.id.change_phone)
    RelativeLayout change_phone;
    @BindView(R.id.tv_code)
    TextView tv_code;
    @BindView(R.id.re_unbind_wchat)
    RelativeLayout re_unbind_wchat;
    /*相册选择code*/
    private static final int CHOOSEPHOTO_CODE = 1;
    /*拍照请求code*/
    private static final int TAKEPHOTO_CODE = 2;
    /*裁剪图片回调*/
    private static final int CROPPHOTO_CODE = 4;
    private static final String IMAGE_FILE_NAME = "user_head_icon.jpg";
    AlibcLogin alibcLogin;
    String openId, member_role;
    Session session;

    @Override
    public int getContainerView() {
        return R.layout.personalactivity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        ButterKnife.bind( this );
        EventBus.getDefault().register( this );
        alibcLogin = AlibcLogin.getInstance();
        setMiddleTitle( "个人信息" );
        member_id = PreferUtils.getString( getApplicationContext(), "member_id" );
        userImg = PreferUtils.getString( getApplicationContext(), "userImg" );
        sex = PreferUtils.getString( getApplicationContext(), "sex" );
        phoneNum = PreferUtils.getString( getApplicationContext(), "phoneNum" );
        userName = PreferUtils.getString( getApplicationContext(), "userName" );
        wchatname = PreferUtils.getString( getApplicationContext(), "wchatname" );
        member_role = PreferUtils.getString( getApplicationContext(), "member_role" );
        tv_code.setText( PreferUtils.getString( getApplicationContext(), "invite_code" ) );
        initView();
        // android 7.0系统解决拍照的问题
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy( builder.build() );
        builder.detectFileUriExposure();
        session = alibcLogin.getSession();
        openId = session.openId;
        if (TextUtils.isEmpty( openId )) {
            switch_one.setChecked( false );
        } else {
            switch_one.setChecked( true );
        }
        initSwitchListener();
        if (!TextUtils.isEmpty( member_role )) {
            int role = Integer.parseInt( member_role );
            if (role > 0) {
                re_change_cdoe.setVisibility( View.VISIBLE );
            } else {
                re_change_cdoe.setVisibility( View.GONE );
            }
        }

    }

    private void initSwitchListener() {
        switch_one.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty( openId )) {
                    alibcLogin.logout( PersonalActivity.this, new LogoutCallback() {
                        @Override
                        public void onSuccess() {
                            openId = "";
                            ToastUtils.showToast( getApplicationContext(), "授权取消" );
                            switch_one.setChecked( false );
                        }

                        @Override
                        public void onFailure(int code, String msg) {
                            switch_one.setChecked( true );
                        }
                    } );
                } else {
                    alibcLogin.showLogin( PersonalActivity.this, new AlibcLoginCallback() {

                        @Override
                        public void onSuccess() {
                            openId = alibcLogin.getSession().openId;
                            ToastUtils.showToast( getApplicationContext(), "授权成功" );
                            switch_one.setChecked( true );
                        }

                        @Override
                        public void onFailure(int code, String msg) {
                            openId = "";
                            switch_one.setChecked( false );
                        }
                    } );
                }
            }
        } );
        String push_alias = PreferUtils.getString( getApplicationContext(), "push_alias" );
        if (!TextUtils.isEmpty( push_alias )) {
            if (push_alias.equals( "open" )) {
                switch_push.setChecked( true );
            } else {
                switch_push.setChecked( false );
            }
        } else {
            switch_push.setChecked( true );
        }
        switch_push.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    /*开启状态*/
                    PushAgent mPushAgent = PushAgent.getInstance( getApplicationContext() );
                    mPushAgent.addAlias( member_id, Constant.YOUMENGPUSH, new UTrack.ICallBack() {
                        @Override
                        public void onMessage(boolean isSuccess, String message) {
                            PreferUtils.putString( getApplicationContext(), "push_alias", "open" );
                            Log.i( "别名增加", message );
                        }
                    } );
                } else {
                    /*关闭状态*/
                    /*移除友盟推送别名*/
                    PushAgent.getInstance( getApplicationContext() ).deleteAlias( member_id, Constant.YOUMENGPUSH, new UTrack.ICallBack() {
                        @Override
                        public void onMessage(boolean b, String s) {
                            PreferUtils.putString( getApplicationContext(), "push_alias", "close" );
                            Log.i( "别名移除", s );
                        }
                    } );
                }
            }
        } );
    }

    /*初始化数据*/
    private void initView() {
        if (TextUtils.isEmpty( userImg )) {
            circleimageview.setImageResource( R.mipmap.user_moren_logo );
        } else {
            Glide.with( getApplicationContext() ).load( userImg ).into( circleimageview );
        }
        if (sex.equals( "0" )) {
            tv_sex.setText( "未知" );
        } else if (sex.equals( "1" )) {
            tv_sex.setText( "男" );
        } else if (sex.equals( "2" )) {
            tv_sex.setText( "女" );
        }
        phoneNum = PreferUtils.getString( getApplicationContext(), "phoneNum" );
        tv_name.setText( userName );
        tv_what.setText( wchatname );
        tv_phone.setText( phoneNum );

    }

    @OnClick({R.id.loginout, R.id.re_change_img, R.id.re_change_name, R.id.re_change_sex, R.id.re_change_wchat,
            R.id.re_change_cdoe, R.id.change_phone, R.id.re_unbind_wchat})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.loginout:
                loginOutData();
                break;
            case R.id.re_change_img:
                changeHeadImg();
                break;
            case R.id.re_change_name:
                intent = new Intent( getApplicationContext(), EditUserNameOrWchatActivity.class );
                intent.putExtra( "content", tv_name.getText().toString().trim() );
                startActivityForResult( intent, 100 );
                break;
            case R.id.re_change_sex:
                editSexDialog();
                break;
            case R.id.re_change_wchat:
                intent = new Intent( getApplicationContext(), EditWchatActivity.class );
                intent.putExtra( "content", tv_what.getText().toString().trim() );
                startActivityForResult( intent, 200 );
                break;
            case R.id.re_change_cdoe:
                intent = new Intent( getApplicationContext(), EditCodeActivity.class );
                startActivity( intent );
                break;
            case R.id.change_phone:
                intent = new Intent( getApplicationContext(), ChangePhoneActivity.class );
                intent.putExtra( "phoneNum", phoneNum );
                startActivity( intent );
                break;
            case R.id.re_unbind_wchat:
                String openWchatId = PreferUtils.getString( getApplicationContext(), "openWchatId" );
                if (TextUtils.isEmpty( openWchatId )) {
                    ToastUtils.showBackgroudCenterToast( getApplicationContext(), "当前账户未绑定微信" );
                } else {
                    intent = new Intent( getApplicationContext(), UnBindWChatActivity.class );
                    intent.putExtra( "phoneNum", phoneNum );
                    startActivity( intent );
                }
                break;
        }
    }

    HashMap<String, String> map;

    /*修改性别*/
    private void editSexDialog() {
        dialog = new Dialog( PersonalActivity.this, R.style.transparentFrameWindowStyle );
        dialog.setContentView( R.layout.dialog_edit_sex );
        Window window = dialog.getWindow();
        window.setGravity( Gravity.CENTER | Gravity.CENTER );
        TextView boy = (TextView) dialog.findViewById( R.id.boy );
        TextView girl = (TextView) dialog.findViewById( R.id.girl );
        TextView cancel = (TextView) dialog.findViewById( R.id.cancel );
        cancel.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        } );
        boy.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                sex = "1";
                map = new HashMap<>();
                map.put( "gender", "1" );
                try {
                    JSONObject jsonObject = new JSONObject( map.toString() );
                    editData( jsonObject.toString() );
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } );
        girl.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                sex = "2";
                map = new HashMap<>();
                map.put( "gender", "2" );
                try {
                    JSONObject jsonObject = new JSONObject( map.toString() );
                    editData( jsonObject.toString() );
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } );
        dialog.show();
    }

    /*提交性别到后台*/
    private void editData(String s) {
        long timelineStr = System.currentTimeMillis() / 1000;
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put( Constant.TIMELINE, String.valueOf( timelineStr ) );
        map.put( Constant.PLATFORM, Constant.ANDROID );
        map.put( "member_id", member_id );
        map.put( "struct", s );
        String qianMingMapParam = ParamUtil.getQianMingMapParam( map );
        String token = EncryptUtil.encrypt( qianMingMapParam + Constant.NETKEY );
        map.put( Constant.TOKEN, token );
        String param = ParamUtil.getMapParam( map );
        MyApplication.getInstance().getMyOkHttp().post()
                .url( Constant.BASE_URL + Constant.EDITPERSONALDATA + "?" + param )
                .tag( this )
                .addHeader( "x-userid", member_id )
                .addHeader( "x-appid", Constant.APPID )
                .addHeader( "x-devid", PreferUtils.getString( getApplicationContext(), Constant.PESUDOUNIQUEID ) )
                .addHeader( "x-nettype", PreferUtils.getString( getApplicationContext(), Constant.NETWORKTYPE ) )
                .addHeader( "x-agent", VersionUtil.getVersionCode( getApplicationContext() ) )
                .addHeader( "x-platform", Constant.ANDROID )
                .addHeader( "x-devtype", Constant.IMEI )
                .addHeader( "x-token", ParamUtil.GroupMap( getApplicationContext(), member_id ) )
                .enqueue( new JsonResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, JSONObject response) {
                        super.onSuccess( statusCode, response );
                        Log.i( "编辑结果", response.toString() );
                        try {
                            JSONObject jsonObject = new JSONObject( response.toString() );
                            if (jsonObject.getString( "status" ).equals( "0" )) {
                                if (sex.equals( "1" )) {
                                    tv_sex.setText( "男" );
                                } else if (sex.equals( "2" )) {
                                    tv_sex.setText( "女" );
                                }
                                PreferUtils.putString( getApplicationContext(), "sex", sex );/*存储性别*/
                            } else {
                                ToastUtils.showToast( getApplicationContext(), Constant.NONET );
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, String error_msg) {
                        ToastUtils.showToast( getApplicationContext(), Constant.NONET );
                    }
                } );
    }

    Dialog dialog;
    Button btn_picture, btn_photo, btn_cancle;

    private void changeHeadImg() {
        View view = LayoutInflater.from( getApplicationContext() ).inflate( R.layout.photo_choose_dialog, null );
        dialog = new Dialog( PersonalActivity.this, R.style.transparentFrameWindowStyle );
        dialog.setContentView( view, new ViewGroup.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT ) );
        Window window = dialog.getWindow();
        // 设置显示动画
        window.setWindowAnimations( R.style.main_menu_animstyle );
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = 0;
        wl.y = getWindowManager().getDefaultDisplay().getHeight();
        // 以下这两句是为了保证按钮可以水平满屏
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        // 设置显示位置
        dialog.onWindowAttributesChanged( wl );
        dialog.setCanceledOnTouchOutside( true );
        dialog.show();
        btn_picture = (Button) view.findViewById( R.id.bt_picture );
        btn_photo = (Button) view.findViewById( R.id.bt_photo );
        btn_cancle = (Button) view.findViewById( R.id.bt_cancle );
        btn_picture.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPhoneImges();//打开图库
            }
        } );
        btn_photo.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamra( dialog );//打开照相机
            }
        } );
        btn_cancle.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        } );
    }

    /*相机*/
    private void openCamra(Dialog dialog) {
        if (ContextCompat.checkSelfPermission( PersonalActivity.this, Manifest.permission.CAMERA ) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission( PersonalActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED) {
            //没有存储和相机权限
            ActivityCompat.requestPermissions( PersonalActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2698 );
        } else {
            Intent intent;
            Uri pictureUri;
            //getMyPetRootDirectory()得到的是Environment.getExternalStorageDirectory() + File.separator+"MyPet"
            //也就是我之前创建的存放头像的文件夹（目录）
            File pictureFile = new File( PictureUtil.getMyPetRootDirectory(), IMAGE_FILE_NAME );
            // 判断当前系统
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent = new Intent( MediaStore.ACTION_IMAGE_CAPTURE );
                //这一句非常重要
                intent.addFlags( Intent.FLAG_GRANT_READ_URI_PERMISSION );
                //""中的内容是随意的，但最好用package名.provider名的形式，清晰明了
                pictureUri = FileProvider.getUriForFile( this, Constant.FILEPROVIDER, pictureFile );
            } else {
                intent = new Intent( MediaStore.ACTION_IMAGE_CAPTURE );
                pictureUri = Uri.fromFile( pictureFile );
            }
            // 去拍照,拍照的结果存到oictureUri对应的路径中
            intent.putExtra( MediaStore.EXTRA_OUTPUT, pictureUri );
            startActivityForResult( intent, TAKEPHOTO_CODE );
            dialog.dismiss();
        }
    }

    /*手机图库*/
    private void openPhoneImges() {
        if (ContextCompat.checkSelfPermission( PersonalActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission( PersonalActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED) {
            //没有存储权限
            ActivityCompat.requestPermissions( PersonalActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2699 );
        } else {
            try {
                intent = new Intent( Intent.ACTION_PICK );
                intent.setType( "image/*" );
                // 判断系统中是否有处理该 Intent 的 Activity
                if (intent.resolveActivity( getPackageManager() ) != null) {
                    startActivityForResult( intent, CHOOSEPHOTO_CODE );
                } else {
                    ToastUtils.showToast( getApplicationContext(), "未找到图片查看器" );
                }
                dialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    String name;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            /*图库选择回调*/
            case CHOOSEPHOTO_CODE:
                if (resultCode != RESULT_CANCELED) {
                    Uri uri = PictureUtil.getImageUri( this, data );
                    startPhotoZoom( uri );
                }
                break;
            /*相机*/
            case TAKEPHOTO_CODE:
                if (resultCode != RESULT_CANCELED) {
                    File pictureFile = new File( PictureUtil.getMyPetRootDirectory(), IMAGE_FILE_NAME );
                    Uri pictureUri;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        pictureUri = FileProvider.getUriForFile( this, Constant.FILEPROVIDER, pictureFile );
                    } else {
                        pictureUri = Uri.fromFile( pictureFile );
                    }
                    startPhotoZoom( pictureUri );
                }
                break;
            /*切割*/
            case CROPPHOTO_CODE:
                if (resultCode != RESULT_CANCELED) {
                    setPicToViewNew( uritempFile );
                }
                break;
            /*用户名修改回调*/
            case 100:
                if (resultCode == 1) {
                    name = data.getStringExtra( "name" );
                    tv_name.setText( name );
                }
                break;
            /*微信号修改回调*/
            case 200:
                if (resultCode == 1) {
                    name = data.getStringExtra( "name" );
                    tv_what.setText( name );
                }
                break;
        }
        super.onActivityResult( requestCode, resultCode, data );
    }


    public void setPicToViewNew(Uri uri) {
        if (uri != null) {
            Bitmap photo = null;
            try {
                photo = BitmapFactory.decodeStream( getContentResolver().openInputStream( uri ) );
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            // 创建 smallIcon 文件夹
            if (Environment.getExternalStorageState().equals( Environment.MEDIA_MOUNTED )) {
                File dirFile = new File( PictureUtil.getMyPetRootDirectory(), "Icon" );
                if (!dirFile.exists()) {
                    if (!dirFile.mkdirs()) {
                    } else {
                    }
                }
                File file = new File( dirFile, IMAGE_FILE_NAME );
                // 保存图片
                FileOutputStream outputStream = null;
                try {
                    outputStream = new FileOutputStream( file );
                    photo.compress( Bitmap.CompressFormat.JPEG, 100, outputStream );
                    outputStream.flush();
                    outputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                upPhotoDataToServie( file );
            }
        }
    }

    // 声明一个订阅方法，用于接收事件
    @Subscribe
    public void onEvent(String msg) {
        switch (msg) {
            case "phone_change":
                initView();
                break;
            case Constant.EDITUSERINFO:
                tv_code.setText( PreferUtils.getString( getApplicationContext(), "invite_code" ) );
                break;

        }
    }

    /*上传头像到服务器*/
    private void upPhotoDataToServie(File file) {
        long timelineStr = System.currentTimeMillis() / 1000;
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put( Constant.TIMELINE, String.valueOf( timelineStr ) );
        map.put( Constant.PLATFORM, Constant.ANDROID );
        map.put( "member_id", member_id );
        String mapParam = ParamUtil.getMapParam( map );
        String token = EncryptUtil.encrypt( mapParam + Constant.NETKEY );
        map.put( Constant.TOKEN, token );
        String param = ParamUtil.getMapParam( map );
        MyApplication.getInstance().getMyOkHttp().upload()
                .addFile( "file", file )
                .url( Constant.BASE_URL + Constant.UPDATEUSERHEAD + "?" + param )
                .addHeader( "x-userid", member_id )
                .addHeader( "x-appid", Constant.APPID )
                .addHeader( "x-devid", PreferUtils.getString( getApplicationContext(), Constant.PESUDOUNIQUEID ) )
                .addHeader( "x-nettype", PreferUtils.getString( getApplicationContext(), Constant.NETWORKTYPE ) )
                .addHeader( "x-agent", VersionUtil.getVersionCode( getApplicationContext() ) )
                .addHeader( "x-platform", Constant.ANDROID )
                .addHeader( "x-devtype", Constant.IMEI )
                .addHeader( "x-token", ParamUtil.GroupMap( getApplicationContext(), PreferUtils.getString( getApplicationContext(), "member_id" ) ) )
                .enqueue( new JsonResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, JSONObject response) {
                        super.onSuccess( statusCode, response );
                        Log.i( "上传结果", response.toString() );
                        try {
                            JSONObject jsonObject = new JSONObject( response.toString() );
                            int aReturn = jsonObject.getInt( "status" );
                            if (aReturn >= 0) {
                                String result = jsonObject.getString( "result" );
                                PreferUtils.putString( getApplicationContext(), "userImg", result );/*存储头像*/
                                Glide.with( getApplicationContext() ).load( result ).into( circleimageview );
                                EventBus.getDefault().post( "headimgChange" );
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
                        ToastUtils.showToast( getApplicationContext(), Constant.NONET );
                    }
                } );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister( this );
    }

    Uri uritempFile;

    private void startPhotoZoom(Uri uri) {
        Intent intent = new Intent( "com.android.camera.action.CROP" );
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags( Intent.FLAG_GRANT_READ_URI_PERMISSION );
        }
        intent.setDataAndType( uri, "image/*" );
        intent.putExtra( "crop", "true" );
        intent.putExtra( "aspectX", 1 ); // 裁剪框比例
        intent.putExtra( "aspectY", 1 );
        intent.putExtra( "outputX", 300 ); // 输出图片大小
        intent.putExtra( "outputY", 300 );
        uritempFile = Uri.parse( "file://" + "/" + Environment.getExternalStorageDirectory().getPath() + "/" + "small.jpg" );
        intent.putExtra( MediaStore.EXTRA_OUTPUT, uritempFile );
        intent.putExtra( "outputFormat", Bitmap.CompressFormat.JPEG.toString() );
        startActivityForResult( intent, CROPPHOTO_CODE );
    }

    private void loginOutData() {
        dialog = new Dialog( PersonalActivity.this, R.style.transparentFrameWindowStyle );
        dialog.setContentView( R.layout.dialog_login_out );
        Window window = dialog.getWindow();
        window.setGravity( Gravity.CENTER | Gravity.CENTER );
        TextView sure = (TextView) dialog.findViewById( R.id.sure );
        TextView cancel = (TextView) dialog.findViewById( R.id.cancel );
        sure.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                /*移除友盟推送别名*/
                PushAgent.getInstance( getApplicationContext() ).deleteAlias( member_id, Constant.YOUMENGPUSH, new UTrack.ICallBack() {
                    @Override
                    public void onMessage(boolean b, String s) {
                        Log.i( "退出友盟", s );
                    }
                } );
                PreferUtils.putBoolean( getApplicationContext(), "isLogin", false );
                EventBus.getDefault().post( Constant.LOGIN_OUT );
                intent = new Intent( getApplicationContext(), MainActivity.class );
                intent.putExtra( "loginout", "loginout" );
                startActivity( intent );
                finish();
            }
        } );
        cancel.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        } );
        dialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 2698:/*相机权限回调*/
                if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    ToastUtils.showToast( getApplicationContext(), "访问相机需要打开存储和相机权限，请前往设置-应用-果冻宝盒-权限进行设置" );
                } else if (grantResults.length <= 1 || grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                    ToastUtils.showToast( getApplicationContext(), "访问相机需要打开存储和相机权限，请前往设置-应用-果冻宝盒-权限进行设置" );
                } else if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults.length > 1 && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent;
                    Uri pictureUri;
                    //getMyPetRootDirectory()得到的是Environment.getExternalStorageDirectory() + File.separator+"MyPet"
                    //也就是我之前创建的存放头像的文件夹（目录）
                    File pictureFile = new File( PictureUtil.getMyPetRootDirectory(), IMAGE_FILE_NAME );
                    // 判断当前系统
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        intent = new Intent( MediaStore.ACTION_IMAGE_CAPTURE );
                        //这一句非常重要
                        intent.addFlags( Intent.FLAG_GRANT_READ_URI_PERMISSION );
                        //""中的内容是随意的，但最好用package名.provider名的形式，清晰明了
                        pictureUri = FileProvider.getUriForFile( this, Constant.FILEPROVIDER, pictureFile );
                    } else {
                        intent = new Intent( MediaStore.ACTION_IMAGE_CAPTURE );
                        pictureUri = Uri.fromFile( pictureFile );
                    }
                    // 去拍照,拍照的结果存到oictureUri对应的路径中
                    intent.putExtra( MediaStore.EXTRA_OUTPUT, pictureUri );
                    startActivityForResult( intent, TAKEPHOTO_CODE );
                    dialog.dismiss();
                }
                break;
            case 2699:/*相册权限回调*/
                if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    ToastUtils.showToast( getApplicationContext(), "访问相册需要打开存储权限，请前往设置-应用-果冻宝盒-权限进行设置" );
                    return;
                } else if (grantResults.length <= 1 || grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                    ToastUtils.showToast( getApplicationContext(), "访问相册需要打开存储权限，请前往设置-应用-果冻宝盒-权限进行设置" );
                    return;
                } else if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults.length > 1 && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    intent = new Intent( Intent.ACTION_PICK );
                    intent.setType( "image/*" );
                    // 判断系统中是否有处理该 Intent 的 Activity
                    if (intent.resolveActivity( getPackageManager() ) != null) {
                        startActivityForResult( intent, CHOOSEPHOTO_CODE );
                    } else {
                        ToastUtils.showToast( getApplicationContext(), "未找到图片查看器" );
                    }
                    dialog.dismiss();
                }
                break;
        }
    }

}
