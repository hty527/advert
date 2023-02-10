package com.platform.lib.bean;

import android.text.TextUtils;

/**
 * created by hty
 * 2022/10/8
 * Desc:广告播放完成的状态及信息
 */
public class Result {

    private String ad_code;//广告位ID
    private String is_click;//是否点击了广告，1：点击了广告，0：未点击广告
    private String ecpm;//广告的ecpm
    private int platformId;//广告的实际平台标识，详见GMNetworkPlatformConst类

    public String getAd_code() {
        return ad_code;
    }

    public void setAd_code(String ad_code) {
        this.ad_code = ad_code;
    }

    public String getIs_click() {
        return is_click;
    }

    public void setIs_click(String is_click) {
        this.is_click = is_click;
    }

    public String getEcpm() {
        if(TextUtils.isEmpty(ecpm)) ecpm="0";
        return ecpm;
    }

    public void setEcpm(String ecpm) {
        this.ecpm = ecpm;
    }

    public int getPlatformId() {
        return platformId;
    }

    public void setPlatformId(int platformId) {
        this.platformId = platformId;
    }
}