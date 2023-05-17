package com.platform.simple;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.anythink.core.api.ATSDK;
import com.anythink.core.api.DeviceInfoCallback;
import com.anythink.interstitial.api.ATInterstitialAutoLoadListener;
import com.platform.lib.bean.Result;
import com.platform.lib.constants.AdConstance;
import com.platform.lib.listener.OnExpressAdListener;
import com.platform.lib.listener.OnPlayListener;
import com.platform.lib.listener.OnRewardVideoListener;
import com.platform.lib.listener.OnTabScreenListener;
import com.platform.lib.manager.PlatformManager;
import com.platform.lib.manager.PlayManager;
import com.platform.lib.manager.TableScreenManager;
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
        //Topon sdk 版本号
        String sdkVersionName = ATSDK.getSDKVersionName();
        ATSDK.integrationChecking(this);//注意：不要在提交上架审核的包中带上此API，避免影响上架,在logcat中过滤 anythink 的Tag来查看日志，日志输出的例子如下：
        /**
         * 正确集成Topod SDK后会输出如下日志
         * ********************************** Network Integration Status *************************************
         * ----------------------------------------
         * NetworkName: Tencent  (v4.482)   //聚合的广告平台的名字以及版本号 （v5.7.6以下版本只输出广告平台的名字）
         * SDK: VERIFIED    //验证聚合SDK是否正确，正确则显示VERIFIED，否则显示NOT VERIFIED
         * Activities : VERIFIED    //验证广告平台的Activity声明是否存在，正确则显示VERIFIED，否则显示缺少的配置
         * Services : VERIFIED  //验证广告平台Service声明是否存在，正确则显示VERIFIED，否则显示NOT VERIFIED或缺少的配置
         * Status: Success  //验证广告平台是否全部集成正确，正确则显示Success，否则显示Fail
         * ----------------------------------------
         * NetworkName: Kuaishou  (v3.3.28)
         * SDK: VERIFIED
         * Dependence Plugin: VERIFIED  //验证广告平台依赖的插件是否存在，正确则显示VERIFIED，否则显示缺少的配置
         * Status: Success
         * ----------------------------------------
         * NetworkName: CSJ  (v4.7.1.2)
         * SDK: VERIFIED
         * Providers : VERIFIED //验证广告平台的Provider声明是否存在，正确则显示VERIFIED，否则显示缺少的配置
         * Permission: VERIFIED
         * Status: Success
         * ----------------------------------------
         * ********************************** Network Integration Status *************************************
         */
        //打开网络调试器模式
//        ATSDK.setDebuggerConfig(this, "a00fe693b8c3016f",new ATDebuggerConfig.Builder(the NetworkFirmId you want to test).build());
        boolean cnSDK = ATSDK.isCnSDK();//判断当前使用Topon SDK是否为中国区
        Log.d(TAG,"SDK版本号："+version+",Topon SDK版本号："+sdkVersionName+",Topon SDK是否为中国区："+cnSDK);
        //打印当前设备的设备信息（IMEI、OAID、GAID、AndroidID等）
        ATSDK.testModeDeviceInfo(this, new DeviceInfoCallback() {
            @Override
            public void deviceInfo(String s) {
                Log.d(TAG,"deviceInfo："+s);
            }
        });
        /**普通激励视频-提前缓存激励视频广告(只需要调用一次，内部会自动缓存下一条激励视频广告)**/
        PlatformManager.getInstance().loadRewardVideo(AdConfig.AD_CODE_REWARD_ID,null);//推荐使用全自动加载模式：initReward()

        /**初始化全自动激励视频广告！！！普通激励视频和全自动激励视频二选一使用，不建议混合使用！！！**/
        PlayManager.getInstance().initAutoReward(this,AdConfig.AD_CODE_REWARD_ID,null);

        /**普通插屏-提前缓存插屏广告(只需要调用一次，内部会自动缓存下一条插屏广告)**/
        PlatformManager.getInstance().loadInsert(AdConfig.AD_CODE_INSERT_ID,null);//推荐使用全自动加载模式：initInsert()
        /**初始化全自动插屏广告！！！普通插屏和全自动插屏二选一使用，不建议混合使用！！！**/
        TableScreenManager.getInstance().initAutoInsert(this,AdConfig.AD_CODE_INSERT_ID,null);

        //缓存原生自渲染信息流广告
