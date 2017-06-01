package com.cs4492.cseuom.bluetoothdevicetracker.scheduler;

import android.util.Log;

import com.cs4492.cseuom.bluetoothdevicetracker.socket_connection.ConnectedSockets;
import com.cs4492.cseuom.bluetoothdevicetracker.socket_connection.MyBluetoothService;

import java.util.Date;
import java.util.List;
import java.util.TimerTask;

/**
 * Created by Heshan Sandamal on 5/30/2017.
 */

public class ScheduledTask extends TimerTask {
    @Override
    public void run() {

        List<MyBluetoothService.ConnectedThread> socketObjectsList = ConnectedSockets.getSocketObjectsList();

        Log.d("scheduler is running",new Date().toString());
        for (MyBluetoothService.ConnectedThread ob:socketObjectsList){
            ob.write("sending message".getBytes());
        }
    }
}
