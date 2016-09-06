package com.carelife.eventplanner.service;

import com.carelife.eventplanner.R;
import com.carelife.eventplanner.ui.MainActivity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by chenzhuwei on 2016/9/6.
 */
public class LocationPollingService extends Service {

    private NotificationManager mManager;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        mManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flag, int startId) {
        new PollingThread().start();
        return super.onStartCommand(intent, flag, startId);
    }

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

    /**
     * Polling thread
     * 模拟向Server轮询的异步线程
     */
    int count = 0;
    class PollingThread extends Thread {
        @Override
        public void run() {
            System.out.println("Polling...");
            count ++;
            //当计数能被5整除时弹出通知
            if (count % 5 == 0) {
                showNotification();
                System.out.println("New message!");
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("Service:onDestroy");
    }

}