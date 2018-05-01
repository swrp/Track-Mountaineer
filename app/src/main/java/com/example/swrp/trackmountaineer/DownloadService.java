
/**
 * Service that lets the application to run in the background even when the mobile is locked
 * Looper is a worker that keeps a thread alive, loops through MessageQueue and sends messages to the corresponding handler to process.
 * Finally Thread gets terminated by calling Looper’s quit() method
 **/


package com.example.swrp.trackmountaineer;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;



public class DownloadService extends Service {

    DownloadThread mThread;
    DownloadHandler mHandler;

    // Creating a new thread so that tracking is performed as a background service
    //Doesn't run on the main thread
    @Override
    public void onCreate() {
        mThread = new DownloadThread();
        mThread.setName("Download Thread");
        mThread.start();

        while (mThread.mHandler == null) {

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
        Message message = Message.obtain();
        mHandler.sendMessage(message);
        return Service.START_REDELIVER_INTENT; // Starts the thread even after closing the application
    }

    // Runs as a seperate thread to track pressure values
    class DownloadThread extends Thread {

        DownloadHandler mHandler;

        @Override
        public void run() {
            Looper.prepare(); // looper and MessageQueue paased to the thred
            mHandler = new DownloadHandler();
            Looper.loop(); // Keeps the thread alive and loops around Message Queue

        }
    }
}
