package com.platform.simple.adn.pangle;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.bytedance.msdk.adapter.util.UIUtils;
import com.bytedance.msdk.api.format.TTMediaView;
import com.bytedance.msdk.api.format.TTNativeAdView;
import com.bytedance.msdk.api.v2.GMAdConstant;
import com.bytedance.msdk.api.v2.GMAdDislike;
import com.bytedance.msdk.api.v2.GMDislikeCallback;
import com.bytedance.msdk.api.v2.ad.custom.GMCustomAdError;
import com.bytedance.msdk.api.v2.ad.custom.nativeAd.GMCustomNativeAd;
import com.bytedance.msdk.api.v2.ad.custom.nativeAd.IGMCustomNativeDislikeDialog;
import com.bytedance.msdk.api.v2.ad.nativeAd.GMNativeCustomVideoReporter;
import com.bytedance.msdk.api.v2.ad.nativeAd.GMViewBinder;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAppDownloadListener;
import com.bytedance.sdk.openadsdk.TTFeedAd;
import com.bytedance.sdk.openadsdk.TTImage;
import com.bytedance.sdk.openadsdk.TTNativeAd;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PangleNativeAd extends GMCustomNativeAd {

    private int appDownloadStatus; //记录下载类广告的下载状态，0为未开始，1为开始下载，2为下载中，3为下载暂停，4为下载失败，5为下载完成，6为安装完成

    private TTFeedAd mTTFeedAd;

    public PangleNativeAd(TTFeedAd feedAd) {
        this.mTTFeedAd = feedAd;
        mTTFeedAd.setVideoAdListener(mVideoAdListener);
        mTTFeedAd.setDownloadListener(downloadListener);
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
            callNativeAdClick();
        }

        @Override
        public void onAdCreativeClick(View view, TTNativeAd ttNativeAd) {
            callNativeAdClick();
        }

        @Override
        public void onAdShow(TTNativeAd ttNativeAd) {
            callNativeAdShow();
        }
    };

    /**
     * 自渲染dialog弹窗
     */
    public void setDislikeDialog() {
        setDislikeDialogCallBack(new IGMCustomNativeDislikeDialog() {
            @Override
            public GMAdDislike getDislikeDialog(Activity activity, Map<String, Object> extra) {
                if (mTTFeedAd != null) {
                    final com.bytedance.sdk.openadsdk.TTAdDislike dislikeDialog = mTTFeedAd.getDislikeDialog(activity);
                    return new GMAdDislike() {
                        @Override
                        public void showDislikeDialog() {
                            if (dislikeDialog != null) {
                                dislikeDialog.showDislikeDialog();
                            }
                        }

                        @Override
                        public void setDislikeCallback(final GMDislikeCallback dislikeCallback) {
                            if (dislikeCallback != null) {
                                dislikeDialog.setDislikeInteractionCallback(new com.bytedance.sdk.openadsdk.TTAdDislike.DislikeInteractionCallback() {
                                    @Override
                                    public void onSelected(int i, String s, boolean b) {
                                        nativeDislikeClick(s); //回传给GroMore,上报埋点使用
                                        dislikeCallback.onSelected(i, s);
                                    }

                                    @Override
                                    public void onCancel() {
                                        dislikeCallback.onCancel();
                                    }

                                    @Override
                                    public void onShow() {
                                        if (dislikeCallback != null) {
                                            dislikeCallback.onShow();
                                        }
                                    }
                                });
                            }
                        }
                    };
                }
                return null;
            }

            @Override
            public void dislikeClick(String disLikeInfo, Map<String, Object> extra) {
                //可以忽略此回调
            }
        });
    }

    private void removeSelfFromParent(View child) {
        try {
            if (child != null) {
                ViewParent parent = child.getParent();
                if (parent != null && parent instanceof ViewGroup) {
                    ((ViewGroup) parent).removeView(child);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void registerViewForInteraction(ViewGroup container, List<View> clickViews, List<View> creativeViews, GMViewBinder viewBinder) {
        if (container instanceof TTNativeAdView) {
            if (mTTFeedAd != null) {
                mTTFeedAd.registerViewForInteraction(container, clickViews, creativeViews, mAdInteractionListener);
            }

            if (mTTFeedAd != null && mTTFeedAd.getAdLogo() != null) {
                View logoView = container.findViewById(viewBinder.logoLayoutId);
                if (logoView != null) {
                    logoView.setVisibility(View.VISIBLE);
                    if (logoView instanceof ViewGroup) {
                        ((ViewGroup) logoView).removeAllViews();
                        ImageView logo = new ImageView(container.getContext());
                        logo.setImageBitmap(mTTFeedAd.getAdLogo());
                        logo.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                        ViewGroup.LayoutParams logolayoutPl = logoView.getLayoutParams();
                        logolayoutPl.width = UIUtils.dp2px(container.getContext(), 38);
                        logolayoutPl.height = UIUtils.dp2px(container.getContext(), 38);
                        logoView.setLayoutParams(logolayoutPl);
                        ((ViewGroup) logoView).addView(logo, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    } else if (logoView instanceof ImageView) {
                        ((ImageView) logoView).setImageBitmap(mTTFeedAd.getAdLogo());
                    }
                }
            }

            // 如果开发者选择自己进行视频播放处理，则这里不处理视频view。但如果videoUrl是空，则兜底还用adn自己的视频播放
            if (!isUseCustomVideo() || mTTFeedAd == null || mTTFeedAd.getCustomVideo() == null || TextUtils.isEmpty(mTTFeedAd.getCustomVideo().getVideoUrl())) {
                TTMediaView mediaView = container.findViewById(viewBinder.mediaViewId);
                if (mediaView != null && mTTFeedAd != null) {
                    View adView = mTTFeedAd.getAdView();
                    if (adView == null) {
                        return;
                    }
                    removeSelfFromParent(adView);
                    mediaView.removeAllViews();
                    mediaView.addView(adView, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
                }
            }
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
    public GMNativeCustomVideoReporter getGMNativeCustomVideoReporter() {
        if (isUseCustomVideo()) {
            return new GMNativeCustomVideoReporter() {
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
            callNativeVideoError(new GMCustomAdError(i, code + ""));
        }

        @Override
        public void onVideoAdStartPlay(TTFeedAd ttFeedAd) {
            callNativeVideoStart();
        }

        @Override
        public void onVideoAdPaused(TTFeedAd ttFeedAd) {
            callNativeVideoPause();
        }

        @Override
        public void onVideoAdContinuePlay(TTFeedAd ttFeedAd) {
            callNativeVideoResume();
        }

        @Override
        public void onProgressUpdate(long current, long duration) {
            callNativeVideoProgressUpdate(current, duration);
        }

        @Override
        public void onVideoAdComplete(TTFeedAd ttFeedAd) {
            callNativeVideoCompleted();
        }
    };

    TTAppDownloadListener downloadListener = new TTAppDownloadListener() {
        @Override
        public void onIdle() {
            appDownloadStatus = GMAdConstant.APP_DOWNLOAD_TYPE_ONIDLE;
            callNativeOnIdle();
        }

        @Override
        public void onDownloadActive(long totalBytes, long currBytes, String fileName, String appName) {
            appDownloadStatus = GMAdConstant.APP_DOWNLOAD_TYPE_ONDOWNLOADACTIVE;
            callNativeOnDownloadActive(totalBytes, currBytes, fileName, appName);
        }

        @Override
        public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {
            appDownloadStatus = GMAdConstant.APP_DOWNLOAD_TYPE_ONDOWNLOADPAUSED;
            callNativeOnDownloadPaused(totalBytes, currBytes, fileName, appName);
        }

        @Override
        public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {
            appDownloadStatus = GMAdConstant.APP_DOWNLOAD_TYPE_ONDOWNLOADFAILED;
            callNativeOnDownloadFailed(totalBytes, currBytes, fileName, appName);
        }

        @Override
        public void onDownloadFinished(long totalBytes, String fileName, String appName) {
            appDownloadStatus = GMAdConstant.APP_DOWNLOAD_TYPE_ONDOWNLOADFINISHED;
            callNativeOnDownloadFinished(totalBytes, fileName, appName);
        }

        @Override
        public void onInstalled(String fileName, String appName) {
            appDownloadStatus = GMAdConstant.APP_DOWNLOAD_TYPE_ONINSTALLED;
            callNativeOnInstalled(fileName, appName);
        }
    };

    @Override
    public void pauseAppDownload() {
        if( mTTFeedAd != null && mTTFeedAd.getDownloadStatusController() != null) {
            if(appDownloadStatus == GMAdConstant.APP_DOWNLOAD_TYPE_ONDOWNLOADACTIVE) {
                mTTFeedAd.getDownloadStatusController().changeDownloadStatus();
            }
        }
    }

    @Override
    public void resumeAppDownload() {
        if( mTTFeedAd != null && mTTFeedAd.getDownloadStatusController() != null) {
            if(appDownloadStatus == GMAdConstant.APP_DOWNLOAD_TYPE_ONDOWNLOADPAUSED) {
                mTTFeedAd.getDownloadStatusController().changeDownloadStatus();
            }
        }
    }

    @Override
    public void cancelDownload() {
        if( mTTFeedAd != null && mTTFeedAd.getDownloadStatusController() != null) {
            mTTFeedAd.getDownloadStatusController().cancelDownload();
        }
    }

    @Override
    public int getDownloadStatus() {
        return appDownloadStatus;
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

    @Override
    public boolean hasDislike() {
        return true;
    }
}
