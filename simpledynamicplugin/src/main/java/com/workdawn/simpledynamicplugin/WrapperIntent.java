package com.workdawn.simpledynamicplugin;

import android.content.Intent;

/**
 * 系统intent包装
 * Created by Administrator
 */
public class WrapperIntent extends Intent {
    /**插件目标的类名 如：MainActivity*/
    private String mQualifiedClassName;
    private String mPkgName;

    public WrapperIntent(String pkgName, String qualifiedClassName){
        mQualifiedClassName = qualifiedClassName;
        mPkgName = pkgName;
    }

    public String getQualifiedClassName() {
        return mQualifiedClassName;
    }

    public void setQualifiedClassName(String mQualifiedClassName) {
        this.mQualifiedClassName = mQualifiedClassName;
    }

    public String getPkgName() {
        return mPkgName;
    }

    public void setPkgName(String mPkgName) {
        this.mPkgName = mPkgName;
    }
}
