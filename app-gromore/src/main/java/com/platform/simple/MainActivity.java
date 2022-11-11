package com.platform.simple;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.platform.lib.bean.Result;
import com.platform.lib.constants.AdConstance;
import com.platform.lib.listener.OnExpressAdListener;
import com.platform.lib.listener.OnPlayListener;
import com.platform.lib.manager.PlatformManager;
import com.platform.lib.manager.PlayManager;
import com.platform.lib.manager.TableScreenManager;
import com.platform.lib.utils.Logger;
import com.platform.lib.widget.ExpressView;

/**
 * created by hty
 * 2022/9/17
 * Desc:各广告演示
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static String PERMISSION_PHONE = "android.permission.READ_PHONE_STATE";
    private static String PERMISSION_WRITE_EXTERNAL = "android.permission.WRITE_EXTERNAL_STORAGE";
    private static String PERMISSION_READ_EXTERNAL = "android.permission.READ_EXTERNAL_STORAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((TextView) findViewById(R.id.tv_version)).setText(String.format("SDK版本:v%s", PlatformManager.getInstance().getVersion()));
        requstPermission();
    }

    /**
     * 权限申请
     */
    private void requstPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(PERMISSION_PHONE) == PackageManager.PERMISSION_GRANTED) {
                init();
            } else {
                requestPermissions(new String[]{PERMISSION_PHONE,PERMISSION_WRITE_EXTERNAL,PERMISSION_READ_EXTERNAL}, 100);
            }
        } else {
            init();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (null != grantResults && grantResults.length > 0) {
            init();
        }
    }

    /**
     * 检查SDK集成状态
     */
    private void init() {
        //SDK版本号
        String version = PlatformManager.getInstance().getVersion();
        //缓存激励视频广告
        //PlatformManager.getInstance().loadRewardVideo(this,AdConfig.AD_CODE_REWARD_ID,null);
        //缓存插屏广告
        //PlatformManager.getInstance().loadInsert(this,AdConfig.AD_CODE_INSERT_ID,null);
        //缓存原生自渲染信息流广告
        //PlatformManager.getInstance().loadStream(this,AdConfig.AD_CODE_STREAM_NATIVE_ID,null);
        //缓存模板信息流广告
        //PlatformManager.getInstance().loadBanner(this,AdConfig.AD_CODE_STREAM_ID,null);
    }

    /**
     * 缓存开屏
     * @param view
     */
    public void cacheSplash(View view) {
        PlatformManager.getInstance().loadSplash(this,AdConfig.AD_CODE_SPLASH_ID,null);
    }

    /**
     * 展示开屏
     * @param view
     */
    public void showSplash(View view) {
        startActivity(new Intent(this, SplashActivity.class));
    }

    /**
     * 缓存激励视频
     * @param view
     */
    public void cacheReward(View view) {
        /**
         * 加载激励视频广告
         * @param context Activity类型上下文
         * @param id 广告位ID
         * @param scene 广告展示场景
         * @param listener 状态监听器，如果监听器为空内部回自动缓存一条激励视频广告
         */
        PlatformManager.getInstance().loadRewardVideo(this,AdConfig.AD_CODE_REWARD_ID,null);
    }

    /**
     * 展示激励视频，推荐使用PlayerManager提供的api来直接播放激励视频广告
     * 请注意，当启用全自动激励视频广告之前，必须线调用PlatformManager.getInstance().initReward()
     * @param view
     */
    public void showReward(View view) {

        //常规缓存激励视频加载\缓存\展示激励视频广告
        /**
         * 加载激励视频广告
         * @param context Activity类型上下文
         * @param id 广告位ID
         * @param scene 广告展示场景
         * @param listener 状态监听器，如果监听器为空内部回自动缓存一条激励视频广告
         */
//        PlatformManager.getInstance().loadRewardVideo(this,AdConfig.AD_CODE_REWARD_ID, new OnRewardVideoListener() {
//
//            @Override
//            public void onSuccess(GMRewardAd gmRewardAd) {
//                gmRewardAd.showRewardAd(MainActivity.this);
//            }
//
//            @Override
//            public void onRewardVerify() {
//
//            }
//
//            @Override
//            public void onShow(String ecpm) {
//
//            }
//
//            @Override
//            public void onError(int code, String message, String adCode) {
//
//            }
//
//            @Override
//            public void onShow() {
//
//            }
//
//            @Override
//            public void onClick() {
//
//            }
//
//            @Override
//            public void onClose() {
//
//            }
//        });

        //封装的便捷播放入口
        /**
         * 开始播放激励视屏
         * @param ad_code 广告位ID
         * @param scene 播放场景
         * @param isAutoModel 是否启用全自动模式，内部自动加载激励视频广告并且在合适的时机自动缓存下一个激励视频广告实例
         * @param listener 状态监听器
         */
        PlayManager.getInstance().startVideo(AdConfig.AD_CODE_REWARD_ID, new OnPlayListener() {
            @Override
            public void onClose(Result status) {
                if(null!=status){
                    Logger.d(TAG,"onClose-->adCode:"+status.getAd_code()+",eCpm:"+status.getEcpm()+",isClick:"+status.getIs_click());
                    //播放成功并关闭了
                    Toast.makeText(getApplicationContext(),"播放结束",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onShow() {
                Toast.makeText(getApplicationContext(),"开始播放",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRewardVerify() {
                Toast.makeText(getApplicationContext(),"此激励视频有效",Toast.LENGTH_SHORT).show();
            }

            //..更多回调事件请实现OnPlayListener中的方法
        });
    }

    /**
     * 缓存插屏广告
     * @param view
     */
    public void cacheInsert(View view) {
        /**
         * 加载插屏广告
         * @param context Activity类型上下文
         * @param id 广告位ID
         * @param scene 广告播放的场景
         * @param listener 状态监听器
         */
        PlatformManager.getInstance().loadInsert(this,AdConfig.AD_CODE_INSERT_ID,null);
    }

    /**
     * 展示插屏广告
     * @param view
     */
    public void showInsert(View view) {
//        PlatformManager.getInstance().loadInsert(this,AdConfig.AD_CODE_INSERT_ID, new OnTabScreenListener() {
//            @Override
//            public void onSuccess(GMInterstitialFullAd interactionAd) {
//                interactionAd.showAd(MainActivity.this);
//            }
//
//            @Override
//            public void onShow() {
//
//            }
//
//            @Override
//            public void onClick() {
//
//            }
//
//            @Override
//            public void onClose() {
//
//            }
//
//            @Override
//            public void onError(int code, String message, String adCode) {
//
//            }
//        });

        //使用全自动模式播放插屏广告
        /**
         * 尝试播放一个插屏广告
         * @param id 广告ID
         * @param scene 广告播放的场景标识
         * @param delayed 延时多久后开始展示插屏，单位：毫秒
         * @param listener 监听器
         */
        TableScreenManager.getInstance().showInsert(AdConfig.AD_CODE_INSERT_ID,new OnPlayListener() {
            @Override
            public void onClose(Result status) {

            }

            @Override
            public void onError(int code, String message, String adCode) {

            }

            //..更多回调事件请实现OnPlayListener中的方法
        });
    }

    public void showCoustomNativeStream(View view) {
        ExpressView expressView = (ExpressView) findViewById(R.id.adv_coustom_native_stream);
        expressView.setAdType(AdConstance.TYPE_STREAM);//设置广告类型，参考AdConstance定义，1：信息流，3：banner
        expressView.setAdCode(AdConfig.AD_CODE_STREAM_NATIVE_ID);//设置广告位ID
        expressView.setAdWidth(getScreenWidthDP()-20f);//设置广告期望渲染的宽度(不设置默认屏幕宽度)
        expressView.setAdHeight(0f);//设置广告期望渲染的高度(不设置默认自适应高度)，当高度为0f时，内部会自适应高度进行渲染。自渲染类型的广告会忽略这个高度！
        expressView.setScene("10");//设置播放广告的场景(不设置默认为缓存场景)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            expressView.setOutlineProvider(new LayoutProvider(dpToPxInt(5f)));
        }
        expressView.setShowErrorInfo(true);//是否显示加载失败的信息
        //设置自定义渲染原生信息流广告UI填充器(不设置默认使用SDK内部默认UI)
        expressView.setNativeRenderControl(new CoustomNativeRender());
        //设置广告的各种状态监听
        expressView.setOnExpressAdListener(new OnExpressAdListener() {

            @Override
            public void onSuccess() {
                //广告加载成功
                findViewById(R.id.adv_coustom_native_stream).setVisibility(View.VISIBLE);
            }

            @Override
            public void onShow() {
                //广告展示成功
            }

            @Override
            public void onClick() {
                //广告被点击了
            }

            @Override
            public void onAdViewHeight(int adViewWidth, int adViewHeight) {
                //信息流\Banner渲染到Window的实际宽高，单位：像素
            }

            @Override
            public void onClose() {
                //广告被关闭了
                findViewById(R.id.adv_coustom_native_stream).setVisibility(View.GONE);
            }

            @Override
            public void onError(int code, String message, String adCode) {
                //广告加载/渲染出错了
                findViewById(R.id.adv_coustom_native_stream).setVisibility(View.VISIBLE);//如果开启了日志输出，在错误时还需要让广告组件处于可见状态
            }
        });
        expressView.requst();//开始请求广告并渲染
    }

    /**
     * 展示自渲染信息流广告
     * @param view
     */
    public void showNativeStream(View view) {
        ExpressView expressView = (ExpressView) findViewById(R.id.adv_native_stream);
        expressView.setAdType(AdConstance.TYPE_STREAM);//设置广告类型，参考AdConstance定义，1：信息流，3：banner
        expressView.setAdCode(AdConfig.AD_CODE_STREAM_NATIVE_ID);//设置广告位ID
        expressView.setAdWidth(getScreenWidthDP()-20f);//设置广告期望渲染的宽度(不设置默认屏幕宽度)
        expressView.setAdHeight(0f);//设置广告期望渲染的高度(不设置默认自适应高度)，当高度为0f时，内部会自适应高度进行渲染。自渲染类型的广告会忽略这个高度！
        expressView.setScene("10");//设置播放广告的场景(不设置默认为缓存场景)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            expressView.setOutlineProvider(new LayoutProvider(dpToPxInt(5f)));
        }
        expressView.setShowErrorInfo(true);//是否显示加载失败的信息
        expressView.setOnExpressAdListener(new OnExpressAdListener() {
            @Override
            public void onSuccess() {
                findViewById(R.id.adv_native_stream).setVisibility(View.VISIBLE);
            }

            @Override
            public void onShow() {

            }

            @Override
            public void onClick() {

            }

            @Override
            public void onAdViewHeight(int adViewWidth, int adViewHeight) {

            }

            @Override
            public void onClose() {
                findViewById(R.id.adv_native_stream).setVisibility(View.GONE);
            }

            @Override
            public void onError(int code, String message, String adCode) {
                findViewById(R.id.adv_native_stream).setVisibility(View.VISIBLE);//如果开启了日志输出，在错误时还需要让广告组件处于可见状态
            }
        });
        expressView.requst();//开始请求广告并渲染
    }

    /**
     * 展示模板信息流广告信息流
     * @param view
     */
    public void showStream(View view) {
        ExpressView expressView = (ExpressView) findViewById(R.id.adv_stream);
        expressView.setAdWidth(getScreenWidthDP());//设置广告期望渲染的宽度
        expressView.setAdHeight(0f);//设置广告期望渲染的高度，当高度为0f时，内部会自适应高度进行渲染。自渲染类型的广告会忽略这个高度！
        expressView.setScene("11");//设置播放广告的场景
        expressView.setAdType(AdConstance.TYPE_STREAM);//设置广告类型，参考AdConstance定义，1：信息流，3：banner
        expressView.setAdCode(AdConfig.AD_CODE_STREAM_ID);//设置广告位ID
        expressView.setShowErrorInfo(true);//是否显示加载失败的信息
        expressView.requst();
    }

    /**
     * 展示Banner
     * @param view
     */
    public void showBanner(View view) {
        ExpressView expressView = (ExpressView) findViewById(R.id.adv_banner);
        expressView.setAdWidth(getScreenWidthDP());
        expressView.setAdType(AdConstance.TYPE_BANNER);
        expressView.setAdCode(AdConfig.AD_CODE_BANNER_ID);
        expressView.setShowErrorInfo(true);
        expressView.requst();
    }

    public float getScreenWidthDP(){
        return pxToDpInt(getScreenWidth());
    }


    public int getScreenWidth() {
        return getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 将px转换成dp
     * @param pxValue
     * @return
     */
    public int pxToDpInt(float pxValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (pxValue / (scale <= 0 ? 1 : scale) + 0.5f);
    }

    /**
     * 将dp转换成px
     * @param dipValue
     * @return
     */
    public int dpToPxInt(float dipValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}