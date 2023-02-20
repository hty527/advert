package com.platform.lib.manager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.anythink.core.api.ATAdInfo;
import com.anythink.rewardvideo.api.ATRewardVideoAd;
import com.platform.lib.bean.Result;
import com.platform.lib.constants.AdConstance;
import com.platform.lib.listener.OnPlayListener;
import com.platform.lib.utils.PlatformUtils;
import com.platform.lib.widget.RewardActivity;

/**
 * created by hty
 * 2022/9/27
 * Desc:激励视频播放管理器
 * 全自动模式默认是关闭状态的
 *
 * 1、显示激励视频：{@link #startVideo(String ad_code, OnPlayListener listener)}
 */
public final class PlayManager extends OnPlayListener {

    private volatile static PlayManager mInstance;
    private OnPlayListener mListener;
    private boolean isShowing;//是否正在播放中

    public static PlayManager getInstance() {
        if(null==mInstance){
            synchronized (PlayManager.class) {
                if (null == mInstance) {
                    mInstance = new PlayManager();
                }
            }
        }
        return mInstance;
    }

    public boolean isShowing() {
        return isShowing;
    }

    public void setShowing(boolean showing) {
        isShowing = showing;
    }

    public void setAdPlayerListener(OnPlayListener listener) {
        mListener = listener;
    }

    /**
     * 开始播放激励视屏
     * @param ad_code 广告位ID
     */
    public void startVideo(String ad_code){
        startVideo(ad_code, AdConstance.SCENE_CACHE,false);
    }

    /**
     * 开始播放激励视屏
     * @param ad_code 广告位ID
     * @param scene 播放场景
     */
    public void startVideo(String ad_code, String scene){
        startVideo(ad_code,scene,false);
    }

    /**
     * 开始播放激励视屏
     * @param ad_code 广告位ID
     * @param scene 播放场景
     * @param isAutoModel 是否启用全自动模式，内部自动加载激励视频广告并且在合适的时机自动缓存下一个激励视频广告实例，默认关闭
     */
    public void startVideo(String ad_code, String scene,boolean isAutoModel){
        startVideo(ad_code,scene,isAutoModel,null);
    }

    /**
     * 开始播放激励视屏
     * @param ad_code 广告位ID
     * @param listener 状态监听器
     */
    public void startVideo(String ad_code, OnPlayListener listener){
        startVideo(ad_code,false,listener);
    }

    /**
     * 开始播放激励视屏
     * @param ad_code 广告位ID
     * @param isAutoModel 是否启用全自动模式，内部自动加载激励视频广告并且在合适的时机自动缓存下一个激励视频广告实例
     * @param listener 状态监听器
     */
    public void startVideo(String ad_code, boolean isAutoModel, OnPlayListener listener){
        startVideo(ad_code,AdConstance.SCENE_CACHE,isAutoModel,listener);
    }

    /**
     * 开始播放激励视屏
     * @param ad_code 广告位ID
     * @param scene 播放场景
     * @param listener 状态监听器
     */
    public void startVideo(String ad_code, String scene, OnPlayListener listener){
        startVideo(ad_code,scene,false,listener);
    }

    /**
     * 开始播放激励视屏
     * @param ad_code 广告位ID
     * @param scene 播放场景
     * @param isAutoModel 是否启用全自动模式，内部自动加载激励视频广告并且在合适的时机自动缓存下一个激励视频广告实例，默认关闭
     * @param listener 状态监听器
     */
    public void startVideo(String ad_code, String scene,boolean isAutoModel, OnPlayListener listener){
        this.mListener=listener;
        Context context = PlatformUtils.getInstance().getContext();
        Intent intent=new Intent(context, RewardActivity.class);
        intent.putExtra("id",ad_code);
        intent.putExtra("scene",scene);
        intent.putExtra("is_auto",isAutoModel?"1":"0");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    public void onSuccess(ATRewardVideoAd atRewardVideoAd) {
        if(null!=mListener) mListener.onSuccess(atRewardVideoAd);
    }

    @Override
    public void onAdvertActivityCreated(Activity activity) {
        if(null!=mListener) mListener.onAdvertActivityCreated(activity);
    }

    @Override
    public void onShow() {
        if(null!=mListener) mListener.onShow();//为满足不同需求的开发者
    }

    @Override
    public void onShow(ATRewardVideoAd atRewardVideoAd) {
        onShow();
        if(null!=mListener) mListener.onShow(atRewardVideoAd);
    }

    @Override
    public void onClick() {
        if(null!=mListener) mListener.onClick();//为满足不同需求的开发者
    }

    @Override
    public void onClick(ATAdInfo atAdInfo) {
        onClick();
        if(null!=mListener) mListener.onClick(atAdInfo);
    }

    @Override
    public void onRewardVerify() {
        if(null!=mListener) mListener.onRewardVerify();
    }

    @Override
    public void onError(int code, String message, String adCode) {
        if(null!=mListener) mListener.onError(code,message,adCode);
    }

    @Override
    public void onClose(Result result) {
        OnPlayListener playerListener=mListener;
        mListener=null;
        if(null!=playerListener) playerListener.onClose(result);
    }
}