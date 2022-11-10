package com.platform.simple;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.platform.lib.listener.OnSplashStatusListener;
import com.platform.lib.widget.SplashView;

/**
 * created by hty
 * 2022/10/8
 * Desc:开屏广告演示
 */
public class SplashActivity extends AppCompatActivity implements OnSplashStatusListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //拉取开屏广告
        SplashView splashAdView = (SplashView) findViewById(R.id.ad_splash);
        splashAdView.loadSplashAd(AdConfig.AD_CODE_SPLASH_ID, this);
    }

    @Override
    public void onShow() {

    }

    @Override
    public void onClose() {
        close();
    }

    @Override
    public void onClick() {

    }

    @Override
    public void onError(int code, String message, String adCode) {
        ((TextView) findViewById(R.id.tv_status)).setText(String.format("广告加载失败,code:%s,adCode:%s\nmessage:%s",code,adCode,message));
    }

    /**
     * 跳转至首页
     */
    private void close() {
        finish();
    }
}