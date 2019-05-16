package com.guodongbaohe.app.util;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class BitmapShareManager {

    private Context context;
    private List<File> files = new ArrayList<>();

    public BitmapShareManager(Context context) {
        this.context = context;
    }

    public void setShareImage(final List<Bitmap> hebingbitmap, final int i, String type) {
        final Dialog loadingDialog = DialogUtil.createLoadingDialog( context, "分享..." );
        if (type.equals( "qq" ) && !Tools.isAppAvilible( context, "com.tencent.mobileqq" )) {
            ToastUtils.showToast( context, "您还没有安装QQ客户端，请先安装QQ客户端" );
            DialogUtil.closeDialog( loadingDialog, context );
            return;
        } else if (type.equals( "wchat" ) && !Tools.isAppAvilible( context, "com.tencent.mm" )) {
            ToastUtils.showToast( context, "您还没有安装微信客户端,请先安转客户端" );
            DialogUtil.closeDialog( loadingDialog, context );
            return;
        } else if (type.equals( "qq_zone" ) && !Tools.isAppAvilible( context, "com.qzone" )) {
            ToastUtils.showToast( context, "您还没有安装QQ空间客户端,请先安装" );
            DialogUtil.closeDialog( loadingDialog, context );
            return;
        }
        new Thread( new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < hebingbitmap.size(); i++) {
                    File file_one = Tools.saveBitmapToSd( context, hebingbitmap.get( i ) );
                    files.add( file_one );
                }
                Intent intent = new Intent();
                ComponentName comp;
                if (type.contains( "qq" )) {
                    if (i == 0) {
                        comp = new ComponentName( "com.tencent.mobileqq", "com.tencent.mobileqq.activity.JumpActivity" );
                    } else {
                        comp = new ComponentName( "com.qzone", "com.qzonex.module.operation.ui.QZonePublishMoodActivity" );
                    }
                } else {
                    if (i == 0) {
                        /*微信好友*/
                        comp = new ComponentName( "com.tencent.mm", "com.tencent.mm.ui.tools.ShareImgUI" );
                    } else {
                        /*微信朋友圈*/
                        comp = new ComponentName( "com.tencent.mm", "com.tencent.mm.ui.tools.ShareToTimeLineUI" );
                    }
                }
                intent.setComponent( comp );
                intent.setAction( Intent.ACTION_SEND_MULTIPLE );
                intent.setType( "image/*" );
                intent.putExtra( "Kdescription", "" );
                ArrayList<Uri> imageUris = new ArrayList<>();
                for (File f : files) {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                        //android 7.0以下
                        imageUris.add( Uri.fromFile( f ) );
                    } else {
                        //android 7.0及以上
                        Uri uri = null;
                        try {
                            uri = Uri.parse( MediaStore.Images.Media.insertImage( context.getContentResolver(), f.getAbsolutePath(), f.getName(), null ) );
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        imageUris.add( uri );
                    }
                }
                intent.putParcelableArrayListExtra( Intent.EXTRA_STREAM, imageUris );
                DialogUtil.closeDialog( loadingDialog, context );
                context.startActivity( intent );
            }
        } ).start();
    }
}
