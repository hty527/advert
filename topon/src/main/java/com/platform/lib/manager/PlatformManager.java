package com.platform.lib.manager;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.anythink.banner.api.ATBannerListener;
import com.anythink.banner.api.ATBannerView;
import com.anythink.core.api.ATAdConst;
import com.anythink.core.api.ATAdInfo;
import com.anythink.core.api.ATAdStatusInfo;
import com.anythink.core.api.ATInitConfig;
import com.anythink.core.api.ATNetworkConfig;
import com.anythink.core.api.ATSDK;
import com.anythink.core.api.AdError;
import com.anythink.interstitial.api.ATInterstitial;
import com.anythink.interstitial.api.ATInterstitialAutoAd;
import com.anythink.interstitial.api.ATInterstitialAutoEventListener;
import com.anythink.interstitial.api.ATInterstitialAutoLoadListener;
import com.anythink.interstitial.api.ATInterstitialListener;
import com.anythink.nativead.api.ATNative;
import com.anythink.nativead.api.ATNativeAdView;
import com.anythink.nativead.api.ATNativeDislikeListener;
import com.anythink.nativead.api.ATNativeEventListener;
import com.anythink.nativead.api.ATNativeNetworkListener;
import com.anythink.nativead.api.NativeAd;
import com.anythink.network.gdt.GDTATConst;
import com.anythink.network.toutiao.TTATConst;
import com.anythink.rewardvideo.api.ATRewardVideoAd;
import com.anythink.rewardvideo.api.ATRewardVideoAutoAd;
import com.anythink.rewardvideo.api.ATRewardVideoAutoEventListener;
import com.anythink.rewardvideo.api.ATRewardVideoAutoLoadListener;
import com.anythink.rewardvideo.api.ATRewardVideoListener;
import com.anythink.splashad.api.ATSplashAd;
import com.anythink.splashad.api.ATSplashAdExtraInfo;
import com.anythink.splashad.api.ATSplashAdListener;
import com.platform.lib.constants.AdConstance;
import com.platform.lib.listener.OnEventListener;
import com.platform.lib.listener.OnExpressListener;
import com.platform.lib.listener.OnInitListener;
import com.platform.lib.listener.OnRewardVideoListener;
import com.platform.lib.listener.OnSplashListener;
import com.platform.lib.listener.OnTabScreenListener;
import com.platform.lib.utils.InitHelper;
import com.platform.lib.utils.Logger;
import com.platform.lib.utils.PlatformPreferences;
import com.platform.lib.utils.PlatformUtils;
import com.qq.e.ads.nativ.ADSize;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * created by hty
 * 2022/9/27
 * Desc:??????TopOn?????????????????????????????????
 *
 * 1??????????????????SDK{@link #initSdk(Context context, String appId, String appSecrecy , OnInitListener listener)}
 * 2???????????????????????????????????????????????????{@link #setDevelop(boolean)}???true???????????????????????????
 * 3???????????????????????????????????????????????????{@link #setOnEventListener(OnEventListener)}
 *
 * ????????????????????????????????????
 * 1????????????????????????SplashView
 * 2????????????????????????????????????PlayerManager?????????api
 * 3??????????????????????????????InsertManager?????????api
 * 4????????????????????????banner?????????????????????ExpressView???
 * ?????????api
 * 1????????????{@link #loadSplash(String id, OnSplashListener listener)}
 * 2??????????????????{@link #loadRewardVideo(String id, OnRewardVideoListener listener)}
 * 3???????????????????????????{@link #showAutoRewardVideo(Activity activity, String id, String scene, OnRewardVideoListener listener)}
 * 4????????????{@link #loadInsert(String id, OnTabScreenListener listener)}
 * 5?????????????????????{@link #showAutoInsert(Activity activity, String id, String scene, OnTabScreenListener listener)}
 * 6???????????????{@link #loadStream(Context context, String id, OnExpressListener listener)}
 * 7???banner???{@link #loadBanner(String id, ViewGroup viewGroup,OnExpressListener listener)}
 */
public final class PlatformManager implements Application.ActivityLifecycleCallbacks {

    private volatile static PlatformManager mInstance;
    private OnEventListener mAdvertEventListener;//??????????????????
    private boolean isDevelop =false;//??????????????????????????????????????????????????????????????????????????????????????????????????????????????????
    private String appId, appSecrecy;//APP_ID\APP_KEY\??????????????????
    private boolean DEBUG = false;
    private Map<Integer,String> mUIText=new HashMap<>();
    //????????????
    private OnRewardVideoListener mRewardVideoListener;
    private ATRewardVideoAd mAtRewardVideoAd;//??????????????????????????????
    //????????????
    private OnSplashListener mSplashListener;
    private ATSplashAd mAdSdkSplash;//??????????????????????????????
    //????????????
    private OnTabScreenListener mInsertListener;
    private ATInterstitial mInterstitialAD;//??????????????????????????????
    //??????\????????????\??????\?????????????????????\??????????????? ???????????????????????????ID
    private String mSplashScene,mSplashCode,mVideoScene,mVideoCode,mVideoScene2,mVideoCode2,mInsertScene,mInsertCode,mInsertScene2,mInsertCode2;
    //????????????????????????????????????????????????
    private OnInitListener mOnRewardInitListener,mOnInsertInitListener;
    private boolean isExternalActivity=false;//Utils?????????Activity????????????????????????

    /**
     * ??????????????????
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

    //========================================??????????????????============================================

    /**
     * ??????SDK???????????????????????????????????????application????????????
     * @param context ??????????????????????????????Application
     * @param appId ?????? APP_ID(topon????????????)
     * @param appSecrecy ?????? APP_SECRECY(topon????????????)
     * @param listener ????????????????????????
     */
    public void initSdk(Context context, String appId, String appSecrecy , OnInitListener listener){
        initSdk(context,appId,appSecrecy,null,false,listener);
    }

    /**
     * ??????SDK???????????????????????????????????????application????????????
     * @param context ??????????????????????????????Application
     * @param appId ?????? APP_ID(topon????????????)
     * @param appSecrecy ?????? APP_SECRECY(topon????????????)
     * @param channel ????????????
     * @param debug ??????debug????????????????????????debug??????????????????logcat??????
     * @param listener ????????????????????????
     */
    public void initSdk(Context context, String appId, String appSecrecy , String channel, boolean debug, OnInitListener listener){
        initSdk(context,appId,appSecrecy,channel,"develop",debug,listener);
    }

    /**
     * ??????SDK???????????????????????????????????????application????????????
     * @param context ??????????????????????????????Application
     * @param appId ?????? APP_ID(topon????????????)
     * @param appSecrecy ?????? APP_SECRECY(topon????????????)
     * @param channel ????????????
     * @param tag SDK??????
     * @param debug ??????debug????????????????????????debug??????????????????logcat??????
     * @param listener ????????????????????????
     */
    public void initSdk(Context context, String appId, String appSecrecy , String channel, String tag, boolean debug, OnInitListener listener){
        Logger.d("initSdk-->appId:" + appId + ",appSecrecy:" + appSecrecy + ",channel:" + channel + ",debug:" + debug);
        try {
            PlatformUtils.getInstance().setContext(context);
            PlatformPreferences.init(context, context.getPackageName() + ".key_info", Context.MODE_MULTI_PROCESS);
            setAppId(appId);
            setAppSecrecy(appSecrecy);
            if (null==context) {
                if(null!=listener) listener.onError(AdConstance.CODE_CONTEXT_INVALID, getText(AdConstance.CODE_CONTEXT_INVALID));
                return;
            }
            if (TextUtils.isEmpty(getAppId())) {
                if(null!=listener) listener.onError(AdConstance.CODE_APPID_INVALID, getText(AdConstance.CODE_APPID_INVALID));
                return;
            }
            if (TextUtils.isEmpty(getAppSecrecy())) {
                if(null!=listener) listener.onError(AdConstance.CODE_APPSECRECY_INVALID, getText(AdConstance.CODE_APPSECRECY_INVALID));
                return;
            }

            this.DEBUG = debug;
            ATSDK.setNetworkLogDebug(DEBUG);
            ATSDK.setAdLogoVisible(DEBUG);
            Logger.setDebug(DEBUG);
            //???????????????SDK
            List<ATInitConfig> sdkConfig=null;
            if(null!=listener){
                sdkConfig = listener.getSdkConfig();
            }
            if(null!=sdkConfig){
                ATNetworkConfig.Builder builder=new ATNetworkConfig.Builder();
                builder.withInitConfigList(sdkConfig);
                ATSDK.init(context, appId, appSecrecy,builder.build());
            }else{
                ATSDK.init(context, appId, appSecrecy);
            }
            if (!TextUtils.isEmpty(channel)) {
                ATSDK.setChannel(channel);
            }
            if(context instanceof Application){
                ((Application) context).registerActivityLifecycleCallbacks(this);
            }
            InitHelper.init(tag);
        }catch (Throwable e){
            e.printStackTrace();
        }finally {
            if(null!=listener) listener.onSuccess(appId);
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

    private String getAppSecrecy() {
        if (TextUtils.isEmpty(appSecrecy)){
            appSecrecy = PlatformPreferences.getInstance().getString("app_secrecy", null);
        }
        return appSecrecy;
    }

    private void setAppSecrecy(String appSecrecy) {
        this.appSecrecy = appSecrecy;
        PlatformPreferences.getInstance().putString("app_secrecy", appSecrecy);
    }

    private int parseErrorCode(AdError adError) {
        if(null==adError) return 0;
        if(null!=adError){
            return PlatformUtils.getInstance().parseInt(adError.getCode());
        }
        return 0;
    }

    private void event(String scene, String ad_type, String ad_id, String status, int error_code, String error_msg) {
        if(null!=mAdvertEventListener) mAdvertEventListener.onEvent(scene,ad_type,ad_id,status,error_code,error_msg);
    }


    /**
     * ????????????????????????????????????
     * @param eventListener
     */
    public PlatformManager setOnEventListener(OnEventListener eventListener) {
        mAdvertEventListener = eventListener;
        return mInstance;
    }

    /**
     * ??????SDK?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
     * @param develop true:??????????????? false:????????????
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
     * ????????????????????????????????????????????????Activity(?????????????????????????????????Activity?????????)
     * @param activity
     */
    public void setActivity(Activity activity){
        this.isExternalActivity=null!=activity;
        PlatformUtils.getInstance().setActivity(activity);
    }

    /**
     * ???topon??????source??????????????????source
     * @return
     */
    public String parseAdSource(int adSource) {
        if (15 == adSource) {
            adSource = 1;
        } else if (28 == adSource) {
            adSource = 5;
        } else if (8 == adSource) {
            adSource = 3;
        }
        return String.valueOf(adSource);
    }

    /**
     * ??????????????????
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

    //==========================================????????????==============================================

    public void setOnSplashListener(OnSplashListener splashListener) {
        this.mSplashListener = splashListener;
    }

    public ATSplashAd getSplash() {
        return mAdSdkSplash;
    }

    public void onResetSplash() {
        mAdSdkSplash = null;
        mSplashListener = null;
    }

    /**
     * ??????????????????
     * @param id ?????????ID
     * @param listener ????????????
     */
    public void loadSplash(String id, OnSplashListener listener){
        loadSplash(id, AdConstance.SCENE_CACHE,listener);
    }

    /**
     * ??????????????????
     * @param context ?????????,??????Activity??????
     * @param id ?????????ID
     * @param listener ????????????
     */
    public void loadSplash(Context context,String id, OnSplashListener listener){
        loadSplash(context,id, AdConstance.SCENE_CACHE,listener);
    }

    /**
     * ??????????????????
     * @param id ?????????ID
     * @param scene ??????
     * @param listener ????????????
     */
    public void loadSplash(String id, final String scene, OnSplashListener listener){
        loadSplash(PlatformUtils.getInstance().getContext(),id,scene,listener);
    }

    /**
     * ??????????????????
     * @param context ?????????,??????Activity??????
     * @param id ?????????ID
     * @param scene ??????
     * @param listener ???????????????????????????????????????????????????????????????????????????
     */
    public void loadSplash(Context context,final String id, final String scene, OnSplashListener listener){
        loadSplash(context,id,scene,PlatformUtils.getInstance().getScreenWidth(),PlatformUtils.getInstance().getScreenHeight(),listener);
    }

    /**
     * ??????????????????
     * @param context ???????????????????????????????????????Activity???
     * @param id ?????????ID
     * @param scene ??????
     * @param width ???????????????????????????????????????
     * @param width ???????????????????????????????????????
     * @param listener ???????????????????????????????????????????????????????????????????????????
     */
    public void loadSplash(Context context,final String id, final String scene,int width,int height, OnSplashListener listener){
        Logger.d("loadSplash-->id:"+id+",scene:"+scene+",width:"+width+",height"+height);
        if(null==context){
            if(null!=listener) listener.onError(AdConstance.CODE_CONTEXT_INVALID, getText(AdConstance.CODE_CONTEXT_INVALID), id);
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
        //?????????????????????
        this.mSplashCode=id;this.mSplashScene=scene;
        if(width<=0) width=PlatformUtils.getInstance().getScreenWidth();
        if(height<=0) height=PlatformUtils.getInstance().getScreenHeight();
        mAdSdkSplash = new ATSplashAd(context, id, mATSplashAdListener);
        Map<String, Object> localMap = new HashMap<>();
        localMap.put(ATAdConst.KEY.AD_WIDTH, width);
        localMap.put(ATAdConst.KEY.AD_HEIGHT, height);
        mAdSdkSplash.setLocalExtra(localMap);
        mAdSdkSplash.loadAd();//???????????????,????????????????????????onAdLoaded
    }

    private ATSplashAdListener mATSplashAdListener = new ATSplashAdListener() {

        @Override
        public void onAdLoaded(boolean b) {
            Logger.d("loadSplash-->loaded");
            event(mSplashScene, AdConstance.TYPE_SPLASH,mSplashCode, AdConstance.STATUS_LOADED_SUCCESS,0,null);
            if(null!= mSplashListener){
                mSplashListener.onSuccess(mAdSdkSplash);
            }
        }

        @Override
        public void onAdLoadTimeout() {
//            Logger.e("loadSplash-->onTimeout");
            event(mSplashScene, AdConstance.TYPE_SPLASH,mSplashCode, AdConstance.STATUS_LOADED_ERROR, AdConstance.CODE_TIMOUT, getText(AdConstance.CODE_TIMOUT));
            OnSplashListener listener=mSplashListener;
            onResetSplash();
            if(null!= listener) listener.onTimeOut();
        }

        @Override
        public void onNoAdError(AdError adError) {
            Logger.e("loadSplash-->error,code:"+adError.getCode()+",message:"+adError.getDesc()+"error:"+adError.getFullErrorInfo());
            event(mSplashScene, AdConstance.TYPE_SPLASH,mSplashCode, AdConstance.STATUS_LOADED_ERROR, parseErrorCode(adError),adError.getFullErrorInfo());
            OnSplashListener listener=mSplashListener;
            onResetSplash();
            if(null!= listener) listener.onError(parseErrorCode(adError),adError.getFullErrorInfo(), mSplashCode);
        }

        @Override
        public void onAdShow(ATAdInfo atAdInfo) {
//            Logger.d("loadSplash-->onAdShow");
            event(mSplashScene, AdConstance.TYPE_SPLASH,mSplashCode, AdConstance.STATUS_SHOW_SUCCESS, 0,null);
            if(null!= mSplashListener){
                mSplashListener.onShow();
            }
        }

        @Override
        public void onAdClick(ATAdInfo atAdInfo) {
//            Logger.d("loadSplash-->onAdClicked");
            if(null!= mSplashListener){
                mSplashListener.onClick();
            }
        }

        @Override
        public void onAdDismiss(ATAdInfo atAdInfo, ATSplashAdExtraInfo atSplashAdExtraInfo) {
//            Logger.d("loadSplash-->onAdDismiss");
            OnSplashListener listener=mSplashListener;
            onResetSplash();
            if(null!= listener) listener.onClose();
        }
    };

    //==========================================????????????==============================================

    public void onResetReward() {
        mAtRewardVideoAd=null;mRewardVideoListener=null;mOnRewardInitListener=null;
    }

    public void setOnRewardVideoListener(OnRewardVideoListener listener) {
        this.mRewardVideoListener = listener;
    }

    public String getEcpm() {
        return "0";
    }

    /**
     * ????????????????????????(??????????????????)
     * ?????????????????????????????????
     * ?????????{@link #initReward(Activity activity, String id, OnInitListener listener)} ??? {@link #showAutoRewardVideo(Activity,String,String, OnRewardVideoListener)}
     * @param activity activity?????????
     * @param listener ?????????
     */
    @Deprecated
    public void showRewardVideo(Activity activity, OnRewardVideoListener listener){
        if(null==activity||activity.isFinishing()){
            if(null!=listener) listener.onError(AdConstance.CODE_ACTIVITY_INVALID, getText(AdConstance.CODE_ACTIVITY_INVALID), null);
            return;
        }
        this.mRewardVideoListener=listener;
        if(null==mAtRewardVideoAd){
            if(null!=mRewardVideoListener) mRewardVideoListener.onError(AdConstance.CODE_ADINFO_INVALID, getText(AdConstance.CODE_ADINFO_INVALID), null);
            return;
        }
        mAtRewardVideoAd.show(activity);
    }

    /**
     * ????????????????????????
     * ?????????????????????????????????
     * ?????????{@link #initReward(Activity activity, String id, OnInitListener listener)} ??? {@link #showAutoRewardVideo(Activity,String,String, OnRewardVideoListener)}
     * @param id ?????????ID
     * @param listener ????????????????????????????????????????????????????????????????????????????????????
     */
    @Deprecated
    public void loadRewardVideo(String id, OnRewardVideoListener listener){
        loadRewardVideo(id,AdConstance.SCENE_CACHE,listener);
    }

    /**
     * ????????????????????????
     * ?????????????????????????????????
     * ?????????{@link #initReward(Activity activity, String id, OnInitListener listener)} ??? {@link #showAutoRewardVideo(Activity,String,String, OnRewardVideoListener)}
     * @param id ?????????ID
     * @param scene ?????????????????????
     * @param listener ????????????????????????????????????????????????????????????????????????????????????
     */
    @Deprecated
    public void loadRewardVideo(String id, String scene, OnRewardVideoListener listener){
        loadRewardVideo(PlatformUtils.getInstance().getContext(),id,scene,listener);
    }

    /**
     * ????????????????????????
     * ?????????????????????????????????
     * ?????????{@link #initReward(Activity activity, String id, OnInitListener listener)} ??? {@link #showAutoRewardVideo(Activity,String,String, OnRewardVideoListener)}
     * @param context ?????????,??????Activity??????
     * @param id ?????????ID
     * @param listener ????????????????????????????????????????????????????????????????????????????????????
     */
    @Deprecated
    public void loadRewardVideo(Context context,String id, OnRewardVideoListener listener){
        loadRewardVideo(context,id, AdConstance.SCENE_CACHE,listener);
    }

    /**
     * ????????????????????????
     * ?????????????????????????????????
     * ?????????{@link #initReward(Activity activity, String id, OnInitListener listener)} ??? {@link #showAutoRewardVideo(Activity,String,String, OnRewardVideoListener)}
     * @param context ?????????,??????Activity??????
     * @param id ?????????ID
     * @param scene ?????????????????????
     * @param listener ????????????????????????????????????????????????????????????????????????????????????
     */
    @Deprecated
    public void loadRewardVideo(Context context,final String id, final String scene, OnRewardVideoListener listener){
        Logger.d("loadRewardVideo-->id:"+id+",scene:"+scene);
        if(null==context){
            if(null!=listener) listener.onError(AdConstance.CODE_CONTEXT_INVALID, getText(AdConstance.CODE_CONTEXT_INVALID), id);
            return;
        }
        if(TextUtils.isEmpty(id)){
            if(null!=listener) listener.onError(AdConstance.CODE_ID_UNKNOWN, getText(AdConstance.CODE_ID_UNKNOWN), id);
            return;
        }
        this.mRewardVideoListener=listener;
        if(null!=mAtRewardVideoAd&&mAtRewardVideoAd.isAdReady()){
            Logger.d("loadRewardVideo-->"+getText(AdConstance.CODE_EXIST_CACHE));
            if(null!=mRewardVideoListener){
                mRewardVideoListener.onSuccess(mAtRewardVideoAd);
            }
            return;
        }
        this.mVideoCode=id;this.mVideoScene=scene;
        mAtRewardVideoAd = new ATRewardVideoAd(context, id);
        mAtRewardVideoAd.setAdListener(mATRewardVideoListener);
        mAtRewardVideoAd.load();
    }

    private ATRewardVideoListener mATRewardVideoListener = new ATRewardVideoListener() {

        @Override
        public void onRewardedVideoAdLoaded() {
            Logger.d("loadRewardVideo-->loaded");
            event(mVideoScene, AdConstance.TYPE_REWARD_VIDEO,mVideoCode, AdConstance.STATUS_LOADED_SUCCESS, 0,null);
            if(null!=mRewardVideoListener){
                mRewardVideoListener.onSuccess(mAtRewardVideoAd);
            }
        }

        @Override
        public void onRewardedVideoAdFailed(AdError adError) {
            Logger.e("loadRewardVideo-->error,code:"+adError.getCode()+",message:"+adError.getDesc()+"error:"+adError.getFullErrorInfo());
            event(mVideoScene, AdConstance.TYPE_REWARD_VIDEO,mVideoCode, AdConstance.STATUS_LOADED_ERROR, parseErrorCode(adError),adError.getFullErrorInfo());
            OnRewardVideoListener listener=mRewardVideoListener;
            onResetReward();
            if(null!=listener) listener.onError(parseErrorCode(adError),adError.getFullErrorInfo(), mVideoCode);
        }

        @Override
        public void onRewardedVideoAdPlayStart(ATAdInfo atAdInfo){
//            Logger.d("loadRewardVideo-->onRewardedVideoAdPlayStart");
            PlayManager.getInstance().setAdSource(parseAdSource(atAdInfo.getNetworkFirmId()));
            event(mVideoScene, AdConstance.TYPE_REWARD_VIDEO,mVideoCode, AdConstance.STATUS_SHOW_SUCCESS, 0,null);
            if(null!=mRewardVideoListener){
                mRewardVideoListener.onShow(mAtRewardVideoAd);
            }
        }

        @Override
        public void onRewardedVideoAdPlayEnd(ATAdInfo atAdInfo) {
//            Logger.d("loadRewardVideo-->onRewardedVideoAdPlayEnd");
        }

        @Override
        public void onRewardedVideoAdPlayFailed(AdError adError, ATAdInfo atAdInfo) {
//            Logger.d("loadRewardVideo-->onRewardedVideoAdPlayFailed,code:"+adError.getCode()+",message:"+adError.getDesc());
            event(mVideoScene, AdConstance.TYPE_REWARD_VIDEO,mVideoCode, AdConstance.STATUS_SHOW_ERROR, parseErrorCode(adError),adError.getFullErrorInfo());
            OnRewardVideoListener listener=mRewardVideoListener;
            onResetReward();
            if(null!=listener) listener.onError(parseErrorCode(adError),adError.getDesc(), mVideoCode);
        }

        @Override
        public void onRewardedVideoAdClosed(ATAdInfo atAdInfo) {
//            Logger.d("loadRewardVideo-->onRewardedVideoAdClosed");
            OnRewardVideoListener listener=mRewardVideoListener;
            onResetReward();
            if(null!=listener) listener.onClose();
        }

        @Override
        public void onRewardedVideoAdPlayClicked(ATAdInfo atAdInfo) {
            PlayManager.getInstance().setAdSource(parseAdSource(atAdInfo.getNetworkFirmId()));
//            Logger.d("loadRewardVideo-->onRewardedVideoAdPlayClicked,atAdInfo:"+atAdInfo.toString());
            if(null!=mRewardVideoListener){
                mRewardVideoListener.onClick(atAdInfo);
            }
        }

        @Override
        public void onReward(ATAdInfo atAdInfo) {
            PlayManager.getInstance().setAdSource(parseAdSource(atAdInfo.getNetworkFirmId()));
//            Logger.d("loadRewardVideo-->onReward");
            if(null!=mRewardVideoListener){
                mRewardVideoListener.onRewardVerify();
            }
        }
    };

    //========================================?????????????????????===========================================

    private boolean isRewardShowing=false;//?????????????????????????????????

    /**
     * ?????????/???????????????????????????????????????
     * @param activity ?????????
     * @param id ??????ID
     * @param listener ?????????
     *                 ATRewardVideoAutoLoadListener
     */
    public void initReward(Activity activity, String id, OnInitListener listener){
        initReward(activity,id,AdConstance.SCENE_CACHE,listener);
    }

    /**
     * ?????????/???????????????????????????????????????
     * @param activity ?????????
     * @param id ??????ID
     * @param scene ????????????/???????????????
     * @param listener ?????????
     */
    public void initReward(Activity activity, String id, String scene,OnInitListener listener){
        Logger.d("initReward-->id:"+id+",scene:"+scene);
        if(null==activity){
            if(null!=listener) listener.onError(AdConstance.CODE_ACTIVITY_INVALID,getText(AdConstance.CODE_ACTIVITY_INVALID));
            return;
        }
        this.mOnRewardInitListener =listener;
        ATAdStatusInfo adStatusInfo = ATRewardVideoAutoAd.checkAdStatus(id);
        Logger.d("initReward-->isReady:"+adStatusInfo.isReady()+",isLoading:"+adStatusInfo.isLoading());
        if(adStatusInfo.isReady()){
            if(null!=listener) listener.onSuccess(id);
            return;
        }
        if(adStatusInfo.isLoading()){
            return;
        }
        this.mVideoCode2=id;this.mVideoScene2=scene;
        ATRewardVideoAutoAd.init(activity, new String[]{id}, mATRewardVideoAutoLoadListener);
    }

    private ATRewardVideoAutoLoadListener mATRewardVideoAutoLoadListener= new ATRewardVideoAutoLoadListener() {

        @Override
        public void onRewardVideoAutoLoaded(String adCode) {
            Logger.d("initReward-->loaded,id:"+adCode);
            event(mVideoScene2, AdConstance.TYPE_REWARD_VIDEO,mVideoCode2, AdConstance.STATUS_LOADED_SUCCESS, 0,null);
            OnInitListener listener=mOnRewardInitListener;
            mOnRewardInitListener=null;
            if(null!= listener) listener.onSuccess(adCode);
        }

        @Override
        public void onRewardVideoAutoLoadFail(String adCode, AdError adError) {
            Logger.e("initReward-->adCode:"+adCode+",error:"+adError.getFullErrorInfo());
            event(mVideoScene2, AdConstance.TYPE_REWARD_VIDEO,mVideoCode2, AdConstance.STATUS_LOADED_ERROR, parseErrorCode(adError),adError.getFullErrorInfo());
            OnInitListener listener=mOnRewardInitListener;
            mOnRewardInitListener=null;
            if(null!= listener) listener.onError(parseErrorCode(adError),adError.getFullErrorInfo());
        }
    };

    /**
     * ????????????????????????????????????????????????????????????????????????????????????
     * @param activity ?????????
     * @param id ?????????ID
     * @param listener ?????????
     */
    public void showAutoRewardVideo(Activity activity, String id, OnRewardVideoListener listener){
        showAutoRewardVideo(activity,id,AdConstance.SCENE_CACHE,listener);
    }

    /**
     * ????????????????????????????????????????????????????????????????????????????????????
     * @param activity ?????????
     * @param id ?????????ID
     * @param scene ???????????????????????????
     * @param listener ?????????
     */
    public void showAutoRewardVideo(Activity activity, String id, String scene, OnRewardVideoListener listener){
        Logger.d("showAutoReward-->id:"+id+",isShowing:"+isRewardShowing+",scene:"+scene);
        if(null==activity){
            if(null!=listener) listener.onError(AdConstance.CODE_ACTIVITY_INVALID, getText(AdConstance.CODE_ACTIVITY_INVALID), id);
            return;
        }
        if(TextUtils.isEmpty(id)){
            if(null!=listener) listener.onError(AdConstance.CODE_ID_UNKNOWN, getText(AdConstance.CODE_ID_UNKNOWN), id);
            return;
        }
        if(isRewardShowing){
            if(null!=listener) listener.onError(AdConstance.CODE_REPEATED, getText(AdConstance.CODE_REPEATED), id);
            return;
        }
        this.mRewardVideoListener=listener;
        this.mVideoCode2=id;this.mVideoScene2=scene;
        ATAdStatusInfo adStatusInfo = ATRewardVideoAutoAd.checkAdStatus(id);
        Logger.d("showAutoReward-->isReady:"+adStatusInfo.isReady()+",isLoading:"+adStatusInfo.isLoading());
        if(adStatusInfo.isReady()){
            ATRewardVideoAutoAd.show(activity,id,mATRewardVideoAutoEventListener);
        }else{
            if(adStatusInfo.isLoading()){
                //??????????????????????????????????????????????????????
            }else{
                initReward(activity, id, scene, new OnInitListener() {
                    @Override
                    public void onSuccess(String id) {
                        ATRewardVideoAutoAd.show(activity,id,mATRewardVideoAutoEventListener);
                    }

                    @Override
                    public void onError(int code, String message) {
                        OnRewardVideoListener listener=mRewardVideoListener;
                        onResetReward();
                        if(null!=listener) listener.onError( code,message,mVideoCode2);
                    }
                });
            }
        }
    }

    private ATRewardVideoAutoEventListener  mATRewardVideoAutoEventListener= new ATRewardVideoAutoEventListener() {

        @Override
        public void onRewardedVideoAdPlayStart(ATAdInfo atAdInfo) {
            isRewardShowing=true;
//             Logger.d("showAutoReward-->onRewardedVideoAdPlayStart");
            event(mVideoScene2, AdConstance.TYPE_REWARD_VIDEO,mVideoCode2, AdConstance.STATUS_SHOW_SUCCESS, 0,null);
            if(null!=mRewardVideoListener) mRewardVideoListener.onShow(null);
        }

        @Override
        public void onRewardedVideoAdPlayEnd(ATAdInfo atAdInfo) {
//            Logger.d("showAutoReward-->onRewardedVideoAdPlayEnd");
        }

        @Override
        public void onRewardedVideoAdPlayFailed(AdError adError, ATAdInfo atAdInfo) {
            isRewardShowing=false;
            event(mVideoScene2, AdConstance.TYPE_REWARD_VIDEO,mVideoCode2, AdConstance.STATUS_SHOW_ERROR, parseErrorCode(adError),adError.getFullErrorInfo());
            Logger.e("showAutoReward-->error,code:"+adError.getCode()+",message:"+adError.getDesc()+"error:"+adError.getFullErrorInfo());
            if(null!=mRewardVideoListener) mRewardVideoListener.onError(PlatformUtils.getInstance().parseInt(adError.getCode()),adError.getFullErrorInfo(), mVideoCode2);
        }

        @Override
        public void onRewardedVideoAdClosed(ATAdInfo atAdInfo) {
            isRewardShowing=false;
//            Logger.d("showAutoRewardVideo-->onRewardedVideoAdClosed");
            OnRewardVideoListener listener=mRewardVideoListener;
            onResetReward();
            if(null!=listener) listener.onClose();
        }

        @Override
        public void onRewardedVideoAdPlayClicked(ATAdInfo atAdInfo) {
            isRewardShowing=true;
//            Logger.d("showAutoRewardVideo-->onRewardedVideoAdPlayClicked");
            if(null!=mRewardVideoListener) mRewardVideoListener.onClick(atAdInfo);
        }

        @Override
        public void onReward(ATAdInfo atAdInfo) {
            isRewardShowing=true;
//            Logger.d("showAutoRewardVideo-->onReward");
            if(null!=mRewardVideoListener) mRewardVideoListener.onRewardVerify();
        }
    };

    //==========================================????????????==============================================

    public void onResetInsert() {
        mInsertListener=null;mInterstitialAD=null;mOnInsertInitListener=null;
    }

    public boolean hasInsertAd() {
        return null!=mInterstitialAD;
    }

    @Deprecated
    public void showInsertAd(Activity activity) {
        showInsertAd(activity,null);
    }

    /**
     * ??????????????????(?????????????????????)
     * ?????????????????????????????????
     * ?????????{@link #initInsert(Activity activity, String id, OnInitListener listener)} + {@link #showAutoInsert(Activity,String,String, OnTabScreenListener)}
     * @param activity ???????????????????????????Activity
     * @param listener ???????????????
     */
    @Deprecated
    public void showInsertAd(Activity activity, OnTabScreenListener listener) {
        if(null!=listener){
            this.mInsertListener=listener;
        }
        if(null==activity||activity.isFinishing()){
            if(null!=listener) listener.onError(AdConstance.CODE_ACTIVITY_INVALID, getText(AdConstance.CODE_ACTIVITY_INVALID), null);
            return;
        }
        if(null==mInterstitialAD){
            if(null!=listener) listener.onError(AdConstance.CODE_ADINFO_INVALID, getText(AdConstance.CODE_ADINFO_INVALID), null);
            return;
        }
        try {
            mInterstitialAD.show(activity);
        }catch (Throwable e){
            e.printStackTrace();
            OnTabScreenListener advInsertListener=mInsertListener;
            onResetInsert();
            if(null!=advInsertListener) advInsertListener.onError(0,e.getMessage(), null);
        }
    }

    /**
     * ??????????????????
     * ?????????????????????????????????
     * ?????????{@link #initInsert(Activity activity, String id, OnInitListener listener)} + {@link #showAutoInsert(Activity,String,String, OnTabScreenListener)}
     * @param id ?????????ID
     * @param listener ??????????????????????????????????????????????????????????????????????????????
     */
    @Deprecated
    public void loadInsert(String id, OnTabScreenListener listener){
        loadInsert(id, AdConstance.SCENE_CACHE,listener);
    }

    /**
     * ??????????????????
     * ?????????????????????????????????
     * ?????????{@link #initInsert(Activity activity, String id, OnInitListener listener)} + {@link #showAutoInsert(Activity,String,String, OnTabScreenListener)}
     * @param context ?????????,??????Activity??????
     * @param id ?????????ID
     * @param listener ??????????????????????????????????????????????????????????????????????????????
     */
    @Deprecated
    public void loadInsert(Activity context,String id, OnTabScreenListener listener){
        loadInsert(context,id, AdConstance.SCENE_CACHE,listener);
    }

    /**
     * ??????????????????
     * ?????????????????????????????????
     * ?????????{@link #initInsert(Activity activity, String id, OnInitListener listener)} + {@link #showAutoInsert(Activity,String,String, OnTabScreenListener)}
     * @param id ?????????ID
     * @param scene ???????????????????????????
     * @param listener ??????????????????????????????????????????????????????????????????????????????
     */
    @Deprecated
    public void loadInsert(String id, String scene, OnTabScreenListener listener){
        loadInsert(PlatformUtils.getInstance().getActivity(),id,scene,listener);
    }

    /**
     * ??????????????????
     * ?????????????????????????????????
     * ?????????{@link #initInsert(Activity activity, String id, OnInitListener listener)} + {@link #showAutoInsert(Activity,String,String, OnTabScreenListener)}
     * @param context ?????????,??????Activity??????
     * @param id ?????????ID
     * @param scene ???????????????????????????
     * @param listener ??????????????????????????????????????????????????????????????????????????????
     */
    @Deprecated
    public void loadInsert(Activity context, final String id, final String scene, OnTabScreenListener listener){
        Logger.d("loadInsert-->id:"+id+",scene:"+scene);
        if(null==context){
            if(null!=listener) listener.onError(AdConstance.CODE_CONTEXT_INVALID, getText(AdConstance.CODE_CONTEXT_INVALID), id);
            return;
        }
        if(TextUtils.isEmpty(id)){
            if(null!=listener) listener.onError(AdConstance.CODE_ID_UNKNOWN, getText(AdConstance.CODE_ID_UNKNOWN), id);
            return;
        }
        this.mInsertListener=listener;
        if(null!=mInterstitialAD){
            Logger.d("loadInsert-->"+getText(AdConstance.CODE_EXIST_CACHE));
            if(null!=mInsertListener){
                mInsertListener.onSuccess(mInterstitialAD);
            }
            return;
        }
        this.mInsertCode=id;this.mInsertScene=scene;
        mInterstitialAD = new ATInterstitial(context, id);
        mInterstitialAD.setAdListener(mATInterstitialListener);
        mInterstitialAD.load();
    }

    private ATInterstitialListener mATInterstitialListener= new ATInterstitialListener() {

        @Override
        public void onInterstitialAdLoaded() {
            Logger.d("loadInsert-->loaded");
            event(mInsertScene, AdConstance.TYPE_INSERT,mInsertCode, AdConstance.STATUS_LOADED_SUCCESS, 0,null);
            if(null!=mInsertListener){
                mInsertListener.onSuccess(mInterstitialAD);
            }
        }

        @Override
        public void onInterstitialAdLoadFail(AdError adError) {
            Logger.e("loadInsert-->error,code:"+adError.getCode()+",message:"+adError.getDesc()+"error:"+adError.getFullErrorInfo());
            event(mInsertScene, AdConstance.TYPE_INSERT,mInsertCode, AdConstance.STATUS_LOADED_ERROR, parseErrorCode(adError),adError.getFullErrorInfo());
            OnTabScreenListener listener=mInsertListener;
            onResetInsert();
            if(null!=listener) listener.onError(parseErrorCode(adError),adError.getFullErrorInfo(), mInsertCode);
        }

        @Override
        public void onInterstitialAdClicked(ATAdInfo atAdInfo) {
//                Logger.d("loadInsert-->onInterstitialAdClicked");
            if(null!=mInsertListener){
                mInsertListener.onClick();
            }
        }

        @Override
        public void onInterstitialAdShow(ATAdInfo atAdInfo) {
//                Logger.d("loadInsert-->onInterstitialAdShow");
            event(mInsertScene, AdConstance.TYPE_INSERT,mInsertCode, AdConstance.STATUS_SHOW_SUCCESS, 0,null);
            if(null!=mInsertListener){
                mInsertListener.onShow();
            }
        }

        @Override
        public void onInterstitialAdClose(ATAdInfo atAdInfo) {
//                Logger.d("loadInsert-->onInterstitialAdClose");
            OnTabScreenListener listener=mInsertListener;
            onResetInsert();
            if(null!=listener) listener.onClose();
        }

        @Override
        public void onInterstitialAdVideoStart(ATAdInfo atAdInfo) {
//                Logger.d("loadInsert-->onInterstitialAdVideoStart");
        }

        @Override
        public void onInterstitialAdVideoEnd(ATAdInfo atAdInfo) {
//                Logger.d("loadInsert-->onInterstitialAdVideoEnd");
        }

        @Override
        public void onInterstitialAdVideoError(AdError adError) {
//                Logger.e("loadInsert-->onInterstitialAdVideoError,code:"+adError.getCode()+",message:"+adError.getFullErrorInfo());
            event(mInsertScene, AdConstance.TYPE_INSERT,mInsertCode, AdConstance.STATUS_SHOW_ERROR, parseErrorCode(adError),adError.getFullErrorInfo());
            OnTabScreenListener advInsertListener=mInsertListener;
            onResetInsert();
            if(null!=advInsertListener) advInsertListener.onError(parseErrorCode(adError),adError.getFullErrorInfo(), mInsertCode);
        }
    };

    //==========================================???????????????============================================

    private boolean isInsertShowing=false;//???????????????????????????

    /**
     * ??????/????????????????????????
     * @param activity ???????????????????????????Activity
     * @param id ?????????ID
     * @param listener ???????????????
     */
    public void initInsert(Activity activity, String id, OnInitListener listener){
        initInsert(activity,id,AdConstance.SCENE_CACHE,listener);
    }

    /**
     * ??????/????????????????????????
     * @param activity ???????????????????????????Activity
     * @param id ?????????ID
     * @param scene ??????????????????
     * @param listener ???????????????
     */
    public void initInsert(Activity activity, String id, String scene, OnInitListener listener){
        Logger.d("initInsert-->id:"+id+",scene:"+scene);
        if(null==activity){
            if(null!=listener) listener.onError(AdConstance.CODE_ACTIVITY_INVALID,getText(AdConstance.CODE_ACTIVITY_INVALID));
            return;
        }
        this.mOnInsertInitListener =listener;
        ATAdStatusInfo adStatusInfo = ATInterstitialAutoAd.checkAdStatus(id);
        Logger.d("initInsert-->isReady:"+adStatusInfo.isReady()+",isLoading:"+adStatusInfo.isLoading());
        if(adStatusInfo.isReady()){
            if(null!=listener) listener.onSuccess(id);
            return;
        }
        if(adStatusInfo.isLoading()){
            return;
        }
        this.mInsertCode2=id;this.mInsertScene2=scene;
        ATInterstitialAutoAd.init(activity, new String[]{id}, mInterstitialAutoLoadListener);
    }

    private ATInterstitialAutoLoadListener mInterstitialAutoLoadListener= new ATInterstitialAutoLoadListener() {

        @Override
        public void onInterstitialAutoLoaded(String adCode) {
            Logger.d("initInsert-->loaded,id:"+adCode);
            event(mInsertScene2, AdConstance.TYPE_INSERT,mInsertCode2, AdConstance.STATUS_LOADED_SUCCESS, 0,null);
            OnInitListener listener=mOnInsertInitListener;
            mOnInsertInitListener=null;
            if(null!= listener) listener.onSuccess(adCode);
        }

        @Override
        public void onInterstitialAutoLoadFail(String adCode, AdError adError) {
            Logger.e("initInsert-->error,adCode:"+adCode+",error:"+adError.getFullErrorInfo());
            event(mInsertScene2, AdConstance.TYPE_INSERT,mInsertCode2, AdConstance.STATUS_LOADED_ERROR, parseErrorCode(adError),adError.getFullErrorInfo());
            OnInitListener listener=mOnInsertInitListener;
            mOnInsertInitListener=null;
            if(null!= listener) listener.onError(parseErrorCode(adError),adError.getFullErrorInfo());
        }
    };

    /**
     * ??????????????????????????????????????????????????????????????????????????????
     * @param activity ???????????????????????????Activity
     * @param id ?????????ID
     * @param listener ???????????????
     */
    public void showAutoInsert(Activity activity, String id, OnTabScreenListener listener){
        showAutoInsert(activity,id,AdConstance.SCENE_CACHE,listener);
    }

    /**
     * ??????????????????????????????????????????????????????????????????????????????
     * @param activity ???????????????????????????Activity
     * @param id ?????????ID
     * @param scene ??????????????????
     * @param listener ???????????????
     */
    public void showAutoInsert(Activity activity, String id, String scene, OnTabScreenListener listener){
        Logger.d("showAutoInsert-->id:"+id+",isShowing:"+isInsertShowing+",scene:"+scene);
        if(null==activity){
            if(null!=listener) listener.onError(AdConstance.CODE_ACTIVITY_INVALID, getText(AdConstance.CODE_ACTIVITY_INVALID), id);
            return;
        }
        if(TextUtils.isEmpty(id)){
            if(null!=listener) listener.onError(AdConstance.CODE_ID_UNKNOWN, getText(AdConstance.CODE_ID_UNKNOWN), id);
            return;
        }
        if(isInsertShowing){
            if(null!=listener) listener.onError(AdConstance.CODE_REPEATED, getText(AdConstance.CODE_REPEATED), id);
            return;
        }
        this.mInsertListener=listener;
        this.mInsertCode2=id;this.mInsertScene2=scene;
        ATAdStatusInfo adStatusInfo = ATInterstitialAutoAd.checkAdStatus(id);
        Logger.d("showAutoInsert-->isReady:"+adStatusInfo.isReady()+",isLoading:"+adStatusInfo.isLoading());
        if(adStatusInfo.isReady()){
            ATInterstitialAutoAd.show(activity,id,mATInterstitialAutoEventListener);
        }else{
            if(adStatusInfo.isLoading()){
                //??????????????????????????????????????????????????????
            }else{
                initInsert(activity, id, scene, new OnInitListener() {
                    @Override
                    public void onSuccess(String id) {
                        ATInterstitialAutoAd.show(activity,id,mATInterstitialAutoEventListener);
                    }

                    @Override
                    public void onError(int code, String message) {
                        OnTabScreenListener listener=mInsertListener;
                        onResetInsert();
                        if(null!=listener) listener.onError( code,message,mInsertCode2);
                    }
                });
            }
        }
    }

    private ATInterstitialAutoEventListener mATInterstitialAutoEventListener= new ATInterstitialAutoEventListener() {

        @Override
        public void onInterstitialAdClicked(ATAdInfo atAdInfo) {
            isInsertShowing=true;
            if(null!=mInsertListener) mInsertListener.onClick();
        }

        @Override
        public void onInterstitialAdShow(ATAdInfo atAdInfo) {
            isInsertShowing=true;
            event(mInsertScene2, AdConstance.TYPE_INSERT,mInsertCode2, AdConstance.STATUS_SHOW_SUCCESS, 0,null);
            if(null!=mInsertListener) mInsertListener.onShow();
        }

        @Override
        public void onInterstitialAdClose(ATAdInfo atAdInfo) {
            isInsertShowing=false;
            if(null!=mInsertListener) mInsertListener.onClose();
        }

        @Override
        public void onInterstitialAdVideoStart(ATAdInfo atAdInfo) {

        }

        @Override
        public void onInterstitialAdVideoEnd(ATAdInfo atAdInfo) {

        }

        @Override
        public void onInterstitialAdVideoError(AdError adError) {
            isInsertShowing=false;
            Logger.e("showAutoInsert-->error,code:"+adError.getCode()+",message:"+adError.getDesc()+"error:"+adError.getFullErrorInfo());
            event(mInsertScene2, AdConstance.TYPE_INSERT,mInsertCode2, AdConstance.STATUS_SHOW_ERROR, parseErrorCode(adError),adError.getFullErrorInfo());
            if(null!=mInsertListener) mInsertListener.onError(PlatformUtils.getInstance().parseInt(adError.getCode()),adError.getFullErrorInfo(),null);
        }
    };

    //=============================Banner?????????Banner?????????View???????????????===============================

    /**
     * ??????Banner??????,????????????Banner?????????ViewGroup???
     * @param id ?????????ID
     * @param viewGroup ??????BannerView?????????
     * @param listener ???????????????
     */
    public void loadBanner(String id, ViewGroup viewGroup,OnExpressListener listener){
        loadBanner(id,viewGroup,AdConstance.SCENE_CACHE,listener);
    }

    /**
     * ??????Banner??????,????????????Banner?????????ViewGroup???
     * @param id ?????????ID
     * @param viewGroup ??????BannerView?????????
     * @param scene ????????????????????????
     * @param listener ???????????????
     */
    public void loadBanner(String id, ViewGroup viewGroup,String scene,OnExpressListener listener){
        loadBanner(id,viewGroup,scene,PlatformUtils.getInstance().getScreenWidthDP(),0f,listener);
    }

    /**
     * ??????Banner??????,????????????Banner?????????ViewGroup???
     * @param id ?????????ID
     * @param viewGroup ??????BannerView?????????
     * @param scene ????????????????????????
     * @param adWidth ??????????????????????????????dp??????0????????????????????????
     * @param adHeight ??????????????????????????????dp??????0???????????????????????????
     * @param listener ???????????????
     */
    public void loadBanner(String id, ViewGroup viewGroup,String scene,float adWidth, float adHeight, OnExpressListener listener){
        Logger.d("loadBanner-->id:"+id+",scene:"+scene+",width:"+adWidth+",height:"+adHeight);
        if(null==viewGroup){
            if(null!=listener) listener.onError(AdConstance.CODE_VIEWGROUP_INVALID, getText(AdConstance.CODE_VIEWGROUP_INVALID), id);
            return;
        }
        if(TextUtils.isEmpty(id)){
            if(null!=listener) listener.onError(AdConstance.CODE_ID_UNKNOWN, getText(AdConstance.CODE_ID_UNKNOWN), id);
            return;
        }
        new Banner().loadBanner(id,viewGroup,scene,adWidth,adHeight,listener);
    }

    private class Banner {

        private OnExpressListener mStreamListener;
        private ATBannerView mBannerView;
        private String mAdCode,mScene;

        public void loadBanner(String id,ViewGroup viewGroup,String scene,float adWidth, float adHeight, OnExpressListener listener) {
            this.mStreamListener=listener;
            mBannerView = new ATBannerView(viewGroup.getContext());
            mBannerView.setPlacementId(id);
            Map<String, Object> localMap = new HashMap<>();
            localMap.put(ATAdConst.KEY.AD_WIDTH, adWidth<=0? PlatformUtils.getInstance().getScreenWidth(): PlatformUtils.getInstance().dpToPxInt(adWidth));
            localMap.put(ATAdConst.KEY.AD_HEIGHT, PlatformUtils.getInstance().dpToPxInt(adHeight));
            mBannerView.setLocalExtra(localMap);
            mBannerView.setLayoutParams(new FrameLayout.LayoutParams(PlatformUtils.getInstance().dpToPxInt(adWidth), adHeight>0f? PlatformUtils.getInstance().dpToPxInt(adHeight): FrameLayout.LayoutParams.WRAP_CONTENT));
            //??????BannerView??????????????????,?????????????????????
            viewGroup.addView(mBannerView);
            this.mAdCode=id;this.mScene=scene;
            mBannerView.setBannerAdListener(mATBannerListener);
            mBannerView.loadAd();
        }

        private ATBannerListener mATBannerListener= new ATBannerListener() {

            @Override
            public void onBannerLoaded() {
                Logger.d("loadBanner-->loaded,id:"+mAdCode);
                event(mScene, AdConstance.TYPE_BANNER,mAdCode, AdConstance.STATUS_LOADED_SUCCESS, 0,null);
                if(null!=mStreamListener) mStreamListener.onSuccessBanner(mBannerView);
            }

            @Override
            public void onBannerFailed(AdError adError) {
//                Logger.e("loadBanner-->error,code:"+adError.getCode()+",message:"+adError.getDesc()+"error:"+adError.getFullErrorInfo());
                error(parseErrorCode(adError),adError.getFullErrorInfo());
            }

            @Override
            public void onBannerClicked(ATAdInfo atAdInfo) {
//                Logger.d("AdBanner-loadBanner-onBannerClicked-->");
            }

            @Override
            public void onBannerShow(ATAdInfo atAdInfo) {
//                Logger.d("AdBanner-loadBanner-onBannerShow-->");
                event(mScene, AdConstance.TYPE_BANNER,mAdCode, AdConstance.STATUS_SHOW_SUCCESS, 0,null);
            }

            @Override
            public void onBannerClose(ATAdInfo atAdInfo) {
//                Logger.d("AdBanner-loadBanner-onBannerClose-->");
                OnExpressListener listener=mStreamListener;
                mBannerView=null;mStreamListener=null;mAdCode=null;mScene=null;
                if(null!=listener) listener.onClose();
            }

            @Override
            public void onBannerAutoRefreshed(ATAdInfo atAdInfo) {
//                Logger.d("AdBanner-loadBanner-onBannerAutoRefreshed-->");
            }

            @Override
            public void onBannerAutoRefreshFail(AdError adError) {
//                Logger.d("AdBanner-loadBanner-onBannerAutoRefreshFail-->");
            }
        };

        private void error(int code,String error) {
            Logger.e("loadBanner-->error,code:" + code + ",error:"+error);
            event(mScene, AdConstance.TYPE_BANNER,mAdCode, AdConstance.STATUS_LOADED_ERROR,code,error);
            OnExpressListener listener=mStreamListener;
            mBannerView=null;mStreamListener=null;
            if(null!=listener) listener.onError(code,error, mAdCode);
        }
    }

    //===================================???????????????\?????????????????????=======================================

    /**
     * ????????????????????????5.9.7.0??????+??????
     * @param context ?????????Activity??????????????????
     * @param id ?????????ID
     * @param listener ???????????????
     */
    public void loadStream(Context context, String id, OnExpressListener listener){
        loadStream(context,id,AdConstance.SCENE_CACHE,listener);
    }

    /**
     * ????????????????????????5.9.7.0??????+??????
     * @param context ?????????Activity??????????????????
     * @param id ?????????ID
     * @param scene ????????????????????????
     * @param listener ???????????????
     */
    public void loadStream(Context context, String id, String scene,OnExpressListener listener){
        loadStream(context,id,scene,PlatformUtils.getInstance().getScreenWidthDP(),0f,listener);
    }

    /**
     * ????????????????????????5.9.7.0??????+??????
     * @param context ?????????Activity??????????????????
     * @param id ?????????ID
     * @param scene ????????????????????????
     * @param adWidth ??????????????????????????????dp??????0????????????????????????
     * @param adHeight ??????????????????????????????dp??????0???????????????????????????
     * @param listener ???????????????
     */
    public void loadStream(Context context, String id,String scene, float adWidth, float adHeight, OnExpressListener listener){
        Logger.d("loadStream-->id:"+id+",scene:"+scene+",width:"+adWidth+",height:"+adHeight);
        if(null==context){
            if(null!=listener) listener.onError(AdConstance.CODE_CONTEXT_INVALID, getText(AdConstance.CODE_CONTEXT_INVALID), id);
            return;
        }
        if(TextUtils.isEmpty(id)){
            if(null!=listener) listener.onError(AdConstance.CODE_ID_UNKNOWN, getText(AdConstance.CODE_ID_UNKNOWN), id);
            return;
        }
        new Stream().loadStream(context,id,scene,adWidth,adHeight,listener);
    }

    private class Stream {

        private OnExpressListener mStreamListener;
        private ATNative mNativeExpressAD;
        private String mAdCode,mScene;

        public void loadStream(Context context, String id, String scene, float adWidth, float adHeight, OnExpressListener listener) {
            this.mStreamListener=listener;
            mNativeExpressAD = new ATNative(context, id,mATNativeNetworkListener);
            Map<String, Object> localMap = new HashMap<>();
            localMap.put(ATAdConst.KEY.AD_WIDTH, adWidth<=0? PlatformUtils.getInstance().getScreenWidth(): PlatformUtils.getInstance().dpToPxInt(adWidth));
            //????????????Pangle???
            localMap.put(TTATConst.NATIVE_AD_IMAGE_HEIGHT, PlatformUtils.getInstance().dpToPxInt(adHeight));
            //???????????????Tencent Ads??????ADSize.AUTO_HEIGHT??????-2
            localMap.put(GDTATConst.AD_HEIGHT,adHeight<=0? ADSize.AUTO_HEIGHT:PlatformUtils.getInstance().dpToPxInt(adHeight));
            localMap.put(ATAdConst.KEY.AD_HEIGHT, PlatformUtils.getInstance().dpToPxInt(adHeight));
            this.mAdCode=id;this.mScene=scene;
            mNativeExpressAD.setLocalExtra(localMap);
            mNativeExpressAD.makeAdRequest();//??????????????????
        }

        private ATNativeNetworkListener mATNativeNetworkListener= new ATNativeNetworkListener() {

            @Override
            public void onNativeAdLoaded() {
                Logger.d("loadStream-->loaded,id:"+mAdCode);
                event(mScene, AdConstance.TYPE_STREAM,mAdCode, AdConstance.STATUS_LOADED_SUCCESS, 0,null);
                if(null!=mStreamListener&&null!=mNativeExpressAD){
                    NativeAd nativeAd = mNativeExpressAD.getNativeAd();
                    if(null!=nativeAd){
                        nativeAd.setDislikeCallbackListener(new ATNativeDislikeListener() {
                            @Override
                            public void onAdCloseButtonClick(ATNativeAdView atNativeAdView, ATAdInfo atAdInfo) {
                                if(null!=mStreamListener) mStreamListener.onClose();
                            }
                        });
                        nativeAd.setNativeEventListener(new ATNativeEventListener() {
                            @Override
                            public void onAdImpressed(ATNativeAdView atNativeAdView, ATAdInfo atAdInfo) {
//                                Logger.d("onAdImpressed-->width:"+atNativeAdView.getWidth()+",height:"+atNativeAdView.getHeight());
                                event(mScene, AdConstance.TYPE_STREAM,mAdCode, AdConstance.STATUS_SHOW_SUCCESS, 0,null);
                                if(null!=mStreamListener) mStreamListener.onShow();

                            }

                            @Override
                            public void onAdClicked(ATNativeAdView atNativeAdView, ATAdInfo atAdInfo) {
                                if(null!=mStreamListener) mStreamListener.onClick();
                            }

                            @Override
                            public void onAdVideoStart(ATNativeAdView atNativeAdView) {}

                            @Override
                            public void onAdVideoEnd(ATNativeAdView atNativeAdView) {}

                            @Override
                            public void onAdVideoProgress(ATNativeAdView atNativeAdView, int i) {}
                        });
                        mStreamListener.onSuccessExpressed(nativeAd);
                    }else{
                        error(AdConstance.CODE_AD_EMPTY,getText(AdConstance.CODE_AD_EMPTY));
                    }
                }else{
                    error(AdConstance.CODE_AD_EMPTY,getText(AdConstance.CODE_AD_EMPTY));
                }
            }

            @Override
            public void onNativeAdLoadFail(AdError adError) {
//                Logger.e("onNativeAdLoadFail-->");
                error(parseErrorCode(adError),adError.getFullErrorInfo());
            }
        };

        private void error(int code,String error) {
            Logger.e("loadStream-->error,code:" + code + ",error:"+error);
            event(mScene, AdConstance.TYPE_STREAM,mAdCode, AdConstance.STATUS_LOADED_ERROR,code,error);
            OnExpressListener listener=mStreamListener;
            mNativeExpressAD=null;mStreamListener=null;
            if(null!=listener) listener.onError(code,error, mAdCode);
        }
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {}

    @Override
    public void onActivityStarted(Activity activity) {
        if(!isExternalActivity) PlatformUtils.getInstance().setActivity(activity);
    }

    @Override
    public void onActivityResumed(Activity activity) {
        if(!isExternalActivity) PlatformUtils.getInstance().setActivity(activity);
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
        InitHelper.unInit();
        context.registerActivityLifecycleCallbacks(this);
    }
}