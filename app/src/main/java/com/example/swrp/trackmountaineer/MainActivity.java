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

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.mbientlab.metawear.MetaWearBoard;
import com.mbientlab.metawear.android.BtleService;

import static com.example.swrp.trackmountaineer.DownloadHandler.baroBosch;
import static com.example.swrp.trackmountaineer.DownloadHandler.pressureData;

public class MainActivity extends AppCompatActivity implements ServiceConnection {

    //MAKE A CHART TO REPRESENT DATA
    //PUSH NOTIFICATIONS TO THE MOBILE
    //SAVE DATA IN A TABLE

    private Float max, min;

    private static final String TAG = "Track-Mountaineer";

    private final String MW_MAC_ADDRESS= "C0:F3:B7:B6:16:DA";

    static MetaWearBoard mwBoard;

    private BtleService.LocalBinder serviceBinder;

    static LineChart chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ///< Bind the service when the activity is created
        getApplicationContext().bindService(new Intent(this, BtleService.class),
                this, Context.BIND_AUTO_CREATE);

        chart = findViewById(R.id.dataChart);
        initializeChart();
        chart.invalidate();
        chart.setDescription(null);

            findViewById(R.id.startButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //new DownloadHandler(MainActivity.this).initialise();
                Intent intent = new Intent(MainActivity.this, DownloadService.class);
                startService(intent);
            }
        });

            findViewById(R.id.stopButton).setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v){
                    baroBosch.stop();
                }
                });

            findViewById(R.id.showGraph).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ILineDataSet dataSet = new LineDataSet(pressureData , "Tracked Pressure Values");
                    LineData lineData = new LineData(dataSet);
                    chart.setData(lineData);
                    chart.invalidate();
                }
            });
    }

        protected void initializeChart() {
            ///< configure axis settings
            YAxis leftAxis = chart.getAxisLeft();
        }


    @Override
    public void onDestroy() {
        super.onDestroy();

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



