package com.guodongbaohe.app.view;

import android.annotation.SuppressLint;
import android.content.ClipboardManager;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.EditText;

import com.guodongbaohe.app.util.ClipContentUtil;

@SuppressLint("AppCompatCustomView")
public class MyEditText extends EditText {
    public MyEditText(Context context) {
        super(context);
    }

    public MyEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTextContextMenuItem(int id) {
        if (id == 16908321) {/*复制操作*/
            ClipboardManager clip = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            //改变剪贴板中Content
            CharSequence text = clip.getPrimaryClip().getItemAt(0).getText();
            ClipContentUtil.getInstance(getContext()).putNewSearch(text.toString().trim());//保存记录到数据库
            Log.i("打印剪切板操作", text.toString());
        }
        return super.onTextContextMenuItem(id);
    }

}
