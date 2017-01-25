package com.sar2016.panczuk.monstersgo;

import android.util.Log;

/**
 * Created by olivier on 10/01/17.
 */

public class MoveDinosServiceThread extends Thread {
    private static final String TAG = "wrappingThread";
    private MoveDinosService service;
    private boolean running = true;

    public MoveDinosServiceThread(MoveDinosService service){
        this.service = service;
        Log.d(TAG, "created");
    }

    @Override
    public void run() {
        while(running) {
            try {
                service.methodToExec();
            }catch(Exception e){
                service.connected = false;
                e.printStackTrace();
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopThread(){
        this.running = false;
    }
}
