package com.platform.lib.adn.pangle;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import com.bytedance.msdk.api.TTAdConstant;
import com.bytedance.msdk.api.v2.ad.custom.GMCustomAdError;
import com.bytedance.msdk.api.v2.ad.custom.draw.GMCustomDrawAd;
import com.bytedance.sdk.openadsdk.TTAdDislike;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;

public class PangleDrawExpressAd extends GMCustomDrawAd {

    private TTNativeExpressAd mTTFeedAd;
    private Context mContext;

    public PangleDrawExpressAd(Context context, TTNativeExpressAd feedAd) {
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
                callDrawAdClick();
            }

            @Override
            public void onAdShow(View view, int i) {
                callDrawAdShow();
            }

            @Override
            public void onRenderFail(View view, String s, int i) {
                callDrawRenderFail(view, s, i);
            }

            @Override
            public void onRenderSuccess(View view, float v, float v1) {
                callDrawRenderSuccess(v, v1);
            }
        });
        if (feedAd.getImageMode() == TTAdConstant.IMAGE_MODE_VIDEO) {
            feedAd.setVideoAdListener(new TTNativeExpressAd.ExpressVideoAdListener() {
                @Override
                public void onVideoLoad() {
                }

                @Override
                public void onVideoError(int i, int i1) {
                    callDrawVideoError(new GMCustomAdError(i, i1 + ""));
                }

                @Override
                public void onVideoAdStartPlay() {
                    callDrawVideoStart();
                }

                @Override
                public void onVideoAdPaused() {
                    callDrawVideoPause();
                }

                @Override
                public void onVideoAdContinuePlay() {
                    callDrawVideoResume();
                }

                @Override
                public void onProgressUpdate(long current, long duration) {
                    callDrawVideoProgressUpdate(current, duration);
                }

                @Override
                public void onVideoAdComplete() {
                    callDrawVideoCompleted();
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
                    callDrawDislikeShow();
                }

                @Override
                public void onSelected(int i, String s, boolean b) {
                    callDrawDislikeSelected(i,s);
                }

                @Override
                public void onCancel() {
                    callDrawDislikeCancel();
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