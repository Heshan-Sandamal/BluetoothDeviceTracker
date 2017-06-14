package com.cs4492.cseuom.bluetoothdevicetracker.socket_connection;

import android.bluetooth.BluetoothDevice;

import com.cs4492.cseuom.bluetoothdevicetracker.protocol.AppMessageConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Heshan Sandamal on 5/26/2017.
 */

public class ConnectedSockets {

    private static List<MyBluetoothService.ConnectedThread> connectedThreads=new ArrayList<>();
    private static List<BluetoothDevice> connectedDeviceList=new ArrayList<>();


    public static void addToConnectedThreadsList(MyBluetoothService.ConnectedThread ct){
        connectedThreads.add(ct);
    }

    public static List<MyBluetoothService.ConnectedThread> getSocketObjectsList(){
        return connectedThreads;
    }

    public static void clearConnectedThreadsList(){
        connectedThreads.clear();
    }

    public static void addToConnectedDevicesList(BluetoothDevice device){
        connectedDeviceList.add(device);
    }

    public static List<BluetoothDevice> getConnectedDeviceList() {
        return connectedDeviceList;
    }

    public static void removeConnectedThreads(String mac){
        List<MyBluetoothService.ConnectedThread> socketObjectsList = ConnectedSockets.getSocketObjectsList();

        for(int i=0;i<socketObjectsList.size();i++){
            MyBluetoothService.ConnectedThread remoteDevice = socketObjectsList.get(i);
            if(remoteDevice.getMmSocket().getRemoteDevice().getAddress().equals(mac)){
                socketObjectsList.remove(remoteDevice);
            }
        }

        if(socketObjectsList.size()==0){
            AppMessageConstants.isTracking = false;
        }
    }
}
