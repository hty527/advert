package com.platform.simple.adn.pangle;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.bytedance.msdk.adapter.pangle.PangleAdapterUtils;
import com.bytedance.msdk.api.v2.GMAdConstant;
import com.bytedance.msdk.api.v2.ad.custom.GMCustomAdError;
import com.bytedance.msdk.api.v2.ad.custom.bean.GMCustomServiceConfig;
import com.bytedance.msdk.api.v2.ad.custom.interstitial.GMCustomInterstitialAdapter;
import com.bytedance.msdk.api.v2.slot.GMAdSlotInterstitial;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;

import java.util.List;
import java.util.Map;

public class PangleCustomerInterstitial extends GMCustomInterstitialAdapter {

    private TTNativeExpressAd mTTNativeExpressAd;
    private boolean isLoadSuccess;
    private PangleAdapterUtils TTNumberUtil;

    @Override
    public void load(Context context, GMAdSlotInterstitial adSlot, GMCustomServiceConfig serviceConfig) {
        TTAdNative adNativeLoader = TTAdSdk.getAdManager().createAdNative(context);
        com.bytedance.sdk.openadsdk.AdSlot.Builder adSlotBuilder = new com.bytedance.sdk.openadsdk.AdSlot.Builder()
                .setCodeId(serviceConfig.getADNNetworkSlotId()) //广告位id
                .setSupportDeepLink(true)
                .setAdCount(1); //请求广告数量为1到3条
        adNativeLoader.loadInteractionExpressAd(adSlotBuilder.build(), new TTAdNative.NativeExpressAdListener() {
            @Override
            public void onError(int code, String message) {
                callLoadFail(new GMCustomAdError(code, message));
            }

            @Override
            public void onNativeExpressAdLoad(List<TTNativeExpressAd> ads) {
                mTTNativeExpressAd = ads.get(0);
                mTTNativeExpressAd.setExpressInteractionListener(new TTNativeExpressAd.AdInteractionListener() {
                    @Override
                    public void onAdDismiss() {
                        callInterstitialClosed();
                    }

                    @Override
                    public void onAdClicked(View view, int i) {
                        callInterstitialAdClick();
                    }

                    @Override
                    public void onAdShow(View view, int i) {
                        callInterstitialShow();
                    }

                    @Override
                    public void onRenderFail(View view, String s, int i) {
                    }

                    @Override
                    public void onRenderSuccess(View view, float v, float v1) {
                        isLoadSuccess = true;
                    }
                });
                mTTNativeExpressAd.render();

                // 获取adn的extra信息（可选），注意需要在callLoadSuccess之前设置
                setMediaExtraInfo(mTTNativeExpressAd.getMediaExtraInfo());

                if (isClientBidding()) {//bidding广告类型
                    Map<String, Object> extraInfo = mTTNativeExpressAd.getMediaExtraInfo();
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
        });
    }

    @Override
    public void showAd(Activity activity) {
        if (mTTNativeExpressAd != null) {
            mTTNativeExpressAd.showInteractionExpressAd(activity);
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
