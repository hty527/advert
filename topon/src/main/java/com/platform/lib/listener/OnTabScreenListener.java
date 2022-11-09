package com.platform.lib.listener;

import com.anythink.interstitial.api.ATInterstitial;

/**
 * created by hty
 * 2022/10/8
 * Desc:插屏广告的加载监听
 */
public interface OnTabScreenListener extends BaseListener {
    /**
     * 加载插屏广告成功了
     * @param interactionAd
     */
    void onSuccess(ATInterstitial interactionAd);
}