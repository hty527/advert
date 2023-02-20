package com.platform.simple.adn.pangle;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import com.bytedance.msdk.api.reward.RewardItem;
import com.bytedance.msdk.api.v2.GMAdConstant;
import com.bytedance.msdk.api.v2.ad.custom.GMCustomAdError;
import com.bytedance.msdk.api.v2.ad.custom.bean.GMCustomServiceConfig;
import com.bytedance.msdk.api.v2.ad.custom.reward.GMCustomRewardAdapter;
import com.bytedance.msdk.api.v2.slot.GMAdSlotRewardVideo;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.bytedance.sdk.openadsdk.TTRewardVideoAd;
import com.platform.lib.utils.TTNumberUtil;
import java.util.HashMap;
import java.util.Map;

public class PangleCustomerReward extends GMCustomRewardAdapter {

    private TTRewardVideoAd mTTRewardVideoAd;
    private boolean isLoadSuccess;

    @Override
    public void load(Context context, GMAdSlotRewardVideo adSlot, GMCustomServiceConfig serviceConfig) {
        TTAdNative adNativeLoader = TTAdSdk.getAdManager().createAdNative(context);
        com.bytedance.sdk.openadsdk.AdSlot.Builder adSlotBuilder = new com.bytedance.sdk.openadsdk.AdSlot.Builder()
                .setCodeId(serviceConfig.getADNNetworkSlotId()) //广告位id
                .setSupportDeepLink(true)
                .setAdCount(1); //请求广告数量为1到3条
        adNativeLoader.loadRewardVideoAd(adSlotBuilder.build(), new TTAdNative.RewardVideoAdListener() {
            @Override
            public void onError(int i, String s) {
                isLoadSuccess = false;
                callLoadFail(new GMCustomAdError(i, s));
            }

            @Override
            public void onRewardVideoAdLoad(TTRewardVideoAd ttRewardVideoAd) {
                mTTRewardVideoAd = ttRewardVideoAd;
                isLoadSuccess = true;
                mTTRewardVideoAd.setRewardAdInteractionListener(new TTRewardVideoAd.RewardAdInteractionListener() {
                    @Override
                    public void onAdShow() {
                        callRewardedAdShow();
                    }

                    @Override
                    public void onAdVideoBarClick() {
                        callRewardClick();
                    }

                    @Override
                    public void onAdClose() {
                        callRewardedAdClosed();
                    }

                    @Override
                    public void onVideoComplete() {
                        callRewardVideoComplete();
                    }

                    @Override
                    public void onVideoError() {
                        callRewardVideoError();
                    }

                    @Deprecated
                    @Override
                    public void onRewardVerify(boolean rewardVerify, int rewardAmount, String rewardName, int errorCode, String errorMsg) {

                    }

                    @Override
                    public void onRewardArrived(boolean isRewardValid, int rewardType, Bundle extraInfo) {
                        callRewardVerify(new RewardItem() {
                            @Override
                            public boolean rewardVerify() {
                                return isRewardValid;
                            }

                            @Override
                            public float getAmount() {
                                float amount = 0;
                                if(extraInfo != null) {
                                    amount = extraInfo.getInt(TTRewardVideoAd.REWARD_EXTRA_KEY_REWARD_AMOUNT);
                                }
                                return amount;
                            }

                            @Override
                            public String getRewardName() {
                                String rewardName = "";
                                if(extraInfo != null) {
                                    rewardName = extraInfo.getString(TTRewardVideoAd.REWARD_EXTRA_KEY_REWARD_NAME);
                                }
                                return rewardName;
                            }

                            @Override
                            public Map<String, Object> getCustomData() {
                                Map<String, Object> customData = new HashMap<>();
                                customData.put(KEY_EXTRA_INFO, extraInfo);
                                customData.put(KEY_REWARD_TYPE, rewardType);
                                return customData;
                            }
                        });
                    }

                    @Override
                    public void onSkippedVideo() {
                        callRewardSkippedVideo();
                    }
                });

                // 获取adn的extra信息（可选），注意需要在callLoadSuccess之前设置
                setMediaExtraInfo(mTTRewardVideoAd.getMediaExtraInfo());

                if (isClientBidding()) {//bidding广告类型
                    Map<String, Object> extraInfo = mTTRewardVideoAd.getMediaExtraInfo();
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

            @Override
            public void onRewardVideoCached() {
            }

            @Override
            public void onRewardVideoCached(TTRewardVideoAd ttRewardVideoAd) {
                callAdVideoCache();
            }
        });
    }

    @Override
    public void showAd(Activity activity) {
        if (mTTRewardVideoAd != null) {
            mTTRewardVideoAd.showRewardVideoAd(activity);
        }
    }

    @Override
    public GMAdConstant.AdIsReadyStatus isReadyStatus() {
        if (mTTRewardVideoAd != null && mTTRewardVideoAd.getExpirationTimestamp() > System.currentTimeMillis()) {
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

    @Override
    public void receiveBidResult(boolean win, double winnerPrice, int loseReason, Map<String, Object> extra) {
        super.receiveBidResult(win, winnerPrice, loseReason, extra);
    }
}
