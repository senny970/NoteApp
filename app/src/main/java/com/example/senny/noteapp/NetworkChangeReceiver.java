package com.example.senny.noteapp;

/**
 * Created by Senny on 09.06.2020.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class NetworkChangeReceiver extends BroadcastReceiver {
    public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 2;
    public static int TYPE_NOT_CONNECTED = 0;

    @Override
    public void onReceive(final Context context, final Intent intent) {

        int network_status = NetworkUtil.getConnectivityStatus(context);

        if (network_status == TYPE_NOT_CONNECTED) {
            try {
                MainActivity.getInstace().onConnectionLose();
                if(!MainActivity.getInstace().isFirst)
                    Toast.makeText(context, "Сеть интернет - недоступна!", Toast.LENGTH_LONG).show();
            } catch (Exception e) {}
        }

        if (network_status == TYPE_WIFI || network_status == TYPE_MOBILE) {
            try {
                MainActivity.getInstace().onConnectionRestored();
            } catch (Exception e) {}
        }
    }
}