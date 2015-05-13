package com.lashgo.android.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;
import com.lashgo.android.R;
import com.lashgo.android.utils.FontUtils;

/**
 * Created by Eugene on 28.07.2014.
 */
public class RobotoTextView extends TextView {
    public RobotoTextView(Context context) {
        super(context);
        setFont(null);
    }

    public RobotoTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFont(attrs);
    }

    public RobotoTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setFont(attrs);
    }

    private void setFont(AttributeSet attrs) {
        if (!isInEditMode() && attrs != null) {
            TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.RobotoTextView, 0, 0);
            try {
                String fontName = typedArray.getString(R.styleable.RobotoTextView_fontName);
                if (!TextUtils.isEmpty(fontName)) {
                    setTypeface(FontUtils.getRobotoTypeface(getContext(), fontName));
                }
            } finally {
                typedArray.recycle();
            }
        }
    }
}
