package com.platform.simple.adn.gdt;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import com.bytedance.msdk.api.format.TTMediaView;
import com.bytedance.msdk.api.format.TTNativeAdView;
import com.bytedance.msdk.api.v2.GMAdConstant;
import com.bytedance.msdk.api.v2.ad.custom.GMCustomAdError;
import com.bytedance.msdk.api.v2.ad.custom.nativeAd.GMCustomNativeAd;
import com.bytedance.msdk.api.v2.ad.nativeAd.GMNativeAdAppInfo;
import com.bytedance.msdk.api.v2.ad.nativeAd.GMViewBinder;
import com.bytedance.msdk.api.v2.slot.GMAdSlotNative;
import com.bytedance.msdk.api.v2.slot.paltform.GMAdSlotGDTOption;
import com.platform.lib.utils.ThreadUtils;
import com.platform.lib.constants.AdConstance;
import com.qq.e.ads.cfg.VideoOption;
import com.qq.e.ads.nativ.MediaView;
import com.qq.e.ads.nativ.NativeADEventListener;
import com.qq.e.ads.nativ.NativeADMediaListener;
import com.qq.e.ads.nativ.NativeUnifiedADAppMiitInfo;
import com.qq.e.ads.nativ.NativeUnifiedADData;
import com.qq.e.ads.nativ.widget.NativeAdContainer;
import com.qq.e.comm.constants.AdPatternType;
import com.qq.e.comm.util.AdError;
import java.util.ArrayList;
import java.util.List;

/**
 * YLH 信息流 开发者自渲染（自渲染）广告对象
 */
public class GdtNativeAd extends GMCustomNativeAd {

    private static final String TAG = GdtNativeAd.class.getSimpleName();

    private NativeUnifiedADData mNativeUnifiedADData;
    private GMAdSlotNative mGMAdSlotNative;
    private Context mContext;
    private String VIEW_TAG = "view_tag";

    public GdtNativeAd(Context context, NativeUnifiedADData feedAd, GMAdSlotNative adSlot) {
        mContext = context;
        mNativeUnifiedADData = feedAd;
        mGMAdSlotNative = adSlot;
        NativeUnifiedADAppMiitInfo info = mNativeUnifiedADData.getAppMiitInfo();
        GMNativeAdAppInfo nativeAdAppInfo = new GMNativeAdAppInfo();
        if (info != null) {
            nativeAdAppInfo.setAppName(info.getAppName());
            nativeAdAppInfo.setAuthorName(info.getAuthorName());
            nativeAdAppInfo.setPackageSizeBytes(info.getPackageSizeBytes());
            nativeAdAppInfo.setPermissionsUrl(info.getPermissionsUrl());
            nativeAdAppInfo.setPrivacyAgreement(info.getPrivacyAgreement());
            nativeAdAppInfo.setVersionName(info.getVersionName());
        }
        setNativeAdAppInfo(nativeAdAppInfo);
        setTitle(mNativeUnifiedADData.getTitle());
        setDescription(mNativeUnifiedADData.getDesc());
        setActionText(mNativeUnifiedADData.getCTAText());
        setIconUrl(mNativeUnifiedADData.getIconUrl());
        setImageUrl(mNativeUnifiedADData.getImgUrl());
        setImageWidth(mNativeUnifiedADData.getPictureWidth());
        setImageHeight(mNativeUnifiedADData.getPictureHeight());
        setImageList(mNativeUnifiedADData.getImgList());
        setStarRating(mNativeUnifiedADData.getAppScore());
        setSource(mNativeUnifiedADData.getTitle());

        if (mNativeUnifiedADData.getAdPatternType() == AdPatternType.NATIVE_VIDEO) {
            setAdImageMode(GMAdConstant.IMAGE_MODE_VIDEO);
        } else if (mNativeUnifiedADData.getAdPatternType() == AdPatternType.NATIVE_1IMAGE_2TEXT
                || mNativeUnifiedADData.getAdPatternType() == AdPatternType.NATIVE_2IMAGE_2TEXT) {
            setAdImageMode(GMAdConstant.IMAGE_MODE_LARGE_IMG);
        } else if (mNativeUnifiedADData.getAdPatternType() == AdPatternType.NATIVE_3IMAGE) {
            setAdImageMode(GMAdConstant.IMAGE_MODE_GROUP_IMG);
        }

        if (mNativeUnifiedADData.isAppAd()) {
            setInteractionType(GMAdConstant.INTERACTION_TYPE_DOWNLOAD);
        } else {
            setInteractionType(GMAdConstant.INTERACTION_TYPE_LANDING_PAGE);
        }
    }

