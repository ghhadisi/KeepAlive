package com.dbgs.keepalive;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Process;
import android.support.annotation.Nullable;
import android.util.Log;

import com.dbgs.keeplive.pollinglibrary.Polling;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by david on 2017/9/6.
 */

public class ProcessService extends Service {

    private static final String TAG = "tuch";
    int i=0;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        Wathcer wathcer=new Wathcer();
//        wathcer.createWatcher(Process.myUid());
        Polling polling = new Polling();
        // "com.dongnao.signalprocess/com.dongnao.signalprocess.ProcessService"
        polling.createWatcher(Process.myPid(),"com.dbgs.keepalive/com.dbgs.keepalive.ProcessService");
        Timer timer = new Timer();
        //定时器
        timer.scheduleAtFixedRate(
                new TimerTask() {
                    public void run() {
                        Log.i(TAG, "服务  "+i);
                        i++;
                    }
                }, 0, 1000 * 3);
    }
}
