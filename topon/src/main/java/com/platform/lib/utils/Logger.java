package com.platform.lib.utils;

import android.util.Log;

/**
 * created by hty
 * 2022/3/24
 * Desc:
 */
public class Logger {

    private static boolean sDebug = true;

    public static final String TAG="PlatformSDK";

    /**
     * 日志级别
     */
    public static final int DEBUG = 0;
    public static final int INFO = 1;
    public static final int ASSERT = 2;
    public static final int VERBOSE = 3;
    public static final int WARN = 4;
    public static final int ERROR = 5;

    public static void setDebug(boolean debug) {
        Logger.sDebug = debug;
    }

    public static void d(String msg) {
        d(TAG,msg);
    }

    public static void d(String tag,String msg) {
        if (!sDebug) return;
        Log.d(tag, msg);
    }

    public static void i(String msg) {
        i(TAG,msg);
    }

    public static void i(String tag,String msg) {
        if (!sDebug) return;
        Log.i(tag, msg);
    }

    public static void v(String msg) {
        v(TAG,msg);
    }

    public static void v(String tag,String msg) {
        if (!sDebug) return;
        Log.v(tag, msg);
    }

    public static void w(String msg) {
        w(TAG,msg);
    }

    public static void w(String tag,String msg) {
        if (!sDebug) return;
        Log.w(tag, msg);
    }

    public static void e(String msg) {
        e(TAG,msg);
    }

    public static void e(String tag,String msg) {
        if (!sDebug) return;
        Log.e(tag, msg);
    }

    /**
     * 日志
     * @param tag 标记
     * @param msg 消息
     * @param level 日志等级
     */
    public static void log(String tag,String msg,int level){
        switch (level) {
            case DEBUG:
            case INFO:
                d(tag,msg);
                break;
            case ASSERT:
                i(tag,msg);
                break;
            case VERBOSE:
                v(tag,msg);
                break;
            case WARN:
                w(tag,msg);
                break;
            case ERROR:
                e(tag,msg);
                break;
        }
    }
}