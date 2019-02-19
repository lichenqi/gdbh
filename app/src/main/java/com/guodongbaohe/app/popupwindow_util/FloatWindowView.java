package com.guodongbaohe.app.popupwindow_util;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.VideoView;

import com.guodongbaohe.app.R;
import com.guodongbaohe.app.activity.XinShouJiaoChengActivity;

public class FloatWindowView extends LinearLayout {
    /**
     * 记录悬浮窗的宽度
     */
    public static int viewWidth;

    /**
     * 记录悬浮窗的高度
     */
    public static int viewHeight;
    private Context mContext;
    private VideoView videoView;


    public FloatWindowView(final Context context) {
        super(context);
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.downloadvideo, this);
        View view = findViewById(R.id.big_window_layout);
        viewWidth = view.getLayoutParams().width;
        viewHeight = view.getLayoutParams().height;

        videoView = (VideoView) this.findViewById(R.id.video_view);

        if (!TextUtils.isEmpty(XinShouJiaoChengActivity.path + "/" + XinShouJiaoChengActivity.videoName)) {
            Log.e("TAG", "播放路径不存在！");
            //可以加载项目中资源文件里面的默认视频

            return;
        }

        videoView.setVideoPath(XinShouJiaoChengActivity.path + "/" + XinShouJiaoChengActivity.videoName);
        videoView.setZOrderOnTop(true);
        videoView.setZOrderMediaOverlay(true);
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer mp) {
                Log.e("TAG", "onPrepared");
                mp.start();
                mp.setLooping(true);

                Intent intentHome = new Intent(Intent.ACTION_MAIN);
                intentHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intentHome.addCategory(Intent.CATEGORY_HOME);
                mContext.startActivity(intentHome);
            }
        });

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.e("TAG", "onCompletion");
                videoView.setVideoPath(XinShouJiaoChengActivity.path + "/" + XinShouJiaoChengActivity.videoName);
                videoView.start();

            }
        });
        videoView.start();
    }
}
