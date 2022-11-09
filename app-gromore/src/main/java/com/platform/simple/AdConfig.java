package com.platform.simple;

/**
 * created by hty
 * 2022/6/17
 * Desc:广告位物料
 */
public interface AdConfig {

    String TO_APP_ID = "5332440";//测试app_id
    String APP_NAME = "趣计步";//测试app_id

    /**
     * 激励视频位id
     */
    String AD_CODE_REWARD_ID            ="102185566";

    /**
     * 开屏广告位id
     */
    String AD_CODE_SPLASH_ID            ="102185086";

    /**
     * 插屏广告位id
     */
    String AD_CODE_INSERT_ID            ="102185564";

    /**
     * 信息流(模板渲染)广告位id
     */
    String AD_CODE_STREAM_ID            ="102185565";

    /**
     * 信息流(自渲染)广告位id
     */
    String AD_CODE_STREAM_NATIVE_ID     ="102185565";

    /**
     * banner广告位id
     */
    String AD_CODE_BANNER_ID            ="102185374";
}