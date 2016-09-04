package com.carelife.eventplanner.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


import com.carelife.eventplanner.R;

import java.util.ArrayList;

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
