package com.platform.lib.widget;

import android.annotation.SuppressLint;
import android.graphics.Outline;
import android.view.View;
import android.view.ViewOutlineProvider;

/**
 * Created by TinyHung@outlook.com
 * 2022/10/13
 */
@SuppressLint("NewApi")
public class LayoutProvider extends ViewOutlineProvider {

    private float mRadius;

    public LayoutProvider(float radius){
        this.mRadius = radius;
    }

    @Override
    public void getOutline(View view, Outline outline) {
        outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), mRadius);
        view.setClipToOutline(true);
    }
}