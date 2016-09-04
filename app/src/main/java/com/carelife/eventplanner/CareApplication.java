package com.carelife.eventplanner;

import android.app.Application;

import com.orm.SugarApp;
import com.squareup.leakcanary.LeakCanary;

/**
 * Created by carelife on 2016/8/20.
 */
public class CareApplication extends SugarApp {

    @Override public void onCreate() {
        super.onCreate();
        LeakCanary.install(this);
    }
}