    @Override
    public void registerViewForInteraction(ViewGroup container, List<View> clickViews, List<View> creativeViews, GMViewBinder viewBinder) {
        /**
         * 先切子线程，再在子线程中切主线程进行广告展示
         */
        ThreadUtils.runOnUIThreadByThreadPool(new Runnable() {
            @Override
            public void run() {
                if (mNativeUnifiedADData != null && container instanceof TTNativeAdView) {
                    TTNativeAdView nativeAdView = (TTNativeAdView) container;
                    NativeAdContainer nativeAdContainer;

                    if (nativeAdView.getChildAt(0) instanceof NativeAdContainer) {
                        //gdt会自动添加logo，会出现重复添加，需要把logo移除
                        nativeAdContainer = (NativeAdContainer) nativeAdView.getChildAt(0);
                        for (int i = 0; i < nativeAdContainer.getChildCount(); ) {
                            View view = nativeAdContainer.getChildAt(i);
                            if (view != null) {
                                Object tag = view.getTag();
                                if (tag != null && (tag instanceof String) && ((String) tag).equals(VIEW_TAG)) {
                                    i++;
                                } else {
                                    nativeAdContainer.removeView(view);
                                }
                            } else {
                                i++;
                            }
                        }
                    } else {
                        nativeAdContainer = new NativeAdContainer(mContext);
                        for (int i = 0; i < nativeAdView.getChildCount(); ) {
                            View view = nativeAdView.getChildAt(i);
                            view.setTag(VIEW_TAG);
                            final int index = nativeAdView.indexOfChild(view);
                            nativeAdView.removeViewInLayout(view);
                            final ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                            nativeAdContainer.addView(view, index, layoutParams);
                        }
                        nativeAdView.removeAllViews();
                        nativeAdView.addView(nativeAdContainer, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    }

                    if(getActivity() != null){
                        /**
                         * 如果GMNativeAd调用的是
                         * void registerView(@NonNull Activity activity, @NonNull ViewGroup container, @NonNull List<View> clickViews, @Nullable List<View> creativeViews, GMViewBinder viewBinder);
                         * 需要使用getActivity()获取传入的Activity
                         * 如果GMNativeAd调用的是
                         * void registerView(@NonNull ViewGroup container, @NonNull List<View> clickViews, @Nullable List<View> creativeViews, GMViewBinder viewBinder);
                         * getActivity()的值为null
                         */
                        mNativeUnifiedADData.bindAdToView(getActivity(), nativeAdContainer, mGMAdSlotNative.getGMAdSlotGDTOption().getNativeAdLogoParams(), clickViews, creativeViews);
                    } else {
                        mNativeUnifiedADData.bindAdToView(mContext, nativeAdContainer, mGMAdSlotNative.getGMAdSlotGDTOption().getNativeAdLogoParams(), clickViews, creativeViews);
                    }

                    TTMediaView ttMediaView = nativeAdView.findViewById(viewBinder.mediaViewId);

                    if (ttMediaView != null && getAdImageMode() == GMAdConstant.IMAGE_MODE_VIDEO) {
                        MediaView gdtMediaView = new MediaView(mContext);
                        ttMediaView.removeAllViews();
                        ttMediaView.addView(gdtMediaView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                        mNativeUnifiedADData.bindMediaView(gdtMediaView, getGMVideoOption(mGMAdSlotNative.getGMAdSlotGDTOption()), new NativeADMediaListener() {
                            @Override
                            public void onVideoInit() {
                            }

                            @Override
                            public void onVideoLoading() {
                            }

                            @Override
                            public void onVideoReady() {
                            }

                            @Override
                            public void onVideoLoaded(int i) {
                            }

                            @Override
                            public void onVideoStart() {
                                callNativeVideoStart();
                            }

                            @Override
                            public void onVideoPause() {
                                callNativeVideoPause();
                            }

                            @Override
                            public void onVideoResume() {
                                callNativeVideoResume();
                            }

                            @Override
                            public void onVideoCompleted() {
                                callNativeVideoCompleted();
                            }

                            @Override
                            public void onVideoError(AdError adError) {
                                if (adError != null) {
                                    callNativeVideoError(new GMCustomAdError(adError.getErrorCode(), adError.getErrorMsg()));
                                } else {
                                    callNativeVideoError(new GMCustomAdError(AdConstance.VIDEO_ERROR, "video error"));
                                }
                            }

                            @Override
                            public void onVideoStop() {
                            }

                            @Override
                            public void onVideoClicked() {
                                callNativeAdClick();
                            }
                        });
                    }
                    if (!TextUtils.isEmpty(mNativeUnifiedADData.getCTAText())) {
                        View view = nativeAdView.findViewById(viewBinder.callToActionId);
                        List<View> CTAViews = new ArrayList<>();
                        CTAViews.add(view);
                        mNativeUnifiedADData.bindCTAViews(CTAViews);
                    }
                    mNativeUnifiedADData.setNativeAdEventListener(new NativeADEventListener() {
                        @Override
                        public void onADExposed() {
                            callNativeAdShow();
                        }

                        @Override
                        public void onADClicked() {
                            callNativeAdClick();
                        }

                        @Override
                        public void onADError(AdError adError) {
                        }

                        @Override
                        public void onADStatusChanged() {

                        }
                    });
                }
            }
        });
    }

    public VideoOption getGMVideoOption(GMAdSlotGDTOption gdtOption) {
        VideoOption.Builder builder = new VideoOption.Builder();
        if (gdtOption != null) {
            builder.setAutoPlayPolicy(gdtOption.getGDTAutoPlayPolicy());
            builder.setAutoPlayMuted(gdtOption.isGDTAutoPlayMuted());
            builder.setDetailPageMuted(gdtOption.isGDTDetailPageMuted());
            builder.setEnableDetailPage(gdtOption.isGDTEnableDetailPage());
            builder.setEnableUserControl(gdtOption.isGDTEnableUserControl());
        }
        return builder.build();
    }

    @Override
    public void onPause() {
        super.onPause();
        /**
         * 先切子线程，再在子线程中切主线程进行onPause操作
         */
        ThreadUtils.runOnUIThreadByThreadPool(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "onPause");
                if (mNativeUnifiedADData != null) {
                    mNativeUnifiedADData.pauseVideo();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        /**
         * 先切子线程，再在子线程中切主线程进行onResume操作
         */
        ThreadUtils.runOnUIThreadByThreadPool(new Runnable() {
            @Override
            public void run() {
                if (mNativeUnifiedADData != null) {
                    mNativeUnifiedADData.resume();
                }
            }
        });
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
                if (mNativeUnifiedADData != null) {
                    mNativeUnifiedADData.destroy();
                    mNativeUnifiedADData = null;
                }
            }
        });
    }

    @Override
    public GMAdConstant.AdIsReadyStatus isReadyStatus() {
        if (mNativeUnifiedADData != null && mNativeUnifiedADData.isValid()) {
            return GMAdConstant.AdIsReadyStatus.AD_IS_READY;
        } else {
            return GMAdConstant.AdIsReadyStatus.AD_IS_NOT_READY;
        }
    }
}
