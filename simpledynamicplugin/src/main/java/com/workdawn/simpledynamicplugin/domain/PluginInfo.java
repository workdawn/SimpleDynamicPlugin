package com.workdawn.simpledynamicplugin.domain;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageParser;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;


import com.workdawn.simpledynamicplugin.PluginContext;
import com.workdawn.simpledynamicplugin.PluginDexClassLoader;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 插件信息
 * Created by Administrator
 */
public class PluginInfo {
    public PackageInfo packageInfo;
    public AssetManager assetManager;
    public Resources resources;
    public PluginDexClassLoader classLoader;
    public Context hostContext;
    public Application application;
    public PluginContext pluginContext;
    public PackageParser.Package pkg;
    public String mainActivity;
    private boolean findDefaultActivity = false;
    public ConcurrentHashMap<String, ActivityInfo> acts = new ConcurrentHashMap<>();
    public ConcurrentHashMap<String, ActivityInfo> receivers = new ConcurrentHashMap<>();
    public ConcurrentHashMap<String, ServiceInfo> services = new ConcurrentHashMap<>();
    public ConcurrentHashMap<String, ProviderInfo> providers = new ConcurrentHashMap<>();

    public PluginInfo(){}

    public PackageInfo getPackageInfo() {
        return packageInfo;
    }

    public void setPackageInfo(PackageInfo packageInfo, PackageParser.Package pkg) {
        this.packageInfo = packageInfo;
        this.pkg = pkg;
        resolveComponentInfo(packageInfo, pkg);
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }

    public void setAssetManager(AssetManager assetManager) {
        this.assetManager = assetManager;
    }

    public Resources getResources() {
        return resources;
    }

    public void setResources(Resources resources) {
        this.resources = resources;
    }

    public PluginDexClassLoader getClassLoader() {
        return classLoader;
    }

    public void setClassLoader(PluginDexClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public Context getHostContext() {
        return hostContext;
    }

    public void setHostContext(Context hostContext) {
        this.hostContext = hostContext;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public PluginContext getPluginContext() {
        return pluginContext;
    }

    public void setPluginContext(PluginContext pluginContext) {
        this.pluginContext = pluginContext;
    }

    public String getMainActivity() {
        return mainActivity;
    }

    public void setMainActivity(String mainActivity) {
        this.mainActivity = mainActivity;
    }

    /**
     * 解析插件中的相关组件信息
     * @param packageInfo 插件包信息
     */
    private void resolveComponentInfo(PackageInfo packageInfo, PackageParser.Package pkg){
        ActivityInfo[] activities = packageInfo.activities;
        if(activities != null && activities.length > 0){
            for(int i = 0; i < activities.length; i++){
                ActivityInfo activityInfo = activities[i];
                acts.put(activityInfo.name, activityInfo);
                if(!findDefaultActivity){
                    ArrayList<PackageParser.ActivityIntentInfo> activityIntentInfoS =  pkg.activities.get(i).intents;
                    for (PackageParser.ActivityIntentInfo intentInfo : activityIntentInfoS) {
                        if(intentInfo.hasAction(Intent.ACTION_MAIN) && intentInfo.hasCategory(Intent.CATEGORY_LAUNCHER)){
                            setMainActivity(activityInfo.name);
                            findDefaultActivity = true;
                            break;
                        }
                    }
                }
            }
        }
        ActivityInfo[] receiversTemp = packageInfo.receivers;
        if(receiversTemp != null && receiversTemp.length > 0){
            for (ActivityInfo receiver : receiversTemp){
                receivers.put(receiver.name, receiver);
            }
        }
        ServiceInfo[] serS = packageInfo.services;
        if(serS != null && serS.length > 0){
            for (ServiceInfo service : serS) {
                services.put(service.name, service);
            }
        }
        ProviderInfo[] proInfoS = packageInfo.providers;
        if(proInfoS != null && proInfoS.length > 0){
            for(ProviderInfo provider : proInfoS){
                providers.put(provider.name, provider);
            }
        }
    }
}
