package com.platform.simple.adn.pangle;

import android.content.Context;
import com.bytedance.msdk.api.v2.ad.custom.bean.GMCustomInitConfig;
import com.bytedance.msdk.api.v2.ad.custom.init.GMCustomAdapterConfiguration;
import com.bytedance.sdk.openadsdk.TTAdConfig;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import java.util.Map;

public class PangleCustomerConfig extends GMCustomAdapterConfiguration {

    @Override
    public void initializeADN(Context context, GMCustomInitConfig gmCustomConfig, Map<String, Object> localExtra) {
        TTAdConfig ttAdConfig = new TTAdConfig.Builder()
                .appId(gmCustomConfig.getAppId())
                .appName("")
                .debug(true) //测试阶段打开，可以通过日志排查问题，上线时去除该调用
                .build();
        TTAdSdk.init(context, ttAdConfig, new TTAdSdk.InitCallback() {
            @Override
            public void success() {
            }

            @Override
            public void fail(int i, String s) {
            }
        });
        callInitSuccess();
    }

    @Override
    public String getNetworkSdkVersion() {
        return "4.0.0.0";
    }

    @Override
    public String getAdapterSdkVersion() {
        return "1.0.0";
    }

}
