package com.guodongbaohe.app.util;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.util.UUID;

public class VideoSaveToPhone {

    private static String generateFileName() {
        return UUID.randomUUID().toString();
    }

    /*视频保存目录*/
    public static String saveVideoUrlToFile(Context context) {
        File file = null;
        File videoFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        String fileName = "/" + generateFileName() + ".mp4";
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            file = new File(videoFile + fileName);
        } else {
            ToastUtils.showToast(context, "手机sd卡不存在");
        }
        return file.getPath();
    }

    /*图片保存目录*/
    public static String saveImageUrlToFile(Context context) {

//        /*第一种存储图片方式  这种方式更加灵活，可以自己指定目录*/
//        File imageFile = Environment.getExternalStorageDirectory();
//        String fileName = generateFileName() + ".png";
//        File filePic = null;
//        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//            filePic = new File(imageFile.getPath() + "/果冻宝盒/" + fileName);
//        } else {
//            ToastUtils.showToast(context, "手机sd卡不存在");
//        }
//        return filePic.getPath();

        /*第二种存储图片方式  这种方式更加方便的访问Android给我们提供好的一些公共目录的方法*/
        File imageFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        String fileName = "/" + generateFileName() + ".png";
        File filePic = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            filePic = new File(imageFile + fileName);
        } else {
            ToastUtils.showToast(context, "手机sd卡不存在");
        }
        return filePic.getPath();

    }

    /*下载的apk文件存放的目录*/
    public static String saveApkUrlToFile(Context context) {
        File downApkFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        String fileName = "/gdbh.apk";
        File filePic = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            filePic = new File(downApkFile + fileName);
        } else {
            ToastUtils.showToast(context, "手机sd卡不存在");
        }
        return filePic.getPath();
    }

}
