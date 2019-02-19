package com.guodongbaohe.app.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.guodongbaohe.app.OnItemClick;
import com.guodongbaohe.app.R;
import com.guodongbaohe.app.adapter.ChangeQRCodeAdapter;
import com.guodongbaohe.app.base_activity.BaseActivity;
import com.guodongbaohe.app.bean.QRCodePicsBean;
import com.guodongbaohe.app.itemdecoration.PinLeiItemDecoration;
import com.guodongbaohe.app.util.DensityUtils;
import com.guodongbaohe.app.util.DialogUtil;
import com.guodongbaohe.app.util.IconAndTextGroupUtil;
import com.guodongbaohe.app.util.NetPicsToBitmap;
import com.guodongbaohe.app.util.PreferUtils;
import com.guodongbaohe.app.util.QRCodeUtil;
import com.guodongbaohe.app.util.StringCleanZeroUtil;

import org.greenrobot.eventbus.EventBus;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChangeQRCodePicActivity extends BaseActivity {
    TextView tv_right_name;
    ImageView iv_back;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    /*主图*/
    @BindView(R.id.p_iv)
    ImageView p_iv;
    /*保存的view控件*/
    @BindView(R.id.ll_view)
    LinearLayout ll_view;
    /*标题*/
    @BindView(R.id.p_title)
    TextView p_title;
    /*券后价*/
    @BindView(R.id.p_coupon_price)
    TextView p_coupon_price;
    /*多少元券*/
    @BindView(R.id.tv_coupon)
    TextView tv_coupon;
    @BindView(R.id.p_two_price)
    TextView p_two_price;
    @BindView(R.id.iv_qr_code)
    ImageView iv_qr_code;
    @BindView(R.id.jia_type)
    TextView jia_type;
    ArrayList<String> pics;
    ChangeQRCodeAdapter changeQRCodeAdapter;
    int positionPics;
    private List<QRCodePicsBean> qrCodePicsBeanList;
    private int click_position = 0;
    private boolean isAdapterClick = false;
    String title, attr_price, attr_prime, attr_site, coupon_surplus, share_qrcode;
    String goods_id, member_id;
    double v;
    BigDecimal bg3;

    @Override
    public int getContainerView() {
        return R.layout.changeqrcodepicactivity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        member_id = PreferUtils.getString(getApplicationContext(), "member_id");
        goods_id = intent.getStringExtra("goods_id");
        title = intent.getStringExtra("title");
        attr_site = intent.getStringExtra("attr_site");
        attr_price = intent.getStringExtra("attr_price");
        attr_prime = intent.getStringExtra("attr_prime");
        pics = intent.getStringArrayListExtra("pics");
        coupon_surplus = intent.getStringExtra("coupon_surplus");
        positionPics = intent.getIntExtra("position", 0);
        share_qrcode = intent.getStringExtra("share_qrcode");
        v = Double.valueOf(attr_prime) - Double.valueOf(attr_price);
        setMiddleTitle("更换二维码主图");
        setRightTVVisible();
        setRightTitle("保存");
        tv_right_name = (TextView) findViewById(R.id.tv_right_name);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        recyclerview.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerview.setLayoutManager(manager);
        recyclerview.setNestedScrollingEnabled(false);
        recyclerview.addItemDecoration(new PinLeiItemDecoration(DensityUtils.dip2px(getApplicationContext(), 10), pics));
        initViewData();
        initView();
        initListener();
    }

    private void initViewData() {
        if (Double.valueOf(coupon_surplus) > 0) {
            jia_type.setText("券后价¥");
            double d_price = Double.valueOf(attr_prime) - Double.valueOf(attr_price);
            bg3 = new BigDecimal(d_price);
            double d_money = bg3.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            tv_coupon.setText(StringCleanZeroUtil.DoubleFormat(d_money) + "元券");
        } else {
            if (v > 0) {
                jia_type.setText("折后价¥");
                double disaccount = Double.valueOf(attr_price) / Double.valueOf(attr_prime) * 10;
                bg3 = new BigDecimal(disaccount);
                double d_zhe = bg3.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
                tv_coupon.setText(d_zhe + "折");
            } else {
                jia_type.setText("特惠价¥");
                tv_coupon.setText("立即抢购");
            }
        }
        IconAndTextGroupUtil.setTextView(getApplicationContext(), p_title, title, attr_site);
        StringCleanZeroUtil.StringFormat(attr_price, p_coupon_price);
        StringCleanZeroUtil.StringFormatWithYuan(attr_price, p_two_price);

        if (!TextUtils.isEmpty(share_qrcode)) {
            Bitmap mBitmap = QRCodeUtil.createQRCodeBitmap(share_qrcode, DensityUtils.dip2px(getApplicationContext(), 100));
            iv_qr_code.setImageBitmap(mBitmap);
        }
    }

    private void initListener() {
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBack();
            }
        });
        tv_right_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBack();
            }
        });
    }

    private void initView() {
        qrCodePicsBeanList = new ArrayList<>();
        for (int i = 0; i < pics.size(); i++) {
            QRCodePicsBean bean = new QRCodePicsBean();
            bean.setUrl(pics.get(i));
            qrCodePicsBeanList.add(bean);
        }
        changeQRCodeAdapter = new ChangeQRCodeAdapter(getApplicationContext(), qrCodePicsBeanList);
        recyclerview.setAdapter(changeQRCodeAdapter);
        recyclerview.scrollToPosition(positionPics);
        for (QRCodePicsBean bean : qrCodePicsBeanList) {
            bean.setIswhich(false);
        }
        qrCodePicsBeanList.get(positionPics).setIswhich(true);
        changeQRCodeAdapter.notifyDataSetChanged();
        Glide.with(getApplicationContext()).load(qrCodePicsBeanList.get(positionPics).getUrl()).asBitmap().into(target);
        click_position = positionPics;
        changeQRCodeAdapter.setonclicklistener(new OnItemClick() {
            @Override
            public void OnItemClickListener(View view, int position) {
                isAdapterClick = true;
                click_position = position;
                for (QRCodePicsBean bean : qrCodePicsBeanList) {
                    bean.setIswhich(false);
                }
                qrCodePicsBeanList.get(position).setIswhich(true);
                changeQRCodeAdapter.notifyDataSetChanged();
                Glide.with(getApplicationContext()).load(qrCodePicsBeanList.get(position).getUrl()).asBitmap().into(target);
            }
        });
    }

    private SimpleTarget target = new SimpleTarget<Bitmap>() {
        @Override
        public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
            p_iv.setImageBitmap(bitmap);
        }
    };

    /**
     * 任意view转bitmap
     */
    public Bitmap viewConversionBitmap(View v) {
        int w = v.getWidth();
        int h = v.getHeight();
        Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);
        c.drawColor(Color.WHITE); /** 如果不设置canvas画布为白色，则生成透明 */
        v.layout(0, 0, w, h);
        v.draw(c);
        return bmp;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            onBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    Dialog loadingDialog;

    private void onBack() {
        finish();
        if (isAdapterClick) {
            loadingDialog = DialogUtil.createLoadingDialog(ChangeQRCodePicActivity.this, "正在加载...");
            Bitmap bitmap = viewConversionBitmap(ll_view);
            String s = NetPicsToBitmap.convertIconToString(bitmap);
            PreferUtils.putString(getApplicationContext(), "bitmap_s", s);
            PreferUtils.putInt(getApplicationContext(), "click_position", click_position);
            EventBus.getDefault().post("changeBitmap_s");
            DialogUtil.closeDialog(loadingDialog);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (loadingDialog != null) {
            DialogUtil.closeDialog(loadingDialog);
        }
    }
}
