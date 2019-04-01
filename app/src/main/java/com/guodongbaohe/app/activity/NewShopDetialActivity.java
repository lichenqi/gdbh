package com.guodongbaohe.app.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.guodongbaohe.app.R;
import com.guodongbaohe.app.bean.BarrageViewBean;
import com.guodongbaohe.app.view.BarrageView;
import com.guodongbaohe.app.view.HeartView;

import java.util.ArrayList;
import java.util.List;

public class NewShopDetialActivity extends AppCompatActivity {
    HeartView main_heartview;
    private BarrageView barrageView;
    private List<BarrageViewBean> barrageViews;
    private ImageView yincang,dianzan;
    private LinearLayout sucai;
    private int isfirst=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newshopdetial);
        main_heartview= (HeartView) findViewById(R.id.main_heartview);
        barrageView = (BarrageView) findViewById(R.id.barrageview);
        yincang=(ImageView)findViewById(R.id.yincang);
        dianzan=(ImageView)findViewById(R.id.dianzan);
        sucai=(LinearLayout)findViewById(R.id.sucai);
        yincang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isfirst==0){
                    sucai.setVisibility(View.GONE);
                    barrageView.setVisibility(View.GONE);
                    isfirst=1;
                }else if (isfirst==1){
                    sucai.setVisibility(View.VISIBLE);
                    barrageView.setVisibility(View.VISIBLE);
                    isfirst=0;
                }
            }
        });
        dianzan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i=0;i<6;i++){
                    main_heartview.addHeart();
                }
            }
        });
        init();
    }

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
}
