package com.guodongbaohe.app.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.guodongbaohe.app.R;
import com.guodongbaohe.app.bean.BarrageViewBean;
import com.guodongbaohe.app.common_constant.MyApplication;
import com.guodongbaohe.app.myokhttputils.response.DownloadResponseHandler;
import com.guodongbaohe.app.util.CommonUtil;
import com.guodongbaohe.app.util.NetUtil;
import com.guodongbaohe.app.util.NumberAnimation;
import com.guodongbaohe.app.util.ToastUtils;
import com.guodongbaohe.app.util.VideoSaveToPhone;
import com.guodongbaohe.app.view.BarrageView;
import com.guodongbaohe.app.view.HeartView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class NewShopDetialActivity extends AppCompatActivity {
    HeartView main_heartview;
    private BarrageView barrageView;
    private List<BarrageViewBean> barrageViews;
    private ImageView yincang, dianzan;
    private LinearLayout sucai;
    ImageView iv_material_down_load;
    private int isfirst = 0;
    private List<String> imagesList;
    private String videoUrl = "https://cloud.video.taobao.com//play/u/393300180/p/1/e/6/t/1/215525269409.mp4";
    private int num = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newshopdetial);
        iv_material_down_load = (ImageView) findViewById(R.id.iv_material_down_load);
        main_heartview = (HeartView) findViewById(R.id.main_heartview);
        barrageView = (BarrageView) findViewById(R.id.barrageview);
        yincang = (ImageView) findViewById(R.id.yincang);
        dianzan = (ImageView) findViewById(R.id.dianzan);
        sucai = (LinearLayout) findViewById(R.id.sucai);
        yincang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isfirst == 0) {
                    sucai.setVisibility(View.GONE);
                    barrageView.setVisibility(View.GONE);
                    isfirst = 1;
                } else if (isfirst == 1) {
                    sucai.setVisibility(View.VISIBLE);
                    barrageView.setVisibility(View.VISIBLE);
                    isfirst = 0;
                }
            }
        });
        dianzan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < 6; i++) {
                    main_heartview.addHeart();
                }
            }
        });
        init();
        initDownLoad();
    }

    /*素材下载*/
    private void initDownLoad() {
        imagesList = new ArrayList<>();
        imagesList.add("https://img.alicdn.com/i4/2303875888/TB2I5YRc8cHL1JjSZFBXXaiGXXa_!!2303875888.png");
        imagesList.add("https://img.alicdn.com/i4/2303875888/TB2I5YRc8cHL1JjSZFBXXaiGXXa_!!2303875888.png");
        imagesList.add("https://img.alicdn.com/i4/2303875888/TB2I5YRc8cHL1JjSZFBXXaiGXXa_!!2303875888.png");
        imagesList.add("https://img.alicdn.com/i4/2303875888/TB2I5YRc8cHL1JjSZFBXXaiGXXa_!!2303875888.png");
        imagesList.add("https://img.alicdn.com/i4/2303875888/TB2I5YRc8cHL1JjSZFBXXaiGXXa_!!2303875888.png");
        imagesList.add("https://img.alicdn.com/i4/2303875888/TB2I5YRc8cHL1JjSZFBXXaiGXXa_!!2303875888.png");
        imagesList.add("https://img.alicdn.com/i4/2303875888/TB2I5YRc8cHL1JjSZFBXXaiGXXa_!!2303875888.png");
        imagesList.add("https://img.alicdn.com/i4/2303875888/TB2I5YRc8cHL1JjSZFBXXaiGXXa_!!2303875888.png");
        imagesList.add("https://img.alicdn.com/i4/2303875888/TB2I5YRc8cHL1JjSZFBXXaiGXXa_!!2303875888.png");
        imagesList.add("https://img.alicdn.com/i4/2303875888/TB2I5YRc8cHL1JjSZFBXXaiGXXa_!!2303875888.png");
        imagesList.add("https://img.alicdn.com/i4/2303875888/TB2I5YRc8cHL1JjSZFBXXaiGXXa_!!2303875888.png");
        imagesList.add("https://img.alicdn.com/i4/2303875888/TB2I5YRc8cHL1JjSZFBXXaiGXXa_!!2303875888.png");
        imagesList.add("https://img.alicdn.com/i4/2303875888/TB2I5YRc8cHL1JjSZFBXXaiGXXa_!!2303875888.png");
        imagesList.add("https://img.alicdn.com/i4/2303875888/TB2I5YRc8cHL1JjSZFBXXaiGXXa_!!2303875888.png");
        imagesList.add("https://img.alicdn.com/i4/2303875888/TB2I5YRc8cHL1JjSZFBXXaiGXXa_!!2303875888.png");
        imagesList.add("https://img.alicdn.com/i4/2303875888/TB2I5YRc8cHL1JjSZFBXXaiGXXa_!!2303875888.png");
        imagesList.add("https://img.alicdn.com/i4/2303875888/TB2I5YRc8cHL1JjSZFBXXaiGXXa_!!2303875888.png");
        imagesList.add("https://img.alicdn.com/i4/2303875888/TB2I5YRc8cHL1JjSZFBXXaiGXXa_!!2303875888.png");
        imagesList.add("https://img.alicdn.com/i4/2303875888/TB2I5YRc8cHL1JjSZFBXXaiGXXa_!!2303875888.png");
        imagesList.add("https://img.alicdn.com/i4/2303875888/TB2I5YRc8cHL1JjSZFBXXaiGXXa_!!2303875888.png");
        imagesList.add("https://img.alicdn.com/i4/2303875888/TB2I5YRc8cHL1JjSZFBXXaiGXXa_!!2303875888.png");
        imagesList.add("https://img.alicdn.com/i4/2303875888/TB2I5YRc8cHL1JjSZFBXXaiGXXa_!!2303875888.png");
        imagesList.add("https://img.alicdn.com/i4/2303875888/TB2I5YRc8cHL1JjSZFBXXaiGXXa_!!2303875888.png");
        imagesList.add("https://img.alicdn.com/i4/2303875888/TB2I5YRc8cHL1JjSZFBXXaiGXXa_!!2303875888.png");
        imagesList.add("https://img.alicdn.com/i4/2303875888/TB2I5YRc8cHL1JjSZFBXXaiGXXa_!!2303875888.png");
        imagesList.add("https://cloud.video.taobao.com//play/u/393300180/p/1/e/6/t/1/215525269409.mp4");
        iv_material_down_load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                num = 0;
                showDownLoadDialog();
            }
        });
    }

    TextView tv_content, tv_down;
    Dialog dialog;

    private void showDownLoadDialog() {
        dialog = new Dialog(NewShopDetialActivity.this, R.style.activitydialog);
        dialog.setContentView(R.layout.show_material_down_load);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER | Gravity.CENTER);
        RelativeLayout cancel = (RelativeLayout) dialog.findViewById(R.id.cancel);
        tv_content = (TextView) dialog.findViewById(R.id.tv_content);
        tv_down = (TextView) dialog.findViewById(R.id.tv_down);
        tv_content.setText("总数:" + imagesList.size() + ",已下载:" + 0);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        tv_down.setOnClickListener(new View.OnClickListener() {/*下载*/
            @Override
            public void onClick(View v) {
                showDownProgress();
            }
        });
        dialog.show();
    }

    /*下载进度条显示*/
    private void showDownProgress() {
        if (NetUtil.getNetWorkState(NewShopDetialActivity.this) < 0) {
            ToastUtils.showToast(getApplicationContext(), "您的网络异常，请联网重试");
            return;
        }
        if (ContextCompat.checkSelfPermission(NewShopDetialActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(NewShopDetialActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //没有存储权限
            ActivityCompat.requestPermissions(NewShopDetialActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            savePicsToLocal();
        }
    }

    NumberAnimation na;

    private void savePicsToLocal() {
//        /*网络路劲存储*/
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                URL imageurl;
//                try {
//                    for (int i = 0; i < imagesList.size() - 1; i++) {
//                        imageurl = new URL(imagesList.get(i));
//                        HttpURLConnection conn = (HttpURLConnection) imageurl.openConnection();
//                        conn.setDoInput(true);
//                        conn.connect();
//                        InputStream is = conn.getInputStream();
//                        Bitmap bitmap = BitmapFactory.decodeStream(is);
//                        is.close();
//                        Message msg = new Message();
//                        // 把bm存入消息中,发送到主线程
//                        msg.obj = bitmap;
//                        handler.sendMessage(msg);
//                    }
//                } catch (MalformedURLException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();

        na = new NumberAnimation(tv_down);
        for (int i = 0; i < imagesList.size(); i++) {
            if (imagesList.get(i).contains(".mp4")) {
                fileUpLoad(imagesList.get(i), VideoSaveToPhone.saveVideoUrlToFile());
            } else {
                fileUpLoad(imagesList.get(i), VideoSaveToPhone.saveImageUrlToFile());
            }
        }
    }

    /*文件下载*/
    private void fileUpLoad(String fileUrl, String path) {
        MyApplication.getInstance().getMyOkHttp().download().tag(this)
                .url(fileUrl)
                .filePath(path)
                .enqueue(new DownloadResponseHandler() {

                    @Override
                    public void onStart(long totalBytes) {
                        super.onStart(totalBytes);
                        na.setNum(0, 99);
                    }

                    @Override
                    public void onCancel() {
                        super.onCancel();
                    }

                    @Override
                    public void onFinish(File downloadFile) {
                        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + downloadFile.getPath())));
                        num++;
                        tv_content.setText("总数:" + imagesList.size() + ",已下载:" + num);
                        if (num == imagesList.size()) {
                            dialog.dismiss();
                            ToastUtils.showToast(getApplicationContext(), "已下载至手机内存");
                        }
                    }

                    @Override
                    public void onProgress(long currentBytes, long totalBytes) {

                    }

                    @Override
                    public void onFailure(String error_msg) {
                        ToastUtils.showToast(getApplicationContext(), "下载失败");
                    }
                });
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            num++;
            tv_content.setText("总数:" + imagesList.size() + ",已下载:" + num);
            Bitmap bitmap = (Bitmap) msg.obj;
            CommonUtil.saveBitmap2file(bitmap, getApplicationContext());
        }
    };

    private void init() {
        barrageViews = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            barrageViews.add(new BarrageViewBean("小灰灰" + (i + 1), "16:1" + i % 10, "https://avatar.csdn.net/B/7/D/3_u011106915.jpg"));
        }
        barrageView.setData(barrageViews);
        barrageView.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        barrageView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        barrageView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        barrageView.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:/*下载权限回调*/
                if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    ToastUtils.showToast(getApplicationContext(), "需要打开存储权限，请前往设置-应用-果冻宝盒-权限进行设置");
                    return;
                } else if (grantResults.length <= 1 || grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                    ToastUtils.showToast(getApplicationContext(), "需要打开存储权限，请前往设置-应用-果冻宝盒-权限进行设置");
                    return;
                } else if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults.length > 1 && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    savePicsToLocal();
                }
                break;
        }
    }

}
