package com.platform.simple.adn.gdt;

import android.content.Context;
import android.os.SystemClock;
import android.view.ViewGroup;
import com.bytedance.msdk.api.v2.GMAdConstant;
import com.bytedance.msdk.api.v2.ad.custom.GMCustomAdError;
import com.bytedance.msdk.api.v2.ad.custom.bean.GMCustomServiceConfig;
import com.bytedance.msdk.api.v2.ad.custom.splash.GMCustomSplashAdapter;
import com.bytedance.msdk.api.v2.slot.GMAdSlotSplash;
import com.platform.lib.utils.ThreadUtils;
import com.platform.lib.constants.AdConstance;
import com.qq.e.ads.splash.SplashAD;
import com.qq.e.ads.splash.SplashADListener;
import com.qq.e.comm.util.AdError;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * YLH 开屏广告自定义Adapter
 */
public class GdtCustomerSplash extends GMCustomSplashAdapter {

    private static final String TAG =  GdtCustomerSplash.class.getSimpleName();
    private volatile SplashAD mSplashAD;
    private boolean isLoadSuccess;


    @Override
    public void load(Context context, GMAdSlotSplash adSlot, GMCustomServiceConfig serviceConfig) {
        /**
         * 在子线程中进行广告加载
         */
        ThreadUtils.runOnThreadPool(new Runnable() {
            @Override
            public void run() {
                mSplashAD = new SplashAD(context, serviceConfig.getADNNetworkSlotId(), new SplashADListener() {
                    @Override
                    public void onADDismissed() {
                        callSplashAdDismiss();
                    }

                    @Override
                    public void onNoAD(AdError adError) {
                        isLoadSuccess = false;
                        if (adError != null) {
                            callLoadFail(new GMCustomAdError(adError.getErrorCode(), adError.getErrorMsg()));
                        } else {
                            callLoadFail(new GMCustomAdError(AdConstance.LOAD_ERROR, "no ad"));
                        }
                    }

                    @Override
                    public void onADPresent() {
                    }

                    @Override
                    public void onADClicked() {
                        callSplashAdClicked();
                    }

                    @Override
                    public void onADTick(long l) {
                    }

                    @Override
                    public void onADExposure() {
                        callSplashAdShow();
                    }

                    @Override
                    public void onADLoaded(long expireTimestamp) {
                        long timeIntervalSec = expireTimestamp - SystemClock.elapsedRealtime();
                        if (timeIntervalSec > 1000) {
                            isLoadSuccess = true;
                            if (isBidding()) {//bidding类型广告
                                double ecpm = mSplashAD.getECPM(); //当无权限调用该接口时，SDK会返回错误码-1
                                if (ecpm < 0) {
                                    ecpm = 0;
                                }
                                callLoadSuccess(ecpm);  //bidding广告成功回调，回传竞价广告价格
                            } else { //普通类型广告
                                callLoadSuccess();
                            }
                        } else {
                            isLoadSuccess = false;
                            callLoadFail(new GMCustomAdError(AdConstance.LOAD_ERROR, "ad has expired"));
                        }
                    }
                }, 3000);
                mSplashAD.fetchAdOnly();
            }
        });
    }

    @Override
    public void showAd(ViewGroup container) {
        /**
         * 先切子线程，再在子线程中切主线程进行广告展示
         */
        ThreadUtils.runOnUIThreadByThreadPool(new Runnable() {
            @Override
            public void run() {
                if (mSplashAD != null && container != null) {
                    container.removeAllViews();
                    mSplashAD.showAd(container);
                }
            }
        });
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
        mSplashAD = null;
    }


    @Override
    public GMAdConstant.AdIsReadyStatus isReadyStatus() {

        /**
         * 在子线程中进行广告是否可用的判断
         */
        Future<GMAdConstant.AdIsReadyStatus> future = ThreadUtils.runOnThreadPool(new Callable<GMAdConstant.AdIsReadyStatus>() {
            @Override
            public GMAdConstant.AdIsReadyStatus call() throws Exception {
                if (mSplashAD != null && mSplashAD.isValid()) {
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

    /**
     * 是否是Bidding广告
     *
     * @return
     */
    public boolean isBidding() {
        return getBiddingType() == GMAdConstant.AD_TYPE_CLIENT_BIDING;
    }
}
