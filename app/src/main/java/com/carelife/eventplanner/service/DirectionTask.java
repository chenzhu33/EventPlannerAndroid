package com.carelife.eventplanner.service;

import android.os.AsyncTask;

import com.carelife.eventplanner.utils.MapUtil;

/**
 * 添加: google distance api异步请求类
 */
public class DirectionTask extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... params) {
        return MapUtil.getInstance().getTimes(params[0], params[1], "driving");
    }
}