package com.platform.lib.constants;

/**
 * created by hty
 * 2022/10/8
 * Desc:
 */
public interface AdConstance {

    /**
     * 广告平台
     */
    String SOURCE_TT                    = "1";//穿山甲
    String SOURCE_TX                    = "3";//优量汇
    String SOURCE_KS                    = "5";//快手
    String SOURCE_TO                    = "8";//topon聚合平台

    /**
     * 广告位类型
     */
    String TYPE_STREAM                  = "1";//信息流
    String TYPE_INSERT                  = "2";//插屏广告
    String TYPE_BANNER                  = "3";//Banner
    String TYPE_REWARD_VIDEO            = "4";//激励视频
    String TYPE_FULL_VIDEO              = "5";//全屏视频
    String TYPE_SPLASH                  = "6";//开屏广告

    /**
     * 广告状态
     */
    String STATUS_LOADED_SUCCESS        = "1";//加载成功
    String STATUS_LOADED_ERROR          = "2";//加载失败
    String STATUS_SHOW_SUCCESS          = "3";//显示成功
    String STATUS_SHOW_ERROR            = "4";//显示失败

    /**
     * 错误码
     */
    int CODE_CONTEXT_INVALID            = 1; //上下文无效
    int CODE_ACTIVITY_INVALID           = 2; //Activity无效或已被关闭
    int CODE_VIEWGROUP_INVALID          = 3; //ViewGroup容器无效
    int CODE_APPID_INVALID              = 4; //app_id无效
    int CODE_APPSECRECY_INVALID         = 5; //app_secrecy无效
    int CODE_ID_UNKNOWN                 = 6;//未知的广告位
    int CODE_ID_INVALID                 = 7;//无效的广告位ID
    int CODE_TYPE_INVALID               = 8;//无效的广告位类型
    int CODE_TIMOUT                     = 9;//超时
    int CODE_ADINFO_INVALID             = 10;//广告对象无效
    int CODE_REPEATED                   = 11;//正在显示中
    int CODE_AD_EMPTY                   = 12;//暂无广告填充
    int CODE_AD_LOADING                 = 13;//广告正在请求中
    int CODE_EXIST_CACHE                = 14;//存在缓存广告
    int CODE_APPLY_FAIL                 = 15;//广告应用到布局失败
    int CODE_DEVELOP                    = 16;//开发者模式，跳过广告
    int CODE_CONFIG_LOADING             = 17;//广告配置正在加载中

    /**
     * 错误描述信息
     */
    String ERROR_CONTEXT_INVALID        = "context invalid"; //上下文无效
    String ERROR_ACTIVITY_INVALID       = "activity invalid"; //Activity无效或已被关闭
    String ERROR_VIEWGROUP_INVALID      = "viewgroup invalid";//ViewGroup容器无效
    String ERROR_APPID_INVALID          = "app_id invalid"; //app_id无效
    String ERROR_APPSECRECY_INVALID     = "app_secrecy invalid"; //app_secrecy无效
    String ERROR_ID_UNKNOWN             = "id unknown";//未知的广告位
    String ERROR_ID_INVALID             = "id is invalid";//无效的广告位ID
    String ERROR_TYPE_INVALID           = "type is invalid";//无效的广告位类型
    String ERROR_TIMOUT                 = "timeout";//超时
    String ERROR_ADINFO_INVALID         = "ad invalid";//广告对象无效
    String ERROR_REPEATED               = "is showing";//正在显示中
    String ERROR_AD_EMPTY               = "ad is empty";//暂无广告填充
    String ERROR_AD_LOADING             = "please wait...";//广告正在请求中
    String ERROR_EXIST_CACHE            = "exist_cache";//存在缓存广告
    String ERROR_APPLY_FAIL             = "express apply fail";//广告应用到布局失败
    String ERROR_DEVELOP                = "develop mode,skip";//开发者模式，跳过广告
    String ERROR_CONFIG_LOADING         = "config loading";//广告配置正在加载中

    /**
     * 加载广告场景
     */
    String SCENE_CACHE                  = "-1";//默认是缓存广告
}