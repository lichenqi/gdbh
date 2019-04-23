package com.guodongbaohe.app.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.guodongbaohe.app.R;
import com.guodongbaohe.app.common_constant.MyApplication;
import com.guodongbaohe.app.myokhttputils.response.DownloadResponseHandler;
import com.guodongbaohe.app.util.ToastUtils;
import com.guodongbaohe.app.util.VideoSaveToPhone;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class VideoPlayActivity extends AppCompatActivity {
    @BindView(R.id.iv_back)
    ImageView iv_back;
    @BindView(R.id.videoView)
    VideoView videoView;
    @BindView(R.id.iv_video_download)
    ImageView iv_video_download;
    String videoUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.video_view );
        ButterKnife.bind( this );
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags( WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS );
        }
        videoUrl = getIntent().getStringExtra( "url" );
        videoView.setVideoPath( videoUrl );
        //创建MediaController对象
        MediaController mediaController = new MediaController( this );
        //VideoView与MediaController建立关联
        videoView.setMediaController( mediaController );
        //让VideoView获取焦点
        videoView.requestFocus();
        videoView.start();
        videoView.setOnCompletionListener( new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                videoView.start();
            }
        } );
    }

    @OnClick({R.id.iv_video_download, R.id.iv_back})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.iv_video_download:/*下载视频*/
                ToastUtils.showToast( getApplicationContext(), "视频开始下载..." );
                if (ContextCompat.checkSelfPermission( VideoPlayActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission( VideoPlayActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED) {
                    //没有存储权限
                    ActivityCompat.requestPermissions( VideoPlayActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1 );
                } else {
                    VideoDownLoad();
                }
                break;
            case R.id.iv_back:
                finish();
                break;
        }
    }

    /*视频下载*/
    private void VideoDownLoad() {
        MyApplication.getInstance().getMyOkHttp().download().tag( this )
                .url( videoUrl )
                .filePath( VideoSaveToPhone.saveVideoUrlToFile( getApplicationContext() ) )
                .enqueue( new DownloadResponseHandler() {

                    @Override
                    public void onStart(long totalBytes) {
                        super.onStart( totalBytes );
                    }

                    @Override
                    public void onCancel() {
                        super.onCancel();
                    }

                    @Override
                    public void onFinish(File downloadFile) {
                        sendBroadcast( new Intent( Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse( "file://" + downloadFile.getPath() ) ) );
                        ToastUtils.showToast( getApplicationContext(), "视频已下载完成" );
                    }

                    @Override
                    public void onProgress(long currentBytes, long totalBytes) {

                    }

                    @Override
                    public void onFailure(String error_msg) {
                        ToastUtils.showToast( getApplicationContext(), "下载失败" );
                    }
                } );
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged( newConfig );
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            getWindow().clearFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN );
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams( LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT );
            params.addRule( RelativeLayout.CENTER_IN_PARENT );
            videoView.setLayoutParams( params );
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getWindow().addFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN );
            videoView.setLayoutParams( new RelativeLayout.LayoutParams( LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT ) );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:/*下载权限回调*/
                if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    ToastUtils.showToast( getApplicationContext(), "需要打开存储权限，请前往设置-应用-果冻宝盒-权限进行设置" );
                    return;
                } else if (grantResults.length <= 1 || grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                    ToastUtils.showToast( getApplicationContext(), "需要打开存储权限，请前往设置-应用-果冻宝盒-权限进行设置" );
                    return;
                } else if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults.length > 1 && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    VideoDownLoad();
                }
                break;
        }
    }

}
