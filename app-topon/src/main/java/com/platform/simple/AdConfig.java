package com.platform.simple;

/**
 * created by hty
 * 2022/6/17
 * Desc:广告位物料,更多测试代码位请参考：https://docs.toponad.com/#/zh-cn/android/android_doc/android_test
 */
public interface AdConfig {

    String TO_APP_ID = "a62b013be01931";//测试app_id
    String TO_APP_KAY = "c3d0d2a9a9d451b07e62b509659f7c97";//测试key

    /**
     * 激励视频位id
     */
    String AD_CODE_REWARD_ID            ="b62b03c000844f";      //穿山甲：b62b03c000844f，快手：b62b032b44e3e2，优量汇：b62b0355867507

    /**
     * 开屏广告位id
     */
    String AD_CODE_SPLASH_ID            ="b62b035f70b2ac";      //穿山甲：b62b035f70b2ac，快手：b62b02dd64934e，优量汇：b62b03605066c1

    /**
     * 插屏广告位id
     */
    String AD_CODE_INSERT_ID            ="b62b0397ba87a8";      //穿山甲：b62b0397ba87a8，快手：b62b032a9446be，优量汇：b62b03987b081c

    /**
     * 信息流(模板渲染)广告位id
     */
    String AD_CODE_STREAM_ID            ="b62ea2e1248bfa";      //穿山甲：b62ea2e1248bfa，快手：b62ea2a0843eb9，优量汇：b62ea546e02034

    /**
     * 信息流(自渲染)广告位id
     */
    String AD_CODE_STREAM_NATIVE_ID     ="b62ea2e2ae729e";      //穿山甲：b62ea2e2ae729e，快手：b62ea2a1ff0a02，优量汇：b62ea5487275ef

    /**
     * banner广告位id
     */
    String AD_CODE_BANNER_ID            ="b62b03bacdcf28";      //穿山甲：b62b03bacdcf28，快手：不支持，优量汇：b62b03bbdd6cf5
}