package com.platform.simple.adn.pangle;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import com.bytedance.msdk.api.v2.GMAdDislike;
import com.bytedance.msdk.api.v2.ad.custom.GMCustomAdError;
import com.bytedance.msdk.api.v2.ad.custom.draw.GMCustomDrawAd;
import com.bytedance.msdk.api.v2.ad.custom.nativeAd.IGMCustomNativeDislikeDialog;
import com.bytedance.msdk.api.v2.ad.draw.GMDrawCustomVideoReporter;
import com.bytedance.msdk.api.v2.ad.nativeAd.GMViewBinder;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTFeedAd;
import com.bytedance.sdk.openadsdk.TTImage;
import com.bytedance.sdk.openadsdk.TTNativeAd;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PangleDrawAd extends GMCustomDrawAd {

    private TTFeedAd mTTFeedAd;

    public PangleDrawAd(TTFeedAd feedAd) {
        this.mTTFeedAd = feedAd;
        mTTFeedAd.setVideoAdListener(mVideoAdListener);
        this.setTitle(feedAd.getTitle()); // appName
        this.setDescription(feedAd.getDescription());
        this.setActionText(feedAd.getButtonText());
        this.setIconUrl(feedAd.getIcon() != null ? feedAd.getIcon().getImageUrl() : null);
        this.setAdImageMode(feedAd.getImageMode());
        this.setInteractionType(feedAd.getInteractionType());
        this.setSource(feedAd.getSource()); // 从数据看也是appName
        this.setStarRating(feedAd.getAppScore());
        //大图和小图
        if (feedAd.getImageMode() == TTAdConstant.IMAGE_MODE_VERTICAL_IMG ||
                feedAd.getImageMode() == TTAdConstant.IMAGE_MODE_LARGE_IMG ||
                feedAd.getImageMode() == TTAdConstant.IMAGE_MODE_SMALL_IMG) {
            if (feedAd.getImageList() != null && !feedAd.getImageList().isEmpty() && feedAd.getImageList().get(0) != null) {
                TTImage image = feedAd.getImageList().get(0);
                this.setImageUrl(image.getImageUrl());
                this.setImageHeight(image.getHeight());
                this.setImageWidth(image.getWidth());
            }
        } else if (feedAd.getImageMode() == TTAdConstant.IMAGE_MODE_GROUP_IMG) {//组图(3图)
            if (feedAd.getImageList() != null && feedAd.getImageList().size() > 0) {
                List<String> images = new ArrayList<>();
                for (TTImage image : feedAd.getImageList()) {
                    images.add(image.getImageUrl());
                }
                this.setImageList(images);
            }
        }
        this.setMediaExtraInfo(feedAd.getMediaExtraInfo());
    }

    TTNativeAd.AdInteractionListener mAdInteractionListener = new TTNativeAd.AdInteractionListener() {
        @Override
        public void onAdClicked(View view, TTNativeAd ttNativeAd) {
            callDrawAdClick();
        }

        @Override
        public void onAdCreativeClick(View view, TTNativeAd ttNativeAd) {
            callDrawAdClick();
        }

        @Override
        public void onAdShow(TTNativeAd ttNativeAd) {
            callDrawAdShow();
        }
    };

    /**
     * 自渲染dialog弹窗
     */
    public void setDislikeDialog() {
        setDislikeDialogCallBack(new IGMCustomNativeDislikeDialog() {
            @Override
            public GMAdDislike getDislikeDialog(Activity activity, Map<String, Object> extra) {
                return null;
            }

            @Override
            public void dislikeClick(String disLikeInfo, Map<String, Object> extra) {
                //dislikeClick
            }
        });
    }


    @Override
    public void registerViewForInteraction(Activity activity, ViewGroup container, List<View> clickViews, List<View> creativeViews, GMViewBinder viewBinder) {
        if (mTTFeedAd != null) {
            List<View> directDownloadViews = getDirectDownloadViews(); //支持穿山甲自渲染信息流注册直接下载区域
            mTTFeedAd.registerViewForInteraction(container, null, clickViews, creativeViews, directDownloadViews, null, mAdInteractionListener);
        }
    }

    @Override
    public String getVideoUrl() {
        if (isUseCustomVideo() && mTTFeedAd != null && mTTFeedAd.getCustomVideo() != null) {
            return mTTFeedAd.getCustomVideo().getVideoUrl();
        }
        return null;
    }

    @Override
    public GMDrawCustomVideoReporter getGMDrawCustomVideoReporter() {
        if (isUseCustomVideo()) {
            return new GMDrawCustomVideoReporter() {
                @Override
                public void reportVideoStart() {
                    if (mTTFeedAd != null && mTTFeedAd.getCustomVideo() != null) {
                        mTTFeedAd.getCustomVideo().reportVideoStart();
                    }
                } // 上报播放开始

                @Override
                public void reportVideoPause(long playingTime) {
                    if (mTTFeedAd != null && mTTFeedAd.getCustomVideo() != null) {
                        mTTFeedAd.getCustomVideo().reportVideoPause(playingTime);
                    }
                } // 上报播放暂停

                @Override
                public void reportVideoContinue(long playingTime) {
                    if (mTTFeedAd != null && mTTFeedAd.getCustomVideo() != null) {
                        mTTFeedAd.getCustomVideo().reportVideoContinue(playingTime);
                    }
                } // 上报播放继续

                @Override
                public void reportVideoFinish() {
                    if (mTTFeedAd != null && mTTFeedAd.getCustomVideo() != null) {
                        mTTFeedAd.getCustomVideo().reportVideoFinish();
                    }
                } // 上报播放结束

                @Override
                public void reportVideoBreak(long playingTime) {
                    if (mTTFeedAd != null && mTTFeedAd.getCustomVideo() != null) {
                        mTTFeedAd.getCustomVideo().reportVideoBreak(playingTime);
                    }
                } // 上报播放中止

                @Override
                public void reportVideoAutoStart() {
                    if (mTTFeedAd != null && mTTFeedAd.getCustomVideo() != null) {
                        mTTFeedAd.getCustomVideo().reportVideoAutoStart();
                    }
                } // 上报自动播放

                @Override
                public void reportVideoStartError(int errorCode, int extra) {
                    if (mTTFeedAd != null && mTTFeedAd.getCustomVideo() != null) {
                        mTTFeedAd.getCustomVideo().reportVideoStartError(errorCode, extra);
                    }
                } // 上报起播错误

                @Override
                public void reportVideoError(long playingTime, int errorCode, int extra) {
                    if (mTTFeedAd != null && mTTFeedAd.getCustomVideo() != null) {
                        mTTFeedAd.getCustomVideo().reportVideoError(playingTime, errorCode, extra);
                    }
                } // 上报播放中错误
            };
        }
        return null;
    }

    TTFeedAd.VideoAdListener mVideoAdListener = new TTFeedAd.VideoAdListener() {
        @Override
        public void onVideoLoad(TTFeedAd ttFeedAd) {

        }

        @Override
        public void onVideoError(int i, int code) {
            callDrawVideoError(new GMCustomAdError(i, code + ""));
        }

        @Override
        public void onVideoAdStartPlay(TTFeedAd ttFeedAd) {
            callDrawVideoStart();
        }

        @Override
        public void onVideoAdPaused(TTFeedAd ttFeedAd) {
            callDrawVideoPause();
        }

        @Override
        public void onVideoAdContinuePlay(TTFeedAd ttFeedAd) {
            callDrawVideoResume();
        }

        @Override
        public void onProgressUpdate(long current, long duration) {
            callDrawVideoProgressUpdate(current, duration);
        }

        @Override
        public void onVideoAdComplete(TTFeedAd ttFeedAd) {
            callDrawVideoCompleted();
        }
    };

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
