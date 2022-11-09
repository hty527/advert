package com.platform.lib.listener;

/**
 * created by hty
 * 2022/11/8
 * Desc:监听器基类
 */
public interface BaseListener {

    /**
     * 广告被显示了
     */
    void onShow();

    /**
     * 广告被点击了
     */
    void onClick();

    /**
     * 广告被关闭了
     */
    void onClose();

    /**
     * 广告拉取/播放错误
     * @param code 错误码，参考：AdConstance 和 https://www.csjplatform.com/union/media/union/download/detail?id=106&docId=62e23367a0556d002fd3caa6&osType=android
     * @param message 错误信息
     * @param adCode 广告位ID
     */
    void onError(int code, String message, String adCode);
}