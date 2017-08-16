package com.workdawn.simpledynamicplugin.utils;

import com.workdawn.simpledynamicplugin.LoadPlugin;
import com.workdawn.simpledynamicplugin.PluginManager;
import com.workdawn.simpledynamicplugin.domain.PluginInfo;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * 插件加载实现类
 * Created by Administrator
 */
public class LoadPluginImpl implements LoadPlugin<String, ConcurrentHashMap<String , PluginInfo>> {

    private final ConcurrentHashMap<String, Future<ConcurrentHashMap<String , PluginInfo>>> cache = new ConcurrentHashMap<>();

    private static LoadPluginImpl loadPluginImpl = new LoadPluginImpl();

    public static LoadPluginImpl getInstance(){
        return loadPluginImpl;
    }

    @Override
    public ConcurrentHashMap<String , PluginInfo> loadPlugin(final String pluginPath) throws Exception{
        Future<ConcurrentHashMap<String , PluginInfo>> result = cache.get(pluginPath);
        if(result == null){
            Callable<ConcurrentHashMap<String , PluginInfo>> callable = new Callable<ConcurrentHashMap<String, PluginInfo>>() {
                @Override
                public ConcurrentHashMap<String, PluginInfo> call() throws Exception {
                    return PluginManager.getPluginManager().realLoad(pluginPath);
                }
            };
            FutureTask<ConcurrentHashMap<String, PluginInfo>> futureTask = new FutureTask<>(callable);
            result = cache.putIfAbsent(pluginPath, futureTask);
            if(result  == null){
                result = futureTask;
                futureTask.run();
            }
        }
        return result.get();
    }
}
