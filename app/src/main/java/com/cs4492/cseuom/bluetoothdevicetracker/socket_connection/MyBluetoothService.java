package com.cs4492.cseuom.bluetoothdevicetracker.socket_connection;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.cs4492.cseuom.bluetoothdevicetracker.protocol.AppMessageConstants;
import com.cs4492.cseuom.bluetoothdevicetracker.protocol.MessageDecoder;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

/**
 * Created by Heshan Sandamal on 5/20/2017.
 */

public class MyBluetoothService {
    private static final String TAG = "MY_APP_DEBUG_TAG";
    private final Context applicationContext;
    private Handler mHandler; // handler that gets info from Bluetooth service
    private int type;       //0-server,1-client


    public MyBluetoothService(Handler mHandler, Context applicationContext, int type) throws IOException {
        this.mHandler=mHandler;
        this.type=type;
        this.applicationContext=applicationContext;
    }

    // Defines several constants used when transmitting messages between the
    // service and the UI.
    private interface MessageConstants {
        public static final int MESSAGE_READ = 0;
        public static final int MESSAGE_WRITE = 1;
        public static final int MESSAGE_TOAST = 2;

        // ... (Add other message types here as needed.)
    }

    public class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private byte[] mmBuffer; // mmBuffer store for the stream

        public ConnectedThread(BluetoothSocket socket) throws IOException {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams; using temp objects because
            // member streams are final.
            try {
                tmpIn = socket.getInputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating input stream", e);
            }
            try {
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating output stream", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            mmBuffer = new byte[1024];
            int numBytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs.
            while (true) {
                try {
                    // Read from the InputStream.
                    numBytes = mmInStream.read(mmBuffer);
                    // Send the obtained bytes to the UI activity.

                    String readMessage = new String(mmBuffer, 0, numBytes);
                    Message readMsg = mHandler.obtainMessage(
                            MessageConstants.MESSAGE_READ, numBytes, -1,
                            readMessage);
                    readMsg.sendToTarget();


                    if(type==1){
                        //decode message
                        String replyMessage = MessageDecoder.decodeMessage(readMessage);
                        if(readMessage!=null){
                            this.write(replyMessage);
                        }else{
//                            //for pinging reply
//                            Message writtenMsg = mHandler.obtainMessage(
//                                    MessageConstants.MESSAGE_WRITE, -1, -1, "sending message");
//                            writtenMsg.sendToTarget();
                        }
//                        this.write(("received@"+new Date().toString()));
                    }else if(type==0){
                        String replyMessage = MessageDecoder.decodeMessage(readMessage);
                        if(readMessage!=null){
                            this.write(replyMessage);
                        }
                    }


                } catch (IOException e) {
                    Log.d(TAG, "Input stream was disconnected", e);
                    Message errorMessage = mHandler.obtainMessage(
                            MessageConstants.MESSAGE_READ, 1024, -1,
                            AppMessageConstants.MASTER_DISCONNECTED);
                    errorMessage.sendToTarget();

                    if(type==1 && AppMessageConstants.isTracking){
                        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                        Ringtone ringTone = RingtoneManager.getRingtone(applicationContext, notification);
                        ringTone.play();
                    }

                    break;
                }
            }
        }

        // Call this from the main activity to send data to the remote device.
        public void write(String message) throws IOException {
            try {
                Message writtenMsg = mHandler.obtainMessage(
                        MessageConstants.MESSAGE_WRITE, -1, -1, message);
                writtenMsg.sendToTarget();

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mmOutStream.write(message.getBytes());
                mmOutStream.flush();

                // Share the sent message with the UI activity.

            } catch (IOException e) {
                Log.d("Error",e.getMessage());

                // Send a failure message back to the activity.
                Message writeErrorMsg =
                        mHandler.obtainMessage(MessageConstants.MESSAGE_TOAST);
                Bundle bundle = new Bundle();
                bundle.putString("toast",
                        "Couldn't send data to the other device");
                writeErrorMsg.setData(bundle);
                writeErrorMsg.sendToTarget();
                throw e;
            }
        }

        // Call this method from the main activity to shut down the connection.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the connect socket", e);
            }
        }

        public BluetoothSocket getMmSocket() {
            return mmSocket;
        }
    }
}
