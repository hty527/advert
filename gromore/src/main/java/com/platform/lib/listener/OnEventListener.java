package com.platform.lib.listener;

/**
 * created by hty
 * 2022/11/8
 * Desc:广告各状态全局状态监听
 */
public interface OnEventListener {

    /**
     * 根据错误码返回文字文案
     * @param code 错误码参考AdConstance定义的"错误码"
     * @return 如果返回的文字为空，则使用SDK内部默认文案
     */
    String getText(int code);

    /**
     * 广告事件回调
     * @param scene 播放广告的场景
     * @param ad_type 广告类型，Constance申明
     * @param ad_code 广告位ID
     * @param ad_status 广告状态：1：加载成功 2：加载失败 3：显示成功 4：显示失败
     * @param error_code 错误码
     * @param error_msg 全量的错误信息
     */
    void onEvent(String scene, String ad_type, String ad_code, String ad_status, int error_code, String error_msg);
}