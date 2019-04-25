package com.guodongbaohe.app.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;

import com.guodongbaohe.app.R;
import com.guodongbaohe.app.base_activity.BigBaseActivity;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class VideoSurfaceViewActivity extends BigBaseActivity implements SurfaceHolder.Callback, MediaPlayer.OnBufferingUpdateListener, MediaController.MediaPlayerControl {
    @BindView(R.id.re_parent)
    LinearLayout re_parent;
    @BindView(R.id.iv_back)
    ImageView iv_back;
    @BindView(R.id.iv_video_download)
    ImageView iv_video_download;
    @BindView(R.id.videoView)
    SurfaceView surfaceView;
    String videoUrl;
    int widthPixels, heightPixels;
    MediaPlayer mediaPlayer;
    MediaController mediaController;
    private int bufferPercentage = 0;

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
        mediaPlayer = new MediaPlayer();
        mediaController = new MediaController( this );
        mediaController.setAnchorView( re_parent );
        surfaceView.setZOrderOnTop( false );
        surfaceView.getHolder().setType( SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS );
        surfaceView.getHolder().addCallback( this );
        surfaceView.requestFocus();
        try {
            mediaPlayer.setDataSource( videoUrl );
            mediaPlayer.setOnBufferingUpdateListener( this );
            mediaController.setMediaPlayer( this );
            mediaController.setEnabled( true );
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @OnClick({R.id.iv_back})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                finish();
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mediaPlayer) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mediaController.show();
        return super.onTouchEvent( event );
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        bufferPercentage = percent;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mediaPlayer.setDisplay( holder );
        mediaPlayer.prepareAsync();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    public void changeVideoSize() {
        int videoWidth = mediaPlayer.getVideoWidth();
        int videoHeight = mediaPlayer.getVideoHeight();
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
        surfaceView.setLayoutParams( new RelativeLayout.LayoutParams( videoWidth, videoHeight ) );
    }

    @Override
    public void start() {
        if (null != mediaPlayer) {
            mediaPlayer.start();
        }
    }

    @Override
    public void pause() {
        if (null != mediaPlayer) {
            mediaPlayer.pause();
        }
    }

    @Override
    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    @Override
    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    @Override
    public void seekTo(int pos) {
        mediaPlayer.seekTo( pos );
    }

    @Override
    public boolean isPlaying() {
        if (mediaPlayer.isPlaying()) {
            return true;
        }
        return false;
    }

    @Override
    public int getBufferPercentage() {
        return bufferPercentage;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }


}