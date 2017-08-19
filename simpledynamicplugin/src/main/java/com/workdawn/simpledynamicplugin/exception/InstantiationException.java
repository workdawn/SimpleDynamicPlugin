package com.workdawn.simpledynamicplugin.exception;

import android.util.AndroidRuntimeException;

/**
 * 实例化异常
 * Created by Administrator
 */
public class InstantiationException extends AndroidRuntimeException {
    public InstantiationException(String msg, Exception cause) {
        super(msg, cause);
    }
}
