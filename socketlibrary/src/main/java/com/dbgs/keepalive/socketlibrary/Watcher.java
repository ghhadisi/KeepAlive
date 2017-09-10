package com.dbgs.keepalive.socketlibrary;

/**
 * Created by Administrator on 2017/9/10.
 */

public class Watcher {
    static {
        System.loadLibrary("socket_keeplive-lib");
    }
    public native void createWatcher(String  userId);
    public native void connectToMonitor();
}
