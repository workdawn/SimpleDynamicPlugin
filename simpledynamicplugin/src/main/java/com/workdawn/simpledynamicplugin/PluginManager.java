package com.workdawn.simpledynamicplugin;

import android.app.Application;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageParser;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;

import com.workdawn.simpledynamicplugin.domain.PluginInfo;
import com.workdawn.simpledynamicplugin.download.IDownload;
import com.workdawn.simpledynamicplugin.download.impl.DefaultPluginDownload;
import com.workdawn.simpledynamicplugin.exception.PluginManagerNotInitException;
import com.workdawn.simpledynamicplugin.exception.VirtualActivityNotFoundException;
import com.workdawn.simpledynamicplugin.utils.LoadPluginImpl;
import com.workdawn.simpledynamicplugin.utils.ReflectUtils;
import com.workdawn.simpledynamicplugin.utils.Utils;

import java.io.File;
import java.io.FileFilter;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 插件管理器
 * Created by Administrator
 */
public class PluginManager {

    private final static String TAG = "PluginManager";
    private static Context mHostContext;
    private static volatile PluginManager mPluginManager;
    /*** 插件下载器*/
    private static IDownload mIDownload = new DefaultPluginDownload();
    /**插件默认位置和插件解压路径*/
    private File mPluginPath, mPluginOutputPath;
    /**插件集合*/
    private ConcurrentHashMap<String , PluginInfo> mPlugins = new ConcurrentHashMap<>();
    /**是否已经加载过插件*/
    private static boolean isPluginLoaded = false;
    private PluginInstrumentation pluginInstrumentation;

    private PluginManager(Context hostContext) throws Exception{
        mPluginPath = hostContext.getDir(Constants.PLUGIN_SAVE_PATH_DIR, Context.MODE_PRIVATE);
        mPluginOutputPath = hostContext.getDir(Constants.PLUGIN_DEX_OUTPUT_PATH_DIR, Context.MODE_PRIVATE);
        pluginInstrumentation = new PluginInstrumentation(mHostContext, Utils.getOriginalInstrumentation());
        Utils.hookInstrumentation(pluginInstrumentation);
    }

    /**
     * 从指定位置加载插件
     * @param pluginPath 插件路径 可以是一个目录也可以是一个文件路径
     */
    public void loadPlugin(String pluginPath) throws Exception{
        mPlugins = LoadPluginImpl.getInstance().loadPlugin(pluginPath);
    }

    /**
     * 加载默认位置插件
     */
    public void loadPlugin() throws Exception{
        loadPlugin(mPluginPath.getAbsolutePath());
    }

    /**
     * 真实加载插件方法
     * @param pluginPath 插件路径
     */
    public ConcurrentHashMap<String , PluginInfo> realLoad(String pluginPath){
        File tempFile = new File(pluginPath);
        if(!tempFile.exists()){
            throw new RuntimeException("Plugin path not found");
        }
        if(tempFile.isDirectory()){
           File[] pluginFiles =  tempFile.listFiles(new FileFilter() {
               @Override
               public boolean accept(File pathname) {
                   if (!pathname.isDirectory()) {
                       String fileName = pathname.getName();
                       String suffix = "";
                       if (fileName.contains(".")) {
                           suffix = fileName.substring(fileName.lastIndexOf("."));
                       }
                       return suffix.equals(".dex") || suffix.equals(".apk");
                   }
                   return false;
               }
           });
            for (File temp : pluginFiles) {
                resolvePlugin(temp.getAbsolutePath());
            }
            isPluginLoaded = true;
            return mPlugins;
        }else{
            isPluginLoaded = true;
            return resolvePlugin(pluginPath);
        }
    }

