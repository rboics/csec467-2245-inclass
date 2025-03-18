package com.csec467.custom_permission_producer;

import static java.lang.Thread.sleep;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class SecureService2 extends Service {
    public SecureService2() {
    }

    public int onStartCommand(Intent intent, int flags, int startId){

        new Thread(() -> {
            sendSecretData();
        }).start();

        return Service.START_NOT_STICKY;
    }

    private void sendSecretData(){

      /*  try {
            sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }*/
        Log.d("SecureService","Handling Request");

        String superSecretData = "Hello World";

        Intent respIntent = new Intent("com.csec467.custom_permission_producer.SECRET_INFO");
        respIntent.putExtra("data", superSecretData);


        sendBroadcast(respIntent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}