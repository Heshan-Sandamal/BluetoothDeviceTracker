package com.cs4492.cseuom.bluetoothdevicetracker;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelUuid;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cs4492.cseuom.bluetoothdevicetracker.socket_connection.AcceptThread;
import com.cs4492.cseuom.bluetoothdevicetracker.socket_connection.ConnectThread;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PairedDevicesListActivity extends AppCompatActivity {

    private TextView textview1 ;
    BluetoothAdapter btAdapter;
    ListView listview ;
    private ListView mLvDevices;
    private ArrayList<String> mDeviceList = new ArrayList<String>();
    private static final int REQUEST_ENABLE_BT = 1;
    ArrayAdapter<String> adapter ;
    Button scanbutton ;
    private String m_Text = "";
    private BluetoothSocket mmSocket ;
    private OutputStream mmOutputStream;
    private InputStream mmInputStream;
    private boolean stopWorker;
    private int readBufferPosition;
    private byte readBuffer [] ;
    private Thread workerThread ;

    @BindView(R.id.mylist)
    ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paired_devices_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Paired Devices");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter name for your device");


        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);


        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                m_Text = input.getText().toString();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
        //ListeningThread t = new ListeningThread();
        //  t.start();
        //  Log.d("mylog" ,"listofdevices") ;

//        /listview=(ListView) findViewById(R.layout.activity_paired_devices_list);


        adapter=new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_checked,
                mDeviceList);



        list.setAdapter(adapter);
        scanbutton = (Button) findViewById(R.id.addBtn);

        btAdapter = BluetoothAdapter.getDefaultAdapter();

        scanbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(m_Text.equals("")){
                    Toast.makeText(PairedDevicesListActivity.this,"Please Enter Name for Your Device !!!",Toast.LENGTH_SHORT).show();

                }else {
                    mDeviceList.clear();
                    scanDevices();
                }

            }
        }) ;

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {


            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String  itemValue = String.valueOf(((TextView) view).getText());
                char symbol= itemValue.charAt(itemValue.length() - 19) ;
                //    Log.d("mylog" , String.valueOf(symbol)) ;
                if(symbol=='#') {
                    // Log.d("mylog" , "haririririri") ;

                    String MAC = itemValue.substring(itemValue.length() - 17);
                    BluetoothDevice bluetoothDevice = btAdapter.getRemoteDevice(MAC);

                    Log.d("MAC",bluetoothDevice.getAddress());

                    for(ParcelUuid pl:bluetoothDevice.getUuids()){
                        Log.d("UUID",pl.getUuid().toString());
                    }

                    ConnectThread connectThread = new ConnectThread(bluetoothDevice,PairedDevicesListActivity.this.handler);
                    connectThread.start();
                    Toast.makeText(PairedDevicesListActivity.this,"Connected to the thread",Toast.LENGTH_SHORT).show();


                    // Initiate a connection request in a separate thread
                    // ConnectingThread t = new ConnectingThread(bluetoothDevice);
                    // t.start();
                    //*******************************************
                    //do whatever you want when click an item
                    // openBT(bluetoothDevice) ;
                    //  sendData();
                    //*******************************************
                    //pairDevice(bluetoothDevice) ;
                    // Toast.makeText(ListofDevices.this,"Initializing Connection Request to"+MAC,Toast.LENGTH_SHORT).show();

                }else{
                    // Log.d("mylog" , "NAaaaaaaaaaa") ;

                }

            }
        });
    }
    void openBT(BluetoothDevice mmDevice) throws IOException {
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); //Standard                //SerialPortService ID
        mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
        mmSocket.connect();
        mmOutputStream = mmSocket.getOutputStream();
        mmInputStream = mmSocket.getInputStream();
        beginListenForData();
        Log.d("mylog","bluetooth opened");
    }

    void beginListenForData() {
        final Handler handler = new Handler();
        final byte delimiter = 10; //This is the ASCII code for a newline character

        stopWorker = false;
        readBufferPosition = 0;
        readBuffer = new byte[1024];
        workerThread = new Thread(new Runnable() {
            public void run() {
                while(!Thread.currentThread().isInterrupted() && !stopWorker) {
                    try {
                        int bytesAvailable = mmInputStream.available();
                        if(bytesAvailable > 0) {
                            byte[] packetBytes = new byte[bytesAvailable];
                            mmInputStream.read(packetBytes);
                            for(int i=0;i<bytesAvailable;i++) {
                                byte b = packetBytes[i];
                                if(b == delimiter) {
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    final String data = new String(encodedBytes, "US-ASCII");
                                    readBufferPosition = 0;

                                    handler.post(new Runnable() {
                                        public void run() {
                                            Log.d("mylog",data);

                                            //myLabel.setText(data);
                                        }
                                    });
                                }else {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }
                    }catch (IOException ex) {
                        stopWorker = true;
                    }
                }
            }
        });

        workerThread.start();
    }

    void sendData() throws IOException {
        String msg = "HUUUUU";
        msg += "\n";
        //mmOutputStream.write(msg.getBytes());
        mmOutputStream.write('A');
        Log.d("mylog","data sent");

        //  myLabel.setText("Data Sent");
    }

    private void pairDevice(BluetoothDevice device) {
        try {
            Method method = device.getClass().getMethod("createBond", (Class[]) null);
            method.invoke(device, (Object[]) null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void unpairDevice(BluetoothDevice device) {
        try {
            Method method = device.getClass().getMethod("removeBond", (Class[]) null);
            method.invoke(device, (Object[]) null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private final BroadcastReceiver mPairReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                final int state        = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
                final int prevState    = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.ERROR);

                if (state == BluetoothDevice.BOND_BONDED && prevState == BluetoothDevice.BOND_BONDING) {
                    Toast.makeText(PairedDevicesListActivity.this,"Paired",Toast.LENGTH_SHORT).show();
                } else if (state == BluetoothDevice.BOND_NONE && prevState == BluetoothDevice.BOND_BONDED){
                    Toast.makeText(PairedDevicesListActivity.this,"Unpaired",Toast.LENGTH_SHORT).show();
                }

            }
        }
    };
    IntentFilter intent = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
    // registerReceiver(mPairReceiver, intent);

    private void scanDevices() {
        CheckBluetoothState();
//        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
//        registerReceiver(mBtReceiver, filter);

        // Getting the Bluetooth adapter
//        btAdapter = BluetoothAdapter.getDefaultAdapter();
//        if(btAdapter != null) {
//            btAdapter.startDiscovery();
//            Toast.makeText(this, "Starting discovery...", Toast.LENGTH_SHORT).show();
//        }
//        else {
//            Toast.makeText(this, "Bluetooth disabled or not available.", Toast.LENGTH_SHORT).show();
//        }
    }

    //    private final BroadcastReceiver mBtReceiver = new BroadcastReceiver() {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
//                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                mDeviceList.add(device.getName()+" # "+device.getAddress()); // get mac address concat device.getAddress()
//                adapter.notifyDataSetChanged();
//                // Log.d("mylog" ,device.getAddress() + ", " + device.getName()) ;
//
//            }
//        }
//    };
    private void CheckBluetoothState() {
        // Checks for the Bluetooth support and then makes sure it is turned on
        // If it isn't turned on, request to turn it on
        // List paired devices
        if(btAdapter==null) {
            //textview1.append("\nBluetooth NOT supported. Aborting.");
            mDeviceList.add("Bluetooth NOT supported. Aborting.");
            adapter.notifyDataSetChanged();
            return;
        } else {
            if (btAdapter.isEnabled()) {
                // textview1.append("\nBluetooth is enabled...");
                mDeviceList.add("Bluetooth is enabled...");
                adapter.notifyDataSetChanged();
                // Listing paired devices
                //textview1.append("\nPaired Devices are:");
                mDeviceList.add("Paired Devices are:");
                adapter.notifyDataSetChanged();

                Set<BluetoothDevice> devices = btAdapter.getBondedDevices();
                for (BluetoothDevice device : devices) {
                    // textview1.append("\n  Device: " + device.getName() + ", " + device);
                    mDeviceList.add(device.getName()+" # "+device); // get mac address  concat "device"
                    adapter.notifyDataSetChanged();
                }
//                mDeviceList.add("Available Devices are:");
//                adapter.notifyDataSetChanged();
            } else {
                //Prompt user to turn on Bluetooth
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
    }

    private final Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };



}
