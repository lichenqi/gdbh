package com.guodongbaohe.app.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.guodongbaohe.app.R;
import com.guodongbaohe.app.base_activity.BaseActivity;
import com.guodongbaohe.app.dialogfragment.BaseNiceDialog;
import com.guodongbaohe.app.dialogfragment.NiceDialog;
import com.guodongbaohe.app.dialogfragment.ViewConvertListener;
import com.guodongbaohe.app.dialogfragment.ViewHolder;
import com.guodongbaohe.app.util.WebViewUtil;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

public class SaveMoneyActivity extends BaseActivity {
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.webview)
    WebView webview;
    ImageView iv_back, iv_right;
    private String share_url, share_title, share_content, share_img;

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
        WebSettings settings = webview.getSettings();
        webview.setVerticalScrollBarEnabled(false);
        settings.setJavaScriptEnabled(true);
        settings.setSupportZoom(false);
        settings.setBuiltInZoomControls(true);
        webview.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                webview.loadUrl(url, WebViewUtil.getWebViewHead(getApplicationContext()));
                return true;
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
        });
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (webview.canGoBack()) {
                    webview.goBack();
                } else {
                    finish();
                }
            }
        });
        webview.loadUrl("http://x.mopland.com/help/save", WebViewUtil.getWebViewHead(getApplicationContext()));
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

    public class DemoJavascriptInterface {

        @JavascriptInterface
        public void invitation(String url, String title, String content, String img) {
            share_url = url;
            share_title = title;
            share_content = content;
            share_img = img;
            /*自定义九宫格样式*/
            customShareStyle();
        }
    }

    private void customShareStyle() {
        NiceDialog.init().setLayoutId(R.layout.save_money_dialog)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    public void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                        holder.setOnClickListener(R.id.tv_cancel, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        LinearLayout wcaht_friend = holder.getView(R.id.wcaht_friend);
                        LinearLayout wchat_circle = holder.getView(R.id.wchat_circle);
                        LinearLayout qq_friend = holder.getView(R.id.qq_friend);
                        LinearLayout save_img = holder.getView(R.id.save_img);
                        wcaht_friend.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                wchatFriendShare();
                            }
                        });
                        wchat_circle.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                wchatFriendCircle();
                            }
                        });
                        qq_friend.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                qqFriend();
                            }
                        });
                        save_img.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                qqZone();
                            }
                        });
                    }
                })
                .setShowBottom(true)
                .setOutCancel(true)
                .setAnimStyle(R.style.EnterExitAnimation)
                .show(getSupportFragmentManager());
    }

    /*微信好友*/
    private void wchatFriendShare() {
        Wechat.ShareParams sp = new Wechat.ShareParams();
        sp.setTitle(share_title);
        sp.setText(share_content);
        sp.setImageUrl(share_img);
        sp.setUrl(share_url);
        sp.setShareType(Platform.SHARE_WEBPAGE);
        Platform wechat = ShareSDK.getPlatform(Wechat.NAME);
        wechat.setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
            }

            @Override
            public void onCancel(Platform platform, int i) {
            }
        });
        wechat.share(sp);
    }

    /*微信朋友圈*/
    private void wchatFriendCircle() {
        WechatMoments.ShareParams sp = new WechatMoments.ShareParams();
        sp.setTitle(share_title);
        sp.setText(share_content);
        sp.setImageUrl(share_img);
        sp.setUrl(share_url);
        sp.setShareType(Platform.SHARE_WEBPAGE);
        Platform weChat = ShareSDK.getPlatform(WechatMoments.NAME);
        weChat.setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
            }

            @Override
            public void onCancel(Platform platform, int i) {
            }
        });
        weChat.share(sp);
    }

    /*qq好友*/
    private void qqFriend() {
        QQ.ShareParams sp = new QQ.ShareParams();
        sp.setTitle(share_title);
        sp.setText(share_content);
        sp.setImageUrl(share_img);
        sp.setTitleUrl(share_url);
        sp.setShareType(Platform.SHARE_WEBPAGE);
        Platform qq = ShareSDK.getPlatform(QQ.NAME);
        if (!qq.isClientValid()) {
            //客户端不可用，
            return;
        }
        qq.setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
            }

            @Override
            public void onCancel(Platform platform, int i) {
            }
        });
        qq.share(sp);
    }

    /*qq空间*/
    private void qqZone() {
        QZone.ShareParams sp = new QZone.ShareParams();
        sp.setTitle(share_title);
        sp.setText(share_content);
        sp.setImageUrl(share_img);
        sp.setTitleUrl(share_url);
        Platform qq = ShareSDK.getPlatform(QZone.NAME);
        if (!qq.isClientValid()) {
            return;
        }
        qq.setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
            }

            @Override
            public void onCancel(Platform platform, int i) {
            }
        });
        qq.share(sp);
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
}
