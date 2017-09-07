package com.dbgs.keeplive.pollinglibrary;

/**
 * Created by Administrator on 2017/9/7.
 */

public class Polling {
    static {
        System.loadLibrary("polling_keeplive-lib");
    }
    public native void createWatcher(int userId, String serviceName);

}
