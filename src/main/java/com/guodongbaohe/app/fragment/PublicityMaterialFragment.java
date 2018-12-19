package com.guodongbaohe.app.fragment;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.guodongbaohe.app.OnItemClick;
import com.guodongbaohe.app.R;
import com.guodongbaohe.app.activity.LoginAndRegisterActivity;
import com.guodongbaohe.app.adapter.PublicityMaterialAdapter;
import com.guodongbaohe.app.bean.ClipBean;
import com.guodongbaohe.app.bean.EverydayHostGoodsBean;
import com.guodongbaohe.app.common_constant.Constant;
import com.guodongbaohe.app.common_constant.MyApplication;
import com.guodongbaohe.app.dialogfragment.BaseNiceDialog;
import com.guodongbaohe.app.dialogfragment.NiceDialog;
import com.guodongbaohe.app.dialogfragment.ViewConvertListener;
import com.guodongbaohe.app.dialogfragment.ViewHolder;
import com.guodongbaohe.app.myokhttputils.response.JsonResponseHandler;
import com.guodongbaohe.app.share.OnekeyShare;
import com.guodongbaohe.app.util.GsonUtil;
import com.guodongbaohe.app.util.ParamUtil;
import com.guodongbaohe.app.util.PreferUtils;
import com.guodongbaohe.app.util.ShareManager;
import com.guodongbaohe.app.util.ToastUtils;
import com.guodongbaohe.app.util.VersionUtil;
import com.guodongbaohe.app.util.XRecyclerViewUtil;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;

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
                    ClipBean clipBean = new ClipBean();
                    ClipboardManager cm = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData mClipData = ClipData.newPlainText("Label", content);
                    cm.setPrimaryClip(mClipData);
                    clipBean.setTitle(content);
                    clipBean.save();
                    ToastUtils.showToast(getContext(), "复制成功");
                }
            });
            /*复制评论事件*/
            adapter.setonFuZhiClickListener(new OnItemClick() {
                @Override
                public void OnItemClickListener(View view, int position) {
                    String comment = list.get(position - 1).getComment();
                    ClipBean clipBean = new ClipBean();
                    ClipboardManager cm = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData mClipData = ClipData.newPlainText("Label", comment);
                    cm.setPrimaryClip(mClipData);
                    clipBean.setTitle(comment);
                    clipBean.save();
                    ToastUtils.showToast(getContext(), "复制评论成功");
                }
            });
        }
        return view;
    }

    private void getData() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("page", String.valueOf(pageNum));
        map.put("type", "2");
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
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 26999);
        } else {
            OnekeyShare oks = new OnekeyShare();
            oks.disableSSOWhenAuthorize();
            oks.setImageUrl(goods_gallery);
            oks.setSite(getString(R.string.app_name));
            oks.setCallback(new OneKeyShareCallback());
            oks.show(getContext());
        }
    }

    private class OneKeyShareCallback implements PlatformActionListener {
        @Override
        public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
            /*保存在后台*/
            saveData();
        }

        @Override
        public void onError(Platform platform, int i, Throwable throwable) {
        }

        @Override
        public void onCancel(Platform platform, int i) {
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

    private void morePicsShareDialog() {
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
                                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                                        || ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                    //没有存储权限
                                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2699);
                                    dialog.dismiss();
                                } else {
                                    dialog.dismiss();
                                    sharePics(0, "wchat");
                                }
                            }
                        });
                        re_wchat_circle.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                /*微信朋友圈分享*/
                                share_type = 1;
                                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                                        || ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                    //没有存储权限
                                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2699);
                                    dialog.dismiss();
                                } else {
                                    dialog.dismiss();
                                    sharePics(1, "wchat");
                                }
                            }
                        });
                        re_qq_friend.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                /*qq好友*/
                                share_type = 2;
                                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                                        || ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                    //没有存储权限
                                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2699);
                                    dialog.dismiss();
                                } else {
                                    dialog.dismiss();
                                    sharePics(0, "qq");
                                }
                            }
                        });
                        re_qq_space.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                /*qq空间*/
                                share_type = 3;
                                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                                        || ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                    //没有存储权限
                                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2699);
                                    dialog.dismiss();
                                } else {
                                    dialog.dismiss();
                                    sharePics(1, "qq_zone");
                                }
                            }
                        });
                    }
                })
                .setShowBottom(true)
                .setOutCancel(true)
                .setAnimStyle(R.style.EnterExitAnimation)
                .show(getFragmentManager());
    }

    Bitmap hebingBitmap;

    private void sharePics(int i, String type) {
        ShareManager shareManager = new ShareManager(getContext());
        shareManager.setShareImage(hebingBitmap, i, list_share_imgs, "", type, 100);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 2699:
                /*存储权限回调*/
                if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    ToastUtils.showToast(getContext(), "分享多张图片需要打开存储权限，请前往设置-应用-果冻宝盒-权限进行设置");
                    return;
                } else if (grantResults.length <= 1 || grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                    ToastUtils.showToast(getContext(), "分享多张图片需要打开存储权限，请前往设置-应用-果冻宝盒-权限进行设置");
                    return;
                } else if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults.length > 1 && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    switch (share_type) {
                        case 0:
                            sharePics(0, "wchat");
                            break;
                        case 1:
                            sharePics(1, "wchat");
                            break;
                        case 2:
                            sharePics(0, "qq");
                            break;
                        case 3:
                            sharePics(1, "qq_zone");
                            break;
                    }
                }
                break;
            case 26999:
                if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    ToastUtils.showToast(getContext(), "分享图片需要打开存储权限，请前往设置-应用-果冻宝盒-权限进行设置");
                    return;
                } else if (grantResults.length <= 1 || grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                    ToastUtils.showToast(getContext(), "分享图片需要打开存储权限，请前往设置-应用-果冻宝盒-权限进行设置");
                    return;
                } else if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults.length > 1 && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                }
                break;
        }
    }
}
