* 可参考Demo中MainActivity类中的各种广告用法。<br>
* 加载广告前请尽量获取IMEI权限，以免影响ECPM(千次展示单价)。

#### 一、SDK初始化
##### 1、初始化
* 广告SDK初始化，建议尽可能的早，在application中初始化
```
    /**
     * 请注意下隐私合规情况下尽可能早的初始化SDK,必须在加载广告前初始化SDK
     * 为方便插屏广告展示，请尽量在初始化时传入Application，如果无法传入Application，则需要调用下列方法设置当前宿主Activity,插屏广告在展示的时候会实时使用当前Activity作为宿主展示，请注意更新Activity
     * PlatformManager.getInstance().setActivity(activity);
     */
    private void initSDK() {

        /**
         * 广告SDK初始化，建议尽可能的早，在开始加载广告前初始化，涉及隐私合规请在获得用户授权后初始化
         * @param context 全局上下文，建议为：Application
         * @param appId 物料 APP_ID(gromore后台获取)
         * @param appName 应用名称
         * @param channel 渠道标识
         * @param tag SDK标识
         * @param debug 是否debug模式，默认：否，debug模式下将输出logcat日志
         * @param listener 初始化状态监听器
         */
        PlatformManager.getInstance().initSdk(this, AdConfig.TO_APP_ID, AdConfig.APP_NAME,null,null, BuildConfig.DEBUG, new OnInitListener() {

            /**
             * 如果需要自定义GroMore初始化的参数信息，请复写此方法，返回你自定义的GMAdConfig对象给SDK用来初始化
             * 具体请阅读文档：https://www.csjplatform.com/union/media/union/download/detail?id=84&docId=27212&osType=android
             * @param appId 应用ID，在gromore后台获取
             * @param appName 应用名称
             * @param channel 渠道名称
             * @param debug 是否开启调试模式，true：开启调试模式，false：关闭调试模式
             * @return
             */
            @Override
            public GMAdConfig getSdkConfig(String appId, String appName, String channel, boolean debug) {
                //返回null或者super.getSdkConfig既表示使用SDK内部的GMAdConfig初始化SDK
                return super.getSdkConfig(appId, appName, channel, debug);
            }

            @Override
            public void onSuccess(String id) {
                //广告SDK初始化成功
            }

            @Override
            public void onError(int code, String message) {
                //广告SDK初始化出错了
            }
        });

        //监听广告日志状态
        PlatformManager.getInstance()
                .setDevelop(true)//是否开启开发者模式，开启后调用PlayManager和TableScreenManager来播放激励视频和插屏广告时，内部将自动跳过广告并回调有效结果。
                .setOnEventListener(new OnEventListener() {

                    /**
                     * 返回广告的缓存、展示等是否可用
                     * @return
                     */
                    @Override
                    public boolean isAvailable() {
                        return true;
                    }
                  /**
                   * 根据错误码返回文字文案
                   * @param code 错误码参考AdConstance定义的“错误码”
                   * @return 如果返回的文字为空，则使用SDK内部默认文案
                   *     int CODE_CONTEXT_INVALID            = 1; //上下文无效
                   *     int CODE_ACTIVITY_INVALID           = 2; //Activity无效或已被关闭
                   *     int CODE_VIEWGROUP_INVALID          = 3; //ViewGroup容器无效
                   *     int CODE_APPID_INVALID              = 4; //app_id无效
                   *     int CODE_APPSECRECY_INVALID         = 5; //app_secrecy无效
                   *     int CODE_ID_UNKNOWN                 = 6;//未知的广告位
                   *     int CODE_ID_INVALID                 = 7;//无效的广告位ID
                   *     int CODE_TYPE_INVALID               = 8;//无效的广告位类型
                   *     int CODE_TIMOUT                     = 9;//超时
                   *     int CODE_ADINFO_INVALID             = 10;//广告对象无效
                   *     int CODE_REPEATED                   = 11;//正在显示中
                   *     int CODE_AD_EMPTY                   = 12;//暂无广告填充
                   *     int CODE_AD_LOADING                 = 13;//广告正在请求中
                   *     int CODE_EXIST_CACHE                = 14;//存在缓存广告
                   *     int CODE_APPLY_FAIL                 = 15;//广告应用到布局失败
                   *     int CODE_DEVELOP                    = 16;//开发者模式，跳过广告
                   *     int CODE_CONFIG_LOADING             = 17;//广告配置正在加载中
                   */
                    @Override
                    public String getText(int code) {
                        switch (code) {
                            case AdConstance.CODE_AD_LOADING:
                                return "请稍等,马上就好...";
                        }
                        return null;
                    }

                    /**
                     * 自定义回调参数：如果当前GroMore应用和激励视频开启服务端验证+需要自定义参数回传(在GroMore后台填写回调地址后由GroMore回传)至自己服务器，请务必实现此方法并返回自己的自定义参数，此SDK内部会在每次缓存激励视频之前设置回调参数。
                     * 如果只是需要设置用户ID回调参数，可不实现此方法但必须在加载广告前设置用户ID:PlatformManager.getInstance().setUserId("用户ID");
                     * @return
                     */
                    @Override
                    public Map<String, String> localExtra() {
                        //例如：
                        Map<String, Object> localMap = new HashMap<>();
                        String customData="{\"userId\":\"88888888\",\"key\":\"value\"}";
                        localMap.put(GMAdConstant.CUSTOM_DATA_KEY_PANGLE, customData);//穿山甲平台
                        localMap.put(GMAdConstant.CUSTOM_DATA_KEY_GDT, customData);//优量汇平台
                        localMap.put(GMAdConstant.CUSTOM_DATA_KEY_KS, customData);//快手平台
                        localMap.put(GMAdConstant.CUSTOM_DATA_KEY_GROMORE_EXTRA, customData);//GroMore自定义消息
                        localMap.put("key", "value");
                        //return localMap;
                        return null;
                    }

                    /**
                     * 各广告的回调事件！！！请不要在这个回调里做耗时操作，否则可能引起卡顿！！！
                     * @param scene 播放广告的场景
                     * @param ad_type 广告类型，Constance申明
                     * @param ad_code 广告位ID
                     * @param ad_status 广告状态：1：加载成功 2：加载失败 3：显示成功 4：显示失败
                     * @param error_code 错误码
                     * @param error_msg 全量的错误信息
                     */
                    @Override
                    public void onEvent(String scene, String ad_type, String ad_code, String ad_status, int error_code, String error_msg) {
                        Logger.log(TAG,"onEvent-->scene:"+scene+",ad_type:"+ad_type+",ad_code:"+ad_code+",ad_status:"+ad_status+",error_code:"+error_code+",error_msg:"+error_msg, "2".equals(ad_status)||"4".equals(ad_status)? Logger.ERROR:Logger.INFO);
                    }
                });
    }
```
#### 二、广告加载
##### 1、开屏广告
##### 1.1、超简单开屏(推荐)
* 1.1.1、在xml中引用组件
```
<com.platform.lib.widget.SplashView
    android:id="@+id/ad_splash"
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"/>
```
* 1.1.2、拉取并渲染开屏广告
```
    SplashView splashAdView = (SplashView) findViewById(R.id.ad_splash);
    /**
     * 加载开屏广告
     * @param ad_code 广告位ID
     * @param scene 场景
     * @param width 预期渲染的宽，单位：分辨率
     * @param width 预期渲染的高，单位：分辨率
     * @param listener 状态监听
     */
    splashAdView.loadSplashAd(AdConfig.AD_CODE_SPLASH_ID, new OnSplashStatusListener() {

        @Override
        public void onShow() {
            //广告显示了
        }

        @Override
        public void onClick() {
            //广告被点击了
        }

        @Override
        public void onClose() {
            //广告关闭了，一般成功展示广告后关闭广告回调此方法
        }

        @Override
        public void onError(int code, String message, String adCode) {
            //广告加载失败了
        }
    });
```
##### 1.2、拉取广告自行渲染
```
    /**
     * 加载开屏广告
     * @param context 上下文，可以是全局也可以是Activity的
     * @param id 广告位ID
     * @param scene 场景
     * @param width 预期渲染的宽，单位：分辨率
     * @param width 预期渲染的高，单位：分辨率
     * @param listener 状态监听，如果监听器为空内部回自动缓存一条开屏广告
     */
    PlatformManager.getInstance().loadSplash(this, AdConfig.AD_CODE_SPLASH_ID, new OnSplashListener() {
        @Override
        public void onSuccess(GMSplashAd gmSplashAd) {
            //在这里将广告组件添加到你的ViewGroup中
        }

        @Override
        public void onTimeOut() {
            //拉取广告超时了
        }

        @Override
        public void onShow() {
            //广告显示了
        }

        @Override
        public void onClick() {
            //广告被点击了
        }

        @Override
        public void onClose() {
            //广告关闭了，一般成功展示广告后关闭广告回调此方法
        }

        @Override
        public void onError(int code, String message, String adCode) {
            //广告加载失败了
        }
    });
```
##### 2、激励视频
* SDK内部封装了激励视频广告的拉取、播放等功能交互，也支持自行拉取广告自己展示，请根据业务场景选择。

