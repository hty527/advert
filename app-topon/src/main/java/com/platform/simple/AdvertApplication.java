package com.platform.simple;

import android.content.Context;
import android.os.Build;
import android.webkit.WebView;
import androidx.multidex.MultiDexApplication;
import com.platform.lib.constants.AdConstance;
import com.platform.lib.listener.OnEventListener;
import com.platform.lib.listener.OnInitListener;
import com.platform.lib.manager.PlatformManager;
import com.platform.lib.utils.Logger;

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
         * 广告SDK初始化，建议尽可能的早，在application中初始化
         * @param context 全局上下文
         * @param appId 物料 APP_ID(topon后台获取)
         * @param appSecrecy 物料 APP_SECRECY(topon后台获取)
         * @param channel 渠道标识
         * @param debug 是否debug模式，默认：否，debug模式下将输出logcat日志，查看日志请在控制台过滤：PlatformSDK
         * @param listener 初始化状态监听器
         */
        PlatformManager.getInstance().initSdk(this, AdConfig.TO_APP_ID, AdConfig.TO_APP_KAY,null,"rongyao", BuildConfig.DEBUG, new OnInitListener() {

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

    @Override
    public void onTerminate() {
        PlatformManager.getInstance().onTerminate(this);//移除内部的栈活跃监听器
        super.onTerminate();
    }
}
