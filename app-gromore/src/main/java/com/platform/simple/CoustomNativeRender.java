package com.platform.simple;

import android.content.Context;
import android.view.View;
import com.platform.lib.listener.NativeRenderControl;

/**
 * created by hty
 * 2022/10/12
 * Desc:实现NativeRenderControl接口开始自定义渲染原生信息流广告
 */
public class CoustomNativeRender implements NativeRenderControl {

    /**
     * 返自定义UI View
     * @param context 创建自定义UI组件，广告ExpressView上下文
     * @return
     */
    @Override
    public View getRenderView(Context context) {
        return View.inflate(context,R.layout.coustom_native_render,null);
    }

    @Override
    public void onRenderNativeView(View selfRenderView, com.bytedance.msdk.api.v2.ad.nativeAd.GMNativeAd nativeAd, float adWidth) {

    }

    /**
     * 将px转换成dp
     * @param context
     * @param pxValue
     * @return
     */
    public static int pxToDpInt(Context context,float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / (scale <= 0 ? 1 : scale) + 0.5f);
    }

    /**
     * 将dp转换成px
     * @param context
     * @param dipValue
     * @return
     */
    public static int dpToPxInt(Context context,float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}