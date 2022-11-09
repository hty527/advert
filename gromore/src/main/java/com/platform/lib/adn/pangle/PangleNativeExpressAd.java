package com.platform.lib.adn.pangle;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import com.bytedance.msdk.api.TTAdConstant;
import com.bytedance.msdk.api.v2.ad.custom.GMCustomAdError;
import com.bytedance.msdk.api.v2.ad.custom.nativeAd.GMCustomNativeAd;
import com.bytedance.sdk.openadsdk.TTAdDislike;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;

public class PangleNativeExpressAd extends GMCustomNativeAd {

    private TTNativeExpressAd mTTFeedAd;
    private Context mContext;

    public PangleNativeExpressAd(Context context,TTNativeExpressAd feedAd) {
        this.mTTFeedAd = feedAd;
        this.mContext = context;
        this.setAdImageMode(feedAd.getImageMode());
        this.setInteractionType(feedAd.getInteractionType());
        this.setMediaExtraInfo(feedAd.getMediaExtraInfo());
        feedAd.setExpressInteractionListener(new TTNativeExpressAd.AdInteractionListener() {
            @Override
            public void onAdDismiss() {
            }

            @Override
            public void onAdClicked(View view, int i) {
                callNativeAdClick();
            }

            @Override
            public void onAdShow(View view, int i) {
                callNativeAdShow();
            }

            @Override
            public void onRenderFail(View view, String s, int i) {
                callNativeRenderFail(view, s, i);
            }

            @Override
            public void onRenderSuccess(View view, float v, float v1) {
                callNativeRenderSuccess(v, v1);
            }
        });
        if (feedAd.getImageMode() == TTAdConstant.IMAGE_MODE_VIDEO) {
            feedAd.setVideoAdListener(new TTNativeExpressAd.ExpressVideoAdListener() {
                @Override
                public void onVideoLoad() {
                }

                @Override
                public void onVideoError(int i, int i1) {
                    callNativeVideoError(new GMCustomAdError(i, i1 + ""));
                }

                @Override
                public void onVideoAdStartPlay() {
                    callNativeVideoStart();
                }

                @Override
                public void onVideoAdPaused() {
                    callNativeVideoPause();
                }

                @Override
                public void onVideoAdContinuePlay() {
                    callNativeVideoResume();
                }

                @Override
                public void onProgressUpdate(long current, long duration) {
                    callNativeVideoProgressUpdate(current, duration);
                }

                @Override
                public void onVideoAdComplete() {
                    callNativeVideoCompleted();
                }

                @Override
                public void onClickRetry() {

                }
            });
        }
        if(mContext instanceof Activity){
            mTTFeedAd.setDislikeCallback((Activity) mContext, new TTAdDislike.DislikeInteractionCallback() {
                @Override
                public void onShow() {
                    callNativeDislikeShow();
                }

                @Override
                public void onSelected(int i, String s, boolean b) {
                    callNativeDislikeSelected(i,s);
                }

                @Override
                public void onCancel() {
                    callNativeDislikeCancel();
                }
            });
        }
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
        if (mTTFeedAd != null) {
            mTTFeedAd.render();
        }
    }

    @Override
    public View getExpressView() {
        if (mTTFeedAd != null) {
            return mTTFeedAd.getExpressAdView();
        }
        return null;
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
    }
}