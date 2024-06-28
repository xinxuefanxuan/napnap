package com.work37.napnap.Adaptor;

import android.content.Context;
import android.util.DisplayMetrics;

import androidx.recyclerview.widget.LinearSmoothScroller;

public class SlowSpeedScroller extends LinearSmoothScroller {

    private static final float MILLISECONDS_PER_INCH = 200f; // 修改：控制动画速度

    public SlowSpeedScroller(Context context) {
        super(context);
    }

    @Override
    protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
        return MILLISECONDS_PER_INCH / displayMetrics.densityDpi;
    }
}