//        PlatformManager.getInstance().loadStream(this,AdConfig.AD_CODE_STREAM_NATIVE_ID,null);
        //缓存模板信息流广告
//        PlatformManager.getInstance().loadStream(this,AdConfig.AD_CODE_STREAM_ID,null);
    }

    /**
     * 缓存开屏
     * @param view
     */
    public void cacheSplash(View view) {
        PlatformManager.getInstance().loadSplash(AdConfig.AD_CODE_SPLASH_ID,null);
    }

    /**
     * 展示开屏
     * @param view
     */
    public void showSplash(View view) {
        startActivity(new Intent(this, SplashActivity.class));
    }

    /**
     * 常规激励视频
     * @param view
     */
    public void rewardVideo(View view) {
        //普通激励视频提前缓存,只需要调用一次即可，内部会在未禁用自动缓存广告并且广告结束后自动缓存下一条广告。
        //PlatformManager.getInstance().loadRewardVideo(AdConfig.AD_CODE_REWARD_ID,null);

        //禁用内部自动缓存激励视频广告，默认开启
//        PlatformManager.getInstance().enableAutoCacheVideo(false);
        /**
         * 开始播放激励视屏
         * @param ad_code 广告位ID
         * @param scene 播放场景
         * @param isAutoModel 是否启用全自动模式，内部自动加载激励视频广告并且在合适的时机自动缓存下一个激励视频广告实例
         * @param listener 状态监听器
         */
        PlayManager.getInstance().startVideo(AdConfig.AD_CODE_REWARD_ID,new OnPlayListener() {

            /**
             * 无论播放成功或失败，都将回调此方法，当播放成功或开启develop模式时，此result对象不会为空。
             * @param result 本次播放广告的基础信息(原始广告平台、是否已点击、)，可使用toString输出打印
             */
            @Override
            public void onClose(Result result) {
                if(null!=result){
                    Log.d(TAG,"onClose-->result:"+result.toString());
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

        //或者使用下列方式开始播放激励视频，以达到自定义UI交互的效果
        /**
         * 加载激励视频广告
         * 此方法已废弃不推荐使用
         * 请使用{@link #initReward(Activity, String, ATRewardVideoAutoLoadListener)} + {@link #showAutoRewardVideo(Activity,String,String, OnRewardVideoListener)}
         * @param context 上下文
         * @param id 广告位ID
         * @param scene 播放广告的场景
         * @param listener 状态监听器，如果监听器为空内部回自动缓存一条激励视频广告
         */
//        PlatformManager.getInstance().loadRewardVideo(AdConfig.AD_CODE_REWARD_ID, new OnRewardVideoListener() {
//
//            @Override
//            public void onLoading() {
//                //广告正在请求中
//            }
//
//            @Override
//            public void onSuccess(ATRewardVideoAd atRewardVideoAd) {
//                //在这里播放激励视频广告
//                atRewardVideoAd.show(MainActivity.this);
//            }
//
//            @Override
//            public void onRewardVerify() {
//                //广告有效性验证
//            }
//
//            @Override
//            public void onShow() {
//                //广告被显示了
//            }
//
//            @Override
//            public void onClick(ATAdInfo rewardAd) {
//                //广告被点击了
//            }
//
//            /**
//             * @param code 错误码，参考：AdConstance 和 https://docs.toponad.com/#/zh-cn/android/android_doc/android_errorcode
//             * @param message 错误信息
//             * @param adCode 广告位ID
//             */
//            @Override
//            public void onError(int code, String message, String adCode) {
//                //广告加载失败了
//            }
//
//            /**
//             * @param cpmInfo cpm/cpm精度/展示单价等json字段,例如格式：{"price":"20.28470431","precision":"exact","pre_price":"0.02028470431"}
//             * @param customData 自定义透传参数
//             */
//            @Override
//            public void onClose(String cpmInfo, String customData) {
//                //广告被关闭了
//            }
//        });
    }

    /**
     * 全自动激励视频
     * @param view
     */
    public void autoRewardVideo(View view) {
        //播放全自动模式激励视频广告前需要先初始化，只需初始化一次
        //PlayManager.getInstance().initAutoReward(this,AdConfig.AD_CODE_REWARD_ID,null);

        /**
         * 开始播放激励视屏
         * @param ad_code 广告位ID
         * @param scene 播放场景
         * @param isAutoModel 是否启用全自动模式，内部自动加载激励视频广告并且在合适的时机自动缓存下一个激励视频广告实例
         * @param listener 状态监听器
         */
        PlayManager.getInstance().startVideo(AdConfig.AD_CODE_REWARD_ID,true, new OnPlayListener() {

            /**
             * 无论播放成功或失败，都将回调此方法，当播放成功或开启develop模式时，此result对象不会为空。
             * @param result 本次播放广告的基础信息(原始广告平台、是否已点击、)，可使用toString输出打印
             */
            @Override
            public void onClose(Result result) {
                if(null!=result){
                    Log.d(TAG,"onClose-->result:"+result.toString());
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
     * 插屏广告
     * @param view
     */
    public void insert(View view) {
        /**
         * 加载插屏广告
         * 此方法已废弃不推荐使用
         * 请使用{@link #initInsert(Activity, String, ATInterstitialAutoLoadListener)} + {@link #showAutoInsert(Activity,String,String, OnTabScreenListener)}
         * @param context 上下文
         * @param id 广告位ID
         * @param scene 播放广告的场景标识
         * @param listener 状态监听器，如果监听器为空内部回自动缓存一条插屏广告
         */
        //普通插屏提前缓存,只需要调用一次即可，内部会在广告关闭后自动缓存下一条广告。
        //PlatformManager.getInstance().loadInsert(AdConfig.AD_CODE_REWARD_ID,null);

        /**
         * 尝试播放一个插屏广告
         * @param id 广告ID
         * @param isAutoModel 是否启用全自动模式，内部自动加载激励视频广告并且在合适的时机自动缓存下一个激励视频广告实例
         * @param scene 广告播放的场景标识
         * @param delayed 延时多久后开始展示插屏，单位：毫秒
         * @param listener 监听器
         */
        TableScreenManager.getInstance().showInsert(AdConfig.AD_CODE_INSERT_ID ,new OnPlayListener() {
            @Override
            public void onClose(Result status) {

            }

            @Override
            public void onError(int code, String message, String adCode) {

            }

            //..更多回调事件请实现OnPlayListener中的方法
        });

        //或者使用下列方式开始插屏
//        PlatformManager.getInstance().loadInsert(AdConfig.AD_CODE_INSERT_ID, new OnTabScreenListener() {
//            @Override
//            public void onLoading() {
//                //广告正在请求中
//            }
//            @Override
//            public void onSuccess(ATInterstitial interactionAd) {
//                //在这里展示插屏广告
//                interactionAd.show(MainActivity.this);
//            }
//
//            @Override
//            public void onShow() {
//                //广告被显示了
//            }
//
//            @Override
//            public void onClick() {
//                //广告被点击了
//            }
//
//            @Override
//            public void onClose() {
//                //广告被关闭了
//            }
//
//            @Override
//            public void onError(int code, String message, String adCode) {
//                //广告加载失败了
//            }
//        });
    }

    /**
     * 全自动插屏广告
     * @param view
     */
    public void autoInsert(View view) {
        //播放全自动模式激励视频广告前需要先初始化，只需初始化一次
        /**
         * 缓存/直接显示插屏广告
         * @param activity 显示插屏广告的宿主Activity
         * @param id 广告位ID
         * @param scene 播放场景标识
         * @param listener 状态监听器
         */
        //PlatformManager.getInstance().initAutoInsert(this, AdConfig.AD_CODE_INSERT_ID, null);

        //使用全自动模式播放插屏广告
        /**
         * 尝试播放一个插屏广告
         * @param id 广告ID
         * @param isAutoModel 是否启用全自动模式，内部自动加载激励视频广告并且在合适的时机自动缓存下一个激励视频广告实例
         * @param scene 广告播放的场景标识
         * @param delayed 延时多久后开始展示插屏，单位：毫秒
         * @param listener 监听器
         */
        TableScreenManager.getInstance().showInsert(AdConfig.AD_CODE_INSERT_ID,true, new OnPlayListener() {
            @Override
            public void onClose(Result status) {

            }

            @Override
            public void onError(int code, String message, String adCode) {

            }

            //..更多回调事件请实现OnPlayListener中的方法
        });
    }

    public void showCustomNativeStream(View view) {
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
        //生命周期处理,信息流广告可能存在视频类型的广告，需要在你的onResume和onPause中分别调用下列方法
        //expressView.onResume();//在你生命周期对应方法中调用
        //expressView.onPause();//在你生命周期对应方法中调用
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