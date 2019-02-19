package com.guodongbaohe.app.view;

import android.annotation.SuppressLint;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Selection;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;

import com.guodongbaohe.app.util.ContainsEmojiEditText;
import com.guodongbaohe.app.util.ToastUtils;

@SuppressLint("AppCompatCustomView")
public class FivteenContainsEmjoyEditText extends EditText {
    //cursor position
    private final int INPUTMAX_LIMIT = 15;   //设置可输入的最大长度()
    //text before inputText;
    private String inputAfterText;
    //is reset text
    private boolean hasReset;

    private Context mContext;

    public FivteenContainsEmjoyEditText(Context context) {
        super(context);
        this.mContext = context;
        initEditText();
    }

    public FivteenContainsEmjoyEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initEditText();
    }

    public FivteenContainsEmjoyEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initEditText();
    }

    private static final int ID_PASTE = android.R.id.paste;

    @Override
    public boolean onTextContextMenuItem(int id) {
        if (id == ID_PASTE) {
            hasReset = true;
            ClipboardManager clip = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            inputAfterText = "" + clip.getText();
        }
        return super.onTextContextMenuItem(id);
    }

    private void initEditText() {
        InputFilter[] textFilters = new InputFilter[1];
        textFilters[0] = new InputFilter.LengthFilter(INPUTMAX_LIMIT) {
            @Override
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                if (source.length() > 0 && dest.length() == INPUTMAX_LIMIT) {
                    ToastUtils.showToast(mContext, "长度最多15位哦!");
                }
                return super.filter(source, start, end, dest, dstart, dend);
            }
        };
        setFilters(textFilters);
        addTextChangedListener(new TextWatcher() {
            private final int charMaxNum = INPUTMAX_LIMIT;
            int cursorPos;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int before, int count) {
                cursorPos = s.length();
                if (hasReset) {
                    hasReset = false;
                } else {
                    inputAfterText = s.toString();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (cursorPos < s.length()) {
                    if (start + before < start + count) {
                        CharSequence input = s.subSequence(start + before, start + count);
                        if (containsEmoji(input.toString())) {
                            ToastUtils.showToast(mContext, "不允许输入表情哦!");
                            setText(inputAfterText);
                            resetCursor(start + before);
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                int length = getText().length();
                if (lisen != null) {
                    lisen.update(length);
                }
            }
        });
    }

    private void resetCursor(int index) {
        CharSequence text = getText();
        if (text instanceof Spannable) {
            Spannable spanText = (Spannable) text;
            Selection.setSelection(spanText, index);
        }
    }

    public interface OnEmojiEditLisen {
        void update(int length);
    }

    private ContainsEmojiEditText.OnEmojiEditLisen lisen;

    public void setOnEmojiEditLisen(ContainsEmojiEditText.OnEmojiEditLisen lisen) {
        this.lisen = lisen;
    }

    /**
     * check 'emoji' isExit
     *
     * @param source
     * @return
     */
    public static boolean containsEmoji(String source) {
        int len = source.length();
        for (int i = 0; i < len; i++) {
            char codePoint = source.charAt(i);
            if (!isEmojiCharacter(codePoint)) {
                return true;
            }
        }
        return false;
    }

    /**
     * check is or not 'emoji'
     *
     * @param codePoint
     * @return
     */
    private static boolean isEmojiCharacter(char codePoint) {
        return (codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA) ||
                (codePoint == 0xD) || ((codePoint >= 0x20) && (codePoint <= 0xD7FF)) ||
                ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) || ((codePoint >= 0x10000)
                && (codePoint <= 0x10FFFF));
    }

}
