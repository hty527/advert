package com.platform.lib.listener;

import com.bytedance.msdk.api.v2.ad.splash.GMSplashAd;

/**
 * created by hty
 * 2022/11/8
 * Desc:开屏
 */
public interface OnSplashListener extends BaseListener {

    /**
     * 加载开屏广告成功了
     * @param gmSplashAd
     */
    void onSuccess(GMSplashAd gmSplashAd);

    /**
     * 广告加载超时了
     */
    void onTimeOut();
}