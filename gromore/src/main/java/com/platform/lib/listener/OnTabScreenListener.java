package com.platform.lib.listener;

import com.bytedance.msdk.api.v2.ad.interstitialFull.GMInterstitialFullAd;

/**
 * created by hty
 * 2022/11/8
 * Desc:插屏
 */
public interface OnTabScreenListener extends BaseListener {

    /**
     * 插屏广告加载中
     */
    void onLoading();

    /**
     * 加载插屏广告成功了
     * @param interactionAd
     */
    void onSuccess(GMInterstitialFullAd interactionAd);
}