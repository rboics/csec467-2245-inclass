package com.csec467.custom_permission_producer;

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