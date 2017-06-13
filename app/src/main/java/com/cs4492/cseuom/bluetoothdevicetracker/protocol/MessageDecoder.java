package com.cs4492.cseuom.bluetoothdevicetracker.protocol;

import android.util.Log;

import com.cs4492.cseuom.bluetoothdevicetracker.socket_connection.ConnectedSockets;

/**
 * Created by Heshan Sandamal on 6/8/2017.
 */

public class MessageDecoder {

    public static String decodeMessage(String message) {
        String message1 = "REG%device%Mac";

        Log.d("reply","---"+message+"----");
        String[] parts = message.split("%");
        String reg = parts[0];
        String devicename = parts[1];
        String mac = parts[2];

        if (reg.equals("REG")) {
            RegisterDevice(devicename, mac);
            return encodeMessage("REG-ACK", devicename, mac, 0); //0 for successfully registered ;

        } else if (reg.equals("REG-ACK")) {
            MessageDecoder.handleRegisterACK();
            return null;
        }

        if (reg.equals("UNREG")) {
            unRegisterDevice(devicename, mac);
            return encodeMessage("UNREG-ACK", devicename, mac, 0); //0 for successfully unregistered

        } else if (reg.equals("UNREG-ACK")) {
            MessageDecoder.handleUnRegisterACK();
            return null;
        }

        if (reg.equals("PING")) {
            return "PING-ACK";
        }
        return null;

    }

    private static void unRegisterDevice(String devicename, String mac) {
        AppMessageConstants.isTracking = false;
    }

    private static String encodeMessage(String ack, String devicename, String mac, int i) {
        return ack;
    }

    private static void RegisterDevice(String devicename, String mac) {
        AppMessageConstants.isTracking = true;
    }

    private static boolean handleRegisterACK() {
        AppMessageConstants.isTracking = true;
        return true;
    }

    private static boolean handleUnRegisterACK() {
        AppMessageConstants.isTracking = false;
        ConnectedSockets.clearConnectedThreadsList();
        return true;
    }

}
