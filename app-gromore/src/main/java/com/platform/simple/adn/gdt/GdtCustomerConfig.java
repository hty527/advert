package com.platform.simple.adn.gdt;

import android.content.Context;
import com.bytedance.msdk.api.v2.ad.custom.bean.GMCustomInitConfig;
import com.bytedance.msdk.api.v2.ad.custom.init.GMCustomAdapterConfiguration;
import com.platform.lib.utils.ThreadUtils;
import com.qq.e.comm.managers.GDTAdSdk;
import com.qq.e.comm.managers.setting.GlobalSetting;
import com.qq.e.comm.managers.status.SDKStatus;
import java.util.Map;

/**
 * YLH 自定义初始化类
 */
public class GdtCustomerConfig extends GMCustomAdapterConfiguration {

    private static final String TAG = GdtCustomerConfig.class.getSimpleName();

    @Override
    public void initializeADN(Context context, GMCustomInitConfig gmCustomConfig, Map<String, Object> localExtra) {

        /**
         * 在子线程中进行初始化
         */
        ThreadUtils.runOnThreadPool(new Runnable() {
            @Override
            public void run() {
                GDTAdSdk.init(context, gmCustomConfig.getAppId());
                GlobalSetting.setPersonalizedState(1);//优量汇个性化推荐广告开关，0为开启个性化推荐广告，1为屏蔽个性化推荐广告
                //初始化成功回调
                callInitSuccess();
            }
        });
    }

    @Override
    public String getNetworkSdkVersion() {
        return SDKStatus.getIntegrationSDKVersion();
    }

    @Override
    public String getAdapterSdkVersion() {
        return "1.0.0";
    }

}
