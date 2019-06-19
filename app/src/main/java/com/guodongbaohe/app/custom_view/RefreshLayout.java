package com.guodongbaohe.app.custom_view;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;

public class RefreshLayout extends RelativeLayout {
    /**
     * 滑动控件时拉去的速度比例
     */
    private final int V_REFRESH = 2;
    /**
     * 是否是刷新过程
     * true 是
     * false 不是
     * 为false的时候才可以进行刷新
     */
    private boolean mIsRefreshDuring;
    /**
     * 可以进下拉刷新
     */
    private boolean mCanDownPull;
    /**
     * 可以进行上拉刷新
     */
    private boolean mCanUpPull;
    /**
     * 判断触摸后是否是初次移动
     */
    private boolean mIsFirstMove;
    /**
     * y轴呢平移的距离
     */
    private int mDistanceY;
    /**
     * 刷新接口对象
     */
    private OnRefresh mOnRefresh;
    /**
     * 用于控制事件拦截的变量
     */
    private boolean mCanIntercept;
    private int mTouchSlop;
    private int mDistance;
    private LayoutParams mHeaderParams;
    private View mHeaderView;
    private View mFootView;
    private int mHeaderMaxHeight;
    private int mStartY;
    private LayoutParams mFootParams;
    private int mFootMaxHeight;
    private PullCallBack mCallBack;
    private View mChildView;
    private ObjectAnimator mAnimator;

    public RefreshLayout(Context context) {
        super( context );
        initData();
    }

    public RefreshLayout(Context context, AttributeSet attrs) {
        super( context, attrs );
        initData();
    }

