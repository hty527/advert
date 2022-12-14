package com.platform.lib.bean;

import android.text.TextUtils;

/**
 * created by hty
 * 2022/10/8
 * Desc:广告播放完成的状态及信息
 */
public class Result {

    private String ad_source;//当前广告的实际平台，1：穿山甲，3：优量汇，5：快手，8：topon聚合平台
    private String ad_code;//广告位ID
    private String is_click;//是否点击了广告，1：点击了广告，0：未点击广告
    private String ecpm;//广告的ecpm

    public String getAd_source() {
        return ad_source;
    }

    public void setAd_source(String ad_source) {
        this.ad_source = ad_source;
    }

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
}