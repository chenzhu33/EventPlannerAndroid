package com.carelife.eventplanner.receiver;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.ActivityCompat;

import com.carelife.eventplanner.Global;
import com.carelife.eventplanner.controller.DistanceManager;

import java.util.List;

/**
 * 添加, 网络监听模块,如果网络恢复,并且距离上次路线检查时间超过间隔,则发起请求
 */
public class NetworkReceiver extends BroadcastReceiver {
    private boolean prevMobileConnected = true;
    private boolean prevWifiConnected = true;

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobileInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifiInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (mobileInfo.isConnected() || wifiInfo.isConnected()) {
            if (!prevMobileConnected && !prevWifiConnected) {
                if (System.currentTimeMillis() - Global.SEND_TIME > Global.ELAPSED_TIME) {
                    // Send Location
                    sendLocation(context);
                }
            }
        }
        prevWifiConnected = wifiInfo.isConnected();
        prevMobileConnected = mobileInfo.isConnected();
    }

    private void sendLocation(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        String locationProvider = "";
        if (providers.contains(LocationManager.GPS_PROVIDER)) {
            //如果是GPS
            locationProvider = LocationManager.GPS_PROVIDER;
        }
        if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
            //如果是Network
            locationProvider = LocationManager.NETWORK_PROVIDER;
        }
        if (locationProvider.isEmpty()) {
            return;
        }
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = locationManager.getLastKnownLocation(locationProvider);
        DistanceManager.getInstance(context).requestDistance(location);
    }
}
