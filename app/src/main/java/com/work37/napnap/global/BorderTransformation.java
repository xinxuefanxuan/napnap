package com.work37.napnap.global;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import androidx.annotation.NonNull;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import java.security.MessageDigest;

public class BorderTransformation extends BitmapTransformation {
    private final int borderWidth;
    private final int borderColor;

    public BorderTransformation(int borderWidth, int borderColor) {
        this.borderWidth = borderWidth;
        this.borderColor = borderColor;
    }

    @Override
    protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
        return addBorder(toTransform, borderWidth, borderColor);
    }

    private Bitmap addBorder(Bitmap bitmap, int borderWidth, int borderColor) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Bitmap borderedBitmap = Bitmap.createBitmap(width, height, bitmap.getConfig());

        Canvas canvas = new Canvas(borderedBitmap);
        canvas.drawBitmap(bitmap, 0, 0, null);

        Paint paint = new Paint();
        paint.setColor(borderColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(borderWidth);
        paint.setAntiAlias(true);

//        canvas.drawRect(0, 0, width, height, paint);
        //画圆角边距
        RectF rectF = new RectF(0 + borderWidth / 2, 0 + borderWidth / 2, width - borderWidth / 2, height - borderWidth / 2);
        canvas.drawRoundRect(rectF, width / 2, height / 2, paint);

        return borderedBitmap;
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
        messageDigest.update(("BorderTransformation" + borderWidth + borderColor).getBytes(CHARSET));
    }
}
