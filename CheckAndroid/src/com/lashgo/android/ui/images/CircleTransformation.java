package com.lashgo.android.ui.images;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import com.lashgo.android.R;
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

        int x = (source.getWidth() - size) / 2;
        int y = (source.getHeight() - size) / 2;

        Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
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
        squaredBitmap.recycle();
        return bitmap;
    }

    @Override
    public String key() {
        return "circle";
    }

}
