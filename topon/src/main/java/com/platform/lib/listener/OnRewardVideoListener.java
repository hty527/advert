package com.platform.lib.listener;

import com.anythink.core.api.ATAdInfo;
import com.anythink.rewardvideo.api.ATRewardVideoAd;

/**
 * created by hty
 * 2022/10/8
 * Desc:激励视频加载\播放等原始状态监听
 */
public interface OnRewardVideoListener {

    /**
     * 加载视频广告成功
     * @param atRewardVideoAd
     */
    void onSuccess(ATRewardVideoAd atRewardVideoAd);

    /**
     * 广告被显示了
     */
    void onShow();

    /**
     * 广告被点击了
     * @param atAdInfo
     */
    void onClick(ATAdInfo atAdInfo);

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
     * @param code 错误码，参考：AdConstance 和 https://docs.toponad.com/#/zh-cn/android/android_doc/android_errorcode
     * @param message 错误信息
     * @param adCode 广告位ID
     */
    void onError(int code, String message, String adCode);
}