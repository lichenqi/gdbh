package com.guodongbaohe.app.base_activity;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.guodongbaohe.app.R;
import com.guodongbaohe.app.activity.SearchResultActivity;
import com.guodongbaohe.app.util.ClipContentUtil;
import com.guodongbaohe.app.util.HistorySearchUtil;

import java.util.List;

/*最原始基本类*/
public class OriginalActivity extends AppCompatActivity {
    private String bracket_one = "{";
    private String bracket_two = "}";
    Dialog dialog;
    ClipboardManager cm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*获取剪切板内容*/
        getClipContent();
    }

    private void getClipContent() {
        cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        if (cm.hasPrimaryClip()) {
            ClipData data = cm.getPrimaryClip();
            if (data == null) return;
            ClipData.Item item = data.getItemAt(0);
            final String content = item.coerceToText(getApplicationContext()).toString().trim().replace("\r\n\r\n", "\r\n");
            if (TextUtils.isEmpty(content)) return;
            if (content.contains(bracket_one) && content.contains(bracket_two)) return;
            /*数据库数据*/
            List<String> clip_list = ClipContentUtil.getInstance(getApplicationContext()).queryHistorySearchList();
            if (clip_list == null) return;
            for (int i = 0; i < clip_list.size(); i++) {
                if (clip_list.get(i).toString().trim().replace("\r\n\r\n", "\r\n").equals(content)) {
                    return;
                }
            }
            showDialog(content);
        }
    }

    private void showDialog(final String content) {
        /*清空剪切板内容*/
        if (cm.hasPrimaryClip()) {
            cm.setPrimaryClip(ClipData.newPlainText(null, ""));
        }
        guoDuTanKuang(content);
    }

    /*过渡弹框*/
    private void guoDuTanKuang(final String content) {
        if (dialog != null) {
            dialog.dismiss();
        }
        dialog = new Dialog(OriginalActivity.this, R.style.transparentFrameWindowStyle);
        dialog.setContentView(R.layout.clip_search_dialog);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER | Gravity.CENTER);
        TextView sure = (TextView) dialog.findViewById(R.id.sure);
        TextView cancel = (TextView) dialog.findViewById(R.id.cancel);
        TextView title = (TextView) dialog.findViewById(R.id.content);
        title.setText(content);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                HistorySearchUtil.getInstance(OriginalActivity.this).putNewSearch(content);//保存记录到数据库
                Intent intent = new Intent(getApplicationContext(), SearchResultActivity.class);
                intent.putExtra("keyword", content);
                intent.putExtra("search_type", 0);
                startActivity(intent);
            }
        });
        dialog.show();
    }

    //判断是否是升级码
    private boolean isUpdataCode(String msg) {
        boolean isture = false;
        if (msg.matches("^(?![^a-zA-Z0-9]+$)(?!\\\\D+$).{16}$")) {
            isture = true;
        }
        return isture;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.fontScale != 1) {
            //非默认值
            getResources();
        }
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        if (res.getConfiguration().fontScale != 1) {//非默认值
            Configuration newConfig = new Configuration();
            newConfig.setToDefaults();//设置默认
            res.updateConfiguration(newConfig, res.getDisplayMetrics());
        }
        return res;
    }

}
