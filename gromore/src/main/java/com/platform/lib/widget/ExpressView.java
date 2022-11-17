package com.platform.lib.widget;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.bytedance.msdk.api.v2.GMAdSize;
import com.bytedance.msdk.api.v2.GMDislikeCallback;
import com.bytedance.msdk.api.v2.ad.banner.GMBannerAd;
import com.bytedance.msdk.api.v2.ad.nativeAd.GMNativeAd;
import com.bytedance.msdk.api.v2.ad.nativeAd.GMNativeExpressAdListener;
import com.platform.lib.R;
import com.platform.lib.constants.AdConstance;
import com.platform.lib.listener.NativeRenderControl;
import com.platform.lib.listener.OnExpressAdListener;
import com.platform.lib.listener.OnExpressListener;
import com.platform.lib.manager.PlatformManager;
import com.platform.lib.utils.Logger;
import com.platform.lib.utils.PlatformUtils;

/**
 * created by hty
 * 2022/11/8
 * Desc:信息流、banner渲染组件
 * 1、设置广告类型及代码位ID：
 * 设置广告类型：{@link #setAdType(String adType)}
 * 设置广告位ID：{@link #setAdCode(String adCode)}
 * 设置广告的预期宽度，单位：dp：{@link #setAdWidth(float adWidth)}
 * 设置广告的预期高度，单位：dp：{@link #setAdHeight(float adHeight)}，如果设置为0，则高度随广告自动填充
 * 设置是否显示错误信息，默认不显示：{@link #setShowErrorInfo(boolean showErrorInfo)}
 * 设置原生信息流广告的自定义UI渲染器(默认使用NativeRender渲染)：{@link #setNativeRenderControl(NativeRenderControl renderControl)}
 *
 * 2、开始加载广告：
 * 加载广告：{@link #requst()}
 *
 * 3、设置广告监听：{@link #setOnExpressAdListener(OnExpressAdListener listener)}
 */
public class ExpressView extends FrameLayout {

//    private static final String TAG = ExpressView.class.getSimpleName();
    private String adSource =AdConstance.SOURCE_TO,adType, adCode,scene=AdConstance.SCENE_CACHE;
    private float adWidth,adHeight;
    //自定义渲染UI控制器
    private NativeRenderControl mRenderControl;
    private FrameLayout mAdContainer;
    private OnExpressAdListener mAdListener;
    private GMNativeAd mNativeAd;
    private boolean mShowErrorInfo;//是否显示详细的错误信息

    public ExpressView(Context context) {
        this(context,null);
    }

