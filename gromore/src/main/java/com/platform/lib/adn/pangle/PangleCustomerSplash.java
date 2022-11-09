package com.platform.lib.adn.pangle;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import com.bytedance.msdk.api.v2.GMAdConstant;
import com.bytedance.msdk.api.v2.ad.custom.GMCustomAdError;
import com.bytedance.msdk.api.v2.ad.custom.bean.GMCustomServiceConfig;
import com.bytedance.msdk.api.v2.ad.custom.splash.GMCustomSplashAdapter;
import com.bytedance.msdk.api.v2.slot.GMAdSlotSplash;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.bytedance.sdk.openadsdk.TTSplashAd;
import com.platform.lib.utils.TTNumberUtil;
import java.util.Map;

public class PangleCustomerSplash extends GMCustomSplashAdapter {

    private TTSplashAd mTTSplashAd;

    @Override
    public void load(Context context, GMAdSlotSplash adSlot, GMCustomServiceConfig serviceConfig) {
        TTAdNative adNativeLoader = TTAdSdk.getAdManager().createAdNative(context);
        com.bytedance.sdk.openadsdk.AdSlot.Builder adSlotBuilder = new com.bytedance.sdk.openadsdk.AdSlot.Builder()
                .setCodeId(serviceConfig.getADNNetworkSlotId()) //广告位id
                .setSupportDeepLink(true)
                .setExpressViewAcceptedSize(1080,1920)
                .setAdCount(1); //请求广告数量为1到3条
        adNativeLoader.loadSplashAd(adSlotBuilder.build(), new TTAdNative.SplashAdListener() {
            @Override
            public void onError(int i, String s) {
                callLoadFail(new GMCustomAdError(i, s));
            }

            @Override
            public void onTimeout() {
            }

            @Override
            public void onSplashAdLoad(TTSplashAd ttSplashAd) {
                mTTSplashAd = ttSplashAd;
                mTTSplashAd.setSplashInteractionListener(new TTSplashAd.AdInteractionListener() {
                    @Override
                    public void onAdClicked(View view, int i) {
                        callSplashAdClicked();
                    }

                    @Override
                    public void onAdShow(View view, int i) {
                        callSplashAdShow();
                    }

                    @Override
                    public void onAdSkip() {
                        callSplashAdSkip();
                    }

                    @Override
                    public void onAdTimeOver() {
                        callSplashAdDismiss();
                    }
                });

                // 获取adn的extra信息（可选），注意需要在callLoadSuccess之前设置
                setMediaExtraInfo(mTTSplashAd.getMediaExtraInfo());

                if (isClientBidding()) {//bidding广告类型
                    Map<String, Object> extraInfo = mTTSplashAd.getMediaExtraInfo();
                    double cpm = 0;
                    //设置cpm
                    if (extraInfo != null) {
                        cpm = TTNumberUtil.getValue(extraInfo.get("price"));
                    }
                    callLoadSuccess(cpm);//bidding广告成功回调，回传竞价广告价格
                } else {
                    callLoadSuccess();//普通广告成功回调
                }
            }
        }, adSlot.getTimeOut());

    }

    @Override
    public void showAd(ViewGroup container) {
        if (mTTSplashAd != null) {
            View view = mTTSplashAd.getSplashView();
            if (view != null) {
                ViewParent parent = view.getParent();
                if (parent instanceof ViewGroup) {
                    ((ViewGroup) parent).removeView(view);
                }
                container.removeAllViews();
                container.addView(view);
            }
        }
    }

    /**
     * 是否clientBidding广告
     *
     * @return
     */
    public boolean isClientBidding() {
        return getBiddingType() == GMAdConstant.AD_TYPE_CLIENT_BIDING;
    }
}
