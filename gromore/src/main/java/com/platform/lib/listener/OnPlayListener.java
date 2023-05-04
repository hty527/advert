package com.platform.lib.listener;

import android.app.Activity;
import com.bytedance.msdk.api.v2.ad.reward.GMRewardAd;
import com.platform.lib.bean.Result;

/**
 * created by hty
 * 2022/11/8
 * Desc:激励视频\开屏播放监听器
 */
public abstract class OnPlayListener {

    /**
     * 广告加载成功了(播放激励视频时回调)
     * @param gmRewardAd 仅激励视频并且非全自动时此对象可能不为空
     */
    public void onSuccess(GMRewardAd gmRewardAd){}

    /**
     * 第三方广告Activity被创建了
     * @param activity
     */
    public void onAdvertActivityCreated(Activity activity){}

    /**
     * 广告被显示了
     */
    public void onShow(){}

    /**
     * 广告被点击了(播放开屏广告时回调)
     */
    public void onClick(){}


    /**
     * 广告被点击了(播放激励视频时回调)
     * @param rewardAd 广告信息
     */
    public void onClick(GMRewardAd rewardAd){}

    /**
     * 广告播放合法有效
     */
    public void onRewardVerify(){}

    /**
     * 播放失败
     * @param code 错误码
     * @param message 描述信息
     * @param adCode 广告位ID
     */
    public void onError(int code, String message, String adCode){}

    /**
     * 当播放激励视频时，无论何种原因，广告播放关闭了，此方法会回调。其它广告类型仅在广告正常关闭时回调
     * @param result 播放状态，当有效播放或开发者模式时不为空，其他状态为null，请注意！
     */
    public abstract void onClose(Result result);
}