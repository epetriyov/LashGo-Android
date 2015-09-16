package com.lashgo.mobile.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.CheckBox;

import com.lashgo.mobile.R;
import com.lashgo.mobile.utils.FontUtils;

/**
 * Created by Eugene on 28.07.2014.
 */
public class RobotoCheckBox extends CheckBox {
    public RobotoCheckBox(Context context) {
        super(context);
        setFont(null);
    }

    public RobotoCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFont(attrs);
    }

    public RobotoCheckBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setFont(attrs);
    }

    private void setFont(AttributeSet attrs) {
        if (!isInEditMode() && attrs != null) {
            TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.RobotoCheckBox, 0, 0);
            try {
                String fontName = typedArray.getString(R.styleable.RobotoCheckBox_fontName);
                if (!TextUtils.isEmpty(fontName)) {
                    setTypeface(FontUtils.getRobotoTypeface(getContext(), fontName));
                }
            } finally {
                typedArray.recycle();
            }
        }
    }
}
