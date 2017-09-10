package com.dbgs.keepalive.java2process;

import android.app.NotificationManager;
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

/**
 * Created by Administrator on 2017/9/8.
 */

public class RemoteService extends Service {
    private static final String TAG = "RemoteService";
    RemoteBinder binder;
    MyRemoteServiceConnection  connection;
    @Override
    public void onCreate() {
        super.onCreate();
        if (binder == null) {
            binder = new RemoteBinder();
        }
        connection = new MyRemoteServiceConnection();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.bindService(new Intent(this, LocalService.class),connection, Context.BIND_IMPORTANT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
//        notification(mBuilder, new Intent(this, MainActivity.class));
        NotificationManager mNotificationManger = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManger.notify(startId, mBuilder.build());

        startForeground(startId, mBuilder.build());
        return START_STICKY;

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
    class  RemoteBinder extends IMyAidlInterface.Stub{

        @Override
        public String getProcessName() throws RemoteException {
            return TAG;
        }
    }

    class MyRemoteServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e(TAG, "RemoteService 服务连接成功");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            //LocalService 被干掉了
            Toast.makeText(RemoteService.this, "RemoteService 服务连接被干掉", Toast.LENGTH_SHORT).show();
            RemoteService.this.startService(new Intent(RemoteService.this, LocalService.class));
            RemoteService.this.bindService(new Intent(RemoteService.this, LocalService.class), connection, Context.BIND_IMPORTANT);
        }
    }
}
