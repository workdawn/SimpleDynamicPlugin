package com.workdawn.simpledynamicplugin;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.view.ContextThemeWrapper;

import com.workdawn.simpledynamicplugin.domain.PluginInfo;
import com.workdawn.simpledynamicplugin.utils.ReflectUtils;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 插件Instrumentation用来代理实际Instrumentation
 * Created by Administrator
 */
public class PluginInstrumentation extends Instrumentation {

    private Instrumentation originalInstrumentation;
    private Context mContext;

    public PluginInstrumentation(Context context, Instrumentation originalInstrumentation){
        this.originalInstrumentation = originalInstrumentation;
        this.mContext = context;
    }

    /**
     * 转发Instrumentation方法
     * @throws Exception
     */
    public ActivityResult execStartActivity (
            Context who, IBinder contextThread, IBinder token, Activity target,
            Intent intent, int requestCode, Bundle options) throws Exception{
        ActivityInfo pluginActInfo = intent.getParcelableExtra(Constants.PLUGIN_ACT_FLAG);
        String pluginPkg = intent.getComponent().getPackageName();
        String pluginClass = intent.getComponent().getClassName();
        if(pluginActInfo == null){
            ConcurrentHashMap<String , PluginInfo>  plugins = PluginManager.getPluginManager().getPlugins();
            PluginInfo pluginInfo = plugins.get(pluginPkg);
            if(pluginInfo != null){
                intent.setClass(mContext, VirtualActivity.class);
                ConcurrentHashMap<String, ActivityInfo> tempActInfoS = pluginInfo.acts;
                if(tempActInfoS != null && tempActInfoS.size() > 0){
                    ActivityInfo tempActInfo = tempActInfoS.get(pluginClass);
                    intent.putExtra(Constants.PLUGIN_ACT_FLAG, tempActInfo);
                }
            }
        }
        Class<?>[] paramTypes = new Class[]{Context.class, IBinder.class, IBinder.class, Activity.class, Intent.class, int.class, Bundle.class};
        return (ActivityResult) ReflectUtils.invokeMethod(Instrumentation.class, originalInstrumentation, "execStartActivity", new Object[]{who, contextThread, token, target, intent, requestCode, options}, paramTypes);
    }

    @Override
    public Activity newActivity(ClassLoader cl, String className, Intent intent) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        ActivityInfo pluginActInfo = intent.getParcelableExtra(Constants.PLUGIN_ACT_FLAG);
        if(pluginActInfo != null){
            ConcurrentHashMap<String, PluginInfo> plugins = PluginManager.getPluginManager().getPlugins();
            PluginInfo pluginInfo = plugins.get(pluginActInfo.packageName);
            if(pluginInfo != null){
                className = pluginActInfo.name;
                cl = pluginInfo.getClassLoader();
            }else{
                throw new RuntimeException("Target PackageName not found");
            }
        }
        return super.newActivity(cl, className, intent);
    }

    @Override
    public void callActivityOnCreate(Activity activity, Bundle icicle) {
        try{
            ClassLoader classLoader = activity.getClass().getClassLoader();
            if(classLoader instanceof PluginDexClassLoader){
                PluginInfo pluginInfo = ((PluginDexClassLoader) classLoader).pluginInfo;
                //用插件中的信息替换activity中的各种值
                ActivityInfo tempActivityInfo = pluginInfo.acts.get(activity.getClass().getName());
                ReflectUtils.setFieldValue(Activity.class, activity, "mApplication", pluginInfo.getApplication());
                ReflectUtils.setFieldValue(Activity.class, activity, "mActivityInfo", tempActivityInfo);
                ReflectUtils.setFieldValue(ContextWrapper.class, activity, "mBase", pluginInfo.getPluginContext());
                ReflectUtils.setFieldValue(ContextThemeWrapper.class, activity, "mResources", pluginInfo.getResources());
                ReflectUtils.setFieldValue(ContextThemeWrapper.class, activity, "mTheme", pluginInfo.getPluginContext().getTheme());
                ReflectUtils.setFieldValue(ContextThemeWrapper.class, activity, "mThemeResource", tempActivityInfo.getThemeResource());
                callApplicationOnCreate(pluginInfo.getApplication());
                int themeResId = tempActivityInfo.theme;
                if(themeResId != 0){
                    activity.getTheme().applyStyle(tempActivityInfo.theme, true);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        super.callActivityOnCreate(activity, icicle);
    }

    @Override
    public void callActivityOnDestroy(Activity activity) {
        super.callActivityOnDestroy(activity);
    }

    @Override
    public void callActivityOnRestoreInstanceState(Activity activity, Bundle savedInstanceState) {
        super.callActivityOnRestoreInstanceState(activity, savedInstanceState);
    }

    @Override
    public void callActivityOnNewIntent(Activity activity, Intent intent) {
        super.callActivityOnNewIntent(activity, intent);
    }

    @Override
    public void callActivityOnStart(Activity activity) {
        super.callActivityOnStart(activity);
    }

    @Override
    public void callActivityOnRestart(Activity activity) {
        super.callActivityOnRestart(activity);
    }

    @Override
    public void callActivityOnResume(Activity activity) {
        super.callActivityOnResume(activity);
    }

    @Override
    public void callActivityOnStop(Activity activity) {
        super.callActivityOnStop(activity);
    }

    @Override
    public void callActivityOnSaveInstanceState(Activity activity, Bundle outState) {
        super.callActivityOnSaveInstanceState(activity, outState);
    }

    @Override
    public void callActivityOnPause(Activity activity) {
        super.callActivityOnPause(activity);
    }
}
