* 可参考Demo中MainActivity类中的各种广告用法。<br>
* 加载广告前请尽量获取IMEI权限，以免影响ECPM(千次展示单价)。

#### 一、SDK初始化
##### 1、初始化
* 广告SDK初始化，建议尽可能的早，在application中初始化
```
    /**
     * 请尽可能早的在application中初始化
     * 为方便插屏广告展示，请尽量在初始化时传入Application，如果无法传入Application，则需要调用下列方法设置当前宿主Activity,插屏广告在展示的时候会实时使用当前Activity作为宿主展示，请注意更新Activity
     * PlatformManager.getInstance().setActivity(activity);
     */
    private void initSDK() {

        /**
         * 广告SDK初始化，建议尽可能的早，在开始加载广告前初始化，涉及隐私合规请在获得用户授权后初始化
         * @param context 全局上下文
         * @param appId 物料 APP_ID(topon后台获取)
         * @param appSecrecy 物料 APP_SECRECY(topon后台获取)
         * @param channel 渠道标识
         * @param debug 是否debug模式，默认：否，debug模式下将输出logcat日志，日志请过滤：PlatformSDK 查看
         * @param listener 初始化状态监听器
         */
        PlatformManager.getInstance().initSdk(this, AdConfig.TO_APP_ID, AdConfig.TO_APP_KAY,null,"rongyao", BuildConfig.DEBUG, new OnInitListener() {

            /**
             * 如果需要自定义第三方广告平台SDK初始化参数，可复写此方法并返回平台SDK配置。具体请阅读文档：https://docs.toponad.com/#/zh-cn/android/android_doc/android_sdk_init_network
             * @return
             */
            @Override
            public List<ATInitConfig> getSdkConfig() {
                //返回null或者super.getSdkConfig既表示不使用自定义参数初始化第三方广告SDK
                return super.getSdkConfig();
            }

            @Override
            public void onSuccess() {
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
                     * 根据错误码返回文字文案
                     * @param code 错误码参考AdConstance定义的“错误码”
                     * @return 如果返回的文字为空，则使用SDK内部默认文案
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
                     * 自定义回调参数：如果当前topon应用和激励视频开启服务端验证+需要自定义参数回传(在topon后台填写回调地址后由topon回传)至自己服务器，请务必实现此方法并返回自己的自定义参数，此SDK内部会在每次缓存激励视频之前设置回调参数。
                     * 如果只是需要设置用户ID回调参数，可不实现此方法但必须在加载广告前设置用户ID:PlatformManager.getInstance().setUserId("用户ID");
                     * @return
                     */
                    @Override
                    public Map<String, Object> localExtra() {
                        //例如：
                        Map<String, Object> localMap = new HashMap<>();
                        localMap.put(ATAdConst.KEY.USER_ID, "用户ID");
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
                }).setUserId("88888888");//开启服务端验证模式下随意设置的回调参数：用户ID，自定义回调参数请实现localExtra并返回自定义键值对
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
    PlatformManager.getInstance().loadSplash("ad_id", new OnSplashListener() {

        @Override
        public void onSuccess(ATSplashAd atSplashAd) {
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
##### 2.1、全自动激励视频(推荐)
* 全自动激励视频模式下，Topon SDK内部会在合适的时机自动开始缓存下一条激励视频广告，推荐使用全自动激励视频。<br>
* 为获得良好顺滑的播放体验，请按下列两步使用全自动激励视频广告播放能力：<br>
* 2.1.1、初始化全自动激励视频
```
    /**
     * 初始化/开始缓存全自动类型激励视频
     * @param activity 上下文
     * @param id 广告ID
     * @param scene 广告加载/处理的场景
     * @param listener 监听器
     */
    PlayManager.getInstance().initAutoReward(activity, AdConfig.AD_CODE_REWARD_ID, new OnInitListener() {
        @Override
        public void onSuccess(String id) {
            //初始化|缓存全自动激励视频广告成功
        }

        @Override
        public void onError(int code, String message) {
            //初始化|缓存失败
        }
    });