##### 2.1、超简单激励视频(推荐)
```
    /**
     * 开始播放激励视屏
     * @param ad_code 广告位ID
     * @param scene 播放场景
     * @param listener 状态监听器
     */
    PlayManager.getInstance().startVideo(AdConfig.AD_CODE_REWARD_ID, new OnPlayListener() {
        @Override
        public void onClose(Result result) {
            if(null!=status){//为空表示播放失败了
                Logger.d(TAG,"onClose-->result:"+result.toString());
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
```
##### 2.2、自加载激励视频
* 2.3.1、自加载激励视频缓存
```
    /**
     * 加载激励视频广告
     * @param context Activity类型上下文
     * @param id 广告位ID
     * @param scene 广告展示场景
     * @param rewardName 激励名称
     * @param listener 状态监听器，如果监听器为空内部回自动缓存一条激励视频广告
     */
    PlatformManager.getInstance().loadRewardVideo(this,AdConfig.AD_CODE_REWARD_ID,null)
```
* 2.3.2、自加载激励视频播放
```
    /**
     * 加载激励视频广告
     * @param context Activity类型上下文
     * @param id 广告位ID
     * @param scene 广告展示场景
     * @param rewardName 激励名称
     * @param listener 状态监听器，如果监听器为空内部回自动缓存一条激励视频广告
     */
    PlatformManager.getInstance().loadRewardVideo(AdConfig.AD_CODE_REWARD_ID, new OnRewardVideoListener() {

        @Override
        public void onLoading() {
            //广告正在请求中
        }
        
        @Override
        public void onSuccess(GMRewardAd gmRewardAd) {
            //在这里播放激励视频广告
            gmRewardAd.showRewardAd(MainActivity.this);
        }

        @Override
        public void onRewardVerify() {
            //广告有效性验证
        }

        @Override
        public void onShow() {
            //广告被显示了
        }

        @Override
        public void onClick(GMRewardAd rewardAd) {
            //广告被点击了
        }

        /**
         * @param code 错误码，参考：AdConstance 和 https://www.csjplatform.com/union/media/union/download/detail?id=106&docId=62e23367a0556d002fd3caa6&osType=android
         * @param message 错误信息
         * @param adCode 广告位ID
         */
        @Override
        public void onError(int code, String message, String adCode) {
            //广告加载失败了
        }

        /**
         * @param cpmInfo cpm/cpm精度/展示单价等json字段,例如格式：{"price":"20.28470431","precision":"exact","pre_price":"0.02028470431"}
         * @param customData 自定义透传参数
         */
        @Override
        public void onClose(String cpmInfo, String customData) {
            //广告被关闭了
        }
    });
    //获取激励视频的最终第三方广告平台ID,ID参照GMNetworkPlatformConst类
    //int adnPlatformId = PlatformManager.getInstance().getAdnPlatformId()
    
```
##### 3、插屏广告
##### 3.1、超简单插屏(推荐)
```
    /**
     * 尝试播放一个插屏广告
     * @param id 广告ID
     * @param scene 广告播放的场景标识
     * @param delayed 延时多久后开始展示插屏，单位：毫秒
     * @param listener 监听器
     */
    TableScreenManager.getInstance().showInsert(AdConfig.AD_CODE_INSERT_ID, new OnPlayListener() {
        @Override
        public void onClose(Result status) {
            //广告被关闭了
        }

        @Override
        public void onError(int code, String message, String adCode) {
            //广告加载失败了
        }

        //..更多回调事件请实现OnPlayListener中的方法
    });
```
##### 3.2、自加载插屏广告
* 3.3.1、插屏广告缓存
```
    /**
     * 加载插屏广告
     * @param context Activity类型上下文
     * @param id 广告位ID
     * @param scene 广告播放的场景
     * @param listener 状态监听器
     */
    PlatformManager.getInstance().loadInsert(this,AdConfig.AD_CODE_INSERT_ID,null);
```
* 3.3.2、自加载插屏广告播放
```
    /**
     * 加载插屏广告
     * @param context Activity类型上下文
     * @param id 广告位ID
     * @param scene 广告播放的场景
     * @param listener 状态监听器
     */
    PlatformManager.getInstance().loadInsert(this,AdConfig.AD_CODE_INSERT_ID, new OnTabScreenListener() {

        @Override
        public void onLoading() {
            //广告正在请求中
        }

        @Override
        public void onSuccess(GMInterstitialFullAd interactionAd) {
            //在这里展示插屏广告
            interactionAd.showAd(MainActivity.this);
        }

        @Override
        public void onShow() {
            //广告被显示了
        }

        @Override
        public void onClick() {
            //广告被点击了
        }

        @Override
        public void onClose() {
            //广告被关闭了
        }

        @Override
        public void onError(int code, String message, String adCode) {
            //广告加载失败了
        }
    });
```

