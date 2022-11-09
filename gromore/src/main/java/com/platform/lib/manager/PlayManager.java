package com.platform.lib.manager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import com.bytedance.msdk.api.v2.ad.reward.GMRewardAd;
import com.platform.lib.bean.Result;
import com.platform.lib.constants.AdConstance;
import com.platform.lib.listener.OnPlayListener;
import com.platform.lib.utils.PlatformUtils;
import com.platform.lib.widget.RewardActivity;

/**
 * created by hty
 * 2022/3/25
 * Desc:激励视频广告播放管理者容器
 *
 * 1、显示激励视频：{@link #startVideo(String ad_code, OnPlayListener listener)}
 */
public final class PlayManager extends OnPlayListener {

    private volatile static PlayManager mInstance;
    private OnPlayListener mListener;
    private boolean isShowing;//是否正在播放中
    private String mAdSource;//真实的激励视频广告平台，1：穿山甲 3：优量汇 5：快手

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

    public String getAdSource() {
        return mAdSource;
    }

    public void setAdSource(String adSource) {
        mAdSource = adSource;
    }

    /**
     * 开始播放激励视屏
     * @param ad_code 广告位ID
     */
    public void startVideo(String ad_code){
        startVideo(ad_code, AdConstance.SCENE_CACHE);
    }

    /**
     * 开始播放激励视屏
     * @param ad_code 广告位ID
     * @param scene 播放场景
     */
    public void startVideo(String ad_code, String scene){
        startVideo(ad_code,scene,null);
    }

    /**
     * 开始播放激励视屏
     * @param ad_code 广告位ID
     * @param listener 状态监听器
     */
    public void startVideo(String ad_code, OnPlayListener listener){
        startVideo(ad_code,AdConstance.SCENE_CACHE,listener);
    }

    /**
     * 开始播放激励视屏
     * @param ad_code 广告位ID
     * @param scene 播放场景
     * @param listener 状态监听器
     */
    public void startVideo(String ad_code, String scene,OnPlayListener listener){
        this.mListener=listener;
        mAdSource=null;
        Context context = PlatformUtils.getInstance().getContext();
        Intent intent=new Intent(context, RewardActivity.class);
        intent.putExtra("id",ad_code);
        intent.putExtra("scene",scene);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    public void onClose(Result result) {
        OnPlayListener playerListener=mListener;
        mListener=null;
        if(null!=playerListener) playerListener.onClose(result);
    }

    @Override
    public void onSuccess(GMRewardAd atRewardVideoAd) {
        if(null!=mListener) mListener.onSuccess(atRewardVideoAd);
    }

    @Override
    public void onShow() {
        if(null!=mListener) mListener.onShow();
    }

    @Override
    public void onClick() {
        if(null!=mListener) mListener.onClick();
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
    public void openActivity(Activity activity) {
        if(null!=mListener) mListener.openActivity(activity);
    }

    @Override
    public void closeActivity(Activity activity) {
        if(null!=mListener) mListener.closeActivity(activity);
    }
}