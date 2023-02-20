package com.platform.simple.adn.gdt;

import android.view.View;
import com.bytedance.msdk.api.v2.GMAdConstant;
import com.bytedance.msdk.api.v2.ad.custom.GMCustomAdError;
import com.bytedance.msdk.api.v2.ad.custom.nativeAd.GMCustomNativeAd;
import com.bytedance.msdk.api.v2.slot.GMAdSlotNative;
import com.platform.lib.utils.ThreadUtils;
import com.platform.lib.constants.AdConstance;
import com.qq.e.ads.nativ.NativeExpressADView;
import com.qq.e.ads.nativ.NativeExpressMediaListener;
import com.qq.e.comm.constants.AdPatternType;
import com.qq.e.comm.pi.AdData;
import com.qq.e.comm.util.AdError;

/**
 * YLH 信息流 ADN提供渲染（模板渲染）广告对象
 */
public class GdtNativeExpressAd extends GMCustomNativeAd {

    private static final String TAG = GdtNativeExpressAd.class.getSimpleName();

    private NativeExpressADView mNativeExpressADView;

    public GdtNativeExpressAd(NativeExpressADView feedAd, GMAdSlotNative adSlot) {
        mNativeExpressADView = feedAd;
        AdData adData = mNativeExpressADView.getBoundData();
        if (adData.getAdPatternType() == AdPatternType.NATIVE_VIDEO) {
            mNativeExpressADView.preloadVideo();
            mNativeExpressADView.setMediaListener(new NativeExpressMediaListener() {
                @Override
                public void onVideoInit(NativeExpressADView nativeExpressADView) {
                }

                @Override
                public void onVideoLoading(NativeExpressADView nativeExpressADView) {
                }

                @Override
                public void onVideoCached(NativeExpressADView nativeExpressADView) {
                }

                @Override
                public void onVideoReady(NativeExpressADView nativeExpressADView, long l) {
                }

                @Override
                public void onVideoStart(NativeExpressADView nativeExpressADView) {
                    callNativeVideoStart();
                }

                @Override
                public void onVideoPause(NativeExpressADView nativeExpressADView) {
                    callNativeVideoPause();
                }

                @Override
                public void onVideoComplete(NativeExpressADView nativeExpressADView) {
                    callNativeVideoCompleted();
                }

                @Override
                public void onVideoError(NativeExpressADView nativeExpressADView, AdError adError) {
                    if (adError != null) {
                        callNativeVideoError(new GMCustomAdError(adError.getErrorCode(), adError.getErrorMsg()));
                    } else {
                        callNativeVideoError(new GMCustomAdError(AdConstance.VIDEO_ERROR, "video error"));
                    }
                }

                @Override
                public void onVideoPageOpen(NativeExpressADView nativeExpressADView) {
                }

                @Override
                public void onVideoPageClose(NativeExpressADView nativeExpressADView) {
                }
            });
            setAdImageMode(GMAdConstant.IMAGE_MODE_VIDEO);
        } else if (adData.getAdPatternType() == AdPatternType.NATIVE_1IMAGE_2TEXT || adData.getAdPatternType() == AdPatternType.NATIVE_2IMAGE_2TEXT) {
            setAdImageMode(GMAdConstant.IMAGE_MODE_LARGE_IMG);
        } else if (adData.getAdPatternType() == AdPatternType.NATIVE_3IMAGE) {
            setAdImageMode(GMAdConstant.IMAGE_MODE_GROUP_IMG);
        } else {
            setAdImageMode(GMAdConstant.IMAGE_MODE_LARGE_IMG);
        }
        setTitle(adData.getTitle());
        setDescription(adData.getDesc());
        setInteractionType(GMAdConstant.INTERACTION_TYPE_LANDING_PAGE);
    }

    /**
     * 如果Adn 有dislike接口需要返回true
     */
    @Override
    public boolean hasDislike() {
        return true;
    }

    @Override
    public void render() {
        /**
         * 先切子线程，再在子线程中切主线程进行广告展示
         */
        ThreadUtils.runOnUIThreadByThreadPool(new Runnable() {
            @Override
            public void run() {
                if (mNativeExpressADView != null) {
                    mNativeExpressADView.render();
                }
            }
        });
    }

    @Override
    public View getExpressView() {
        return mNativeExpressADView;
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
        /**
         * 在子线程进行onDestroy操作
         */
        ThreadUtils.runOnThreadPool(new Runnable() {
            @Override
            public void run() {
                if (mNativeExpressADView != null) {
                    mNativeExpressADView.destroy();
                    mNativeExpressADView = null;
                }
            }
        });
    }

    @Override
    public GMAdConstant.AdIsReadyStatus isReadyStatus() {
        if (mNativeExpressADView != null && mNativeExpressADView.isValid()) {
            return GMAdConstant.AdIsReadyStatus.AD_IS_READY;
        } else {
            return GMAdConstant.AdIsReadyStatus.AD_IS_NOT_READY;
        }
    }
}