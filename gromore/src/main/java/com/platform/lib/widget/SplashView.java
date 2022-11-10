package com.platform.lib.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import com.bytedance.msdk.api.v2.ad.splash.GMSplashAd;
import com.platform.lib.constants.AdConstance;
import com.platform.lib.listener.OnSplashListener;
import com.platform.lib.listener.OnSplashStatusListener;
import com.platform.lib.manager.PlatformManager;
import com.platform.lib.utils.PlatformUtils;

/**
 * created by hty
 * 2022/10/8
 * Desc:开屏广告容器包装,内部根据广告配置自动识别广告平台和渲染广告
 * 1、开始加载广告：{@link #loadSplashAd(String ad_code, OnSplashStatusListener listener)}
 */
public class SplashView extends FrameLayout implements OnSplashListener {

    private OnSplashStatusListener mListener;
    private String mCurrentId;

    public SplashView(Context context) {
        super(context);
    }

    public SplashView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SplashView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 加载开屏广告
     * @param ad_code 广告位ID
     * @param listener 状态监听
     */
    public void loadSplashAd(String ad_code, OnSplashStatusListener listener){
        loadSplashAd(ad_code,AdConstance.SCENE_CACHE,listener);
    }

    /**
     * 加载开屏广告
     * @param ad_code 广告位ID
     * @param scene 场景
     * @param listener 状态监听
     */
    public void loadSplashAd(String ad_code, String scene,OnSplashStatusListener listener){
        loadSplashAd(ad_code,scene,PlatformUtils.getInstance().getScreenWidth(),PlatformUtils.getInstance().getScreenHeight(), listener);
    }

    /**
     * 加载开屏广告
     * @param ad_code 广告位ID
     * @param scene 场景
     * @param width 预期渲染的宽，单位：分辨率
     * @param width 预期渲染的高，单位：分辨率
     * @param listener 状态监听
     */
    public void loadSplashAd(String ad_code, String scene,int width,int height,OnSplashStatusListener listener){
        this.mListener=listener;
        if(!TextUtils.isEmpty(ad_code)){
            this.mCurrentId=new StringBuilder(ad_code).toString();
            PlatformManager.getInstance().loadSplash(PlatformUtils.getInstance().getActivity(getContext()),ad_code,scene,width,height,SplashView.this);
        }else{
            if(null!=mListener) mListener.onError(AdConstance.CODE_ID_UNKNOWN, PlatformManager.getInstance().getText(AdConstance.CODE_ID_UNKNOWN),ad_code);
        }
    }

    @Override
    public void onSuccess(GMSplashAd atSplashAd) {
//        Logger.d("onSuccess-->ATSplashAd");
        if(null!=atSplashAd){
            removeAllViews();
            atSplashAd.showAd(this);
        }else{
            onError(0,null,"");
        }
    }

    @Override
    public void onShow() {
//        Logger.d("onShow-->");
        if(null!=mListener) mListener.onShow();
    }

    @Override
    public void onClose() {
//        Logger.d("onClose-->");
        removeAllViews();
        if(null!=mListener) mListener.onClose();
    }

    @Override
    public void onTimeOut() {
//        Logger.d("onTimeOut-->");
        onError(AdConstance.CODE_TIMOUT,PlatformManager.getInstance().getText(AdConstance.CODE_TIMOUT),mCurrentId);
    }

    @Override
    public void onClick() {
//        Logger.d("onClick-->");
        if(null!=mListener) mListener.onClick();
    }

    @Override
    public void onError(int code, String message, String adCode) {
//        Logger.d("onError-->code:"+code+",message:"+message+",id:"+adCode);
        if(null!=mListener) mListener.onError(code,message,adCode);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        onReset();
    }

    public void onReset(){
        mListener=null;
    }
}