```
* 2.1.2、全自动激励视频播放(参数isAutoModel是关键)
```
    /**
     * 开始播放激励视屏
     * @param ad_code 广告位ID
     * @param scene 播放场景
     * @param isAutoModel 是否启用全自动模式，内部自动加载激励视频广告并且在合适的时机自动缓存下一个激励视频广告实例
     * @param listener 状态监听器
     */
    PlayManager.getInstance().startVideo(AdConfig.AD_CODE_REWARD_ID,true, new OnPlayListener() {

        @Override
        public void onClose(Result result) {
            if(null!=result){
                Logger.d(TAG,"onClose-->result:"+result.getAd_code());
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
* 2.1.3、全自动激励视频自定义播放<br>
* SDK提供全自动激励视频的缓存和播放API，开发者可自己定义交互使用
```
    1、初始化全自动激励视频
    PlatformManager.getInstance().initAutoReward(activity, AdConfig.AD_CODE_REWARD_ID, new OnInitListener() {});
    2、全自动激励视频播放
    PlatformManager.getInstance().showAutoRewardVideo(activity, AdConfig.AD_CODE_REWARD_ID, new OnRewardVideoListener() {});    
```
##### 2.2、普通超简单激励视频(推荐)
* <font color=red>使用PlayManager提供的api播放激励视频广告时，内部默认关闭全自动激励视频加载模式，如需启用，请传入isAutoModel参数时=true。</font>
```
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
                Logger.d(TAG,"onClose-->adCode:"+status.getAd_code()+",isClick:"+status.getIs_click());
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
* SDK内部封装了激励视频广告的拉取、播放等功能交互，也支持自行拉取广告自己展示，请根据业务场景选择。
##### 2.3、常规激励视频
* 2.3.1、常规激励视频缓存
```
    //为提升播放速度，建议调用下列方法提前缓存激励视频广告
    /**
     * 加载激励视频广告
     * 此方法已废弃不推荐使用
     * 请使用{@link #initReward(Activity activity, String id, OnInitListener listener)} 和 {@link #showAutoRewardVideo(Activity,String,String, OnRewardVideoListener)}
     * @param context 上下文,推荐Activity类型
     * @param id 广告位ID
     * @param scene 播放广告的场景
     * @param listener 状态监听器，如果监听器为空内部回自动缓存一条激励视频广告
     */
    PlatformManager.getInstance().loadRewardVideo(AdConfig.AD_CODE_REWARD_ID,null);
```
* 2.3.2、常规激励视频播放
```
    /**
     * 加载激励视频广告
     * 此方法已废弃不推荐使用
     * 请使用{@link #initReward(Activity activity, String id, OnInitListener listener)} 和 {@link #showAutoRewardVideo(Activity,String,String, OnRewardVideoListener)}
     * @param context 上下文,推荐Activity类型
     * @param id 广告位ID
     * @param scene 播放广告的场景
     * @param listener 状态监听器，如果监听器为空内部回自动缓存一条激励视频广告
     */
    PlatformManager.getInstance().loadRewardVideo(AdConfig.AD_CODE_REWARD_ID, new OnRewardVideoListener() {
        @Override
        public void onSuccess(ATRewardVideoAd atRewardVideoAd) {
            //在这里播放激励视频广告
            atRewardVideoAd.show(MainActivity.this);
        }

        @Override
        public void onRewardVerify() {
            //广告有效性验证
        }

        @Override
        public void onError(int code, String message, String adCode) {
            //广告加载失败了
        }

        @Override
        public void onShow() {
            //广告被显示了
        }

        @Override
        public void onClick(ATAdInfo atAdInfo) {
            //广告被点击了
        }

        @Override
        public void onClose(String cpmInfo, String customData) {
            //广告被关闭了
        }
    });
    //获取激励视频的最终第三方广告平台ID,ID详见https://docs.toponad.com/#/zh-cn/android/android_doc/android_sdk_callback_access?id=callback_info说明
    //int adnPlatformId = PlatformManager.getInstance().getAdnPlatformId()
```
##### 3、插屏广告
##### 3.1、全自动插屏(推荐)
* 全自动插屏模式下，Topon SDK内部会在合适的时机自动开始缓存下一条插屏广告，推荐使用全自动插屏。<br>
* 为获得良好顺滑的播放体验，请按下列两步使用全自动插屏广告播放能力：<br>
* 3.1.1、初始化全自动插屏
```
    /**
     * 初始化/开始缓存全自动插屏(可忽略)
     * @param activity 上下文
     * @param id 广告ID
     * @param scene 广告加载/处理的场景
     * @param listener 监听器
     */
    TableScreenManager.getInstance().initAutoInsert(this, AdConfig.AD_CODE_INSERT_ID, new OnInitListener() {
        @Override
        public void onSuccess(String id) {
            //缓存插屏广告成功
        }

        @Override
        public void onError(int code, String message) {
            //缓存失败
        }
    });
```
* 3.1.2、全自动插屏播放(参数isAutoModel是关键)
```
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
            //插屏广告关闭了
        }

        @Override
        public void onError(int code, String message, String adCode) {
            //插屏广告播放失败了
        }

        //..更多回调事件请实现OnPlayListener中的方法
    });
```
* 3.1.3、全自动插屏自定义播放<br>
* SDK提供全自动激励视频的缓存和播放API，开发者可自己定义交互使用
```
    1、初始化全自动激励视频
    PlatformManager.getInstance().initAutoInsert(activity, AdConfig.AD_CODE_INSERT_ID, new OnInitListener() {});
    2、全自动激励视频播放
    PlatformManager.getInstance().showAutoInsert(activity, AdConfig.AD_CODE_INSERT_ID, new OnTabScreenListener() {});    
```
##### 3.2、普通超简单插屏(推荐)
* 使用TableScreenManager提供的api播放插屏广告时，内部默认关闭全自动插屏加载模式，如需启用，请传入isAutoModel参数时=true。
```
    /**
     * 尝试播放一个插屏广告
     * @param id 广告ID
     * @param isAutoModel 是否启用全自动模式，内部自动加载激励视频广告并且在合适的时机自动缓存下一个激励视频广告实例
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
##### 3.3、常规插屏广告
* 3.3.1、常规插屏广告缓存
```
    //为提升播放速度，建议调用下列方法提前缓存插屏广告
    /**
     * 加载插屏广告
     * 此方法已废弃不推荐使用
     * 请使用{@link #initInsert(Activity activity, String id, OnInitListener listener)} + {@link #showAutoInsert(Activity,String,String, OnTabScreenListener)}
     * @param context 上下文,推荐Activity类型
     * @param id 广告位ID
     * @param scene 播放广告的场景标识
     * @param listener 状态监听器，如果监听器为空内部回自动缓存一条插屏广告
     */
    PlatformManager.getInstance().loadInsert(AdConfig.AD_CODE_INSERT_ID,null);
```
* 3.3.2、常规插屏广告播放
```
    /**
     * 加载插屏广告
     * 此方法已废弃不推荐使用
     * 请使用{@link #initInsert(Activity activity, String id, OnInitListener listener)} + {@link #showAutoInsert(Activity,String,String, OnTabScreenListener)}
     * @param context 上下文,推荐Activity类型
     * @param id 广告位ID
     * @param scene 播放广告的场景标识
     * @param listener 状态监听器，如果监听器为空内部回自动缓存一条插屏广告
     */
    PlatformManager.getInstance().loadInsert(AdConfig.AD_CODE_INSERT_ID, new OnTabScreenListener() {
        @Override
        public void onSuccess(ATInterstitial interactionAd) {
            //在这里展示插屏广告
            interactionAd.show(MainActivity.this);
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
     * 自渲染广告处理
     * @param selfRenderView 将广告信息绑定到自定义UI组件上，并且将各子自定义ViewGroup(比如点击按钮、icon组件)绑定到native广告上,selfRenderView:自定义渲染UI组件
     * @param adMaterial 原生广告信息
     * @param adWidth 期望渲染的广告宽度，高度SDK自适应，会通过OnExpressAdListener回调通知实际渲染的高度
     * @param nativePrepareInfo 包含各子自定义ViewGroup(比如点击按钮、icon组件)等组件信息
     */
    @Override
    public void onRenderNativeView(View selfRenderView, ATNativeMaterial adMaterial, float adWidth, ATNativePrepareInfo nativePrepareInfo) {
        //在这里渲染广告信息到UI上，并且将可点击View绑定到nativePrepareInfo中
    }
}

    //将自定义渲染器绑定到ExpressView中(不设置默认使用SDK内部默认UI)
    expressView.setNativeRenderControl(new CoustomNativeRender());
```
##### 4.4、加载信息流广告
```
    /**
     * 加载信息流广告，5.9.7.0本版+支持
     * @param context 必须为Activity类型的上下文
     * @param id 广告位ID
     * @param scene 广告播放场景标识
     * @param adWidth 期望的广告宽，单位：dp，传0表示宽度为屏幕宽
     * @param adHeight 期望的广告高，单位：dp，传0表示高度随广告自动
     * @param listener 状态监听器
     */
    PlatformManager.getInstance().loadStream(this, AdConfig.AD_CODE_STREAM_ID, 1, getScreenWidth(), new OnExpressListener() {
        @Override
        public void onSuccessExpressed(NativeAd nativeAd) {
            //在这里将信息流广告添加到你的ViewGroup中，参考文档：https://docs.toponad.com/#/zh-cn/android/android_doc/android_sdk_native_access_new
        }

        @Override
        public void onSuccessBanner(ATBannerView atBannerView) {

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
     * 加载Banner广告,需要先将Banner添加到ViewGroup上
     * @param id 广告位ID
     * @param viewGroup 装载BannerView的容器
     * @param scene 广告播放场景标识
     * @param adWidth 期望的广告宽，单位：dp，传0表示宽度为屏幕宽
     * @param adHeight 期望的广告高，单位：dp，传0表示高度随广告自动
     * @param listener 状态监听器
     */
    PlatformManager.getInstance().loadBanner(AdConfig.AD_CODE_BANNER_ID,  viewGroup,getScreenWidth(), getScreenHeight(),new OnExpressListener() {
        @Override
        public void onSuccessExpressed(NativeAd nativeAd) {

        }

        @Override
        public void onSuccessBanner(ATBannerView atBannerView) {
            //SDK内部已经将Banner广告添加到了你传入的viewGroup里，请不要重复将atBannerView添加到你的容器中！参考文档：https://docs.toponad.com/#/zh-cn/android/android_doc/android_sdk_banner_access
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
1.anythink_bk_gdt_file_path.xml
    <?xml version="1.0" encoding="utf-8"?>
    <paths xmlns:android="http://schemas.android.com/apk/res/android">
        <external-path name="gdt_sdk_download_path" path="GDTDOWNLOAD" />
        <root-path name="root" path="" />
        <external-cache-path
            name="gdt_sdk_download_path1"
            path="com_qq_e_download" />
        <cache-path
            name="gdt_sdk_download_path2"
            path="com_qq_e_download" />
    </paths>

2.anythink_bk_tt_file_path.xml
    <?xml version="1.0" encoding="utf-8"?>
    <paths>
        <external-path name="tt_external_root" path="." />
        <external-path name="tt_external_download" path="Download" />
        <external-files-path name="tt_external_files_download" path="Download" />
        <files-path name="tt_internal_file_download" path="Download" />
        <cache-path name="tt_internal_cache_download" path="Download" />
        <root-path name="root" path="" />
    </paths>

3.network_security_config.xml
    <?xml version="1.0" encoding="utf-8"?>
    <network-security-config>
        <base-config cleartextTrafficPermitted="true" />
    </network-security-config>
    
```
* 请在你的app的AndroidManifest.xml中增加如下配置，以下配置仅支持快手、穿山甲、优量汇三种平台，如需支持更多，请自行参阅官方文档。
```
    <application
        android:usesCleartextTraffic="true">
        <!--广告-BEGIN-->
        <!--穿山甲-->
        <provider
            android:name="com.bytedance.sdk.openadsdk.multipro.TTMultiProvider"
            android:authorities="${applicationId}.TTMultiProvider"
            android:exported="false" />
        <provider
            android:name="com.bytedance.sdk.openadsdk.TTFileProvider"
            android:authorities="${applicationId}.TTFileProvider"
            android:exported="false"
            android:grantUriPermissions="true">
            <meta-data
                android:name="android.support.FILE_PROVIDER_PATHS"
                android:resource="@xml/anythink_bk_tt_file_path" />
        </provider>
        <!--优量汇-->
        <provider
            android:name="com.qq.e.comm.GDTFileProvider"
            android:authorities="${applicationId}.gdt.fileprovider"
            android:exported="false"
            android:grantUriPermissions="true">
            <meta-data
                android:name="android.support.FILE_PROVIDER_PATHS"
                android:resource="@xml/anythink_bk_gdt_file_path" />
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
-keep class com.bytedance.pangle.** {*;}
-keep class com.bytedance.sdk.openadsdk.** { *; }
-keep class ms.bd.c.Pgl.**{*;}
-keep class com.bytedance.mobsec.metasec.ml.**{*;}
-keep class com.bytedance.embedapplog.** {*;}
-keep class com.bytedance.embed_dr.** {*;}
-keep class com.bykv.vk.** {*;}
-keep class com.lynx.** { *; }
-keep class com.ss.android.**{*;}
-keep class android.support.v4.app.FragmentActivity{}
-keep class androidx.fragment.app.FragmentActivity{}
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

# =================================TOPON-BEGIN=================================
-keep public class com.anythink.**
-keepclassmembers class com.anythink.** {
   *;
}
-keep public class com.anythink.network.**
-keepclassmembers class com.anythink.network.** {
   public *;
}
-dontwarn com.anythink.hb.**
-keep class com.anythink.hb.**{ *;}
-dontwarn com.anythink.china.api.**
-keep class com.anythink.china.api.**{ *;}
# new in v5.6.6
-keep class com.anythink.myoffer.ui.**{ *;}
-keepclassmembers public class com.anythink.myoffer.ui.** {
   public *;
}
# ==================================TOPON-END==================================

#如果集成了OAID sdk
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

##### 2、更多问题请阅读[Topon接入文档][1]<br>
[1]:https://docs.toponad.com/#/zh-cn/android/android_doc/android_sdk_config_cn_access "Topon接入文档"

##### 更多文档更新中。。。