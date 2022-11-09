package com.platform.lib.adn.gdt;

import android.content.Context;

import com.bytedance.msdk.api.v2.GMAdConstant;
import com.bytedance.msdk.api.v2.ad.custom.GMCustomAdError;
import com.bytedance.msdk.api.v2.ad.custom.bean.GMCustomServiceConfig;
import com.bytedance.msdk.api.v2.ad.custom.nativeAd.GMCustomNativeAdapter;
import com.bytedance.msdk.api.v2.slot.GMAdSlotNative;
import com.platform.lib.utils.ThreadUtils;
import com.platform.lib.constants.AdConstance;
import com.qq.e.ads.nativ.ADSize;
import com.qq.e.ads.nativ.NativeADUnifiedListener;
import com.qq.e.ads.nativ.NativeExpressAD;
import com.qq.e.ads.nativ.NativeExpressADView;
import com.qq.e.ads.nativ.NativeUnifiedAD;
import com.qq.e.ads.nativ.NativeUnifiedADData;
import com.qq.e.comm.util.AdError;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * YLH 信息流广告自定义Adapter
 */
public class GdtCustomerNative extends GMCustomNativeAdapter {

    private static final String TAG = GdtCustomerNative.class.getSimpleName();

    @Override
    public void load(Context context, GMAdSlotNative adSlot, GMCustomServiceConfig serviceConfig) {
        /**
         * 在子线程中进行广告加载
         */
        ThreadUtils.runOnThreadPool(new Runnable() {
            @Override
            public void run() {
                if (isNativeAd()) {
                    //自渲染类型
                    NativeUnifiedAD nativeUnifiedAD = new NativeUnifiedAD(context, serviceConfig.getADNNetworkSlotId(),
                            new NativeADUnifiedListener() {
                                @Override
                                public void onADLoaded(List<NativeUnifiedADData> list) {
                                    List<GdtNativeAd> tempList = new ArrayList<>();
                                    for (NativeUnifiedADData feedAd : list) {
                                        GdtNativeAd gdtNativeAd = new GdtNativeAd(context, feedAd, adSlot);
                                        if (isBidding()) {//bidding广告类型
                                            double ecpm = feedAd.getECPM();//当无权限调用该接口时，SDK会返回错误码-1
                                            if (ecpm < 0) {
                                                ecpm = 0;
                                            }
                                            gdtNativeAd.setBiddingPrice(ecpm); //回传竞价广告价格
                                        }
                                        tempList.add(gdtNativeAd);
                                    }
                                    callLoadSuccess(tempList);
                                }

                                @Override
                                public void onNoAD(AdError adError) {
                                    if (adError != null) {
                                        callLoadFail(new GMCustomAdError(adError.getErrorCode(), adError.getErrorMsg()));
                                    } else {
                                        callLoadFail(new GMCustomAdError(AdConstance.LOAD_ERROR, "no ad"));
                                    }
                                }
                            });
                    nativeUnifiedAD.setMaxVideoDuration(adSlot.getGMAdSlotGDTOption().getGDTMaxVideoDuration());
                    nativeUnifiedAD.setMinVideoDuration(adSlot.getGMAdSlotGDTOption().getGDTMinVideoDuration());
                    nativeUnifiedAD.loadData(1);
                } else if (isExpressRender()) {
                    //模板类型
                    NativeExpressAD nativeExpressAD = new NativeExpressAD(context, getAdSize(adSlot), serviceConfig.getADNNetworkSlotId(),
                            new NativeExpressAD.NativeExpressADListener() {

                                private Map<NativeExpressADView, GdtNativeExpressAd> mListenerMap = new HashMap<>();

                                @Override
                                public void onADLoaded(List<NativeExpressADView> list) {
                                    List<GdtNativeExpressAd> tempList = new ArrayList<>();
                                    for (NativeExpressADView feedAd : list) {
                                        GdtNativeExpressAd gdtNativeAd = new GdtNativeExpressAd(feedAd, adSlot);
                                        if (isBidding()) {//bidding广告类型
                                            double ecpm = feedAd.getECPM();//当无权限调用该接口时，SDK会返回错误码-1
                                            if (ecpm < 0) {
                                                ecpm = 0;
                                            }
                                            gdtNativeAd.setBiddingPrice(ecpm); //回传竞价广告价格
                                        }
                                        mListenerMap.put(feedAd, gdtNativeAd);
                                        tempList.add(gdtNativeAd);
                                    }
                                    callLoadSuccess(tempList);
                                }

                                @Override
                                public void onRenderFail(NativeExpressADView nativeExpressADView) {
                                    GdtNativeExpressAd gdtNativeAd = mListenerMap.get(nativeExpressADView);
                                    if (gdtNativeAd != null) {
                                        gdtNativeAd.callNativeRenderFail(nativeExpressADView, "render fail", AdConstance.RENDER_FAIL);
                                    }
                                }

                                @Override
                                public void onRenderSuccess(NativeExpressADView nativeExpressADView) {
                                    GdtNativeExpressAd gdtNativeAd = mListenerMap.get(nativeExpressADView);
                                    if (gdtNativeAd != null) {
                                        gdtNativeAd.callNativeRenderSuccess(ADSize.FULL_WIDTH, ADSize.AUTO_HEIGHT);
                                    }
                                }

                                @Override
                                public void onADExposure(NativeExpressADView nativeExpressADView) {
                                    GdtNativeExpressAd gdtNativeAd = mListenerMap.get(nativeExpressADView);
                                    if (gdtNativeAd != null) {
                                        gdtNativeAd.callNativeAdShow();
                                    }
                                }

                                @Override
                                public void onADClicked(NativeExpressADView nativeExpressADView) {
                                    GdtNativeExpressAd gdtNativeAd = mListenerMap.get(nativeExpressADView);
                                    if (gdtNativeAd != null) {
                                        gdtNativeAd.callNativeAdClick();
                                    }
                                }

                                @Override
                                public void onADClosed(NativeExpressADView nativeExpressADView) {
                                    GdtNativeExpressAd gdtNativeAd = mListenerMap.get(nativeExpressADView);
                                    if (gdtNativeAd != null) {
                                        gdtNativeAd.onDestroy();
                                    }
                                    mListenerMap.remove(nativeExpressADView);
                                }

                                @Override
                                public void onADLeftApplication(NativeExpressADView nativeExpressADView) {
                                }

                                @Override
                                public void onNoAD(AdError adError) {
                                    if (adError != null) {
                                        callLoadFail(new GMCustomAdError(adError.getErrorCode(), adError.getErrorMsg()));
                                    } else {
                                        callLoadFail(new GMCustomAdError(AdConstance.LOAD_ERROR, "no ad"));
                                    }
                                }
                            });
                    nativeExpressAD.loadAD(1);
                } else {
                    //其他类型，开发者如果有需要，请在平台自行配置json,然后通过 serviceConfig.getCustomAdapterJson() 获取配置
                }
            }
        });
    }

    private ADSize getAdSize(GMAdSlotNative adSlot) {
        ADSize adSize = new ADSize(ADSize.FULL_WIDTH, ADSize.AUTO_HEIGHT); // 消息流中用AUTO_HEIGHT
        if (adSlot.getWidth() > 0) {
            adSize = new ADSize(adSlot.getWidth(), ADSize.AUTO_HEIGHT);
        }
        return adSize;
    }

    /**
     * 是否clientBidding广告
     *
     * @return
     */
    public boolean isBidding() {
        return getBiddingType() == GMAdConstant.AD_TYPE_CLIENT_BIDING;
    }

    @Override
    public void receiveBidResult(boolean win, double winnerPrice, int loseReason, Map<String, Object> extra) {
        super.receiveBidResult(win, winnerPrice, loseReason, extra);
    }
}
