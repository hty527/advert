package com.platform.lib.listener;

import com.bytedance.msdk.api.v2.GMAdConfig;
import com.platform.lib.manager.PlatformManager;

/**
 * created by hty
 * 2022/11/8
 * Desc:初始化状态
 */
public abstract class OnInitListener {

    /**
     * 构建初始化配置，开发者可重写此配置，自定义自己的配置
     * @param appId 应用ID，在gromore后台获取
     * @param appName 应用名称
     * @param channel 渠道名称
     * @param debug 是否开启调试模式，true：开启调试模式，false：关闭调试模式
     * @return
     */
    public GMAdConfig buildGromoreConfig(String appId, String appName, String channel,boolean debug) {
        return PlatformManager.getInstance().buildGromoreConfig(appId,appName,channel,debug);
    }

    /**
     * 初始化成功
     * @param appId 应用ID，在gromore后台获取
     */
    public void onSuccess(String appId){}

    /**
     * 初始化失败
     * @param code 错误码,参考Constance类
     * @param message 描述信息
     */
    public void onError(int code,String message){}

}