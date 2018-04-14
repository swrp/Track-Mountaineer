package com.example.swrp.trackmountaineer;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.mbientlab.metawear.Data;
import com.mbientlab.metawear.MetaWearBoard;
import com.mbientlab.metawear.Route;
import com.mbientlab.metawear.Subscriber;
import com.mbientlab.metawear.UnsupportedModuleException;
import com.mbientlab.metawear.android.BtleService;
import com.mbientlab.metawear.builder.RouteBuilder;
import com.mbientlab.metawear.builder.RouteComponent;
import com.mbientlab.metawear.module.BarometerBosch;
import com.mbientlab.metawear.module.Temperature;

import bolts.Continuation;
import bolts.Task;

public class MainActivity extends AppCompatActivity implements ServiceConnection {


    private static final String TAG = "Track-Mountaineer";

    private final String MW_MAC_ADDRESS= "C0:F3:B7:B6:16:DA";

    static MetaWearBoard mwBoard;

    private BtleService.LocalBinder serviceBinder;

    private int selectedSourceIndex= 0;

    private TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ///< Bind the service when the activity is created
        getApplicationContext().bindService(new Intent(this, BtleService.class),
                this, Context.BIND_AUTO_CREATE);

        textView = findViewById(R.id.pressureValue);



            findViewById(R.id.startButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   Intent intent = new Intent(MainActivity.this, DownloadService.class);
                   startService(intent);
                }
            });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        ///< Unbind the service when the activity is destroyed
        getApplicationContext().unbindService(this);
    }


    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        serviceBinder = (BtleService.LocalBinder) service;
        Log.i(TAG, "Service connected");
        connectBLE();
    }

    private void connectBLE() {
        final BluetoothManager btManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        final BluetoothDevice remoteDevice =
                btManager.getAdapter().getRemoteDevice(MW_MAC_ADDRESS);

        // Create a MetaWear board object for the Bluetooth Device
        mwBoard = serviceBinder.getMetaWearBoard(remoteDevice);

        Log.i(TAG, "Service Connected to ... " + MW_MAC_ADDRESS);

    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }

}



