package com.cs4492.cseuom.bluetoothdevicetracker.socket_connection;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

import static android.content.ContentValues.TAG;

/**
 * Created by Heshan Sandamal on 5/20/2017.
 */

public class AcceptThread extends Thread {
    private static final String NAME = "heshan";
    private static final UUID MY_UUID = new UUID(8333, 3242);
    private final BluetoothServerSocket mmServerSocket;
    private Handler handler;

    public AcceptThread(BluetoothAdapter mBluetoothAdapter, Handler handler) throws IOException {
        // Use a temporary object that is later assigned to mmServerSocket
        // because mmServerSocket is final.
        BluetoothServerSocket tmp = null;
        try {
            // MY_UUID is the app's UUID string, also used by the client code.
            tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
            Log.e("UUID",MY_UUID.toString());
        } catch (IOException e) {
            Log.e(TAG, "Socket's listen() method failed", e);
        }
        mmServerSocket = tmp;
        this.handler = handler;
    }

    public void run() {
        BluetoothSocket socket = null;
        // Keep listening until exception occurs or a socket is returned.
        while (true) {
            try {
                socket = mmServerSocket.accept();
            } catch (IOException e) {
                Log.e(TAG, "Socket's accept() method failed", e);
                break;
            }

            if (socket != null) {
                // A connection was accepted. Perform work associated with
                // the connection in a separate thread.
                try {
                    manageMyConnectedSocket(socket,this.handler);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    mmServerSocket.close();
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage());
                }
                break;
            }
        }
    }

    private void manageMyConnectedSocket(BluetoothSocket socket, Handler handler) throws IOException {
        //new MyBluetoothService(socket);
        MyBluetoothService bds = new MyBluetoothService(handler,0);
        MyBluetoothService.ConnectedThread ct = bds.new ConnectedThread(socket);
        ct.run();
    }

    // Closes the connect socket and causes the thread to finish.
    public void cancel() {
        try {
            mmServerSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "Could not close the connect socket", e);
        }
    }
}
