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
import com.anythink.banner.api.ATBannerView;
import com.anythink.nativead.api.ATNativeAdView;
import com.anythink.nativead.api.ATNativePrepareExInfo;
import com.anythink.nativead.api.NativeAd;
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
 * 2022/9/27
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

    private String adType, adCode,scene=AdConstance.SCENE_CACHE;
    private float adWidth,adHeight;
    //自定义渲染UI控制器
    private NativeRenderControl mRenderControl;
    private FrameLayout mAdContainer;
    private OnExpressAdListener mAdListener;
    private NativeAd mNativeAd;
    private ATBannerView mATBannerView;
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
            error(AdConstance.CODE_ID_INVALID,PlatformManager.getInstance().getText(AdConstance.CODE_ID_INVALID),adCode);
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
        PlatformManager.getInstance().loadBanner(adCode,mAdContainer,scene,adWidth,adHeight,adStreamListener);
    }

    private void loadStream() {
        if(adWidth<=0){
            adWidth= PlatformUtils.getInstance().getScreenWidthDP();
        }
//        Logger.d("loadTOStream-->adSource:"+adSource+",adType:"+adType+",adCode:"+adCode+",adWidth:"+adWidth+",adHeight:"+adHeight);
        PlatformManager.getInstance().loadStream(PlatformUtils.getInstance().getActivity(getContext()), adCode,scene,adWidth,adHeight,adStreamListener);
    }

    private OnExpressListener adStreamListener=new OnExpressListener() {

        @Override
        public void onSuccessExpressed(NativeAd nativeAd) {
//            Logger.d("onSuccessExpressed-->");
            if(null!=mAdContainer&&null!=nativeAd){
                mNativeAd = nativeAd;
                renderExpressView();
                if(null!= mAdListener){
                    mAdListener.onSuccess();
                }
            }else{
                error(AdConstance.CODE_AD_EMPTY,PlatformManager.getInstance().getText(AdConstance.CODE_AD_EMPTY), adCode);
            }
        }

        /**
         * Banner广告在PlatformManager中已被设置了监听事件，这里只需要关心adStreamListener的各种回调事件即可
         * @param atBannerView
         */
        @Override
        public void onSuccessBanner(ATBannerView atBannerView) {
            ExpressView.this.mATBannerView= atBannerView;
//            Logger.d("onSuccessBanner-->");
            if(null!=mAdContainer){
                mAdContainer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if(null!=mAdContainer){
                            int measuredWidth = mAdContainer.getMeasuredWidth();
                            int measuredHeight = mAdContainer.getMeasuredHeight();
//                            Logger.d("ATBannerView-->height-->"+measuredHeight);
                            mAdContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            if(null!= mAdListener) {
                                mAdListener.onAdViewHeight(measuredWidth,measuredHeight);
                            }
                        }
                    }
                });
                if(null!= mAdListener) {
                    mAdListener.onSuccess();
                }
            }
        }

        @Override
        public void onError(int code, String message, String adCode) {
//            Logger.e("onError-->code:"+code+",message:"+message+",adInfo:"+adInfo);
            error(code,message,adCode);
        }

        @Override
        public void onShow() {
            if(null!= mAdListener){
                mAdListener.onShow();
            }
        }

        @Override
        public void onClick() {
            if(null!= mAdListener){
                mAdListener.onClick();
            }
        }

        @Override
        public void onClose() {
//            Logger.d("onClose-->");
            if(null!=mAdContainer){
                mAdContainer.removeAllViews();
                mAdContainer.getLayoutParams().height=0;
            }
            if(null!= mAdListener){
                mAdListener.onClose();
            }
        }
    };

    /**
     * 开始渲染信息流(模板或原生)
     */
    private void renderExpressView() {
        if(null==mRenderControl){
            mRenderControl=new NativeRender();
        }
        mNativeAd.manualImpressionTrack();
        mAdContainer.removeAllViews();
        //adHeight = adWidth * 2 / 3;
        //限制3：2高度
        mAdContainer.getLayoutParams().width= PlatformUtils.getInstance().dpToPxInt(adWidth);
        mAdContainer.getLayoutParams().height=LayoutParams.WRAP_CONTENT;//ScreenUtils.getInstance().dpToPxInt(adHeight)
        mAdContainer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if(null!=mAdContainer){
                    int measuredWidth = mAdContainer.getMeasuredWidth();
                    int measuredHeight = mAdContainer.getMeasuredHeight();
//                            Logger.d("onAdImpressed-->height:"+measuredHeight);
                    mAdContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    if(null!= mAdListener) {
                        mAdListener.onAdViewHeight(measuredWidth,measuredHeight);
                    }
                }
            }
        });
        //原生/模板渲共用的ATNativeAdView
        ATNativeAdView nativeAdView=new ATNativeAdView(mAdContainer.getContext());
        //原生广告信息
        ATNativePrepareExInfo nativePrepareExInfo = new ATNativePrepareExInfo();
        //先将广告组件添加到容器
        mAdContainer.addView(nativeAdView);
        if(mNativeAd.isNativeExpress()){//模板渲染
            mNativeAd.renderAdContainer(nativeAdView,null);
        }else{
            //原生渲染时需要将组件添加到自定义容器中
            View renderView = mRenderControl.getRenderView(getContext());
            PlatformUtils.getInstance().removeViewByGroup(renderView);
            mNativeAd.renderAdContainer(nativeAdView,renderView);
            mRenderControl.onRenderNativeView(renderView,mNativeAd.getAdMaterial(),adWidth,nativePrepareExInfo);
        }
        //将广告渲染到ATNativeAdView中
        mNativeAd.prepare(nativeAdView,nativePrepareExInfo);//开始渲染
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
            mNativeAd.onResume();
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
        if(null!=mNativeAd){
            mNativeAd.destory();
        }
        if(null!=mATBannerView){
            PlatformUtils.getInstance().removeViewByGroup(mATBannerView);
            mATBannerView.destroy();
        }
        mNativeAd=null;mATBannerView=null;
        if(null!=mAdContainer){
            mAdContainer.removeAllViews();
            mAdContainer.getLayoutParams().height=0;
        }
    }
}