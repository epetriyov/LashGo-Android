package com.lashgo.android.ui.images;

import android.content.Context;
import android.graphics.*;
import com.lashgo.android.R;
import com.squareup.picasso.Transformation;

/**
 * Created by Eugene on 15.07.2014.
 */
public class CircleTransformation implements Transformation {

    private Context context;

    private boolean displayStroke;

    public CircleTransformation(Context context, boolean displayStroke) {
        this.context = context;
        this.displayStroke = displayStroke;
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

//        if (displayStroke) {
//            Paint circlePaint = new Paint();
//            circlePaint.setAntiAlias(true);
//            circlePaint.setFilterBitmap(true);
//            circlePaint.setDither(true);
//            circlePaint.setStrokeWidth(11);
//            circlePaint.setColor(context.getResources().getColor(R.color.vote_stroke));
//            canvas.drawCircle(r, r, r + 10, circlePaint);
//        }
        squaredBitmap.recycle();
        return bitmap;
    }

    @Override
    public String key() {
        return "circle";
    }

}
