package com.workdawn.simpledynamicplugin;

/**
 * 相关常量声明
 * Created by Administrator
 */
public class Constants {

    /**插件本地保存地址*/
    public final static String PLUGIN_SAVE_PATH_DIR = "d-plugin";
    /**插件解压dex存放目录*/
    public final static String PLUGIN_DEX_OUTPUT_PATH_DIR = "d-plugin-out";
    /**占位activity类全限定名*/
    public final static String VIRTUAL_ACTIVITY = "com.workdawn.simpledynamicplugin.VirtualActivity";
    /**插件activity标识*/
    public final static String PLUGIN_ACT_FLAG = "d-plugin_act";
    /**
     * copy form system
     * signatures included in the package.
     */
    public static final int GET_SIGNATURES  = 0x00000040;
}
