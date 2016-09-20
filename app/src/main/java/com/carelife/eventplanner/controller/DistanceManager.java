package com.carelife.eventplanner.controller;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;

import com.carelife.eventplanner.Global;
import com.carelife.eventplanner.R;
import com.carelife.eventplanner.dom.Plan;
import com.carelife.eventplanner.service.DirectionTask;
import com.carelife.eventplanner.ui.MainActivity;
import com.carelife.eventplanner.utils.TimeUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 添加: 路线相关的管理器, 检查当前位置,与event的位置。
 * 如果现在的时间+到event所在地的时间+提醒阈值时间>event开始的时间,则发起一个notification,提醒用户
 */
public class DistanceManager {
    private static DistanceManager instance;
    private Context context;

    private ExecutorService executorService;

    private DistanceManager(Context context) {
        this.context = context;
        executorService = Executors.newCachedThreadPool();
    }

    public static DistanceManager getInstance(Context context) {
        if (instance == null) {
            instance = new DistanceManager(context);
        }
        return instance;
    }

    public void requestDistance(Location currentLocation) {
        String start = currentLocation.getLatitude() + "," + currentLocation.getLongitude();
        List<Plan> planList = Plan.listAll(Plan.class);
        for (final Plan plan : planList) {
            String dest = plan.getLocation();
            new DirectionTask() {
                @Override
                protected void onPostExecute(String result) {
                    checkTime(result, plan);
                }
            }.executeOnExecutor(executorService, start, dest);
        }
    }


    private void checkTime(String result, Plan plan) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            long travelTime = jsonObject.getJSONArray("rows").getJSONObject(0)
                    .getJSONArray("elements").getJSONObject(0)
                    .getJSONObject("duration").getLong("value");
            if (plan.getStartDate() - System.currentTimeMillis() < travelTime + Global.EVENT_NOTIFY_THRESHOLD) {
                showNotification(travelTime, plan.getTitle(), plan.getStartDate());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    //弹出Notification
    private void showNotification(long travelTime, String title, long startTime) {
        NotificationManager mManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent i = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, i, 0);

        Notification.Builder builder = new Notification.Builder(context)
                .setAutoCancel(true)
                .setContentTitle(context.getResources().getString(R.string.app_name))
                .setContentText("Your event " + title + " will be hold at " +
                        TimeUtil.toDate(startTime) + ". You are now " +
                        travelTime / 1000.0d / 60 + " minutes away from there")
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_launcher)
                .setWhen(System.currentTimeMillis())
                .setOngoing(true);
        Notification mNotification = builder.build();

        mManager.notify(0, mNotification);
    }

}
