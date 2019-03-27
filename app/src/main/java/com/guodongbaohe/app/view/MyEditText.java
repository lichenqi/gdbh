package com.guodongbaohe.app.view;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

import com.guodongbaohe.app.util.ClipContentUtil;

import static android.content.Context.CLIPBOARD_SERVICE;

@SuppressLint("AppCompatCustomView")
public class MyEditText extends EditText {

    public MyEditText(Context context) {
        super(context);
    }

    public MyEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTextContextMenuItem(int id) {
        if (id == android.R.id.copy) {/*复制操作*/
            ClipboardManager cm = (ClipboardManager) getContext().getSystemService(CLIPBOARD_SERVICE);
            if (cm.hasPrimaryClip()) {
                ClipData data = cm.getPrimaryClip();
                ClipData.Item item = data.getItemAt(0);
                final String content = item.coerceToText(getContext()).toString().trim().replace("\r\n\r\n", "\r\n");
                ClipContentUtil.getInstance(getContext()).putNewSearch(content);//保存记录到数据库
            }
        }
        return super.onTextContextMenuItem(id);
    }

}
