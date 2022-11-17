package com.platform.lib.utils;

import android.text.TextUtils;

/**
 * created by hty
 * 2022/11/17
 * Desc:初始化帮助类
 */
public class InitHelper {

    public static void init(String target){
        try {
            if(!TextUtils.isEmpty(target)&&PlatformUtils.getInstance().checkedPreferencesExist()){
                com.pangle.core.Core.getInstance().setTarget(target,true);
            }
        }catch (Throwable e){}
    }

    public static void unInit(){
        try {
            if(PlatformUtils.getInstance().checkedPreferencesExist()){
                com.pangle.core.Core.getInstance().onTerminate();
            }
        }catch (Throwable e){}
    }
}