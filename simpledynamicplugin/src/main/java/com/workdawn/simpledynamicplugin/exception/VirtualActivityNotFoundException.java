package com.workdawn.simpledynamicplugin.exception;

/**
 * 占位activity未找到异常
 * Created by Administrator
 */
public class VirtualActivityNotFoundException extends RuntimeException {

    public VirtualActivityNotFoundException(String detailMessage) {
        super(detailMessage);
    }
}
