package com.nckuesnclab.daniel.lockapp;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.nckuesnclab.daniel.lockapp.model.AppManager;

public class LockAppService extends Service {

    private MyBinder myBinder = new MyBinder();
    private AppManager appManager;
    private Handler handler;
    private Runnable runnable;

    @Override
    public void onCreate() {
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(runnable, 1000);
                appManager.checkApp();
            }
        };
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return myBinder;
    }

    class MyBinder extends Binder{

        public void setAppManager(AppManager manager){
            appManager = manager;
        }

        public void startCheck(){
            handler.post(runnable);
        }

        public void stopCheck(){
            handler.removeCallbacks(runnable);
        }

    }
}


