package com.workdawn.simpledynamicplugin;

import com.workdawn.simpledynamicplugin.domain.PluginInfo;

import dalvik.system.DexClassLoader;

/**
 * 插件dex加载器
 * Created by Administrator
 */
public class PluginDexClassLoader extends DexClassLoader {

    public PluginInfo pluginInfo;
    public PluginDexClassLoader(String dexPath, String optimizedDirectory, String libraryPath, ClassLoader parent, PluginInfo pluginInfo) {
        super(dexPath, optimizedDirectory, libraryPath, parent);
        this.pluginInfo = pluginInfo;
    }
}
