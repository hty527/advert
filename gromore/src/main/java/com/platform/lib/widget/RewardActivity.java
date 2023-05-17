package com.platform.lib.widget;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import com.bytedance.msdk.api.v2.ad.reward.GMRewardAd;
import com.platform.lib.R;
import com.platform.lib.bean.Result;
import com.platform.lib.constants.AdConstance;
import com.platform.lib.listener.OnRewardVideoListener;
import com.platform.lib.manager.PlatformManager;
import com.platform.lib.manager.PlayManager;
import com.platform.lib.utils.Logger;

/**
 * created by hty
 * 2022/11/8
 * Desc:激励视频播放交互容器
 */
public class RewardActivity extends Activity implements Application.ActivityLifecycleCallbacks {

    private LoadingView mLoadingView;
    //播放场景(由宿主传入，sdk将回调给宿主这个标识场景)、广告位ID、此视频广告的ECPM、自定义透传字段
    private String play_scene,ad_code,mCpmInfo,mCustomData;
    //是否播放成功、是否点击了、是否是一个有效的播放、是否正在播放中
    private boolean success,isClick,rewardVerify,isPlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lib_activity_reward);
        PlayManager.getInstance().setShowing(true);
        mLoadingView = (LoadingView) findViewById(R.id.lib_loading_view);
        ((TextView) findViewById(R.id.lib_tv_close)).setText("关闭");
        getApplication().registerActivityLifecycleCallbacks(this);
        init(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        init(intent);
    }

    private void init(Intent intent) {
        ad_code = intent.getStringExtra("id");
        play_scene = intent.getStringExtra("scene");
        Logger.d("init-->id:"+ad_code+",scene:"+play_scene);
        if(TextUtils.isEmpty(ad_code)){
            error(PlatformManager.getInstance().getText(AdConstance.CODE_ID_INVALID));
            return;
        }
        if(PlatformManager.getInstance().isDevelop()){
            isClick=true;
            error(PlatformManager.getInstance().getText(AdConstance.CODE_DEVELOP)+",id:"+ad_code);
            return;
        }
        isClick=false;isPlay=false;
        playRewardVideo(ad_code);
    }

    /**
     * 开始准备播放激励视频广告
     */
    private void playRewardVideo(String ad_code) {
        PlatformManager.getInstance().loadRewardVideo(this,ad_code,play_scene,onRewardVideoListener);
    }

    private OnRewardVideoListener onRewardVideoListener=new OnRewardVideoListener() {

        @Override
        public void onLoading() {
            loading(PlatformManager.getInstance().getText(AdConstance.CODE_AD_LOADING));
        }

        @Override
        public void onSuccess(GMRewardAd gmRewardAd) {
            if(isPlay) return;
            if(!isFinishing()){
                if(null!=gmRewardAd){
                    try {
                        gmRewardAd.showRewardAd(RewardActivity.this);
                        isPlay=true;
                        PlayManager.getInstance().onSuccess(gmRewardAd);
                    }catch (Throwable e){
                        e.printStackTrace();
                        error("reward play error,"+e.getMessage());
                    }
                }else{
                    error("reward info invalid");
                }
            }
        }

        @Override
        public void onError(int code, String message, String adInfo) {
//            Logger.d("onError-->code:"+code+",message:"+message+",adInfo:"+adInfo+",success:"+success);
            if(success) return;
            if(PlatformManager.getInstance().isDevelop()){
                error("code:"+code+",message:"+message+",id:"+adInfo);
            }else{
                error("code:"+code+",message:"+message);
            }
            PlayManager.getInstance().onError(code,message,adInfo);
        }

        @Override
        public void onShow() {
            success=true;
            mCustomData=null;
            isPlay=true;
//            Logger.d("onShow-->");
            PlayManager.getInstance().onShow();
        }

        @Override
        public void onClick(GMRewardAd rewardAd) {
//            Logger.d("onClick-->:");
            isClick=true;
            success=true;
            PlayManager.getInstance().onClick(rewardAd);
        }

        @Override
        public void onRewardVerify() {
            success=true;
            rewardVerify=true;
            //Logger.d("onRewardVerify-->");
            PlayManager.getInstance().onRewardVerify();
        }

        @Override
        public void onClose(String cpmInfo, String customData) {
            Logger.d("onClose-->cpmInfo:"+cpmInfo+",customData:"+customData);
            success=true;
            RewardActivity.this.mCpmInfo =cpmInfo;
            RewardActivity.this.mCustomData=customData;
            finish();
        }
    };

    public void loading(String message){
        if(null!=mLoadingView) mLoadingView.showRequst(message);
        ((TextView) findViewById(R.id.lib_tv_close)).setVisibility(View.INVISIBLE);
    }

    public void error(String message){
        PlayManager.getInstance().onError(0,message, ad_code);
        if(null!=mLoadingView) mLoadingView.showResult(message);
        TextView closeView = (TextView) findViewById(R.id.lib_tv_close);
        closeView.setVisibility(View.VISIBLE);
        closeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        getApplication().unregisterActivityLifecycleCallbacks(this);
        PlayManager.getInstance().setShowing(false);
        PlatformManager.getInstance().onResetReward();
        super.onDestroy();
        if(PlatformManager.getInstance().isDevelop()||success){
            Result status = new Result();
            status.setAdCode(TextUtils.isEmpty(ad_code)?"0":ad_code);
            status.setIsClick(PlatformManager.getInstance().isDevelop()?"1":isClick?"1":"0");
            status.setCpmInfo(mCpmInfo);
            status.setPlatformId(PlatformManager.getInstance().getAdnPlatformId());
            status.setCustomData(mCustomData);
            status.setRewardVerify(rewardVerify);
            PlayManager.getInstance().onClose(status);
        }else{
            PlayManager.getInstance().onClose(null);
        }
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {
//        if(activity instanceof BaseProxyActivity){//KsRewardVideoActivity
////            Logger.d("onActivityCreated-->快手-->");
//        }else if(activity instanceof ADActivity){//RewardvideoPortraitADActivity
////            Logger.d("onActivityCreated-->优量汇-->");
//        }else if(activity instanceof GenerateProxyActivity){//TTRewardVideoActivity、GenerateProxyActivity
////            Logger.d("onActivityCreated-->穿山甲-->");
//        }
        PlayManager.getInstance().onAdvertActivityCreated(activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {}

    @Override
    public void onActivityResumed(Activity activity) {}

    @Override
    public void onActivityPaused(Activity activity) {}

    @Override
    public void onActivityStopped(Activity activity) {}

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {}

    @Override
    public void onActivityDestroyed(Activity activity) {}
}