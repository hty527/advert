package com.platform.lib.widget;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import com.bytedance.msdk.api.reward.RewardItem;
import com.bytedance.msdk.api.v2.ad.reward.GMRewardAd;
import com.bytedance.pangle.activity.GenerateProxyActivity;
import com.kwad.sdk.api.proxy.BaseProxyActivity;
import com.platform.lib.R;
import com.platform.lib.bean.Result;
import com.platform.lib.constants.AdConstance;
import com.platform.lib.listener.OnRewardVideoListener;
import com.platform.lib.manager.PlatformManager;
import com.platform.lib.manager.PlayManager;
import com.platform.lib.utils.Logger;
import com.qq.e.ads.ADActivity;

/**
 * created by hty
 * 2022/11/8
 * Desc:激励视频播放交互容器
 */
public class RewardActivity extends Activity implements Application.ActivityLifecycleCallbacks {

    private LoadingView mLoadingView;
    //播放场景(由宿主传入，sdk将回调给宿主这个标识场景)、广告位ID、此视频广告的ECPM
    private String play_scene,ad_code,ad_ecpm;
    //是否播放成功、是否点击了
    private boolean success=false,isClick=false;
    //广告容器宿主
    private Activity mAdActivity;
    //第三方广告平台标识，请参阅：GMNetworkPlatformConst类
    private int mPlatformId;

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
        ad_ecpm=null;
        Logger.d("init-->id:"+ad_code+",scene:"+play_scene);
        if(TextUtils.isEmpty(ad_code)){
            error(PlatformManager.getInstance().getText(AdConstance.CODE_ID_INVALID)+",id:"+ad_code);
            return;
        }
        if(PlatformManager.getInstance().isDevelop()){
            isClick=true;
            error(PlatformManager.getInstance().getText(AdConstance.CODE_DEVELOP)+",id:"+ad_code);
            return;
        }
        isClick=false;
        playRewardVideo(ad_code);
    }

    /**
     * 开始准备播放激励视频广告
     */
    private void playRewardVideo(String ad_code) {
        loading(PlatformManager.getInstance().getText(AdConstance.CODE_AD_LOADING));
        PlatformManager.getInstance().loadRewardVideo(this,ad_code,play_scene,onRewardVideoListener);
    }

    private OnRewardVideoListener onRewardVideoListener=new OnRewardVideoListener() {

        @Override
        public void onSuccess(GMRewardAd gmRewardAd) {
            if(!isFinishing()){
                if(null!=gmRewardAd&&gmRewardAd.isReady()){
                    gmRewardAd.showRewardAd(RewardActivity.this);
                    PlayManager.getInstance().onSuccess(gmRewardAd);
                }else{
                    error("reward info invalid");
                }
            }
        }

        @Override
        public void onError(int code, String message, String adInfo) {
//            Logger.d("onError-->code:"+code+",message:"+message+",adInfo:"+adInfo+",success:"+success);
            if(success) return;
            error("code:"+code+",message:"+message+",id:"+adInfo);
            PlayManager.getInstance().onError(code,message,adInfo);
        }

        @Override
        public void onShow() {
            onShow(null);
        }

        @Override
        public void onShow(String ecpm) {
            ad_ecpm =ecpm;
            success=true;
            getPlatformId();
            Logger.d("onShow-->ecpm:"+ecpm+",platformId:"+ mPlatformId);
            PlayManager.getInstance().onShow();
        }

        @Override
        public void onRewardVerify(RewardItem rewardItem) {
            success=true;
            if(TextUtils.isEmpty(ad_ecpm)){
                ad_ecpm = PlatformManager.getInstance().getEcpm();
            }
            //Logger.d("onRewardVerify-->ecpm:"+ad_ecpm+",rewardItem:"+(null!=rewardItem?rewardItem.getCustomData():null));
            getPlatformId();
            PlayManager.getInstance().onRewardVerify(rewardItem);
        }

        @Override
        public void onClose() {
//            Logger.d("onClose-->:");
            success=true;
            finish();
        }

        @Override
        public void onClick() {
//            Logger.d("onClick-->:");
            isClick=true;
            success=true;
            getPlatformId();
            PlayManager.getInstance().onClick();
        }
    };

    private void getPlatformId() {
        if(0==mPlatformId){
            mPlatformId = PlatformManager.getInstance().getAdnPlatformId();
        }
    }

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
    public void finish() {
        //先将Activity回调给需要关心的宿主
        if(null!=mAdActivity){
            PlayManager.getInstance().closeActivity(mAdActivity);
        }
        getApplication().unregisterActivityLifecycleCallbacks(this);
        PlayManager.getInstance().setShowing(false);
        PlatformManager.getInstance().onResetReward();
        super.finish();
        if(PlatformManager.getInstance().isDevelop()||success){
            Result status = new Result();
            status.setAd_code(TextUtils.isEmpty(ad_code)?"0":ad_code);
            status.setIs_click(PlatformManager.getInstance().isDevelop()?"1":isClick?"1":"0");
            status.setEcpm(ad_ecpm);
            status.setPlatformId(mPlatformId);
            PlayManager.getInstance().onClose(status);
        }else{
            PlayManager.getInstance().onClose(null);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        Logger.d("onDestroy-->");
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {
        try {
            if(activity instanceof BaseProxyActivity){//KsRewardVideoActivity
//            Logger.d("onActivityCreated-->快手-->");
                this.mAdActivity=activity;
            }else if(activity instanceof ADActivity){//RewardvideoPortraitADActivity
//            Logger.d("onActivityCreated-->优量汇-->");
                this.mAdActivity=activity;
            }else if(activity instanceof GenerateProxyActivity){//TTRewardVideoActivity、GenerateProxyActivity
//            Logger.d("onActivityCreated-->穿山甲-->");
                this.mAdActivity=activity;
            }
            if(null!=mAdActivity){
                PlayManager.getInstance().openActivity(mAdActivity);
            }
        }catch (Throwable e){
        }
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