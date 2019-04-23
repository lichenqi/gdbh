package com.guodongbaohe.app.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.session.MediaController;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.guodongbaohe.app.R;
import com.guodongbaohe.app.base_activity.BigBaseActivity;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VideoSurfaceViewActivity extends BigBaseActivity implements SurfaceHolder.Callback {
    @BindView(R.id.re_parent)
    RelativeLayout re_parent;
    @BindView(R.id.iv_back)
    ImageView iv_back;
    @BindView(R.id.iv_video_download)
    ImageView iv_video_download;
    @BindView(R.id.videoView)
    SurfaceView videoView;
    private MediaPlayer player;
    private SurfaceHolder holder;
    String videoUrl;
    int widthPixels, heightPixels;
    private MediaController mediaController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.videosurfaceviewactivity );
        ButterKnife.bind( this );
        Intent intent = getIntent();
        videoUrl = intent.getStringExtra( "url" );
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        widthPixels = displayMetrics.widthPixels;
        heightPixels = displayMetrics.heightPixels;
        holder = videoView.getHolder();
        holder.addCallback( this );
        holder.setKeepScreenOn( true );
        player = new MediaPlayer();
        player.setAudioStreamType( AudioManager.STREAM_MUSIC );
        player.setOnPreparedListener( new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                if (!player.isPlaying()) {
                    player.start();
                }
            }
        } );
        player.setOnVideoSizeChangedListener( new MediaPlayer.OnVideoSizeChangedListener() {
            @Override
            public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                changeVideoSize();
            }
        } );
        try {
            player.setDataSource( videoUrl );
            player.setVideoScalingMode( MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING );
            player.setLooping( true );
            player.prepare();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        player.setDisplay( holder );
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (player != null) {
            player.release();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.release();
        }
    }

    public void changeVideoSize() {
        int videoWidth = player.getVideoWidth();
        int videoHeight = player.getVideoHeight();
        //根据视频尺寸去计算->视频可以在sufaceView中放大的最大倍数。
        float max;
        if (getResources().getConfiguration().orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            //竖屏模式下按视频宽度计算放大倍数值
            max = Math.max( (float) videoWidth / (float) widthPixels, (float) videoHeight / (float) heightPixels );
        } else {
            //横屏模式下按视频高度计算放大倍数值
            max = Math.max( ((float) videoWidth / (float) heightPixels), (float) videoHeight / (float) widthPixels );
        }
        //视频宽高分别/最大倍数值 计算出放大后的视频尺寸
        videoWidth = (int) Math.ceil( (float) videoWidth / max );
        videoHeight = (int) Math.ceil( (float) videoHeight / max );
        //无法直接设置视频尺寸，将计算出的视频尺寸设置到surfaceView 让视频自动填充。
        videoView.setLayoutParams( new RelativeLayout.LayoutParams( videoWidth, videoHeight ) );
    }
}