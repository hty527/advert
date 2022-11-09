package com.platform.lib.listener;

import com.anythink.splashad.api.ATSplashAd;

/**
 * created by hty
 * 2022/10/8
 * Desc:开屏广告的加载\播放状态
 */
public interface OnSplashListener extends BaseListener {

    /**
     * 加载开屏广告成功了
     * @param atSplashAd
     */
    void onSuccess(ATSplashAd atSplashAd);

    /**
     * 广告加载超时了
     */
    void onTimeOut();
}