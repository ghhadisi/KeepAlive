package com.dbgs.keepalive.java2process;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2017/9/8.
 */

public class LocalService extends Service {
    private static final String TAG = "LocalService";
    private MyBinder myBinder;
    MyServiceConnection connection;
    private PendingIntent pintent;
    private int counter = 0;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (myBinder == null) {
            myBinder = new MyBinder();
        }
        connection = new MyServiceConnection();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.bindService(new Intent(this, RemoteService.class), connection, Context.BIND_IMPORTANT);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
//        notification(mBuilder,new Intent(this, MainActivity.class));
        startTimer();

        NotificationManager mNotificationManger = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManger.notify(startId, mBuilder.build());
        startForeground(startId, mBuilder.build());

        return START_STICKY;
    }

    class MyBinder extends IMyAidlInterface.Stub{

        @Override
        public String getProcessName() throws RemoteException {
            return TAG;
        }
    }

    class MyServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e(TAG,"LocalService 服务连接成功");

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Toast.makeText(LocalService.this, "LocalServcie has stopped", Toast.LENGTH_SHORT).show();
            LocalService.this.startService(new Intent(LocalService.this, RemoteService.class));
            LocalService.this.bindService(new Intent(LocalService.this, RemoteService.class), connection, Context.BIND_IMPORTANT);
        }
    }

    public void notification(NotificationCompat.Builder mBuilder, Intent intent) {
        mBuilder.setDefaults(NotificationCompat.DEFAULT_SOUND);
        //设置震动方式，延迟零秒，震动一秒，延迟一秒、震动一秒
        mBuilder.setContentTitle("服务启动中");
        mBuilder.setSmallIcon(R.drawable.ic_launcher);
        mBuilder.setWhen(System.currentTimeMillis());
        mBuilder.setContentInfo("Info");
        mBuilder.setTicker("服务需要启动");
        pintent= PendingIntent.getActivity(this, 0, intent, 0);
        mBuilder.setContentIntent(pintent);
    }
    Timer timer;
    TimerTask timerTask;
    long oldTime = 0;

    public void startTimer() {
        timer = new Timer();
        initializeTimerTask();
        timer.schedule(timerTask, 1000, 1000);
    }
    void initializeTimerTask(){
        timerTask = new TimerTask() {
            @Override
            public void run() {
                Log.d(TAG, "count: " + counter++);
            }
        };
    }
}
