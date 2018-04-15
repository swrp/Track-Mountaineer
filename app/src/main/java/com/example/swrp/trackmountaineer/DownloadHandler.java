package com.example.swrp.trackmountaineer;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.mbientlab.metawear.Data;
import com.mbientlab.metawear.MetaWearBoard;
import com.mbientlab.metawear.Route;
import com.mbientlab.metawear.Subscriber;
import com.mbientlab.metawear.UnsupportedModuleException;
import com.mbientlab.metawear.builder.RouteBuilder;
import com.mbientlab.metawear.builder.RouteComponent;
import com.mbientlab.metawear.module.BarometerBosch;

import java.util.ArrayList;

import bolts.Continuation;
import bolts.Task;

import static com.example.swrp.trackmountaineer.MainActivity.chart;
import static com.example.swrp.trackmountaineer.MainActivity.mwBoard;

class DownloadHandler extends Handler  {

    private MainActivity mainActivity;

    private static final String TAG = "Track-Mountaineer";

    private final String MW_MAC_ADDRESS = "C0:F3:B7:B6:16:DA";

    static BarometerBosch baroBosch;

    private static final float BAROMETER_SAMPLE_FREQ = 26.32f, LIGHT_SAMPLE_PERIOD= 1 / BAROMETER_SAMPLE_FREQ;

    private final MetaWearBoard board = mwBoard;

   static ArrayList<Entry> pressureData= new ArrayList<Entry>();

    protected long prevUpdate = -1;

    private final Handler chartHandler= new Handler();


    private int sampleCount;

    public DownloadHandler(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public DownloadHandler() {

    }

    @Override
    public void handleMessage(Message msg) {
        try {
            retrieveBoard();
        } catch (UnsupportedModuleException e) {
            e.printStackTrace();
        }
    }

    private void retrieveBoard() throws UnsupportedModuleException {

        board.connectAsync().onSuccessTask(new Continuation<Void, Task<Route>>() {
            @Override
            public Task<Route> then(Task<Void> task) throws Exception {

                baroBosch = board.getModuleOrThrow(BarometerBosch.class);

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
                    baroBosch.start();
                }
                return null;
            }
        });
    }

}

