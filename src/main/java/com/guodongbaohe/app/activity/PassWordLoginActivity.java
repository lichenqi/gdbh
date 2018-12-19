package com.guodongbaohe.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;


import com.guodongbaohe.app.R;
import com.guodongbaohe.app.base_activity.BigBaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/*账号密码登录*/
public class PassWordLoginActivity extends BigBaseActivity {
    @BindView(R.id.register)
    TextView register;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.passwordloginactivity);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.register})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.register:/*注册按钮*/
                startActivity(new Intent(getApplicationContext(), InvitationCodeActivity.class));
                break;
        }
    }
}
