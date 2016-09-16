package com.carelife.eventplanner.service;

import com.carelife.eventplanner.R;
import com.carelife.eventplanner.dom.Plan;
import com.carelife.eventplanner.ui.MainActivity;
import com.carelife.eventplanner.utils.MapUtil;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.util.List;

/**
 * Created by chenzhuwei on 2016/9/6.
 */
public class LocationPollingService extends Service {

    private NotificationManager mManager;
    private LocationManager locationManager;
    private String locationProvider;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        mManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        //获取显示地理位置信息的TextView
        //获取地理位置管理器
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //获取所有可用的位置提供器
        List<String> providers = locationManager.getProviders(true);
        if (providers.contains(LocationManager.GPS_PROVIDER)) {
            //如果是GPS
            locationProvider = LocationManager.GPS_PROVIDER;
        } else if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
            //如果是Network
            locationProvider = LocationManager.NETWORK_PROVIDER;
        } else {
            return;
        }
        //监视地理位置变化
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(locationProvider, 5000, 0, locationListener);
    }

    @Override
    public int onStartCommand(Intent intent, int flag, int startId) {
        return super.onStartCommand(intent, flag, startId);
    }

    /**
     * LocationListern监听器
     * 参数：地理位置提供器、监听位置变化的时间间隔、位置变化的距离间隔、LocationListener监听器
     */

    LocationListener locationListener =  new LocationListener() {

        @Override
        public void onStatusChanged(String provider, int status, Bundle arg2) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }

        @Override
        public void onLocationChanged(Location location) {
            String start = location.getLatitude()+","+location.getLongitude();
            List<Plan> planList = Plan.listAll(Plan.class);
            for(Plan plan : planList) {
                String dest = plan.getLocation();
                new DirectionTask().execute(start, dest);
            }
        }
    };

    //弹出Notification
    private void showNotification() {
        Intent i = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, 0);

        Notification.Builder builder = new Notification.Builder(this)
                     .setAutoCancel(true)
                     .setContentTitle(getResources().getString(R.string.app_name))
                     .setContentText("You have new message!")
                     .setContentIntent(pendingIntent)
                     .setSmallIcon(R.drawable.ic_launcher)
                     .setWhen(System.currentTimeMillis())
                     .setOngoing(true);
        Notification mNotification = builder.build();

        mManager.notify(0, mNotification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("Service:onDestroy");
    }

    private class DirectionTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            return MapUtil.getInstance().getRoutes(params[0], params[1], "driving", "false");
        }

        @Override
        protected void onPostExecute(String result) {
            showNotification();
        }
    }
}