package com.platform.lib.listener;

import com.bytedance.msdk.api.v2.ad.reward.GMRewardAd;

/**
 * created by hty
 * 2022/11/8
 * Desc:激励视频
 */
public interface OnRewardVideoListener extends BaseListener {

    /**
     * 加载视频广告成功
     * @param gmRewardAd
     */
    void onSuccess(GMRewardAd gmRewardAd);
    /**
     * 该视频广告的有效性校验
     */
    void onRewardVerify();

    /**
     * 广告被显示了
     * @param ecpm 当前广告的实时ecpm
     */
    void onShow(String ecpm);
}