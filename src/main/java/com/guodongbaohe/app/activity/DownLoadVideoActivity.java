package com.guodongbaohe.app.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.guodongbaohe.app.MainActivity;
import com.guodongbaohe.app.R;
import com.guodongbaohe.app.base_activity.BaseActivity;
import com.guodongbaohe.app.common_constant.Constant;
import com.guodongbaohe.app.request.IRequestPermissions;
import com.guodongbaohe.app.request.IRequestPermissionsResult;
import com.guodongbaohe.app.request.RequestPermissions;
import com.guodongbaohe.app.request.RequestPermissionsResultSetApp;
import com.guodongbaohe.app.service.FloatWindowService;
import com.guodongbaohe.app.util.PermissionUtils;
import com.guodongbaohe.app.util.PreferUtils;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.security.Permission;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.ButterKnife;

public class DownLoadVideoActivity extends BaseActivity {
    IRequestPermissions requestPermissions = RequestPermissions.getInstance();//动态权限请求
    IRequestPermissionsResult requestPermissionsResult = RequestPermissionsResultSetApp.getInstance();//动态权限请求结果处理



    private static DownLoadVideoActivity instance;
    private int max;
    String url;

    @Override
    public int getContainerView() {
        return R.layout.downloadvideo;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);


        url=getIntent().getStringExtra("url");






    }




}
