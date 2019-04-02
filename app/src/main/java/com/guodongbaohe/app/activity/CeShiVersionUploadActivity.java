package com.guodongbaohe.app.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.guodongbaohe.app.R;
import com.guodongbaohe.app.bean.VersionBean;
import com.guodongbaohe.app.common_constant.Constant;
import com.guodongbaohe.app.common_constant.MyApplication;
import com.guodongbaohe.app.myokhttputils.response.DownloadResponseHandler;
import com.guodongbaohe.app.myokhttputils.response.JsonResponseHandler;
import com.guodongbaohe.app.util.CleanDataUtil;
import com.guodongbaohe.app.util.GsonUtil;
import com.guodongbaohe.app.util.ParamUtil;
import com.guodongbaohe.app.util.PreferUtils;
import com.guodongbaohe.app.util.VersionUtil;
import com.guodongbaohe.app.util.VideoSaveToPhone;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class CeShiVersionUploadActivity extends AppCompatActivity {
    String local_version;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*版本升级接口*/
        local_version = VersionUtil.getAndroidNumVersion(getApplicationContext());
        getVersionCodeData();
    }

    String download, title, desc, is_update;

    /*版本升级接口*/
    private void getVersionCodeData() {
        MyApplication.getInstance().getMyOkHttp().post().url(Constant.BASE_URL + Constant.VERSIONUPDATE)
                .tag(this)
                .addHeader("x-appid", Constant.APPID)
                .addHeader("x-devid", PreferUtils.getString(getApplicationContext(), Constant.PESUDOUNIQUEID))
                .addHeader("x-nettype", PreferUtils.getString(getApplicationContext(), Constant.NETWORKTYPE))
                .addHeader("x-agent", VersionUtil.getVersionCode(getApplicationContext()))
                .addHeader("x-platform", Constant.ANDROID)
                .addHeader("x-devtype", Constant.IMEI)
                .addHeader("x-token", ParamUtil.GroupMap(getApplicationContext(), ""))
                .enqueue(new JsonResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, JSONObject response) {
                        super.onSuccess(statusCode, response);
                        Log.i("版本信息", response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            if (jsonObject.getInt("status") >= 0) {
                                VersionBean versionBean = GsonUtil.GsonToBean(response.toString(), VersionBean.class);
                                if (versionBean == null) return;
                                VersionBean.VersionData result = versionBean.getResult();
                                is_update = result.getIs_update();/*是否强制更新标识 no 代表随意；yes 代表强制更新*/
                                desc = result.getDesc();
                                title = result.getTitle();
                                download = result.getDownload();
                                String version = result.getVersion();
                                Integer localCode = Integer.valueOf(local_version.replace(".", "").trim());
                                if (Integer.valueOf(version) > localCode) {
                                    versionUpdataDialog();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, String error_msg) {

                    }
                });
    }

    /*版本升级弹窗*/
    private void versionUpdataDialog() {
        final Dialog dialog = new Dialog(CeShiVersionUploadActivity.this, R.style.activitydialog);
        dialog.setContentView(R.layout.version_update_dialog);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER | Gravity.CENTER);
        TextView tv_one = (TextView) dialog.findViewById(R.id.tv_one);
        TextView tv_two = (TextView) dialog.findViewById(R.id.tv_two);
        TextView sure = (TextView) dialog.findViewById(R.id.sure);
        tv_one.setText(title);
        tv_two.setText(desc);
        RelativeLayout cancel = (RelativeLayout) dialog.findViewById(R.id.cancel);
        if (!TextUtils.isEmpty(is_update) && is_update.equals("yes")) {
            cancel.setVisibility(View.GONE);
        } else {
            cancel.setVisibility(View.VISIBLE);
        }
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (ContextCompat.checkSelfPermission(CeShiVersionUploadActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(CeShiVersionUploadActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    //没有存储权限
                    ActivityCompat.requestPermissions(CeShiVersionUploadActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2699);
                } else {
                    downLoadApk();
                }
            }
        });
        if (!TextUtils.isEmpty(is_update) && is_update.equals("yes")) {
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
        } else {
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(true);
        }
        dialog.show();
    }

    private void downLoadApk() {
        MyApplication.getInstance().getMyOkHttp().download().tag(this)
                .url(download)
                .filePath(VideoSaveToPhone.saveApkUrlToFile(getApplicationContext()))
                .enqueue(new DownloadResponseHandler() {

                    @Override
                    public void onStart(long totalBytes) {
                        super.onStart(totalBytes);
                        Log.i("下载开始", totalBytes + "");
                    }

                    @Override
                    public void onFinish(File downloadFile) {
                        Log.i("下载完成", downloadFile.getPath());
                        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + downloadFile.getPath())));
                        /*下载完成去安转apk*/
                        installAPK(downloadFile);
                    }

                    @Override
                    public void onProgress(long currentBytes, long totalBytes) {

                    }

                    @Override
                    public void onFailure(String error_msg) {

                    }
                });
    }

    /*安转apk文件*/
    private void installAPK(File downloadFile) {
        File apkFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "gdbh.apk");
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri apkUri = FileProvider.getUriForFile(getApplicationContext(), Constant.FILEPROVIDER, downloadFile);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(downloadFile), "application/vnd.android.package-archive");
        }
        CleanDataUtil.clearAllCache(getApplicationContext());
        startActivity(intent);
    }

}
