package com.cs4492.cseuom.bluetoothdevicetracker;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cs4492.cseuom.bluetoothdevicetracker.scheduler.PingScheduler;
import com.cs4492.cseuom.bluetoothdevicetracker.socket_connection.AcceptThread;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ServerActivity extends AppCompatActivity {

    @BindView(R.id.serverButton)
    Button serverButton;
    private BluetoothAdapter btAdapter;

    @BindView(R.id.deviceNameTextBox)
    TextView deviceNameTextBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btAdapter = BluetoothAdapter.getDefaultAdapter();

        serverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



            }
        });
    }



}
