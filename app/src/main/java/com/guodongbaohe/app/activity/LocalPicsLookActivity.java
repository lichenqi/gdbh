package com.guodongbaohe.app.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.guodongbaohe.app.R;
import com.guodongbaohe.app.base_activity.BigBaseActivity;
import com.guodongbaohe.app.bean.InviteAwardBean;
import com.guodongbaohe.app.util.PreferUtils;
import com.guodongbaohe.app.util.QRCodeUtil;

import java.util.List;


public class LocalPicsLookActivity extends BigBaseActivity {

    List<InviteAwardBean.InviteAwardData> imgList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.localpicslookactivity);
        String invite_code = PreferUtils.getString(getApplicationContext(), "invite_code");
        String share_friends_title = PreferUtils.getString(getApplicationContext(), "share_friends_title");
        int position = getIntent().getIntExtra("position", 0);
        imgList = (List<InviteAwardBean.InviteAwardData>) getIntent().getSerializableExtra("imgList");
        ImageView iv = (ImageView) findViewById(R.id.iv);
        TextView tv_invite_code = (TextView) findViewById(R.id.tv_invite_code);
        ImageView iv_qrcode = (ImageView) findViewById(R.id.iv_qrcode);
        tv_invite_code.setText("邀请码: " + invite_code);
        if (position == 0) {
            tv_invite_code.setTextColor(0xff000000);
        } else {
            tv_invite_code.setTextColor(0xffffffff);
        }
        Bitmap mBitmap = QRCodeUtil.createQRCodeBitmap(share_friends_title, 650);
        iv_qrcode.setImageBitmap(mBitmap);
        Glide.with(getApplicationContext()).load(imgList.get(position).getImage()).into(iv);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.ap2, R.anim.ap1);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            finish();
            overridePendingTransition(R.anim.ap2, R.anim.ap1);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
