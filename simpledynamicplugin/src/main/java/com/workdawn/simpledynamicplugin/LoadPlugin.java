package com.workdawn.simpledynamicplugin;

/**
 * 加载插件接口
 * Created by Administrator
 */
public interface LoadPlugin<A, V> {
    V loadPlugin(A pluginPath) throws Exception;
}
