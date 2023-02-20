package com.platform.simple.adn.gdt;

import android.app.Activity;
import android.content.Context;

import com.bytedance.msdk.api.reward.RewardItem;
import com.bytedance.msdk.api.v2.GMAdConstant;
import com.bytedance.msdk.api.v2.ad.custom.GMCustomAdError;
import com.bytedance.msdk.api.v2.ad.custom.bean.GMCustomServiceConfig;
import com.bytedance.msdk.api.v2.ad.custom.reward.GMCustomRewardAdapter;
import com.bytedance.msdk.api.v2.slot.GMAdSlotRewardVideo;
import com.platform.lib.utils.ThreadUtils;
import com.platform.lib.constants.AdConstance;
import com.qq.e.ads.rewardvideo.RewardVideoAD;
import com.qq.e.ads.rewardvideo.RewardVideoADListener;
import com.qq.e.comm.util.AdError;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * YLH 激励视频广告自定义Adapter
 */
public class GdtCustomerReward extends GMCustomRewardAdapter {

    private static final String TAG = GdtCustomerReward.class.getSimpleName();

    private volatile RewardVideoAD mRewardVideoAD;
    private boolean isLoadSuccess;

    @Override
    public void load(Context context, GMAdSlotRewardVideo adSlot, GMCustomServiceConfig serviceConfig) {
        /**
         * 在子线程中进行广告加载
         */
        ThreadUtils.runOnThreadPool(new Runnable() {
            @Override
            public void run() {
                mRewardVideoAD = new RewardVideoAD(context, serviceConfig.getADNNetworkSlotId(), new RewardVideoADListener() {
                    @Override
                    public void onADLoad() {
                        isLoadSuccess = true;
                        if (isBidding()) {//bidding类型广告
                            double ecpm = mRewardVideoAD.getECPM(); //当无权限调用该接口时，SDK会返回错误码-1
                            if (ecpm < 0) {
                                ecpm = 0;
                            }
                            callLoadSuccess(ecpm);
                        } else {//普通类型广告
                            callLoadSuccess();
                        }
                    }

                    @Override
                    public void onVideoCached() {
                        callAdVideoCache();
                    }

                    @Override
                    public void onADShow() {
                        callRewardedAdShow();
                    }

                    @Override
                    public void onADExpose() {
                    }

                    @Override
                    public void onReward(Map<String, Object> map) {
                        callRewardVerify(new RewardItem() {
                            @Override
                            public boolean rewardVerify() {
                                return true;
                            }

                            @Override
                            public float getAmount() {
                                if (adSlot != null) {
                                    return adSlot.getRewardAmount();
                                }
                                return 0;
                            }

                            @Override
                            public String getRewardName() {
                                if (adSlot != null) {
                                    return adSlot.getRewardName();
                                }
                                return "";
                            }

                            @Override
                            public Map<String, Object> getCustomData() {
                                return map;
                            }
                        });
                    }

                    @Override
                    public void onADClick() {
                        callRewardClick();
                    }

                    @Override
                    public void onVideoComplete() {
                        callRewardVideoComplete();
                    }

                    @Override
                    public void onADClose() {
                        callRewardedAdClosed();
                    }

                    @Override
                    public void onError(AdError adError) {
                        isLoadSuccess = false;
                        if (adError != null) {
                            callLoadFail(new GMCustomAdError(adError.getErrorCode(), adError.getErrorMsg()));
                        } else {
                            callLoadFail(new GMCustomAdError(AdConstance.LOAD_ERROR, "no ad"));
                        }
                    }
                }, !adSlot.isMuted());
                mRewardVideoAD.loadAD();
            }
        });
    }

    @Override
    public void showAd(Activity activity) {
        /**
         * 先切子线程，再在子线程中切主线程进行广告展示
         */
        ThreadUtils.runOnUIThreadByThreadPool(new Runnable() {
            @Override
            public void run() {
                if (mRewardVideoAD != null) {
                    mRewardVideoAD.showAD(activity);
                }
            }
        });

    }

    @Override
    public GMAdConstant.AdIsReadyStatus isReadyStatus() {
        /**
         * 在子线程中进行广告是否可用的判断
         */
        Future<GMAdConstant.AdIsReadyStatus> future = ThreadUtils.runOnThreadPool(new Callable<GMAdConstant.AdIsReadyStatus>() {
            @Override
            public GMAdConstant.AdIsReadyStatus call() throws Exception {
                if (mRewardVideoAD != null && mRewardVideoAD.isValid()) {
                    return GMAdConstant.AdIsReadyStatus.AD_IS_READY;
                } else {
                    return GMAdConstant.AdIsReadyStatus.AD_IS_NOT_READY;
                }
            }
        });
        try {
            GMAdConstant.AdIsReadyStatus result = future.get(500, TimeUnit.MILLISECONDS);//设置500毫秒的总超时，避免线程阻塞
            if (result != null) {
                return result;
            } else {
                return GMAdConstant.AdIsReadyStatus.AD_IS_NOT_READY;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return GMAdConstant.AdIsReadyStatus.AD_IS_NOT_READY;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRewardVideoAD = null;
    }

    /**
     * 是否是Bidding广告
     *
     * @return
     */
    public boolean isBidding() {
        int biddingType = getBiddingType();
        return biddingType == GMAdConstant.AD_TYPE_CLIENT_BIDING;
    }

    @Override
    public boolean isReady() {
        boolean isReadyStatus = isReadyStatus() == GMAdConstant.AdIsReadyStatus.AD_IS_READY;
        return isReadyStatus;
    }

    @Override
    public void receiveBidResult(boolean win, double winnerPrice, int loseReason, Map<String, Object> extra) {
        super.receiveBidResult(win, winnerPrice, loseReason, extra);
    }
}
