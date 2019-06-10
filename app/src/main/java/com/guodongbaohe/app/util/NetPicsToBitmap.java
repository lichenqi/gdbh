package com.guodongbaohe.app.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class NetPicsToBitmap {

    public static Bitmap returnBitMap(String url) {
        URL myFileUrl = null;
        Bitmap bitmap = null;
        try {
            myFileUrl = new URL( url );
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
            conn.setDoInput( true );
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream( is );
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /*网络路劲转bitmap*/
    public static Bitmap getBitmap(String url) {
        Bitmap bm = null;
        try {
            URL iconUrl = new URL( url );
            URLConnection conn = iconUrl.openConnection();
            HttpURLConnection http = (HttpURLConnection) conn;
            int length = http.getContentLength();
            conn.connect();
            // 获得图像的字符流
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream( is, length );
            bm = BitmapFactory.decodeStream( bis );
            bis.close();
            is.close();// 关闭流
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bm;
    }

    /*bitmap图片转成String*/
    public static String convertIconToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();// outputstream
        bitmap.compress( Bitmap.CompressFormat.PNG, 100, baos );
        byte[] appicon = baos.toByteArray();// 转为byte数组
        return Base64.encodeToString( appicon, Base64.DEFAULT );
    }

    /*string转成bitmap*/
    public static Bitmap convertStringToIcon(String st) {
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray;
            bitmapArray = Base64.decode( st, Base64.DEFAULT );
            bitmap = BitmapFactory.decodeByteArray( bitmapArray, 0, bitmapArray.length );
            return bitmap;
        } catch (Exception e) {
            return null;
        }
    }

    public static Bitmap getBitmaps(String imgUrl) {
        InputStream inputStream = null;
        ByteArrayOutputStream outputStream = null;
        URL url = null;
        try {
            url = new URL( imgUrl );
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod( "GET" );
            httpURLConnection.setReadTimeout( 2000 );
            httpURLConnection.connect();
            if (httpURLConnection.getResponseCode() == 200) {
                //网络连接成功
                inputStream = httpURLConnection.getInputStream();
                outputStream = new ByteArrayOutputStream();
                byte buffer[] = new byte[1024 * 8];
                int len = -1;
                while ((len = inputStream.read( buffer )) != -1) {
                    outputStream.write( buffer, 0, len );
                }
                byte[] bu = outputStream.toByteArray();
                Bitmap bitmap = BitmapFactory.decodeByteArray( bu, 0, bu.length );
                return bitmap;
            } else {
            }
        } catch (Exception e) {
            // TODO: handle exception
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /*file转Uri*/
    public static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query( MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID}, MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null );
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt( cursor.getColumnIndex( MediaStore.MediaColumns._ID ) );
            Uri baseUri = Uri.parse( "content://media/external/images/media" );
            return Uri.withAppendedPath( baseUri, "" + id );
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put( MediaStore.Images.Media.DATA, filePath );
                return context.getContentResolver().insert( MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values );
            } else {
                return null;
            }
        }
    }

}
