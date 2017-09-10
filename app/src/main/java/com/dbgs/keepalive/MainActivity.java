package com.dbgs.keepalive;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.dbgs.keepalive.java2process.JobHandleService;
import com.dbgs.keepalive.java2process.LocalService;
import com.dbgs.keepalive.java2process.RemoteService;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        startService(new Intent(MainActivity.this,ProcessService.class));
//        this.startService(new Intent(this, LocalService.class));
//        this.startService(new Intent(this, RemoteService.class));
        this.startService(new Intent(this, JobHandleService.class));
    }
}

