package com.guodongbaohe.app.util;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by 李晨奇 on 2017/6/8.
 */

public class PermissionUtils {
    public static int ResultCode1 = 100;//权限请求码
    public static int ResultCode2 = 200;//权限请求码
    public static int ResultCode3 = 300;//权限请求码
    public static String PermissionTip1 = "亲爱的用户 \n\n软件部分功能需要请求您的手机权限，请允许以下权限：\n\n";//权限提醒
    public static String PermissionTip2 = "\n请到 “应用信息 -> 权限” 中授予！";//权限提醒
    public static String PermissionDialogPositiveButton = "去手动授权";
    public static String PermissionDialogNegativeButton = "取消";
    /*读写照相权限*/
    private static String[] PERMISSIONS_CAMERA_AND_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};

    /*读写权限*/
    private static String[] PERMISSIONS_WRITE_AND_READ = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,};

    public static boolean isCameraPermission(Activity context, int requestCode) {
        if (Build.VERSION.SDK_INT >= 23) {
            int storagePermission = ActivityCompat.checkSelfPermission(context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int cameraPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA);
            if (storagePermission != PackageManager.PERMISSION_GRANTED || cameraPermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(context, PERMISSIONS_CAMERA_AND_STORAGE,
                        requestCode);
                return false;
            }
        }
        return true;
    }

    public static boolean isWrite(Activity context, int requestCode) {
        if (Build.VERSION.SDK_INT >= 23) {
            int storagePermission = ActivityCompat.checkSelfPermission(context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int storagePermission_read = ActivityCompat.checkSelfPermission(context,
                    Manifest.permission.READ_EXTERNAL_STORAGE);
            if (storagePermission != PackageManager.PERMISSION_GRANTED && storagePermission_read != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(context, PERMISSIONS_WRITE_AND_READ,
                        requestCode);
                return false;
            }
        }
        return true;
    }
    private static PermissionUtils permissionUtils;
    public static PermissionUtils getInstance(){
        if(permissionUtils == null){
            permissionUtils = new PermissionUtils();
        }
        return permissionUtils;
    }
    private HashMap<String,String> permissions;
    public HashMap<String,String> getPermissions(){
        if(permissions == null){
            permissions = new HashMap<>();
            initPermissions();
        }
        return permissions;
    }
    private void initPermissions(){
        //短信权限
        permissions.put("android.permission.READ_SMS","--短信");
        permissions.put("android.permission.RECEIVE_WAP_PUSH","--短信");
        permissions.put("android.permission.RECEIVE_MMS","--短信");
        permissions.put("android.permission.RECEIVE_SMS","--短信");
        permissions.put("android.permission.SEND_SMS","--短信");
        permissions.put("android.permission.READ_CELL_BROADCASTS","--短信");
    }
    /**
     * 获得权限名称集合（去重）
     * @param permission 权限数组
     * @return 权限名称
     */
    public String getPermissionNames(List<String> permission){
        if(permission==null || permission.size()==0){
            return "\n";
        }
        StringBuilder sb = new StringBuilder();
        List<String> list = new ArrayList<>();
        HashMap<String,String> permissions = getPermissions();
        for(int i=0; i<permission.size(); i++){
            String name = permissions.get(permission.get(i));
            if(name!=null && !list.contains(name)){
                list.add(name);
                sb.append(name);
                sb.append("\n");
            }
        }
        return sb.toString();
    }
}
