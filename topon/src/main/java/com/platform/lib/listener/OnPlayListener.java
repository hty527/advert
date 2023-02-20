package com.platform.lib.listener;

import android.app.Activity;
import com.anythink.core.api.ATAdInfo;
import com.anythink.rewardvideo.api.ATRewardVideoAd;
import com.platform.lib.bean.Result;

/**
 * created by hty
 * 2022/10/8
 * Desc:激励视频\信息流\Banner\插屏等广告 播放状态监听器
 */
public abstract class OnPlayListener {

    /**
     * 当播放激励视频时，无论何种原因，广告播放关闭了，此方法会回调。其它广告类型仅在广告正常关闭时回调
     * @param result 播放状态，当有效播放或开发者模式时不为空，其他状态为null，请注意！
     */
    public abstract void onClose(Result result);

    /**
     * 广告加载成功了(播放激励视频时回调)
     * @param atRewardVideoAd 仅激励视频并且非全自动时此对象可能不为空
     */
    public void onSuccess(ATRewardVideoAd atRewardVideoAd){}

    /**
     * 广告被显示了
     */
    public void onShow(){}

    /**
     * 广告被显示了
     * @param atRewardVideoAd 仅激励视频并且非全自动时此对象可能不为空
     */
    public void onShow(ATRewardVideoAd atRewardVideoAd){}

    /**
     * 广告被点击了(播放激励视频时回调)
     */
    public void onClick(){}

    /**
     * 广告被点击了(播放激励视频时回调)
     * @param atAdInfo 广告信息
     */
    public void onClick(ATAdInfo atAdInfo){}

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
     * 第三方广告Activity显示
     * @param activity
     */
    public void openActivity(Activity activity){}
}