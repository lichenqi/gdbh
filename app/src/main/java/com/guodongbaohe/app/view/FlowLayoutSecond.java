package com.guodongbaohe.app.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.guodongbaohe.app.R;
import com.guodongbaohe.app.bean.HotBean;

import java.util.List;

public class FlowLayoutSecond extends ViewGroup {
    //自定义属性
    private int LINE_SPACE;
    private int ROW_SPACE;
    //放置标签的集合
    private List<HotBean.HotBeanData> lables;
    private List<String> lableSelects;

    public FlowLayoutSecond(Context context) {
        this( context, null );
    }

    public FlowLayoutSecond(Context context, AttributeSet attrs) {
        this( context, attrs, 0 );
    }

    public FlowLayoutSecond(Context context, AttributeSet attrs, int defStyleAttr) {
        super( context, attrs, defStyleAttr );
        //获取自定义属性
        TypedArray a = context.obtainStyledAttributes( attrs, R.styleable.FlowLayoutSecond );
        LINE_SPACE = a.getDimensionPixelSize( R.styleable.FlowLayoutSecond_lineSpaceSecond, 20 );
        ROW_SPACE = a.getDimensionPixelSize( R.styleable.FlowLayoutSecond_rowSpaceSecond, 20 );
        a.recycle();
    }

    /**
     * 添加margin 属性
     *
     * @param attrs
     * @return
     */
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams( getContext(), attrs );
    }

    /**
     * 添加标签
     *
     * @param lables 标签集合
     * @param isAdd  是否添加
     */
    TextView tv;
    int maxSize = 0;

    public void setLables(final List<HotBean.HotBeanData> lables, boolean isAdd) {
        this.lables = lables;
        removeAllViews();
        maxSize = lables.size();
        if (lables != null && lables.size() > 0) {
            for (int i = 0; i < maxSize; i++) {
                final String name = lables.get( i ).getWord();
                tv = new TextView( getContext() );
                tv.setLayoutParams( new ViewGroup.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT ) );
                tv.setText( name );
                tv.setTextSize( 14 );
                tv.setBackgroundResource( R.drawable.history_search_bg );
                tv.setTextColor( 0xff585858 );
                tv.setGravity( Gravity.CENTER );
                tv.setPadding( 30, 15, 30, 15 );
                tv.setMaxLines( 1 );
                tv.setEllipsize( TextUtils.TruncateAt.END );
                //点击事件
//                tv.setOnClickListener( new View.OnClickListener() {/*匿名内部类写法*/
////                    @Override
////                    public void onClick(View v) {
////                        onItem.OnItemClick( name );
////                    }
////                } );
                tv.setOnClickListener( new MyClickListener( name ) );
                //添加到容器中
                addView( tv );
            }
        }
    }

    /*外部监听实例化写法*/
    private class MyClickListener implements OnClickListener {

        private String name;

        public MyClickListener(String name) {
            this.name = name;
        }

        @Override
        public void onClick(View v) {
            onItem.OnItemClick( name );
        }
    }

    /**
     * 通过测量子控件高度，来设置自身控件的高度
     * 主要是计算
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //测量所有子view的宽高
        measureChildren( widthMeasureSpec, heightMeasureSpec );
        //获取view的宽高测量模式
        int widthMode = MeasureSpec.getMode( widthMeasureSpec );
        int widthSize = MeasureSpec.getSize( widthMeasureSpec );
        int heightMode = MeasureSpec.getMode( heightMeasureSpec );
        int heightSize = MeasureSpec.getSize( heightMeasureSpec );
        //这里的宽度建议使用match_parent或者具体值，当然当使用wrap_content的时候没有重写的话也是match_parent所以这里的宽度就直接使用测量的宽度
        int width = widthSize;
        int height;
        //判断宽度
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            int row = 1;
            int widthSpace = width; //宽度剩余空间
            for (int i = 0; i < getChildCount(); i++) {
                View view = getChildAt( i );
                //获取标签宽度
                int childW = view.getMeasuredWidth();
                //判断剩余宽度是否大于此标签宽度
                if (widthSpace >= childW) {
                    widthSpace -= childW;
                } else {
                    row++;
                    widthSpace = width - childW;
                }
                //减去两边间距
                widthSpace -= LINE_SPACE;
            }
            //测算最终所需要的高度  每行按100个单位计算
            height = (100 * row) + (row - 1) * ROW_SPACE;
        }
        //保存测量高度
        setMeasuredDimension( width, height );
    }

    /**
     * 摆放子view
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int row = 0;
        int right = 0;
        int bottom = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View chileView = getChildAt( i );
            int childW = chileView.getMeasuredWidth();
            int childH = chileView.getMeasuredHeight();
            right += childW;
            bottom = (childH + ROW_SPACE) * row + childH;
            if (right > (r - LINE_SPACE)) {
                row++;
                right = childW;
                bottom = (childH + ROW_SPACE) * row + childH;
            }
            chileView.layout( right - childW, bottom - childH, right, bottom );
            right += LINE_SPACE;
        }
    }

    public interface OnItem {
        void OnItemClick(String name);
    }

    private OnItem onItem;

    public void setOnClickListener(OnItem onItem) {
        this.onItem = onItem;
    }
}
