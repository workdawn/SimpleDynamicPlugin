package com.workdawn.androiddynamicplugin;

import android.app.Application;

import com.workdawn.simpledynamicplugin.PluginManager;

/**
 * 测试Application
 * Created by Administrator
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        try{
            PluginManager.init(this);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