##### 4、信息流
##### 4.1、超简单信息流(推荐)
##### 4.1.1、在xml中引用组件
```
    <com.platform.lib.widget.ExpressView
        android:id="@+id/adv_stream"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"/>
```
##### 4.1.2、拉取并渲染信息流
```
    ExpressView expressView = (ExpressView) findViewById(R.id.adv_stream);
    expressView.setAdType(AdConstance.TYPE_STREAM);//设置广告类型，参考AdConstance定义，1：信息流，3：banner
    expressView.setAdCode(AdConfig.AD_CODE_STREAM_ID);//设置广告位ID
    expressView.setAdWidth(getScreenWidthDP()-20f);//设置广告期望渲染的宽度(不设置默认屏幕宽度)
    expressView.setAdHeight(0f);//设置广告期望渲染的高度(不设置默认自适应高度)，当高度为0f时，内部会自适应高度进行渲染。自渲染类型的广告会忽略这个高度！
    expressView.setScene("10");//设置播放广告的场景(不设置默认为缓存场景)
    expressView.setShowErrorInfo(true);//是否显示加载失败的信息
    //设置广告的各种状态监听
    expressView.setOnExpressAdListener(new OnExpressAdListener() {

        @Override
        public void onSuccess() {
            //广告加载成功
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
        }

        @Override
        public void onError(int code, String message, String adCode) {
            //广告加载/渲染出错了
        }
    });
    expressView.requst();//开始请求广告并渲染
    //生命周期处理,信息流广告可能存在视频类型的广告，需要在你的onResume和onPause中分别调用下列方法
    //expressView.onResume();//在你生命周期对应方法中调用
    //expressView.onPause();//在你生命周期对应方法中调用
```
##### 4.2、模板渲染信息流
```
    //1.设置广告类型为信息流
    expressView.setAdType(AdConstance.TYPE_STREAM);//设置广告类型，参考AdConstance定义，1：信息流，3：banner
    //设置模板渲染类型的信息流广告位
    expressView.setAdCode(AdConfig.AD_CODE_STREAM_ID);//设置广告位ID
```
##### 4.3、自渲染原生信息流
##### 4.3.1、自渲染准备
```
    //1.设置广告类型为信息流
    expressView.setAdType(AdConstance.TYPE_STREAM);//设置广告类型，参考AdConstance定义，1：信息流，3：banner
    //设置原生类型的信息流广告位
    expressView.setAdCode(AdConfig.AD_CODE_STREAM_NATIVE_ID);//设置广告位ID
    //设置自定义渲染原生信息流广告UI填充器(不设置默认使用SDK内部默认UI),具体实现请看步骤：4.3.1
    expressView.setNativeRenderControl(new CoustomNativeRender());
```
##### 4.3.1、自渲染实现
* 参考Demo中的CoustomNativeRender类实现
```
    1、class实现NativeRenderControl接口，实现getRenderView()和onRenderNativeView()方法

    2、返回自定义UI和绑定广告信息到视图上以及将可点击View绑定到广告信息中

public class CoustomNativeRender implements NativeRenderControl {

    /**
     * 返自定义UI View
     * @param context 创建自定义UI组件，广告ExpressView上下文
     * @return
     */
    @Override
    public View getRenderView(Context context) {
        //返回你自己的UI
        return View.inflate(context,R.layout.coustom_native_render,null);
    }

    /**
     * 将广告信息绑定到自定义UI组件上，并且将各子自定义ViewGroup(比如点击按钮、icon组件)绑定到native广告上
     * @param selfRenderView 将广告信息绑定到自定义UI组件上，并且将各子自定义ViewGroup(比如点击按钮、icon组件)绑定到native广告上,selfRenderView:自定义渲染UI组件
     * @param nativeAd 原生广告信息
     * @param adWidth 期望渲染的广告宽度，高度SDK自适应，会通过OnExpressAdListener回调通知实际渲染的高度
     */
    @Override
    public void onRenderNativeView(View selfRenderView, com.bytedance.msdk.api.v2.ad.nativeAd.GMNativeAd nativeAd, float adWidth) {
        //在这里渲染广告信息到UI上
    }
}

    //将自定义渲染器绑定到ExpressView中(不设置默认使用SDK内部默认UI)
    expressView.setNativeRenderControl(new CoustomNativeRender());
```
##### 4.4、加载信息流广告
```
    /**
     * 加载信息流广告
     * @param context Activity类型上下文
     * @param id 广告位ID
     * @param scene 广告加载场景
     * @param adCount 预期加载的广告数量
     * @param adWidth 预期加载并渲染的信息流宽度，单位dp
     * @param adHeight 预期加载并渲染的信息流高度，单位dp，为0时自适应高度
     * @param listener
     */
    PlatformManager.getInstance().loadStream(this, AdConfig.AD_CODE_STREAM_ID, new OnExpressListener() {
        @Override
        public void onSuccessExpressed(GMNativeAd gmNativeAd) {
            //在这里将信息流广告添加到你的ViewGroup中，参考文档：https://www.csjplatform.com/union/media/union/download/detail?id=106&docId=62e233017ef212002ebc3a31&osType=android
        }

        @Override
        public void onSuccessBanner(GMBannerAd gmBannerAd) {

        }

        @Override
        public void onShow() {
            //广告被显示了
        }

        @Override
        public void onClick() {
            //广告被点击了
        }

        @Override
        public void onClose() {
            //广告被关闭了
        }

        @Override
        public void onError(int code, String message, String adCode) {
            //广告加载失败了
        }
    });
```
##### 5、Banner
##### 5.1、超简单Banner(推荐)
##### 5.1.1、在xml中引用组件
```
    <com.platform.lib.widget.ExpressView
        android:id="@+id/adv_banner"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"/>
```
##### 5.1.2、拉取并渲染Banner
```
    ExpressView expressView = (ExpressView) findViewById(R.id.adv_banner);
    expressView.setAdWidth(getScreenWidthDP());//设置期望渲染的广告宽度
    expressView.setAdType(AdConstance.TYPE_BANNER);//设置广告类型
    expressView.setAdCode(AdConfig.AD_CODE_BANNER_ID);//设置广告位ID
    expressView.setShowErrorInfo(true);
    //开始拉取广告，内部自动渲染
    expressView.requst();
```
##### 5.2、Banner广告拉取
```
    /**
     * 加载Banner广告
     * @param context Activity类型上下文
     * @param id 广告位ID
     * @param scene 广告加载场景
     * @param adWidth 预期加载并渲染的信息流宽度，单位dp
     * @param adHeight 预期加载并渲染的信息流高度，单位dp，为0时自适应高度
     * @param listener
     */
    PlatformManager.getInstance().loadBanner(this,AdConfig.AD_CODE_BANNER_ID,new OnExpressListener() {
        @Override
        public void onSuccessExpressed(GMNativeAd gmNativeAd) {

        }

        @Override
        public void onSuccessBanner(GMBannerAd gmBannerAd) {
            //在这里将信息流广告添加到你的ViewGroup中，参考文档：https://www.csjplatform.com/union/media/union/download/detail?id=106&docId=62e233167ef212002ebc3a36&osType=android
        }

        @Override
        public void onShow() {
            //广告被显示了
        }

        @Override
        public void onClick() {
            //广告被点击了
        }

        @Override
        public void onClose() {
            //广告被关闭了
        }

        @Override
        public void onError(int code, String message, String adCode) {
            //广告加载失败了
        }
    });
```