    public ExpressView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ExpressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View.inflate(context, R.layout.lib_view_express_layout,this);
    }


    /**
     * 设置广告状态监听
     * @param listener
     */
    public void setOnExpressAdListener(OnExpressAdListener listener) {
        mAdListener = listener;
    }

    /**
     * 设置广告类型，参考AdConstance定义常量类型，
     * @param adType 1：信息流、3：banner
     */
    public void setAdType(String adType) {
        this.adType = adType;
    }

    /**
     * 设置广告位ID
     * @param adCode 广告位ID
     */
    public void setAdCode(String adCode) {
        this.adCode = adCode;
    }

    /**
     * 设置广告的预期宽度，单位：dp
     * @param adWidth 设置广告的预期宽度，单位：dp
     */
    public void setAdWidth(float adWidth) {
        this.adWidth = adWidth;
    }

    /**
     * 设置广告的预期高度，单位：dp
     * @param adHeight 设置广告的预期高度，单位：dp
     */
    public void setAdHeight(float adHeight) {
        this.adHeight = adHeight;
    }

    /**
     * 设置广告播放的场景
     * @param scene 设置广告播放的场景
     */
    public void setScene(String scene) {
        this.scene = scene;
    }

    /**
     * 设置是否显示错误信息，默认不显示
     * @param showErrorInfo 设置是否显示错误信息，默认不显示
     */
    public void setShowErrorInfo(boolean showErrorInfo) {
        mShowErrorInfo = showErrorInfo;
    }

    /**
     * 设置原生信息流广告的自定义UI渲染器，默认使用NativeRender渲染。原生信息流广告SDK内部自行渲染。
     * @param renderControl
     */
    public void setNativeRenderControl(NativeRenderControl renderControl) {
        mRenderControl = renderControl;
    }

    /**
     * 开始加载广告
     */
    public void requst(){
        Logger.d("requst-->type:"+adType+",id:"+ adCode +",width:"+adWidth+",height:"+adHeight+",scene:"+scene);
        if(TextUtils.isEmpty(adType)||TextUtils.isEmpty(adCode)){
//            Logger.d("未知的平台|类型|广告位");
            error(AdConstance.CODE_ID_INVALID, PlatformManager.getInstance().getText(AdConstance.CODE_ID_INVALID),adCode);
            return;
        }
        if(adWidth<=0) {
            adWidth= PlatformUtils.getInstance().getScreenWidthDP();
//            Logger.d("loadAd-->adWidth:"+adWidth);
        }
        mAdContainer = (FrameLayout) findViewById(R.id.lib_ad_container);
        if(AdConstance.TYPE_STREAM.equals(adType)){
            loadStream();
        }else if(AdConstance.TYPE_BANNER.equals(adType)){
            loadBanner();
        }else{
//            Logger.d("loadAd-->未知的广告类型");
            error(AdConstance.CODE_TYPE_INVALID,PlatformManager.getInstance().getText(AdConstance.CODE_TYPE_INVALID),adCode);
        }
    }

    private void loadBanner() {
        if(adHeight<=0f){
            adHeight=adWidth*90/600;
        }
//        Logger.d("loadTOBanner-->adSource:"+adSource+",adType:"+adType+",adCode:"+adCode+",adWidth:"+adWidth+",adHeight:"+adHeight);
        PlatformManager.getInstance().loadBanner(PlatformUtils.getInstance().getActivity(getContext()),adCode,scene,adWidth,adHeight,adStreamListener);
    }

    private void loadStream() {
        if(adWidth<=0){
            adWidth=PlatformUtils.getInstance().getScreenWidthDP();
        }
//        Logger.d(TAG,"loadTOStream-->adSource:"+adSource+",adType:"+adType+",adCode:"+adCode+",adWidth:"+adWidth+",adHeight:"+adHeight);
        if(null!=mNativeAd&&mNativeAd.isReady()){
//            Logger.d(TAG,"loadTOStream-->渲染");
            renderExpressAdView();
        }else{
//            Logger.d(TAG,"loadTOStream-->加载");
            PlatformManager.getInstance().loadStream(PlatformUtils.getInstance().getActivity(getContext()),adCode,scene,1,adWidth,adHeight,adStreamListener);
        }
    }

    private OnExpressListener adStreamListener=new OnExpressListener() {

        @Override
        public void onSuccessExpressed(GMNativeAd gmNativeAd) {
//            Logger.d(TAG,"onSuccessExpressAd-->gmNativeAd");
            if(null!=gmNativeAd){
                if(null!=mAdContainer){
                    ExpressView.this.mNativeAd=gmNativeAd;
                    renderExpressAdView();
                }else{
                    error(AdConstance.CODE_VIEWGROUP_INVALID,PlatformManager.getInstance().getText(AdConstance.CODE_VIEWGROUP_INVALID),adCode);
                }
            }else{
                error(AdConstance.CODE_ADINFO_INVALID,PlatformManager.getInstance().getText(AdConstance.CODE_ADINFO_INVALID),adCode);
            }
        }

        @Override
        public void onSuccessBanner(GMBannerAd gmBannerAd) {
//            Logger.d(TAG,"onSuccessExpressAd-->gmBannerAd");
            if(null!=gmBannerAd){
                if(null!=mAdContainer){
                    mAdContainer.removeAllViews();
                    adHeight=adWidth * 90 / 600;//宽高需要根据创建的代码位宽高进行限制
                    mAdContainer.getLayoutParams().width=PlatformUtils.getInstance().dpToPxInt(adWidth);
                    mAdContainer.getLayoutParams().height=LayoutParams.WRAP_CONTENT;//ScreenUtils.getInstance().dpToPxInt(adHeight)
                    mAdContainer.addView(gmBannerAd.getBannerView());
                    if(null!= mAdListener) {
                        mAdListener.onSuccess();
                        mAdContainer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                            @Override
                            public void onGlobalLayout() {
                                int measuredWidth = mAdContainer.getMeasuredWidth();
                                int measuredHeight = mAdContainer.getMeasuredHeight();
//                                Logger.d("GMBannerAd-->height-->"+measuredHeight);
                                mAdContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                                if(null!= mAdListener) {
                                    mAdListener.onAdViewHeight(measuredWidth,measuredHeight);
                                }
                            }
                        });
                    }
                }else{
                    error(AdConstance.CODE_VIEWGROUP_INVALID,PlatformManager.getInstance().getText(AdConstance.CODE_VIEWGROUP_INVALID),adCode);
                }
            }else{
                error(AdConstance.CODE_ADINFO_INVALID,PlatformManager.getInstance().getText(AdConstance.CODE_ADINFO_INVALID),adCode);
            }
        }

        @Override
        public void onError(int code, String message, String adInfo) {
//            Logger.d(TAG,"onError-->code:"+code+",message:"+message+",adInfo:"+adInfo);
            error(code,message,adInfo);
        }

        @Override
        public void onShow() {
//            Logger.d(TAG,"onShow-->");
            if(null!= mAdListener){
                mAdListener.onShow();
            }
        }

        @Override
        public void onClick() {
//            Logger.d(TAG,"onClick-->");
            if(null!= mAdListener){
                mAdListener.onClick();
            }
        }

        @Override
        public void onClose() {
//            Logger.d(TAG,"onClose-->");
            close();
        }
    };

    /**
     * 渲染信息流模板广告
     * @return
     */
    public void renderExpressAdView() {
        if(null!=mNativeAd&&null!=mAdContainer){
            //判断是否存在dislike按钮
            if (mNativeAd.hasDislike()) {
                mNativeAd.setDislikeCallback(PlatformUtils.getInstance().getActivity(getContext()), new GMDislikeCallback() {
                    @Override
                    public void onSelected(int position, String value) {
//                        Logger.d(TAG,"renderExpressAdView-->DislikeCallback-onSelected-->position:"+position+",value:"+value);
                        //用户选择不喜欢原因后，移除广告展示
                        close();
                        destroy();
                    }

                    @Override
                    public void onCancel() {
//                        Logger.d(TAG,"renderExpressAdView-->DislikeCallback-onCancel");
                    }

                    @Override
                    public void onRefuse() {
//                        Logger.d(TAG,"renderExpressAdView-->DislikeCallback-onRefuse");
                    }

                    @Override
                    public void onShow() {
//                        Logger.d(TAG,"renderExpressAdView-->DislikeCallback-onShow");
                    }
                });
            }
            //设置点击展示回调监听
            mNativeAd.setNativeAdListener(new GMNativeExpressAdListener() {
                @Override
                public void onAdClick() {
//                    Logger.d(TAG,"renderExpressAdView-->onAdClick");
                    if(null!= mAdListener){
                        mAdListener.onClick();
                    }
                }

                @Override
                public void onAdShow() {
//                    Logger.d(TAG,"renderExpressAdView-->onAdShow");
                    if(null!= mAdListener){
                        mAdListener.onShow();
                    }
                }

                @Override
                public void onRenderFail(View view, String msg, int code) {
//                    Logger.d(TAG,"renderExpressAdView-->onRenderFail,msg:"+msg+",code:"+code);
                    error(code,msg,adCode);
                }

                // ** 注意点 ** 不要在广告加载成功回调里进行广告view展示，要在onRenderSucces进行广告view展示，否则会导致广告无法展示。
                @Override
                public void onRenderSuccess(float width, float height) {
//                    Logger.d(TAG,"renderExpressAdView-->onRenderSuccess,width,:"+width+",height:"+height);
                    //回调渲染成功后将模板布局添加的父View中
                    if (mAdContainer != null&&null!=mNativeAd) {
//                        Logger.d(TAG,"renderExpressAdView-->onRenderSuccess,isExpressAd:"+mNativeAd.isExpressAd());
                        if(mNativeAd.isExpressAd()){
                            //获取视频播放view,该view SDK内部渲染，在媒体平台可配置视频是否自动播放等设置。
                            int sWidth,sHeight;
                            final View expressView = mNativeAd.getExpressView(); // 获取广告view  如果存在父布局，需要先从父布局中移除
                            if (width == GMAdSize.FULL_WIDTH && height == GMAdSize.AUTO_HEIGHT) {
                                sWidth = LayoutParams.MATCH_PARENT;
                                sHeight = LayoutParams.WRAP_CONTENT;
                            } else {
                                sWidth = PlatformUtils.getInstance().dpToPxInt(adWidth);
                                sHeight = (int) ((sWidth * height) / width);
                            }
//                            Logger.d(TAG,"renderExpressAdView-->onRenderSuccess,sWidth,:"+sWidth+",sHeight:"+sHeight);
                            if (expressView != null) {
                                /**
                                 * 如果存在父布局，需要先从父布局中移除
                                 */
                                PlatformUtils.getInstance().removeViewByGroup(expressView);
                                mAdContainer.removeAllViews();
                                mAdContainer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                                    @Override
                                    public void onGlobalLayout() {
                                        if(null!=mAdContainer){
                                            int measuredWidth = mAdContainer.getMeasuredWidth();
                                            int measuredHeight = mAdContainer.getMeasuredHeight();
//                                            Logger.d(TAG,"renderExpressAdView-->measuredWidth-->"+measuredWidth+",measuredHeight:"+measuredHeight);
                                            mAdContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                                            if(null!=mAdListener){
                                                mAdListener.onAdViewHeight(measuredWidth,measuredHeight);
                                            }
                                        }
                                    }
                                });
                                //如果广告高度大于0,则使用广告自身宽高属性,否则限定View宽高
                                if(sWidth>0){
                                    LayoutParams layoutParams = new LayoutParams(sWidth, sHeight);
                                    mAdContainer.addView(expressView, layoutParams);
                                }else{
                                    mAdContainer.getLayoutParams().width=PlatformUtils.getInstance().dpToPxInt(adWidth);
                                    mAdContainer.getLayoutParams().height= LayoutParams.WRAP_CONTENT;
                                    mAdContainer.addView(expressView);
                                }
                                if(null!= mAdListener) {
                                    mAdListener.onSuccess();
                                }
                            }else{
                                error(AdConstance.CODE_ADINFO_INVALID,PlatformManager.getInstance().getText(AdConstance.CODE_ADINFO_INVALID),adCode);
                            }
                        }else{
                            //原生自渲染信息流广告
                            if(null==mRenderControl){
                                mRenderControl=new NativeRender();
                            }
                            View renderView = mRenderControl.getRenderView(getContext());
                            PlatformUtils.getInstance().removeViewByGroup(renderView);
                            LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                            mAdContainer.addView(renderView ,layoutParams);
                            mRenderControl.onRenderNativeView(renderView,mNativeAd,adWidth);
                            if(null!= mAdListener) {
                                mAdListener.onSuccess();
                            }
                        }
                    }else{
                        error(AdConstance.CODE_VIEWGROUP_INVALID,PlatformManager.getInstance().getText(AdConstance.CODE_VIEWGROUP_INVALID),adCode);
                    }
                }
            });
            //开始渲染
            mNativeAd.render();
        }else{
            error(AdConstance.CODE_VIEWGROUP_INVALID,PlatformManager.getInstance().getText(AdConstance.CODE_VIEWGROUP_INVALID),adCode);
        }
    }

    private void close() {
        if(null!=mAdContainer){
            mAdContainer.removeAllViews();
            mAdContainer.getLayoutParams().height=0;
        }
        if(null!= mAdListener){
            mAdListener.onClose();
        }
    }

    private void error(int code, String message, String adCode) {
        if(null==mAdContainer) return;
        if(mShowErrorInfo){
            addErrorView(code,message,adCode);
        }else{
            mAdContainer.removeAllViews();
            mAdContainer.getLayoutParams().height=0;
        }
        if(null!= mAdListener){
            mAdListener.onError(code,message,adCode);
        }
    }

    /**
     * 添加一个错误的交互View
     * @param code 错误码
     * @param message 描述信息
     * @param adCode 广告位ID
     */
    public void addErrorView(int code, String message, String adCode) {
        if(null==mAdContainer) return;
        TextView errorView= null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            errorView = new TextView(mAdContainer.getContext(),null,0,R.style.ExpressTextView);
        }else{
            ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(mAdContainer.getContext(), R.style.ExpressTextView);
            errorView = new TextView(contextThemeWrapper);
        }
        FrameLayout.LayoutParams layoutParams=new FrameLayout.LayoutParams(adWidth<=0?PlatformUtils.getInstance().getScreenWidth():PlatformUtils.getInstance().dpToPxInt(adWidth),PlatformUtils.getInstance().dpToPxInt(200f));
        errorView.setGravity(Gravity.CENTER);
        errorView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,15f);
        errorView.setTextColor(Color.parseColor("#FF666666"));
        errorView.setLineSpacing(0,1.2f);
        int padding = PlatformUtils.getInstance().dpToPxInt(10f);
        errorView.setPadding(padding,padding,padding,padding);
        errorView.setScrollbarFadingEnabled(true);
        errorView.setMovementMethod(ScrollingMovementMethod.getInstance());
        mAdContainer.addView(errorView,layoutParams);
        errorView.setText("load error,id："+adCode+"\ncode:"+code+",message："+message);
        //处理父布局可能存在的ScrollView或者ListView以及如RecyclerView的所有可滚动View的手势冲突事件
        errorView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction()==MotionEvent.ACTION_DOWN){
                    //通知父控件不要干扰
                    view.getParent().requestDisallowInterceptTouchEvent(true);
                }
                if(motionEvent.getAction()==MotionEvent.ACTION_MOVE){
                    //通知父控件不要干扰
                    view.getParent().requestDisallowInterceptTouchEvent(true);
                }
                if(motionEvent.getAction()==MotionEvent.ACTION_UP){
                    view.getParent().requestDisallowInterceptTouchEvent(false);
                }
                return false;
            }
        });
    }
    /**
     * 主要针对部分平台信息流视频类型广告
     */
    public void onResume(){
        if(null!=mNativeAd){
            mNativeAd.resume();
        }
    }

    /**
     * 主要针对部分平台信息流视频类型广告
     */
    public void onPause(){
        if(null!=mNativeAd){
            mNativeAd.onPause();
        }
    }

    public void destroy() {
//        Logger.d(TAG,"onDestroy-->");
        if(null!=mAdContainer){
            mAdContainer.removeAllViews();
            mAdContainer.getLayoutParams().height=0;
        }
        if(null!=mNativeAd){
            mNativeAd.destroy();
            mNativeAd=null;
        }
    }
}