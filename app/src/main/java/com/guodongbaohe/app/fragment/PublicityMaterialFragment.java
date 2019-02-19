package com.guodongbaohe.app.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.guodongbaohe.app.OnItemClick;
import com.guodongbaohe.app.R;
import com.guodongbaohe.app.activity.LoginAndRegisterActivity;
import com.guodongbaohe.app.adapter.PublicityMaterialAdapter;
import com.guodongbaohe.app.bean.EverydayHostGoodsBean;
import com.guodongbaohe.app.common_constant.Constant;
import com.guodongbaohe.app.common_constant.MyApplication;
import com.guodongbaohe.app.dialogfragment.BaseNiceDialog;
import com.guodongbaohe.app.dialogfragment.NiceDialog;
import com.guodongbaohe.app.dialogfragment.ViewConvertListener;
import com.guodongbaohe.app.dialogfragment.ViewHolder;
import com.guodongbaohe.app.myokhttputils.response.JsonResponseHandler;
import com.guodongbaohe.app.util.ClipContentUtil;
import com.guodongbaohe.app.util.CommonUtil;
import com.guodongbaohe.app.util.GsonUtil;
import com.guodongbaohe.app.util.ParamUtil;
import com.guodongbaohe.app.util.PreferUtils;
import com.guodongbaohe.app.util.ShareManager;
import com.guodongbaohe.app.util.ToastUtils;
import com.guodongbaohe.app.util.Tools;
import com.guodongbaohe.app.util.VersionUtil;
import com.guodongbaohe.app.util.XRecyclerViewUtil;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

