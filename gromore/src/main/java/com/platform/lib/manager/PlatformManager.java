package com.platform.lib.manager;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.FrameLayout;
import com.bytedance.msdk.adapter.pangle.PangleNetworkRequestInfo;
import com.bytedance.msdk.adapter.util.ThreadHelper;
import com.bytedance.msdk.api.AdError;
import com.bytedance.msdk.api.GMAdEcpmInfo;
import com.bytedance.msdk.api.UserInfoForSegment;
import com.bytedance.msdk.api.reward.RewardItem;
import com.bytedance.msdk.api.v2.GMAdConfig;
import com.bytedance.msdk.api.v2.GMAdConstant;
import com.bytedance.msdk.api.v2.GMAdSize;
import com.bytedance.msdk.api.v2.GMConfigUserInfoForSegment;
import com.bytedance.msdk.api.v2.GMMediationAdSdk;
import com.bytedance.msdk.api.v2.GMPangleOption;
import com.bytedance.msdk.api.v2.GMPrivacyConfig;
import com.bytedance.msdk.api.v2.GMSettingConfigCallback;
import com.bytedance.msdk.api.v2.ad.banner.GMBannerAd;
import com.bytedance.msdk.api.v2.ad.banner.GMBannerAdListener;
import com.bytedance.msdk.api.v2.ad.banner.GMBannerAdLoadCallback;
import com.bytedance.msdk.api.v2.ad.interstitialFull.GMInterstitialFullAd;
import com.bytedance.msdk.api.v2.ad.interstitialFull.GMInterstitialFullAdListener;
import com.bytedance.msdk.api.v2.ad.interstitialFull.GMInterstitialFullAdLoadCallback;
import com.bytedance.msdk.api.v2.ad.nativeAd.GMNativeAd;
import com.bytedance.msdk.api.v2.ad.nativeAd.GMNativeAdLoadCallback;
import com.bytedance.msdk.api.v2.ad.nativeAd.GMUnifiedNativeAd;
import com.bytedance.msdk.api.v2.ad.reward.GMRewardAd;
import com.bytedance.msdk.api.v2.ad.reward.GMRewardedAdListener;
import com.bytedance.msdk.api.v2.ad.reward.GMRewardedAdLoadCallback;
import com.bytedance.msdk.api.v2.ad.splash.GMSplashAd;
import com.bytedance.msdk.api.v2.ad.splash.GMSplashAdListener;
import com.bytedance.msdk.api.v2.ad.splash.GMSplashAdLoadCallback;
import com.bytedance.msdk.api.v2.slot.GMAdOptionUtil;
import com.bytedance.msdk.api.v2.slot.GMAdSlotBanner;
import com.bytedance.msdk.api.v2.slot.GMAdSlotInterstitialFull;
import com.bytedance.msdk.api.v2.slot.GMAdSlotNative;
import com.bytedance.msdk.api.v2.slot.GMAdSlotRewardVideo;
import com.bytedance.msdk.api.v2.slot.GMAdSlotSplash;
import com.bytedance.msdk.api.v2.slot.paltform.GMAdSlotGDTOption;
import com.platform.lib.constants.AdConstance;
import com.platform.lib.listener.BaseListener;
import com.platform.lib.listener.OnEventListener;
import com.platform.lib.listener.OnExpressListener;
import com.platform.lib.listener.OnInitListener;
import com.platform.lib.listener.OnRewardVideoListener;
import com.platform.lib.listener.OnSplashListener;
import com.platform.lib.listener.OnTabScreenListener;
import com.platform.lib.utils.Logger;
import com.platform.lib.utils.PlatformPreferences;
import com.platform.lib.utils.PlatformUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * created by hty
 * 2022/11/8
 * Desc:针对GoMoMore聚合广告的五种广告封装实现
 *
 * 1、初始化广告SDK{@link #initSdk(Context context, String appId,OnInitListener listener)}
 * 2、开发者免插屏及激励视频广告：设置{@link #setDevelop(boolean)}为true即可免激励视频广告
 * 3、广告位、广告事件状态请设置监听器{@link #setOnEventListener(OnEventListener)}
 *
 * 推荐的各广告的使用方法：
 * 1、开屏广告请使用SplashView
 * 2、激励视频播放播放请使用PlayerManager提供的api
 * 3、插屏广告播放请使用InsertManager提供的api
 * 4、信息流、插屏、banner广告播放请使用ExpressView类
 * 各广告api
 * 1、开屏：{@link #loadSplash(Activity context,String id, OnSplashListener listener)}
 * 2、激励视频：{@link #loadRewardVideo(Activity context,String id, OnRewardVideoListener listener)}
 * 3、插屏：{@link #loadInsert(Activity context,String id, OnTabScreenListener listener)}
 * 4、信息流：{@link #loadStream(Activity context,String id, OnExpressListener listener)}
 * 5、banner：{@link #loadBanner(Activity context,String id, OnExpressListener listener)}
 */
public final class PlatformManager implements Application.ActivityLifecycleCallbacks {

    private volatile static PlatformManager mInstance;
    private OnEventListener mAdvertEventListener;//广告状态监听
    private boolean isDevelop =false;//是否处于开发模式，开发模式情况下激励视频广告免播放，也不会去缓存激励视频广告
    private String appId, appName,finalSplashCode;//APP_ID\APP_KEY\媒体物料名称\穿山甲的兜底开屏代码位
    private boolean DEBUG = false;
    private Activity mCurrentActivity;//当前正在活跃的Activity,用于显示插屏、加载信息流、Banner等广告
    private Map<Integer,String> mUIText=new HashMap<>();
    //激励视频
    private OnRewardVideoListener mRewardVideoListener;
    private GMRewardAd mGMRewardAd;
    //开屏广告
    private int mSplashTimeOut = 3000;//超时时间,单位：毫秒
    private OnSplashListener mSplashListener;
    private GMSplashAd mAdSdkSplash;
    //插屏广告
    private GMInterstitialFullAd mInterstitialAd;
    private OnTabScreenListener mInsertListener;
    //Banner广告
    private GMBannerAd mBannerViewAd;
    //开屏\激励视频\插屏\全自动激励视频\全自动插屏 的临时场景和广告位ID
    private String mSplashScene,mSplashCode,mVideoScene,mVideoCode,mInsertScene,mInsertCode;
    //初始化监听器
    private OnInitListener mOnInitListener;

    /**
     * 文案提示内容
     */
    {
        mUIText.put(AdConstance.CODE_CONTEXT_INVALID,AdConstance.ERROR_CONTEXT_INVALID);
        mUIText.put(AdConstance.CODE_ACTIVITY_INVALID,AdConstance.ERROR_ACTIVITY_INVALID);
        mUIText.put(AdConstance.CODE_VIEWGROUP_INVALID,AdConstance.ERROR_VIEWGROUP_INVALID);
        mUIText.put(AdConstance.CODE_APPID_INVALID,AdConstance.ERROR_APPID_INVALID);
        mUIText.put(AdConstance.CODE_APPSECRECY_INVALID,AdConstance.ERROR_APPSECRECY_INVALID);
        mUIText.put(AdConstance.CODE_ID_UNKNOWN,AdConstance.ERROR_ID_UNKNOWN);
        mUIText.put(AdConstance.CODE_ID_INVALID,AdConstance.ERROR_ID_INVALID);
        mUIText.put(AdConstance.CODE_TYPE_INVALID,AdConstance.ERROR_TYPE_INVALID);
        mUIText.put(AdConstance.CODE_TIMOUT,AdConstance.ERROR_TIMOUT);
        mUIText.put(AdConstance.CODE_ADINFO_INVALID,AdConstance.ERROR_ADINFO_INVALID);
        mUIText.put(AdConstance.CODE_REPEATED,AdConstance.ERROR_REPEATED);
        mUIText.put(AdConstance.CODE_AD_EMPTY,AdConstance.ERROR_AD_EMPTY);
        mUIText.put(AdConstance.CODE_AD_LOADING,AdConstance.ERROR_AD_LOADING);
        mUIText.put(AdConstance.CODE_EXIST_CACHE,AdConstance.ERROR_EXIST_CACHE);
        mUIText.put(AdConstance.CODE_APPLY_FAIL,AdConstance.ERROR_APPLY_FAIL);
        mUIText.put(AdConstance.CODE_DEVELOP,AdConstance.ERROR_DEVELOP);
        mUIText.put(AdConstance.CODE_CONFIG_LOADING,AdConstance.ERROR_CONFIG_LOADING);
    }

    public static PlatformManager getInstance() {
        if(null==mInstance){
            synchronized (PlatformManager.class) {
                if (null == mInstance) {
                    mInstance = new PlatformManager();
                }
            }
        }
        return mInstance;
    }

    //========================================初始化及设置============================================

    /**
     * 广告SDK初始化，建议尽可能的早，在application中初始化
     * @param context 全局上下文，建议为：Application
     * @param appId 物料 APP_ID(gromore后台获取)
     * @param listener 初始化状态监听器
     */
    public void initSdk(Context context, String appId, OnInitListener listener){
        initSdk(context,appId, null,null,false,listener);
    }

    /**
     * 广告SDK初始化，建议尽可能的早，在application中初始化
     * @param context 全局上下文，建议为：Application
     * @param appId 物料 APP_ID(gromore后台获取)
     * @param appName 应用名称
     * @param channel 渠道标识
     * @param debug 是否debug模式，默认：否，debug模式下将输出logcat日志
     * @param listener 初始化状态监听器
     */
    public void initSdk(Context context, String appId, String appName,String channel, boolean debug, OnInitListener listener){
        initSdk(context,appId, appName,channel,"develop",debug,listener);
    }

    /**
     * 广告SDK初始化，建议尽可能的早，在application中初始化
     * @param context 全局上下文，建议为：Application
     * @param appId 物料 APP_ID(gromore后台获取)
     * @param appName 应用名称
     * @param channel 渠道标识
     * @param tag SDK标识
     * @param debug 是否debug模式，默认：否，debug模式下将输出logcat日志
     * @param listener 初始化状态监听器
     */
    public void initSdk(Context context, String appId,String appName, String channel, String tag, boolean debug, OnInitListener listener){
        Logger.d("initSdk-->appId:" + appId + ",appName:" + appName + ",channel:" + channel + ",debug:" + debug);
        try {
            PlatformUtils.getInstance().setContext(context);
            PlatformPreferences.init(context, context.getPackageName() + ".key_info", Context.MODE_MULTI_PROCESS);
            setAppId(appId);
            setAppName(appName);
            if (null==context) {
                if(null!=listener) listener.onError(AdConstance.CODE_CONTEXT_INVALID, getText(AdConstance.CODE_CONTEXT_INVALID));
                return;
            }
            if (TextUtils.isEmpty(getAppId())) {
                if(null!=listener) listener.onError(AdConstance.CODE_APPID_INVALID, getText(AdConstance.CODE_APPID_INVALID));
                return;
            }
            if (TextUtils.isEmpty(getAppName())) {
                if(null!=listener) listener.onError(AdConstance.CODE_APPSECRECY_INVALID, getText(AdConstance.CODE_APPSECRECY_INVALID));
                return;
            }
            this.mOnInitListener=listener;
            this.DEBUG = debug;
            Logger.setDebug(DEBUG);
            GMMediationAdSdk.registerConfigCallback(new GMSettingConfigCallback() {
                @Override
                public void configLoad() {
                    Logger.d("initSdk-->configLoad");
                    GMMediationAdSdk.unregisterConfigCallback(this);
                    initSuccess();
                }
            });
            //初始化SDK
            GMMediationAdSdk.initialize(context,null!=listener?listener.buildGromoreConfig(appId,appName,channel,debug):buildGromoreConfig(appId,appName,channel,debug));
            if(context instanceof Application){
                ((Application) context).registerActivityLifecycleCallbacks(this);
            }
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    initSuccess();
                }
            },3000);
        }catch (Throwable e){
            e.printStackTrace();
            initSuccess();
        }
    }

    /**
     * 监听广告的加载、展示状态
     * @param eventListener
     */
    public PlatformManager setOnEventListener(OnEventListener eventListener) {
        mAdvertEventListener = eventListener;
        return mInstance;
    }

    /**
     * 设置SDK是否处于开发者模式下，如果处于开发者模式下，插屏、激励视频将跳过播放直接返回有效结果。
     * @param develop true:开发者模式 false:正式模式
     * @return
     */
    public PlatformManager setDevelop(boolean develop) {
        isDevelop = develop;
        return mInstance;
    }

    public boolean isDevelop() {
        return isDevelop;
    }

    /**
     * 返回交互文案
     * @param code
     * @return
     */
    public String getText(int code){
        if(null!=mAdvertEventListener){
            String text = mAdvertEventListener.getText(code);
            if(!TextUtils.isEmpty(text)){
                return text;
            }
        }
        return mUIText.get(code);
    }

    public String getVersion(){
        return PlatformUtils.getInstance().getVersion();
    }

    /**
     * 构建初始化配置，开发者可重写此配置，自定义自己的配置
     * @param appId 应用ID，在gromore后台获取
     * @param appName 应用名称
     * @param channel 渠道名称
     * @param debug 是否开启调试模式，true：开启调试模式，false：关闭调试模式
     * @return
     */
    public GMAdConfig buildGromoreConfig(String appId, String appName, String channel,boolean debug) {
        GMConfigUserInfoForSegment userInfo = new GMConfigUserInfoForSegment();
        userInfo.setGender(UserInfoForSegment.GENDER_MALE);
        userInfo.setChannel(channel);
        userInfo.setSubChannel("msdk-sub-channel");
        userInfo.setAge(999);
        userInfo.setUserValueGroup("msdk-demo-user-value-group");

//        Map<String, String> customInfos = new HashMap<>();
//        userInfo.setCustomInfos(customInfos);
        return new GMAdConfig.Builder()
                .setAppId(appId)
                .setAppName(appName)
                .setDebug(false)
                //.setPublisherDid(getAndroidId(context))
                .setOpenAdnTest(false)
                .setConfigUserInfoForSegment(userInfo)
                .setPangleOption(new GMPangleOption.Builder()
                        .setIsPaid(false)
                        .setTitleBarTheme(GMAdConstant.TITLE_BAR_THEME_DARK)
                        .setAllowShowNotify(true)
                        .setAllowShowPageWhenScreenLock(true)
                        .setDirectDownloadNetworkType(GMAdConstant.NETWORK_STATE_WIFI, GMAdConstant.NETWORK_STATE_3G)
                        .setIsUseTextureView(true)
                        .setNeedClearTaskReset()
                        .setKeywords("")
                        .build())
                .setPrivacyConfig(new GMPrivacyConfig() {
                    // 重写相应的函数，设置需要设置的权限开关，不重写的将采用默认值
                    // 例如，重写isCanUsePhoneState函数返回true，表示允许使用ReadPhoneState权限。
                    @Override
                    public boolean isCanUsePhoneState() {
                        return false;
                    }

                    //当isCanUseWifiState=false时，可传入Mac地址信息，穿山甲sdk使用您传入的Mac地址信息
                    @Override
                    public String getMacAddress() {
                        return "";
                    }

                    // 设置青少年合规，默认值GMAdConstant.ADULT_STATE.AGE_ADULT为成年人
                    @Override
                    public GMAdConstant.ADULT_STATE getAgeGroup() {
                        return GMAdConstant.ADULT_STATE.AGE_ADULT;
                    }
                })
                .build();
    }

    /**
     * 设置穿山甲开屏兜底代码位
     * @param finalSplashCode 穿山甲代码位
     */
    public void setFinalSplashCode(String finalSplashCode) {
        this.finalSplashCode = finalSplashCode;
    }

    private String getSplashFinalCode() {
        return finalSplashCode;
    }

    /**
     * 初始化完成
     */
    private void initSuccess() {
        if(null!=mOnInitListener){
            OnInitListener listener=mOnInitListener;
            mOnInitListener=null;
            listener.onSuccess(getAppId());
        }
    }

    private String getAppId() {
        if (TextUtils.isEmpty(appId)){
            appId = PlatformPreferences.getInstance().getString("app_id", null);
        }
        return appId;
    }

    private void setAppId(String appId) {
        this.appId = appId;
        PlatformPreferences.getInstance().putString("app_id", appId);
    }

    private String getAppName() {
        if (TextUtils.isEmpty(appName)){
            appName = PlatformPreferences.getInstance().getString("app_name", null);
        }
        return appName;
    }

    private void setAppName(String appName) {
        this.appName = appName;
        PlatformPreferences.getInstance().putString("app_name", appName);
    }

    private void event(String scene, String ad_type, String ad_id, String status, int error_code, String error_msg) {
        if(null!=mAdvertEventListener) mAdvertEventListener.onEvent(scene,ad_type,ad_id,status,error_code,error_msg);
    }

    /**
     * 返回广告配置的可用状态，不可用自动加载配置
     * @param listener
     * @param posiid 广告位
     * @param method 方法
     * @return
     */
    private boolean isConfigAvailable(BaseListener listener, String posiid, String method){
        if (GMMediationAdSdk.configLoadSuccess()) {
//            Logger.d("isConfig->exist,posiid:"+posiid+",method:"+method);
            return true;
        } else {
//            Logger.d("isConfig->not exist,posiid:"+posiid+",method:"+method);
            GMMediationAdSdk.registerConfigCallback(new GMSettingConfigCallback() {
                @Override
                public void configLoad() {
//                    Logger.d("isConfig->loaded,posiid:"+posiid+",method:"+method);
                    GMMediationAdSdk.unregisterConfigCallback(this);
                }
            });
            //不用使用内部类，否则在ondestory中无法移除该回调
            if(null!=listener) listener.onError(AdConstance.CODE_CONFIG_LOADING, getText(AdConstance.CODE_CONFIG_LOADING),posiid);
            return false;
        }
    }

//============================================开屏广告===========================================

    /**
     * 设置开屏加载超时时长
     * @param timeOut 超时时间戳，单位：毫秒
     * @return
     */
    public PlatformManager setSplashTimeOut(int timeOut) {
        this.mSplashTimeOut =timeOut;
        return mInstance;
    }

    public void setOnSplashListener(OnSplashListener splashListener) {
        this.mSplashListener = splashListener;
    }

    public GMSplashAd getSplash() {
        return mAdSdkSplash;
    }

    public void onResetSplash() {
        mAdSdkSplash = null;
        mSplashListener = null;
    }

    /**
     * 加载开屏广告
     * @param context Activity类型上下文
     * @param id 广告位ID
     * @param listener 状态监听
     */
    public void loadSplash(Activity context,String id, OnSplashListener listener){
        loadSplash(context,id, AdConstance.SCENE_CACHE,listener);
    }

    /**
     * 加载开屏广告
     * @param context Activity类型上下文
     * @param id 广告位ID
     * @param scene 场景
     * @param listener 状态监听，如果监听器为空内部回自动缓存一条开屏广告
     */
    public void loadSplash(Activity context, String id, String scene, OnSplashListener listener){
        loadSplash(context,id,scene,PlatformUtils.getInstance().getScreenWidth(),PlatformUtils.getInstance().getScreenHeight(),listener);
    }

    /**
     * 加载开屏广告
     * @param context Activity类型上下文
     * @param id 广告位ID
     * @param scene 场景
     * @param width 预期渲染的宽，单位：分辨率
     * @param width 预期渲染的高，单位：分辨率
     * @param listener 状态监听，如果监听器为空内部回自动缓存一条开屏广告
     */
    public void loadSplash(Activity context, String id, String scene, int width,int height,OnSplashListener listener){
        Logger.d("loadSplash-->id:"+id+",scene:"+scene+",width:"+width+",height"+height);
        if(null==context||context.isFinishing()){
            if(null!=listener) listener.onError(AdConstance.CODE_ACTIVITY_INVALID, getText(AdConstance.CODE_ACTIVITY_INVALID), id);
            return;
        }
        if(TextUtils.isEmpty(id)){
            if(null!=listener) listener.onError(AdConstance.CODE_ID_UNKNOWN, getText(AdConstance.CODE_ID_UNKNOWN), id);
            return;
        }
        this.mSplashListener =listener;
        if(null!=mAdSdkSplash){
            Logger.d("loadSplash-->"+getText(AdConstance.CODE_EXIST_CACHE));
            if(null!= mSplashListener){
                mSplashListener.onSuccess(mAdSdkSplash);
            }
            return;
        }
        //初始化开屏广告
        this.mSplashCode=id;this.mSplashScene=scene;
        if(null==listener&&!GMMediationAdSdk.configLoadSuccess()){
            Logger.e("loadSplash-->config loading");
            if(null!=listener) listener.onError(AdConstance.CODE_CONFIG_LOADING, getText(AdConstance.CODE_CONFIG_LOADING), id);
            return;
        }
        if(width<=0) width=PlatformUtils.getInstance().getScreenWidth();
        if(height<=0) height=PlatformUtils.getInstance().getScreenHeight();
        mAdSdkSplash = new GMSplashAd(context,id);
        mAdSdkSplash.setAdSplashListener(mSplashAdListener);
        //加载开屏广告配置
        GMAdSlotSplash adSlot = new GMAdSlotSplash.Builder()
                .setImageAdSize(width, height)
                .setTimeOut(mSplashTimeOut)//设置超时
                .setSplashButtonType(GMAdConstant.SPLASH_BUTTON_TYPE_FULL_SCREEN)
                .setDownloadType(GMAdConstant.DOWNLOAD_TYPE_POPUP)
                .setForceLoadBottom(true) //强制加载兜底开屏广告，只能在GroMore提供的demo中使用，其他情况设置无效
                .setBidNotify(true)//开启bidding比价结果通知，默认值为false
                .setSplashShakeButton(true) //开屏摇一摇开关，默认开启，目前只有gdt支持
                .build();
        PangleNetworkRequestInfo ttNetworkRequestInfo = new PangleNetworkRequestInfo(getAppId(),getSplashFinalCode());
        mAdSdkSplash.loadAd(adSlot, ttNetworkRequestInfo, new GMSplashAdLoadCallback() {

            @Override
            public void onSplashAdLoadFail(com.bytedance.msdk.api.AdError adError) {
                Logger.e("loadSplash-->error,code:"+adError.code+",message:"+adError.message);
                event(mSplashScene, AdConstance.TYPE_SPLASH,mSplashCode, AdConstance.STATUS_LOADED_ERROR,adError.code,adError.message);
                OnSplashListener listener=mSplashListener;
                onResetSplash();
                if(null!= listener) listener.onError(adError.code,adError.message, mSplashCode);
            }

            @Override
            public void onSplashAdLoadSuccess() {
                Logger.d("loadSplash-->loaded");
                event(mSplashScene, AdConstance.TYPE_SPLASH,mSplashCode, AdConstance.STATUS_LOADED_SUCCESS,0,null);
                if(null!= mSplashListener){
                    mSplashListener.onSuccess(mAdSdkSplash);
                }
            }

            @Override
            public void onAdLoadTimeout() {
                event(mSplashScene, AdConstance.TYPE_SPLASH,mSplashCode, AdConstance.STATUS_LOADED_ERROR, AdConstance.CODE_TIMOUT, getText(AdConstance.CODE_TIMOUT));
                OnSplashListener listener=mSplashListener;
                onResetSplash();
                if(null!= listener) listener.onTimeOut();
            }
        });
    }

    private GMSplashAdListener mSplashAdListener=new GMSplashAdListener(){

        @Override
        public void onAdClicked() {
//            Logger.d("loadSplash-->onAdClicked");
            if(null!= mSplashListener){
                mSplashListener.onClick();
            }
        }

        @Override
        public void onAdShow() {
//            Logger.d("loadSplash-->onAdShow");
            event(mSplashScene, AdConstance.TYPE_SPLASH,mSplashCode, AdConstance.STATUS_SHOW_SUCCESS, 0,null);
            if(null!= mSplashListener){
                mSplashListener.onShow();
            }
        }

        @Override
        public void onAdShowFail(com.bytedance.msdk.api.AdError adError) {
            Logger.e("loadSplash-->showError,code:"+adError.code+",message:"+adError.message);
            event(mSplashScene, AdConstance.TYPE_SPLASH,mSplashCode, AdConstance.STATUS_SHOW_ERROR,adError.code,adError.message);
            OnSplashListener listener=mSplashListener;
            onResetSplash();
            if(null!= listener) listener.onError(adError.code,adError.message, mSplashCode);
        }

        @Override
        public void onAdSkip() {
//            Logger.d("loadSplash-->onAdSkip");
            OnSplashListener listener=mSplashListener;
            onResetSplash();
            if(null!= listener) listener.onClose();
        }

        @Override
        public void onAdDismiss() {
//            Logger.d("loadAdvOpenScreen-->onAdDismiss");
            OnSplashListener listener=mSplashListener;
            onResetSplash();
            if(null!= listener) listener.onClose();
        }
    };


    //============================================激励视频===========================================

    /**
     * 显示激励视频广告(如果存在的话)
     * @param activity Activity类型上下文
     * @param listener 监听器
     */
    public void showRewardVideo(Activity activity, OnRewardVideoListener listener){
        if(null==activity||activity.isFinishing()){
            if(null!=listener) listener.onError(AdConstance.CODE_ACTIVITY_INVALID, getText(AdConstance.CODE_ACTIVITY_INVALID), null);
            return;
        }
        this.mRewardVideoListener=listener;
        if(null==mGMRewardAd){
            if(null!=mRewardVideoListener) mRewardVideoListener.onError(AdConstance.CODE_ADINFO_INVALID, getText(AdConstance.CODE_ADINFO_INVALID), null);
            return;
        }
        mGMRewardAd.showRewardAd(activity);
    }

    public void onResetReward(){
        mRewardVideoListener =null;
        if (mGMRewardAd != null) {
            mGMRewardAd.destroy();
            mGMRewardAd = null;
        }
    }

    /**
     * 是否存在激励视频缓存
     * @return
     */
    public boolean hasRewardCacheAd() {
        return null!=mGMRewardAd && mGMRewardAd.isReady();
    }

    public void setOnRewardVideoListener(OnRewardVideoListener listener) {
        this.mRewardVideoListener = listener;
    }

    /**
     * 加载激励视频广告
     * @param context Activity类型上下文
     * @param id 广告位ID
     * @param listener 状态监听器，如果监听器为空内部回自动缓存一条激励视频广告
     */
    public void loadRewardVideo(Activity context,String id, OnRewardVideoListener listener){
        loadRewardVideo(context,id,AdConstance.SCENE_CACHE,listener);
    }

    /**
     * 加载激励视频广告
     * @param context Activity类型上下文
     * @param id 广告位ID
     * @param scene 广告展示场景
     * @param listener 状态监听器，如果监听器为空内部回自动缓存一条激励视频广告
     */
    public void loadRewardVideo(Activity context, String id, String scene, OnRewardVideoListener listener) {
        loadRewardVideo(context,id,scene,"reward",listener);
    }

    /**
     * 加载激励视频广告
     * @param context Activity类型上下文
     * @param id 广告位ID
     * @param scene 广告展示场景
     * @param rewardName 激励名称
     * @param listener 状态监听器，如果监听器为空内部回自动缓存一条激励视频广告
     */
    public void loadRewardVideo(Activity context, String id, String scene, String rewardName,OnRewardVideoListener listener) {
        Logger.d("loadRewardVideo-->id:"+id+",scene:"+scene+",reward:"+rewardName);
        if(null==context||context.isFinishing()){
            if(null!=listener) listener.onError(AdConstance.CODE_ACTIVITY_INVALID, getText(AdConstance.CODE_ACTIVITY_INVALID), id);
            return;
        }
        if(TextUtils.isEmpty(id)){
            if(null!=listener) listener.onError(AdConstance.CODE_ID_UNKNOWN, getText(AdConstance.CODE_ID_UNKNOWN), id);
            return;
        }
        this.mRewardVideoListener=listener;
        if(null!=mGMRewardAd&&mGMRewardAd.isReady()){
            Logger.d("loadRewardVideo-->"+getText(AdConstance.CODE_EXIST_CACHE));
            if(null!=mRewardVideoListener){
                mRewardVideoListener.onSuccess(mGMRewardAd);
            }
            return;
        }
        this.mVideoCode=id;this.mVideoScene=scene;
        if(null==listener&&!GMMediationAdSdk.configLoadSuccess()){
            Logger.e("loadRewardVideo-->config loading");
            if(null!=listener) listener.onError(AdConstance.CODE_CONFIG_LOADING, getText(AdConstance.CODE_CONFIG_LOADING), id);
            return;
        }
        if(isConfigAvailable(listener,id,"loadRewardVideo")){
            mGMRewardAd = new GMRewardAd(context, id);
            mGMRewardAd.loadAd(buildRewardConfig(rewardName), new GMRewardedAdLoadCallback() {
                @Override
                public void onRewardVideoLoadFail(AdError adError) {
                    Logger.e("loadRewardVideo-->error,code:"+adError.code+",message:"+adError.message);
                    event(mVideoScene, AdConstance.TYPE_REWARD_VIDEO,mVideoCode, AdConstance.STATUS_LOADED_ERROR, adError.code,adError.message);
                    OnRewardVideoListener listener=mRewardVideoListener;
                    onResetReward();
                    if(null!=listener) listener.onError(adError.code,adError.message, mVideoCode);
                }

                @Override
                public void onRewardVideoAdLoad() {
                    Logger.d("loadRewardVideo-->loaded");
                    printLoadAdInfo(); //打印已经加载广告的信息
                    if(null!=mGMRewardAd){
                        GMRewardedAdListener adListener = new GMRewardedAdListener() {
                            @Override
                            public void onRewardedAdShow() {
//                                Logger.d("loadRewardVideo-->onRewardedAdShow");
                                if(null!= mRewardVideoListener){
                                    if(null!=mGMRewardAd){
                                        mRewardVideoListener.onShow(getEcpm());
                                    }else{
                                        mRewardVideoListener.onShow("0");
                                    }
                                }
                            }

                            @Override
                            public void onRewardedAdShowFail(com.bytedance.msdk.api.AdError adError) {
                                Logger.e("loadRewardVideo-->showError,code:"+adError.code+",message:"+adError.message);
                                event(mVideoScene, AdConstance.TYPE_SPLASH,mVideoCode, AdConstance.STATUS_SHOW_ERROR,adError.code,adError.message);
                                OnRewardVideoListener listener=mRewardVideoListener;
                                onResetReward();
                                if(null!= listener) listener.onError(adError.code,adError.message, mSplashCode);

                            }

                            @Override
                            public void onRewardClick() {
//                                Logger.d("loadRewardVideo-->onRewardClick");
                                if(null!=mRewardVideoListener){
                                    mRewardVideoListener.onClick();
                                }
                            }

                            @Override
                            public void onRewardedAdClosed() {
//                                Logger.d("loadRewardVideo-->onRewardedAdClosed");
                                OnRewardVideoListener listener=mRewardVideoListener;
                                onResetReward();
                                if(null!=listener) listener.onClose();
                            }

                            @Override
                            public void onVideoComplete() {
//                                Logger.d("loadRewardVideo-->onVideoComplete");
                            }

                            @Override
                            public void onVideoError() {
//                                Logger.e("loadRewardVideo-->onVideoError");
                            }

                            @Override
                            public void onRewardVerify(RewardItem rewardItem) {
//                                Logger.d("loadRewardVideo-->rewardItem");
                                if(null!= mRewardVideoListener){
                                    mRewardVideoListener.onRewardVerify();
                                }
                            }

                            @Override
                            public void onSkippedVideo() {
//                                Logger.d("loadRewardVideo-->onSkippedVideo");
                            }
                        };
                        mGMRewardAd.setRewardAdListener(adListener);
                        //穿山甲再看一次监听
                        //mGMRewardAd.setRewardPlayAgainListener(adListener);
                        event(mVideoScene, AdConstance.TYPE_REWARD_VIDEO,mVideoCode, AdConstance.STATUS_LOADED_SUCCESS, 0,null);
                        if(null!=mRewardVideoListener){
                            mRewardVideoListener.onSuccess(mGMRewardAd);
                        }
                    }else{
                        OnRewardVideoListener listener=mRewardVideoListener;
                        onResetReward();
                        if(null!= listener) listener.onError(AdConstance.CODE_ADINFO_INVALID,getText(AdConstance.CODE_ADINFO_INVALID),mVideoCode);
                    }
                }

                @Override
                public void onRewardVideoCached() {
//                    Logger.d("loadRewardVideo-->onRewardVideoCached:");
                }
            });
        }
    }

    /**
     * 返回当前广告的ECPM
     * @return
     */
    public String getEcpm(){
        if(null!=mGMRewardAd){
            GMAdEcpmInfo gmAdShowEcpmInfo = mGMRewardAd.getShowEcpm();
            String ecpm  = null;
            if (null != gmAdShowEcpmInfo) {
                ecpm = gmAdShowEcpmInfo.getPreEcpm();
            }
            return ecpm;
        }
        return "0";
    }

    private GMAdSlotRewardVideo buildRewardConfig(String rewardName) {
        GMAdSlotRewardVideo adSlotRewardVideo = new GMAdSlotRewardVideo.Builder()
                .setMuted(true)//对所有SDK的激励广告生效，除需要在平台配置的SDK，如穿山甲SDK
                .setVolume(0f)//配合Admob的声音大小设置[0-1]
                .setGMAdSlotGDTOption(GMAdOptionUtil.getGMAdSlotGDTOption().build())
                .setGMAdSlotBaiduOption(GMAdOptionUtil.getGMAdSlotBaiduOption().build())
                //.setCustomData(customData)
                .setRewardName(rewardName) //奖励的名称
                .setRewardAmount(3)  //奖励的数量
                .setUserID("888888")//用户id,必传参数
                .setUseSurfaceView(false)
                .setOrientation(GMAdConstant.VERTICAL)//必填参数，期望视频的播放方向：GMAdConstant.HORIZONTAL 或 GMAdConstant.VERTICAL
                .setBidNotify(true)//开启bidding比价结果通知，默认值为false
                .build();
        return  adSlotRewardVideo;
    }

    //打印已经加载广告的信息
    private void printLoadAdInfo() {
        if (mGMRewardAd == null) {
            return;
        }
//        /**
//         * 获取已经加载的clientBidding ，多阶底价广告的相关信息
//         */
//        List<GMAdEcpmInfo> gmAdEcpmInfos = mGMRewardAd.getMultiBiddingEcpm();
//        if (gmAdEcpmInfos != null) {
//            for (GMAdEcpmInfo info : gmAdEcpmInfos) {
//                Logger.e("***多阶+client相关信息*** AdNetworkPlatformId" + info.getAdNetworkPlatformId()
//                        + "  AdNetworkRitId:" + info.getAdNetworkRitId()
//                        + "  ReqBiddingType:" + info.getReqBiddingType()
//                        + "  PreEcpm:" + info.getPreEcpm()
//                        + "  LevelTag:" + info.getLevelTag()
//                        + "  ErrorMsg:" + info.getErrorMsg()
//                        + "  request_id:" + info.getRequestId()
//                        + "  SdkName:" + info.getAdNetworkPlatformName()
//                        + "  CustomSdkName:" + info.getCustomAdNetworkPlatformName());
//            }
//        }
//
//        /**
//         * 获取实时填充/缓存池中价格最优的代码位信息即相关价格信息，每次只有一个信息
//         */
//        GMAdEcpmInfo gmAdEcpmInfo = mGMRewardAd.getBestEcpm();
//        if (gmAdEcpmInfo != null) {
//            Logger.e("***实时填充/缓存池中价格最优的代码位信息*** AdNetworkPlatformId" + gmAdEcpmInfo.getAdNetworkPlatformId()
//                    + "  AdNetworkRitId:" + gmAdEcpmInfo.getAdNetworkRitId()
//                    + "  ReqBiddingType:" + gmAdEcpmInfo.getReqBiddingType()
//                    + "  PreEcpm:" + gmAdEcpmInfo.getPreEcpm()
//                    + "  LevelTag:" + gmAdEcpmInfo.getLevelTag()
//                    + "  ErrorMsg:" + gmAdEcpmInfo.getErrorMsg()
//                    + "  request_id:" + gmAdEcpmInfo.getRequestId()
//                    + "  SdkName:" + gmAdEcpmInfo.getAdNetworkPlatformName()
//                    + "  CustomSdkName:" + gmAdEcpmInfo.getCustomAdNetworkPlatformName());
//        }
//
//        /**
//         * 获取获取当前缓存池的全部信息
//         */
//        List<GMAdEcpmInfo> gmCacheInfos = mGMRewardAd.getCacheList();
//        if (gmCacheInfos != null) {
//            for (GMAdEcpmInfo info : gmCacheInfos) {
//                Logger.e( "***缓存池的全部信息*** AdNetworkPlatformId" + info.getAdNetworkPlatformId()
//                        + "  AdNetworkRitId:" + info.getAdNetworkRitId()
//                        + "  ReqBiddingType:" + info.getReqBiddingType()
//                        + "  PreEcpm:" + info.getPreEcpm()
//                        + "  LevelTag:" + info.getLevelTag()
//                        + "  ErrorMsg:" + info.getErrorMsg()
//                        + "  request_id:" + info.getRequestId()
//                        + "  SdkName:" + info.getAdNetworkPlatformName()
//                        + "  CustomSdkName:" + info.getCustomAdNetworkPlatformName());
//            }
//        }
    }

    //=============================================插屏============================================

    public void onResetInsert() {
        if (mInterstitialAd != null) {
            mInterstitialAd.destroy();
            mInterstitialAd = null;
        }
        mInsertListener = null;
    }

    public boolean hasInsertAd() {
        return null != mInterstitialAd && mInterstitialAd.isReady();
    }

    public void showInsertAd(Activity activity,OnTabScreenListener listener) {
        mInsertListener = listener;
        try {
            if(null!=mInterstitialAd&&null!=activity&&!activity.isFinishing()){
                mInterstitialAd.showAd(activity);
            }else{
                if(null!=listener){
                    if(null!= mInsertListener) mInsertListener.onError(AdConstance.CODE_ACTIVITY_INVALID,getText(AdConstance.CODE_ACTIVITY_INVALID),mInsertCode);
                }
                onResetInsert();
            }
        }catch (Throwable e){
            e.printStackTrace();
            if(null!=listener){
                listener.onError(0,e.getMessage(),null);
            }
            onResetInsert();
        }
    }

    /**
     * 加载插屏广告
     * @param id 广告位ID
     * @param listener 状态监听器
     */
    public void loadInsert (Activity activity, String id, OnTabScreenListener listener) {
        loadInsert(activity,id,AdConstance.SCENE_CACHE,listener);
    }

    /**
     * 加载插屏广告
     * @param context Activity类型上下文
     * @param id 广告位ID
     * @param scene 广告播放的场景
     * @param listener 状态监听器
     */
    public void loadInsert (Activity context, String id,String scene, OnTabScreenListener listener) {
        Logger.d("loadInsert-->id:"+id+",scene:"+scene);
        if(null==context||context.isFinishing()){
            if(null!=listener) listener.onError(AdConstance.CODE_ACTIVITY_INVALID, getText(AdConstance.CODE_ACTIVITY_INVALID), id);
            return;
        }
        if(TextUtils.isEmpty(id)){
            if(null!=listener) listener.onError(AdConstance.CODE_ID_UNKNOWN, getText(AdConstance.CODE_ID_UNKNOWN), id);
            return;
        }
        this.mInsertListener =listener;
        if(null!=mInterstitialAd){
            Logger.d("loadInsert-->"+getText(AdConstance.CODE_EXIST_CACHE));
            if(null!= mInsertListener){
                mInsertListener.onSuccess(mInterstitialAd);
            }
            return;
        }
        this.mInsertCode=id;this.mInsertScene=scene;
        if(null==listener&&!GMMediationAdSdk.configLoadSuccess()){
            Logger.e("loadInsert-->config loading");
            if(null!=listener) listener.onError(AdConstance.CODE_CONFIG_LOADING, getText(AdConstance.CODE_CONFIG_LOADING), id);
            return;
        }
        if(isConfigAvailable(listener,id,"loadInsert")){
            mInterstitialAd = new GMInterstitialFullAd(context,id);
            GMAdSlotInterstitialFull adSlotInterstitialFull = new GMAdSlotInterstitialFull.Builder()
                    .setGMAdSlotBaiduOption(GMAdOptionUtil.getGMAdSlotBaiduOption().build())
                    .setGMAdSlotGDTOption(GMAdOptionUtil.getGMAdSlotGDTOption().build())
                    .setImageAdSize(600, 600)  //设置宽高 （插全屏类型下_插屏广告使用）
                    .setVolume(0.5f) //admob 声音配置，与setMuted配合使用
                    .setUserID("user123")//用户id,必传参数 (插全屏类型下_全屏广告使用)
//                    .setCustomData(customData)
                    .setRewardName("reward") //奖励的名称
                    .setRewardAmount(3)  //奖励的数量
                    .setOrientation(GMAdConstant.VERTICAL)//必填参数，期望视频的播放方向：TTAdConstant.HORIZONTAL 或 TTAdConstant.VERTICAL; (插全屏类型下_全屏广告使用)
                    .setBidNotify(true)//开启bidding比价结果通知，默认值为false
                    .build();
            //请求广告，调用插屏广告异步请求接口
            mInterstitialAd.loadAd(adSlotInterstitialFull, new GMInterstitialFullAdLoadCallback() {

                @Override
                public void onInterstitialFullLoadFail(com.bytedance.msdk.api.AdError adError) {
                    Logger.e("loadInsert-->error,code:"+adError.code+",message:"+adError.message);
                    event(mInsertScene, AdConstance.TYPE_INSERT,mInsertCode, AdConstance.STATUS_LOADED_ERROR, adError.code,adError.message);
                    OnTabScreenListener listener= mInsertListener;
                    onResetInsert();
                    if(null!=listener) listener.onError(adError.code,adError.message, mInsertCode);
                }

                @Override
                public void onInterstitialFullAdLoad() {
                    Logger.d( "loadInsert-->loaded");
                    ThreadHelper.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(null!=mInterstitialAd) {
                                mInterstitialAd.setAdInterstitialFullListener(new GMInterstitialFullAdListener() {

                                    @Override
                                    public void onInterstitialFullShow() {
//                                        Logger.d("loadInsert-->onInterstitialAdShow");
                                        event(mInsertScene, AdConstance.TYPE_INSERT,mInsertCode, AdConstance.STATUS_SHOW_SUCCESS, 0,null);
                                        if(null!=mInsertListener){
                                            mInsertListener.onShow();
                                        }
                                    }

                                    @Override
                                    public void onInterstitialFullShowFail(com.bytedance.msdk.api.AdError adError) {
                                        Logger.e( "loadInsert-->showError,code:"+adError.code+",msg:"+adError.message);
                                        event(mInsertScene, AdConstance.TYPE_INSERT,mInsertCode, AdConstance.STATUS_SHOW_ERROR, adError.code,adError.message);
                                        OnTabScreenListener listener= mInsertListener;
                                        onResetInsert();
                                        if(null!=listener) listener.onError(adError.code,adError.message, mInsertCode);
                                    }

                                    @Override
                                    public void onInterstitialFullClick() {
//                                        Logger.d( "loadInsert-->onInterstitialFullClick");
                                        if(null!= mInsertListener){
                                            mInsertListener.onClick();
                                        }
                                    }

                                    @Override
                                    public void onInterstitialFullClosed() {
//                                        Logger.d( "loadInsert-->onInterstitialFullClosed");
                                        OnTabScreenListener listener=mInsertListener;
                                        onResetInsert();
                                        if(null!=listener) listener.onClose();

                                    }

                                    @Override
                                    public void onVideoComplete() {
//                                        Logger.d( "loadInsert-->onVideoComplete");
                                    }

                                    @Override
                                    public void onVideoError() {
//                                        Logger.d( "loadInsert-->onVideoError");
                                    }

                                    @Override
                                    public void onSkippedVideo() {
//                                        Logger.d( "loadInsert-->onSkippedVideo");
                                    }

                                    @Override
                                    public void onAdOpened() {
//                                        Logger.d( "loadInsert-->onAdOpened");
                                    }

                                    @Override
                                    public void onAdLeftApplication() {
//                                        Logger.d( "loadInsert-->onAdLeftApplication");
                                    }

                                    @Override
                                    public void onRewardVerify(RewardItem rewardItem) {
//                                        Logger.d( "loadInsert-->onRewardVerify");
                                    }
                                });
                                event(mInsertScene, AdConstance.TYPE_INSERT,mInsertCode, AdConstance.STATUS_LOADED_SUCCESS, 0,null);
                                if(null!= mInsertListener){
                                    mInsertListener.onSuccess(mInterstitialAd);
                                }
                            }else{
                                OnTabScreenListener listener= mInsertListener;
                                onResetInsert();
                                if(null!= listener) listener.onError(AdConstance.CODE_ADINFO_INVALID,getText(AdConstance.CODE_ADINFO_INVALID),mInsertCode);
                            }
                        }
                    });
                }

                @Override
                public void onInterstitialFullCached() {
//                    Logger.d( "loadAdvInsert-->onInterstitialFullCached"+",thread:"+Thread.currentThread().getName());
//                    if(null!= mInsertListener){
//                        mInsertListener.onSuccess(mInterstitialAd);
//                    }
                }
            });
        }
    }

    //=============================================信息流============================================

    /**
     * 加载信息流广告
     * @param context Activity类型上下文
     * @param id 广告位ID
     * @param listener
     */
    public void loadStream(Activity context, String id,OnExpressListener listener){
        loadStream(context, id,1,listener);
    }

    /**
     * 加载信息流广告
     * @param context Activity类型上下文
     * @param id 广告位ID
     * @param adCount 预期加载的广告数量
     * @param listener
     */
    public void loadStream(Activity context, String id, int adCount, OnExpressListener listener){
        loadStream(context, id,AdConstance.SCENE_CACHE,adCount,listener);
    }

    /**
     * 加载信息流广告
     * @param context Activity类型上下文
     * @param id 广告位ID
     * @param scene 广告加载场景
     * @param adCount 预期加载的广告数量
     * @param listener
     */
    public void loadStream(Activity context, String id, String scene,int adCount, OnExpressListener listener){
        loadStream(context, id,scene,adCount,PlatformUtils.getInstance().getScreenWidthDP()-32f,0,listener);
    }

    /**
     * 加载信息流广告
     * @param context Activity类型上下文
     * @param id 广告位ID
     * @param scene 广告加载场景
     * @param adCount 预期加载的广告数量
     * @param adWidth 预期加载并渲染的信息流宽度，单位dp
     * @param adHeight 预期加载并渲染的信息流高度，单位dp，为0时自适应高度
     * @param listener
     */
    public void loadStream(Activity context, String id, String scene,int adCount,float adWidth, float adHeight, OnExpressListener listener) {
        Logger.d("loadStream-->id:"+id+",scene:"+scene+",adWidth:"+adWidth+",adHeight:"+adHeight+",adCount:"+adCount);
        if(null==context||context.isFinishing()){
            if(null!=listener) listener.onError(AdConstance.CODE_ACTIVITY_INVALID, getText(AdConstance.CODE_ACTIVITY_INVALID), id);
            return;
        }
        if(TextUtils.isEmpty(id)){
            if(null!=listener) listener.onError(AdConstance.CODE_ID_UNKNOWN, getText(AdConstance.CODE_ID_UNKNOWN), id);
            return;
        }
        if(null==listener&&!GMMediationAdSdk.configLoadSuccess()){
            Logger.e("loadStream-->config loading");
            if(null!=listener) listener.onError(AdConstance.CODE_CONFIG_LOADING, getText(AdConstance.CODE_CONFIG_LOADING), id);
            return;
        }
        if(isConfigAvailable(listener,id,"loadStream")){
            new Stream().loadStream(context,id,scene,adCount,adWidth,adHeight,listener);
        }
    }

    private class Stream {

        private OnExpressListener mOnExpressListener;
        private String mAdCode,mScene;
        private GMUnifiedNativeAd mNativeExpressAD;

        public void loadStream(Activity context, String id, String scene, int adCount, float adWidth, float adHeight, OnExpressListener listener) {
            this.mOnExpressListener =listener;
            mNativeExpressAD = new GMUnifiedNativeAd(context, id);//模板视频
            // 针对Gdt Native自渲染广告，可以自定义gdt logo的布局参数。该参数可选,非必须。
            FrameLayout.LayoutParams gdtNativeAdLogoParams =
                    new FrameLayout.LayoutParams(PlatformUtils.getInstance().dpToPxInt(adWidth), PlatformUtils.getInstance().dpToPxInt(adHeight), Gravity.RIGHT | Gravity.TOP); // 例如，放在右上角

            GMAdSlotGDTOption.Builder adSlotNativeBuilder = GMAdOptionUtil.getGMAdSlotGDTOption()
                    .setNativeAdLogoParams(gdtNativeAdLogoParams);

            /**
             * 创建feed广告请求类型参数GMAdSlotNative,具体参数含义参考文档
             * 备注
             * 1: 如果是信息流自渲染广告，设置广告图片期望的图片宽高 ，不能为0
             * 2:如果是信息流模板广告，宽度设置为希望的宽度，高度设置为0(0为高度选择自适应参数)
             */
            GMAdSlotNative adSlotNative = new GMAdSlotNative.Builder()
                    .setGMAdSlotBaiduOption(GMAdOptionUtil.getGMAdSlotBaiduOption().build())//百度相关的配置
                    .setGMAdSlotGDTOption(adSlotNativeBuilder.build())//gdt相关的配置
                    .setAdmobNativeAdOptions(GMAdOptionUtil.getAdmobNativeAdOptions())//admob相关配置
                    .setAdStyleType(GMAdConstant.TYPE_EXPRESS_AD)//必传，表示请求的模板广告还是原生广告，AdSlot.TYPE_EXPRESS_AD：模板广告 ； AdSlot.TYPE_NATIVE_AD：原生广告
                    // 备注
                    // 1:如果是信息流自渲染广告，设置广告图片期望的图片宽高 ，不能为0
                    // 2:如果是信息流模板广告，宽度设置为希望的宽度，高度设置为0(0为高度选择自适应参数)
                    .setImageAdSize((int)adWidth, (int)adHeight)// 必选参数 单位dp ，详情见上面备注解释
                    .setAdCount(adCount)//请求广告数量为1到3条
                    .setBidNotify(true)//开启bidding比价结果通知，默认值为false
                    .build();

            this.mAdCode=id;this.mScene=scene;
            //请求广告，调用feed广告异步请求接口，加载到广告后，拿到广告素材自定义渲染
            /**
             * 注：每次加载信息流广告的时候需要新建一个GMUnifiedNativeAd，否则可能会出现广告填充问题
             * (例如：mTTAdNative = new GMUnifiedNativeAd(this, mAdUnitId);）
             */
            mNativeExpressAD.loadAd(adSlotNative, new GMNativeAdLoadCallback() {
                @Override
                public void onAdLoaded(List<GMNativeAd> list) {
                    Logger.d("loadStream->loaded");
                    if(null!= mOnExpressListener){
                        if(null != list && list.size()>0){
                            mOnExpressListener.onSuccessExpressed(list.get(0));
                        }else{
                            error(AdConstance.CODE_AD_EMPTY,getText(AdConstance.CODE_AD_EMPTY));
                        }
                    }
                }

                @Override
                public void onAdLoadedFail(com.bytedance.msdk.api.AdError adError) {
                    error(adError.code,adError.message);
                }
            });
        }

        private void error(int code,String error) {
            Logger.e("loadStream-->error,code:" + code + ",error:"+error);
            event(mScene, AdConstance.TYPE_STREAM,mAdCode, AdConstance.STATUS_LOADED_ERROR,code,error);
            OnExpressListener listener= mOnExpressListener;
            mNativeExpressAD=null;
            mOnExpressListener =null;
            if(null!=listener) listener.onError(code,error, mAdCode);
        }
    }

    //=============================================Banner============================================
    /**
     * 加载Banner广告
     * @param context Activity类型上下文
     * @param id 广告位ID
     * @param listener
     */
    public void loadBanner(Activity context, String id,OnExpressListener listener) {
        loadBanner(context,id,AdConstance.SCENE_CACHE,listener);
    }

    /**
     * 加载Banner广告
     * @param context Activity类型上下文
     * @param id 广告位ID
     * @param scene 广告加载场景
     * @param listener
     */
    public void loadBanner(Activity context, String id,String scene,OnExpressListener listener) {
        loadBanner(context,id,scene,PlatformUtils.getInstance().getScreenWidthDP(),0,listener);
    }

    /**
     * 加载Banner广告
     * @param context Activity类型上下文
     * @param id 广告位ID
     * @param scene 广告加载场景
     * @param adWidth 预期加载并渲染的信息流宽度，单位dp
     * @param adHeight 预期加载并渲染的信息流高度，单位dp，为0时自适应高度
     * @param listener
     */
    public void loadBanner(Activity context, String id, String scene,float adWidth, float adHeight, OnExpressListener listener) {
        Logger.d("loadBanner-->id:"+id+",scene:"+scene+",width:"+adWidth+",height:"+adHeight);
        if(null==context||context.isFinishing()){
            if(null!=listener) listener.onError(AdConstance.CODE_ACTIVITY_INVALID, getText(AdConstance.CODE_ACTIVITY_INVALID), id);
            return;
        }
        if(TextUtils.isEmpty(id)){
            if(null!=listener) listener.onError(AdConstance.CODE_ID_UNKNOWN, getText(AdConstance.CODE_ID_UNKNOWN), id);
            return;
        }
        if(null==listener&&!GMMediationAdSdk.configLoadSuccess()){
            Logger.e("loadBanner-->config loading");
            if(null!=listener) listener.onError(AdConstance.CODE_CONFIG_LOADING, getText(AdConstance.CODE_CONFIG_LOADING), id);
            return;
        }
        if(isConfigAvailable(listener,id,"loadBanner")){
            new Banner().loadAdvBanner(context,id,scene,adWidth,adHeight,listener);
        }
    }

    private class Banner {

        private OnExpressListener mOnExpressListener;
        private String mAdCode,mScene;

        public void loadAdvBanner(Activity context, String id,String scene, float adWidth, float adHeight, OnExpressListener listener) {
            this.mOnExpressListener =listener;
            if (mBannerViewAd != null) {
                mBannerViewAd.destroy();
                mBannerViewAd=null;
            }
            this.mAdCode=id;this.mScene=scene;
            //注：每次加载banner的时候需要新建一个GMBannerAd，一个广告对象只能load一次，banner广告对象getBannerView只能一次，第二次调用会返回空
            mBannerViewAd = new GMBannerAd(context, id);
            //设置广告事件监听
            mBannerViewAd.setAdBannerListener(new GMBannerAdListener() {
                @Override
                public void onAdOpened() {
//                    Logger.d("Banner-loadBanner-->onAdOpened:");
                }

                @Override
                public void onAdLeftApplication() {
//                    Logger.d("Banner-loadBanner-->onAdLeftApplication:");
                }

                @Override
                public void onAdClosed() {
//                    Logger.d("Banner-loadBanner-->onAdClosed:");
                    if (null != mOnExpressListener) {
                        mOnExpressListener.onClose();
                    }
                }

                @Override
                public void onAdClicked() {
//                    Logger.d("Banner-loadBanner-->onAdClicked:");
                    if (null != mOnExpressListener) {
                        mOnExpressListener.onClick();
                    }
                }

                @Override
                public void onAdShow() {
//                    Logger.d("Banner-loadBanner-->onAdShow:");
                    event(mScene, AdConstance.TYPE_BANNER,mAdCode, AdConstance.STATUS_SHOW_SUCCESS, 0,null);
                }

                @Override
                public void onAdShowFail(com.bytedance.msdk.api.AdError adError) {
                    Logger.e("loadBanner-->showError:code:"+adError.code+",message:"+adError.message);
                    event(mScene, AdConstance.TYPE_BANNER,mAdCode, AdConstance.STATUS_SHOW_ERROR,adError.code,adError.message);
                    OnExpressListener listener= mOnExpressListener;
                    mOnExpressListener =null;
                    if(null!=listener) listener.onError(adError.code,adError.message, mAdCode);

                }
            });
            //设置广告配置
            GMAdSlotBanner slotBanner = new GMAdSlotBanner.Builder()
                    .setBannerSize(GMAdSize.BANNER_CUSTOME)
                    .setImageAdSize((int)adWidth,(int)adHeight)// GMAdSize.BANNER_CUSTOME可以调用setImageAdSize设置大小,这里传入实际预期渲染的宽高(单位:DP)
//                .setRefreshTime(30) // 从v3100版本开始，不支持sdk端设置banner轮播时间，只能从GroMore平台进行配置。sdk端设置无效。
                    .setAllowShowCloseBtn(true)//如果广告本身允许展示关闭按钮，这里设置为true就是展示。注：目前只有mintegral支持。
                    .setBidNotify(true)//开启bidding比价结果通知，默认值为false
                    .build();
            //请求广告，对请求回调的广告作渲染处理
            mBannerViewAd.loadAd(slotBanner, new GMBannerAdLoadCallback() {

                @Override
                public void onAdFailedToLoad(com.bytedance.msdk.api.AdError adError) {
                    error(adError.code, adError.message);
                }

                @Override
                public void onAdLoaded() {
                    Logger.d("loadBanner-->loaded");
                    if(null!= mOnExpressListener){
                        if(null!=mBannerViewAd){
                            mOnExpressListener.onSuccessBanner(mBannerViewAd);
                        }else{
                            error(AdConstance.CODE_AD_EMPTY,getText(AdConstance.CODE_AD_EMPTY));
                        }
                    }
                }
            });
        }

        private void error(int code,String error) {
            Logger.e("loadBanner-->error,code:" + code + ",error:"+error);
            event(mScene, AdConstance.TYPE_BANNER,mAdCode, AdConstance.STATUS_LOADED_ERROR,code,error);
            OnExpressListener listener= mOnExpressListener;
            mOnExpressListener =null;
            if(null!=listener) listener.onError(code,error, mAdCode);
        }
    }

    public Activity getTempActivity() {
        return mCurrentActivity;
    }

    public void setCurrentActivity(Activity currentActivity) {
        mCurrentActivity = currentActivity;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {}

    @Override
    public void onActivityStarted(Activity activity) {
        this.mCurrentActivity=activity;
    }

    @Override
    public void onActivityResumed(Activity activity) {
        this.mCurrentActivity=activity;
    }

    @Override
    public void onActivityPaused( Activity activity) {}

    @Override
    public void onActivityStopped( Activity activity) {}

    @Override
    public void onActivitySaveInstanceState( Activity activity,  Bundle bundle) {}

    @Override
    public void onActivityDestroyed( Activity activity) {}

    public void onTerminate(Application context){
        context.registerActivityLifecycleCallbacks(this);
    }
}