package com.platform.lib.listener;

import com.anythink.banner.api.ATBannerView;
import com.anythink.nativead.api.NativeAd;

/**
 * created by hty
 * 2022/10/8
 * Desc:信息流、Banner广告的加载状态
 */
public interface OnExpressListener extends BaseListener {

    /**
     * 信息流广告加载成功
     * @param nativeAd
     */
    void onSuccessExpressed(NativeAd nativeAd);

    /**
     * banner广告加载成功
     * @param atBannerView
     */
    void onSuccessBanner(ATBannerView atBannerView);
}