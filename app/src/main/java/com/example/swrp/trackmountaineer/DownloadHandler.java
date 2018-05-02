
/*
 * Handler enqueues task in the MessageQueue using Looper and also executes them when the task comes out of the MessageQueue.
 * Tracking data from the Metawear Sensor is performed by the handler class and runs on a seperate thread that lets the application to run when the mobile is locked.
 */

package com.example.swrp.trackmountaineer;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.github.mikephil.charting.data.Entry;
import com.mbientlab.metawear.Data;
import com.mbientlab.metawear.MetaWearBoard;
import com.mbientlab.metawear.Route;
import com.mbientlab.metawear.Subscriber;
import com.mbientlab.metawear.UnsupportedModuleException;
import com.mbientlab.metawear.builder.RouteBuilder;
import com.mbientlab.metawear.builder.RouteComponent;
import com.mbientlab.metawear.module.BarometerBosch;
import com.mbientlab.metawear.module.Temperature;
import com.mbientlab.metawear.module.Timer;

import java.util.ArrayList;

import bolts.Continuation;
import bolts.Task;

import static com.example.swrp.trackmountaineer.MainActivity.mwBoard;

class DownloadHandler extends Handler  {

    private MainActivity mainActivity;

    private Timer timer;

    private Timer.ScheduledTask mwTask;

    private static final String TAG = "Track-Mountaineer"; //Tag value for ease of reading logcat

    private final String MW_MAC_ADDRESS = "C0:F3:B7:B6:16:DA"; //Hardcoded MAC address

    static BarometerBosch baroBosch;


    private final MetaWearBoard board = mwBoard;

   static ArrayList<Entry> pressureData= new ArrayList<Entry>();

   static ArrayList<Entry> timeStamp = new ArrayList<Entry>();

    protected long prevUpdate = -1;

    protected Route streamRoute = null;

    private final Handler chartHandler= new Handler();


    private int sampleCount;

    public DownloadHandler(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public DownloadHandler() {

    }

    //Handler function called by the looper
    @Override
    public void handleMessage(Message msg) {
        try {
            retrieveBoard();
        } catch (UnsupportedModuleException e) {
            e.printStackTrace();
        }
    }

    //Function where the Application starts tracking the pressure values from the Metawear Sensor
    private void retrieveBoard() throws UnsupportedModuleException {

        board.connectAsync().onSuccessTask(new Continuation<Void, Task<Route>>() {
            @Override
            public Task<Route> then(Task<Void> task) throws Exception {

                baroBosch = board.getModuleOrThrow(BarometerBosch.class);
                timer = board.getModuleOrThrow(Timer.class);

                baroBosch.configure()
                        .filterCoeff(BarometerBosch.FilterCoeff.AVG_16)
                        .pressureOversampling(BarometerBosch.OversamplingMode.ULTRA_HIGH)
                        .commit();

                return baroBosch.pressure().addRouteAsync(new RouteBuilder() {
                    @Override
                    public void configure(RouteComponent source) {
                        source.stream(new Subscriber() {
                            @Override
                            public void apply(Data data, Object... env) {

                                if(pressureData.size() >= sampleCount) {
                                    try {
                                        Thread.sleep(1000); // Tracks Pressure values for every second
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    //logs pressure value into an arraylist
                                    pressureData.add(new Entry(data.value(Float.class), sampleCount));
                                    sampleCount ++ ;
                                }
                                Log.i(TAG, "Pressure (Pa) = " + data.value(Float.class));
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
                            baroBosch.start(); // starts tracking on successful connection with the metawear board
                        }
                        return null;
                    }
                });
    }
}



