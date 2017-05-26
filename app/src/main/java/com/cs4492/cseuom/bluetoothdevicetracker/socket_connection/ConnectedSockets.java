package com.cs4492.cseuom.bluetoothdevicetracker.socket_connection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Heshan Sandamal on 5/26/2017.
 */

public class ConnectedSockets {

    private static List<MyBluetoothService.ConnectedThread> connectedThreads=new ArrayList<>();


    public static void addToList(MyBluetoothService.ConnectedThread ct){
        connectedThreads.add(ct);
    }

    public static List<MyBluetoothService.ConnectedThread> getSocketObjectsList(){
        return connectedThreads;
    }

}
