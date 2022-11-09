package com.platform.lib.listener;

/**
 * created by hty
 * 2022/10/12
 * Desc:信息流\Banner渲染状态监听
 */
public interface OnExpressAdListener {

    /**
     * 广告加载成功了
     */
    void onSuccess();

    /**
     * 广告被显示了
     */
    void onShow();

    /**
     * 广告被点击了
     */
    void onClick();

    /**
     * 广告成功渲染后的高度，单位：像素。仅Banner和信息流回调
     * @param adViewHeight
     */
    void onAdViewHeight(int adViewWidth,int adViewHeight);

    /**
     * 广告被移除了
     */
    void onClose();

    /**
     * 播放失败
     * @param code 错误码
     * @param message 描述信息
     * @param adCode 广告位ID
     */
    void onError(int code, String message, String adCode);
}