package com.guodongbaohe.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.guodongbaohe.app.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TouMingSearchDialogActivity extends Activity {
    @BindView(R.id.re_parent)
    RelativeLayout re_parent;
    @BindView(R.id.content)
    TextView content;
    @BindView(R.id.cancel)
    TextView cancel;
    @BindView(R.id.sure)
    TextView sure;
    String key_word;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.toumingsearchdialogactivity);
        ButterKnife.bind(this);
        key_word = getIntent().getStringExtra("key_word");
        re_parent.getBackground().setAlpha(200);
        content.setText(key_word);
    }

    @OnClick({R.id.cancel, R.id.sure})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.cancel:
                finish();
                break;
            case R.id.sure:
                Intent intent = new Intent(getApplicationContext(), SearchResultActivity.class);
                intent.putExtra("keyword", key_word);
                startActivity(intent);
                finish();
                break;
        }
    }
}