#### 四、AndroidManifest.xml配置
* xml文件申明，请将xml文件放在res-xml目录下(除穿山甲、快手、优量汇三个平台之外的其它平台请自行前往官网文档获取)
```
1.pangle_file_paths.xml
    <?xml version="1.0" encoding="utf-8"?>
    <paths xmlns:android="http://schemas.android.com/apk/res/android">
        <!--为了适配所有路径可以设置 path = "." -->
    
        <external-path name="tt_external_root" path="." />
        <external-path name="tt_external_download" path="Download" />
        <external-files-path name="tt_external_files_download" path="Download" />
        <files-path name="tt_internal_file_download" path="Download" />
        <cache-path name="tt_internal_cache_download" path="Download" />
    </paths>
```
* 请在你的app的AndroidManifest.xml中增加如下配置，以下配置仅支持快手、穿山甲、优量汇三种平台，如需支持更多，请自行参阅官方文档。
```
    <application
        android:usesCleartextTraffic="true">
        <!--广告-BEGIN-->
        <!--穿山甲-->
        <provider
            android:name="com.bytedance.sdk.openadsdk.TTFileProvider"
            android:authorities="${applicationId}.TTFileProvider"
            android:exported="false"
            android:grantUriPermissions="true">
            <meta-data
                android:name="android.support.FILE_PROVIDER_PATHS"
                android:resource="@xml/pangle_file_paths" />
        </provider>
        <provider
            android:name="com.bytedance.sdk.openadsdk.multipro.TTMultiProvider"
            android:authorities="${applicationId}.TTMultiProvider"
            android:exported="false" />
        <!--优量汇-->
        <provider
            android:name="com.qq.e.comm.GDTFileProvider"
            android:authorities="${applicationId}.gdt.fileprovider"
            android:exported="false"
            android:grantUriPermissions="true">
            <meta-data
                android:name="android.support.FILE_PROVIDER_PATHS"
                android:resource="@xml/gdt_file_path" />
        </provider>
        <!--广告-END-->
    </application>
```
#### 五、代码混淆
* 请在proguard-rules.pro文件中添加如下代码(仅示例快手、穿山甲、优量汇三种平台混淆，如需支持更多，请自行参阅官方文档。)：
```
# =================================快手广告-BEGIN=================================
-keep class org.chromium.** {*;}
-keep class org.chromium.** { *; }
-keep class aegon.chrome.** { *; }
-keep class com.kwai.**{ *; }
-dontwarn com.kwai.**
-dontwarn com.kwad.**
-dontwarn com.ksad.**
-dontwarn aegon.chrome.**
-keepclassmembers class * {
    *** getContext(...);
    *** getActivity(...);
    *** getResources(...);
    *** startActivity(...);
    *** startActivityForResult(...);
    *** registerReceiver(...);
    *** unregisterReceiver(...);
    *** query(...);
    *** getType(...);
    *** insert(...);
    *** delete(...);
    *** update(...);
    *** call(...);
    *** setResult(...);
    *** startService(...);
    *** stopService(...);
    *** bindService(...);
    *** unbindService(...);
    *** requestPermissions(...);
    *** getIdentifier(...);
   }
# ==================================快手广告-END==================================

# =================================穿山甲广告-BEGIN=================================
## pangle 穿山甲原有的
-keepclassmembers class * {
    *** getContext(...);
    *** getActivity(...);
    *** getResources(...);
    *** startActivity(...);
    *** startActivityForResult(...);
    *** registerReceiver(...);
    *** unregisterReceiver(...);
    *** query(...);
    *** getType(...);
    *** insert(...);
    *** delete(...);
    *** update(...);
    *** call(...);
    *** setResult(...);
    *** startService(...);
    *** stopService(...);
    *** bindService(...);
    *** unbindService(...);
    *** requestPermissions(...);
    *** getIdentifier(...);
   }

-keep class com.bytedance.pangle.** {*;}
-keep class com.bytedance.sdk.openadsdk.** { *; }
-keep class com.bytedance.frameworks.** { *; }
-keep class ms.bd.c.Pgl.**{*;}
-keep class com.bytedance.mobsec.metasec.ml.**{*;}
-keep class com.ss.android.**{*;}
-keep class com.bytedance.embedapplog.** {*;}
-keep class com.bytedance.embed_dr.** {*;}
-keep class com.bykv.vk.** {*;}
# ==================================穿山甲广告-END==================================
# =================================优量汇广告-BEGIN=================================
-keep class com.qq.e.** {
    public protected *;
}
-keep class android.support.v4.**{
    public *;
}
-keep class android.support.v7.**{
    public *;
}
-keep class MTT.ThirdAppInfoNew {
    *;
}
-keep class com.tencent.** {
    *;
}
-dontwarn dalvik.**
-dontwarn com.tencent.smtt.**
# ==================================优量汇广告-END==================================

# =================================GroMore聚合-BEGIN=================================
-keep class bykvm*.**
-keep class com.bytedance.msdk.adapter.**{ public *; }
-keep class com.bytedance.msdk.api.** {
 public *;
}
-keep class com.bytedance.msdk.base.TTBaseAd{*;}
-keep class com.bytedance.msdk.adapter.TTAbsAdLoaderAdapter{
    public *;
    protected <fields>;
}
# ==================================GroMore聚合-END==================================

# =================================OAID-BEGIN=================================
-dontwarn com.bun.**
-dontwarn sun.misc.**
-keep class com.bun.**{ *;}
-keep class com.zui.**{ *;}
-keep class XI.CA.XI.**{*;}
-keep class XI.K0.XI.**{*;}
-keep class XI.XI.K0.**{*;}
-keep class XI.vs.K0.**{*;}
-keep class XI.xo.XI.XI.**{*;}
-keep class com.asus.msa.SupplementaryDID.**{*;}
-keep class com.asus.msa.sdid.**{*;}
-keep class com.bun.lib.**{*;}
-keep class com.bun.miitmdid.**{*;}
-keep class com.huawei.hms.ads.identifier.**{*;}
-keep class com.samsung.android.deviceidservice.**{*;}
-keep class org.json.**{*;}
#OAID-oaid_sdk_1.0.23 START
-keep, includedescriptorclasses class com.asus.msa.SupplementaryDID.** { *; }
-keepclasseswithmembernames class com.asus.msa.SupplementaryDID.** { *; }
-keep, includedescriptorclasses class com.asus.msa.sdid.** { *; }
-keepclasseswithmembernames class com.asus.msa.sdid.** { *; }
-keep public class com.netease.nis.sdkwrapper.Utils {public <methods>;}
-keep class com.samsung.android.deviceidservice.**{*;}
-keep class a.**{*;}
#OAID-native保留方法注解
-dontshrink
-dontwarn android.support.annotation.Keep
#OAID-保留注解，如果不添加改行会导致我们的@Keep注解失效
-keepclasseswithmembernames class * {  # 保持 native 方法不被混淆
    native <methods>;
}
-keepattributes *Annotation*
-keep @android.support.annotation.Keep class **
#OAID-native保留方法注解
# ==================================OAID-END==================================
```
#### 五、其它
##### 1、查看日志输出
* 需要在调用初始化时打开debug调试模式
```
    PlatformManager.getInstance().initSdk(...,true);
```
* 或直接在初始化后设置debug模式
```
    com.platform.lib.utils.Logger.setDebug(true);
```
* 查看日志请在控制台过滤：PlatformSDK

##### 2、更多聚合SDK问题请阅读[GroMore接入文档][1]<br>
[1]:https://www.csjplatform.com/union/media/union/download/detail?id=106&osType=android&locale=zh-CN "GroMore接入文档"

##### 更多文档不定时更新中。。。