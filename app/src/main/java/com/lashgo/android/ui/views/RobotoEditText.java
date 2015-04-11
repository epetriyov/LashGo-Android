package com.lashgo.android.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.EditText;
import com.lashgo.android.R;
import com.lashgo.android.utils.FontUtils;

/**
 * Created by Eugene on 28.07.2014.
 */
public class RobotoEditText extends EditText {
    public RobotoEditText(Context context) {
        super(context);
        setFont(null);
    }

    public RobotoEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFont(attrs);
    }

    public RobotoEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setFont(attrs);
    }

    private void setFont(AttributeSet attrs) {
        if (!isInEditMode() && attrs != null) {
            TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.RobotoEditText, 0, 0);
            String fontName = typedArray.getString(R.styleable.RobotoEditText_fontName);
            if (!TextUtils.isEmpty(fontName)) {
                setTypeface(FontUtils.getRobotoTypeface(getContext(), fontName));
            }
        }
    }
}
