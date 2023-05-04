package com.platform.simple.adn.pangle;

import android.content.Context;

import com.bytedance.msdk.api.v2.GMAdConstant;
import com.bytedance.msdk.api.v2.ad.custom.GMCustomAdError;
import com.bytedance.msdk.api.v2.ad.custom.bean.GMCustomServiceConfig;
import com.bytedance.msdk.api.v2.ad.custom.nativeAd.GMCustomNativeAdapter;
import com.bytedance.msdk.api.v2.slot.GMAdSlotNative;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.bytedance.sdk.openadsdk.TTFeedAd;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;
import com.platform.simple.TTNumberUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PangleCustomerNative extends GMCustomNativeAdapter {

    @Override
    public void load(Context context, GMAdSlotNative adSlot, GMCustomServiceConfig serviceConfig) {
        TTAdNative adNativeLoader = TTAdSdk.getAdManager().createAdNative(context);
        com.bytedance.sdk.openadsdk.AdSlot.Builder adSlotBuilder = new com.bytedance.sdk.openadsdk.AdSlot.Builder()
                .setCodeId(serviceConfig.getADNNetworkSlotId()) //广告位id
                .setSupportDeepLink(true)
                .setAdCount(1); //请求广告数量为1到3条
        if (isNativeAd()) {
            //自渲染
            adNativeLoader.loadFeedAd(adSlotBuilder.build(), new TTAdNative.FeedAdListener() {

                @Override
                public void onError(int i, String s) {
                    callLoadFail(new GMCustomAdError(i, s));
                }

                @Override
                public void onFeedAdLoad(List<TTFeedAd> list) {
                    List<PangleNativeAd> tempList = new ArrayList<>();
                    for (TTFeedAd feedAd : list) {
                        PangleNativeAd pangleNativeAd = new PangleNativeAd(feedAd);
                        if (isClientBidding()) {//bidding广告类型
                            Map<String, Object> extraInfo = feedAd.getMediaExtraInfo();
                            //设置cpm
                            if (extraInfo != null) {
                                double cpm = TTNumberUtil.getValue(extraInfo.get("price"));
                                pangleNativeAd.setBiddingPrice(cpm > 0 ? cpm : 0); //回传竞价广告价格
                            }
                        }
                        pangleNativeAd.setDislikeDialog(); //自渲染dialog弹窗
                        tempList.add(pangleNativeAd);
                    }
                    callLoadSuccess(tempList);
                }
            });
        } else {
            //模板
            adNativeLoader.loadNativeExpressAd(adSlotBuilder.build(), new TTAdNative.NativeExpressAdListener() {
                @Override
                public void onError(int i, String s) {
                    callLoadFail(new GMCustomAdError(i, s));
                }

                @Override
                public void onNativeExpressAdLoad(List<TTNativeExpressAd> list) {
                    List<PangleNativeExpressAd> tempList = new ArrayList<>();
                    for (TTNativeExpressAd feedAd : list) {
                        PangleNativeExpressAd pangleNativeExpressAd = new PangleNativeExpressAd(context, feedAd);
                        if (isClientBidding()) {//bidding广告类型
                            Map<String, Object> extraInfo = feedAd.getMediaExtraInfo();
                            //设置cpm
                            if (extraInfo != null) {
                                double cpm = TTNumberUtil.getValue(extraInfo.get("price"));
                                pangleNativeExpressAd.setBiddingPrice(cpm > 0 ? cpm : 0);//回传竞价广告价格
                            }
                        }
                        tempList.add(pangleNativeExpressAd);
                    }
                    callLoadSuccess(tempList);
                }
            });
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

    @Override
    public void receiveBidResult(boolean win, double winnerPrice, int loseReason, Map<String, Object> extra) {
        super.receiveBidResult(win, winnerPrice, loseReason, extra);
    }
}
