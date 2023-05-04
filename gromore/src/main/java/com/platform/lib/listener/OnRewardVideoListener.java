package com.platform.lib.listener;

import com.bytedance.msdk.api.v2.ad.reward.GMRewardAd;

/**
 * created by hty
 * 2022/11/8
 * Desc:激励视频
 */
public interface OnRewardVideoListener {

    /**
     * 加载视频广告成功
     * @param gmRewardAd
     */
    void onSuccess(GMRewardAd gmRewardAd);
    /**
     * 广告被显示了
     */
    void onShow();

    /**
     * 广告被点击了
     */
    void onClick(GMRewardAd rewardAd);

    /**
     * 该视频广告的有效性校验
     */
    void onRewardVerify();


    /**
     * 广告被关闭了
     * @param cpmInfo cpm/cpm精度/展示单价等json字段,例如格式：{"price":"20.28470431","precision":"exact","pre_price":"0.02028470431"}
     * @param customData 自定义透传参数
     */
    void onClose(String cpmInfo,String customData);

    /**
     * 广告拉取/播放错误
     * @param code 错误码，参考：AdConstance 和 https://www.csjplatform.com/union/media/union/download/detail?id=106&docId=62e23367a0556d002fd3caa6&osType=android
     * @param message 错误信息
     * @param adCode 广告位ID
     */
    void onError(int code, String message, String adCode);
}