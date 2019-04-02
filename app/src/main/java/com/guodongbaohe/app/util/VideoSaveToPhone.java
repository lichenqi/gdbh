package com.guodongbaohe.app.util;

import android.os.Environment;

import java.io.File;
import java.util.UUID;

public class VideoSaveToPhone {

    private static String generateFileName() {
        return UUID.randomUUID().toString();
    }

    private static final String SD_PATH = Environment.getExternalStorageDirectory().getPath() + "/果冻宝盒/";

    /*视频保存目录*/
    public static String saveVideoUrlToFile() {
        String savePath;
        String fileName = generateFileName() + ".mp4";
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            savePath = SD_PATH;
        } else {
            return null;
        }
        File filePic = new File(savePath + fileName);
        return filePic.getPath();
    }

    /*图片保存目录*/
    public static String saveImageUrlToFile() {
        String savePath;
        String fileName = generateFileName() + ".JPEG";
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            savePath = SD_PATH;
        } else {
            return null;
        }
        File filePic = new File(savePath + fileName);
        return filePic.getPath();
    }

}
