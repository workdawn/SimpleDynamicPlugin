package com.workdawn.simpledynamicplugin.exception;

/**
 * 插件管理器没有初始化异常
 * Created by Administrator
 */
public class PluginManagerNotInitException extends RuntimeException {

    public PluginManagerNotInitException(String detailMessage) {
        super(detailMessage);
    }
}
