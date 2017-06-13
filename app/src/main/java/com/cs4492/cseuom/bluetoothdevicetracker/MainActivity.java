package com.cs4492.cseuom.bluetoothdevicetracker;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cs4492.cseuom.bluetoothdevicetracker.protocol.AppMessageConstants;
import com.cs4492.cseuom.bluetoothdevicetracker.scheduler.PingScheduler;
import com.cs4492.cseuom.bluetoothdevicetracker.socket_connection.AcceptThread;
import com.cs4492.cseuom.bluetoothdevicetracker.socket_connection.ConnectedSockets;
import com.cs4492.cseuom.bluetoothdevicetracker.socket_connection.MyBluetoothService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private BluetoothAdapter BTAdapter;

    private SQLiteDatabase database;

    public static int REQUEST_BLUETOOTH = 1;

    @BindView(R.id.textView2)
    TextView textView2;

    @BindView(R.id.myButton)
    Button startServiceButton;

    @BindView(R.id.stopService)
    Button stopServiceButton;

    @BindView(R.id.device_name_textbox)
    TextView deviceNameTextBox;

    @BindView(R.id.device_address_text)
    TextView deviceAddressTextBox;

    @BindView(R.id.refresh_connected_clients_button)
    Button refreshConnectedClientsButton;

    private String m_Text = "";

    @BindView(R.id.connected_clients_list)
    ListView connectedClientsList;

    @BindView(R.id.clientConnectedListLabel)
    TextView clientConnectedListLabel;

    @BindView(R.id.stop_alarm_button)
    Button stopAlarmButton;

    BroadcastReceiver myReceiver = null;
    Boolean myReceiverIsRegistered = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BTAdapter = BluetoothAdapter.getDefaultAdapter();
        // Phone does not support Bluetooth so let the user know and exit.

        ButterKnife.bind(this);

        if (BTAdapter == null) {
            new AlertDialog.Builder(this)
                    .setTitle("Not compatible")
                    .setMessage("Your phone does not support Bluetooth")
                    .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
        if (!BTAdapter.isEnabled()) {
            Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBT, REQUEST_BLUETOOTH);
        } else {
            deviceAddressTextBox.setText(BTAdapter.getName() + BTAdapter.getName());
        }

        database = openOrCreateDatabase("BluetoothDeviceTracker", MODE_PRIVATE, null);
        database.execSQL("CREATE TABLE IF NOT EXISTS User(userName VARCHAR);");

        Cursor resultSet = database.rawQuery("Select userName from User", null);


        if (resultSet.moveToFirst()) {
            deviceNameTextBox.setText(resultSet.getString(0));
        } else {
            showNameInputDialog();
        }


        //ButterKnife.bind(this);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        startServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<MyBluetoothService.ConnectedThread> socketObjectsList = ConnectedSockets.getSocketObjectsList();
                Log.d("dfd", "message");
                startService(new Intent(MainActivity.this, PingScheduler.class));
                AppMessageConstants.isTracking=true;
//                for (MyBluetoothService.ConnectedThread ob:socketObjectsList){
//                    Log.d("sending msg","message");
//                    ob.write("sending message".getBytes());
//                }
            }
        });

        stopServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(new Intent(MainActivity.this, PingScheduler.class));
                AppMessageConstants.isTracking=false;
            }
        });

        refreshConnectedClientsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.setConnectedClientList();
            }
        });

        setConnectedClientList();
        myReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                System.out.print("jesddddddddddddddddddddddddddddddddddddddddddddd");
                Log.d("broadcase", "broadcast");
            }
        };

        stopAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ringTone.stop();
                stopAlarmButton.setVisibility(View.INVISIBLE);
            }
        });

        registerReceiver(myReceiver, new IntentFilter("com.cs4492.cseuom.bluetoothdevicetracker"));
        Toast.makeText(this, "Registered the broadcast listener", Toast.LENGTH_SHORT);
        myReceiverIsRegistered = true;

    }

    private void setConnectedClientList() {
        List<MyBluetoothService.ConnectedThread> socketObjectsList = ConnectedSockets.getSocketObjectsList();

        ArrayList<String> mDeviceList = new ArrayList<String>();

        for (MyBluetoothService.ConnectedThread connectedThread : socketObjectsList) {
            BluetoothDevice remoteDevice = connectedThread.getMmSocket().getRemoteDevice();
            mDeviceList.add(remoteDevice.getName() + remoteDevice.getAddress());

        }

        if (mDeviceList.isEmpty()) {
            textView2.setText(R.string.not_connected_to_network);
        }


        ArrayAdapter adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_checked,
                mDeviceList);

        connectedClientsList.setAdapter(adapter);
    }


    private void showNameInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter name for your device");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                m_Text = input.getText().toString();
                database.execSQL("INSERT INTO User VALUES('" + m_Text + "');");
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();


    }

    @Override
    protected void onPause() {
        super.onPause();
        if (myReceiverIsRegistered) {
            unregisterReceiver(myReceiver);
            myReceiverIsRegistered = false;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (AppMessageConstants.CONNECTED_CLIENT.equals(AppMessageConstants.hostType)) {
            setConnectedClientList();
//            MainActivity.this.startServiceButton.setEnabled(false);
            startServiceButton.setText("Unregister");

            startServiceButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    List<MyBluetoothService.ConnectedThread> socketObjectsList = ConnectedSockets.getSocketObjectsList();
                    if(socketObjectsList.size()>0){
                        try {
                            socketObjectsList.get(0).write("UNREG");
                        } catch (IOException e) {
                            Log.d("error",e.getMessage());
                        }
                    }
                }
            });

            MainActivity.this.stopServiceButton.setVisibility(View.INVISIBLE);
            MainActivity.this.clientConnectedListLabel.setText("Connected Server");
            this.textView2.setText("This device is tracked using Bluetooth");
            MainActivity.this.setConnectedClientList();
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.Paired) {
            // Handle the camera action
            Intent intent = new Intent(MainActivity.this, PairedDevicesListActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);

        } else if (id == R.id.Available) {
            startActivity(new Intent(MainActivity.this, AvailableDevicesList.class));

        } else if (id == R.id.nav_slideshow) {
//            startActivity(new Intent(MainActivity.this, ServerActivity.class));
            try {
                new AcceptThread(BTAdapter, MainActivity.this.handler, getApplicationContext()).start();
                Toast.makeText(MainActivity.this, "Started the thread", Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (id == R.id.nav_manage) {
            startActivity(new Intent(MainActivity.this, AvailableDevicesList.class));

        }
//        else if (id == R.id.nav_share) {
//
//        } else if (id == R.id.nav_send) {
//
//        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private Ringtone ringTone;
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            MainActivity.this.setConnectedClientList();
            String data=null;
            if(msg!=null && msg.obj!=null){
                data=msg.obj.toString();
                textView2.setText(data);
            }
            try {
                Log.d("data", msg.obj.toString());

                if(AppMessageConstants.MASTER_DISCONNECTED.equals(data)){
                    ConnectedSockets.clearConnectedThreadsList();
                    MainActivity.this.setConnectedClientList();
                    try {

                        if(AppMessageConstants.isTracking){
                            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                            ringTone = RingtoneManager.getRingtone(getApplicationContext(), notification);
                            ringTone.play();
                            stopAlarmButton.setVisibility(View.VISIBLE);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }


            } catch (Exception e) {
                Log.e("error", "error message");
            }

            Toast.makeText(MainActivity.this, "Message receivedd", Toast.LENGTH_LONG);

            //super.handleMessage(msg);
        }
    };


}
