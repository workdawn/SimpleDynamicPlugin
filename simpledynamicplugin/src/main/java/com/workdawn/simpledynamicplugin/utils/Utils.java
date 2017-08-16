package com.workdawn.simpledynamicplugin.utils;

import android.app.Instrumentation;
import android.content.pm.PackageInfo;
import android.content.pm.PackageParser;
import android.content.pm.PackageUserState;
import android.os.Build;

import com.workdawn.simpledynamicplugin.Constants;

import java.io.File;
import java.util.HashMap;

/**
 * 工具类
 * Created by Administrator
 */
public class Utils {

    /**
     * hook 应用Instrumentation
     * @param instrumentation 替换的instrumentation
     * @throws Exception
     */
    public static void hookInstrumentation(Instrumentation instrumentation) throws Exception{
        execSetOrGetValue(instrumentation, 0, "android.app.ActivityThread", "currentActivityThread", "mInstrumentation");
    }

    /**
     * 获取或者设置字段值
     * @param o 目标对象
     * @param type 类型
     * @param className 类名
     * @param methodName 方法名
     * @param propertyName 属性名
     * @throws Exception
     */
    public static Object execSetOrGetValue(Object o, int type, String className, String methodName, String propertyName) throws Exception{
        Class<?> cls = Class.forName(className);
        Object target = ReflectUtils.invokeMethod(cls, methodName);
        if(type == 0){
            ReflectUtils.setFieldValue(cls, target, propertyName, o);
            return null;
        }else{
            return ReflectUtils.getFieldValue(cls, target, propertyName);
        }
    }

    /**
     * 获得原始Instrumentation
     * @throws Exception
     */
    public static Instrumentation getOriginalInstrumentation() throws Exception{
        return (Instrumentation)execSetOrGetValue(null, 1, "android.app.ActivityThread", "currentActivityThread", "mInstrumentation");
    }

    /**
     * copy form system Resources.java
     */
    public static int selectDefaultTheme(int curTheme, int targetSdkVersion) {
        return selectSystemTheme(curTheme, targetSdkVersion,
                android.R.style.Theme,
                android.R.style.Theme_Holo,
                android.R.style.Theme_DeviceDefault,
                android.R.style.Theme_DeviceDefault_Light_DarkActionBar);
    }

    /**
     * copy form system Resources.java
     */
    public static int selectSystemTheme(int curTheme, int targetSdkVersion, int orig, int holo,
                                        int dark, int deviceDefault) {
        if (curTheme != 0) {
            return curTheme;
        }
        if (targetSdkVersion < Build.VERSION_CODES.HONEYCOMB) {
            return orig;
        }
        if (targetSdkVersion < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            return holo;
        }
        if (targetSdkVersion < Build.VERSION_CODES.CUR_DEVELOPMENT) {
            return dark;
        }
        return deviceDefault;
    }

    /**
     * copy from system class PackageParser.java
     * @param archiveFilePath 包路径
     * @param flags 获取的类型标识
     */
    public static HashMap<PackageInfo, PackageParser.Package> getPackageArchiveInfo(String archiveFilePath, int flags) {
        HashMap<PackageInfo, PackageParser.Package> result = new HashMap<>();
        final PackageParser parser = new PackageParser();
        final File apkFile = new File(archiveFilePath);
        try {
            PackageParser.Package pkg = parser.parseMonolithicPackage(apkFile, 0);
            if ((flags & Constants.GET_SIGNATURES) != 0) {
                parser.collectCertificates(pkg, 0);
                parser.collectManifestDigest(pkg);
            }
            PackageUserState state = new PackageUserState();
            PackageInfo packageInfo = PackageParser.generatePackageInfo(pkg, null, flags, 0, 0, null, state);
            result.put(packageInfo, pkg);
            return result;
        } catch (PackageParser.PackageParserException e) {
            return null;
        }

    }


}
