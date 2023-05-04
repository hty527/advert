package com.platform.lib.manager;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.bytedance.msdk.api.v2.ad.interstitialFull.GMInterstitialFullAd;
import com.platform.lib.bean.Result;
import com.platform.lib.constants.AdConstance;
import com.platform.lib.listener.OnPlayListener;
import com.platform.lib.listener.OnTabScreenListener;
import com.platform.lib.utils.Logger;
import com.platform.lib.utils.PlatformUtils;

/**
 * created by hty
 * 2022/10/8
 * Desc:插屏广告播放代理人
 * 内部默认关闭全自动加载模式，当全自动模式打开后，广告SDK内部会自动拉插屏广告及在合适的时机自动缓存下一条插屏广告
 * 请关注字段为isAutoModel的参数
 *
 * 1、显示插屏：{@link #showInsert(String id, OnPlayListener listener)}
 */
public final class TableScreenManager {

    private volatile static TableScreenManager mInstance;
    private OnPlayListener mPlayerListener;
    private Handler mHandler;
    private boolean isClick=false;//是否点击了广告
    private String mCurrentId;//当前正被处理的广告位ID

    public static TableScreenManager getInstance() {
        if(null==mInstance){
            synchronized (TableScreenManager.class) {
                if (null == mInstance) {
                    mInstance = new TableScreenManager();
                }
            }
        }
        return mInstance;
    }

    public Handler getHandler() {
        if(null==mHandler){
            mHandler=new Handler(Looper.getMainLooper());
        }
        return mHandler;
    }

    /**
     * 尝试播放一个插屏广告
     * @param id 广告ID
     */
    public void showInsert(String id){
        showInsert(id,0);
    }

    /**
     * 尝试播放一个插屏广告
     * @param id 广告ID
     * @param delayed 延时展示插屏广告的时间戳，单位：毫秒
     */
    public void showInsert(String id,long delayed){
        showInsert(id,delayed,null);
    }

    /**
     * 尝试播放一个插屏广告
     * @param id 广告ID
     * @param listener 监听器
     */
    public void showInsert(String id, OnPlayListener listener){
        showInsert(id,0,listener);
    }

    /**
     * 尝试播放一个插屏广告
     * @param id 广告ID
     * @param delayed 延时展示插屏广告的时间戳，单位：毫秒
     * @param listener 监听器
     */
    public void showInsert(String id, long delayed, OnPlayListener listener){
        showInsert(id,AdConstance.SCENE_CACHE,delayed,listener);
    }

    /**
     * 尝试播放一个插屏广告
     * @param id 广告ID
     * @param scene 广告播放的场景标识
     * @param listener 监听器
     */
    public void showInsert(String id,String scene, OnPlayListener listener){
        showInsert(id,scene,0,listener);
    }

    /**
     * 尝试播放一个插屏广告
     * @param id 广告ID
     * @param scene 广告播放的场景标识
     * @param delayed 延时多久后开始展示插屏，单位：毫秒
     * @param listener 监听器
     */
    public void showInsert(String id, String scene, long delayed, OnPlayListener listener){
        Logger.d("showInsert-->id:"+id+",delayed:"+delayed+",scene:"+scene);
        if(TextUtils.isEmpty(id)){
            if(null!=listener) listener.onError(AdConstance.CODE_ID_UNKNOWN, PlatformManager.getInstance().getText(AdConstance.CODE_ID_UNKNOWN), id);
            return;
        }
        if(PlatformManager.getInstance().isDevelop()){
            Result status=new Result();
            status.setAdCode(id);
            status.setIsClick("1");
            if(null!=listener) listener.onClose(status);
            return;
        }
        this.mPlayerListener=listener;
        if(null!=mHandler){
            mHandler.removeCallbacksAndMessages(null);
            mHandler.removeMessages(0);
        }
        this.mCurrentId=new StringBuilder(id).toString();
        if(delayed<=0){
            startInsert(scene);
            return;
        }
        getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startInsert(scene);
            }
        },delayed);
    }

    /**
     * 开始加载并渲染插屏广告
     * @param scene 广告展示场景
     */
    private void startInsert(String scene) {
        isClick=false;
        //优先检查缓存,如果缓存为空,直接拉取穿山甲的广告
        if(PlatformManager.getInstance().hasInsertAd()){
            PlatformManager.getInstance().showInsertAd(PlatformUtils.getInstance().getActivity(),onInsertListener);
            return;
        }
        PlatformManager.getInstance().loadInsert(PlatformUtils.getInstance().getActivity(),mCurrentId,scene,onInsertListener);
    }

    private OnTabScreenListener onInsertListener=new OnTabScreenListener() {

        @Override
        public void onSuccess(GMInterstitialFullAd interactionAd) {
//            Logger.d("onSuccess");
            Activity tempActivity = PlatformUtils.getInstance().getActivity();
            if(null!=interactionAd&&!tempActivity.isFinishing()){
                setShow(true);
                if(null!=mPlayerListener) mPlayerListener.onSuccess(null);
                interactionAd.showAd(tempActivity);
            }else{
                setShow(false);
            }
        }

        @Override
        public void onError(int code, String message, String adCode) {
            setShow(false);
//            Logger.d("onError,code:"+code+",message:"+message+",id:"+adCode);
            if(null!=mPlayerListener) mPlayerListener.onError(code,message,mCurrentId);
        }

        @Override
        public void onShow() {
            setShow(true);
//            Logger.d("onShow");
            if(null!=mPlayerListener) mPlayerListener.onShow();
        }

        @Override
        public void onClick() {
//            Logger.d("onClick");
            isClick=true;
            if(null!=mPlayerListener) mPlayerListener.onClick();
        }

        @Override
        public void onClose() {
            setShow(false);
//            Logger.d("onClose");
            PlatformManager.getInstance().onResetInsert();
            OnPlayListener onInsertListener=mPlayerListener;
            mPlayerListener=null;
            Result status=new Result();
            status.setAdCode(mCurrentId);
            status.setIsClick(isClick?"1":"0");
            if(null!=onInsertListener) onInsertListener.onClose(status);
            if(TextUtils.isEmpty(mCurrentId)){
                cacheInsertAd(mCurrentId);
            }
        }
    };

    public void setShow(boolean show) {

    }

    public void onReset(){
        if(null!=mHandler){
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    /**
     * 预缓存一条插屏广告
     * @param id 广告位ID
     */
    public void cacheInsertAd(String id) {
//        Logger.d("cacheInsert-->id:"+id);
        PlatformManager.getInstance().loadInsert(PlatformUtils.getInstance().getActivity(),id,null);
    }
}