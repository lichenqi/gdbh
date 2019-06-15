package com.guodongbaohe.app.dagger2_test;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.TextView;

import com.guodongbaohe.app.R;
import com.guodongbaohe.app.base_activity.BaseActivity;

import java.lang.ref.WeakReference;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/*handler导致的内存泄漏*/
public class DaggerActvity extends BaseActivity {

    @Inject
    Student student;
    MyHandler myHandler;
    @BindView(R.id.tv_content)
    TextView tvContent;


    @Override
    public int getContainerView() {
        return R.layout.dagger_activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        ButterKnife.bind( this );
        setMiddleTitle( "我是dagger" );
        DaggerMainComponent.builder().build().inject( this );
        tvContent.setText( student.showMessage() );
        myHandler = new MyHandler( DaggerActvity.this );
        Message message = Message.obtain();
        message.obj = "我是李晨奇";
        myHandler.sendMessageDelayed( message, 2000 );
    }


    private static final class MyHandler extends Handler {

        private WeakReference<DaggerActvity> mActivity;

        public MyHandler(DaggerActvity mainActivity) {
            mActivity = new WeakReference<>( mainActivity );
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage( msg );
            DaggerActvity daggerActvity = mActivity.get();
            if (null != daggerActvity) {
                Log.i( "打印handler消息", msg.obj.toString() );
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myHandler.removeCallbacksAndMessages( null );
    }
}
