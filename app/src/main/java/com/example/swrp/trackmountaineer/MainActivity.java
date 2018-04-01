package com.example.swrp.trackmountaineer;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.mbientlab.metawear.Data;
import com.mbientlab.metawear.MetaWearBoard;
import com.mbientlab.metawear.Route;
import com.mbientlab.metawear.Subscriber;
import com.mbientlab.metawear.android.BtleService;
import com.mbientlab.metawear.builder.RouteBuilder;
import com.mbientlab.metawear.builder.RouteComponent;
import com.mbientlab.metawear.module.Temperature;

import bolts.Continuation;
import bolts.Task;

public class MainActivity extends AppCompatActivity implements ServiceConnection {


    private static final String TAG = "Track-Mountaineer";

    private final String MW_MAC_ADDRESS= "C0:F3:B7:B6:16:DA";

    private MetaWearBoard mwBoard;

    private BtleService.LocalBinder serviceBinder;

    private Temperature temperature;

    private Temperature.Sensor tempSensor;



    private byte gpioDataPin= 0, gpioPulldownPin= 1;
    private boolean activeHigh= false;
    private int selectedSourceIndex= 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ///< Bind the service when the activity is created
        getApplicationContext().bindService(new Intent(this, BtleService.class),
                this, Context.BIND_AUTO_CREATE);

        findViewById(R.id.startButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tempSensor.read();
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
        retrieveBoard();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }

    public void retrieveBoard() {
        final BluetoothManager btManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        final BluetoothDevice remoteDevice =
                btManager.getAdapter().getRemoteDevice(MW_MAC_ADDRESS);

        // Create a MetaWear board object for the Bluetooth Device
        mwBoard = serviceBinder.getMetaWearBoard(remoteDevice);

        mwBoard.connectAsync().onSuccessTask(new Continuation<Void, Task<Route>>() {
            @Override
            public Task<Route> then(Task<Void> task) throws Exception {

                    Log.i(TAG, "Service Connected to ... " + MW_MAC_ADDRESS);

                temperature = mwBoard.getModuleOrThrow(Temperature.class);

                ((Temperature.ExternalThermistor) temperature.findSensors(Temperature.SensorType.EXT_THERMISTOR)[0])
                        .configure((byte) 0, (byte) 1, false);

                Temperature.Sensor tempSensor = temperature.sensors()[selectedSourceIndex];

                return tempSensor.addRouteAsync(new RouteBuilder() {
                    @Override
                    public void configure(RouteComponent source) {
                        source.stream(new Subscriber() {
                            @Override
                            public void apply(Data data, Object... env) {
                                Log.i(TAG, "Temperature (C) = " + data.value(Float.class));
                            }
                        });
                    }
                });
            }
        }).continueWith(new Continuation<Route, Void>() {
            @Override
            public Void then(Task<Route> task) throws Exception {
                if (task.isFaulted()) {
                    Log.w(TAG, "Failed to configure the app" + task.getError());
                } else {
                    Log.i(TAG, "App Configured");
                }
                return null;
            }
        });
    }
}


