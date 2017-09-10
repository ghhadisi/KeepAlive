package com.dbgs.keepalive.java2process;

import android.app.ActivityManager;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

/**
 * Created by Administrator on 2017/9/8.
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class JobHandleService extends JobService {
    private static final String TAG = "JobHandleService";
    private static int kJobId = 0x0001;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "jobService create");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "JobHandleService start");
        scheduleJob(getJobInfo());
        return START_NOT_STICKY;
    }
    public void scheduleJob(JobInfo job) {
        Log.d(TAG, "scheduleJob");
        JobScheduler js = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        js.schedule(job);
    }

    public JobInfo getJobInfo() {
        JobInfo.Builder builder = new JobInfo.Builder(kJobId, new ComponentName(
                this, JobHandleService.class));
        builder.setPersisted(true);
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        builder.setRequiresCharging(false);
        builder.setPeriodic(100);
        return builder.build();
    }


    @Override
    public boolean onStartJob(JobParameters params) {
        Log.e(TAG, "onStartJob");
        boolean isLocalServiceWork = isServiceWork(this,"com.dbgs.keepalive.java2process.LocalService");
        boolean isRemoteServiceWork = isServiceWork(this, "com.dbgs.keepalive.java2process.RemoteService");
        if (!isLocalServiceWork ||
                !isRemoteServiceWork) {
            this.startService(new Intent(this, LocalService.class));
            this.startService(new Intent(this, RemoteService.class));
            Toast.makeText(this, "process started by jobHandleService", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.e(TAG, "onStopJob");
        scheduleJob(getJobInfo());
        return true;
    }
    public boolean isServiceWork(Context context, String serviceName) {
        boolean isWorked = false;
        ActivityManager manager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> list = manager.getRunningServices(128);
        if (list.size() < 0) {
            return false;
        }

        for (int i = 0; i < list.size(); i++) {
            String name = list.get(i).service.getClassName().toString();
            if (serviceName.equals(name)) {
                isWorked = true;
                break;
            }
        }
        return isWorked;
    }
}
