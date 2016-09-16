package com.carelife.eventplanner.ui;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


import com.carelife.eventplanner.R;
import com.carelife.eventplanner.service.LocationPollingService;

import java.util.ArrayList;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MainActivity extends ActionBarActivity {
    final ArrayList<Fragment> fragmentList = new ArrayList<>();

    private ItemFragment itemFragment;
    private CalendarFragment calendarFragment;
    private TextView textBar1;
    private TextView textBar2;
    private ViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initTextView();

        itemFragment = new ItemFragment();
        calendarFragment = new CalendarFragment();
        fragmentList.add(itemFragment);
        fragmentList.add(calendarFragment);

        pager = (ViewPager) findViewById(R.id.viewpager);
        pager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), fragmentList));
        pager.setCurrentItem(0);
        pager.setOnPageChangeListener(new MyOnPageChangeListener());
        pager.setOffscreenPageLimit(1);

        MainActivityPermissionsDispatcher.checkPermissionWithCheck(MainActivity.this);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // NOTE: delegate the permission handling to generated method
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @NeedsPermission({Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.ACCESS_FINE_LOCATION})
    public void checkPermission() {
        Intent startIntent = new Intent(this, LocationPollingService.class);
        startIntent.setAction("LocationPollingService");
        bindService(startIntent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.e("Y","connected");
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        }, Service.BIND_AUTO_CREATE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_add) {
            Intent i = new Intent(this, PlanDetailActivity.class);
            i.putExtra("PlanId", -1L);
            startActivity(i);
            return true;
        } else if (id == R.id.action_ascend) {
            itemFragment.sort(true);
        } else if(id == R.id.action_descend) {
            itemFragment.sort(false);
        }

        return super.onOptionsItemSelected(item);
    }

    private void initTextView() {
        textBar1 = (TextView) findViewById(R.id.tab1);
        textBar2 = (TextView) findViewById(R.id.tab2);
        textBar1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pager.setCurrentItem(0);
            }
        });
        textBar2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pager.setCurrentItem(1);
            }
        });
    }

    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }

        @Override
        public void onPageSelected(int arg0) {
            if (arg0 == 0) {
                textBar1.setTextColor(getResources().getColor(android.R.color.holo_blue_light));
                textBar2.setTextColor(getResources().getColor(android.R.color.darker_gray));
                itemFragment.notifyChange();
            } else {
                textBar1.setTextColor(getResources().getColor(android.R.color.darker_gray));
                textBar2.setTextColor(getResources().getColor(android.R.color.holo_blue_light));
                calendarFragment.notifyChange();
            }
        }
    }

    public class MyFragmentPagerAdapter extends FragmentPagerAdapter {
        ArrayList<Fragment> list;

        public MyFragmentPagerAdapter(android.support.v4.app.FragmentManager fm, ArrayList<Fragment> list) {
            super(fm);
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Fragment getItem(int arg0) {
            return list.get(arg0);
        }
    }
}
