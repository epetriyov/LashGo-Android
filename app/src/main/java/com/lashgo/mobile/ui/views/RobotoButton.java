package com.lashgo.mobile.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.Button;
import com.lashgo.mobile.R;
import com.lashgo.mobile.utils.FontUtils;

/**
 * Created by Eugene on 28.07.2014.
 */
public class RobotoButton extends Button {
    public RobotoButton(Context context) {
        super(context);
        setFont(null);
    }

    public RobotoButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFont(attrs);
    }

    public RobotoButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setFont(attrs);
    }

    private void setFont(AttributeSet attrs) {
        if (!isInEditMode() && attrs != null) {
            TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.RobotoButton, 0, 0);
            try {
                String fontName = typedArray.getString(R.styleable.RobotoButton_fontName);
                if (!TextUtils.isEmpty(fontName)) {
                    setTypeface(FontUtils.getRobotoTypeface(getContext(), fontName));
                }
            } finally {
                typedArray.recycle();
            }
        }
    }
}
