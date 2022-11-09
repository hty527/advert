package com.platform.lib.listener;

import android.content.Context;
import android.view.View;
import com.anythink.nativead.api.ATNativeMaterial;
import com.anythink.nativead.api.ATNativePrepareInfo;

/**
 * created by hty
 * 2022/10/12
 * Desc:原生自渲染信息流广告填充器
 */
public interface NativeRenderControl<V extends View> {

    /**
     * 创建自定义UI组件
     * @param context 广告ExpressView上下文。返回一个有效的自定义ViewGroup，SDK内部会添加到容器中
     * @return
     */
    V getRenderView(Context context);

    /**
     * 将广告信息绑定到自定义UI组件上，并且将各子自定义ViewGroup(比如点击按钮、icon组件)绑定到native广告上
     * @param selfRenderView 将广告信息绑定到自定义UI组件上，并且将各子自定义ViewGroup(比如点击按钮、icon组件)绑定到native广告上,selfRenderView:自定义渲染UI组件
     * @param adMaterial 原生广告信息
     * @param adWidth 期望渲染的广告宽度，高度SDK自适应，会通过OnExpressAdListener回调通知实际渲染的高度
     * @param nativePrepareInfo 包含各子自定义ViewGroup(比如点击按钮、icon组件)等组件信息
     */
    void onRenderNativeView(V selfRenderView, ATNativeMaterial adMaterial, float adWidth, ATNativePrepareInfo nativePrepareInfo);
}