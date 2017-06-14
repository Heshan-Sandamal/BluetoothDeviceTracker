package com.cs4492.cseuom.bluetoothdevicetracker.protocol;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.util.Log;

import com.cs4492.cseuom.bluetoothdevicetracker.socket_connection.ConnectedSockets;
import com.cs4492.cseuom.bluetoothdevicetracker.socket_connection.MyBluetoothService;

import java.util.List;

/**
 * Created by Heshan Sandamal on 6/8/2017.
 */

public class MessageDecoder {

    public static String decodeMessage(String message) {
        String message1 = "REG%device%Mac";

        Log.d("reply","---"+message+"----");
        String[] parts = message.split("%");
        String reg = parts[0];

        String devicename="";
        String mac="";
        if(parts.length>1){
            devicename=parts[1];
            mac = parts[2];
        }



        if (reg.equals("REG")) {
            RegisterDevice(devicename, mac);
            return encodeMessage("REG-ACK",devicename,mac); //0 for successfully registered ;

        } else if (reg.equals("REG-ACK")) {
            MessageDecoder.handleRegisterACK();
            return null;
        }

        if (reg.equals("UNREG")) {
            unRegisterDevice(devicename, mac);
            return encodeMessage("UNREG-ACK",devicename,mac); //0 for successfully unregistered

        } else if (reg.equals("UNREG-ACK")) {
            handleUnRegisterACK(devicename,mac);
            return null;
        }

        if (reg.equals("PING")) {
            return encodeMessage("PING-ACK",devicename,mac);
        }
        return null;

    }

    private static void unRegisterDevice(String devicename, String mac) {
        ConnectedSockets.removeConnectedThreads(mac);
    }

    private static String encodeMessage(String ack,String deviceName,String mac) {
        String unRegMessage=ack+"%"+deviceName+"%"+BluetoothAdapter.getDefaultAdapter().getAddress();
        return unRegMessage;
    }

    private static void RegisterDevice(String devicename, String mac) {
        AppMessageConstants.isTracking = true;
    }

    private static boolean handleRegisterACK() {
        AppMessageConstants.isTracking = true;
        return true;
    }

    private static boolean handleUnRegisterACK(String devicename, String mac) {

        //ConnectedSockets.clearConnectedThreadsList();
        ConnectedSockets.removeConnectedThreads(mac);
        return true;
    }

    public static String getWriteMessageForUI(String message){
        String[] parts = message.split("%");
        String reg = parts[0];

        String devicename="";
        String mac="";
        if(parts.length>1){
            devicename=parts[1];
            mac = parts[2];
        }

        return reg+" to the "+ devicename;
    }


    public static String getReadMessageForUI(String message){
        String[] parts = message.split("%");
        String reg = parts[0];

        String devicename="";
        String mac="";
        if(parts.length>1){
            devicename=parts[1];
            mac = parts[2];
        }

        return reg+" from "+ devicename;
    }

}
