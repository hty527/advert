package com.platform.lib.listener;

import java.util.Map;

/**
 * created by hty
 * 2022/10/8
 * Desc:广告各状态全局状态监听
 */
public interface OnEventListener {

    /**
     * 广告的缓存、展示功能是否可用
     * @return 广告的缓存、展示功能是否可用，true：可用，false：被禁用
     */
    boolean isAvailable();

    /**
     * 根据错误码返回文字文案
     * @param code 错误码参考AdConstance定义的"错误码"
     * @return 如果返回的文字为空，则使用SDK内部默认文案
     */
    String getText(int code);

    /**
     * 自定义透传的数据
     * @return 自定义透传的数据,开启服务端验证的激励视频代码位传入。SDK将回传给给代码位指定的回调地址
     * 参考：https://docs.toponad.com/#/zh-cn/android/android_doc/android_sdk_rewardedvideo_auto_access
     */
    Map<String,Object> localExtra();

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