package com.example.swrp.trackmountaineer;

import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.github.mikephil.charting.charts.LineChart;
import com.mbientlab.metawear.MetaWearBoard;
import com.mbientlab.metawear.android.BtleService;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.example.swrp.trackmountaineer.DownloadHandler.baroBosch;
import static com.example.swrp.trackmountaineer.DownloadHandler.pressureData;

public class MainActivity extends AppCompatActivity implements ServiceConnection {

    private static final String TAG = "Track-Mountaineer";

    static int numOfValues = 0;

    //Hardcoded Metawear MAC Address
    private final String MW_MAC_ADDRESS= "C0:F3:B7:B6:16:DA";

    static MetaWearBoard mwBoard;

    private BtleService.LocalBinder serviceBinder;

    static LineChart chart;

    /*
    Pop-up Message to access files before writing into the internal storage
     */
    protected boolean shouldAskPermissions() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }


    /*
        Setting Permissions to Write data to Mobile Internal Storage
     */
    @TargetApi(23)
    protected void askPermissions() {
        String[] permissions = {
                "android.permission.READ_EXTERNAL_STORAGE",
                "android.permission.WRITE_EXTERNAL_STORAGE"
        };
        int requestCode = 200;
        requestPermissions(permissions, requestCode);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (shouldAskPermissions()) {
            askPermissions();
        }

        ///< Bind the service when the activity is created
        getApplicationContext().bindService(new Intent(this, BtleService.class),
                this, Context.BIND_AUTO_CREATE);

            findViewById(R.id.startButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

            // ShowGraph Button for Graphical Representation of Tracked Pressure Values
            findViewById(R.id.showGraph).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, ShowGraph.class);
                    startActivity(intent);
                }
            });

            //Save Button to log data to a csv file in Downloads directory
        findViewById(R.id.saveButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    saveData();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        findViewById(R.id.notificationButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNotification(); // pops up notification when the pressure is low
            }
        });
    }

    /*
    Method to save tracked data in the Mobile Internal Storage - Downloads Folder
     */
    public void saveData() throws IOException {
        String fileName = "METAWEAR.csv";
        final File path = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS), fileName);

        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS), "/" + fileName);
        file.createNewFile();
        FileOutputStream outputStream = null;

        outputStream = new FileOutputStream(file, true);

        for (int i = 0; i<pressureData.size() ; i++){
            outputStream.write((pressureData.get(i) + ","+ "\n").getBytes());
        }
        outputStream.close();

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

    // Method to Connect to the Sensor Bluetooth Module
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

    /*
    Method to Push Notifications when Pressure is below sustainable Heart Rate
     */
    public void createNotification(){

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        String CHANNEL_ID = "";


            //Opens the graph activity with a tap on notification
        Intent intent = new Intent(this, ShowGraph.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        Resources r = getResources();

        NotificationCompat.Builder notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.mountain_background)
                .setContentTitle("Low Pressure Alert..!!")
                .setContentText("Pressure Levels are dropping. Going Ahead might be a risk..")
                .setAutoCancel(true)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);


        notificationManager.notify(0, notification.build());

    }

}



