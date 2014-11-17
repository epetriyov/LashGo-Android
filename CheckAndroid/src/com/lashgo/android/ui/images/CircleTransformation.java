package com.lashgo.android.ui.images;

import android.content.Context;
import android.graphics.*;
import com.lashgo.android.R;
import com.lashgo.android.utils.PhotoUtils;
import com.squareup.picasso.Transformation;

/**
 * Created by Eugene on 15.07.2014.
 */
public class CircleTransformation implements Transformation {

    private Context context;

    private boolean useDarkening;


    public CircleTransformation(Context context, boolean useDarkening) {
        this.context = context;
        this.useDarkening = useDarkening;
        if (context == null) {
            throw new IllegalStateException("Context can't be null");
        }
    }

    @Override
    public Bitmap transform(Bitmap source) {

        int size = Math.min(source.getWidth(), source.getHeight());

        Bitmap squaredBitmap = Bitmap.createBitmap(source, 0, 0, size, size);
        if (squaredBitmap != source) {
            source.recycle();
        }

        Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        BitmapShader shader = new BitmapShader(squaredBitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
        paint.setShader(shader);
        paint.setAntiAlias(true);

        float r = size / 2f;
        canvas.drawCircle(r, r, r, paint);

        if (useDarkening) {
            Paint shadow = new Paint();
            shadow.setColor(context.getResources().getColor(R.color.darkening));
            canvas.drawCircle(r, r, r, shadow);
        }

//        Paint arc = new Paint();
//        arc.setStyle(Paint.Style.STROKE);
//        arc.setShader(new SweepGradient(r, r, R.color.circle_gradient_start, R.color.circle_gradient_end));
//        arc.setStrokeWidth(11);
//        canvas.drawArc(new RectF(0, 0, source.getWidth(), source.getHeight()), 360, 360, false, arc);

        squaredBitmap.recycle();
        return bitmap;
    }

    @Override
    public String key() {
        return "circle";
    }

}
