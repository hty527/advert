package com.platform.simple;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.anythink.nativead.api.ATNativeImageView;
import com.anythink.nativead.api.ATNativeMaterial;
import com.anythink.nativead.api.ATNativePrepareExInfo;
import com.anythink.nativead.api.ATNativePrepareInfo;
import com.platform.lib.listener.NativeRenderControl;
import java.util.ArrayList;
import java.util.List;

/**
 * created by hty
 * 2022/10/12
 * Desc:实现NativeRenderControl接口开始自定义渲染原生信息流广告
 */
public class CoustomNativeRender implements NativeRenderControl {

    /**
     * 返自定义UI View
     * @param context 创建自定义UI组件，广告ExpressView上下文
     * @return
     */
    @Override
    public View getRenderView(Context context) {
        return View.inflate(context,R.layout.coustom_native_render,null);
    }

    /**
     * 自渲染广告处理
     * @param selfRenderView 将广告信息绑定到自定义UI组件上，并且将各子自定义ViewGroup(比如点击按钮、icon组件)绑定到native广告上,selfRenderView:自定义渲染UI组件
     * @param adMaterial 原生广告信息
     * @param adWidth 期望渲染的广告宽度，高度SDK自适应，会通过OnExpressAdListener回调通知实际渲染的高度
     * @param nativePrepareInfo 包含各子自定义ViewGroup(比如点击按钮、icon组件)等组件信息
     */
    @Override
    public void onRenderNativeView(View selfRenderView, ATNativeMaterial adMaterial, float adWidth, ATNativePrepareInfo nativePrepareInfo) {
        if(null!=adMaterial&&null!=selfRenderView){
            //标题栏
            TextView titleView = (TextView) selfRenderView.findViewById(R.id.native_title);
            //Icon
            FrameLayout iconArea = (FrameLayout) selfRenderView.findViewById(R.id.native_icon);
            iconArea.setOutlineProvider(new LayoutProvider(dpToPxInt(selfRenderView.getContext(),22f)));
            //描述信息
            TextView descView = (TextView) selfRenderView.findViewById(R.id.native_desc);
            //下载、打开按钮
            TextView ctaView = (TextView) selfRenderView.findViewById(R.id.native_btn);
            //广告内容平台
            TextView adFromView = (TextView) selfRenderView.findViewById(R.id.native_from);
            //广告平台LOGO
            ATNativeImageView logoView = (ATNativeImageView) selfRenderView.findViewById(R.id.native_ad_logo);
            //关闭按钮
            View closeView = selfRenderView.findViewById(R.id.native_close);
            //主视图(封面图片、封面视频)容器
            FrameLayout contentArea = (FrameLayout) selfRenderView.findViewById(R.id.native_ad_container);
            contentArea.setOutlineProvider(new LayoutProvider(dpToPxInt(selfRenderView.getContext(),5f)));//给主视图加一个圆角
            // bind view
            if (nativePrepareInfo == null) nativePrepareInfo = new ATNativePrepareInfo();

            //可点击的 views，请将支持点击的View添加到此列表中
            List<View> clickViewList = new ArrayList<>();

            //title(这里不显示，需要显示请打开)
//            String title = adMaterial.getTitle();
//            if (!TextUtils.isEmpty(title)) {
//                titleView.setText(title);
//                nativePrepareInfo.setTitleView(titleView);//bind title
//                clickViewList.add(titleView);
//                titleView.setVisibility(View.VISIBLE);
//            } else {
//                titleView.setVisibility(View.GONE);
//            }

            String descriptionText = adMaterial.getDescriptionText();
            if (!TextUtils.isEmpty(descriptionText)) {
                // desc
                descView.setText(descriptionText);
                nativePrepareInfo.setDescView(descView);//bind desc
                clickViewList.add(descView);
                descView.setVisibility(View.VISIBLE);
            } else {
                descView.setVisibility(View.GONE);
            }

            // icon(这里不显示，需要显示请打开)
//            View adIconView = adMaterial.getAdIconView();
//            String iconImageUrl = adMaterial.getIconImageUrl();
//            iconArea.removeAllViews();
//            final ATNativeImageView iconView = new ATNativeImageView(selfRenderView.getContext());
//            if (adIconView != null) {
//                iconArea.addView(adIconView);
//                nativePrepareInfo.setIconView(adIconView);//bind icon
//                clickViewList.add(adIconView);
//                iconArea.setVisibility(View.VISIBLE);
//            } else if (!TextUtils.isEmpty(iconImageUrl)) {
//                iconArea.addView(iconView);
//                iconView.setImage(iconImageUrl);
//                nativePrepareInfo.setIconView(iconView);//bind icon
//                clickViewList.add(iconView);
//                iconArea.setVisibility(View.VISIBLE);
//            } else {
//                iconArea.setVisibility(View.INVISIBLE);
//            }


            // cta button
            String callToActionText = adMaterial.getCallToActionText();
            if (!TextUtils.isEmpty(callToActionText)) {
                ctaView.setText(callToActionText);
                nativePrepareInfo.setCtaView(ctaView);//bind cta button
                clickViewList.add(ctaView);
                ctaView.setVisibility(View.VISIBLE);
            } else {
                ctaView.setVisibility(View.GONE);
            }

            // media view
            View mediaView = adMaterial.getAdMediaView(contentArea);
            int mainImageHeight = adMaterial.getMainImageHeight();
            int mainImageWidth = adMaterial.getMainImageWidth();
            //确定最大渲染宽度
            int realMainImageWidth = dpToPxInt(selfRenderView.getContext(),adWidth);
            FrameLayout.LayoutParams mainImageParam = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            if(mainImageWidth>0&&mainImageHeight>0){
                mainImageParam.width = realMainImageWidth;
                mainImageParam.height = realMainImageWidth * mainImageHeight / mainImageWidth;
            }else{
                mainImageParam.width = realMainImageWidth;
                mainImageParam.height = realMainImageWidth * 9 / 16;
            }
            contentArea.removeAllViews();
            if (mediaView != null) {
                if (mediaView.getParent() != null) {
                    ((ViewGroup) mediaView.getParent()).removeView(mediaView);
                }
                mainImageParam.gravity = Gravity.CENTER;
                mediaView.setLayoutParams(mainImageParam);
                contentArea.addView(mediaView, mainImageParam);
                clickViewList.add(mediaView);
                contentArea.setVisibility(View.VISIBLE);
            } else if (!TextUtils.isEmpty(adMaterial.getMainImageUrl())) {
                ATNativeImageView imageView = new ATNativeImageView(selfRenderView.getContext());
                imageView.setImage(adMaterial.getMainImageUrl());
                imageView.setLayoutParams(mainImageParam);
                contentArea.addView(imageView, mainImageParam);

                nativePrepareInfo.setMainImageView(imageView);//bind main image
                clickViewList.add(imageView);
                contentArea.setVisibility(View.VISIBLE);
            } else {
                contentArea.removeAllViews();
                contentArea.setVisibility(View.GONE);
            }

            //Ad Logo(这里不显示，需要显示请打开)
//            String adChoiceIconUrl = adMaterial.getAdChoiceIconUrl();
//            Bitmap adLogoBitmap = adMaterial.getAdLogo();
//            if (!TextUtils.isEmpty(adChoiceIconUrl)) {
//                logoView.setImage(adChoiceIconUrl);
//                nativePrepareInfo.setAdLogoView(logoView);//bind ad choice
//                logoView.setVisibility(View.VISIBLE);
//            } else if (adLogoBitmap != null) {
//                logoView.setImageBitmap(adLogoBitmap);
//                logoView.setVisibility(View.VISIBLE);
//            } else {
//                logoView.setImageBitmap(null);
//                logoView.setVisibility(View.GONE);
//            }

            String adFrom = adMaterial.getAdFrom();
            String adTitle = adMaterial.getTitle();
            // ad from
            if (!TextUtils.isEmpty(adFrom)) {
                adFromView.setVisibility(View.VISIBLE);
                adFromView.setText(adFrom);
            } else if(!TextUtils.isEmpty(adTitle)){
                //如果平台为空，用title
                adFromView.setText(adTitle);
                adFromView.setVisibility(View.VISIBLE);
            }else{
                adFromView.setVisibility(View.GONE);
            }
            nativePrepareInfo.setAdFromView(adFromView);//bind ad from


            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(dpToPxInt(selfRenderView.getContext(),40f), dpToPxInt(selfRenderView.getContext(),10f));//ad choice
            layoutParams.gravity = Gravity.BOTTOM | Gravity.RIGHT;
            nativePrepareInfo.setChoiceViewLayoutParams(layoutParams);//bind layout params for ad choice
            nativePrepareInfo.setCloseView(closeView);//bind close button

            nativePrepareInfo.setClickViewList(clickViewList);//bind click view list

            if (nativePrepareInfo instanceof ATNativePrepareExInfo) {
                List<View> creativeClickViewList = new ArrayList<>();//click views
                creativeClickViewList.add(ctaView);
                ((ATNativePrepareExInfo) nativePrepareInfo).setCreativeClickViewList(creativeClickViewList);//bind custom view list
            }
        }
    }

    /**
     * 将px转换成dp
     * @param context
     * @param pxValue
     * @return
     */
    public static int pxToDpInt(Context context,float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / (scale <= 0 ? 1 : scale) + 0.5f);
    }

    /**
     * 将dp转换成px
     * @param context
     * @param dipValue
     * @return
     */
    public static int dpToPxInt(Context context,float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}