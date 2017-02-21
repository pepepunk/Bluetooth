package com.pepepunk.app.myapplication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();
    int DISCOVERY_REQUEST;

    private ListView list;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list = (ListView) findViewById(R.id.disp);

        arrayList = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayList);
        list.setAdapter(adapter);

        BroadcastReceiver bluethootState = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String stateExtra = BluetoothAdapter.EXTRA_STATE;
                int state = intent.getIntExtra(stateExtra, -1);
                String toastText = "";

                switch (state) {
                    case (BluetoothAdapter.STATE_TURNING_ON):
                        toastText = "Encendiendo Bluetooth";
                        break;


                    case (BluetoothAdapter.STATE_ON):
                        toastText = "Bluetooth Encendido";
                        break;


                    case (BluetoothAdapter.STATE_TURNING_OFF):
                        toastText = "Apagando Bluetooth";
                        break;

                    case (BluetoothAdapter.STATE_OFF):
                        toastText = "Bluetooth Apagado";
                        break;
                }
                Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_LONG).show();
            }

        };
        if (!bluetooth.isEnabled()) {
            String actionStateChanged = BluetoothAdapter.ACTION_STATE_CHANGED;
            String actionRequestEnable = BluetoothAdapter.ACTION_REQUEST_ENABLE;
            registerReceiver(bluethootState, new IntentFilter(actionStateChanged));
            startActivityForResult(new Intent(actionRequestEnable), 0);
        }

        BroadcastReceiver discoveryMonitor = new BroadcastReceiver() {
            String dStarted = BluetoothAdapter.ACTION_DISCOVERY_STARTED;
            String dFinished = BluetoothAdapter.ACTION_DISCOVERY_FINISHED;

            @Override
            public void onReceive(Context context, Intent intent) {
                if (dStarted.equals(intent.getAction())) {
                    Toast.makeText(getApplication(), "Proceso de busqueda iniciado", Toast.LENGTH_LONG).show();
                } else if (dFinished.equals(intent.getAction())) {
                    Toast.makeText(getApplicationContext(), "El proceso de busqueda finalizó", Toast.LENGTH_LONG).show();
                }
            }
        };
        registerReceiver(discoveryMonitor, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED));
        registerReceiver(discoveryMonitor, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));

        BroadcastReceiver discoveryResult = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String remoteDeviceName = intent.getStringExtra(BluetoothDevice.EXTRA_NAME);
                BluetoothDevice remoteDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                arrayList.add(remoteDeviceName+" : "+remoteDevice.getAddress());
                adapter.notifyDataSetChanged();

                Toast.makeText(getApplicationContext(), remoteDeviceName, Toast.LENGTH_LONG).show();
            }
        };

        registerReceiver(discoveryResult, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        if (!bluetooth.isDiscovering()) {
            bluetooth.startDiscovery();
        }
    }
}