package com.cs4492.cseuom.bluetoothdevicetracker.scheduler;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.util.Timer;

public class PingScheduler extends Service {
    public PingScheduler() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        Timer time = new Timer(); // Instantiate Timer Object
        ScheduledTask st = new ScheduledTask(); // Instantiate SheduledTask class
        time.schedule(st, 0, 1000); // Create Repetitively task for every 1 secs
        return null;
    }
}
