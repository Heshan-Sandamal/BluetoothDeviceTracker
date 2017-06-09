package com.cs4492.cseuom.bluetoothdevicetracker.socket_connection;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.cs4492.cseuom.bluetoothdevicetracker.protocol.AppMessageConstants;

import java.io.IOException;
import java.util.UUID;

import static android.content.ContentValues.TAG;

/**
 * Created by Heshan Sandamal on 5/20/2017.
 */

public class ConnectThread extends Thread {
    private static UUID MY_UUID;
    private final BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;
    private final Context applicationContext;
    private Handler handler;

    public ConnectThread(BluetoothDevice device, Handler handler, Context applicationContext) {
        // Use a temporary object that is later assigned to mmSocket
        // because mmSocket is final.
        BluetoothSocket tmp = null;
        mmDevice = device;
        this.applicationContext=applicationContext;
//        this.MY_UUID=device.getUuids()[device.getUuids().length-1].getUuid();
        this.MY_UUID=new UUID(8333, 3242);

        try {
            // Get a BluetoothSocket to connect with the given BluetoothDevice.
            // MY_UUID is the app's UUID string, also used in the server code.
            tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {
            Log.e(TAG, "Socket's create() method failed", e);
        }
        mmSocket = tmp;
        this.handler=handler;
    }

    public void run() {
        // Cancel discovery because it otherwise slows down the connection.
        //mBluetoothAdapter.cancelDiscovery();

        try {
            // Connect to the remote device through the socket. This call blocks
            // until it succeeds or throws an exception.
            mmSocket.connect();
        } catch (IOException connectException) {
            Log.e(TAG, "Could not connect to the client socket", connectException);
            try {
                mmSocket.close();
            } catch (IOException closeException) {
                Log.e(TAG, "Could not close the client socket", closeException);
            }
            return;
        }

        // The connection attempt succeeded. Perform work associated with
        // the connection in a separate thread.
        try {
            manageMyConnectedSocket(mmSocket,this.handler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void manageMyConnectedSocket(BluetoothSocket mmSocket, Handler handler) throws IOException {
        MyBluetoothService bds=new MyBluetoothService(handler, this.applicationContext, 1);
        MyBluetoothService.ConnectedThread ct= bds.new ConnectedThread(mmSocket);
        ct.start();
        ct.write("heshan".getBytes());
        Log.d("writing message","wrinting meeeeeeeeeeeeee");

        AppMessageConstants.hostType= AppMessageConstants.CONNECTED_CLIENT;
        ConnectedSockets.addToConnectedThreadsList(ct);

    }

    // Closes the client socket and causes the thread to finish.
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "Could not close the client socket", e);
        }
    }
}
