package com.platform.lib.widget;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bytedance.msdk.api.v2.ad.nativeAd.GMNativeAd;
import com.platform.lib.R;
import com.platform.lib.listener.NativeRenderControl;
import com.platform.lib.utils.ImageLoader;
import com.platform.lib.utils.PlatformUtils;

/**
 * created by hty
 * 2022/11/8
 * Desc:原生信息流渲染器
 */
public class NativeRender implements NativeRenderControl {

    @Override
    public View getRenderView(Context context) {
        return View.inflate(context, R.layout.lib_native_render,null);
    }

    @Override
    public void onRenderNativeView(View selfRenderView, GMNativeAd nativeAd, float adWidth) {
        if(null!=selfRenderView&&null!=nativeAd){
            //标题栏
            TextView titleView = (TextView) selfRenderView.findViewById(R.id.lib_native_title);
            //Icon
            FrameLayout iconArea = (FrameLayout) selfRenderView.findViewById(R.id.lib_native_icon);
            PlatformUtils.getInstance().setCircleRadius(iconArea,PlatformUtils.getInstance().dpToPxInt(8f));
            //描述信息
            TextView descView = (TextView) selfRenderView.findViewById(R.id.lib_native_desc);
            //下载、打开按钮
            TextView ctaView = (TextView) selfRenderView.findViewById(R.id.lib_native_btn);
            //广告内容平台
            TextView adFromView = (TextView) selfRenderView.findViewById(R.id.lib_native_from);
            //广告主封面
            ImageView cover = (ImageView) selfRenderView.findViewById(R.id.lib_native_cover);
            //广告平台LOGO
            ImageView logoView = (ImageView) selfRenderView.findViewById(R.id.lib_native_ad_logo);
            //关闭按钮
            View closeView = selfRenderView.findViewById(R.id.lib_native_close);
            //主视图(封面图片、封面视频)容器
            FrameLayout contentArea = (FrameLayout) selfRenderView.findViewById(R.id.lib_native_ad_container);
            //主视图的宽高
            int mainImageWidth = nativeAd.getImageWidth();
            int mainImageHeight = nativeAd.getImageHeight();
            //确定最大渲染宽度
            int realMainImageWidth = PlatformUtils.getInstance().dpToPxInt(adWidth);
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) contentArea.getLayoutParams();
            if(mainImageWidth>0&&mainImageHeight>0){
                layoutParams.width = realMainImageWidth;
                layoutParams.height = realMainImageWidth * mainImageHeight / mainImageWidth;
            }else{
                layoutParams.width = realMainImageWidth;
                layoutParams.height = realMainImageWidth * 9 / 16;
            }
            contentArea.setLayoutParams(layoutParams);
            String imageUrl = nativeAd.getImageUrl();
            String title = nativeAd.getTitle();
            String description = nativeAd.getDescription();
            String actionText = nativeAd.getActionText();
            titleView.setText(title);
            descView.setText(description);
            ctaView.setText(actionText);
            new ImageLoader().displayImage(cover,imageUrl);
        }
    }
}