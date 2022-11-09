package com.platform.simple;

import android.graphics.Outline;
import android.os.Build;
import android.view.View;
import android.view.ViewOutlineProvider;

import androidx.annotation.RequiresApi;

/**
 * Created by TinyHung@outlook.com
 * 2022/10/11
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
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
