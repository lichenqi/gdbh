package com.guodongbaohe.app.util;

import android.os.Environment;

import java.io.File;

/**
 * Created by lcq on 2017/7/26.
 */

public class IOUtils {

    public static File getPathFile(String path) {
        File outputFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), path);
        return outputFile;
    }

    public static void rmoveFile(String path) {
        File file = getPathFile(path);
        if (file.exists()) {
            file.delete();
        }
    }
}