    /**
     * 解析插件
     * @param pluginFilePath 插件文件路径
     */
    private ConcurrentHashMap<String , PluginInfo> resolvePlugin(String pluginFilePath){
        PluginInfo info = new PluginInfo();
        info.setHostContext(mHostContext);
        HashMap<PackageInfo, PackageParser.Package> packageInfoS = Utils.getPackageArchiveInfo(pluginFilePath,
                PackageManager.GET_ACTIVITIES | PackageManager.GET_SERVICES | PackageManager.GET_RECEIVERS | PackageManager.GET_PROVIDERS);
        /*PackageInfo packageInfo = mHostContext.getPackageManager().getPackageArchiveInfo(pluginFilePath,
                PackageManager.GET_ACTIVITIES | PackageManager.GET_SERVICES | PackageManager.GET_RECEIVERS | PackageManager.GET_PROVIDERS);*/
        if(packageInfoS != null){
            Set<PackageInfo> keys = packageInfoS.keySet();
            PackageInfo packageInfo = null;
            PackageParser.Package pkg = null;
            for (PackageInfo temp : keys) {
                packageInfo = temp;
                pkg = packageInfoS.get(temp);
            }
            if(packageInfo != null && pkg != null){
                PluginInfo plugin = mPlugins.get(packageInfo.packageName);
                if(plugin != null){
                    return null;
                }
                info.setPackageInfo(packageInfo, pkg);
                PluginDexClassLoader classLoader = new PluginDexClassLoader(pluginFilePath, mPluginOutputPath.getAbsolutePath(), packageInfo.activities[0].applicationInfo.nativeLibraryDir, mHostContext.getClassLoader(), info);
                info.setClassLoader(classLoader);
                generateAssetAndResources(pluginFilePath, info);
                PluginContext pluginContext = new PluginContext(info);
                info.setPluginContext(pluginContext);
                Application application = createPluginApplication(info);
                info.setApplication(application);
                mPlugins.put(packageInfo.packageName, info);
                return mPlugins;
            }
        }else{
            throw new RuntimeException("resolve plugin package fail");
        }
       return null;
    }

