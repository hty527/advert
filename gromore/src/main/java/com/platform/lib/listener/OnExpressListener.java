package com.platform.lib.listener;

import com.bytedance.msdk.api.v2.ad.banner.GMBannerAd;
import com.bytedance.msdk.api.v2.ad.nativeAd.GMNativeAd;

/**
 * created by hty
 * 2022/11/8
 * Desc:信息流、Banner
 */
public interface OnExpressListener extends BaseListener {

    /**
     * 信息流广告加载成功
     * @param gmNativeAd
     */
    void onSuccessExpressed(GMNativeAd gmNativeAd);

    /**
     * banner广告加载成功
     * @param gmBannerAd
     */
    void onSuccessBanner(GMBannerAd gmBannerAd);
}