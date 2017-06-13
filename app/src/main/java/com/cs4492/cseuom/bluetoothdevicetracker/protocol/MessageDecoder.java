package com.cs4492.cseuom.bluetoothdevicetracker.protocol;

/**
 * Created by Heshan Sandamal on 6/8/2017.
 */

public class MessageDecoder {

    public static void decodeMessage(String message){
        String message1="REG%device%Mac" ;


        String[] parts = message1.split("%");
        String reg = parts[0];
        String devicename = parts[1];
        String mac = parts[2];

        if(reg.equals("REG")){
            if(RegisterDevice(devicename,mac)){
                encodeMessage("REG-ACK",devicename,mac,0); //0 for successfully registered ;
            }else{
                encodeMessage("REG-ACK",devicename,mac,1); //1 for failed to register ;
            }
        }
        if(reg.equals("UNREG")) {
            if (unRegisterDevice(devicename, mac)) {
                encodeMessage("UNREG-ACK", devicename, mac, 0); //0 for successfully unregistered ;
            } else{
            encodeMessage("UNREG-ACK", devicename, mac, 1); //1 for error in unregistering ;
        }

        }
        if(reg.equals("PING")) {

        }



    }

    private static boolean unRegisterDevice(String devicename, String mac) {
        return true;
    }

    private static void encodeMessage(String ack, String devicename, String mac, int i) {
    }

    private static boolean RegisterDevice(String devicename, String mac) {
        return true ;
    }

}
