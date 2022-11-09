package com.platform.lib.listener;

/**
 * created by hty
 * 2022/10/8
 * Desc:初始化状态
 */
public interface OnInitListener {

    /**
     * 初始化成功
     * @param appId 应用ID，在gromore后台获取
     */
    void onSuccess(String appId);

    /**
     * 初始化失败
     * @param code 错误码,参考Constance类
     * @param message 描述信息
     */
    void onError(int code,String message);
}