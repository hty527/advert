package com.platform.simple.adn.pangle;

import android.app.Activity;
import android.content.Context;
import com.bytedance.msdk.api.v2.GMAdConstant;
import com.bytedance.msdk.api.v2.ad.custom.GMCustomAdError;
import com.bytedance.msdk.api.v2.ad.custom.bean.GMCustomServiceConfig;
import com.bytedance.msdk.api.v2.ad.custom.fullvideo.GMCustomFullVideoAdapter;
import com.bytedance.msdk.api.v2.slot.GMAdSlotFullVideo;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.bytedance.sdk.openadsdk.TTFullScreenVideoAd;
import com.platform.simple.TTNumberUtil;
import java.util.Map;

public class PangleCustomerFullVideo extends GMCustomFullVideoAdapter {

    private TTFullScreenVideoAd mTTFullScreenVideoAd;
    private boolean isLoadSuccess;

    @Override
    public void load(Context context, GMAdSlotFullVideo adSlot, GMCustomServiceConfig serviceConfig) {
        TTAdNative adNativeLoader = TTAdSdk.getAdManager().createAdNative(context);
        com.bytedance.sdk.openadsdk.AdSlot.Builder adSlotBuilder = new com.bytedance.sdk.openadsdk.AdSlot.Builder()
                .setCodeId(serviceConfig.getADNNetworkSlotId()) //广告位id
                .setSupportDeepLink(true)
                .setAdCount(1); //请求广告数量为1到3条
        adNativeLoader.loadFullScreenVideoAd(adSlotBuilder.build(), new TTAdNative.FullScreenVideoAdListener() {
            @Override
            public void onError(int code, String message) {
                callLoadFail(new GMCustomAdError(code, message));
            }

            @Override
            public void onFullScreenVideoAdLoad(TTFullScreenVideoAd ttFullScreenVideoAd) {
                mTTFullScreenVideoAd = ttFullScreenVideoAd;
                isLoadSuccess = true;
                mTTFullScreenVideoAd.setFullScreenVideoAdInteractionListener(new TTFullScreenVideoAd.FullScreenVideoAdInteractionListener() {
                    @Override
                    public void onAdShow() {
                        callFullVideoAdShow();
                    }

                    @Override
                    public void onAdVideoBarClick() {
                        callFullVideoAdClick();
                    }

                    @Override
                    public void onAdClose() {
                        callFullVideoAdClosed();
                    }

                    @Override
                    public void onVideoComplete() {
                        callFullVideoComplete();
                    }

                    @Override
                    public void onSkippedVideo() {
                        callFullVideoSkippedVideo();
                    }
                });

                // 获取adn的extra信息（可选），注意需要在callLoadSuccess之前设置
                setMediaExtraInfo(mTTFullScreenVideoAd.getMediaExtraInfo());

                if (isClientBidding()) {//bidding广告类型
                    Map<String, Object> extraInfo = mTTFullScreenVideoAd.getMediaExtraInfo();
                    double cpm = 0;
                    //设置cpm
                    if (extraInfo != null) {
                        cpm = TTNumberUtil.getValue(extraInfo.get("price"));
                    }
                    callLoadSuccess(cpm);  //bidding广告成功回调，回传竞价广告价格
                } else {
                    callLoadSuccess();//普通广告成功回调
                }
            }

            @Override
            public void onFullScreenVideoCached() {
            }

            @Override
            public void onFullScreenVideoCached(TTFullScreenVideoAd ttFullScreenVideoAd) {
                isLoadSuccess = true;
                callAdVideoCache();
            }
        });
    }

    @Override
    public void showAd(Activity activity) {
        if (mTTFullScreenVideoAd != null) {
            mTTFullScreenVideoAd.showFullScreenVideoAd(activity);
        }
    }

    @Override
    public GMAdConstant.AdIsReadyStatus isReadyStatus() {
        if (mTTFullScreenVideoAd != null && mTTFullScreenVideoAd.getExpirationTimestamp() > System.currentTimeMillis()) {
            return GMAdConstant.AdIsReadyStatus.AD_IS_READY;
        } else {
            return GMAdConstant.AdIsReadyStatus.AD_IS_EXPIRED;
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
