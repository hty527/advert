package com.platform.lib.bean;

import android.text.TextUtils;

/**
 * created by hty
 * 2022/10/8
 * Desc:广告播放完成的状态及信息
 */
public class Result {

    private String adCode;//广告位ID
    private String isClick;//是否点击了广告，1：点击了广告，0：未点击广告
    private boolean rewardVerify;//是否是一个有效的播放
    private String cpmInfo;//广告的ecpm
    private int platformId;//广告的实际平台标识，详见GMNetworkPlatformConst类
    private String customData;//启用服务端验证下发奖励时的用户自定义数据

    public String getAdCode() {
        return adCode;
    }

    public void setAdCode(String adCode) {
        this.adCode = adCode;
    }

    public String getIsClick() {
        return isClick;
    }

    public void setIsClick(String isClick) {
        this.isClick = isClick;
    }

    public boolean isRewardVerify() {
        return rewardVerify;
    }

    public void setRewardVerify(boolean rewardVerify) {
        this.rewardVerify = rewardVerify;
    }

    public String getCpmInfo() {
        if(TextUtils.isEmpty(cpmInfo)) cpmInfo ="0";
        return cpmInfo;
    }

    public void setCpmInfo(String cpmInfo) {
        this.cpmInfo = cpmInfo;
    }

    public int getPlatformId() {
        return platformId;
    }

    public void setPlatformId(int platformId) {
        this.platformId = platformId;
    }

    public String getCustomData() {
        return customData;
    }

    public void setCustomData(String customData) {
        this.customData = customData;
    }

    @Override
    public String toString() {
        return "Result{" +
                "adCode='" + adCode + '\'' +
                ", isClick='" + isClick + '\'' +
                ", rewardVerify='" + rewardVerify + '\'' +
                ", cpmInfo='" + cpmInfo + '\'' +
                ", platformId=" + platformId +
                ", customData='" + customData + '\'' +
                '}';
    }
}