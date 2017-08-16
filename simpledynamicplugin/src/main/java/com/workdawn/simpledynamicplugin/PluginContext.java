package com.workdawn.simpledynamicplugin;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.ApplicationInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Process;

import com.workdawn.simpledynamicplugin.domain.PluginInfo;
import com.workdawn.simpledynamicplugin.utils.Utils;

/**
 * 插件上下文对象
 * Created by Administrator
 */
public class PluginContext extends ContextWrapper {

    PluginInfo pluginInfo;

    public PluginContext(PluginInfo pluginInfo) {
        super(pluginInfo.getHostContext());
        this.pluginInfo = pluginInfo;
    }

    @Override
    public AssetManager getAssets() {
        return pluginInfo.getAssetManager();
    }

    @Override
    public Resources getResources() {
        return pluginInfo.getResources();
    }

    @Override
    public ClassLoader getClassLoader() {
        return pluginInfo.getClassLoader();
    }

    @Override
    public Resources.Theme getTheme() {
        Resources.Theme theme = pluginInfo.getResources().newTheme();
        theme.applyStyle(Utils.selectDefaultTheme(pluginInfo.getPackageInfo().applicationInfo.theme, pluginInfo.getPackageInfo().applicationInfo.targetSdkVersion), false);
        return theme;
    }

    @Override
    public Context getApplicationContext() {
        return pluginInfo.getApplication();
    }

    @Override
    public ApplicationInfo getApplicationInfo() {
        return pluginInfo.getPackageInfo().applicationInfo;
    }

    @Override
    public String getPackageName() {
        return pluginInfo.getPackageInfo().packageName;
    }

    @Override
    public String getPackageResourcePath() {
        int myUid = Process.myUid();
        ApplicationInfo appInfo = this.pluginInfo.getPackageInfo().applicationInfo;
        return appInfo.uid == myUid ? appInfo.sourceDir : appInfo.publicSourceDir;
    }

    @Override
    public String getPackageCodePath() {
        return pluginInfo.getPackageInfo().applicationInfo.sourceDir;
    }

    @Override
    public Context getBaseContext() {
        return pluginInfo.getHostContext() instanceof ContextWrapper ? ((ContextWrapper) pluginInfo.getHostContext()).getBaseContext() : pluginInfo.getHostContext();
    }
}
