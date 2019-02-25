package com.guodongbaohe.app.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.guodongbaohe.app.R;
import com.guodongbaohe.app.base_activity.BaseActivity;
import com.guodongbaohe.app.popupwindow_util.FloatWindowService;
import com.guodongbaohe.app.receiver.IRequestPermissions;
import com.guodongbaohe.app.receiver.IRequestPermissionsResult;
import com.guodongbaohe.app.receiver.RequestPermissions;
import com.guodongbaohe.app.receiver.RequestPermissionsResultSetApp;
import com.guodongbaohe.app.util.DialogUtil;
import com.guodongbaohe.app.util.PermissionUtils;
import com.guodongbaohe.app.util.WebViewUtil;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class XinShouJiaoChengActivity extends BaseActivity {
    private static XinShouJiaoChengActivity instance;

    public static synchronized XinShouJiaoChengActivity getInstance() {
        return instance;
    }

    public static final String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Video";

    static SimpleDateFormat formatter;
    static Date curDate;
    public static final String videoName = ".mp4";
    /**
     * 读写权限  自己可以添加需要判断的权限
     */
    public static String[] permissionsREAD = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    Dialog dialog;
    IRequestPermissions requestPermissions = RequestPermissions.getInstance();//动态权限请求
    IRequestPermissionsResult requestPermissionsResult = RequestPermissionsResultSetApp.getInstance();//动态权限请求结果处理
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.webview)
    WebView webview;
    ImageView iv_back, iv_right;
    private View customView;
    private FrameLayout fullscreenContainer;
    private WebChromeClient.CustomViewCallback customViewCallback;
    protected static final FrameLayout.LayoutParams COVER_SCREEN_PARAMS = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

    @Override
    public int getContainerView() {
        return R.layout.baseh5activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_right = (ImageView) findViewById(R.id.iv_right);
        setRightIVVisible();
        iv_right.setImageResource(R.mipmap.webview_reload);
        instance = this;
        Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        WebSettings settings = webview.getSettings();
        webview.setVerticalScrollBarEnabled(false);
        settings.setJavaScriptEnabled(true);
        settings.setAllowFileAccess(true); // 允许访问文件
        settings.setSupportZoom(false);
        settings.setBuiltInZoomControls(true);
        settings.setLoadWithOverviewMode(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE); // 不加载缓存内容
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        //设置下4.4以上的webview，允许加载混合内容,https加载时候需加上这行代码
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url, WebViewUtil.getWebViewHead(getApplicationContext()));
                return true;
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                if (error.getPrimaryError() == SslError.SSL_DATE_INVALID
                        || error.getPrimaryError() == SslError.SSL_EXPIRED
                        || error.getPrimaryError() == SslError.SSL_INVALID
                        || error.getPrimaryError() == SslError.SSL_UNTRUSTED) {
                    handler.proceed();
                } else {
                    handler.cancel();
                }
                super.onReceivedSslError(view, handler, error);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

        });

        webview.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    progressBar.setVisibility(View.GONE);
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress(newProgress);
                }
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                setMiddleTitle(title);
                super.onReceivedTitle(view, title);
            }

            /*** 视频播放相关的方法 **/

            @Override
            public View getVideoLoadingProgressView() {
                FrameLayout frameLayout = new FrameLayout(XinShouJiaoChengActivity.this);
                frameLayout.setLayoutParams(new WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT));
                return frameLayout;
            }

            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
                showCustomView(view, callback);
            }

            @Override
            public void onHideCustomView() {
                hideCustomView();
            }
        });
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (customView != null) {
                    hideCustomView();
                } else if (customView == null && webview.canGoBack()) {
                    webview.goBack();
                } else {
                    finish();
                }
            }
        });
        webview.loadUrl(url, WebViewUtil.getWebViewHead(getApplicationContext()));
        webview.addJavascriptInterface(new DemoJavascriptInterface(), "daihao");
        initRightListener();
    }

    /*webview刷新*/
    private void initRightListener() {
        iv_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (webview != null) {
                    webview.reload();
                }
            }
        });
    }

    Dialog loadingDialog;

    public class DemoJavascriptInterface {

        @JavascriptInterface
        public void down(String url) {
            Log.i("fadfasdfads", url);
            if (!TextUtils.isEmpty(url)) {
                requestPermissions();
                if (lacksPermissions(XinShouJiaoChengActivity.this, permissionsREAD)) {//读写权限没开启
                    ActivityCompat.requestPermissions(XinShouJiaoChengActivity.this, permissionsREAD, 0);
                } else {

                    loadingDialog = DialogUtil.createLoadingDialog(XinShouJiaoChengActivity.this, "正在下载....");

                    loginOutData(url);

                    //读写权限已开启
                }
            }
//            payData();
        }
    }

    //请求权限
    private boolean requestPermissions() {
        //需要请求的权限
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};
        //开始请求权限
        return requestPermissions.requestPermissions(
                this,
                permissions,
                PermissionUtils.ResultCode1);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webview.canGoBack()) {
            webview.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        if (webview != null) {
            webview.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            webview.clearHistory();
            ((ViewGroup) webview.getParent()).removeView(webview);
            webview.destroy();
            webview = null;
        }
        super.onDestroy();

    }

    /**
     * 视频播放全屏
     **/
    private void showCustomView(View view, WebChromeClient.CustomViewCallback callback) {
        // if a view already exists then immediately terminate the new one
        if (customView != null) {
            callback.onCustomViewHidden();
            return;
        }

        XinShouJiaoChengActivity.this.getWindow().getDecorView();

        FrameLayout decor = (FrameLayout) getWindow().getDecorView();
        fullscreenContainer = new FullscreenHolder(XinShouJiaoChengActivity.this);
        fullscreenContainer.addView(view, COVER_SCREEN_PARAMS);
        decor.addView(fullscreenContainer, COVER_SCREEN_PARAMS);
        customView = view;
        setStatusBarVisibility(false);
        customViewCallback = callback;
    }

    /**
     * 隐藏视频全屏
     */
    private void hideCustomView() {
        if (customView == null) {
            return;
        }

        setStatusBarVisibility(true);
        FrameLayout decor = (FrameLayout) getWindow().getDecorView();
        decor.removeView(fullscreenContainer);
        fullscreenContainer = null;
        customView = null;
        customViewCallback.onCustomViewHidden();
        webview.setVisibility(View.VISIBLE);
    }

    /**
     * 全屏容器界面
     */
    static class FullscreenHolder extends FrameLayout {

        public FullscreenHolder(Context ctx) {
            super(ctx);
            setBackgroundColor(ctx.getResources().getColor(android.R.color.black));
        }

        @Override
        public boolean onTouchEvent(MotionEvent evt) {
            return true;
        }
    }

    private void setStatusBarVisibility(boolean visible) {
        int flag = visible ? 0 : WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setFlags(flag, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    //用户授权操作结果（可能授权了，也可能未授权）
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //用户给APP授权的结果
        //判断grantResults是否已全部授权，如果是，执行相应操作，如果否，提醒开启权限
        if (requestPermissionsResult.doRequestPermissionsResult(this, permissions, grantResults)) {
            //请求的权限全部授权成功，此处可以做自己想做的事了


        } else {
            //输出授权结果
            Toast.makeText(XinShouJiaoChengActivity.this, "授权失败，请手动开启权限！", Toast.LENGTH_LONG).show();
        }
    }

    private void loginOutData(final String url) {
        if (loadingDialog != null) {
            DialogUtil.closeDialog(loadingDialog);
        }
        dialog = new Dialog(XinShouJiaoChengActivity.this, R.style.transparentFrameWindowStyle);
        dialog.setContentView(R.layout.download_vedio);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER | Gravity.CENTER);
        TextView textView = (TextView) dialog.findViewById(R.id.tv);
        TextView sure = (TextView) dialog.findViewById(R.id.sure);
        if (textView == null || sure == null) return;
        textView.setText("下载完成，请去手机文件夹Video查看！");
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                checkAndDownLoadVideo(url);
            }
        });
        dialog.show();
    }

    /**
     * 判断权限集合
     * permissions 权限数组
     * return true-表示没有改权限  false-表示权限已开启
     */
    public static boolean lacksPermissions(Context mContexts, String[] permissionsREAD) {
        for (String permission : permissionsREAD) {
            if (lacksPermission(mContexts, permission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否缺少权限
     */
    private static boolean lacksPermission(Context mContexts, String permission) {
        return ContextCompat.checkSelfPermission(mContexts, permission) ==
                PackageManager.PERMISSION_DENIED;
    }


    /**
     * 检查并下载视频资源
     */
    private void checkAndDownLoadVideo(String url) {
        formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        curDate = new Date(System.currentTimeMillis());
        File file = new File(path);
        Log.e("TAG", "===>" + path);
        //文件夹不存在，则创建它
        if (!file.exists()) {
            file.mkdir();
        }
        FileDownloader.setup(this);
        FileDownloader.getImpl().create(url)
                .setPath(path + "/" + formatter.format(curDate) + videoName)
                .setForceReDownload(false)
                .setListener(new FileDownloadListener() {
                    @Override
                    protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        Log.e("TAG", "pending");
                    }

                    @Override
                    protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
                        Log.e("TAG", "connected");
                    }

                    @Override
                    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        Log.e("TAG", "progress");
                    }

                    @Override
                    protected void blockComplete(BaseDownloadTask task) {
                        Log.e("TAG", "blockComplete");
                    }

                    @Override
                    protected void retry(final BaseDownloadTask task, final Throwable ex, final int retryingTimes, final int soFarBytes) {

                        Log.e("TAG", "retry");
                    }

                    @Override
                    protected void completed(BaseDownloadTask task) {
                        Log.e("TAG", "completed");
                        Intent intent = new Intent(XinShouJiaoChengActivity.this, FloatWindowService.class);
                        startService(intent);
                    }

                    @Override
                    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        Log.e("TAG", "paused");
                    }

                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
                        Log.e("TAG", "error===>" + e.getMessage());
                    }

                    @Override
                    protected void warn(BaseDownloadTask task) {
                        Log.e("TAG", "warn");
                    }
                }).start();
    }
}