/*宣传素材*/
public class PublicityMaterialFragment extends Fragment {
    @BindView(R.id.xrecycler)
    XRecyclerView xrecycler;
    @BindView(R.id.to_top)
    ImageView to_top;
    private View view;
    private int pageNum = 1;
    PublicityMaterialAdapter adapter;
    List<EverydayHostGoodsBean.GoodsList> list = new ArrayList<>();
    String goods_gallery;
    private int which_position;
    private List<String> list_share_imgs;
    /*多图分享回调字段*/
    private final int MOREPHOTO = 100;
    /*单图分享对调字段*/
    private final int ONLYPHOTO = 200;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.everydayfaddishfragment, container, false);
            ButterKnife.bind(this, view);
            getData();
            xrecycler.setHasFixedSize(true);
            xrecycler.setLayoutManager(new LinearLayoutManager(getContext()));
            XRecyclerViewUtil.setView(xrecycler);
            adapter = new PublicityMaterialAdapter(getActivity(), getContext(), list);
            xrecycler.setAdapter(adapter);
            xrecycler.setLoadingListener(new XRecyclerView.LoadingListener() {
                @Override
                public void onRefresh() {
                    pageNum = 1;
                    getData();
                }

                @Override
                public void onLoadMore() {
                    pageNum++;
                    getData();
                }
            });
            /*分享事件*/
            adapter.setonShareListener(new OnItemClick() {
                @Override
                public void OnItemClickListener(View view, int position) {
                    if (PreferUtils.getBoolean(getContext(), "isLogin")) {
                        which_position = position - 1;
                        String content = list.get(position - 1).getContent();
                        ClipboardManager cm = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData mClipData = ClipData.newPlainText("Label", content);
                        cm.setPrimaryClip(mClipData);
                        ClipContentUtil.getInstance(getContext()).putNewSearch(content);//保存记录到数据库
                        goods_gallery = list.get(position - 1).getGoods_gallery();
                        if (goods_gallery.contains("||")) {
                            /*多张图片分享用原生实现*/
                            String[] imgs = goods_gallery.replace("||", ",").split(",");
                            list_share_imgs = new ArrayList<>();
                            for (int i = 0; i < imgs.length; i++) {
                                list_share_imgs.add(imgs[i]);
                            }
                            morePicsShareDialog();
                        } else {
                            /*单张图片用sharesdk分享*/
                            ToastUtils.showToast(getContext(), "文案内容已复制成功");
                            showShare();
                        }
                    } else {
                        startActivity(new Intent(getContext(), LoginAndRegisterActivity.class));
                    }
                }
            });
            /*滑动监听*/
            xrecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    int i = recyclerView.computeVerticalScrollOffset();
                    if (i > 1200) {
                        to_top.setVisibility(View.VISIBLE);
                    } else {
                        to_top.setVisibility(View.GONE);
                    }
                }
            });
            /*一键回到顶部按钮*/
            to_top.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    xrecycler.scrollToPosition(0);
                }
            });
            /*长按复制内容事件*/
            adapter.setonlongclicklistener(new OnItemClick() {
                @Override
                public void OnItemClickListener(View view, int position) {
                    String content = list.get(position - 1).getContent();
                    ClipboardManager cm = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData mClipData = ClipData.newPlainText("Label", content);
                    cm.setPrimaryClip(mClipData);
                    ClipContentUtil.getInstance(getContext()).putNewSearch(content);//保存记录到数据库
                    ToastUtils.showToast(getContext(), "复制成功");
                }
            });
            /*复制评论事件*/
            adapter.setonFuZhiClickListener(new OnItemClick() {
                @Override
                public void OnItemClickListener(View view, int position) {
                        String comment = list.get(position - 1).getComment();
                        ClipboardManager cm = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData mClipData = ClipData.newPlainText("Label", comment);
                        cm.setPrimaryClip(mClipData);
                        ClipContentUtil.getInstance(getContext()).putNewSearch(comment);//保存记录到数据库
                        ToastUtils.showToast(getContext(), "复制评论成功");
                }
            });
        }
        return view;
    }

    private void getData() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("page", String.valueOf(pageNum));
        map.put("type", "material");
        map.put("limit", "10");
        final String param = ParamUtil.getMapParam(map);
        MyApplication.getInstance().getMyOkHttp().post().url(Constant.BASE_URL + Constant.EVERYDAYHOSTGOODS + "?" + param)
                .tag(this)
                .addHeader("x-appid", Constant.APPID)
                .addHeader("x-devid", PreferUtils.getString(getContext(), Constant.PESUDOUNIQUEID))
                .addHeader("x-nettype", PreferUtils.getString(getContext(), Constant.NETWORKTYPE))
                .addHeader("x-agent", VersionUtil.getVersionCode(getContext()))
                .addHeader("x-platform", Constant.ANDROID)
                .addHeader("x-devtype", Constant.IMEI)
                .addHeader("x-token", ParamUtil.GroupMap(getContext(), ""))
                .enqueue(new JsonResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, JSONObject response) {
                        super.onSuccess(statusCode, response);
                        Log.i("宣传素材", response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            if (jsonObject.getInt("status") >= 0) {
                                EverydayHostGoodsBean bean = GsonUtil.GsonToBean(response.toString(), EverydayHostGoodsBean.class);
                                if (bean != null) {
                                    List<EverydayHostGoodsBean.GoodsList> result = bean.getResult();
                                    if (pageNum == 1) {
                                        list.clear();
                                        list.addAll(result);
                                        adapter.notifyDataSetChanged();
                                        xrecycler.refreshComplete();
                                    } else {
                                        list.addAll(result);
                                        adapter.notifyDataSetChanged();
                                        xrecycler.loadMoreComplete();
                                    }
                                }
                            } else {
                                xrecycler.refreshComplete();
                                xrecycler.loadMoreComplete();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, String error_msg) {
                        xrecycler.refreshComplete();
                        xrecycler.loadMoreComplete();
                        ToastUtils.showToast(getContext(), Constant.NONET);
                    }
                });
    }

    private void showShare() {
        /*先开存储权限*/
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //没有存储权限
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, ONLYPHOTO);
        } else {
            /*自定义九宫格样式*/
            customShareStyle();
        }
    }

    private void saveData() {
        HashMap<String, String> map = new HashMap<>();
        map.put("gid", list.get(which_position).getId());
        String param = ParamUtil.getMapParam(map);
        MyApplication.getInstance().getMyOkHttp().post()
                .url(Constant.BASE_URL + Constant.SHARE_NUM + "?" + param)
                .tag(this)
                .addHeader("x-appid", Constant.APPID)
                .addHeader("x-devid", PreferUtils.getString(getContext(), Constant.PESUDOUNIQUEID))
                .addHeader("x-nettype", PreferUtils.getString(getContext(), Constant.NETWORKTYPE))
                .addHeader("x-agent", VersionUtil.getVersionCode(getContext()))
                .addHeader("x-platform", Constant.ANDROID)
                .addHeader("x-devtype", Constant.IMEI)
                .addHeader("x-token", ParamUtil.GroupMap(getContext(), ""))
                .enqueue(new JsonResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, JSONObject response) {
                        super.onSuccess(statusCode, response);
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            int status = jsonObject.getInt("status");
                            if (status >= 0) {
                                xrecycler.refresh();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, String error_msg) {
                    }
                });
    }

    /*分享类型*/
    private int share_type = 0;

    /*多张图片分享方法*/
    private void morePicsShareDialog() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //没有存储权限
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, MOREPHOTO);
        } else {
            morePhotoShareDialog();
        }
    }

    /*多张图片分享弹框*/
    private void morePhotoShareDialog() {
        NiceDialog.init().setLayoutId(R.layout.morepicssharedialog)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    public void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                        holder.setOnClickListener(R.id.tv_cancel, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        RelativeLayout re_wchat_friend = holder.getView(R.id.re_wchat_friend);
                        RelativeLayout re_wchat_circle = holder.getView(R.id.re_wchat_circle);
                        RelativeLayout re_qq_friend = holder.getView(R.id.re_qq_friend);
                        RelativeLayout re_qq_space = holder.getView(R.id.re_qq_space);
                        re_wchat_friend.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                /*微信好友分享*/
                                share_type = 0;
                                dialog.dismiss();
                                sharePics(0, "wchat");
                            }
                        });
                        re_wchat_circle.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                /*微信朋友圈分享*/
                                dialog.dismiss();
                                /*由于微信机制不让分享多图直接到微信朋友圈*/
                                WChatCircleDialog();
                                /*保存多张图片到朋友圈*/
                                saveMorePhotoToLocal();
                            }
                        });
                        re_qq_friend.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                /*qq好友*/
                                share_type = 2;
                                dialog.dismiss();
                                sharePics(0, "qq");
                            }
                        });
                        re_qq_space.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                /*多张保存图片*/
                                dialog.dismiss();
                                saveMorePhotoToLocal();
                            }
                        });
                    }
                })
                .setShowBottom(true)
                .setOutCancel(true)
                .setAnimStyle(R.style.EnterExitAnimation)
                .show(getFragmentManager());
    }

    /*保存多张图片到朋友圈*/
    private void saveMorePhotoToLocal() {
        /*网络路劲存储*/
        new Thread(new Runnable() {
            @Override
            public void run() {
                URL imageurl;
                try {
                    for (int i = 0; i < list_share_imgs.size(); i++) {
                        imageurl = new URL(list_share_imgs.get(i));
                        HttpURLConnection conn = (HttpURLConnection) imageurl.openConnection();
                        conn.setDoInput(true);
                        conn.connect();
                        InputStream is = conn.getInputStream();
                        Bitmap bitmap = BitmapFactory.decodeStream(is);
                        is.close();
                        Message msg = new Message();
                        // 把bm存入消息中,发送到主线程
                        msg.obj = bitmap;
                        handler.sendMessage(msg);
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    Bitmap hebingBitmap;

    private void sharePics(int i, String type) {
        ShareManager shareManager = new ShareManager(getContext());
        shareManager.setShareImage(hebingBitmap, i, list_share_imgs, "", type, 100);
    }

    private void customShareStyle() {
        NiceDialog.init().setLayoutId(R.layout.custom_share_style)
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
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        URL imageurl = null;
                                        try {
                                            imageurl = new URL(goods_gallery);
                                        } catch (MalformedURLException e) {
                                            e.printStackTrace();
                                        }
                                        try {
                                            HttpURLConnection conn = (HttpURLConnection) imageurl.openConnection();
                                            conn.setDoInput(true);
                                            conn.connect();
                                            InputStream is = conn.getInputStream();
                                            Bitmap bitmap = BitmapFactory.decodeStream(is);
                                            is.close();
                                            Message msg = new Message();
                                            // 把bm存入消息中,发送到主线程
                                            msg.obj = bitmap;
                                            handler.sendMessage(msg);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }).start();
                            }
                        });
                    }
                })
                .setShowBottom(true)
                .setOutCancel(true)
                .setAnimStyle(R.style.EnterExitAnimation)
                .show(getFragmentManager());
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            Bitmap bitmap = (Bitmap) msg.obj;
            CommonUtil.saveBitmap2file(bitmap, getContext());
        }
    };

    /*微信好友分享*/
    private void wchatFriendShare() {
        Wechat.ShareParams sp = new Wechat.ShareParams();
        sp.setImageUrl(goods_gallery);
        sp.setShareType(Platform.SHARE_IMAGE);
        Platform wechat = ShareSDK.getPlatform(Wechat.NAME);
        wechat.setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                saveData();
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

    /*微信朋友圈分享*/
    private void wchatFriendCircle() {
        WechatMoments.ShareParams sp = new WechatMoments.ShareParams();
        sp.setImageUrl(goods_gallery);
        sp.setShareType(Platform.SHARE_IMAGE);
        Platform weChat = ShareSDK.getPlatform(WechatMoments.NAME);
        weChat.setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                saveData();
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
        sp.setImageUrl(goods_gallery);
        sp.setShareType(Platform.SHARE_IMAGE);
        Platform qq = ShareSDK.getPlatform(QQ.NAME);
        qq.setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                saveData();
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


    Dialog dialog;

    /*多图分享到微信时弹框到微信app*/
    private void WChatCircleDialog() {
        dialog = new Dialog(getContext(), R.style.transparentFrameWindowStyle);
        dialog.setContentView(R.layout.wchatcircle_dialog_pics);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER | Gravity.CENTER);
        TextView dismiss = (TextView) dialog.findViewById(R.id.dismiss);
        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        TextView open_wx = (TextView) dialog.findViewById(R.id.open_wx);
        open_wx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Tools.isAppAvilible(getContext(), "com.tencent.mm")) {
                    ToastUtils.showToast(getContext(), "您还没有安装微信客户端,请先安转客户端");
                    return;
                }
                Intent intent = new Intent(Intent.ACTION_MAIN);
                ComponentName cmp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI");
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setComponent(cmp);
                startActivity(intent);
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    /*权限回调*/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MOREPHOTO:
                /*多图分享回调*/
                if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    ToastUtils.showToast(getContext(), "分享多张图片需要打开存储权限，请前往设置-应用-果冻宝盒-权限进行设置");
                    return;
                } else if (grantResults.length <= 1 || grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                    ToastUtils.showToast(getContext(), "分享多张图片需要打开存储权限，请前往设置-应用-果冻宝盒-权限进行设置");
                    return;
                } else if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults.length > 1 && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    /*多图分享弹框显示*/
                    morePhotoShareDialog();
                }
                break;
            case ONLYPHOTO:
                /*单图回调*/
                if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    ToastUtils.showToast(getContext(), "分享图片需要打开存储权限，请前往设置-应用-果冻宝盒-权限进行设置");
                    return;
                } else if (grantResults.length <= 1 || grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                    ToastUtils.showToast(getContext(), "分享图片需要打开存储权限，请前往设置-应用-果冻宝盒-权限进行设置");
                    return;
                } else if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults.length > 1 && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    /*自定义九宫格样式*/
                    customShareStyle();
                }
                break;
        }
    }

}
