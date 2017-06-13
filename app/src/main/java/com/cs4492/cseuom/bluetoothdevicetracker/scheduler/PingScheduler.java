package com.cs4492.cseuom.bluetoothdevicetracker.scheduler;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.util.Timer;

public class PingScheduler extends Service {
    private Timer time;

    public PingScheduler() {

    }

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        
        Log.d("starting stss","tasks");
        Log.d("starting st","task");
        time = new Timer(); // Instantiate Timer Object

        ScheduledTask st = new ScheduledTask(); // Instantiate SheduledTask class
        time.schedule(st, 0, 2000); // Create Repetitively task for everyt1 secs
    }

    @Override
    public void onDestroy() {
        this.time.cancel();
    }
}