    /**
     * 创建插件Application环境
     * @param pluginInfo 插件信息
     */
    private Application createPluginApplication(PluginInfo pluginInfo){
        String appClazz = pluginInfo.getPackageInfo().applicationInfo.className;
        if(TextUtils.isEmpty(appClazz)){
            appClazz = "android.app.Application";
        }
        try {
            return pluginInstrumentation.newApplication(pluginInfo.getClassLoader(), appClazz, pluginInfo.getPluginContext());
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 生成插件资源resources
     * @param resDir 插件地址
     */
    private void generateAssetAndResources(String resDir, PluginInfo pluginInfo){
        try {
            AssetManager assetManager = AssetManager.class.newInstance();
            ReflectUtils.invokeMethod(assetManager.getClass(), assetManager, "addAssetPath", new Object[]{resDir}, String.class);
            Resources res = mHostContext.getResources();
            Resources pluginRes = new Resources(assetManager, res.getDisplayMetrics(), res.getConfiguration());
            pluginInfo.setAssetManager(assetManager);
            pluginInfo.setResources(pluginRes);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 获取解析到的插件集合
     * @return 插件集合
     */
    public ConcurrentHashMap<String , PluginInfo> getPlugins(){
        return mPlugins;
    }


    /**
     * get pluginManager
     * @return PluginManager
     */
    public static PluginManager getPluginManager(){
        checkInit();
        return mPluginManager;
    }

    /**
     * 检查是否已经加载插件
     */
    private void checkPluginLoaded(){
        if(!isPluginLoaded){
            throw new RuntimeException("Please transfer PluginManager.loadPlugin() or PluginManager.loadPlugin(pluginPath) first");
        }
    }
    /**
     * 把插件中目标act中的相关配置信息填充到占位act中
     * @param intent 启动intent
     */
    private void inflatePluginParamsToVirtual(WrapperIntent intent){
        checkPluginLoaded();
        if (mPlugins != null && mPlugins.size() > 0) {
            String targetPackage = intent.getPkgName();
            String className = intent.getQualifiedClassName();
            if (!TextUtils.isEmpty(targetPackage)) {
                PluginInfo pluginInfo = mPlugins.get(targetPackage);
                if(pluginInfo != null){
                    ConcurrentHashMap<String, ActivityInfo> tempActInfoS = pluginInfo.acts;
                    if(tempActInfoS != null && tempActInfoS.size() > 0){
                        ActivityInfo tempActInfo = tempActInfoS.get(className);
                        int launchMode = tempActInfo.launchMode;
                        intent.putExtra(Constants.PLUGIN_ACT_FLAG, tempActInfo);
                        switch (launchMode){
                            case 0:
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                break;
                            case 1:
                                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                break;
                            case 2:
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                break;
                            case 3:
                                break;
                        }
                    }
                }else{
                    throw new RuntimeException("Target packageName not found");
                }
            }
        }
    }

    /**
     * 启动activity
     * @param intent 包装过的intent
     */
    public void startActivity(Context context, WrapperIntent intent){
        inflatePluginParamsToVirtual(intent);
        intent.setClass(context, VirtualActivity.class);
        context.startActivity(intent);
    }

    /**
     * 启动插件中默认activity
     *  <intent-filter>
            <action android:name="android.intent.action.MAIN"/>
            <category android:name="android.intent.category.LAUNCHER"/>
        </intent-filter>
     */
    public void startDefaultActivity(Context context){
        if(mPlugins != null && mPlugins.size() == 1){
            Set<String> keys = mPlugins.keySet();
            for (String pkgName : keys) {
                startDefaultActivity(context, pkgName);
            }
        }else{
            throw new RuntimeException("Start defaultActivity fail");
        }
    }

    /**
     * 启动指定包名插件下面的默认activity
     * @param pkgName 包名
     */
    public void startDefaultActivity(Context context, String pkgName){
        if(mPlugins != null && mPlugins.size() > 0){
            PluginInfo info = mPlugins.get(pkgName);
            String mainActivity = info.getMainActivity();
            if(!TextUtils.isEmpty(mainActivity)){
                WrapperIntent intent = new WrapperIntent(pkgName, mainActivity);
                startActivity(context, intent);
            }else{
                throw new RuntimeException("Start defaultActivity fail, defaultActivity not found");
            }
        }else{
            throw new RuntimeException("Start defaultActivity fail");
        }
    }

    /**
     * 通过包名和类全限定名来加载插件中的fragment
     * @param pkgName 插件包名
     * @param qualifiedClassName fragment全限定名
     * @return Fragment
     */
    public Fragment loadPluginFragment(String pkgName, String qualifiedClassName){
        return loadPluginFragment(pkgName, qualifiedClassName, null);
    }

    /**
     * 通过包名和类全限定名来加载插件中的fragment
     * @param pkgName 插件包名
     * @param qualifiedClassName fragment全限定名
     * @param args 参数
     * @return Fragment
     */
    public Fragment loadPluginFragment(String pkgName, String qualifiedClassName, Bundle args) {
        checkPluginLoaded();
        if(mPlugins != null && mPlugins.size() > 0){
            PluginInfo info = mPlugins.get(pkgName);
            preparePluginEnv();
            return Fragment.instantiate(info.getPluginContext(), qualifiedClassName, args);
        }
        return null;
    }

    /**
     * 准备插件fragment运行时上下文环境
     */
    private void preparePluginEnv(){
        //// TODO: prepare plugin fragment env
    }

    /**
     * check pluginManger init
     */
    private static void checkInit(){
        if(mPluginManager == null){
            throw new PluginManagerNotInitException("PluginManager not init");
        }
    }

    /**
     * init plugin
     * @param context 上下文对象
     */
    public static void init(Context context) throws Exception{
        mHostContext = context.getApplicationContext();
        if(mPluginManager == null){
            synchronized (PluginManager.class){
                if (mPluginManager == null){
                    mPluginManager = new PluginManager(context.getApplicationContext());
                }
            }
        }
    }

    /**
     * 设置插件下载引擎
     * @param downloadEngine 下载引擎
     */
    public static void setPluginDownloadEngine(IDownload downloadEngine){
        mIDownload = downloadEngine;
    }

    /**
     * init plugin with special uri
     * @param context 上下文对象
     * @param pluginUri 插件地址
     */
    public static void init(Context context, String... pluginUri) throws Exception{
        init(context);
    }
}
