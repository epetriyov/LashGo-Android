package com.lashgo.mobile.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;
import com.lashgo.mobile.LashgoConfig;
import com.lashgo.mobile.R;

/**
 * Created by Eugene on 11.05.2015.
 */
public class GradientImageView extends View {

    private static final float DEFAULT_STROKE_WIDTH = 11f;

    private float strokeWidth;

    private float size;

    private float r;

    private LashgoConfig.CheckState checkState;

    private Paint activePaint;

    private Paint votePaint;

    private RectF rectF;

    private float koef = 0f;

    private Paint finishedPaint;

    public GradientImageView(Context context) {
        super(context);
        init(null);
    }

    public GradientImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public GradientImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (!isInEditMode() && attrs != null) {
            TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.GradientImageView, 0, 0);
            try {
                strokeWidth = typedArray.getDimension(R.styleable.GradientImageView_gradStrokeWidth, DEFAULT_STROKE_WIDTH);
            } finally {
                typedArray.recycle();
            }
        }


        activePaint = new Paint();
        activePaint.setStyle(Paint.Style.STROKE);
        activePaint.setAntiAlias(true);
        activePaint.setStrokeWidth(strokeWidth);

        votePaint = new Paint();
        votePaint.setStyle(Paint.Style.STROKE);
        votePaint.setColor(getResources().getColor(R.color.circle_vote));
        votePaint.setAntiAlias(true);
        votePaint.setStrokeWidth(strokeWidth);

        finishedPaint = new Paint();
        finishedPaint.setStyle(Paint.Style.STROKE);
        finishedPaint.setColor(getResources().getColor(R.color.finished_circle));
        finishedPaint.setAntiAlias(true);
        finishedPaint.setStrokeWidth(strokeWidth);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        size = Math.min(w, h);
        r = size / 2f;
        activePaint.setShader(new SweepGradient(r, r, getResources().getColor(R.color.circle_gradient_start), getResources().getColor(R.color.circle_gradient_end)));
        rectF = new RectF(strokeWidth, strokeWidth, size - strokeWidth, size - strokeWidth);
        super.onSizeChanged(w, h, oldw, oldh);
    }

    public void updateImage(LashgoConfig.CheckState checkState, float koef) {
        if (this.checkState == null || LashgoConfig.CheckState.ACTIVE.equals(checkState) || !this.checkState.equals(checkState)) {
            this.checkState = checkState;
            this.koef = koef;
            invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawArc(rectF, 0f, 360f, false, finishedPaint);
        if (LashgoConfig.CheckState.VOTE.equals(checkState)) {
            canvas.drawArc(rectF, 0f, 360f, false, votePaint);
        } else if (LashgoConfig.CheckState.ACTIVE.equals(checkState)) {
            canvas.save();
            canvas.rotate(-90f, r, r);
            canvas.drawArc(rectF, 360f * (1f - koef), 360f * koef, false, activePaint);
            canvas.restore();
        }
        super.onDraw(canvas);
    }
}
