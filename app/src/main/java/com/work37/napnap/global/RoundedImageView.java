package com.work37.napnap.global;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

public class RoundedImageView extends AppCompatImageView {
    public RoundedImageView(Context context) {
        super(context);
    }

    public RoundedImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RoundedImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setRoundedImage(Drawable drawable, int radius) {
        RequestOptions requestOptions = new RequestOptions()
                .transform(new RoundedCorners(radius))
                .diskCacheStrategy(DiskCacheStrategy.ALL);

        Glide.with(getContext())
                .load(drawable)
                .apply(requestOptions)
                .into(this);
    }

    public void setRoundedImage(String imageUrl, int radius, int borderWidth, int borderColor) {
        RequestOptions requestOptions = new RequestOptions()
                .transform(new BorderTransformation(borderWidth, borderColor), new RoundedCorners(radius))
                .diskCacheStrategy(DiskCacheStrategy.ALL);

        Glide.with(getContext())
                .load(imageUrl)
                .apply(requestOptions)
                .into(this);
    }
}
