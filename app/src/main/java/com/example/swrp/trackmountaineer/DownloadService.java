package com.example.swrp.trackmountaineer;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import com.mbientlab.metawear.Data;
import com.mbientlab.metawear.MetaWearBoard;
import com.mbientlab.metawear.Route;
import com.mbientlab.metawear.Subscriber;
import com.mbientlab.metawear.UnsupportedModuleException;
import com.mbientlab.metawear.builder.RouteBuilder;
import com.mbientlab.metawear.builder.RouteComponent;
import com.mbientlab.metawear.module.BarometerBosch;

import bolts.Continuation;
import bolts.Task;


import static com.example.swrp.trackmountaineer.MainActivity.mwBoard;

/**
 * Created by swrp on 4/12/18.
 */

public class DownloadService extends Service {

    DownloadThread mThread;
    DownloadHandler mHandler;

    @Override
    public void onCreate() {
        mThread = new DownloadThread();
        mThread.setName("Download Thread");
        mThread.start();

        while (mThread.mHandler == null){

        }
            mHandler = mThread.mHandler;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //intent.getExtras();
        Message message = Message.obtain();
        mHandler.sendMessage(message);
        return Service.START_STICKY;
    }

    class DownloadThread extends Thread {

        DownloadHandler mHandler;

        @Override
        public void run() {
            Looper.prepare();
            mHandler = new DownloadHandler();
            Looper.loop();
        }
    }

    class DownloadHandler extends Handler {

        private static final String TAG = "Track-Mountaineer";

        private final String MW_MAC_ADDRESS = "C0:F3:B7:B6:16:DA";

        private BarometerBosch baroBosch;

        private MetaWearBoard board = mwBoard;

        @Override
        public void handleMessage(Message msg) {
            //super.handleMessage(msg);
            try {
                retrieveBoard();
            } catch (UnsupportedModuleException e) {
                e.printStackTrace();
            }
        }

        private void retrieveBoard() throws UnsupportedModuleException {


            mwBoard.connectAsync().onSuccessTask(new Continuation<Void, Task<Route>>() {
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
}