    public RefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super( context, attrs, defStyleAttr );
        initData();
    }

    /**
     * 必须让头尾控件实现的接口
     */
    public interface HeadAndFootCallBack {
        //设置属性
        void setAttribute();

        //开始刷新
        void startPull();

        //停止刷新
        void stopPull();
    }

    /**
     * 必须让被拖动的控件子类实现
     */
    public interface PullCallBack {
        boolean canDownPull();

        boolean canUpPull();
    }

    private void initData() {
        //不调用该方法不能进行绘制
        setWillNotDraw( false );
    }

    /**
     * 下拉刷新完成后必须使用该方法
     */
    public void downPullFinish() {
        mAnimator.setFloatValues( mChildView.getTranslationY(), 0 );
        mAnimator.start();
        ((HeadAndFootCallBack) mHeaderView).stopPull();
    }

    /**
     * 上拉完成后必须调用该方法
     */
    public void upPullFinish() {
        mAnimator.setFloatValues( mChildView.getTranslationY(), 0 );
        mAnimator.start();
        ((HeadAndFootCallBack) mFootView).stopPull();
    }

    /**
     * 自动下拉刷新
     */
    public void autoDownPullForHead() {
        postDelayed( new Runnable() {
            @Override
            public void run() {
                mCanDownPull = true;
                mCanUpPull = false;
                mAnimator.setFloatValues( 10, mHeaderMaxHeight );
                mAnimator.start();
                ((HeadAndFootCallBack) mHeaderView).startPull();
                mOnRefresh.onDownPullRefresh();
            }
        }, 500 );
    }

    /**
     * 自动下拉刷新
     */
    public void autoUpPullForHead() {
        postDelayed( new Runnable() {
            @Override
            public void run() {
                mCanDownPull = false;
                mCanUpPull = true;
                mAnimator.setFloatValues( 0, mFootMaxHeight );
                mAnimator.start();
                ((HeadAndFootCallBack) mFootView).startPull();
                mOnRefresh.onUpPullRefresh();
            }
        }, 500 );
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mCanIntercept;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        Log.e( "shen", "mIsRefreshDuring=" + mIsRefreshDuring );
        if (mIsRefreshDuring)/*如果正在进行刷新将不会获取MotionEvent*/ {
            return super.dispatchTouchEvent( event );
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartY = (int) event.getY();
                initPull();
                break;
            case MotionEvent.ACTION_MOVE:
                if (event.getPointerCount() == 1) {
                    int moveY = (int) event.getY();
                    mDistanceY = (moveY - mStartY) / V_REFRESH;
                    if (!mIsFirstMove && mDistanceY != 0 && mDistanceY < mTouchSlop) {
                        mCanDownPull = mDistanceY > 0;
                        mCanUpPull = !mCanDownPull;
                        mIsFirstMove = true;
                    }
                    if (mCanDownPull && mCallBack.canDownPull()) {
                        upDataForDownPull();//下拉刷新
                        mChildView.setEnabled( false );
                        mCanIntercept = true;
                    }
                    if (mCanUpPull && mCallBack.canUpPull()) {
                        upDataForUpPull();//上拉加载
                        mChildView.setEnabled( false );
                        mCanIntercept = true;
                    }
                    mStartY = moveY;
                }
                break;
            case MotionEvent.ACTION_UP:
                mIsRefreshDuring = true;
                mIsFirstMove = false;
                if (mHeaderParams.height >= mHeaderMaxHeight)/*可以下拉刷新*/ {
                    ((HeadAndFootCallBack) mHeaderView).startPull();
                    mOnRefresh.onDownPullRefresh();
                } else if (mFootParams.height >= mFootMaxHeight)/*可以上拉刷新*/ {
                    ((HeadAndFootCallBack) mFootView).startPull();
                    mOnRefresh.onUpPullRefresh();
                } else if (mHeaderParams.height > 0 && mHeaderParams.height < mHeaderMaxHeight)/*不能进行下拉刷新，收回*/ {
                    releaseForDownFinished();
                } else if (mFootParams.height > 0 && mFootParams.height < mFootMaxHeight)/*不能进行下拉刷新，收回*/ {
                    releaseForUpFinished();
                } else {
                    mIsRefreshDuring = false;
                    mCanIntercept = false;
                }
                break;
        }
        super.dispatchTouchEvent( event );
        return true;
    }

    /**
     * 每次进行触摸都需要进行初始化
     */
    private void initPull() {
        mCanDownPull = false;
        mCanUpPull = false;
    }

    /**
     * 不需要进行上拉刷新
     */
    private void releaseForUpFinished() {
        mAnimator.setFloatValues( mChildView.getTranslationY(), 0 );
        mAnimator.start();
    }

    /**
     * 不需要进行下拉刷新
     */
    private void releaseForDownFinished() {
        mAnimator.setFloatValues( mChildView.getTranslationY(), 0 );
        mAnimator.start();
    }

    /**
     * 上拉时处理手势
     */
    private void upDataForUpPull() {
        if (mDistanceY != 0) {
            mFootParams.height -= mDistanceY;
            if (mFootParams.height <= 0) {
                mFootParams.height = 0;
            }
            if (mFootParams.height >= mFootMaxHeight) {
                mFootParams.height = mFootMaxHeight;
            }
            mChildView.setTranslationY( -mFootParams.height );
            mFootView.requestLayout();
        }
    }

    /**
     * 下拉时处理手势
     */
    private void upDataForDownPull() {
        if (mDistanceY != 0) {
            mHeaderParams.height += mDistanceY;
            if (mHeaderParams.height >= mHeaderMaxHeight) { //最大
                mHeaderParams.height = mHeaderMaxHeight;
            }
            if (mHeaderParams.height <= 0) { //最小
                mHeaderParams.height = 0;
            }
            mChildView.setTranslationY( mHeaderParams.height );
            mHeaderView.requestLayout();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        //加载头
        mHeaderView = getChildAt( 0 );
        if (!(mHeaderView instanceof HeadAndFootCallBack)) {
            new IllegalStateException( "HeaderView必须实现HeadAndFootCallBack接口" );
        }
        ((HeadAndFootCallBack) mHeaderView).setAttribute();
        mHeaderParams = (LayoutParams) mHeaderView.getLayoutParams();
        mHeaderParams.addRule( RelativeLayout.ALIGN_PARENT_TOP );

        //加载尾
        mFootView = getChildAt( 2 );
        if (!(mFootView instanceof HeadAndFootCallBack)) {
            new IllegalStateException( "FootView必须实现HeadAndFootCallBack接口" );
        }
        ((HeadAndFootCallBack) mFootView).setAttribute();
        mFootParams = (LayoutParams) mFootView.getLayoutParams();
        mFootParams.addRule( RelativeLayout.ALIGN_PARENT_BOTTOM );

        mChildView = getChildAt( 1 );
        if (!(mChildView instanceof HeadAndFootCallBack)) {
            new IllegalStateException( "ChildView必须实现PullCallBack接口" );
        }
        mCallBack = (PullCallBack) getChildAt( 1 );

        //设置动画
        mAnimator = ObjectAnimator.ofFloat( mChildView, "translationY", 0 );
        mAnimator.setInterpolator( new DecelerateInterpolator() );
        mAnimator.addUpdateListener( new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int translationY = (int) mChildView.getTranslationY();
                if (mCanUpPull) { //从移动到的位置往下滑
                    mFootParams.height = Math.abs( translationY );
                    mFootView.requestLayout();
                } else if (mCanDownPull) {
                    mHeaderParams.height = Math.abs( translationY );
                    mHeaderView.requestLayout();
                }
                Log.e( "shen", "translationY=" + translationY );
                Log.e( "shen", "mHeaderParams.height=" + mHeaderParams.height );
                if (translationY == 0) {
                    mChildView.setEnabled( true );
                    mDistanceY = 0; //重置
                    mIsRefreshDuring = false; //重置
                    mCanIntercept = false;
                } else {
                    mIsRefreshDuring = true;
                }
            }
        } );
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged( w, h, oldw, oldh );
        mTouchSlop = ViewConfiguration.get( getContext() ).getScaledTouchSlop();
        mDistance = mTouchSlop * 5;
        //设置下拉头初始属性
        mHeaderMaxHeight = mHeaderParams.height;
        mHeaderParams.height = 0;
        mHeaderView.requestLayout();
        //设置上拉尾初始属性
        mFootMaxHeight = mFootParams.height;
        mFootParams.height = 0;
        mFootView.requestLayout();
    }

    /**
     * 下拉/上拉事件监听
     */
    public interface OnRefresh {
        /**
         * 下拉刷新
         */
        void onDownPullRefresh();

        /**
         * 上拉加载
         */
        void onUpPullRefresh();
    }

    public void setOnRefresh(OnRefresh onRefresh) {
        mOnRefresh = onRefresh;
    }
}
