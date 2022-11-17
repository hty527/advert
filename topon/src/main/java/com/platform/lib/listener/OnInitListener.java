package com.platform.lib.listener;

import com.anythink.core.api.ATInitConfig;
import java.util.List;

/**
 * created by hty
 * 2022/10/8
 * Desc:初始化状态
 */
public abstract class OnInitListener {

    /**
     * 构建初始化配置，开发者可重写此配置，自定义自己的配置
     * @return
     */
    public List<ATInitConfig> getSdkConfig() {
        return null;
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