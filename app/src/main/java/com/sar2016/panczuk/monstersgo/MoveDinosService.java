package com.sar2016.panczuk.monstersgo;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class MoveDinosService extends Service {
    private static final String TAG = "MoveDinosService";
    public boolean connected = false;
    private MoveDinosServiceThread wrappingThread;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate(){
        Log.d(TAG, "Created");

        this.wrappingThread = new MoveDinosServiceThread(this);
        this.wrappingThread.start();
        this.wrappingThread.stopThread();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Called");
        return super.onStartCommand(intent, flags, startId);
    }

    public void methodToExec(){
       /* MainActivity activity = ((MainActivity)MainActivity.context);
        ArrayList<Monster> monsters = activity.getMonsters();

        if(activity.isMapReady()) {
            for (int i = 0; i < monsters.size(); i++){
                LatLng latLng = monsters.get(i).marker.getPosition();
                double meters = 5;
                double coef = meters * 0.0000089;
                final double new_lat = latLng.latitude + coef;
                final double new_long = latLng.longitude + coef / Math.cos(latLng.latitude * 0.018);
                final Monster m = monsters.get(i);
                m.marker.setPosition(new LatLng(new_lat, new_long));
            }
        }
        /*
        */
        if(!connected){
           Log.d("NotConn", "notConn");
        }else {
            Log.d(TAG, "Method To Exec");
        }
    }

    @Override
    public void onDestroy() {
        wrappingThread.stopThread();
        super.onDestroy();
    }

}
