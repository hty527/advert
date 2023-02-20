package com.platform.lib.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import com.platform.lib.widget.LayoutProvider;
import com.platform.lib.BuildConfig;
import java.lang.reflect.Method;

/**
 * created by hty
 * 2022/3/24
 * Desc:
 */
public final class PlatformUtils {

    private volatile static PlatformUtils mInstance;
    private Activity mActivity;//这个Activity用在Flutter语言中，Flutter语言需要赋值此Activity
    private Context mContext;//全局上下文

    public static PlatformUtils getInstance() {
        if(null==mInstance){
            synchronized (PlatformUtils.class) {
                if (null == mInstance) {
                    mInstance = new PlatformUtils();
                }
            }
        }
        return mInstance;
    }

    public Activity getActivity() {
        return mActivity;
    }

    public void setActivity(Activity activity) {
        mActivity = activity;
    }

    public void setContext(Context context) {
        mContext = context;
    }

    public Context getContext() {
        if(null!=mContext){
            return mContext;
        }
        mContext = getApplicationContext();
        return mContext;
    }

    /**
     * 获取上下文所在的Activity
     * @param context
     * @return
     */
    public Activity getActivity(Context context) {
        if(null!=mActivity&&!mActivity.isFinishing()){
            return mActivity;
        }
        if (context == null) return null;
        if (context instanceof Activity) {
            return (Activity) context;
        } else if (context instanceof android.view.ContextThemeWrapper) {
            return getActivity(((android.view.ContextThemeWrapper) context).getBaseContext());
        }
        return null;
    }

    /**
     * 反射获取Context
     * @return
     */
    public Context getApplicationContext() {
        try {
            Class<?> ActivityThread = Class.forName("android.app.ActivityThread");
            Method method = ActivityThread.getMethod("currentActivityThread");
            Object currentActivityThread = method.invoke(ActivityThread);//获取currentActivityThread 对象
            Method method2 = currentActivityThread.getClass().getMethod("getApplication");
            return (Context)method2.invoke(currentActivityThread);//获取 Context对象
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public int parseInt(String value) {
        if(TextUtils.isEmpty(value)) return 0;
        try {
            return Integer.valueOf(value);
        }catch (Throwable e){
            e.printStackTrace();
            return 0;
        }
    }

    public int getScreenWidth() {
        return getContext().getResources().getDisplayMetrics().widthPixels;
    }

    public int getScreenHeight() {
        return getContext().getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * 将px转换成dp
     * @param pxValue
     * @return
     */
    public int pxToDpInt(float pxValue) {
        try {
            final float scale = getContext().getResources().getDisplayMetrics().density;
            return (int) (pxValue / (scale <= 0 ? 1 : scale) + 0.5f);
        }catch (Throwable e){
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 将dp转换成px
     * @param dipValue
     * @return
     */
    public int dpToPxInt(float dipValue) {
        try {
            final float scale = getContext().getResources().getDisplayMetrics().density;
            return (int) (dipValue * scale + 0.5f);
        }catch (Throwable e){
            e.printStackTrace();
        }
        return 0;
    }

    public float getScreenWidthDP(){
        return pxToDpInt(getScreenWidth());
    }

    public void removeViewByGroup(ViewGroup viewGroup) {
        if(null!=viewGroup){
            try {
                if (viewGroup.getParent() != null) {
                    if(viewGroup.getParent() instanceof ViewGroup){
                        ((ViewGroup) viewGroup.getParent()).removeView(viewGroup);
                    }
                }
            }catch (Throwable e){
            }
        }
    }

    public void removeViewByGroup(View view) {
        if(null!=view){
            try {
                if(null!=view&&null!=view.getParent() && view.getParent() instanceof ViewGroup){
                    ((ViewGroup) view.getParent()).removeView(view);
                }
            }catch (Throwable e){
            }
        }
    }

    public boolean checkedPreferencesExist(){
        return checkedClassExist("");//com.pangle.core.Core
    }

    public boolean checkedClassExist(String className) {
        boolean exist=true;
        try {
            Class.forName(className);
        } catch (Throwable e) {
            exist=false;
        }finally {
            return exist;
        }
    }

    public void setCircleRadius(View view, int radius) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.setOutlineProvider(new LayoutProvider(radius));
        }
    }

    public String getVersion(){
        return BuildConfig.VERSION_NAME;
    }
}