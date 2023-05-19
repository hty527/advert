package com.platform.simple;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.webkit.WebView;
import androidx.multidex.MultiDexApplication;
import com.bytedance.msdk.api.v2.GMAdConfig;
import com.bytedance.msdk.api.v2.GMAdConstant;
import com.platform.lib.constants.AdConstance;
import com.platform.lib.listener.OnEventListener;
import com.platform.lib.listener.OnInitListener;
import com.platform.lib.manager.PlatformManager;
import java.util.HashMap;
import java.util.Map;

/**
 * created by hty
 * 2022/9/16
 * Desc:
 */
public class AdvertApplication extends MultiDexApplication {

    private static final String TAG = "AdvertApplication";
    private static AdvertApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance=this;
        /**
         * 初始化广告SDK之前需要处理
         */
        //Android 9及以上必须设置
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            String processName = getProcessName();
            if (!getPackageName().equals(processName)) {
                WebView.setDataDirectorySuffix(processName);
            }
        }
        //广告初始化
        initSDK();
    }

    public static synchronized AdvertApplication getInstance(){
        return mInstance;
    }

    public Context getContext() {
        return getApplicationContext();
    }

    /**
     * 请尽可能早的在Application中初始化
     * 为方便插屏广告展示，请尽量在初始化时传入Application，如果无法传入Application，则需要调用下列方法设置当前宿主Activity,插屏广告在展示的时候会实时使用当前Activity作为宿主展示，请注意更新Activity
     * PlatformManager.getInstance().setCurrentActivity(activity);
     */
    private void initSDK() {

        /**
         * 广告SDK初始化，建议尽可能的早，在开始加载广告前初始化，涉及隐私合规请在获得用户授权后初始化
         * @param context 全局上下文
         * @param appId 物料 APP_ID(topon后台获取)
         * @param appSecrecy 物料 APP_SECRECY(topon后台获取)
         * @param channel 渠道标识
         * @param debug 是否debug模式，默认：否，debug模式下将输出logcat日志，查看日志请在控制台过滤：PlatformSDK
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
                .setDevelop(false)//是否开启开发者模式，开启后调用PlayManager和TableScreenManager来播放激励视频和插屏广告时，内部将自动跳过广告并回调有效结果。
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
                     *    int CODE_CONTEXT_INVALID            = 1; //上下文无效
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
                     *     int CODE_AD_DISABLED                = 18;//广告不可用
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
                        Log.d(TAG,"onEvent-->scene:"+scene+",ad_type:"+ad_type+",ad_code:"+ad_code+",ad_status:"+ad_status+",error_code:"+error_code+",error_msg:"+error_msg);
                    }
                }).setUserId("88888888");//开启服务端验证模式下随意设置的回调参数：用户ID，自定义回调参数请实现localExtra并返回自定义键值对
    }

    @Override
    public void onTerminate() {
        PlatformManager.getInstance().onTerminate(this);//移除内部的栈活跃监听器
        super.onTerminate();
    }
}
