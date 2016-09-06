package com.carelife.eventplanner.receiver;

import com.carelife.eventplanner.Global;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 *
 * Created by chenzhuwei on 2016/9/6.
 */
public class NetworkReceiver extends BroadcastReceiver {
    private boolean prevMobileConnected = true;
    private boolean prevWifiConnected = true;

    @Override
    public void onReceive(Context context, Intent intent) {
        //Toast.makeText(context, intent.getAction(), 1).show();
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobileInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifiInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo activeInfo = manager.getActiveNetworkInfo();
//        Toast.makeText(context, "mobile:"+mobileInfo.isConnected()+"\n"+"wifi:"+wifiInfo.isConnected()
//                +"\n"+"active:"+activeInfo.getTypeName(), Toast.LENGTH_SHORT).show();
        if(mobileInfo.isConnected() || wifiInfo.isConnected()) {
            if(!prevMobileConnected && !prevWifiConnected) {
                if(System.currentTimeMillis() - Global.SEND_TIME > Global.ELAPSED_TIME) {
                    // Send Location
                }
            }
        }
        prevWifiConnected = wifiInfo.isConnected();
        prevMobileConnected = mobileInfo.isConnected();
    }
}
